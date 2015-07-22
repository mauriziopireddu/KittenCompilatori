package absyn;

import java.io.FileWriter;
import bytecode.NEWSTRING;
import bytecode.VIRTUALCALL;
import bytecode.CONST;
import bytecode.RETURN;
import semantical.TypeChecker;
import translation.Block;
import types.ClassType;
import types.IntType;
import types.TypeList;




public class Assert extends Command {

	private final Expression asserted;
	
	public Assert(int pos, Expression asserted) {
		super(pos);
		
		this.asserted = asserted;
	}
	
	public Expression getAsserted() {
		return asserted;
	}
	
	@Override
	protected void toDotAux(FileWriter where) throws java.io.IOException {
			linkToNode("asserted", asserted.toDot(where), where);
	}

	@Override
	// effettua l'analisi semantica specifica a ciascun comando
	protected TypeChecker typeCheckAux(TypeChecker checker) {
		asserted.mustBeBoolean(checker);
		boolean expected = checker.isTesting();
		
		if (expected != true)
			error("assert not defined in method test");
		
		return checker;
	}

	@Override
	public boolean checkForDeadcode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	//con translate costruiamo un blocco per assert dato che il bytecode di kitten
	//e un grafo di blocchi di codice contenente codice sequenziale
	
	public Block translate(Block continuation) {
		String out = makeFailureMessage();
		//creo il blocco (non ha predecessori ne sucessori)
		Block failed = new Block(new RETURN(IntType.INSTANCE));
		failed = new CONST(-1).followedBy(failed); //aggiungiamo i vari elementi sopra al blocco failed 
		
		
		//virtualcall chiama il metodo output e parametri formali di tipo t risalendo nella catena delle superclassi
		//il ricevitore e e i parametri attuali della chiamata si trovano nello stack al momento della chiamata
		// vengono sostituiti alla fine con il valore di ritorno del metodo
		failed = new VIRTUALCALL(ClassType.mkFromFileName("String.kit"),
			ClassType.mkFromFileName("String.kit").methodLookup("output", TypeList.EMPTY)).followedBy(failed);
		failed = new NEWSTRING(out).followedBy(failed);
		
		//traduce l'espressione assumendo che sia di tipo boolean (garantito precedentemente da type-checking)
		return asserted.translateForTesting(continuation, failed);
		//prende continuation oppure il blocco failed che abbiamo costruito
	}
	
	private String makeFailureMessage() {
		String pos = getTypeChecker().calculatePosition(getPos());

		return "Assert fallita a: " + pos;
	}
}
