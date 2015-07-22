package bytecode;

import javaBytecodeGenerator.JavaClassGenerator;

import org.apache.bcel.generic.InstructionList;

/**
 * A bytecode of the intermediate Kitten language with one or no subsequent bytecode.
 *
 * @author <A HREF="mailto:fausto.spoto@univr.it">Fausto Spoto</A>
 */

// Implementa le istruzioni sequenziali 

public abstract class NonBranchingBytecode extends Bytecode {

	/**
	 * Constructs a non-branching bytecode.
	 */

	protected NonBranchingBytecode() {
		super();
	}

	/**
	 * Generates the Java bytecode corresponding to this Kitten bytecode.
	 * Sometimes there is a direct correspondence between the two bytecode
	 * languages. Other times, instead, one needs to generate more than one
	 * Java bytecode to emulate the semantics of a single Kitten bytecode.
	 *
	 * @param classGen the Java class generator to be used for this generation
	 * @return the Java bytecode(s) corresponding to this Kitten bytecode
	 */

	//instructionList rappresenta una classe BCEL che rappresenta una sequenza eventualmente vuota di bytecode java
	public abstract InstructionList generateJavaBytecode(JavaClassGenerator classGen);
}