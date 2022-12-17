
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
		DISPLAY_PRECISION = 1000,
		COMPOSITE_EVALUATION_TABLE_SIZE = 1000 * 1000,
		TAYLOR_ITERATIONS = 70, SERIES_ITERATIONS = 10,
		ROOT_ITERATIONS = 6
	;


	/**
	 * intermediate results having computed
	 *   the Ramanujan series and SQRT 2
	 */
	static Factorization sqrt_2, sqrt_5, series;


	static void computePi ()
	{
		RamanujanTest RT = new RamanujanTest ();
		series = display ( () -> RT.run (SERIES_ITERATIONS) , "Series" );
		NewtonRaphsonIterativeTest NR = new NewtonRaphsonIterativeTest (2);
		sqrt_2 = display ( () -> NR.run (ROOT_ITERATIONS) , "SQRT 2" );
		pi = RT.computePi (sqrt_2, series);
	}
	static Factorization pi;


	static void computePhi ()
	{
		NewtonRaphsonIterativeTest NR = new NewtonRaphsonIterativeTest (5);
		sqrt_5 = display ( () -> NR.run (ROOT_ITERATIONS) , "SQRT 5" );
		phi = NR.computePhi (sqrt_5);
	}
	static Factorization phi;


	static void computeAtan ()
	{
		TaylorTest TT = new TaylorTest ();
		NewtonRaphsonIterativeTest NR = new NewtonRaphsonIterativeTest (3);
		Factorization sqrt_3 = display ( () -> NR.run (ROOT_ITERATIONS) , "SQRT 3" );
		//  atan = display ( () -> TT.computeAtan (TAYLOR_ITERATIONS, TT.atan6 (sqrt_3), 6) , "ATAN" );
		atan = display ( () -> TT.computeAtan (TAYLOR_ITERATIONS, TT.atan12 (sqrt_3), 12) , "ATAN" );
	}
	static Factorization atan;


	static void computeEuler ()
	{
		TaylorTest TT = new TaylorTest ();
		e = display ( () -> TT.computeEuler (TAYLOR_ITERATIONS) , "Euler" );
	}
	static Factorization e;


	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		init (COMPOSITE_EVALUATION_TABLE_SIZE);

		computePi (); computePhi (); computeEuler (); computeAtan ();

		display (phi, AccuracyCheck.PHI_REF, "PHI", DISPLAY_PRECISION);
		display (sqrt_2, AccuracyCheck.S2_REF, "SQRT 2", DISPLAY_PRECISION);
		display (atan, AccuracyCheck.PI_REF, "ATAN", DISPLAY_PRECISION);
		display (pi, AccuracyCheck.PI_REF, "PI", DISPLAY_PRECISION);
		display (e, AccuracyCheck.E_REF, "E", DISPLAY_PRECISION);

	}


}

