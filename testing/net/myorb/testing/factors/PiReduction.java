
package net.myorb.testing.factors;

import net.myorb.math.primenumbers.Factorization;
import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;

import java.math.BigInteger;

/**
 * evaluate precision of computed approximations
 * - apply multiple reduction steps to evaluate consequences
 * @author Michael Druckman
 */
public class PiReduction extends IterativeAlgorithmTests
{


	static ExpressionFactorizedFieldManager mgr;


	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		mgr = FactorizationCore.mgr;

		PiReduction testScripts = new PiReduction ();

		Factorization reduced = testScripts.initialPiComputation ();

		/*
			( 2^79 * 3^82 * 11^39 ) 
				/ ( 5 * 7^2 * 13 * 19 * 23^2 * 29 * 31 * 37 * 41 * 43
					* 5609768703045705497422081379517510785580096999255926942793420765718921200740996154762263 )
		 */

		mgr.setDisplayPrecision (200);

		reduced = testScripts.additionalReduction (reduced);

		/*
			( 2^74 * 11^39 )
				/ ( 3 * 5 * 7^3 * 13 * 19 * 23^3 * 29 * 31 * 37 * 41 * 43
					* 2728380639 3130309963 3412151031 6840881820 00093 )		45 digits
		 */

		reduced = testScripts.anotherReduction (reduced);

		/*
			( 2^35 * 11^39 )
				/ ( 3^3 * 5 * 7^4 * 13 * 19 * 23^3 * 29 * 31 * 37 * 41 * 43
		 			* 22073 * 105509 * 195493 * 17302644996492001 )
		 */

	}


	/**
	 * use Ramanujan to compute PI
	 *  and apply reduction on primes > 8 bits in length
	 * @return the resulting approximation
	 */
	Factorization initialPiComputation ()
	{
		SERIES_ITERATIONS = 10; ROOT_ITERATIONS = 6;

		computeSqrt ();		// prepare SQRT constants
		computePi ();		// use Ramanujan to compute PI

		// check SQRT and PI for precision of computed parameters
		display (sqrt_2, AccuracyCheck.S2_REF, "SQRT 2");
		display (pi, AccuracyCheck.PI_REF, "PI");

		Factorization reduced =
			mgr.getPrecisionManager ()
				.adjust (pi, 8, System.out).getAdjustedValue ();
		display (reduced, AccuracyCheck.PI_REF, "REDUCED");
		return reduced;
	}


	/**
	 * First Additional reduction
	 * @param starting the value from initialPiComputation
	 * @return the resulting approximation
	 */
	Factorization additionalReduction (Factorization starting)
	{
		Factorization reduced;
		BigInteger N = new BigInteger ("3").pow (82);
		BigInteger D = new BigInteger ("5609768703045705497422081379517510785580096999255926942793420765718921200740996154762263");
		display (reduced = mgr.getPrecisionManager ().reduceBy (N, D, starting, null), AccuracyCheck.PI_REF, "First Additional");
		return reduced;
	}


	/**
	 * Second Additional reduction
	 * @param starting the value from additionalReduction
	 * @return the resulting approximation
	 */
	Factorization anotherReduction (Factorization starting)
	{
		Factorization reduced;
		BigInteger N = new BigInteger ("2").pow (37);
		BigInteger D = new BigInteger ("272838063931303099633412151031684088182000093");
		display (reduced = mgr.getPrecisionManager ().reduceBy (N, D, starting, null), AccuracyCheck.PI_REF, "Second Additional");
		return reduced;
	}


}

