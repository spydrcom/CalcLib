
package net.myorb.testing.factors;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.computational.iterative.IterationTools;
import net.myorb.math.computational.iterative.NewtonRaphson;

import net.myorb.math.primenumbers.Factorization;

/**
 * compute SQRT n using the Newton-Raphson method
 * - this is done using the method to solve for roots of x^2 - n = 0
 * @author Michael Druckman
 */
public class NewtonRaphsonIterativeTest extends NewtonRaphson <Factorization>
{


	static boolean tracing = false;


	NewtonRaphsonIterativeTest ()
	{
		super (FactorizationCore.mgr);
		this.IT = new IterationTools <Factorization> (manager);
	}

	NewtonRaphsonIterativeTest (int n)
	{  this ();  establishFunction (n);  }
	protected IterationTools <Factorization> IT;


	/**
	 * prepare a polynomial function description
	 * - the root of this function will be SQRT N
	 * @param N the constant term of the polynomial
	 * @return this object returned for chaining
	 */
	public NewtonRaphsonIterativeTest establishFunction (int N)
	{ this.functionDescription = new int [] { -N, 0, 1 }; return this; }			// description for [ -N + 0*x + 1*x^2 ]
	protected int [] functionDescription;


	/**
	 * entry point for running the test
	 * - simple version of stand-alone test sqrt(2)
	 * @param a not used
	 */
	public static void main (String [] a)
	{

		Factorization approx;

		FactorizationCore.init (1_000_000);

		approx = new NewtonRaphsonIterativeTest (2).run (11);

		FactorizationCore.display (approx, AccuracyCheck.S2_REF, "SQRT", 2000);

	}


	/**
	 * describe polynomial with initial coefficient values.
	 * - the initial value of X is also set
	 */
	void initializeFunction ()
	{
		initializeFunction (G.coefficients (functionDescription));
	}
	void initializeFunction (GeneratingFunctions.Coefficients<Factorization> C)
	{
		establishFunction (C);
		setApproximationOfX (IT.ONE);
	}


	/**
	 * @param iterations number of iterations to run
	 * @return the computed result after specified iterations
	 */
	public Factorization run (int iterations)
	{
		initializeFunction ();
		for (int i = 1; i <= iterations; i++) { iterate (); }
		return getX ();
	}
	public Factorization run
	(int iterations, GeneratingFunctions.Coefficients<Factorization> C)
	{
		initializeFunction (C);
		for (int i = 1; i <= iterations; i++) { iterate (); }
		return getX ();
	}


	/**
	 * apply next iteration to computation
	 */
	void iterate ()
	{
		applyIteration ();
		testVal = IT.POW (getX (), 2);
		if ( ! tracing ) return;
		System.out.println (this);
		System.out.println ();
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.iterative.IterationFoundations#add(java.lang.StringBuffer)
	 */
	public void add (StringBuffer buffer)
	{ buffer.append ("X^n = ").append (toString (testVal)).append ("\n"); }
	protected Factorization testVal;


	/**
	 * compute sqrt approximation
	 * @param x parameter to sqrt function
	 * @param iterations number of iterations to use
	 * @return the computed root value
	 */
	public Factorization sqrt (Factorization x, int iterations)
	{
		GeneratingFunctions.Coefficients <Factorization>
			C = G.toCoefficients
			(
				new Factorization []
				{
					IT.NEG (x), IT.Z, IT.ONE
				}
			);
		return run (iterations, C);
	}


	/**
	 * compute PHI with SQRT 5 
	 * @param radical5 the computed value of SQRT 5 with adequate precision
	 * @return the computed approximation of PHI
	 */
	public Factorization computePhi (Factorization radical5)
	{
		Factorization HALF = IT.oneOver (IT.S (2));
		Factorization radical5plus1 = IT.sumOf (IT.ONE, radical5);
		return IT.productOf (HALF, radical5plus1);
	}
	// phi = ( 1 + sqrt 5 ) / 2


}

