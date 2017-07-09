package utility;

import java.io.Serializable;

/**
 * The Class GenericHashTable.
 *
 * @param <G>
 *            the generic type
 * @param <H>
 *            the generic type
 */
public class GenericHashTable<G, H> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int n;
	public G[] keys;
	public H[] vals;
	private int occupied;
	private double acceptableLoad = 0.7;
	private double load;
	private int growthFactor = 2;

	/**
	 * Instantiates a new generic hash table.
	 *
	 * @param n
	 *            the n
	 */
	@SuppressWarnings("unchecked")
	public GenericHashTable(int n) {
		this.n = n;
		this.keys = (G[]) new Object[n];
		this.vals = (H[]) new Object[n];
	}

	/**
	 * Puts the (key, val) pair into the hash table.
	 *
	 * @param key
	 *            the key
	 * @param val
	 *            the val
	 */
	public void put(G key, H val) {

		int index = (int) (hash(key.toString()) % this.n);
		if (index < 0) {
			index *= -1;
		}
		int c = 1;
		boolean inserted = false;

		while (!inserted) {

			if (this.keys[index] == null) {

				this.keys[index] = key;
				inserted = true;

			} else if (!this.keys[index].equals(key)) {

				/* Perform double hashing. */
				index = (int) ((hash(key.toString()) + c * hash2(key.toString())) % this.n);
				if (index < 0) {
					index *= -1;
				}
				c++;

			} else {
				/* Key has already been inserted. */
				inserted = true;
			}
		}

		this.vals[index] = val;
		this.occupied++;
		this.load = ((double) this.occupied / (double) this.n);
		if (this.load > this.acceptableLoad) {
			rehash(this.n * growthFactor);
		}
	}

	/**
	 * Deletes the (key, val) pair from the hash table.
	 *
	 * @param key
	 *            the key
	 * @param val
	 *            the val
	 */
	public void del(G key, H val) {
		int index = (int) (hash(key.toString()) % this.n);
		if (index < 0) {
			index *= -1;
		}
		int c = 1;
		while (!this.keys[index].equals(key)) {
			if (this.keys[index] == null) {
				return;
			} else {
				index = (int) ((hash(key.toString()) + c * hash2(key.toString())) % this.n);
				if (index < 0) {
					index *= -1;
				}
				c++;
			}
		}
		this.vals[index] = null;
		this.occupied--;
		this.load = ((double) this.occupied / (double) this.n);
	}

	/**
	 * Gets the val associated with the key.
	 *
	 * @param key
	 *            the key
	 * @return the h
	 */
	public H get(G key) {
		int index = (int) (hash(key.toString()) % this.n);
		if (index < 0) {
			index *= -1;
		}
		int c = 1;
		while (this.keys[index] != null) {
			if (this.keys[index].equals(key)) {
				return this.vals[index];
			} else {
				index = (int) ((hash(key.toString()) + c * hash2(key.toString())) % this.n);
				if (index < 0) {
					index *= -1;
				}
				c++;
			}
		}
		return null;
	}

	/**
	 * Rehash and grow the hash table.
	 *
	 * @param newSize
	 *            the new size
	 */
	private void rehash(int newSize) {

		/*
		 * Create a larger hash table containing the same (key, val) pairs.
		 */
		GenericHashTable<G, H> big = new GenericHashTable<>(newSize);
		for (G key : this.keys) {
			H val = this.get(key);
			big.put(key, val);
		}

		/* Make this hash table equal to the bigger one. */
		this.n = newSize;
		this.keys = big.keys;
		this.vals = big.vals;
		this.occupied = big.occupied;
		this.load = big.load;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String desc = "";
		for (int i = 0; i < vals.length; i++) {
			desc += i + ": ";
			desc += vals[i] + ", ";
			desc = desc.substring(0, desc.lastIndexOf(","));
			desc += "\n";
		}
		return desc.substring(0, desc.lastIndexOf("\n"));
	}

	/**
	 * Hash Method 1 for double hashing.
	 *
	 * @param key
	 *            the key
	 * @return the long
	 */
	private long hash(String key) {
		long hash = 5381;
		for (int c = 0; c < key.length(); c++) {
			hash = hash * 33 + key.charAt(c);
		}
		return hash;
	}

	/**
	 * Hash Method 2 for double hashing.
	 *
	 * @param key
	 *            the key
	 * @return the long
	 */
	private long hash2(String key) {
		long hash = 0;
		int i;
		for (i = 0; i < key.length(); i++) {
			hash += key.charAt(i);
			hash += (hash << 10);
			hash ^= (hash >> 6);
		}
		hash += (hash << 3);
		hash ^= (hash >> 11);
		hash += (hash << 15);
		return hash;
	}
}