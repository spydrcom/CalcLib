
package net.myorb.testing;

import net.myorb.math.primenumbers.Factorization;

import net.myorb.math.computational.iterative.NewtonRaphson;

/**
 * compute SQRT 2 using the Newton-Raphson method
 * - this is done using the method to solve for roots of x^2 - 2 = 0
 * @author Michael Druckman
 */
public class NewtonRaphsonIterativeTest extends NewtonRaphson <Factorization>
{

	NewtonRaphsonIterativeTest ()
	{
		super (FactorizationCore.mgr);
	}

	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{
		FactorizationCore.init (1000*1000);
		System.out.println (new NewtonRaphsonIterativeTest ().run (10));
	}

	/**
	 * describe polynomial with initial coefficient values.
	 * - the initial value of X is also set
	 */
	void initializeFunction ()
	{
		establishFunction (G.coefficients (new int [] {-2, 0, 1}));
		setApproximationOfX (manager.newScalar (1));
	}

	/**
	 * @param iterations number of iterations to run
	 * @return the computed result after specified iterations
	 */
	public Factorization run (int iterations)
	{
		initializeFunction ();
		for (int i=1; i<=iterations; i++) { iterate (); }
		return getX ();
	}

	/**
	 * apply next iteration to computation
	 */
	void iterate ()
	{
		applyIteration ();
		testVal = manager.pow (getX (), 2);
		System.out.println (this);
		System.out.println ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.iterative.IterationFoundations#add(java.lang.StringBuffer)
	 */
	public void add (StringBuffer buffer) { buffer.append ("X^n = ").append (toString (testVal)).append ("\n"); }
	Factorization testVal;

}
