package absyn;

import java.io.FileWriter;


import semantical.TypeChecker;
import translation.Block;

/**
 * A node of abstract syntax representing a {@code for} command.
 *
 * @author <A HREF="mailto:fausto.spoto@univr.it">Fausto Spoto</A>
 */

public class For extends Command {

    /**
     * The code to be executed before the loop starts.
     */

    private final Command initialisation;

    /**
     * The guard or condition of the loop.
     */

    private final Expression condition;

    /**
     * The code to be executed after each iteration.
     */

    private final Command update;

    /**
     * The body of the loop.
     */

    private final Command body;

    /**
     * Constructs the abstract syntax of a {@code for} command.
     *
     * @param pos the position in the source file where it starts
     *            the concrete syntax represented by this abstract syntax
     * @param initialisation the code to be executed before the loop starts
     * @param condition the guard or condition of the loop
     * @param update the code to be executed after each iteration
     * @param body the body of the loop
     */

    public For(int pos, Command initialisation, Expression condition, Command update, Command body) {
    	super(pos);

    	this.initialisation = initialisation;
    	this.condition = condition;
    	this.update = update;
    	this.body = body;
    }

    /**
     * Adds abstract syntax class-specific information in the dot file
     * representing the abstract syntax of this {@code for} command.
     * This amounts to adding two arcs from the node for the {@code for}
     * command to the abstract syntax for {@link #initialisation},
     * {@link #condition}, {@link #update} and {@link #body}.
     *
     * @param where the file where the dot representation must be written
     */

    @Override
    protected void toDotAux(FileWriter where) throws java.io.IOException {
    	linkToNode("initialisation", initialisation.toDot(where), where);
    	linkToNode("condition", condition.toDot(where), where);
    	linkToNode("update", update.toDot(where), where);
    	linkToNode("body", body.toDot(where), where);
    }

    /**
     * Performs the type-checking of the {@code for} command
     * by using a given type-checker. It type-checks the initialisation,
     * condition, update and body
     * of the {@code for} command. It checks that the condition is
     * a Boolean expression. Note that local declarations inside
     * {@link #initialisation} are visible in {@link #condition},
     * {@link #update} and {@link #body}. Returns the original type-checker
     * passed as a parameter, so that local declarations in
     * {@link #initialisation}, {@link #update} and {@link #body}
     * are not visible after the {@code for} command.
     *
     * @param checker the type-checker to be used for type-checking
     * @return the type-checker {@code checker}
     */

    @Override
    protected TypeChecker typeCheckAux(TypeChecker checker) {
    	// Consideriamo il tipo ortografico risultante da type-checking
        // Il componente inizializzazione . Utilizzando questo
        // Nuovo tipo ortografico nei seguenti controlli , permettiamo variabili locali
        // Definito nella inizializzazione < componente sia visibile
        // Nelle altre componenti per il comando
    	TypeChecker initChecker = initialisation.typeCheck(checker);

    	condition.mustBeBoolean(initChecker);

    	// facciamo type-check dell componente di aggiornamento . Si noti che il
        // Risultante tipo ortografico non viene utilizzato , in modo che le dichiarazioni locali
        // Nel campo aggiornamento non sono visibili dall'esterno quel campo
    	update.typeCheck(initChecker);

    	// type-check del corpo di questo comando .
        // Si noti che il risultante tipo checker non viene utilizzato
    	body.typeCheck(initChecker);

    	// ritorniamo il type-checker originale
    	return checker;
    }

    /**
     * Checks that this {@code for} does not contain <i>dead-code</i>, that is,
     * commands that can never be executed. It calls itself recursively
     * on {@link #initialisation}, {@link #update} and {@link #body}. There is
     * dead-code only if every syntactical execution path in {@link #initialisation}
     * ends with a {@code return}, {@code break} or {@code continue} command.
     *
     * @return true if and only if every syntactical execution path in
     *         {@link #initialisation} ends with a {@code return}, {@code break} or
     *         {@code continue} command. Note that it returns false otherwise, since we
     *         have no guarantee that the loop will ever be entered at least once
     */

    @Override
    public boolean checkForDeadcode() {
    	update.checkForDeadcode();
    	body.checkForDeadcode();

    	if (initialisation.checkForDeadcode()) {
    		error("dead-code after for loop initialisation");

    		return true;
    	}

    	return false;
    }

    /**
     * Translates this command into intermediate Kitten bytecode. Namely, it returns
     * code that evaluates {@link #initialisation}, then {@link #condition} and then
     * continues with the compilation of {@link #body} and {@link #update} or with
     * the given {@code continuation}. After the compilation of {@link #update}, the
     * the {@link #condition} of the loop is checked again.
     *
     * @param where the method or constructor where this expression occurs
     * @param continuation the continuation to be executed after this command
     * @return the code executing this command and then {@code continuation}
     */

    public Block translate(Block continuation) {

    	/* The idea is to translate a for command into the code

            initialisation -> condition -> (no) continuation
                              | (yes) ^
                              V       |
                            body -> update
    	 */

    	
    	Block pivot = new Block();

    	// we translate the condition of the loop. If the condition is true,
    	// we execute the translation of the body and then the update.
    	// Otherwise we execute what follows this command. This code will be
    	// used to translate the initialisation component
    	Block test = condition.translateForTesting(body.translate(update.translate(pivot)), continuation);

    	test.doNotMerge();

    	// we link the pivot to the code for the test, so that we close the loop
    	pivot.linkTo(test);

    	return initialisation.translate(test);
    }
}