package utility;

import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class GenericLinkedHashTable<G, H> {

	public int n;
	public G[] keys;
	public PriorityQueue<H>[] entries;
	private boolean grows;
	private int occupied;
	private double capacity;
	private boolean sortReversed;

	@SuppressWarnings("unchecked")
	public GenericLinkedHashTable(int n, boolean sortReversed) {
		this.n = n;
		this.keys = (G[]) new Object[n];
		this.entries = new PriorityQueue[n];
		this.sortReversed = sortReversed;
	}

	@SuppressWarnings("unchecked")
	public GenericLinkedHashTable(int n, double capacity) {
		this.n = n;
		this.keys = (G[]) new Object[n];
		this.entries = new PriorityQueue[n];
		this.grows = true;
		this.capacity = capacity;
	}

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
					this.entries[index] = new PriorityQueue<H>(11, Collections.reverseOrder());
				} else {
					this.entries[index] = new PriorityQueue<H>();
				}
				inserted = true;
			} else if (!this.keys[index].equals(key)) {
				index = (int) ((hash(key.toString()) + c * hash2(key.toString())) % this.n);
				if (index < 0) {
					index *= -1;
				}
				c++;
			} else {
				inserted = true;
			}
		}
		this.entries[index].add(val);
		this.occupied++;
		if (grows) {
			double p = ((double) occupied) / n;
			if (p >= capacity) {
				// rehash
				@SuppressWarnings("unchecked")
				G[] biggerKeys = (G[]) new Object[this.n * 2];
				@SuppressWarnings("unchecked")
				LinkedList<H>[] biggerEntries = new LinkedList[this.n * 2];
			}
		}
	}

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
		this.entries[index].remove(val);
		this.occupied--;
	}

	public PriorityQueue<H> get(G key) {
		int index = (int) (hash(key.toString()) % this.n);
		if (index < 0) {
			index *= -1;
		}
		int c = 1;
		while (this.keys[index] != null) {
			if (this.keys[index].equals(key)) {
				return this.entries[index];
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

	@Override
	public String toString() {
		String desc = "";
		for (int i = 0; i < entries.length; i++) {
			desc += i + ": ";
			for (H val : entries[i]) {
				desc += val + ", ";
			}
			desc = desc.substring(0, desc.lastIndexOf(","));
			desc += "\n";
		}
		return desc.substring(0, desc.lastIndexOf("\n"));
	}

	private long hash(String key) {
		long hash = 5381;
		for (int c = 0; c < key.length(); c++) {
			hash = hash * 33 + key.charAt(c);
		}
		return hash;
	}

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