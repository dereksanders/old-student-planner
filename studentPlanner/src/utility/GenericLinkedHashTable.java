package utility;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.PriorityQueue;

import model.Meeting;

/**
 * The Class GenericLinkedHashTable.
 *
 * @param <G>
 *            the generic type
 * @param <H>
 *            the generic type
 */
public class GenericLinkedHashTable<G, H> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int n;
	public G[] keys;
	public PriorityQueue<H>[] valQueues;
	private boolean sortReversed;
	private int occupied;
	private double acceptableLoad = 0.7;
	private double load;
	private int growthFactor = 2;

	/**
	 * Instantiates a new generic linked hash table.
	 *
	 * @param n
	 *            the n
	 * @param sortReversed
	 *            the sort reversed
	 */
	@SuppressWarnings("unchecked")
	public GenericLinkedHashTable(int n, boolean sortReversed) {
		this.n = n;
		this.keys = (G[]) new Object[n];
		this.valQueues = new PriorityQueue[n];
		this.sortReversed = sortReversed;
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
				if (sortReversed) {
					this.valQueues[index] = new PriorityQueue<H>(11, Collections.reverseOrder());
				} else {
					this.valQueues[index] = new PriorityQueue<H>();
				}
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

		this.valQueues[index].add(val);
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
		this.valQueues[index].remove(val);
		this.occupied--;
		this.load = ((double) this.occupied / (double) this.n);
	}

	/**
	 * Gets the queue of vals associated with the key.
	 *
	 * @param key
	 *            the key
	 * @return the priority queue
	 */
	public PriorityQueue<H> get(G key) {
		int index = (int) (hash(key.toString()) % this.n);
		if (index < 0) {
			index *= -1;
		}
		int c = 1;
		while (this.keys[index] != null) {
			if (this.keys[index].equals(key)) {
				return this.valQueues[index];
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
		GenericLinkedHashTable<G, H> big = new GenericLinkedHashTable<>(newSize, this.sortReversed);
		for (G key : this.keys) {
			for (H val : this.get(key)) {
				big.put(key, val);
			}
		}

		/* Make this hash table equal to the bigger one. */
		this.n = newSize;
		this.keys = big.keys;
		this.valQueues = big.valQueues;
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
		for (int i = 0; i < valQueues.length; i++) {
			desc += i + ": ";
			for (H val : valQueues[i]) {
				desc += val + ", ";
			}
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