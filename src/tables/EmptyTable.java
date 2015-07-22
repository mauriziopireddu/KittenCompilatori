package tables;

/**
 * An empty symbol table.
 *
 * @author  <A HREF="mailto:fausto.spoto@univr.it">Fausto Spoto</A>
 */
//implementa un ambiente vuoto dove non esiste alcun legame per le variabili
final class EmptyTable<E> extends Table<E> {

	EmptyTable() {}

	@Override
	public E get(String key) {
		return null;  // non c'e chiave in questa tabella vuota
	}

	@Override
	public Table<E> put(String key, E value) {
		// costruisce una tabella di simboli non vuota con sottoalbero vuoto
		
		return new NonEmptyTable<E>(key, value);
	}
}