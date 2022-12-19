
package net.myorb.testing.factors;

import net.myorb.math.primenumbers.Factorization;

/**
 * unit test for new PrecisionManipulation class of primenumbers package
 * @author Michael Druckman
 */
public class PrecisionManipulation extends net.myorb.math.primenumbers.PrecisionManipulation
{

	PrecisionManipulation () { super (FactorizationCore.mgr); }

	/**
	 * entry point for running the test
	 * - simple version of stand-alone test sqrt(2)
	 * @param a not used
	 */
	public static void main (String [] a)
	{

		FactorizationCore.init (1_000_000);

		int scale = 350, precision = 2000; boolean trace = true;

		PrecisionManipulation pmgr = new PrecisionManipulation ();

		Factorization approx = new NewtonRaphsonIterativeTest (2).run (11);
		FactorizationCore.display (approx, AccuracyCheck.S2_REF, "SQRT", precision);

		Reduction red = pmgr.adjust (approx, scale, trace? System.out: null);
		Factorization adjustedValue = red.getAdjustedValue ();
		int matching = red.evaluate (precision);
		FactorizationCore.display
		(
			adjustedValue,
			AccuracyCheck.S2_REF,
			"ADJUSTED", precision
		);

		System.out.println ("matching = " + matching); System.out.println ();

		if (trace) pmgr.analyze (adjustedValue.getFactors ());

	}

}
