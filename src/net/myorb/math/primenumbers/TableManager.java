
package net.myorb.math.primenumbers;

/**
 * 
 * primitives required to drive generation of a table of prime factorizations
 * 
 * @author Michael Druckman
 *
 */
public interface TableManager
{

  	/**
  	 * check table entry for mark as prime or composite
  	 * @param value the entry of the table to check
  	 * @return TRUE = has been marked
  	 */
  	public boolean markIsPresent (int value);
  	
   	/**
   	 * mark a value with an initial factorization
   	 * @param value the value for which factorization is being added
   	 * @param prime the first prime number found to be a factor of this value
   	 * @param mul the multiplier equaling value/prime, this is the remainder of the factorization
   	 */
  	public void markComposite (int value, int prime, int mul);

	/**
	 * update table marking all unmarked (if any) composites which contain given prime as factor
	 * @param primeNumber the prime value used to identify composites
	 */
	public void markCompositesFor (int primeNumber);

	/**
	 * enter specified parameter as prime and mark all associated composites
	 * @param primeNumber the base prime number to mark and check for composites
	 */
	public void markPrimeAndCompositesFor (int primeNumber);

	/**
	 * a prime is marked in the table by setting base=value and multiplier=1
	 * @param primeNumber the value being marked as prime
	 */
	public void markPrime (int primeNumber);
	
	/**
	 * the size of the table of primes being constructed
	 * @return the count of entries in the table
	 */
	public int getTableSize ();

}
