package tables;

/**
 * A non-empty symbol table. It is organized as a binary search tree.
 *
 * @author  <A HREF="mailto:fausto.spoto@univr.it">Fausto Spoto</A>
 */
//Table<> e una struttura data che usiamo per rappresentare gli ambienti 
//Table<E> specifica che un ambiente ha un'operazione di tipo get(key) e di tipo put(key, value)
//che vengono rispettivamente implementate in NonEmptyTable.java e EmptyTable.java essendo Table classe abstract
//Ricordiamo che l'ambiente non e altro che una funzione che associa gli ID ai valori denotabilli(posizioni di memoria, 
//procedure, funzioni)

// Implementa un ambiente in cui c'e un legame per almeno una variabile
final class NonEmptyTable<E> extends Table<E> {

	/**
	 * the key on top of the tree.
	 */

	private final String key;

	/**
	 * the value bound on key at the top of the tree.
	 */

	private final E value;

	/**
	 * the left subtree.
	 */

	private final Table<E> left;

	/**
	 * the right subtree.
	 */

	private final Table<E> right;

	/**
	 * Builds a non-empty table.
	 *
	 * @param key the key in the root of the tree
	 * @param value the value bound to <tt>key</tt>
	 * @param left the left subtree
	 * @param right the right subtree
	 */

	private NonEmptyTable(String key, E value, Table<E> left, Table<E> right) {
		this.key = key;
		this.value = value;
		this.left = left;
		this.right = right;
	}

	/**
	 * Builds a non-empty table having empty subtrees.
	 *
	 * @param key the key in the root of the tree
	 * @param value the value bound to {@code key}
	 */

	NonEmptyTable(String key, E value) {
		this.key = key;
		this.value = value;
		this.left = Table.empty();
		this.right = Table.empty();
	}

	@Override
	public E get(String key) {
		// permette di leggere il valore di tipo E legato ad una variabile key
		int comp = this.key.compareTo(key);

		if (comp < 0)
			return left.get(key);
		else if (comp == 0)
			return value;
		else
			return right.get(key);
	}

	@Override
	public Table<E> put(String key, E value) {
		//costruisce un nuovo ambiente in cui la variabile key e legata a value di tipo E
		//put non e un'operazione distruttiva ma lascia inalterato il vecchio albero 
		//restituisce un nuovo ambiente con aggiunto un nuovo legame
		int comp = this.key.compareTo(key);

		if (comp < 0) {
			Table<E> temp = left.put(key,value);
			if (temp == left)
				return this;
			else
				return new NonEmptyTable<E>(this.key, this.value, temp, right);
		}
		else if (comp == 0)
			if (value == this.value)
				return this;
			else
				return new NonEmptyTable<E>(this.key, value, left, right);
		else {
			Table<E> temp = right.put(key,value);
			if (temp == right)
				return this;
			else
				return new NonEmptyTable<E>(this.key, this.value, left, temp);
		}
	}
}