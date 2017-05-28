package utility;

public class GenericEntry<G, H> {

	public G key;
	public H val;

	public GenericEntry(G key, H val) {

		this.key = key;
		this.val = val;
	}

	@Override
	public String toString() {
		return "(" + key + ", " + val + ")";
	}
}
