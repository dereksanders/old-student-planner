package utility;

public class GenericHashTable<G, H> {

	public int n;
	public GenericEntry<G, H>[] entries;

	@SuppressWarnings("unchecked")
	public GenericHashTable(int n) {
		this.n = n;
		this.entries = new GenericEntry[n];
	}

	public void put(GenericEntry<G, H> e) {

		int index = (int) (hash(e.key.toString()) % this.n);

		/* Make sure index is positive. */
		if (index < 0) {
			index *= -1;
		}

		/* c = number of attempts to find empty index */
		int c = 1;
		while (this.entries[index] != null) {

			/* double hashing */
			index = (int) ((hash(e.key.toString()) + c * hash2(e.key.toString())) % this.n);

			/* Make sure index is positive. */
			if (index < 0) {
				index *= -1;
			}

			c++;
		}

		this.entries[index] = new GenericEntry<G, H>(e.key, e.val);
	}

	public H get(G key) {

		int index = (int) (hash(key.toString()) % this.n);

		/* Make sure index is positive. */
		if (index < 0) {
			index *= -1;
		}

		int c = 1;

		while (!this.entries[index].key.equals(key)) {
			index = (int) ((hash(key.toString()) + c * hash2(key.toString())) % this.n);

			if (index < 0) {
				index *= -1;
			}

			c++;
		}

		return this.entries[index].val;
	}

	public boolean contains(G key) {
		for (GenericEntry<G, H> pair : this.entries) {
			if (pair != null) {
				if (pair.key.equals(key)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		String desc = "";
		for (int i = 0; i < entries.length; i++) {
			if (entries[i] != null) {
				desc += entries[i].key + ": " + entries[i].val + ", ";
			}
		}
		return desc.substring(0, desc.lastIndexOf(","));
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
