
package net.myorb.testing.factors;

import net.myorb.math.primenumbers.Factorization;

import net.myorb.math.computational.iterative.Taylor;
import net.myorb.math.computational.iterative.IterationTools;

import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;

import net.myorb.math.GeneratingFunctions.Coefficients;

import net.myorb.math.Polynomial;

/**
 * evaluation of the Taylor series for computation of function results
 * - this is the entry point and driver for specification of the iteration count
 * @author Michael Druckman
 */
public class TaylorSeries extends Taylor <Factorization>
{


	static final int POLYNOMIAL_ORDER = 30;		// number of terms in the polynomial evaluation
	static final int MAX_PRECISION = 20;		// truncate intermediate results at specified precision


	public TaylorSeries ()
	{
		super (FactorizationCore.mgr);
		this.IT = new IterationTools <> (manager);
		manager.setDisplayPrecision (25);
		this.enableTracing ();
	}
	protected IterationTools <Factorization> IT;


	/**
	 * straight polynomial evaluation with no short circuit
	 * - so this will show 30 digits in result since all terms evaluated
	 */
	void test1 ()
	{
		Factorization HALF = IT.oneOver (IT.S (2)),
			V = compute (IT.getExpDerivativeComputer (), HALF);
		FactorizationCore.display (V, ref, "SQRT e");
	}
	String ref = "1.6487212707001281468486507878142";

	/**
	 * optimized polynomial evaluation with short circuit
	 * - so this will show 20 digits in computed result
	 * - since short circuit reduces term count
	 */
	void test2 ()
	{
		Factorization HALF = IT.oneOver (IT.S (2));
		Coefficients <Factorization> C = this.computeCoefficients
			(IT.getExpDerivativeComputer (), MAX_PRECISION);
		FactorizationCore.display (compute (C, HALF), ref, "SQRT e");
	}


	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		FactorizationCore.init (1_000_000);

		TaylorSeries TS =
			new TaylorSeries ().establishParameters
				(FactorizationCore.mgr, MAX_PRECISION);
		//TS.enableTracing ();

		TS.test1 ();
		TS.test2 ();

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


	Factorization compute
		(
			IterationTools.DerivativeComputer <Factorization> computer,
			Factorization x
		)
	{
		Polynomial.PowerFunction <Factorization>
			S = seriesFor (computer, POLYNOMIAL_ORDER);
		return S.eval (x);
	}


	Factorization compute
		(
			Coefficients <Factorization> C,
			Factorization x
		)
	{
		return eval (x, C);
	}


}

