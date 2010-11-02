package models;

import java.util.HashMap;
import java.util.Iterator;

/**
 * A Map of Objects with IDs.
 * 
 * @param <E> Class to collect
 */
public class IDTable<E> extends HashMap<Integer, E> implements Iterable<E> {
	private int id;

	public IDTable() {
		this.id = 1;
	}

	public int nextID() {
		return this.id;
	}

	public int add(E o) {
		this.put(this.id, o);
		return this.id++;
	}

	public void remove(int key) {
		this.remove((Object) key);
	}

	public Iterator<E> iterator() {
		return this.values().iterator();
	}

	public boolean contains(E o) {
		return this.containsValue(o);
	}
}
