
package net.myorb.testing.factors;

import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.math.computational.iterative.IterationTools;
import net.myorb.math.computational.iterative.Taylor;

import net.myorb.math.computational.Combinatorics;

import net.myorb.math.SpaceManager;

/**
 * tests for numeric series used in Taylor tests
 * - tangent and secant Taylor series require Bernoulli and Euler series respectively
 * - Stirling second kind numbers used in some Euler number algorithms
 * - Euler first and second order added to complete the set
 * @author Michael Druckman
 */
public class Euler extends IterativeCompoundAlgorithmTests
{


	/**
	 * identify proper toString for data display
	 * @param <T> data type
	 */
	interface Formatter <T>
	{
		/**
		 * format the data item
		 * @param x the data item to display
		 * @return the data as text
		 */
		String format (T x);
	}


	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		factoredNumbersTest ();

		realNumbersTest ();

		secantTest ();

		tanTest ();

	}


	/**
	 * testing Bernoulli numbers indirectly by running tangent calculations
	 */
	static void tanTest ()
	{
		ExpressionFloatingFieldManager manager = new ExpressionFloatingFieldManager ();
		IterationTools <Double> IT = new IterationTools <Double> (); IT.setManager (manager);
		Taylor <Double> taylor = new Taylor <Double> (manager); taylor.enableTracing ();
		double  tan  =  taylor.run ( 18, IT.getTanDerivativeComputer (), Math.PI/12 );
		System.out.println (tan); System.out.println ("==="); System.out.println ();
		// 0.26794919243 112270647255365849413 precise to 11 digits w/ double

		int dif = AccuracyCheck.difAt
			(
				"0.26794919243112270647255365849413",
				Double.toString (tan)
			);
		System.out.println (dif + " places match");
		System.out.println ("===");
		System.out.println ();
	}


	/**
	 * testing Euler numbers indirectly by running secant calculations
	 */
	static void secantTest ()
	{
		ExpressionFloatingFieldManager manager = new ExpressionFloatingFieldManager ();
		IterationTools <Double> IT = new IterationTools <Double> (); IT.setManager (manager);
		Taylor <Double> taylor = new Taylor <Double> (manager); taylor.enableTracing ();
		double  sec  = taylor.run ( 20, IT.getSecDerivativeComputer (), Math.PI/12 );
		System.out.println (sec); System.out.println ("==="); System.out.println ();
		// 1.035276180410083 0493955953504962 precise to 15 digits w/ double

		int dif = AccuracyCheck.difAt
			(
				"1.0352761804100830493955953504962",
				Double.toString (sec)
			);
		System.out.println (dif + " places match");
		System.out.println ("===");
		System.out.println ();
	}


	/**
	 * run tests of Stirling, Euler and Bernoulli series
	 */
	static void factoredNumbersTest ()
	{
		Euler testScripts = new Euler ();
		ExpressionFactorizedFieldManager m;
		testScripts.IT.setManager (m = FactorizationCore.mgr);
		testScripts.runNumbers ( testScripts.IT.combo, x -> m.toDecimalString (x), m );
	}
	static void realNumbersTest ()
	{
		Euler testScripts = new Euler ();
		ExpressionFloatingFieldManager m = new ExpressionFloatingFieldManager ();
		Combinatorics <Double> combo = new Combinatorics <Double> (m, null);
		testScripts.runNumbers (combo, x -> x.toString (), m );
	}


	/**
	 * run computation of Euler and Bernoulli series
	 */
	<T> void runNumbers
		(
			Combinatorics <T> combo,
			Formatter <T> fmt, SpaceManager <T> m
		)
	{
		En  (0, combo, fmt, "Numbers");
		En  (1, combo, fmt, "(first order)");
		En  (2, combo, fmt, "(second order)");
		 S  (1, combo, fmt, m);
		 S  (2, combo, fmt, m);
		B2n (combo, fmt);
	}


	/*
	 * Euler
	 */

	<T> void En (int kind, Combinatorics <T> combo, Formatter <T> fmt, String title)
	{
		System.out.println ("Euler " + title);

		if (kind == 0)
		{
			for (int n = 0; n < 20; n+=2)
				System.out.println 
				(
//						"E2nD  " + fmt.format (combo.E2nDoubleSum (n))
//						"E2n   " + fmt.format (combo.E2n (n))
						"En    " + fmt.format (combo.En (n))
				);
		}
		else
		{
			double Enm = 0;

			for (int n = 1; n <= 10; n++)
			{
				System.out.print ("n = " + n);			
				System.out.print ("\t");

				for (int m = 0; m <= 10; m++)
				{
					if (kind == 1 && (Enm = Combinatorics.eulerNumbers (n, m)) == 0.0) break;
					else if (kind == 2 && (Enm = Combinatorics.eulerNumbersSecondOrder (n, m)) == 0.0) break;
					System.out.print ( (long) Enm ); System.out.print ("\t");
				}

				System.out.println ();
			}
		}

		System.out.println ("===");
		System.out.println ();
	}
	/*
	 * E2n
	 *  0   2  4    6     8      10       12							ERROR level seen using DOUBLE
	 *																	=============================
		1, -1, 5, -61, 1385, -50521, 2702765,
		-19936098_1, 1939151214_5, -24048796_75441,						OVERFLOW seen at iteration 14 using En (roundoff starts at 10)
		3703711_88237525, -69348_874393137901, 15514_534163557086905, 
		-408_7072509293123892361, 125_2259641403629865468285,			E2nD continues but roundoff proceeds marked as _
		-441543893249023104553682821, 177519391579539289436664789665
	 *
	 * E (first order)
	 * 
		n = 1	1	
		n = 2	1	1	
		n = 3	1	4	1	
		n = 4	1	11	11	1	
		n = 5	1	26	66	26	1	
		n = 6	1	57	302	302	57	1	
		n = 7	1	120	1191	2416	1191	120	1	
		n = 8	1	247	4293	15619	15619	4293	247	1	
		n = 9	1	502	14608	88234	156190	88234	14608	502	1	
		n = 10	1	1013	47840	455192	1310354	1310354	455192	47840	1013	1	
	 *
	 * E (second order)
	 * 
		n = 1	1	
		n = 2	1	2	
		n = 3	1	8	6	
		n = 4	1	22	58	24	
		n = 5	1	52	328	444	120	
		n = 6	1	114	1452	4400	3708	720	
		n = 7	1	240	5610	32120	58140	33984	5040	
		n = 8	1	494	19950	195800	644020	785304	341136	40320	
		n = 9	1	1004	67260	1062500	5765500	12440064	11026296	3733920	362880	
		n = 10	1	2026	218848	5326160	44765000	155357384	238904904	162186912	44339040	3628800	
	 *
	 */


	/*
	 * Bernoulli
	 */

	<T> void B2n (Combinatorics <T> combo, Formatter <T> fmt)
	{
		System.out.println ("Bernoulli");
		for (int n = 0; n < 20; n++)
			System.out.println 
			(
				fmt.format (combo.firstKindBernoulli (n))
			);
		System.out.println ("===");
		System.out.println ();
	}
	/*
	 * B2n
	 *  0     1    2  3      4  5     6  7      8  9    10
	    1   1/2  1/6  0  -1/30  0  1/42  0  -1/30  0  5/66
	 */


	/*
	 * Stirling
	 */

	<T> void S (int kind, Combinatorics <T> combo, Formatter <T> fmt, SpaceManager <T> m)
	{
		T Snk = null;
		System.out.println ("Stirling " + kind);
		for (int n = 1; n <= 10; n++)
		{
			System.out.print ("n = " + n); System.out.print ("\t");

			for (int k = 1; k <= 10; k++)
			{
				if (kind == 1 && m.isZero (Snk = combo.stirlingNumbers1 (n, k))) break;
				else if (kind == 2 && m.isZero (Snk = combo.stirlingNumbers2 (n, k))) break;
				System.out.print ( fmt.format (Snk) + "\t" );
			}

			System.out.println ();			
		}
		System.out.println ("===");
		System.out.println ();
	}
	/*
	 * S first
	 * 
		n = 1	1	
		n = 2	1	1	
		n = 3	2	3	1	
		n = 4	6	11	6	1	
		n = 5	24	50	35	10	1	
		n = 6	120	274	225	85	15	1	
		n = 7	720	1764	1624	735	175	21	1	
		n = 8	5040	13068	13132	6769	1960	322	28	1	
		n = 9	40320	109584	118124	67284	22449	4536	546	36	1	
		n = 10	362880	1026576	1172700	723680	269325	63273	9450	870	45	1	
	 *
	 * S second
	 * 
		n = 1	1	
		n = 2	1	1	
		n = 3	1	3	1	
		n = 4	1	7	6	1	
		n = 5	1	15	25	10	1	
		n = 6	1	31	90	65	15	1	
		n = 7	1	63	301	350	140	21	1	
		n = 8	1	127	966	1701	1050	266	28	1	
		n = 9	1	255	3025	7770	6951	2646	462	36	1	
		n = 10	1	511	9330	34105	42525	22827	5880	750	45	1	
	 *
	 */


}
