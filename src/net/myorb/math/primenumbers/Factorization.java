
package net.myorb.math.primenumbers;

import net.myorb.math.SpaceManager;
import net.myorb.math.SignManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Map;

import java.math.BigInteger;

/**
 * 
 * a factorization contains prime/exponent pairs plus the operators on those values
 * 
 * @author Michael Druckman
 *
 */
public class Factorization extends SignManager
{

	/**
	 * a set of BigInteger prime values identifies the factors in a Factorization
	 */
	public static class PrimeFactors extends HashSet<BigInteger>
	{ private static final long serialVersionUID = -1481135277277985774L; }

	/**
	 * map primes to their exponent
	 *   in the representation
	 */
	protected FactorCollection factorCollection;

	/**
	 * Factorization is the control logic for a factor collection
	 */
	public Factorization () { factorCollection = new FactorMapping (); }

	/**
	 * get the collection of factors for this representation
	 * @return the factor hash for this factorization
	 */
	public FactorCollection getFactors () { return factorCollection; }

	/**
	 * an underlying mechanism provides an expedited means of computing factorizations
	 */
	public interface Underlying
	{
		/**
		 * the number of expedited factorizations available
		 * @return the count available
		 */
		int factorizationCount ();

		/**
		 * build a factorization for an integral value
		 * @param value the value for which factorization is being built
		 * @return the factorization object built
		 */
		Factorization factorsFor (int value);

		/**
		 * is this given value in the range of expedited factorizations
		 * @param value the value for which factorization is requested
		 * @return TRUE when value is in range
		 */
		boolean inTableRange (BigInteger value);
		
		/**
		 * get a list of primes to use to build a factorization
		 * @param value the value for which factorization is being built
		 * @param bitscale the number of bits in the value being factored
		 * @return a List object of prime BigInteger values
		 */
		public List<BigInteger> getPrimesForCheck (BigInteger value, int bitscale);

		/**
		 * get a list of primes
		 * @param limit the upper bound of the range
		 * @return list of primes
		 */
		List<BigInteger> getPrimesUpTo (int limit);

		/**
		 * get a list of primes
		 * @param n the count of primes requested
		 * @return list of primes
		 */
		List<BigInteger> getPrimes (int n);

		/**
		 * get a list of all computed primes
		 * @return list of primes
		 */
		List<BigInteger> getAllPrimes ();

		/**
		 * find the Nth prime
		 * @param n index of prime of interest
		 * @return the Nth prime
		 */
		BigInteger getNthPrime (int n);

		/**
		 * check for option being enabled
		 * @param named the name of the option to check
		 * @return TRUE is option is enabled
		 */
		boolean isOptionEnabled (String named);
	}

	/**
	 * identify the expedited factorization mechanism
	 * @param underlying the object which implements the interface to be used
	 */
	public static void setImplementation (Underlying underlying)
	{
		factorizationFieldManager = new FactorizationFieldManager ();
		implementation = underlying;
	}

	/**
	 * query underlying implementation
	 * @return the object providing underlying implementation
	 */
	public static Underlying getImplementation () { return implementation; }
	protected static Underlying implementation;

	/**
	 * get component field manager
	 * @return the type manager for factors
	 */
	public FactorizationFieldManager getFieldManager () { return factorizationFieldManager; }
	private static FactorizationFieldManager factorizationFieldManager;

	/**
	 * change a Factorization object to contain a new set of factors
	 * @param f the Factorization to be copied
	 */
	public void set (Factorization f)
	{
		FactorCollection factors;
		(factors = this.getFactors ()).removeFactors ();
		factors.addFactors (f.getFactors ());
	}

	/**
	 * use underlying collection for efficient update
	 * @param factor the prime value being added/updated to/in factorization
	 * @param factorCount the number of the factors being introduced
	 */
	public void addFactor (BigInteger factor, int factorCount)
	{
		if (factorCount == 0) return;															// no new factor(s) to be added
		FactorCollection collection = getFactors ();											// access collection object of factorization
		if (!collection.hasFactor (factor)) collection.addFactor (factor, factorCount);			// new factor so count is the exponent value
		else collection.setFactor (factor, collection.checkExponent (factor) + factorCount);	// add count to previous exponent
	}

	/**
	 * get a set of primes which contains the entries of two Factorizations joined
	 * @param factor the alternate Factorization to be joined to THIS one
	 * @return a HashSet with both sets of prime keys
	 */
	public PrimeFactors join (Factorization factor)
	{
		PrimeFactors primes = new PrimeFactors ();
		primes.addAll (factor.getFactors ().getPrimes ());
		primes.addAll (this.getFactors ().getPrimes ());
		return primes;
	}

	/**
	 * compute product of factors resulting in integer representation of value
	 * @return an extended precision representation of integral value
	 */
	public BigInteger reduce ()
	{
		BigInteger result = BigInteger.ONE;										// start product as 1
		FactorCollection factors = this.getFactors ();
		for (BigInteger prime : factors.getPrimes ())							// the key set is the list of primes in this value
		{
			int exponent = factors.readExponentFor (prime);
			result = result.multiply (prime.pow (exponent));
		}																		// prime value maps to exponent value
		if (this.isNegative ()) result = result.negate ();						// respect negative flag
		return result;
	}

	/**
	 * normalize Factorization into representation as a fraction
	 * @param value the value for which fraction representation is being built
	 * @param manager a Factorization manager for the Fraction type manager
	 * @return a Fraction object with normalized numerator/denominator
	 */
	public static Distribution normalize
	(Factorization value, SpaceManager<Factorization> manager) 
	{
		value.getFactors ().normalize ();										// eliminate 1 as a factor
		Distribution normalized = new Distribution (manager);
		normalized.set (value.duplicate ());									// make a copy of the value
		normalized.copySign (value);											// copy sign flag to result
		return normalized;
	}

	/**
	 * invert the negative marking and return a copy of the value with the inverted flag
	 * @return a Factorization equivalent to a negated copy of THIS
	 */
	public Factorization negate ()
	{
		Factorization result = this.duplicate ();
		result.copyInvertedSign (this);
		return result;
	}

	/**
	 * compute sum of THIS Factorization with specified addend
	 * @param addend the Factorization of the alternate addend being summed
	 * @return a Factorization representing the sum
	 */
	public Factorization add (Factorization addend) 
	{
		Distribution
			thisItem = normalize (this, factorizationFieldManager),		// represent items as normalized fractions
			addendItem = normalize (addend, factorizationFieldManager);
		return Distribution.flattened (thisItem.add (addendItem));		// use fraction addition and flatten resulting sum
	}

	/**
	 * compute product of THIS Factorization with specified factor
	 * @param factor the Factorization of the alternate factor being multiplied
	 * @return a Factorization representing the product
	 */
	public Factorization multiplyBy (Factorization factor)
	{
		if (factor == null) return null;
		Factorization result = new Factorization ();
		PrimeFactors primes = join (factor);							// create set of factors from both value

		FactorCollection
			tFactors = this.getFactors (),
			fFactors = factor.getFactors (),							// access the factor lists for each multiplicand
			rFactors = result.getFactors ();							// also access the factor list of the result
		int exp1, exp2, sum;

		for (BigInteger prime : primes)									// iterate over set of primes taken from join
		{
			exp1 = tFactors.checkExponent (prime);
			exp2 = fFactors.checkExponent (prime);						// get exponents associated with prime for each value

			if ((sum = exp1 + exp2) != 0)
			{ rFactors.setFactor (prime, sum); }						// the sum may be zero in which case we void out prime
		}
		result.setSignXor (this, factor);								// negative for result becomes XOR of two value flags
		return result;
	}

	/**
	 * compute quotient of THIS Factorization with specified divisor
	 * @param divisor the Factorization of the divisor being used in the computation THIS/divisor
	 * @return a Factorization representing the computed result
	 */
	public Factorization divideBy (Factorization divisor)
	{
		if (divisor == null)
		{ throw new RuntimeException ("Division By Zero"); }
		Factorization result = new Factorization ();
		PrimeFactors primes = join (divisor);							// create set of factors from both value

		FactorCollection
			tFactors = this.getFactors (),
			dFactors = divisor.getFactors (),							// access the factor lists for divisor & dividend
			rFactors = result.getFactors ();							// also access the factor list of the result
		int exp1, exp2, dif;

		for (BigInteger prime : primes)									// iterate over set of primes taken from join
		{
			exp1 = tFactors.checkExponent (prime);
			exp2 = dFactors.checkExponent (prime);						// get exponents associated with prime for each value

			if ((dif = exp1 - exp2) != 0)
			{ rFactors.setFactor (prime, dif); }						// the sum may be zero in which case we void out prime
		}
		result.setSignXor (this, divisor);								// negative for result becomes XOR of two value flags
		return result;
	}

	/**
	 * raise THIS to power with given exponent
	 * @param n the integral value of the exponent
	 * @return a Factorization representing the computed result
	 */
	public Factorization pow (int n)
	{
		if (n == 1) return this;
		Factorization product = this;
		if (n == 0) return factorizationFieldManager.getOne ();
		if (n < 0) return factorizationFieldManager.invert (pow (-n));
		for (int i=1; i<n; i++) product = product.multiplyBy (this);
		return product;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractMap#toString()
	 */
	public String toString ()
	{
		StringBuffer buffer = new StringBuffer ();
		FactorCollection factors = this.getFactors ();

		Object[] keys = 
			factors.getPrimes ().toArray ();
		Arrays.sort (keys);								// primes are placed in sorted order
		
		for (Object prime : keys)						// the key set is the list of prime factors
		{
			appendFactor (prime.toString (), buffer);
			int e = factors.readExponentFor (prime);	// exponent for this prime read from map
			appendExponent (e, buffer);
		}

		if (buffer.length () == 0)
		{
			if (this.isNegative ())						// empty prime list implies value is 1
				return " - 1";							//		but may be negative
			else return "1";
		}

		return buffer.toString ();
	}

	/**
	 * format the exponent portion of a factor display
	 * @param exponent the integer value of the exponent to be displayed
	 * @param toBuffer the buffer collecting the display
	 */
	private void appendExponent (int exponent, StringBuffer toBuffer)
	{
		if (exponent < 0)
		{
			toBuffer
				.append ("^(")
				.append (exponent).append (")");			// place negative exponents in parenthesis
		}
		else if (exponent > 1)
		{
			toBuffer.append ("^").append (exponent);		// output exponent values greater than 1
		}
	}

	/**
	 * format text display of factor value
	 * @param factor the display representation of the value
	 * @param toBuffer the buffer collecting the display
	 */
	private void appendFactor (String factor, StringBuffer toBuffer)
	{
		
		if (toBuffer.length () > 0)
		{
			toBuffer.append (" * ");					// continuing a factor list so insert multiplier
		}
		else if (this.isNegative ())
		{
			toBuffer.append (" - ");					// starting the list might need to place negative indicator
		}

		toBuffer.append (factor);						// prime value placed in buffer
	}

	/**
	 * build a duplicate of THIS object
	 * @return the duplicate object
	 */
	public Factorization duplicate ()
	{
		Factorization dup = new Factorization ();
		dup.getFactors ().addFactors (this.getFactors ());
		dup.copySign (this);
		return dup;
	}

}

/**
 * factors of a Factorization are collected as BigInteger keys mapped to integer exponents
 */
class FactorMapping extends HashMap<BigInteger,Integer> implements FactorCollection
{

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.FactorCollection#getPrimes()
	 */
	public Set<BigInteger> getPrimes () { return this.keySet (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.FactorCollection#setFactor(java.math.BigInteger, int)
	 */
	public void setFactor (BigInteger prime, int exponent)
	{
		this.put (prime, exponent);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.FactorCollection#hasFactor(java.math.BigInteger)
	 */
	public boolean hasFactor (BigInteger prime)
	{
		return this.containsKey (prime);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.FactorCollection#addFactor(java.math.BigInteger, int)
	 */
	public void addFactor (BigInteger prime, int exponent)
	{
		if (this.hasFactor (prime))											// determine if this prime has already been seen as a factor
		{ this.setFactor (prime, this.get (prime) + exponent); }			// exponent previously set (prime was a previous factor) so add in count
		else { this.setFactor (prime, exponent); }							// first time prime is seen so use parameter (not a previous factor)
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.FactorCollection#addFactors(net.myorb.math.primenumbers.FactorCollection)
	 */
	public void addFactors (FactorCollection factors)
	{ this.putAll (factors.getFactorMap ()); }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.FactorCollection#removeFactors()
	 */
	public void removeFactors () { this.clear (); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.FactorCollection#removeFactor(java.math.BigInteger)
	 */
	public void removeFactor (BigInteger prime) { this.remove (prime); }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.FactorCollection#incrementExponentFor(java.math.BigInteger)
	 */
	public void incrementExponentFor (BigInteger prime)
	{
		this.addFactor (prime, 1);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.FactorCollection#readExponentFor(java.lang.Object)
	 */
	public int readExponentFor (Object prime)
	{
		return this.get (prime);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.FactorCollection#checkExponent(java.math.BigInteger)
	 */
	public int checkExponent (BigInteger prime)
	{
		if (this.hasFactor (prime))
			return this.readExponentFor (prime);
		else return 0;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.FactorCollection#normalize()
	 */
	public void normalize ()
	{
		removeFactor (BigInteger.ONE);						// empty is treated as 1
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.FactorCollection#isPrime()
	 */
	public boolean isPrime ()
	{
		normalize ();										// eliminate 1 as factor
		if (this.isEmpty ()) return true;					// one is considered prime
		if (this.size () > 1) return false;					// more than one factor implies not prime
		BigInteger prime = this.getSolitaryPrime ();		// get the one prime in this factorization
		return this.readExponentFor (prime) == 1;			// single prime with exponent=1 implies prime
	}
	
	/**
	 * when only one factor
	 * @return the first entry of iterator is the one prime value
	 */
	private BigInteger getSolitaryPrime ()
	{
		return this.getPrimes ().iterator ().next ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.FactorCollection#getFactorMap()
	 */
	public Map<BigInteger,Integer> getFactorMap () { return this; }

	private static final long serialVersionUID = -2481135277277985774L;

}

