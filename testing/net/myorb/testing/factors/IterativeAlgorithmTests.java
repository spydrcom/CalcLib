
package net.myorb.testing.factors;

import net.myorb.math.primenumbers.Factorization;

/**
 * computations of PI and E as a test of the Factorization library
 * @author Michael Druckman
 */
public class IterativeAlgorithmTests extends FactorizationCore
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
	public static final int
		DISPLAY_PRECISION = 3200,
		COMPOSITE_EVALUATION_TABLE_SIZE = 1000 * 1000,
		TAYLOR_ITERATIONS = 20, SERIES_ITERATIONS = 10,
		ROOT_ITERATIONS = 7
	;


	/**
	 * a function that computes an approximation
	 */
	interface Computer
	{
		/**
		 * @return the computed value
		 */
		Factorization compute ();
	}


	/**
	 * intermediate results having computed
	 *   the Ramanujan series and SQRT 2
	 */
	static Factorization sqrt_2, sqrt_5, series, taylor;


	static void computePi ()
	{
		sqrt_2 = display ( () -> new NewtonRaphsonIterativeTest (2).run (ROOT_ITERATIONS) , "SQRT 2" );
		series = display ( () -> new RamanujanTest ().run (SERIES_ITERATIONS) , "Series" );
		pi = RamanujanTest.computePi (sqrt_2, series);
	}
	static Factorization pi;


	static void computePhi ()
	{
		sqrt_5 = display ( () -> new NewtonRaphsonIterativeTest (5).run (ROOT_ITERATIONS) , "SQRT 5" );
		phi = NewtonRaphsonIterativeTest.computePhi (sqrt_5);
	}
	static Factorization phi;


	static void computeE ()
	{
		taylor = display ( () -> new TaylorTest ().run (TAYLOR_ITERATIONS) , "Taylor" );
	}


	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		init (COMPOSITE_EVALUATION_TABLE_SIZE);

		computePi (); computePhi (); computeE ();

		display (phi, AccuracyCheck.PHI_REF, "PHI", DISPLAY_PRECISION);
		display (sqrt_2, AccuracyCheck.S2_REF, "SQRT 2", DISPLAY_PRECISION);
		display (taylor, AccuracyCheck.E_REF, "E", DISPLAY_PRECISION);
		display (pi, AccuracyCheck.PI_REF, "PI", DISPLAY_PRECISION);

	}


	/**
	 * @param computer the function evaluating the approximation
	 * @param tag a display name for the value
	 * @return the computed value
	 */
	static Factorization display
		(Computer computer, String tag)
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

