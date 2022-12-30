
package net.myorb.testing.factors;

import net.myorb.math.primenumbers.Factorization;
import net.myorb.testing.factors.FactorizationCore.Computer;

/**
 * computations of PI and E as a test of the Factorization library
 * - additional tests cover the range of Taylor series tests found in the tools class
 * - Ramanujan, Newton, and Taylor supply the algorithms for the tested iterative formulas
 * @author Michael Druckman
 */
public class IterativeAlgorithmTests extends TaylorTest
{


	/*
	 *		Iterations					Precision
	 *		 ROOT	SERIES	TAYLOR		 ROOT	SERIES	TAYLOR
	 *		==================================================
	 *		   5	  10	  30	|	   25	   88	   35
	 *		   6	  20	  50	|	   49	  169	   67
	 *		   7	  30	  70	|	   98	  249	  103
	 *		   8	  50	  90	|	  197	  408	  140
	 *		   9	 100	 100	|	  393	  807	  161
	 *		  10	 115	 115	|	  784	  927	  192
	 *		  11	 130	 130	|	 1569	 1047	  223
	 *		  12	 150	 160	|	 3136	 1207	  288
	 *		  13	 185	 200	|	>3500	 1486	  379
	 *
	 */
	public static int
		DISPLAY_PRECISION = 2000,
		COMPOSITE_EVALUATION_TABLE_SIZE = 1000 * 1000,
		TAYLOR_ITERATIONS = 70, SERIES_ITERATIONS = 30,
		ROOT_ITERATIONS = 7, COMPUTATION_PRECISION = 30
	;


	/*
	 * import display functionality
	 */

	public static void displayError
	(Factorization val, int square, String tag)
	{ FactorizationCore.displayError (val, square, tag); }
	public static void timeStamp () { FactorizationCore.timeStamp (); }
	public static void display (Factorization approx, String REF, String tag)
	{ FactorizationCore.display (approx, REF, tag, DISPLAY_PRECISION); }
	public static Factorization display (Computer computer, String tag)
	{ return FactorizationCore.display (computer, tag); }


	/*
	 * computation test scripts
	 */


	/**
	 * computation of square-roots
	 */
	void computeSqrt ()
	{
		NR = new NewtonRaphsonIterativeTest ();
		NR.establishParameters (COMPUTATION_PRECISION);
		sqrt_2 = display ( () -> NR.establishFunction (2).run (ROOT_ITERATIONS) , "SQRT 2" );
		sqrt_3 = display ( () -> NR.establishFunction (3).run (ROOT_ITERATIONS) , "SQRT 3" );
		sqrt_5 = display ( () -> NR.establishFunction (5).run (ROOT_ITERATIONS) , "SQRT 5" );
		timeStamp ();
	}
	static Factorization sqrt_2, sqrt_3, sqrt_5; // SQRT computed using Newton-Raphson method
	static NewtonRaphsonIterativeTest NR;


	/**
	 * computation of phi
	 */
	static void computePhi ()
	{
		phi = display ( () -> NR.computePhi (sqrt_5) , "PHI" );
	}
	static Factorization phi; // phi computed from ( 1 + SQRT 5 ) / 2


	/**
	 * computation of e
	 */
	void computeEuler ()
	{
		e = display ( () -> computeEuler (TAYLOR_ITERATIONS) , "Euler" );
	}
	static Factorization e; // e as computed using the Taylor expansion of exp 1


	/**
	 * computation of pi
	 */
	void computePi ()
	{
		RamanujanTest RT = new RamanujanTest ();
		Factorization series = display ( () -> RT.run (SERIES_ITERATIONS) , "Series" );
		pi = RT.computePi (sqrt_2, series);
	}
	static Factorization pi; // pi as computed using the Ramanujan series


	/**
	 * computation of arc-tangent
	 */
	void computeAtan ()
	{
		atan6 = display ( () -> computeAtan (TAYLOR_ITERATIONS, tanPi6 (sqrt_3), 6) , "ATAN" );
		atan12 = display ( () -> computeAtan (TAYLOR_ITERATIONS, tanPi12 (sqrt_3), 12) , "ATAN" );
	}
	static Factorization atan6, atan12; // arc tan expecting pi/6 and pi/12 respectively


	/**
	 * computation of Inverse Tangent Integral
	 */
	void computeITI ()
	{
		ITI = display ( () -> runInvTanTest (TAYLOR_ITERATIONS, sqrt_3) , "TI2" );
	}
	static Factorization ITI; // Inverse Tangent Integral is this Taylor expansion


	/**
	 * run through the computation scripts
	 */
	void computeValues ()
	{
		computeSqrt (); computePi (); computePhi (); computeEuler (); computeAtan (); computeITI ();
	}


	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		IterativeAlgorithmTests test = new IterativeAlgorithmTests ();
		test.establishParameters (FactorizationCore.mgr, COMPUTATION_PRECISION);
		test.computeValues ();

		System.out.println ();
		System.out.println ("+-+-+-+-+-+-+-+-+");
		System.out.println ();

		showSqrtErrors ();

		display (phi, AccuracyCheck.PHI_REF, "PHI");
		display (sqrt_2, AccuracyCheck.S2_REF, "SQRT 2");
		display (atan12, AccuracyCheck.PI_REF, "ATAN12");
		display (atan6, AccuracyCheck.PI_REF, "ATAN6");
		display (ITI, AccuracyCheck.Ti2_REF, "Ti2");
		display (pi, AccuracyCheck.PI_REF, "PI");
		display (e, AccuracyCheck.E_REF, "E");

	}


	/**
	 * format table of computed errors on SQRT calculations
	 */
	public static void showSqrtErrors ()
	{
		System.out.println ();
		System.out.println ("SQRT errors");
		System.out.println ();

		displayError (sqrt_2, 2, "SQRT 2");
		displayError (sqrt_3, 3, "SQRT 3");
		displayError (sqrt_5, 5, "SQRT 5");

		System.out.println ("===");
		System.out.println ();
	}


	/*
	 * prepare the static composite lookup table
	 */

	static { FactorizationCore.init (COMPOSITE_EVALUATION_TABLE_SIZE); }


}

