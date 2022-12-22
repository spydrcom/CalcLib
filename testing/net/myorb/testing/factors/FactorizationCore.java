
package net.myorb.testing.factors;

import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;

import net.myorb.math.primenumbers.FactorizationImplementation;
import net.myorb.math.primenumbers.sieves.SieveOfSundaram;
import net.myorb.math.primenumbers.Factorization;

/**
 * allocation and initialization of the prime and composite lookup tables
 * @author Michael Druckman
 */
public class FactorizationCore
{


	/**
	 * initialize a table of composites for use in factorizations
	 * @param tableSize the number of composites to compute
	 */
	public static void init (int tableSize)
	{
		Factorization.setImplementation
		(support = new FactorizationImplementation (tableSize));					// version of implementation that uses table scan
		support.initFactorizationsWithStats (new SieveOfSundaram (support));		// using non-default sieve (SieveOfSundaram)
	}
	public static ExpressionFactorizedFieldManager mgr = new ExpressionFactorizedFieldManager ();
	public static FactorizationImplementation support;


	/**
	 * provide data describing the precision of samples
	 * @param approx the Factorization approximation
	 * @param REF the text of the reference value
	 * @param tag a display name for the value
	 * @param precision digits assumed
	 */
	public static void display
		(Factorization approx, String REF, String tag, int precision)
	{

		String APX;

		System.out.println ();
		int p = mgr.pushDisplayPrecision (precision);
		System.out.println (tag); System.out.println ();
		System.out.println (APX = mgr.toDecimalString (approx));
		System.out.println (toRatio (approx));
		System.out.println ();

		System.out.print ("DIF AT = ");
		System.out.println (AccuracyCheck.difAt (REF, APX));

		mgr.setDisplayPrecision (p);
		System.out.println ("===");
		System.out.println ();

	}


	/**
	 * calculate errors of SQRT approximations
	 * @param approx the value computed as approximation of a SQRT
	 * @param square the correct value of the square of the approximation
	 * @param tag a display name for context
	 */
	public static void displayError
	(Factorization approx, int square, String tag)
	{
		Factorization approxSQ = mgr.pow (approx, 2);
		Factorization error = mgr.add (approxSQ, mgr.negate (mgr.newScalar (square)));
		System.out.print (tag + " error = "); System.out.println (mgr.toDecimalString (error));
		System.out.println ();
	}


	/**
	 * convert to fraction display
	 * @param x the value to be displayed
	 * @return the text of the display
	 */
	public static String toRatio (Factorization x) { return mgr.toPrimeFactors (x); }


	/**
	 * format as decimal text
	 * @param value the value to be displayed
	 * @param precision the number of digits for the display
	 * @return the text as formatted
	 */
	public static String toDecimalString (Factorization value, int precision)
	{
		// format results for display
		int p = mgr.pushDisplayPrecision (precision);
		String rep =  mgr.toDecimalString (value);
		mgr.setDisplayPrecision (p);
		return rep;
	}


	/**
	 * a function that computes an approximation
	 */
	public interface Computer
	{
		/**
		 * @return the computed value
		 */
		Factorization compute ();
	}


	/**
	 * wrap computation in display tags
	 * @param computer the function evaluating the approximation
	 * @param tag a display name for the value
	 * @return the computed value
	 */
	public static Factorization display (Computer computer, String tag)
	{

		Factorization value;

		System.out.println ();
		System.out.println (tag);
		System.out.println ();

		value = computer.compute ();

		System.out.print (tag + " = ");
		System.out.println (value);
		timeStamp ();

		return value;

	}


	/**
	 * show elapsed time
	 */
	public static void timeStamp ()
	{
		System.out.println ();
		System.out.println ("---");
		long elapsed = System.currentTimeMillis () - start;
		System.out.println (elapsed + "ms");
		System.out.println ("---");
	}
	static { start = System.currentTimeMillis (); }
	static long start;

}
