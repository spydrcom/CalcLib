
package net.myorb.testing.factors;

import net.myorb.math.computational.iterative.IterationTools;
import net.myorb.math.primenumbers.Factorization;

import net.myorb.data.notations.json.JsonLowLevel;
import net.myorb.data.notations.json.*;

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

		IterativeAlternativeAlgorithmTests testScripts =
			new IterativeAlternativeAlgorithmTests ();
		//testScripts.enableTracing ();

		testScripts.computeSqrt ();
		testScripts.computePi ();

		testScripts.runTanTest ();

		testScripts.runSecTest ();

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
		compute (getPiOver (12), 30, ref, IT.getTanDerivativeComputer (), "TAN");
	}


	/**
	 * computation of sec
	 */
	void runSecTest ()
	{
		String ref = "1.0352761804100830493955953504962";
		compute (getPiOver (12), 30, ref, IT.getSecDerivativeComputer (), "SEC");
	}


	/**
	 * computation of chi2
	 */
	void runChiTests ()
	{
		//runChiTest
		//	(IT.ONE, 2000, AccuracyCheck.Chi1_REF, "CHI2(1)");
		// chi2 (1)  =  PI^2/8  =  1.2337005501361698273543113749845

		computePhi ();
		Factorization chiPhi = compute
				(
					IT.sumOf (reduce (phi), IT.S (-1)), 80,
					AccuracyCheck.ChiPhi_REF, IT.getChi2DerivativeComputer (),
					"CHI2(phi-1)"
				);
		// chi2(PHI-1)  =  PI^2/12 - 3/4 [ln(PHI)]^2  =  0.64879341799121742386351077989936
		runLnPhiTest (reduce (chiPhi));
	}


	/**
	 * computation of ln(PHI)
	 */
	void runLnPhiTest (Factorization chi)
	{
		display
			(
				chi, AccuracyCheck.ChiPhi_REF,
				"REDUCED CHI ( as factor of ln[PHI] )"
			);
		Factorization lnPhi = computeLnPhiTest (chi);
		display (lnPhi, AccuracyCheck.LnPhi_REF, "ln PHI");
		timeStamp ();
	}
	Factorization computeLnPhiTest (Factorization chi)
	{
		Factorization
			piSQ = IT.POW (getReducedPi (), 2),
			piSQ12  = IT.productOf (IT.oneOver (IT.S (12)), piSQ),
			lnPhi34 = IT.sumOf (piSQ12, manager.negate (chi));				// PI^2/12 - chi
		Factorization
			lnPhi3  = IT.productOf (reduce (lnPhi34), IT.S (4)),
			lnPhiSQ = IT.productOf (lnPhi3, IT.oneOver (IT.S (3)));			// 4/3 * [ PI^2/12 - chi ]
		return NR.sqrt (lnPhiSQ, ROOT_ITERATIONS);
	}
	Factorization reduce (Factorization x)					// truncation to 25 decimal places
	{
		return FactorizationCore.mgr
			.getPrecisionManager ()
			.truncate (x, 40);	
	}


	/**
	 * compute fraction of PI
	 * @param n the fraction to be computed
	 * @return the computed fraction
	 */
	Factorization getPiOver (int n)
	{
		Factorization Nth = IT.oneOver (IT.S (n));
		return manager.multiply (getReducedPi (), Nth);
	}


	/* (non-Javadoc)
	 * @see net.myorb.testing.factors.IterativeCompoundAlgorithmTests#getReducedPi()
	 */
	Factorization getReducedPi ()
	{
		JsonLowLevel.JsonValue json = null;
		try { json = readFile (); } catch (Exception e) {}
		Factorization pi = reduce (FactorizationCore.mgr.fromJson (json));
		System.out.println ("Reduced PI = " + pi);
		return pi;
	}
	JsonLowLevel.JsonValue readFile () throws Exception
	{
		JsonLowLevel.JsonValue json =
			JsonReader.readFrom ( JsonReader.getFileSource ("data/PI.json") );
		JsonSemantics.JsonObject JO = (JsonSemantics.JsonObject) json;
		System.out.println (json = JO.getMemberCalled ("Content"));
		return json;
	}


}

