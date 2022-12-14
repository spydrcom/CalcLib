
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

}
