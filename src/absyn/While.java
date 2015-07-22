package absyn;

import java.io.FileWriter;


import semantical.TypeChecker;
import translation.Block;

/**
 * A node of abstract syntax representing a <tt>while</tt> command.
 *
 * @author <A HREF="mailto:fausto.spoto@univr.it">Fausto Spoto</A>
 */

public class While extends Command {

	/**
	 * The guard or condition of the loop.
	 */

	private final Expression condition;

	/**
	 * The body of the loop.
	 */

	private final Command body;

	/**
	 * Constructs the abstract syntax of a {@code while} command.
	 *
	 * @param pos the position in the source file where it starts
	 *            the concrete syntax represented by this abstract syntax
	 * @param condition the guard or condition of the loop
	 * @param body the body of the loop
	 */

	public While(int pos, Expression condition, Command body) {
		super(pos);

		this.condition = condition;
		this.body = body;
	}

	/**
	 * Yields the abstract syntax of the guard or condition of the {@code while} command.
	 *
	 * @return the abstract syntax of the guard or condition of the {@code while} command
	 */

	public Expression getCondition() {
		return condition;
	}

	/**
	 * Yields the abstract syntax of the body of the {@code while} command.
	 *
	 * @return the abstract syntax of the body of the {@code while} command
	 */

	public Command getBody() {
		return body;
	}

	/**
	 * Adds abstract syntax class-specific information in the dot file
	 * representing the abstract syntax of the {@code while} command.
	 * This amounts to adding two arcs from the node for the {@code while}
	 * command to the abstract syntax for {@link #condition} and {@link #body}.
	 *
	 * @param where the file where the dot representation must be written
	 */

	@Override
	protected void toDotAux(FileWriter where) throws java.io.IOException {
		linkToNode("condition", condition.toDot(where), where);
		linkToNode("body", body.toDot(where), where);
	}

	/**
	 * Performs the type-checking of the {@code while} command
	 * by using a given type-checker. It type-checks the condition and body
	 * of the {@code while} command. It checks that the condition is
	 * a Boolean expression. Returns the original type-checker
	 * passed as a parameter, so that local declarations in the body of
	 * the loop are not visible after.
	 *
	 * @param checker the type-checker to be used for type-checking
	 * @return {@code checker} itself
	 */

	@Override
	protected TypeChecker typeCheckAux(TypeChecker checker) {
		// la condizione del ciclo deve essere booleana
		condition.mustBeBoolean(checker);

		// il type checker risultante non viene usato
		body.typeCheck(checker);

		// le dichiarazioni locali nel corpo del ciclo sono perse dopo il ciclo
		return checker;
	}

	/**
	 * Checks that this {@code while} does not contain <i>dead-code</i>, that is,
	 * commands that can never be executed. It calls itself recursively on {@link #body}.
	 *
	 * @return false, since there is no guarantee that the loop will be entered at least once
	 */

	@Override
	public boolean checkForDeadcode() {
		body.checkForDeadcode();

		return false;
	}

	/**
	 * Translates this command into intermediate Kitten bytecode. Namely,
	 * it returns a code which evaluates the {@link #condition} and then continues with
	 * the compilation of {@link #body} or with the given {@code continuation}.
	 * After the {@link #body}, the {@link #condition} is checked again.
	 *
	 * @param where the method or constructor where this expression occurs
	 * @param continuation the continuation to be executed after this command
	 * @return the code executing this command and then the {@code continuation}
	 */

	public Block translate(Block continuation) {
		/* tradurre un comando while nel codice

	    condition -> (no) continuation
	    | (yes) ^
	    V       |
	    body ----
		 */

		//creiamo un blocco vuoto usato per chiudere il blocco
		Block pivot = new Block();

		// Traduciamo la condizione del loop. Se vera, eseguiamo la traduzione
		// altrimenti andiamo avanti
		Block result = condition.translateForTesting(body.translate(pivot), continuation);

		result.doNotMerge();

		// liankiamo il pivot al codice che testa la condizione e chiudiamo il loop
		pivot.linkTo(result);

		return result;
	}
}