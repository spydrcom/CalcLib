
package net.myorb.testing.factors;

import net.myorb.math.computational.iterative.IterationTools;
import net.myorb.math.computational.iterative.IterationTools.DerivativeComputer;

import net.myorb.math.primenumbers.Factorization;

/**
 * evaluation of the Taylor series for trigonometry using chosen precision of PI
 * - the value of PI is obtained from a data file containing a previously stored reduced version
 * - time stamps on original version VS this version show significant difference in overhead
 * @author Michael Druckman
 */
public class IterativeAlternativeAlgorithmTests
	extends IterativeCompoundAlgorithmTests
{


	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		IterativeAlternativeAlgorithmTests
			testScripts = new IterativeAlternativeAlgorithmTests ();
		testScripts.establishParameters (FactorizationCore.mgr, COMPUTATION_PRECISION);
		//testScripts.enableTracing ();

		// required constants
		testScripts.computeSqrt ();
		testScripts.computePi ();

		// scripted tests
		testScripts.runTanTest ();
		testScripts.runSecTest ();
		testScripts.runLnPhiTest ();
		testScripts.runChiTests ();
		testScripts.runTrigTest ();

	}


	/**
	 * general computation test script
	 * @param of the parameter to the computation
	 * @param iterations the number of iteration to run
	 * @param ref the text of the expected result
	 * @param computer the derivative computer
	 * @param tag a descriptive title
	 * @return the computed value
	 */
	Factorization compute
		(
			Factorization of, int iterations, String ref,
			IterationTools.DerivativeComputer <Factorization> computer,
			String tag
		)
	{
		Factorization result =
			run (iterations, computer, of);
		display (result, ref, tag); timeStamp ();
		return result;
	}


	/**
	 * computation of tan
	 */
	void runTanTest ()
	{
		String ref = "0.26794919243112270647255365849413";
		DerivativeComputer <Factorization> computer = IT.getTanDerivativeComputer ();
		compute (getPiOver (12), TRIG_SERIES_ITERATIONS, ref, computer, "TAN");
	}


	/**
	 * computation of sec
	 */
	void runSecTest ()
	{
		String ref = "1.0352761804100830493955953504962";
		DerivativeComputer <Factorization> computer = IT.getSecDerivativeComputer ();
		compute (getPiOver (12), TRIG_SERIES_ITERATIONS, ref, computer, "SEC");
	}


	/**
	 * computation of ln phi
	 */
	void runLnPhiTest ()
	{
		computePhi (); computeEuler ();
		checkLnPhi (reduce (phi), reduce (e));
	}


	/**
	 * computation of chi2
	 */
	void runChiTests ()
	{
		String ref = "1.2337005501361698273543113749845";
		DerivativeComputer <Factorization> computer = IT.getChi2DerivativeComputer ();
		compute (IT.ONE, 200, ref, computer, "CHI2(1)");
		runChiPhiTests (computer);
	}


	/**
	 * chi2 (phi-1) followed by ln(phi)
	 * @param computer the DerivativeComputer being used for chi tests
	 */
	void runChiPhiTests (DerivativeComputer <Factorization> computer)
	{
		computePhi ();
		Factorization phiMinus1 = IT.sumOf (reduce (phi), IT.S (-1));
		Factorization chiPhi = compute (phiMinus1, 80, ChiPhi_REF, computer, "CHI2(phi-1)" );
		runLnPhiTest (reduce (chiPhi));
	}
	//				chi2(PHI-1)  =  PI^2/12 - 3/4 [ln(PHI)]^2
	public static String ChiPhi = "0.6487934179 9121742386 3510779899 36";	// 32 digits
	public static String ChiPhi_REF = ChiPhi.replaceAll (" ", "");


	/**
	 * computation of ln(phi)
	 */
	void runLnPhiTest (Factorization chiPhi)
	{
		display (chiPhi, ChiPhi_REF, ChiPhiTitle);
		Factorization lnPhi = computeLnPhiTest (chiPhi);
		display (lnPhi, AccuracyCheck.LnPhi_REF, "ln PHI");
		timeStamp ();
	}
	public static String ChiPhiTitle = "REDUCED CHI ( as factor of ln[PHI] )";


	/**
	 * formula for computation of LnPhi from chi2Phi
	 * @param chiPhi the computed value of chi2 (phi)
	 * @return the computed value of ln (phi)
	 */
	Factorization computeLnPhiTest (Factorization chiPhi)
	{
		Factorization
			piSQ = IT.POW (getReducedPi (), 2),
			piSQ12  = IT.productOf (IT.oneOver (IT.S (12)), piSQ),
			lnPhi34 = IT.sumOf (piSQ12, manager.negate (chiPhi));			// PI^2/12 - chiPhi
		Factorization
			lnPhi3  = IT.productOf (reduce (lnPhi34), IT.S (4)),
			lnPhiSQ = IT.productOf (lnPhi3, IT.oneOver (IT.S (3)));			// 4/3 * [ PI^2/12 - chiPhi ]
		return NR.sqrt (lnPhiSQ, ROOT_ITERATIONS);
	}


	/**
	 * truncation to specified precision
	 * @param x value to be truncated
	 * @return truncated value
	 */
	Factorization reduce (Factorization x) { return FactorizationCore.reduce (x, COMPUTATION_PRECISION); }


	/**
	 * compute fraction of PI
	 * - reduce precision of the value being used
	 * @param n the fraction to be computed
	 * @return the computed fraction
	 */
	Factorization getPiOver (int n) { return FactorizationCore.getPiOver (n, COMPUTATION_PRECISION); }


	/* (non-Javadoc)
	 * @see net.myorb.testing.factors.IterativeCompoundAlgorithmTests#getReducedPi()
	 */
	Factorization getReducedPi () { return FactorizationCore.getReducedPi (COMPUTATION_PRECISION); }
	// the override reducing PI precision has dramatic effect on execution time
	// this effect is seen running IterativeCompoundAlgorithmTests
	// as compared to running the override version


}

