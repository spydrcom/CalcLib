
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
 * - this is the entry point and driver for specification of the iteration count
 * @author Michael Druckman
 */
public class TaylorSeries extends Taylor <Factorization>
{


	static final int POLYNOMIAL_ORDER = 40;		// number of terms in the polynomial evaluation
	static final int MAX_PRECISION = 20;		// truncate intermediate results at specified precision


	public TaylorSeries ()
	{
		super (FactorizationCore.mgr);
		this.IT = new IterationTools <> (manager);
		FactorizationCore.timeStampReset ();
		manager.setDisplayPrecision (25);
		this.Q = IT.oneOver (IT.S (4));
		this.H = IT.oneOver (IT.S (2));
		this.enableTracing ();
	}
	protected Factorization Q, H;	// Quarter, Half
	protected IterationTools <Factorization> IT;


	/**
	 * establish context for the algorithms that will run tests
	 */
	public interface ComputationEngine
	{
		/**
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

		// compute constants used by tests
		new IterativeAlgorithmTests ().computeSqrt ();
		IterativeAlgorithmTests.computePhi ();

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
	 * ln (1 + x)
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
	 * chi (phi-1)
	 */
	void test4 ()
	{
		Factorization phiM1 =
			IT.sumOf (IterativeAlgorithmTests.phi, IT.S (-1));
		runComparisonTest
		(
			IT.getChi2DerivativeComputer (), phiM1,
			IterativeAlternativeAlgorithmTests.ChiPhi_REF,
			"chi (phi-1)"
		);
	}


	/*
	 * evaluation of the function using polynomial evaluation
	 */

	Factorization computeSeries
		(
			IterationTools.DerivativeComputer <Factorization> computer,
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

	void dumpCoefficients (Polynomial.PowerFunction <Factorization> S)
	{
		java.util.List <Factorization> CF = S.getCoefficients ();
		java.util.List <String> CD = new java.util.ArrayList <> ();
		for (Factorization F : CF) { CD.add (manager.toDecimalString (F)); }
		System.out.println (CD);
	}

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

	Factorization computeFoundation
		(
			IterationTools.DerivativeComputer <Factorization> computer,
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

	void foundationTest
		(
			DerivativeComputer <Factorization> computer,
			Factorization P, String ref, String tag
		)
	{
		runTest ( (c, p) -> this.computeFoundation (c, p), computer, P, ref, tag+" [foundation]");
	}


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

