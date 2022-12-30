
package net.myorb.testing.factors;

import net.myorb.math.computational.iterative.Taylor;
import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;
import net.myorb.math.computational.iterative.IterationTools;

import net.myorb.math.primenumbers.Factorization;

/**
 * evaluation of the Taylor series for computation of Eulers constant
 * - this is the entry point and driver for specification of the iteration count
 * @author Michael Druckman
 */
public class TaylorTest extends Taylor <Factorization>
{


	// choose tests to run
	public static final boolean
			RUN_EULER_TEST = true,
			RUN_POWER_TESTS = true,
			RUN_POLYLOG_TEST = true
	;

	static final int							// iteration counts for tests
			EULER_SERIES_ITERATIONS = 50,		//		converges about 1 digit per iteration
			POLYLOG_SERIES_ITERATIONS = 500,	//		very slow convergence, only 2 digits for 500 iterations
			POWER_SERIES_ITERATIONS = 40		//		all convergence quickly and give good precision
	;

	static final int MAX_PRECISION = 20;		// truncate intermediate results at specified precision


	public TaylorTest ()
	{
		super (FactorizationCore.mgr);
		this.IT = new IterationTools <> (manager);
	}
	protected IterationTools <Factorization> IT;


	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		FactorizationCore.init (1_000_000);

		TaylorTest TT =
			new TaylorTest ().establishParameters
				(FactorizationCore.mgr, MAX_PRECISION);

		if (RUN_EULER_TEST)
		{
			TT.runEulerTest ();
		}

		if (RUN_POWER_TESTS)
		{
			TT.runPowerTests ();
		}

		if (RUN_POLYLOG_TEST)
		{
			TT.runPolylogTest ();
		}

	}

	TaylorTest establishParameters
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
	 * Euler test
	 */

	/**
	 * display results of test
	 * @param iterations the number to be run
	 * @return the computed result
	 */
	public Factorization computeEuler (int iterations)
	{
		return run (iterations, IT.getExpDerivativeComputer (), IT.ONE);
	}
	void runEulerTest ()
	{
		FactorizationCore.display
		(
			computeEuler (EULER_SERIES_ITERATIONS),
			AccuracyCheck.E_REF, "E", 1000
		);			
	}


	/*
	 * Power series
	 */

	/**
	 * run the full series of tests
	 */
	public void runPowerTests ()
	{
		// parameter for tests will be 1/4
		Factorization P = IT.oneOver (IT.S (4));
		// all reference values were provided by calculator

		FactorizationCore.display
		(
			// ( 1 + 1/4 ) ^ (1/2)
			computePowerSeries (IT.getSqrtDerivativeComputer (), P),
			"1.1180339887498948482045868343656", "SQRT", 1000
		);			

		FactorizationCore.display
		(
			// ( 1 + 1/4 ) ^ (-1/2)
			computePowerSeries (IT.getInvSqrtDerivativeComputer (), P),
			"0.89442719099991587856366946749251", "INV SQRT", 1000
		);			

		FactorizationCore.display
		(
			// 1 / ( 1 - 1/4 ) = 4/3
			computePowerSeries (IT.getGeometricDerivativeComputer (), P),
			"1.3333333333333333333333333333333", "GEO", 1000
		);			

		FactorizationCore.display
		(
			// computer takes exponent as parameter
			// so this test is ( 1 + 1/4 ) ^ ( 1 / 4 )
			computePowerSeries (IT.getBinomialDerivativeComputer (P), P),
			"1.0573712634405641195350370000286", "BIN", 1000
		);			

		FactorizationCore.display
		(
			// ln ( 1 + 1/4 )
			computePowerSeries (IT.getLogDerivativeComputer (), P),
			"0.22314355131420975576629509030983", "LOG 1.25", 1000
		);			

		FactorizationCore.display
		(
			// - ln ( 1 + (-1/2) )
			computeLn2SQ (), AccuracyCheck.Ln2SQ_REF, "LOG 2 ^ 2", 1000
		);			
	}

	/**
	 * compute (ln 2)^2 with the log series
	 * @return ln(2)^2
	 */
	public Factorization computeLn2SQ ()
	{
		Factorization P = IT.oneOver (IT.S (-2));
		Factorization lnHalf = run (2*POWER_SERIES_ITERATIONS, IT.getLogDerivativeComputer (), P);
		return IT.POW (lnHalf, 2); // NOTE: ln(1/2)^2 == ln(2)^2 because ln(1/2) = -ln(2)
	}

	/**
	 * direct computation of ln phi using log series
	 * @param phi computed value of phi from sqrt 5
	 * @param e computed value of e
	 * @return ln phi
	 */
	public Factorization computeLnPhi (Factorization phi, Factorization e)
	{
		Factorization lnPhiOverE = run
			(
				POWER_SERIES_ITERATIONS+5, IT.getLogDerivativeComputer (),
				IT.sumOf (IT.productOf (phi, IT.oneOver (e)), IT.S (-1))
			);
		return IT.sumOf (lnPhiOverE, IT.ONE);
	}
	public void checkLnPhi (Factorization phi, Factorization e)
	{
		FactorizationCore.display
		(
			// ln ( phi )
			computeLnPhi (phi, e),
			AccuracyCheck.LnPhi_REF,
			"LOG PHI", 1000
		);			
	}

	/**
	 * run calculations of test
	 * @param computer calculation engine for test
	 * @param P the parameter value for test
	 * @return the computed result
	 */
	public Factorization computePowerSeries
		(
			IterationTools.DerivativeComputer <Factorization> computer,
			Factorization P
		)
	{
		return run (POWER_SERIES_ITERATIONS, computer, P);
	}


	/*
	 * inverse tangent integrals test
	 */

	/**
	 * inverse tangent integrals test
	 * @param iterations the number to be run for test
	 * @param Sqrt_3 the computed SQRT(3)
	 * @return the computed result
	 */
	public Factorization runInvTanTest (int iterations, Factorization Sqrt_3)
	{
		Factorization p = IT.sumOf (IT.S (2), IT.NEG (Sqrt_3));
		return run (iterations, IT.getTi2DerivativeComputer (), p);
	}


	/*
	 * elliptic integrals 
	 */

	/**
	 * K elliptic integral
	 * @param iterations the number to be run for test
	 * @param PI value of PI to use in calculation
	 * @param numerator of the parameter fraction
	 * @param ref text of the reference
	 */
	public void runKTest
		(
			int iterations,
			Factorization PI, Factorization numerator,
			String ref
		)
	{
		runEITest ( iterations, IT.getKDerivativeComputer (), PI, numerator, ref, "K" );
	}

	/**
	 * E elliptic integral
	 * @param iterations the number to be run
	 * @param PI value of PI to use in calculation
	 * @param S2 value of sqrt2 to use in calculation
	 * @param ref text of the reference
	 */
	public void runETest
		(
			int iterations,
			Factorization PI, Factorization S2,
			String ref
		)
	{
		runEITest ( iterations, IT.getEDerivativeComputer (), PI, S2, ref, "E" );
	}

	/**
	 * general form of EI tests
	 * @param iterations the number to be run
	 * @param computer a derivative computer for test
	 * @param PI value of PI to use in calculation of result
	 * @param numerator double the parameter value to function call
	 * @param ref text of the verification reference
	 * @param tag a display tag for the test
	 */
	public void runEITest
		(
			int iterations,
			IterationTools.DerivativeComputer <Factorization> computer,
			Factorization PI, Factorization numerator,
			String ref, String tag
		)
	{
		Factorization HALF = IT.oneOver (IT.S (2));
		Factorization HN = IT.productOf (HALF, numerator);

		Factorization result = IT.productOf
			(
				run ( iterations, computer, HN ),
				IT.productOf ( HALF, PI )
			);
		FactorizationCore.display ( result, ref, tag, 1000 );
	}


	/*
	 * Li2 test
	 */

	/**
	 * evaluate Li2(1/2) and check result
	 * - difference between Li2 and PI^2 is Ln2^2
	 * @param iterations the number to be run for test
	 * @param PI value of PI to use in calculation
	 */
	public void runLn2SQtest (int iterations, Factorization PI)
	{
		Factorization Li2Half = computeLi2 (iterations, IT.oneOver (IT.S (2)));
		Factorization PiSQ12 = IT.productOf (IT.POW (PI, 2), IT.oneOver (IT.S (12)));
		Factorization HalfLn2SQ = IT.sumOf (PiSQ12, IT.NEG (Li2Half));
		Factorization Ln2SQ = IT.productOf (HalfLn2SQ, IT.S (2));

		FactorizationCore.display
		(
			Ln2SQ, AccuracyCheck.Ln2SQ_REF, "Ln2SQ", 100
		);
		// Li2(1/2) = PI^2/12 - Ln(2)^2/2
	}

	/**
	 * display results of test
	 * @param iterations the number to be run
	 * @param parameter the value to pass to the function
	 * @return the computed result
	 */
	public Factorization computeLi2 (int iterations, Factorization parameter)
	{
		return run (iterations, IT.getLi2DerivativeComputer (), parameter);
	}
	void runPolylogTest ()
	{
		FactorizationCore.display
		(
			computeLi2 (POLYLOG_SERIES_ITERATIONS, IT.ONE),
			AccuracyCheck.Li2_REF, "Li2", 1000
		);
	}


	/*
	 * methods used in ATAN tests
	 */

	/**
	 * check for factor of PI on ATAN
	 * @param iterations the number to be run
	 * @param of the parameter to pass to ATAN function
	 * @param checkProduct anticipated factor of PI
	 * @return the computed result
	 */
	public Factorization computeAtan
		(int iterations, Factorization of, int checkProduct)
	{
		Factorization approx =
			run (iterations, IT.getAtanDerivativeComputer (), of);
		return IT.productOf (approx, IT.S (checkProduct));
	}

	/**
	 * compute TAN(PI/12)
	 * @param Sqrt_3 the computed SQRT(3)
	 * @return 2 - SQRT(3)
	 */
	public Factorization tanPi12 (Factorization Sqrt_3)
	{ return IT.sumOf (IT.NEG (Sqrt_3), IT.S (2)); }

	/**
	 * compute TAN(PI/6)
	 * @param Sqrt_3 the computed SQRT(3)
	 * @return 1 / SQRT(3)
	 */
	public Factorization tanPi6 (Factorization Sqrt_3)
	{ return IT.oneOver (Sqrt_3); }


}

