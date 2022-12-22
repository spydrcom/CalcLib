
package net.myorb.testing.factors;

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
		
		testScripts.enableTracing ();

		testScripts.computeSqrt ();
		testScripts.computePi ();

		testScripts.runChiTests ();

		testScripts.runTrigTest ();

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
		Factorization chiPhi = 
			runChiTest (IT.sumOf (reduce (phi), IT.S (-1)), 80, AccuracyCheck.ChiPhi_REF, "CHI2(phi-1)");
		// chi2(PHI-1)  =  PI^2/12 - 3/4 [ln(PHI)]^2  =  0.64879341799121742386351077989936
		runLnPhiTest (reduce (chiPhi));
	}
	Factorization runChiTest (Factorization of, int iterations, String ref, String tag)
	{
		Factorization chi = run
			(iterations, IT.getChi2DerivativeComputer (), of);
		display (chi, ref, tag);
		timeStamp ();
		return chi;
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
			.truncate (x, 25);	
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

