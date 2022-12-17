
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
		mgr.setDisplayPrecision (precision);
		System.out.println (tag); System.out.println ();
		System.out.println (APX = mgr.toDecimalString (approx));
		System.out.println (toRatio (approx));
		System.out.println ();

		System.out.print ("DIF AT = ");
		System.out.println (AccuracyCheck.difAt (REF, APX));
		System.out.println ();

	}


	/**
	 * convert to fraction display
	 * @param x the value to be displayed
	 * @return the text of the display
	 */
	public static String toRatio (Factorization x) { return mgr.toPrimeFactors (x); }


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
		System.out.println ();

		return value;

	}


}

