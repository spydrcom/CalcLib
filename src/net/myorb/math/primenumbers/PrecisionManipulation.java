
package net.myorb.math.primenumbers;

import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;

import java.math.BigInteger;

/**
 * apply precision reduction
 *  by truncation of the ratio of largest prime factors
 * @author Michael Druckman
 */
public class PrecisionManipulation
{


	/**
	 * a description of a reduction including artifacts of the process
	 */
	public interface Reduction
	{

		/**
		 * @return quotient and remainder of adjustment factors
		 */
		BigInteger [] getDivRem ();

		/**
		 * @return the value that replaced the adjustment factors
		 */
		Factorization getReducedFactor ();

		/**
		 * @return numerator and denominator of adjustment factors
		 */
		BigInteger [] getReductionFactors ();

		/**
		 * @return the adjusted value after the reduction
		 */
		Factorization getAdjustedValue ();

		/**
		 * compare adjusted value with source
		 * @param outTo maximum digits to compare
		 * @return the number of matching digits
		 */
		public int evaluate (int outTo);

	}


	public PrecisionManipulation
	(ExpressionFactorizedFieldManager mgr) { this.mgr = mgr; }
	protected ExpressionFactorizedFieldManager mgr;


	/**
	 * provide a sysout display of a factor collection
	 * @param factors the factor collection that makes a Factorization
	 * @return the list of primes in the collection
	 */
	public static BigInteger [] analyze (FactorCollection factors)
	{
		return sortedPrimes (factors, System.out);
	}


	/**
	 * sort the primes in a collection
	 * @param factors the factor collection that makes a Factorization
	 * @param trace a PrintStream to format an analysis for
	 * @return the list of primes in the collection
	 */
	public static BigInteger [] sortedPrimes
		(FactorCollection factors, java.io.PrintStream trace)
	{
		BigInteger [] primes =
			factors.getPrimes ().toArray (ARRAY_MODEL);
		java.util.Arrays.sort (primes, 0, primes.length);
		if (trace != null) ReductionImpl.dump (factors, primes, trace);
		return primes;
	}
	public static final BigInteger ARRAY_MODEL [] = new BigInteger [] {};


	/**
	 * apply precision reduction to a Factorization
	 * @param source the Factorization to apply adjustment to
	 * @param scale the bit size of primes that are to be adjusted
	 * @param trace a PrintStream to format an analysis for
	 * @return a description of the reduction
	 */
	public Reduction adjust (Factorization source, int scale, java.io.PrintStream trace)
	{
		FactorCollection collection = source.getFactors ();
		ReductionImpl reduction = new ReductionImpl (source, mgr);

		for (BigInteger p : collection.getPrimes ())
		{
			if (p.bitLength () > scale)
			{ reduction.adjust (p); }
		}

		reduction.completeAdjustment ();
		if (trace != null) reduction.display (trace);
		return reduction;
	}


	/**
	 * verify value accuracy against reference
	 * @param reference the reference to be used
	 * @param comparedWith a string to match with reference digits
	 * @return the position where match fails checking character by character
	 */
	public static int evaluate (String reference, String comparedWith)
	{
		int most = Math.min (comparedWith.length (), reference.length ());
		for (int i=0; i<most;i++) { if (reference.charAt(i) != comparedWith.charAt(i)) return i; }
		return reference.length ();
	}


}


/**
 * collect factor identified as part of the reduction
 */
class ReductionImpl implements PrecisionManipulation.Reduction
{


	public ReductionImpl
		(
			Factorization source,
			ExpressionFactorizedFieldManager mgr
		)
	{
		this.factors = (this.adjusted = source)
			.getFactors ().getFactorMap ();
		this.source = source;
		this.mgr = mgr;
	}
	protected java.util.Map <BigInteger, Integer> factors;
	protected ExpressionFactorizedFieldManager mgr;
	protected Factorization source;


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
	public Factorization completeAdjustment ()
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
	 * display quotient and remainder used in this evaluation
	 * - additional artifacts of the computations also included
	 * - the amount of precision carried forward is shown
	 * @param trace the print stream for the display
	 */
	public void display (java.io.PrintStream trace)
	{
		trace.println ();

		// the numerator and denominator of the adjustment
		trace.print ("N = "); trace.print (N); trace.println ();
		trace.print ("D = "); trace.print (D); trace.println ();

		// the remainder and the quotient from the division
		trace.print ("rem = "); trace.print (divRem [1]); trace.println ();
		trace.print ("fudge = "); trace.print (fudgeFactor); trace.println ();

		int matching = evaluate (1000);
		// the number of decimal places where the adjusted value matches the source
		trace.print ("precision = "); trace.print (matching);
		trace.print (" (digits matching source)");
		trace.println ();

		trace.println ();
	}


	/**
	 * provide a dump of the elements of a Factorization
	 * @param factors the collection of factors in the representation
	 * @param primes the prime number found in the representation
	 * @param trace the print stream to be written to
	 */
	public static void dump
	(FactorCollection factors, BigInteger [] primes, java.io.PrintStream trace)
	{
		for (BigInteger prime : primes)
		{
			trace.print (prime); trace.print ("\t"); 
			trace.print (factors.getFactorMap ().get (prime)); trace.print ("\t"); 
			trace.print (prime.bitLength ()); trace.println ();
		}
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.PrecisionManipulation.Reduction#getDivRem()
	 */
	public BigInteger [] getDivRem () { return divRem; }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.PrecisionManipulation.Reduction#getReducedFactor()
	 */
	public Factorization getReducedFactor () { return fudgeFactor; }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.PrecisionManipulation.Reduction#getReductionFactors()
	 */
	public BigInteger [] getReductionFactors () { return new BigInteger [] {N, D}; }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.PrecisionManipulation.Reduction#getAdjustedValue()
	 */
	public Factorization getAdjustedValue () { return adjusted; }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.PrecisionManipulation.Reduction#evaluate(int)
	 */
	public int evaluate (int outTo)
	{
		int p = mgr.pushDisplayPrecision (outTo);
		String test = mgr.toDecimalString (adjusted),
				reference = mgr.toDecimalString (source);
		int match = PrecisionManipulation.evaluate (reference, test);
		mgr.setDisplayPrecision (p);
		return match;
	}


}

