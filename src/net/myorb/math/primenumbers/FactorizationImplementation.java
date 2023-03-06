
package net.myorb.math.primenumbers;

import net.myorb.math.specialfunctions.ExponentialIntegral;

import net.myorb.math.ComputationConfiguration;
import net.myorb.math.primenumbers.sieves.*;

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.Date;
import java.util.List;

/**
 * 
 * build support structures for prime number factorization algorithms
 * 
 * @author Michael Druckman
 *
 */
public class FactorizationImplementation
	extends ComputationConfiguration<Factorization>
	implements Factorization.Underlying, TableManager, SieveDriver
{


	/**
	 * a support object keeps an internal table of factorizations
	 * @param factorsToCompute the count of factorizations to be kept as a constant pool
	 */
	public FactorizationImplementation (int factorsToCompute)
	{
   		base = new int[factorsToCompute];
		multiplier = new int[factorsToCompute];
   		baseFactors = new int[factorsToCompute];
  		Arrays.fill (base, 0, factorsToCompute-1, 0);					// fill the base array with zero values indicating no factors found
 		FACTORIZATION_COUNT = BigInteger.valueOf (factorsToCompute);	// a big integer version of the size of the table for comparison with values
	   	selectedOptions = new OptionMapping ();
	   	this.establishPrimeLists ();
	}


	/**
	 * size of constant table to build
	 */
	public final BigInteger FACTORIZATION_COUNT;


	/**
	 * tabulate identified primes and supporting data
	 * - parallel lists for prime numbers and counts
	 */
	void establishPrimeLists ()
	{
	   	primes = new ArrayList<BigInteger> ();
	   	primeCounts = new ArrayList<Integer> ();
	   	primeCounts.add (0);
	}
	protected List<Integer> primeCounts;
	protected List<BigInteger> primes;


	/**
	 * collect pairs of base/multiplier giving an initial prime and the remainder making the value
	 */
	protected int[] base, baseFactors;		// base is the first factor found for a value, and baseFactors is the exponent for that factor
	protected int[] multiplier;				// multiplier is not necessarily prime, but is smaller which means its factorization is also in table


	/**
	 * determine if given entry in table represents prime
	 * @param value the value for which determination is to be made
	 * @return TRUE = value is prime
	 */
	public boolean isPrime (int value)		// both multiplier == 1 and baseFactors == 1 required to recognize as prime
	{
		//return (multiplier[value] == 1 &&
		//		baseFactors[value] == 1);
		return base[value] == value;		// however, base holding full value is simpler test
	}


	/********************************************
	 * implementation of TableManager interface *
	 ********************************************/

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.TableManager#getTableSize()
	 */
	public int getTableSize () { return base.length; }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.TableManager#markIsPresent(int)
	 */
	public boolean markIsPresent (int value)
	{
		return base[value] != 0;
	}

   	/* (non-Javadoc)
   	 * @see net.myorb.math.primenumbers.TableManager#markComposite(int, int, int)
   	 */
   	public void markComposite (int value, int prime, int mul)
	{
		base[value] = prime;								// first prime found to be a true factor of the value
		baseFactors[value] = 1;								// the base exponent may be > 1, but must be at least one
		multiplier[value] = mul;							// multiplier is remainder after being reduced by cancellation of this factor
		adjustForMultipleFactors (value);
	}

   	/**
   	 * check multiplier for additional factors of the base prime
   	 * @param value the entry of the table being checked
   	 */
   	public void adjustForMultipleFactors (int value)
   	{
   		while
   			(
   				multiplier[value] > 1 &&					// if multiplier reduced to 1 base prime is only factor
   				multiplier[value] % base[value] == 0		// factor is found when remainder after division by prime is zero
   			)
   		{
   			baseFactors[value]++;							// increase exponent to represent additional factor
   			multiplier[value] /= base[value];				// remove a copy of factor from multiplier
   		}
   	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.TableManager#markPrime(int)
	 */
	public void markPrime (int primeNumber)
	{														// definition of prime, only factors are self and 1
		baseFactors[primeNumber] = 1;						// the base exponent for prime is necessarily one (1)
		base[primeNumber] = primeNumber;					// first prime found to be a true factor of the value (self for prime)
		multiplier[primeNumber] = 1;						// multiplier is remainder after being reduced by cancellation of this factor (1 for prime)

		BigInteger internal =
			BigInteger.valueOf (primeNumber);				// BigInteger is used as internal representation to handle large multipliers
		primes.add (internal);								// add to list of primes as big integer object
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.TableManager#noteCount()
	 */
	public void noteCount ()
	{
		primeCounts.add (primes.size ()); 					// this provides counts of primes generated
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.TableManager#markCompositesFor(int)
	 */
	public void markCompositesFor (int prime)
	{
		int primeMultiple = prime;
		int tableEnd = getTableSize () - 1;
		for (int multiplier = 2; ; multiplier++)			// start at 2 and increment multiplier on each iteration, limit unspecified
		{
			primeMultiple += prime;							// mark multiples of this new prime as having factors in master factor table

			if (primeMultiple > tableEnd) return;			// stop when end of table seen having added prime repeatedly into running sum

			if (!markIsPresent (primeMultiple))				// don't re-mark items already marked with factors (improved efficiency)
			{												// table entry shows value = basePrime * multiplier (composite number)
				markComposite
				(
					primeMultiple, prime, multiplier
				);
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.TableManager#markPrimeAndCompositesFor(int)
	 */
	public void markPrimeAndCompositesFor (int primeNumber)
	{
		markPrime (primeNumber);								// mark recognized value as prime (base=value, multiplier=1)
		markCompositesFor (primeNumber);						// mark composites relative to this prime
	}


	/*******************************************
	 * implementation of SieveDriver interface *
	 *******************************************/

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.sieves.SieveDriver#generatePrimes()
	 */
	public void generatePrimes ()
	{
		int tableEnd = getTableSize () - 1;
		int prime = 1;

		markComposite (1, 1, 1);								// set first entry to be consistent

		while (true)
		{
			do													// search table for next prime indicated by base[value] == 0
			{
				noteCount ();					 				// this dump provides counts of primes generated is each block (showing pattern)
				if (++prime > tableEnd) return;					// done when the count has reached end of table, prime is value being checked
			} while (markIsPresent (prime));					// if base (mark) for this number is still zero then it is recognized as prime
			markPrimeAndCompositesFor (prime);					// mark recognized value as prime and mark associated composites as well
		}
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.sieves.SieveDriver#getName()
	 */
	public String getName ()
	{
		return "Sieve Of Eratosthenes (DEFAULT)"; // the name of the sieve selected, mark this as the default version
	}


	/********************************************************
	 * implementation of Factorization.Underlying interface *
	 ********************************************************/

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.Factorization.Underlying#factorizationCount()
	 */
	public int factorizationCount () { return getTableSize (); } // the number of constant factorizations available

	/* (non-Javadoc)
   	 * @see net.myorb.math.primenumbers.Factorization.Underlying#inTableRange(java.math.BigInteger)
   	 */
   	public boolean inTableRange (BigInteger value)
   	{
   		return value.compareTo(FACTORIZATION_COUNT) < 0; // table range is 0..FACTORIZATION_COUNT-1
   	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.Factorization.Underlying#factorsFor(int)
	 */
	public Factorization factorsFor (int value)
	{
		Factorization f = new Factorization ();
		addFactorsFor (value, f.getFactors ());
		return f;
	}

	/**
	 * start from value parameter and walk the base/multiplier tables to accumulate factors
	 * @param value the starting value to evaluate in the primes table
	 * @param f the object collecting factors
	 */
	public void addFactorsFor (int value, FactorCollection f)
	{
		while (value >= 2)
		{
			f.addFactor												// increase exponent of this prime to introduce it as factor
				(
					BigInteger.valueOf (base[value]),
					baseFactors[value]
				);
			value = multiplier[value];								// value is now remainder after being reduced by cancellation of this factor
		}
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.Factorization.Underlying#getPrimesForCheck(java.math.BigInteger, int)
	 */
	public List<BigInteger> getPrimesForCheck (BigInteger value, int bitscale)
	{
		boolean dump = isOptionEnabled (ComputationConfiguration.DUMP_PRIME_FACTORIZATION);
		return PrimesTableScan.getPrimesForCheck (value, bitscale, primes, FACTORIZATION_COUNT, dump);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.Factorization.Underlying#getPrimesUpTo(int)
	 */
	public List<BigInteger> getPrimesUpTo (int limit)
	{
		if (primes == null)
			throw new RuntimeException ("Factors table not initialized");
		for (int i = 0; i < primes.size (); i++)
		{
			if (primes.get (i).intValue () > limit)
			{ return primes.subList (0, i); }
		}
		return primes;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.Factorization.Underlying#getNthPrime(int)
	 */
	public BigInteger getNthPrime (int n) { return n == 0 ? BigInteger.ONE : primes.get (n-1); }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.Factorization.Underlying#getPrimes(int)
	 */
	public List<BigInteger> getPrimes (int n) { return primes.subList (0, n); }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.Factorization.Underlying#getAllPrimes()
	 */
	public List<BigInteger> getAllPrimes () { return primes; }


	/*
	 * prime counting functions
	 */

	/**
	 * count prime factors
	 * @param n the number to evaluate
	 * @param multiplicity sum exponents as contributions
	 * @param singleFactored any squared factor results as 0
	 * @return the count of factors given criteria
	 */
	public int countPrimes (int n, boolean multiplicity, boolean singleFactored)
	{
		int count = 0, factors;
		if (n >= primeCounts.size ())
			throw new RuntimeException ("Factor list unavailable");
		while (n != 1)
		{
			factors = baseFactors [n];
			if ( singleFactored && factors > 1 ) return 0;
			count += multiplicity ? factors : 1;
			n = multiplier [n];
		}
		return count;
	}

	/**
	 * represent parity of value
	 * @param n value being considered
	 * @return 1 for even or -1 for odd
	 */
	public int parity (int n) { return n % 2 == 0 ? 1 : -1; }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.Factorization.Underlying#piFunction(int)
	 */
	public BigInteger piFunction (int n)
	{
		if (n >= primeCounts.size ())
			return piFunctionApproximation (n);
		return BigInteger.valueOf (this.primeCounts.get (n));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.Factorization.Underlying#piFunctionApproximation(int)
	 */
	public BigInteger piFunctionApproximation (int n)								// Error at n=10^9 (pi=50,847,534)
	{
//		common Approximation formulas being n/ln(n) and Li(n)
//		as long as the factors table LENGTH > 10^6 the choice is obvious
//		return BigInteger.valueOf ((long) Math.floor ((double) n / Math.log (n)));	//		2,592,592 =	5.1000%
		return BigInteger.valueOf ((long) ExponentialIntegral.Li (n));				//			1,701 =	0.0033%
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.Factorization.Underlying#OMEGA(int)
	 */
	public int OMEGA (int n) { return countPrimes (n, true, false); }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.Factorization.Underlying#omega(int)
	 */
	public int omega (int n) { return countPrimes (n, false, false); }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.Factorization.Underlying#lambda(int)
	 */
	public int lambda (int n) { return parity (OMEGA (n)); } // = (-1)^(OMEGA(n))

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.Factorization.Underlying#mobius(int)
	 */
	public int mobius (int n)
	{
		// delta(OMEGA[n],omega[n])*lamba(n) -- Kronecker delta [ p1==p2 ? 1 : 0 ]
		return n == 1 ? 1 : (n = countSingleFactoredPrimes (n)) == 0 ? 0 : parity (n);
	}
	public int countSingleFactoredPrimes (int n)
	{ return countPrimes (n, false, true); }


	/*************************************************************
	 * initialization of factorization algorithm data structures *
	 *************************************************************/

	/**
	 * provide a name for the prime selection algorithm
	 * @return the name of the algorithm that will be used to select prime range for evaluation
	 */
	protected String algotithmUsed () { return "TABLE SCAN"; }

	/**
	 * perform init and report timing and quantities
	 * @param sieve use specified prime generator
	 */
	public void initFactorizationsWithStats (SieveDriver sieve)
	{
		Date start = new Date ();
		System.out.println ("Factorization Table Computation Starting, initialization complete");
		sieve.generatePrimes ();

		Date finish = new Date ();
		System.out.println (base.length + " Factorizations Computed");
		System.out.println ("---");
		
		System.out.println (sieve.getName () + " used for prime generation");
		System.out.println ("Algorithm " + algotithmUsed () + " will be used for prime range selection");
		System.out.println ("Total primes recognized " + primes.size ());
		System.out.println ("---");

		long millis = finish.getTime() - start.getTime();
		System.out.println (millis + "ms");
		System.out.println ("---");
	}

	/**
	 * perform init and report timing and quantities.
	 * use THIS default prime generator
	 */
	public void initFactorizationsWithStats ()
	{
		initFactorizationsWithStats (this); // this class implements SieveDriver as a default
	}


}


/**
 * 
 * implementation of Table Scan algorithm for prime range selection
 * 
 * @author Michael Druckman
 *
 */
class PrimesTableScan
{


	/**
	 * using the primes list
	 * @param primes the list of primes available
	 * @param dumpSelected optional trace output request
	 */
	PrimesTableScan (List<BigInteger> primes, boolean dumpSelected)
	{
		this.primes = primes;
		highest = primes.size () - 1;
		DUMP_PRIME_FACTORIZATION = dumpSelected;
		lowest = 0;
	}


	boolean DUMP_PRIME_FACTORIZATION;
	List<BigInteger> primes;
	int highest, lowest;


	/**
	 * looking to narrow search to within this threshold
	 */
	private static final int PROXIMITY_THRESHOLD = 10;


	/**
	 * implement binary search
	 *  for prime closest to specified value
	 * @param upTo the value being sought
	 * @return index of closest prime
	 */
	public int runTableScan (BigInteger upTo)
	{
		BigInteger prime;
		int test = highest / 2;
		int dif, comparison;

		while (true)									// binary search of table finding prime closest (>=) to specified value
		{
			prime = primes.get (test);
			comparison = prime.compareTo (upTo);
			if (comparison == 0) highest = lowest = test;					// found exact value
			else if (comparison > 0) highest = test; else lowest = test;	// test is higher or lower

			if ((dif = highest - lowest) < PROXIMITY_THRESHOLD)				// resulting search points are within threshold
			{ test = highest; break; } else test = lowest + dif/2;			// break when close enough, otherwise set narrower range
		}

		if (DUMP_PRIME_FACTORIZATION) executeDump (upTo, test);

		return test;
	}


	/**
	 * show trace output of table scan results
	 * @param upTo the value that initiated the scan
	 * @param index the index in the primes table found
	 */
	private void executeDump (BigInteger upTo, int index)
	{
		System.out.println													// trace dump is output to system out
		(
			"{ upTo SQRT: " + upTo + "," + 
			" [low: " + lowest + " - hi: " + highest + "] => HI prime: " +
			primes.get (index) + " }"
		);
	}


	/**
	 * get approximation of SQRT of value for use as maximum prime in factorization
	 * @param value the value being factorized, maximum factor is half the scale of this value
	 * @param bitlength the estimation of the number of bits in the representation of the value
	 * @param dumpSelected optional trace output request
	 * @return approximation of SQRT by scale
	 */
	public static BigInteger halfScale
	(BigInteger value, int bitlength, boolean dumpSelected)
	{
		BigInteger sqrt = BigInteger.ONE.shiftLeft (bitlength);
		if (dumpSelected) FactorizationImplementation.dumpMaxFactor (sqrt, bitlength);
		return sqrt;
	}


	/**
	 * select the prime to check as potential factors of value specified
	 * @param value the value being factorized, maximum factor is half the scale of this value
	 * @param bitscale the estimation of the number of bits in the representation of the value
	 * @param primes the list of primes found in the prime generation processing
	 * @param tableSize the size of the computed factors table
	 * @param dump TRUE => trace option was requested
	 * @return the selected list of prime to use
	 */
	public static List<BigInteger> getPrimesForCheck
		(
			BigInteger value, int bitscale, List<BigInteger> primes,
			BigInteger tableSize, boolean dump
		)
	{
		// half scale is approximate sqrt of value.
		// any factor must be <= sqrt(value), otherwise other factor < 1
		BigInteger sqrt = halfScale (value, bitscale, dump);

		if (sqrt.compareTo (tableSize) < 0)												// table is big enough
		{
			PrimesTableScan scanner =
				new PrimesTableScan (primes, dump);										// create table scan object
			return primes.subList (0, scanner.runTableScan (sqrt));						// reduced list is computed and returned
		}
		else																			// entire table is within range 0..sqrt (of value)
		{
			if (dump)
			{ System.out.println ("{ *** FULL TABLE USED *** }"); }						// trace indicates that full prime table is used
			return primes;
		}
	}


}

