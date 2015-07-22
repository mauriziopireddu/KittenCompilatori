package absyn;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import semantical.TypeChecker;
import translation.Block;
import types.ClassMemberSignature;
import types.ClassType;

import types.IntType;
import types.TestSignature;
import types.TypeList;
import types.VoidType;
import bytecode.VIRTUALCALL;
import bytecode.RETURN;
import bytecode.NEWSTRING;
import bytecode.CONST;









public class TestDeclaration extends CodeDeclaration {

	private final String name;

	public TestDeclaration(int pos, String name, Command body, ClassMemberDeclaration next) {
		super(pos, null, body, next);

		this.name = name;
	}

	/**
	 * Yields the name of this test.
	 *
	 * @return the name of this test
	 */

	public String getName() {
		return name;
	}

	/**
	 * Yields the signature of this test declaration.
	 *
	 * @return the signature of this test declaration. Yields {@code null}
	 *         if type-checking has not been performed yet
	 */

	@Override
	public TestSignature getSignature() {
		return (TestSignature) super.getSignature();
	}

	@Override
	protected void toDotAux(FileWriter where) throws IOException {
		linkToNode("body", getBody().toDot(where), where);

	}

	/**
	 * Adds the signature of this test declaration to the given class.
	 *
	 * @param clazz the class where the signature of this test declaration must be added
	 */

	@Override
	protected void addTo(ClassType clazz) {
		TestSignature tSig = new TestSignature(clazz, name, this);

		clazz.addTest(tSig);

		// we record the signature of this test inside this abstract syntax
		setSignature(tSig);	
	}

	@Override
	//implementa le operazioni specifiche alla singola espressione
	protected void typeCheckAux(ClassType clazz) {
		TypeChecker checker = new TypeChecker(VoidType.INSTANCE, clazz.getErrorMsg(), true);

		//facciamo il type-check del corpo del test nel risultante type-checking
		getBody().typeCheck(checker);

		// notiamo che non c'e dead-code nel corpo del test
		getBody().checkForDeadcode();
		
		// i test ritornano nulla, cosi che non dobbiamo notare
		// se e sempre presente un return alla fine di ogni percorso di esecuzione sintattica
		// nel corpo del test
		
	}

	
	public void translate(Set<ClassMemberSignature> done) {
		if (done.add(getSignature())) {
			translateAux(getSignature().getDefiningClass(),done);
			Block post = new Block(new RETURN(IntType.INSTANCE));
			post = new CONST(0).followedBy(post);
			//prendiamo il metodo output 
			post = new VIRTUALCALL(ClassType.mkFromFileName("String.kit"),
					ClassType.mkFromFileName("String.kit").methodLookup("output", TypeList.EMPTY))
			.followedBy(post);
			//utile per stampare la stringa "Assert passsed"
			post = new NEWSTRING("passed").followedBy(post);	
			getSignature().setCode(getBody().translate(post));
			//traduce in kitten bytecode tutti i membri della classe a cui fa riferimento il blocco 
			translateReferenced(getSignature().getCode(), done, new HashSet<Block>());
		}
	}
	
}
