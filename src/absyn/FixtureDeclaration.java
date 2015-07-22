package absyn;

import java.io.FileWriter;
import java.io.IOException;

import semantical.TypeChecker;
import types.ClassType;
import types.FixtureSignature;
import types.VoidType;

public class FixtureDeclaration extends CodeDeclaration {

	private static int counter;
	private String name;
	
	public FixtureDeclaration(int pos, Command body, ClassMemberDeclaration next) {
		super(pos, null, body, next);
		name = "fixture".concat(String.valueOf(++counter));
	}
	
	/**
	 * Yields the signature of this fixture declaration.
	 *
	 * @return the signature of this fixture declaration.
	 *         Yields {@code null} if type-checking has not been performed yet
	 */


	@Override
	protected void toDotAux(FileWriter where) throws IOException {
		linkToNode("body", getBody().toDot(where), where);
		
	}

	/**
	 * Adds the signature of this fixture declaration to the given class.
	 *
	 * @param clazz the class where the signature of this fixture
	 *              declaration must be added
	 */
	
	@Override
	protected void addTo(ClassType clazz) {
		FixtureSignature fSig = new FixtureSignature (clazz, name, this);
		
		clazz.addFixture(fSig);
		
		//salviamo la firma di questa fixture nella sintassi astratta
		setSignature(fSig);
	}
	
	@Override
	//implementa le operazioni specifiche alla singola espressione
	protected void typeCheckAux(ClassType clazz) {
		TypeChecker checker = new TypeChecker(VoidType.INSTANCE, clazz.getErrorMsg());
		checker = checker.putVar("this", clazz);
		
		// facciamo il type-check del corpo del costruttore nel type-checker risultante
		getBody().typeCheck(checker);

		// notiamo che non c'e dead-code nel corpo del costruttore 
		getBody().checkForDeadcode();
		
		//le fixtures restituiscono nulla, cosi che non controlliamo se 
		//una dichiarazione di ritorno e sempre presente alla fine di ogni 
		//percorso di esecuzione sintattico nel corpo di una fixture
	}
}