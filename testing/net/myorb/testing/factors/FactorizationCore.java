
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

	public static void init (int tableSize)
	{
		Factorization.setImplementation
		(support = new FactorizationImplementation (tableSize));					// version of implementation that uses table scan
		support.initFactorizationsWithStats (new SieveOfSundaram (support));		// using non-default sieve (SieveOfSundaram)
	}
	public static ExpressionFactorizedFieldManager mgr = new ExpressionFactorizedFieldManager ();
	public static FactorizationImplementation support;

	public static String toRatio (Factorization x) { return mgr.toPrimeFactors (x); }


	/**
	 * @param approx the Factorization approximation
	 * @param REF the text of the reference value
	 * @param tag a display name for the value
	 * @param precision digits assumed
	 */
	static void display
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


}
