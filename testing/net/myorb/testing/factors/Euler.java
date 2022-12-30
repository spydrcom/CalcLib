
package net.myorb.testing.factors;

import net.myorb.math.computational.iterative.IterationTools;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.computational.iterative.Taylor;

/**
 * tests for numeric series used in Taylor tests
 * - tangent and secant Taylor series require Bernoulli and Euler series respectively
 * @author Michael Druckman
 */
public class Euler extends IterativeCompoundAlgorithmTests
{


	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		numbersTest ();

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
		double tan = taylor.run (30, IT.getTanDerivativeComputer (), Math.PI/12);
		// 0.26794919243112270647255365849413
		System.out.println (tan);
	}


	/**
	 * testing Euler numbers indirectly by running secant calculations
	 */
	static void secantTest ()
	{
		ExpressionFloatingFieldManager manager = new ExpressionFloatingFieldManager ();
		IterationTools <Double> IT = new IterationTools <Double> (); IT.setManager (manager);
		Taylor <Double> taylor = new Taylor <Double> (manager); taylor.enableTracing ();
		double sec = taylor.run (30, IT.getSecDerivativeComputer (), Math.PI/12);
		// 1.0352761804100830493955953504962
		System.out.println (sec);
	}


	/**
	 * run tests of Euler and Bernoulli series
	 */
	static void numbersTest ()
	{
		Euler testScripts = new Euler ();
		testScripts.IT.setManager (FactorizationCore.mgr);
		testScripts.runNumbers ();
	}


	/**
	 * run computation of Euler and Bernoulli series
	 */
	void runNumbers ()
	{
		E2n ();
		B2n ();
	}

	void E2n ()
	{
		System.out.println ("Euler");
		for (int n = 0; n < 20; n+=2)
			System.out.println 
			(
//					"E2nD  "+manager.toDecimalString (IT.combo.E2nDoubleSum (n))
//					"E2n   "+manager.toDecimalString (IT.combo.E2n (n))
					"En    "+manager.toDecimalString (IT.combo.En (n))
			);
	}
	/*
	 * E2n
	 *  0   2  4    6     8      10       12
		1, -1, 5, -61, 1385, -50521, 2702765, -199360981, 19391512145, -2404879675441, 
		370371188237525, -69348874393137901, 15514534163557086905, -4087072509293123892361, 
		1252259641403629865468285, -441543893249023104553682821, 
		177519391579539289436664789665
	 */

	void B2n ()
	{
		System.out.println ("Bernoulli");
		for (int n = 0; n < 20; n++)
			System.out.println 
			(
				manager.toDecimalString (IT.combo.firstKindBernoulli (n))
			);
	}
	/*
	 * B2n
	 *  0     1    2  2      4  5     6  7      8  9    10
	    1   1/2  1/6  0  -1/30  0  1/42  0  -1/30  0  5/66
	 */

}
