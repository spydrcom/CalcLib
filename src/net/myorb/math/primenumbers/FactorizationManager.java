
package net.myorb.math.primenumbers;

import net.myorb.math.libraries.FactorizationLibrary;

import java.math.BigInteger;
import java.util.List;

/**
 * 
 * an encapsulation of logic that performs prime factorization
 * 
 * @author Michael Druckman
 *
 */
public class FactorizationManager extends ImplementationAccess
{


	/*
	 * brute force implementation of factorization
	 */


	/**
	 * check a prime value for REM = 0
	 * @param value the value being factored
	 * @param by the prime being check for inclusion
	 * @param factors the factors collected so far
	 * @return the remainder after attempt
	 */
	public BigInteger attemptReduction
	(BigInteger value, BigInteger by, Factorization factors)
	{
		int count = 0;
		BigInteger remainder = value;

		while (hasFactor (remainder, by))														// loop until this prime is not a factor
		{																						// each prime may be a multiple factor of the remainder
			count++;																			// counting number of times new factor goes into value
			remainder = remainder.divide (by);													// remainder computed with value removed
			if (isSmallEnough (remainder)) break;												// remainder is a table lookup?
		}

		factors.addFactor (by, count);															// introduce to count
		return remainder;
	}


	/**
	 * find prime factors of a value
	 * @param value the value to be factored
	 * @return a factor description of value
	 */
	public Factorization attemptFactorization (BigInteger value)
	{
		BigInteger remainder = value;
		Factorization factors = new Factorization ();											// empty factorization is 1

		for (BigInteger potentialFactor : getPrimesForCheck (value))							// check all primes from table within range
		{
			remainder = attemptReduction (remainder, potentialFactor, factors);					// attempt each from table entry

			if (isSmallEnough (remainder))														// shortcut for table lookup
			{
				return factors.multiplyBy (factorsFor (remainder.intValue ()));
			}
		}

		factors.addFactor (remainder, 1);														// remainder has no recognizable factors, assumed to be prime
		return factors;
	}


	/*
	 * alternate implementation of factorization
	 */


	/**
	 * iterate over primes list finding factors of value
	 * @param value the value to be represented in this Factorization
	 * @param multiplier the factors found to be in value
	 * @return the remainder after factors removed
	 */
	public BigInteger identifyFactors
	(BigInteger value, Factorization multiplier)
	{
		probablePrime = false;
		boolean reduced = false;
		Factorization factors = multiplier;
		Factorization newPrimeFactor = null;										// holding place for a newly found factor
		BigInteger remainder = value;												// start with remainder = full value, as if prime
		iterationCount = 0;

		FACTORS:
			for (BigInteger potentialFactor : getPrimesForCheck (value))			// iterate over primes list
			{
				while (hasFactor (remainder, potentialFactor))						// loop until this prime is not a factor
				{																	// each prime may be a multiple factor of the remainder
					if (!reduced)
					{
						newPrimeFactor = prime (potentialFactor);					// build a prime object for this newly found factor
						reduced = true;												// at least one factor has been found in this iteration
					}
					factors = factors.multiplyBy (newPrimeFactor);					// prime is multiplied into Factorization 
					remainder = remainder.divide (potentialFactor);					// remove prime from remainder
					if (isSmallEnough (remainder)) break FACTORS;					// is remainder small enough?
				}
				iterationCount++;
				if (!reduced) continue;												// optimization check makes no sense if value not reduced
				if (probablePrime = isProbablePrime (remainder)) break;				// a probable prime evaluation for short circuit
				reduced = false;
			}
		multiplier.set (factors);
		return remainder;
	}
	protected boolean probablePrime;												// flags control optimization execution patterns
	public static int iterationCount;


	/**
	 * call underlying prime range selection method
	 * @param value the value to be represented in this Factorization
	 * @return the selected list of primes
	 */
	public List<BigInteger> getPrimesForCheck (BigInteger value)
	{
		bitscale = bitScale (value);											// compute bit length and save for other uses
		return getImplementation ().getPrimesForCheck (value, bitscale);		// use underlying primes selection algorithm
	}
	protected int bitscale;														// bit length can approximate SQRT


	/**
	 * use bitlength of value to approximate SQRT
	 * @param value the value being factorized, maximum factor is half the scale of this value
	 * @return approximation of SQRT by bitlength
	 */
	public static int bitScale (BigInteger value)
	{
		int bitlength = value.bitLength () / 2;									// half the bit length gives approximate SQRT size

		if (dumpSelected ())
		{
			System.out.println ("[ val: " + value + ", bits: " + bitlength + " ]");
		}

		return bitlength + 1;
	}


	/**
	 * format a trace of the parameters used to build the Factorization
	 * @param value the value to be represented in this Factorization
	 * @param remainder the remainder of the reduction process
	 * @param multiplier the factors found to be in value
	 */
	public void formatTrace
	(BigInteger value, BigInteger remainder, Factorization multiplier)
	{
		/**
		 * lots of trace data captured (on option)
		 * to help understand factorization
		 */

		if  (dumpSelected ())
		{																		// trace the factors found in larger factorizations
			System.out.println ("<<<");
			int count = getFactorizationCount ();								// the size of prime table
			System.out.println ("factoring: " + value);							// trace the value being factored
			System.out.print ("multiplier: " + multiplier);						// show the factors found
			System.out.print ("   remainder prob prime: " + probablePrime);		// was result likely prime
			System.out.print ("   [" + bitscale + "/" + count + "]");			// the bit lengh of the value
			System.out.println (" remainder: " + remainder);					// the final remainder
			System.out.println (">>>");
		}
	}


	/**
	 * compute the appropriate final product of the factors found with the remainder
	 * @param remainder the remainder of the reduction process
	 * @param multiplier the factors found to be in value
	 * @return the final Factorization
	 */
	public static Factorization product
	(BigInteger remainder, Factorization multiplier)
	{
		/**
		 * the multiplier now contains all factors found.
		 * the break in the iterative loop came from either a small remainder
		 * or from a large value that appears to be prime. the remainder 
		 * and the multiplier are the final product.
		 */
		Factorization result;
		if (isSmallEnough (remainder))
		{
			result = forValue (remainder).multiplyBy (multiplier);
		}																		// use simple table lookup for remainder
		else result = prime (remainder).multiplyBy (multiplier);				// remainder may not be prime but that is OK
		result.getFactors ().normalize ();										// eliminate 1 as a factor, treat empty as 1
		return result;															// normalized result with all factors
	}


	/**
	 * execute sequence of steps to compute result
	 * @param value the value to be represented in this Factorization
	 * @return the resulting computed Factorization
	 */
	protected Factorization findFactors (BigInteger value)
	{
		Factorization multiplier = onePrime ();									// multiplier starts at 1, no factors found yet
		BigInteger remainder = identifyFactors (value, multiplier);
		formatTrace (value, remainder, multiplier);
		return product (remainder, multiplier);									// normalized result with all factors
	}


	/**
	 * determine factors that should be present in representation
	 * @param value the value to be represented in this Factorization
	 * @return the resulting computed Factorization
	 */
	public static Factorization findFactorization (BigInteger value)
	{
		if (isProbablePrime (value)) return prime (value);						// quick short circuit check for probably prime
		return new FactorizationManager ().findFactors (value);
	}


	/**
	 * check for zero MOD dividing prime into value
	 * @param value the value being checked for having factor
	 * @param potentialFactor the prime number being checked as a factor
	 * @return TRUE if factor divides evenly into value
	 */
	public static boolean hasFactor
	(BigInteger value, BigInteger potentialFactor)
	{ return BigInteger.ZERO.compareTo (value.mod (potentialFactor)) == 0; }	// test factor divides into value


	/**
	 * approximation of potential of a value for being prime
	 * @param value the value being checked for prime
	 * @return TRUE if value probably prime
	 */
	public static boolean isProbablePrime (BigInteger value)
	{
		if (RUNNING_SHORT_CIRCUIT_TEST) return false;							// for tests which verify short circuit is not too aggressive
		return value.isProbablePrime (10);										// use BigInteger algorithm for probable prime evaluation
	}
	static boolean RUNNING_SHORT_CIRCUIT_TEST = false;

	/**
	 * empty factorization represents one (1)
	 * @return the empty factorization representing 1
	 */
	public static Factorization onePrime () { return new Factorization (); }


	/**
	 * Factorization of a prime is a single value with exponent=1
	 * @param value the value to be represented as a prime
	 * @return the constructed Factorization object
	 */
	public static Factorization prime (BigInteger value)
	{
		Factorization result = new Factorization ();
		result.getFactors ().setFactor (value, 1);
		return result;
	}


	/**
	 * determine means for building Factorization and maintain negative flag
	 * @param value the value for which factorization is being built
	 * @return a Factorization equivalent to the value specified
	 */
	public static Factorization forValue (long value)
	{
		checkImplementation ();
		boolean negative = false;
		Factorization result = null;
		if (value == 0) return null;											// zero represented as null
		else if (value < 0)
		{
			value = -value;														// maintain sign of value and flag
			negative = true;
		}
		if (value >= getFactorizationCount ())
		{ result = findFactorization (BigInteger.valueOf (value)); }			// value is not in expedited table
		else result = factorsFor ((int) value);									// table lookup is fastest
		result.setSign (negative);												// copy sign flag
		return result;
	}


	/**
	 * determine means for building Factorization and maintain negative flag
	 * @param value the value for which factorization is being built
	 * @return a Factorization equivalent to the value specified
	 */
	public static Factorization forValue (BigInteger value)
	{
		checkImplementation ();
		boolean negative; int comparison; Factorization result;
		if ((comparison = value.compareTo(BigInteger.ZERO)) == 0) return null;	// zero represented as null
		if (negative = comparison < 0) value = value.negate ();					// maintain sign flag for result
		if (getImplementation ().inTableRange (value))							// value can be found in table for faster execution
		{ result = factorsFor (value.intValue ()); }							// table lookup is fastest when available
		else result = findFactorization (value);								// value is not in expedited table
		result.setSign (negative);												// copy sign flag
		return result;
	}


	public static void checkImplementation ()
	{
		if (getImplementation () == null)
			throw new RuntimeException ("Factors table not initialized");
	}


	public static void main (String[] args)
	{
		FactorizationLibrary fl = 
				new net.myorb.math.libraries.FactorizationLibrary ();
		fl.configure ("5000000");

		RUNNING_SHORT_CIRCUIT_TEST = true;
		String arg = "1218123862066680455119155234529893506428158216311965900";
		System.out.println (forValue (new BigInteger (arg)));
	}


}

