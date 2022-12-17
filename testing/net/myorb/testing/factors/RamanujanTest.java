
package net.myorb.testing.factors;

import net.myorb.math.computational.iterative.Ramanujan;
import net.myorb.math.primenumbers.Factorization;

/**
 * evaluation of the Ramanujan series for computation of PI
 * - this is the entry point and driver for specification of the iteration count
 * @author Michael Druckman
 */
public class RamanujanTest extends Ramanujan <Factorization>
{


	RamanujanTest ()
	{
		super (FactorizationCore.mgr);
	}


	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{
		FactorizationCore.init (1_000_000);
		Factorization approx = new RamanujanTest ().run (15);
		System.out.println (FactorizationCore.toRatio (approx));
	}


	/**
	 * @param iterations number of iterations to run
	 * @return the computed sum after specified iterations
	 */
	public Factorization run (int iterations)
	{
		initializeSummation ();
		for (int i = 1; i <= iterations; i++)
		{ applyIteration (); }
		return summation;
	}


	/**
	 * compute PI with SQRT 2 and the Ramanujan series
	 * @param radical2 the computed value of SQRT 2 with adequate precision
	 * @param series the computed value of the Ramanujan series with adequate precision
	 * @return the computed approximation of PI
	 */
	public Factorization computePi (Factorization radical2, Factorization series)
	{
		Factorization
			X = IT.S (2), C9801 = IT.S (9801);
		X = IT.productOf (X, IT.oneOver (C9801));

		X = IT.productOf (X, radical2);
		X = IT.productOf (X, series);

		return IT.oneOver (X);
	}
	// 1 / pi = ( 2 * sqrt(2) / 9801 ) * SIGMA [0 <= k <= INFINITY] ( (4*k)! * (1103 + 26390*k) / ((k!)^4 * 396 ^ (4*k)) )


}

