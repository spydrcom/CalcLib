
package net.myorb.testing.factors;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.primenumbers.Factorization;

import net.myorb.math.computational.iterative.NewtonRaphson;
import net.myorb.math.computational.iterative.IterationTools;

import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;

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


	/*

			Requested	Iterations		Elapsed		Resulting
			Precision	(short-circuit)	Time		Precision
			=========   =============== =======     =========

			 10			 5				 340ms			  12
			 20			 7				 370ms			  22
			 30			 7				 550ms			  33
			 40			 7				 880ms			  42
			 50			 8				 900ms			  54
			 60			 8				 930ms			  62
			 70			 8				1100ms			  73
			 80			 8				1130ms			  83
			 90			 9				1260ms			  93
			100			10				1500ms			 102
			120			 9				1700ms			 124
			150			 9				1900ms			 154
			200			11				3100ms			 206
			300			10				4650ms			 303

			 0			11				 950ms			1569
			 0			12				2460ms			3136

			times are averages done of several runs...
			cross-over appears to be 60 digits of requested precision.

			the precision manipulations significantly reduce time efficiency.
			for numbers >60 it seem better strategy to just take results set by iteration count...
			and then apply a precision reduction of the longer result for subsequent uses.

	 */
	static final int
		MAX_PRECISION		= 300,		//	00 gives unrestricted
		MAX_DIGIT_COMPARE	= 1000,
		MAX_ITERATIONS		= 12
	;


	/**
	 * entry point for running the test
	 * - simple version of stand-alone test sqrt(2)
	 * @param a not used
	 */
	public static void main (String [] a)
	{

		Factorization approx;

		FactorizationCore.init (1_000_000);
		approx = new NewtonRaphsonIterativeTest (2)		// testing SQRT 2
		.establishParameters (MAX_PRECISION)
		.run (MAX_ITERATIONS);

		FactorizationCore.display
			(
				approx,
				AccuracyCheck.S2_REF, "SQRT",			// SQRT 2 reference
				MAX_DIGIT_COMPARE
			);
		FactorizationCore.timeStamp ();

	}

	/**
	 * install Precision Monitor
	 * @param precision requested digits as metric of accuracy
	 * @return THIS object
	 */
	NewtonRaphsonIterativeTest establishParameters (int precision)
	{ return establishParameters (FactorizationCore.mgr, precision); }

	NewtonRaphsonIterativeTest establishParameters
		(ExpressionFactorizedFieldManager m, int precision)
	{
		if (precision > 0)
		{
			this.installPrecisionMonitor
			(
				m.getPrecisionManager (),
				m, precision
			);
		}
		return this;
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
	public Factorization run ( int iterations )
	{ initializeFunction (); runForCount (iterations); return getX (); }
	public Factorization run ( int iterations,  GeneratingFunctions.Coefficients <Factorization>  C )
	{ initializeFunction (C); runForCount (iterations); return getX (); }

	/**
	 * trap short-circuit condition
	 * - allowing controlled termination of loop
	 * @param iterations number of iterations to run
	 */
	void runForCount (int iterations)
	{
		try { for ( int i = 1; i <= iterations; i++ ) { iterate (i); } }
		catch (ShortCircuitTermination e) { if (tracing) System.out.println (e.getMessage ()); }
	}


	/**
	 * apply next iteration to computation
	 */
	void iterate (int iteration) throws ShortCircuitTermination
	{
		applyIteration ();
		this.testForShortCircuit
		(this.getDelta (), iteration, manager);
		if (tracing) traceIteration ();
	}
	void traceIteration ()
	{
		testVal = IT.POW (getX (), 2);
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

