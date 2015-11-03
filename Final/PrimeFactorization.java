package edu.iastate.cs228.hw3;

import java.util.ListIterator;

/**
 * 
 * @author Clinton Elliott
 *
 */
public class PrimeFactorization implements Iterable<PrimeFactor> {
	private static final long OVERFLOW = -1;
	private long value; // the factored integer
						// it is set to OVERFLOW when the number is greater than
						// 2^63-1,
						// the largest number representable by the type long.

	/**
	 * Reference to dummy node at the head.
	 */
	private Node head;

	/**
	 * Reference to dummy node at the tail.
	 */
	private Node tail;

	private int size; // number of distinct prime factors

	// ------------
	// Constructors
	// ------------

	/**
	 * Default constructor constructs an empty list to represent the number 1.
	 * 
	 * Combined with the add() method, it can be used to create a prime
	 * factorization.
	 */
	public PrimeFactorization() {
		size = 0;
		value = 1;
		head = new Node();
		tail = new Node();

		head.next = tail; // makes tail a link to head
		tail.previous = head; // makes head a link to tail
	}

	/**
	 * Obtains the prime factorization of n and creates a doubly linked list to
	 * store the result. Follows the algorithm in Section 1.2 of the project
	 * description.
	 * 
	 * @param n
	 * @throws IllegalArgumentException
	 *             if n < 1
	 */
	public PrimeFactorization(long n) throws IllegalArgumentException {
		this();
		if (n < 1) {
			throw new IllegalArgumentException(
					"The value for N can't be less than 1");
		}
		if (n == 1) {
			for (int i = 2; i * i <= n; i++) {
				while ((isPrime(i) == true) && (n % i == 0)) {
					add(i, 1);
					n = n / i;
				}
			}
		}
		if (n != 1) {
			add((int) n, 1);
		}
	}

	/**
	 * Copy constructor. It is unnecessary to verify the primality of the
	 * numbers in the list.
	 * 
	 * @param pf
	 */
	public PrimeFactorization(PrimeFactorization pf) {
		this(); // creates an instance of PrimeFactorization
		PrimeFactorizationIterator primeIt = pf.iterator();
		while (primeIt.hasNext()) {
			PrimeFactor primeTemp = primeIt.cursor.pFactor;
			add(primeTemp.prime, primeTemp.multiplicity);
			primeTemp = primeIt.next();
		}
	}

	/**
	 * Constructs a factorization from an array of prime factors. Useful when
	 * the number is too large to be represented even as a long integer.
	 * 
	 * @param pflist
	 */
	public PrimeFactorization(PrimeFactor[] pfList) {
		this();
		for (int i = 0; i < pfList.length; i++) {
			add(pfList[i].prime, pfList[i].multiplicity);
		}
	}

	// --------------
	// Primality Test
	// --------------

	/**
	 * Test if a number is a prime or not. Check iteratively from 2 to the
	 * largest integer not exceeding the square root of n to see if it divides
	 * n.
	 * 
	 * @param n
	 * @return true if n is a prime false otherwise
	 */
	public static boolean isPrime(long n) {
		if (n == 2) {
			return true;
		} else if (n % 2 == 0 || n < 2) {
			return false;
		}
		for (int i = 2; i * i <= n; i++) {
			if (n % i == 0) {
				return false;
			}
		}
		return true;
	}

	// ---------------------------
	// Multiplication and Division
	// ---------------------------

	/**
	 * Multiplies this.value with another number n. You can do this in one loop:
	 * Factor n and traverse the doubly linked list in the same time. For
	 * details refer to Section 3.1 in the project description. Store the prime
	 * factorization of the product. Update value and size.
	 * 
	 * @param n
	 * @throws IllegalArgumentException
	 *             if n < 1
	 */
	public void multiply(long n) throws IllegalArgumentException {
		int i = 0;
		if (n < 1) {
			throw new IllegalArgumentException("Value for N is less than 1");
		}
		for (i = 2; (i * i < n); i++) {
			while ((isPrime(i) == true) && (n % i == 0)) {
				add(i, 1);
				n = n / i;
			}
			if (n != 1) {
				add((int) n, 1); // Adds any remainder other than 1. as long as
									// it is a prime.
			}
		}
	}

	/**
	 * Multiplies this.value with another number in the factorization form.
	 * Traverse both linked lists and store the result in this list object. See
	 * Section 3.1 in the project description for details of algorithm.
	 * 
	 * @param pf
	 */
	public void multiply(PrimeFactorization pf) {
		PrimeFactorizationIterator primeIt = pf.iterator();
		while (primeIt.hasNext()) {
			PrimeFactor primeTemp = primeIt.cursor.pFactor;
			add(primeTemp.prime, primeTemp.multiplicity);
			primeTemp = primeIt.next(); // cycles the to the next value
		}
	}

	/**
	 * Divides this.value by n. Make updates to the list, value, size if
	 * divisible. No update otherwise. Refer to Section 3.2 in the project
	 * description for details.
	 * 
	 * @param n
	 * @return true if divisible false if not divisible
	 * @throws IllegalArgumentException
	 *             if n <= 0
	 */
	public boolean dividedBy(long n) throws IllegalArgumentException {
		PrimeFactorizationIterator primeIt = iterator();
		if (n <= 0) {
			throw new IllegalArgumentException("Value of N is <= 0");
		}
		if (value < n && value != 1) {
			return false;
		} else {
			PrimeFactorization primeTemp = new PrimeFactorization(n);
			dividedBy(primeTemp);
			return true;
		}
	}

	/**
	 * Division where the divisor is represented in the factorization form.
	 * Update the linked list of this object accordingly by removing those nodes
	 * housing prime factors that disappear after the division. No update if
	 * this number is not divisible by pf. Algorithm details are given in
	 * Section 3.2.
	 * 
	 * @param pf
	 * @return true if divisible by pf false otherwise
	 */
	public boolean dividedBy(PrimeFactorization pf) { // needs work
		if ((this.value != -1) && (pf.value != -1) && (this.value < pf.value)) {
			return false;
		} else if (this.value != -1 && pf.value == -1) {
			return false;
		} else {
			return true;
		}
	}

	// -------------------------------------------------
	// Greatest Common Divisor and Least Common Multiple
	// -------------------------------------------------

	/**
	 * Computes the greatest common divisor (gcd) of this.value and an integer
	 * n, and return the result as a PrimeFactors object. Calls the method
	 * Euclidean() if this.value != OVERFLOW.
	 * 
	 * It is more efficient to factorize the gcd than n, which is often much
	 * greater.
	 * 
	 * @param n
	 * @return prime factorization of gcd
	 * @throws IllegalArgumentException
	 *             if n < 1
	 */
	public PrimeFactorization gcd(long n) throws IllegalArgumentException {
		long greatComFac = 0;
		if (n < 1) {
			throw new IllegalArgumentException("Value of N is less than 1");
		}
		if (this.value != OVERFLOW) {
			greatComFac = Euclidean(this.value, n);
		}
		PrimeFactorization primeGreatComFac = new PrimeFactorization(
				greatComFac);
		return primeGreatComFac;
	}

	/**
	 * Implements the Euclidean algorithm to compute the gcd of two natural
	 * numbers m and n. The algorithm is described in Section 4.1 of the project
	 * description.
	 * 
	 * @param m
	 * @param n
	 * @return gcd of m and n.
	 * @throws IllegalArgumentException
	 *             if m < 1 or n < 1
	 */
	public static long Euclidean(long m, long n)
			throws IllegalArgumentException {
		if (n < 1 || m < 1) {
			throw new IllegalArgumentException(
					"Value of M and N is less than 1");
		}
		long greatComFac = m;
		while (m % n != 0) {
			greatComFac = (m % n);
			m = n;
			n = greatComFac;
		}
		return greatComFac;
	}

	/**
	 * Computes the gcd of this.value and pf.value by traversing the two lists.
	 * No direct computation involving value and pf.value. Refer to Section 4.2
	 * in the project description on how to proceed.
	 * 
	 * @param pf
	 * @return prime factorization of the gcd
	 */
	public PrimeFactorization gcd(PrimeFactorization pf) {
		PrimeFactorization gcd = new PrimeFactorization();
		PrimeFactorizationIterator thisIt = this.iterator();
		PrimeFactorizationIterator primeIt = pf.iterator();

		while (thisIt.hasNext() && primeIt.hasNext()) {
			PrimeFactor thisItFactor = thisIt.cursor.pFactor;
			PrimeFactor primeItFactor = primeIt.cursor.pFactor;

			if (thisItFactor.prime == primeItFactor.prime) {
				if (thisItFactor.multiplicity < primeItFactor.multiplicity) {
					gcd.add(thisItFactor.prime, thisItFactor.multiplicity);
				} else {
					gcd.add(primeItFactor.prime, primeItFactor.multiplicity);
				}
				thisItFactor = thisIt.next();
				primeItFactor = primeIt.next();
			} else if (primeItFactor.prime > primeItFactor.prime) {
				primeItFactor = primeIt.next();
			} else {
				thisItFactor = thisIt.next();
			}
		}
		if (this.value == -1 || pf.value == -1) {
			gcd.updateValue();
		}
		return gcd;
	}

	/**
	 * Computes the least common multiple (lcm) of this.value and the number
	 * represented by pf. The list-based algorithm is given in Section 4.3 in
	 * the project description.
	 * 
	 * @param pf
	 * @return factored least common multiple
	 */
	public PrimeFactorization lcm(PrimeFactorization pf) {
		PrimeFactorization lcm = new PrimeFactorization();
		PrimeFactorizationIterator thisIt = this.iterator();
		PrimeFactorizationIterator primeIt = pf.iterator();

		while (thisIt.hasNext() && primeIt.hasNext()) {
			PrimeFactor thisItFactor = thisIt.cursor.pFactor;
			PrimeFactor primeItFactor = primeIt.cursor.pFactor;

			if (thisItFactor.prime == primeItFactor.prime) {
				if (thisItFactor.multiplicity < primeItFactor.multiplicity) {
					lcm.add(primeItFactor.prime, thisItFactor.multiplicity);
				} else {
					lcm.add(thisItFactor.prime, primeItFactor.multiplicity);
				}
				thisItFactor = thisIt.next();
				primeItFactor = primeIt.next();
			} else if (primeItFactor.prime > primeItFactor.prime) {
				lcm.add(thisItFactor.prime, thisItFactor.multiplicity);
				thisItFactor = thisIt.next();
			} else {
				lcm.add(primeItFactor.prime, primeItFactor.multiplicity);
				primeItFactor = primeIt.next();
			}
		}
		PrimeFactor thisItFactor = thisIt.cursor.pFactor;
		PrimeFactor primeItFactor = primeIt.cursor.pFactor;
		while (thisIt.hasNext()) {
			lcm.add(thisItFactor.prime, thisItFactor.multiplicity);
			thisIt.next();
		}
		while (primeIt.hasNext()) {
			lcm.add(primeItFactor.prime, primeItFactor.multiplicity);
			primeIt.next();
		}
		if (this.value == -1 && pf.value == -1) {
			lcm.value = OVERFLOW;
		} else {
			lcm.updateValue();
		}
		return lcm;
	}

	/**
	 * Computes the least common multiple of this.value and an integer n.
	 * Construct a PrimeFactors object using n and then call the lcm() method
	 * above. Calls the first lcm() method.
	 * 
	 * @param n
	 * @return factored least common multiple
	 * @throws IllegalArgumentException
	 *             if n < 1
	 */
	public PrimeFactorization lcm(long n) throws IllegalArgumentException {
		PrimeFactorization nPrimeFactor = new PrimeFactorization(n);
		PrimeFactorization lcm = this.lcm(nPrimeFactor);
		return lcm;
	}

	// ------------
	// List Methods
	// ------------

	/**
	 * Traverses the list to determine if p is a prime factor.
	 * 
	 * Precondition: p is a prime.
	 * 
	 * @param p
	 * @return true if p is a prime factor of the number represented by this
	 *         linked list false otherwise
	 * @throws IllegalArgumentException
	 *             if p is not a prime
	 */
	public boolean containsPrimeFactor(int p) throws IllegalArgumentException {
		PrimeFactorizationIterator primeIt = iterator();
		while (primeIt.hasNext()) {
			PrimeFactor pfTemp = primeIt.cursor.pFactor;
			if (pfTemp.prime == p) {
				return true;
			} else if (pfTemp.prime > p) {
				return false;
			}
			pfTemp = primeIt.next();
		}
		return false;
	}

	// The next two methods ought to be private but are made public for testing
	// purpose.

	/**
	 * Adds a prime factor p of multiplicity m. Search for p in the linked list.
	 * If p is found at a node N, add m to N.multiplicity. Otherwise, create a
	 * new node to store p and m.
	 * 
	 * Precondition: p is a prime.
	 * 
	 * @param p
	 *            prime
	 * @param m
	 *            multiplicity
	 * @return true if m >= 1 false if m < 1
	 */
	public boolean add(int p, int m) {
		PrimeFactorizationIterator primeIt = iterator();
		while (primeIt.hasNext()) {
			PrimeFactor primeTemp = primeIt.cursor.pFactor;
			if (primeTemp.prime == p) {
				primeTemp.multiplicity += m;
				updateValue();
				return true;
			}
			if (primeTemp.prime > p) {
				break;
			}
			primeTemp = primeIt.next();
		}
		primeIt.add(new PrimeFactor(p, m));
		return true;
	}

	/**
	 * Removes a prime p of multiplicity m from the list. It starts by searching
	 * for p in the linked list. Return if p is not found. Otherwise, let N be
	 * the node that stores p. If N.multiplicity > m, subtract m from
	 * N.multiplicity. Otherwise, remove the node N.
	 * 
	 * Precondition: p is a prime.
	 * 
	 * @param p
	 * @param m
	 * @return true on success false when p is either not found or found at a
	 *         node N but m > N.multiplicity
	 * @throws IllegalArgumentException
	 *             if m < 1
	 */
	public boolean remove(int p, int m) throws IllegalArgumentException {
		if (m < 1) {
			throw new IllegalArgumentException("Value of M is negative");
		}
		if (!containsPrimeFactor(p)) {
			return false;
		}
		PrimeFactorizationIterator pfIter = iterator();
		while (pfIter.hasNext()) {
			PrimeFactor pfTemp = pfIter.next();

			if (pfTemp.prime == p) {
				if (pfTemp.multiplicity > m) {
					pfTemp.multiplicity -= m;
					updateValue();
				} else {
					pfIter.remove();
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return size of the list
	 */
	public int size() {
		return size;
	}

	/**
	 * Writes out the list as a factorization in the form of a product.
	 * Represents exponentiation by a caret. For example, if the number is 5814,
	 * the returned string would be printed out as "2 * 3^2 * 17 * 19".
	 */
	@Override
	public String toString() {
		PrimeFactorizationIterator pfi = iterator();
		String tempString = new String();
		while (pfi.hasNext()) {
			String p = Integer.toString(pfi.next().prime);
			String m = Integer.toString(pfi.next().multiplicity);
			if (tempString == null && m == "1") {
				tempString = p;
			} else if (tempString == null) {
				tempString = p + "^" + m;
			} else if (tempString != null && m == "1") {
				tempString = tempString + " * " + p;
			} else {
				tempString = tempString + " * " + p + "^" + m;
			}
		}
		return tempString;
	}

	// The next three methods are for testing, but you may use them as you like.

	/**
	 * @return true if this PrimeFactorization is representing a value that is
	 *         too large to be within long's range. e.g. 999^999. false
	 *         otherwise.
	 */
	public boolean valueOverflow() {
		return value == OVERFLOW;
	}

	/**
	 * @return value represented by this PrimeFactorization, or -1 if
	 *         valueOverflow()
	 */
	public long value() {
		return value;
	}

	public PrimeFactor[] toArray() {
		PrimeFactor[] arr = new PrimeFactor[size];
		int i = 0;
		for (PrimeFactor pf : this)
			arr[i++] = pf;
		return arr;
	}

	@Override
	public PrimeFactorizationIterator iterator() {
		return new PrimeFactorizationIterator();
	}

	/**
	 * Doubly-linked node type for this class.
	 */
	private class Node {
		public PrimeFactor pFactor; // prime factor
		public Node next;
		public Node previous;

		/**
		 * Default constructor for creating a dummy node.
		 */
		public Node() {
			pFactor = null;
			next = null;
			previous = null;
		}

		/**
		 * Precondition: p is a prime
		 * 
		 * @param p
		 *            prime number
		 * @param m
		 *            multiplicity
		 * @throws IllegalArgumentException
		 *             if m < 1
		 */
		public Node(int p, int m) throws IllegalArgumentException {
			if (m < 1) {
				throw new IllegalArgumentException("Value of M is less than 1");
			}
			pFactor = new PrimeFactor(p, m);
			previous = null;
			next = null;
		}

		/**
		 * Constructs a node over a provided PrimeFactor object.
		 * 
		 * @param pf
		 * @throws IllegalArgumentException
		 */
		public Node(PrimeFactor pf) {
			pFactor = new PrimeFactor(pf.prime, pf.multiplicity);
			previous = null;
			next = null;
		}

		/**
		 * Printed out in the form: prime + "^" + multiplicity. For instance
		 * "2^3".
		 */
		@Override
		public String toString() {
			String temp = new String(Integer.toString(pFactor.prime) + "^"
					+ Integer.toString(pFactor.multiplicity));
			return temp;
		}
	}

	private class PrimeFactorizationIterator implements
			ListIterator<PrimeFactor> {
		// Class invariants:
		// 1) logical cursor position is always between cursor.previous and
		// cursor
		// 2) after a call to next(), cursor.previous refers to the node just
		// returned
		// 3) after a call to previous() cursor refers to the node just returned
		// 4) index is always the logical index of node pointed to by cursor

		private Node cursor = head;
		private Node pending = null; // node pending for removal
		private int index = 0;

		// other instance variables ...

		/**
		 * Default constructor positions the cursor before the smallest prime
		 * factor.
		 */
		public PrimeFactorizationIterator() {
			cursor = head.next;
			index = 0;
			pending = null;
		}

		@Override
		public boolean hasNext() {
			return index < (size);
		}

		@Override
		public boolean hasPrevious() {
			return (index > 0);
		}

		@Override
		public PrimeFactor next() {
			if (hasNext() == true) {
				pending = cursor;
				cursor = cursor.next;
				index++;
				return pending.pFactor;
			} else {
				return null;
			}
		}

		@Override
		public PrimeFactor previous() {
			if (hasPrevious() == true) {
				pending = cursor;
				cursor = cursor.previous;
				index--;
				return pending.pFactor;
			} else {
				return null;
			}
		}

		/**
		 * Removes the prime factor returned by next() or previous()
		 * 
		 * @throws IllegalStateException
		 *             if pending == null
		 */
		@Override
		public void remove() throws IllegalStateException {
			if (pending == null) {
				throw new IllegalStateException();
			} else {
				unlink(pending);
				size--;
			}
		}

		/**
		 * Adds a prime factor at the cursor position. The cursor is at a wrong
		 * position in either of the two situations below:
		 * 
		 * a) pf.prime < cursor.previous.pFactor.prime if cursor.previous !=
		 * null. b) pf.prime > cursor.pFactor.prime if cursor != null.
		 * 
		 * Take into account the possibility that pf.prime ==
		 * cursor.pFactor.prime.
		 * 
		 * Precondition: pf.prime is a prime.
		 * 
		 * @param pf
		 * @throws IllegalArgumentException
		 *             if the cursor is at a wrong position.
		 */
		@Override
		public void add(PrimeFactor pf) throws IllegalArgumentException {
			if (cursor.previous != head
					&& cursor.previous.pFactor.prime >= pf.prime)
				throw new IllegalArgumentException();
			if (cursor != tail && cursor.pFactor.prime <= pf.prime)
				throw new IllegalArgumentException();
			else {
				Node n = new Node(pf.clone());
				link(cursor, n);
				size++;
			}
		}

		@Override
		public int nextIndex() {
			return index;
		}

		@Override
		public int previousIndex() {
			return index - 1;
		}

		@Deprecated
		@Override
		public void set(PrimeFactor pf) {
			throw new UnsupportedOperationException(getClass().getSimpleName()
					+ " does not support set method");
		}

		// Other methods you may want to add or override that could possibly
		// facilitate
		// other operations, for instance, addition, access to the previous
		// element, etc.
		//
		// ...
		//
	}

	// --------------
	// Helper methods
	// --------------

	/**
	 * Inserts toAdd into the list after current without updating size.
	 * 
	 * Precondition: current != null, toAdd != null
	 */
	private void link(Node current, Node toAdd) {
		toAdd.previous = current.previous;
		toAdd.next = current;
		current.previous = toAdd;
	}

	/**
	 * Removes toRemove from the list without updating size.
	 */
	private void unlink(Node toRemove) {
		toRemove.previous.next = toRemove.next;
		toRemove.next.previous = toRemove.previous;
		toRemove.next = null;
		toRemove.previous = null;
	}

	/**
	 * Remove all the nodes in the linked list except the two dummy nodes.
	 * 
	 * Made public for testing purpose. Ought to be private otherwise.
	 */
	public void clearList() {
		new PrimeFactorization();
	}

	/**
	 * Multiply the prime factors (with multiplicities) out to obtain the
	 * represented integer. Use Math.multiply(). If an exception is throw,
	 * assign OVERFLOW to the instance variable value. Otherwise, assign the
	 * multiplication result to the variable.
	 * 
	 */
	private void updateValue() {
		PrimeFactorizationIterator pfi = iterator();
		value = 0;
		try {
			while (pfi.hasNext()) {
				int temp = 1;
				for (int i = pfi.cursor.pFactor.multiplicity; i < 0; --i) {
					Math.multiplyExact(temp, pfi.cursor.pFactor.prime);
				}
				value += temp;
				pfi.next();

			}
		} catch (ArithmeticException e) {
			value = OVERFLOW;
		}

	}
}