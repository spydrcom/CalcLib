
package net.myorb.testing.factors;

import net.myorb.math.primenumbers.PrecisionManipulation;
import net.myorb.math.primenumbers.Factorization;

/**
 * unit test for new PrecisionManipulation class of prime-numbers package
 * @author Michael Druckman
 */
public class PrecisionManipulationTest extends PrecisionManipulation
{


	static int scale = 350, precision = 2000; static boolean trace = true;


	PrecisionManipulationTest () { super (FactorizationCore.mgr); }


	/**
	 * entry point for running the test
	 * - simple version of stand-alone test sqrt(2)
	 * @param a not used
	 */
	public static void main (String [] a)
	{

		FactorizationCore.init (1_000_000);

		// SQRT 2 computed with Newton-Raphson ( 11 iterations )
		// - producing 1569 digits of precision shown against reference

		Factorization approx =
			new NewtonRaphsonIterativeTest (2).run (11);
		show (approx, "SQRT");

		// prepare to run PrecisionManipulation reduction process

		System.out.println ();
		System.out.println ("Trace PrecisionManipulation processing");

		// compute reduced adjustment of original approximation
		PrecisionManipulationTest pmgr = new PrecisionManipulationTest ();
		Reduction red = pmgr.adjust (approx, scale, trace? System.out: null);
		Factorization adjustedValue = red.getAdjustedValue ();
		// show adjusted value statistics for comparison
		show (adjustedValue, "ADJUSTED");

	}


	/**
	 * show analysis of value
	 * @param value the value to analyze
	 * @param tag a banner to show context
	 */
	static void show (Factorization value, String tag)
	{

		FactorizationCore.display
		(
			value,
			AccuracyCheck.S2_REF,
			tag, precision
		);

		if (trace) analyze (value.getFactors ());

	}


}

