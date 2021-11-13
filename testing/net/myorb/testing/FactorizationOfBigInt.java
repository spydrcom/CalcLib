
package net.myorb.testing;

import net.myorb.math.primenumbers.*;
import net.myorb.math.primenumbers.sieves.SieveOfSundaram;

import java.math.BigInteger;

public class FactorizationOfBigInt extends FactorAdjustment
{


	public static final String
	numeratorDigits		= "4445543253238546227777822036946171978625807664697905766984",
	denominatorDigits	= "1415060366963480161186573164611560053913785826145542814715";


	public void establishRatio ()
	{
		num = mgr.attemptFactorization (new BigInteger (numeratorDigits));
		denom = mgr.attemptFactorization (new BigInteger (denominatorDigits));
		ratio = num.divideBy(denom);
	}
	Factorization num, denom, ratio;



	public void initializeParameters ()
	{
		establishRatio ();
		System.out.println (); System.out.print ("ratio:  "); System.out.print (ffm.toDecimalString (ratio));
	}


	public void runNumeratorTest ()
	{
		System.out.println ();
		System.out.println ("Numerator Test");

		Factorization adjusted = replaceBlob (num), alternate;
		formalDisplay ("alternate:  ", alternate = adjusted.divideBy (denom));

		Factorization error = num.add (adjusted.negate ()), decErr = error.divideBy (denom);
		System.out.println ("error:  " + error + " (" + error.reduce () + ") = " + ffm.toDecimalString (decErr));
		simpleDecimalDisplay ("dif:  ", ratio.add (alternate.negate ()));
	}


	public void runRatioTest ()
	{
		System.out.println ();
		System.out.println ("Ratio Test");
		substituteAndAnalyze (ratio);
	}


	/**
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		initTable ();

		FactorizationOfBigInt test =
			new FactorizationOfBigInt ();
		test.initializeParameters ();

		test.runNumeratorTest ();
		test.runRatioTest ();
	}


	public static void initTable ()
	{
		Factorization.setImplementation
		(support = new FactorizationImplementation (4 * 1000 * 1000));				// version of implementation that uses table scan
		support.initFactorizationsWithStats (new SieveOfSundaram (support));		// using non-default sieve (SieveOfSundaram)
	}
	public static FactorizationImplementation support;


	//FactorizationOfBigInt () { super (700); }


}

