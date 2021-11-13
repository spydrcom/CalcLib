
package net.myorb.math.primenumbers.sieves;

/**
 * 
 * entry point into prime generators.
 * see SievePrimitives for supporting base layer.
 * 
 * @author Michael Druckman
 *
 */
public interface SieveDriver
{

	/**
	 * generate tables of primes and composite factorizations
	 */
	public void generatePrimes ();
	
	/**
	 * get the name of the sieve
	 * @return text string name of this algorithm
	 */
	public String getName ();

}
