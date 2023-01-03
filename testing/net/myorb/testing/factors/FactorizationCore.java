
package net.myorb.testing.factors;

import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;

import net.myorb.math.primenumbers.FactorizationImplementation;
import net.myorb.math.primenumbers.sieves.SieveOfSundaram;
import net.myorb.math.primenumbers.Factorization;

import net.myorb.data.notations.json.*;

/**
 * allocation and initialization of the prime and composite lookup tables
 * @author Michael Druckman
 */
public class FactorizationCore
{


	/**
	 * initialize a table of composites for use in factorizations
	 * @param tableSize the number of composites to compute
	 */
	public static void init (int tableSize)
	{
		Factorization.setImplementation
		(support = new FactorizationImplementation (tableSize));					// version of implementation that uses table scan
		support.initFactorizationsWithStats (new SieveOfSundaram (support));		// using non-default sieve (SieveOfSundaram)
	}
	public static ExpressionFactorizedFieldManager
			mgr = new ExpressionFactorizedFieldManager ();
	public static FactorizationImplementation support;


	/**
	 * provide data describing the precision of samples
	 * @param approx the Factorization approximation of a test value
	 * @param REF the text of the reference value to compare
	 * @param tag a display name for the computed value
	 * @param precision maximum digits to be checked
	 */
	public static void display
		(Factorization approx, String REF, String tag, int precision)
	{
		String APX;
		System.out.println ();
		int n = precision == 0 ?
			REF.length () : precision;
		int p = mgr.pushDisplayPrecision (n);
		System.out.println (tag); System.out.println ();
		System.out.println ( APX = mgr.toDecimalString ( approx ) );
		System.out.println ( toRatio ( approx ) ); System.out.println ();
		showDifAt ( APX, REF);  mgr.setDisplayPrecision ( p );
		System.out.println ("==="); System.out.println ();
	}
	public static void display
	(Factorization approx, String REF, String tag)
	{ display (approx, REF, tag, 0); }



	/**
	 * display evaluation of comparison of approximation against reference
	 * @param REF the text of the reference
	 * @param APX the approximation
	 */
	public static void showDifAt (String REF, String APX)
	{
		System.out.print ("DIF AT = ");
		System.out.println (AccuracyCheck.difAt (REF, APX));
	}


	/**
	 * calculate errors of SQRT approximations
	 * @param approx the value computed as approximation of a SQRT
	 * @param square the correct value of the square of the approximation
	 * @param tag a display name for context
	 */
	public static void displayError
	(Factorization approx, int square, String tag)
	{
		Factorization approxSQ = mgr.pow (approx, 2);
		Factorization error = mgr.add (approxSQ, mgr.negate (mgr.newScalar (square)));
		System.out.print (tag + " error = "); System.out.println (mgr.toDecimalString (error));
		System.out.println ();
	}


	/**
	 * convert to fraction display
	 * @param x the value to be displayed
	 * @return the text of the display
	 */
	public static String toRatio (Factorization x) { return mgr.toPrimeFactors (x); }


	/**
	 * format as decimal text
	 * @param value the value to be displayed
	 * @param precision the number of digits for the display
	 * @return the text as formatted
	 */
	public static String toDecimalString (Factorization value, int precision)
	{
		// format results for display
		int p = mgr.pushDisplayPrecision (precision);
		String rep =  mgr.toDecimalString (value);
		mgr.setDisplayPrecision (p);
		return rep;
	}


	/**
	 * a function that computes an approximation
	 */
	public interface Computer
	{
		/**
		 * @return the computed value
		 */
		Factorization compute ();
	}


	/**
	 * wrap computation in display tags
	 * @param computer the function evaluating the approximation
	 * @param tag a display name for the value
	 * @return the computed value
	 */
	public static Factorization display (Computer computer, String tag)
	{
		Factorization value;

		System.out.println ();
		System.out.println (tag);
		System.out.println ();

		value = computer.compute ();

		System.out.print (tag + " = ");
		System.out.println (value);
		timeStamp ();

		return value;
	}


	/**
	 * compute fraction of PI
	 * @param n the fraction to be computed
	 * @param withPrecision number of decimal places
	 * @return the computed fraction
	 */
	public static Factorization getPiOver (int n, int withPrecision)
	{
		Factorization Nth = mgr.invert (mgr.newScalar (n));
		return mgr.multiply (getReducedPi (withPrecision), Nth);
	}


	/**
	 * read stored value of PI and choose precision
	 * @param n number of decimal places of precision
	 * @return reduced value of PI
	 */
	public static Factorization getReducedPi (int n)
	{
		JsonLowLevel.JsonValue json = null;
		try { json = readFile (); } catch (Exception e) {}
		Factorization pi = reduce (FactorizationCore.mgr.fromJson (json), n);
		// System.out.println ("Reduced PI = " + pi);
		return pi;
	}
	static JsonLowLevel.JsonValue readFile () throws Exception
	{
		JsonLowLevel.JsonValue json =
			JsonReader.readFrom ( JsonReader.getFileSource ("data/PI.json") );
		JsonSemantics.JsonObject JO = (JsonSemantics.JsonObject) json;
		json = JO.getMemberCalled ("Content");
		// System.out.println (json);
		return json;
	}


	/**
	 * truncation to 25 decimal places
	 * @param x the value to be truncated
	 * @param n number of decimal places
	 * @return truncated value
	 */
	public static Factorization reduce (Factorization x, int n)
	{
		return mgr.getPrecisionManager ()
				.truncate (x, n);	
	}


	/**
	 * show elapsed time
	 */
	public static void timeStamp ()
	{
		System.out.println ();
		System.out.println ("---");
		long elapsed = System.currentTimeMillis () - start;
		System.out.println (elapsed + "ms");
		System.out.println ("---");
	}
	static { start = System.currentTimeMillis (); }
	static long start;


}

