
package net.myorb.testing;

import net.myorb.math.primenumbers.*;
import net.myorb.math.primenumbers.sieves.*;
import net.myorb.math.*;

import java.math.BigInteger;

/**
 * 
 * build large integers and attempt to use FactorizationManager to reduce large fractions
 * 
 * @author Michael Druckman
 *
 */
public class FactorizationManagerTest extends FactorizationManager
{

	/**
	 * execute tests on complex objects
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		Factorization.setImplementation
		//(support = new FactorizationImplementationUsingBitlength (1000 * 1000));	// version of implementation that uses Bit Length SQRT
		(support = new FactorizationImplementation (1000 * 1000));					// version of implementation that uses table scan
		support.initFactorizationsWithStats (new SieveOfSundaram (support));		// using non-default sieve (SieveOfSundaram)
		System.out.println ();

		//support.markOptionDeselected (ComputationConfiguration.DUMP_PRIME_FACTORIZATION);
		Factorization n, d;

		n = findFactorization (new BigInteger ("314159265358979323846264"));
		d = findFactorization (new BigInteger ("100000000000000000000000"));
		runTest (n, d);

		n = findFactorization (new BigInteger ("31415926535"));
		d = findFactorization (new BigInteger ("10000000000"));
		runTest (n, d);

		n = findFactorization (new BigInteger ("2718281828459"));
		d = findFactorization (new BigInteger ("1000000000000"));
		runTest (n, d);

		n = findFactorization (new BigInteger ("27182818284"));
		d = findFactorization (new BigInteger ("10000000000"));
		runTest (n, d);

	}
	public static FactorizationImplementation support;

	public static void runTest
		(
				Factorization n,
				Factorization d
		)
	{
		SpaceManager<Factorization> mgr =
			new FactorizationFieldManager ();
		Distribution ratio = new Distribution (mgr);
		ratio.set (n, d); Distribution.normalize (ratio);

		System.out.println (ratio);
		System.out.println (mgr.toDecimalString (Distribution.flattened (ratio)));
		System.out.println ();
		
		System.out.println (mgr.toJson (Distribution.flattened (ratio)));
		System.out.println (mgr.fromJson(mgr.toJson (Distribution.flattened (ratio))));
	}

}
