
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
		testScripts.runTrigTest ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.testing.factors.IterativeCompoundAlgorithmTests#getReducedPi()
	 */
	Factorization getReducedPi ()
	{
		JsonLowLevel.JsonValue json = null;
		try { json = readFile (); } catch (Exception e) {}
		Factorization pi = FactorizationCore.mgr.fromJson (json);
		System.out.println (pi);
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
