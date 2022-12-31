
package net.myorb.testing.factors;

import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.math.computational.iterative.IterationTools;
import net.myorb.math.computational.iterative.Taylor;

import net.myorb.math.computational.Combinatorics;

/**
 * tests for numeric series used in Taylor tests
 * - tangent and secant Taylor series require Bernoulli and Euler series respectively
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

		numbersTest ();

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
	 * run tests of Euler and Bernoulli series
	 */
	static void numbersTest ()
	{
		Euler testScripts = new Euler ();
		ExpressionFactorizedFieldManager m;
		testScripts.IT.setManager (m = FactorizationCore.mgr);
		testScripts.runNumbers (testScripts.IT.combo, x -> m.toDecimalString (x));
	}
	static void realNumbersTest ()
	{
		Euler testScripts = new Euler ();
		ExpressionFloatingFieldManager m = new ExpressionFloatingFieldManager ();
		Combinatorics <Double> combo = new Combinatorics <Double> (m, null);
		testScripts.runNumbers (combo, x -> x.toString () );
	}


	/**
	 * run computation of Euler and Bernoulli series
	 */
	<T> void runNumbers (Combinatorics <T> combo, Formatter <T> fmt)
	{
		E2n (combo, fmt);
		B2n (combo, fmt);
	}

	<T> void E2n (Combinatorics <T> combo, Formatter <T> fmt)
	{
		System.out.println ("Euler");
		for (int n = 0; n < 20; n+=2)
			System.out.println 
			(
//					"E2nD  " + fmt.format (combo.E2nDoubleSum (n))
//					"E2n   " + fmt.format (combo.E2n (n))
					"En    " + fmt.format (combo.En (n))
			);
		System.out.println ("===");
		System.out.println ();
	}
	/*
	 * E2n
	 *  0   2  4    6     8      10       12
		1, -1, 5, -61, 1385, -50521, 2702765, -199360981, 19391512145, -2404879675441, 
		370371188237525, -69348874393137901, 15514534163557086905, -4087072509293123892361, 
		1252259641403629865468285, -441543893249023104553682821, 
		177519391579539289436664789665
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
	 *  0     1    2  2      4  5     6  7      8  9    10
	    1   1/2  1/6  0  -1/30  0  1/42  0  -1/30  0  5/66
	 */

}
