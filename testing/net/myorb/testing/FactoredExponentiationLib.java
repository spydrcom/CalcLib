
package net.myorb.testing;

import net.myorb.math.ExponentiationLib;
import net.myorb.math.ComputationConfiguration;
import net.myorb.math.primenumbers.sieves.*;
import net.myorb.math.primenumbers.*;
import net.myorb.math.SpaceManager;

/**
 * 
 * run unit tests on exponentiation library based on prime factorizations
 * 
 * @author Michael Druckman
 *
 */
public class FactoredExponentiationLib extends ExponentiationLib<Factorization>
{

	/**
	 * monitor object for display of test results
	 */
	static Monitor.Factored factoredMonitor = new Monitor.Factored ();

	/**
	 * high precision value of E for error computations
	 */
	static final Double E = 2.7182818284590452353602874713527;

	/**
	 * construct test object
	 * @param manager type manager for Factorization
	 */
	public FactoredExponentiationLib
	(SpaceManager<Factorization> manager)
	{ super (manager); }

	
	/**
	 * execute series of tests
	 * @param mgr nabager for Factorization type
	 */
	public void runTests (FactorizationFieldManager mgr)
	{
		factoredMonitor.activity ("2^3 = ", pow (manager.newScalar(2), 3));

		// mark trace options to be executed
		//lib.markOptionSelected (ComputationConfiguration.DUMP_ITERATIVE_TERM_VALUES);
		//support.markOptionSelected (ComputationConfiguration.DUMP_PRIME_FACTORIZATION);
		support.markOptionDeselected (ComputationConfiguration.DUMP_PRIME_FACTORIZATION);

		// tests on exp (x)
		factoredMonitor.activity ("e^(0) = ", exp (mgr.newScalar (0)), 1.0);
		factoredMonitor.activity ("e^(-1) = ", exp (FactorizationManager.onePrime().negate ()), 1/E);
		factoredMonitor.activity ("e^(1) = ", exp (FactorizationManager.onePrime ()), E);

		factoredMonitor.activity ("ln(0.5) = ", lnInRange (forValue (2).inverted ()).getUnderlying ());
		factoredMonitor.activity ("ln(10) = ", lnInvertAndNegate (mgr.newScalar (10)));
	}


	/**
	 * execute tests on factorized library
	 * @param args not used
	 */
	public static void main (String... args)
	{
//		Factorization.setImplementation
//			(support = new FactorizationImplementation (1000 * 1000));					// default implementation uses Table Scan
		Factorization.setImplementation
			(support = new FactorizationImplementationUsingBitlength (1000 * 1000));	// version of implementation that uses Bit Length SQRT
		//support.initFactorizationsWithStats (new SieveOfEratosthenes (support));		// using non-default sieve (SieveOfEratosthenes)
		support.initFactorizationsWithStats (new SieveOfSundaram (support));			// using non-default sieve (SieveOfSundaram)
		System.out.println ();

		FactorizationFieldManager mgr = new FactorizationFieldManager ();
		new FactoredExponentiationLib (mgr).runTests (mgr);
	}
	public static FactorizationImplementation support;


}
