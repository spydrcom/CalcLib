
package net.myorb.math.primenumbers;

import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;

import java.math.BigInteger;

/**
 * apply precision reduction by truncation of the ration of largest prime factors
 * @author Michael Druckman
 */
public class PrecisionManipulation
{


	public PrecisionManipulation
	(ExpressionFactorizedFieldManager mgr) { this.mgr = mgr; }
	protected ExpressionFactorizedFieldManager mgr;


	/**
	 * provide a sysout display of a factor collection
	 * @param factors the factor collection that makes a Factorization
	 * @return the list of primes in the collection
	 */
	public BigInteger [] analyze (FactorCollection factors)
	{
		return sortedPrimes (factors, System.out);
	}


	/**
	 * sort the primes in a collection
	 * @param factors the factor collection that makes a Factorization
	 * @param trace a PrintStream to format an analysis for
	 * @return the list of primes in the collection
	 */
	public BigInteger [] sortedPrimes (FactorCollection factors, java.io.PrintStream trace)
	{
		BigInteger [] primes =
			factors.getFactorMap ().keySet ().toArray (new BigInteger [] {});
		java.util.Arrays.sort (primes, 0, primes.length);

		if (trace != null)
		{
			for (BigInteger prime : primes)
			{
				trace.print (prime); trace.print ("\t"); 
				trace.print (factors.getFactorMap ().get (prime)); trace.print ("\t"); 
				trace.print (prime.bitLength ()); trace.println ();
			}
		}

		return primes;
	}


	/**
	 * apply precision reduction to a Factorization
	 * @param source the Factorization to apply adjustment to
	 * @param scale the bit size of primes that are to be adjusted
	 * @param trace a PrintStream to format an analysis for
	 * @return the adjusted Factorization
	 */
	public Factorization adjust (Factorization source, int scale, java.io.PrintStream trace)
	{
		FactorCollection collection = source.getFactors ();
		Reduction reduction = new Reduction (source, mgr);

		for (BigInteger p : collection.getPrimes ())
		{
			if (p.bitLength () > scale)
			{ reduction.adjust (p); }
		}

		Factorization adjusted = reduction.completedAdjustment ();
		if (trace != null) reduction.display (trace);
		return adjusted;
	}


}


/**
 * collect factor identified as part of the reduction
 */
class Reduction
{

	public Reduction (Factorization source, ExpressionFactorizedFieldManager mgr)
	{
		this.factors = (this.adjusted = source)
			.getFactors ().getFactorMap ();
		this.mgr = mgr;
	}
	protected java.util.Map <BigInteger, Integer> factors;
	protected ExpressionFactorizedFieldManager mgr;


	/**
	 * add a large prime to the collection of values to include in the fudge
	 * @param p a prime from the factors found to be larger than the filter
	 */
	public void adjust (BigInteger p)
	{
		if ((exp = factors.get (p)) > 0)
		{ N = N.multiply (factor = p.pow (exp)); }
		else { D = D.multiply (factor = p.pow (-exp)); }

		// the factor must be removed from the original value factor list
		adjusted = mgr.multiply (adjusted, mgr.pow (mgr.bigScalar (factor), -exp));
	}
	protected BigInteger N = BigInteger.ONE, D = BigInteger.ONE, factor;
	protected Factorization adjusted; int exp;


	/**
	 * compute the fudge factor
	 * - large bit length primes have been collected from the source
	 * - the fudge factor is the truncated ratio of these factors
	 * @return the adjusted Factorization
	 */
	public Factorization completedAdjustment ()
	{
		if (N.compareTo (D) > 0)
		// numerator is the larger value
		{ divRem = N.divideAndRemainder (D); exp = 1; }
		// denominator is larger so the ratio must be inverted
		else { divRem = D.divideAndRemainder (N); exp = -1; }

		// truncate the division of the factors and invert if necessary
		fudgeFactor = mgr.pow ( mgr.bigScalar ( divRem [0] ), exp );
		// the fudge is placed back in the value factors list
		adjusted = mgr.multiply (adjusted, fudgeFactor);

		return adjusted;
	}
	protected Factorization fudgeFactor;
	protected BigInteger divRem [];


	/**
	 * display remainder and fudge factor found in this evaluation
	 * @param trace the print stream for the display
	 */
	public void display (java.io.PrintStream trace)
	{
		trace.println ();
		trace.print ("rem = "); trace.print (divRem [1]); trace.println ();
		trace.print ("fudge = "); trace.print (fudgeFactor); trace.println ();
		trace.println ();
	}


}

