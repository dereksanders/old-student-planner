package model;

import java.time.LocalDate;

/**
 * The Class Term.
 */
public class NullTerm extends Term {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new term.
	 *
	 * @param name
	 *            the name
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public NullTerm(String name, LocalDate start, LocalDate end) {

		super(name, start, end);
	}

	public boolean isNull() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "null";
	}
}
