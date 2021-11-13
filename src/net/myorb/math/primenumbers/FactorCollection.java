
package net.myorb.math.primenumbers;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

/**
 * 
 * the functionality required to represent a set of factors making up a value.
 * the factorization is built as a collection of prime^exponent pairs which multiplied together make the value.
 * the methods of this interface allow queries of the primes and their exponents.
 * 
 * @author Michael Druckman
 *
 */
public interface FactorCollection
{

	/**
	 * get set of primes found in this factorization
	 * @return set of BigInteger prime values
	 */
	public Set<BigInteger> getPrimes ();

	/**
	 * set the exponent of given prime
	 * @param prime the selected prime to have updated exponent
	 * @param exponent the new value of the exponent
	 */
	public void setFactor (BigInteger prime, int exponent);

	/**
	 * is specified prime part of THIS Factorization
	 * @param prime the prime value in question
	 * @return TRUE if prime is a factor
	 */
	public boolean hasFactor (BigInteger prime);

	/**
	 * eliminate factor from collection
	 * @param prime the factor to be removed
	 */
	public void removeFactor (BigInteger prime);

	/**
	 * add to THIS factorization
	 * @param prime the prime value being added
	 * @param exponent the count of factors of this prime being added
	 */
	public void addFactor (BigInteger prime, int exponent);

	/**
	 * add a set of factors to THIS factorization
	 * @param factors a collection object holding a set of factors
	 */
	public void addFactors (FactorCollection factors);

	/**
	 * remove all entries in this Factorization
	 */
	public void removeFactors ();

	/**
	 * increment the exponent value of specified prime associated with THIS FactorMapping
	 * @param prime the prime value to add as a factor
	 */
	public void incrementExponentFor (BigInteger prime);

	/**
	 * the integer value mapped from a prime is the exponent for that prime in this Factorization
	 * @param prime the prime to be read from THIS Factorization
	 * @return the exponent value for the specified prime
	 */
	public int checkExponent (BigInteger prime);

	/**
	 * when factor is known to be in the map
	 * @param prime the factor to get the exponent for
	 * @return the exponent value
	 */
	public int readExponentFor (Object prime);

	/**
	 * look at factors to determine prime status
	 * @return TRUE if THIS is factorization of a prime
	 */
	public boolean isPrime ();

	/**
	 * the value ONE(1) need not be in a Factorization
	 */
	public void normalize ();

	/**
	 * get the mapping of factors to exponents
	 * @return a Map object
	 */
	public Map<BigInteger,Integer> getFactorMap ();
	
}
