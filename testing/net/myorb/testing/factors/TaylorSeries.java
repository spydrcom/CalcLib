
package net.myorb.testing.factors;

import net.myorb.math.computational.iterative.Taylor;
import net.myorb.math.computational.iterative.IterationTools;
import net.myorb.math.computational.iterative.IterationTools.DerivativeComputer;

import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;

import net.myorb.math.GeneratingFunctions.Coefficients;
import net.myorb.math.primenumbers.Factorization;
import net.myorb.math.Polynomial;

/**
 * evaluation of the Taylor series for computation of function results
 * - this is the entry point and driver for specification of the iteration counts
 * - this platform provides comparisons between use of fixed polynomials with computed coefficients
 * - as opposed to use of the Foundation mechanisms which apply restrictions to precision of computations
 * - HOW MUCH precision is needed from the computations VS HOW MUCH time consumption is considered acceptable
 * - this is intended to demonstrate trade-offs of excess precision as opposed to excess monitoring
 * - both have time efficiency considerations which can become prohibitive
 * - and then there are the precision considerations
 * @author Michael Druckman
 */
public class TaylorSeries extends Taylor <Factorization>
{


	static final int POLYNOMIAL_ORDER = 40;		// number of terms in the polynomial evaluation
	static final int MAX_PRECISION = 20;		// truncate intermediate results at specified precision


	public TaylorSeries ()
	{
		super (new IAT ().getManager ());
		this.IT = new IterationTools <> (manager);
		this.manager.setDisplayPrecision (25);
		FactorizationCore.timeStampReset ();
		this.prepareConstants ();
		this.enableTracing ();
	}
	protected IterationTools <Factorization> IT;


	/**
	 * parameters to function calls
	 */
	void prepareConstants ()
	{
		this.Q = IT.oneOver (IT.S (4));		// Quarter
		this.H = IT.oneOver (IT.S (2));		// Half
	}
	protected Factorization Q, H;


	/**
	 * establish context for the algorithms that will run tests
	 */
	public interface ComputationEngine
	{
		/**
		 * execute a function call given a DerivativeComputer
		 * @param computer the DerivativeComputer for the function
		 * @param P the parameter to the function when called
		 * @return the result of the function call
		 */
		Factorization evaluate
		(
			DerivativeComputer <Factorization> computer,
			Factorization P
		);
	}


	/**
	 * entry point for running the test cases
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		TaylorSeries TS =
			new TaylorSeries ().establishParameters
				(FactorizationCore.mgr, MAX_PRECISION);
		//TS.enableTracing ();

		TS.test1 ();
		TS.test2 ();
		TS.test3 ();
		TS.test4 ();

	}
	TaylorSeries establishParameters
		(ExpressionFactorizedFieldManager m, int precision)
	{
		this.installPrecisionMonitor
		(
			m.getPrecisionManager (),
			m, precision
		);
		return this;
	}


	/*
	 * test cases
	 */

	/**
	 * straight polynomial evaluation with no short circuit
	 * - so this will show 30 digits in result since all terms evaluated
	 * compared with optimized polynomial evaluation with short circuit
	 * - so this will show 20 digits in computed result
	 * - since short circuit reduces term count
	 */
	void test1 ()
	{
		runComparisonTest
		(
			IT.getExpDerivativeComputer (), H,
			"1.6487212707001281468486507878142",
			"SQRT e"
		);
	}

	/**
	 * binomial expansion
	 */
	void test2 ()
	{
		runComparisonTest
		(
			IT.getBinomialDerivativeComputer (Q), Q,
			"1.0573712634405641195350370000286",
			"BIN"
		);
	}

	/**
	 * ln ( 1 + x )
	 */
	void test3 ()
	{
		runComparisonTest
		(
			IT.getLogDerivativeComputer (), Q,
			"0.22314355131420975576629509030983",
			"ln 1.25"
		);
	}

	/**
	 * chi ( phi - 1 )
	 */
	void test4 ()
	{
		Factorization phiM1 =
			IT.sumOf (IAT.getPhi (), IT.S (-1));
		runComparisonTest
		(
			IT.getChi2DerivativeComputer (), phiM1,
			IAT.ChiPhi_REF, "chi (phi-1)"
		);
	}


	/*
	 * evaluation of the function using polynomial evaluation
	 */

	/**
	 * evaluate function using a polynomial built from DerivativeComputer
	 * @param computer the DerivativeComputer for the function
	 * @param x the parameter to the function when called
	 * @return the result of the function call
	 */
	Factorization computeSeries
		(
			DerivativeComputer <Factorization> computer,
			Factorization x
		)
	{
		System.out.println ("build series");
		Polynomial.PowerFunction <Factorization>
			S = seriesFor (computer, POLYNOMIAL_ORDER);
		System.out.println ("Polynomial Eval");
		FactorizationCore.timeStamp ();
		dumpCoefficients (S);
		return S.eval (x);
	}

	/**
	 * display computed coefficients
	 * @param S the series built for the function
	 */
	void dumpCoefficients (Polynomial.PowerFunction <Factorization> S)
	{
		java.util.List <String> CD = new java.util.ArrayList <> ();
		for (Factorization F : S.getCoefficients ())
		{ CD.add (manager.toDecimalString (F)); }
		System.out.println (CD);
	}

	/**
	 * run a test using a prepared polynomial
	 * @param computer the DerivativeComputer for the function
	 * @param P the parameter to the function when called
	 * @param ref the reference showing proper result
	 * @param tag identifier for test
	 */
	void seriesTest
		(
			DerivativeComputer <Factorization> computer,
			Factorization P, String ref, String tag
		)
	{
		runTest ( (c, p) -> this.computeSeries (c, p), computer, P, ref, tag+" [series]");
	}


	/*
	 * evaluation of the function using IterationFoundations algorithms
	 */

	/**
	 * evaluate function using iterative foundations algorithms
	 * @param computer the DerivativeComputer for the function
	 * @param x the parameter to the function when called
	 * @return the result of the function call
	 */
	Factorization computeFoundation
		(
			DerivativeComputer <Factorization> computer,
			Factorization x
		)
	{
		System.out.println ("Compute Coefficients");
		Coefficients <Factorization> C = this.computeCoefficients
				(computer, POLYNOMIAL_ORDER);
		System.out.println ("Iteration Foundation eval");
		FactorizationCore.timeStamp ();
		return eval (x, C);
	}

	/**
	 * run a test using iterative foundations algorithms
	 * @param computer the DerivativeComputer for the function
	 * @param P the parameter to the function when called
	 * @param ref the reference showing proper result
	 * @param tag identifier for test
	 */
	void foundationTest
		(
			DerivativeComputer <Factorization> computer,
			Factorization P, String ref, String tag
		)
	{
		runTest ( (c, p) -> this.computeFoundation (c, p), computer, P, ref, tag+" [foundation]");
	}


	/*
	 * test controls
	 */

	/**
	 * run a test as specified by the driver
	 * @param engine a driver that runs the function
	 * @param computer a derivative computer for test
	 * @param P parameter to function being evaluated
	 * @param ref result evaluation reference
	 * @param tag identifier for test
	 */
	void runTest
		(
			ComputationEngine engine,
			DerivativeComputer <Factorization> computer,
			Factorization P, String ref, String tag
		)
	{
		FactorizationCore.timeStampReset ();
		Factorization V = engine.evaluate (computer, P);
		FactorizationCore.display (V, ref, tag);
		FactorizationCore.timeStamp ();
	}

	/**
	 * run series VS foundations tests
	 * @param computer a derivative computer for test
	 * @param P parameter to function being evaluated
	 * @param ref result evaluation reference
	 * @param tag identifier for test
	 */
	void runComparisonTest
		(
			DerivativeComputer <Factorization> computer,
			Factorization P, String ref, String tag
		)
	{
		seriesTest (computer, P, ref, tag);
		foundationTest (computer, P, ref, tag);
	}


}


/**
 * import test suite that computes SQRT values to be used as constants
 */
class IAT extends IterativeAlternativeAlgorithmTests
{
	IAT () { computeSqrt (); }

	ExpressionFactorizedFieldManager getManager ()
	{ return FactorizationCore.mgr; }

	static Factorization getPhi ()
	{
		computePhi ();
		return phi;
	}
}

