
package net.myorb.testing;

import net.myorb.math.HighSpeedMathLibrary;
import net.myorb.math.PowerLibrary;
import net.myorb.math.SpaceManager;
import net.myorb.math.computational.Combinatorics;
import net.myorb.math.primenumbers.Factorization;
import net.myorb.math.primenumbers.FactorizationFieldManager;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

public class CombinatoricsTesting<T> extends Combinatorics<T>
{

	public CombinatoricsTesting
	(SpaceManager<T> manager, PowerLibrary<T> lib)
	{
		super (manager, lib);
	}

	public static void Htest (Combinatorics<Double> c)
	{
		System.out.println ("H(3) = " + c.H (3));
		System.out.println ();
	}

	public static void Htest
	(Combinatorics<Double> c, PowerLibrary<Double> lib)
	{
		double Hhalf = 2 - 2 * lib.ln (2d);
		System.out.println ("actual H(0.5) = " + Hhalf);
		System.out.println ("computed = " + c.H (0.5));
		System.out.println ();
	}

	public static void gammaTest (Combinatorics<Double> c)
	{
		System.out.println ("Gamma(5) = " + c.gamma (5d));
		System.out.println ();
	}

	public static void zetaTest (Combinatorics<Double> c)
	{
		//System.out.println ("zeta(1/2) approx -1.4603545");
		System.out.println ("zeta(2) approx 1.6449340668");
		System.out.println ("Zeta(2) = " + c.zeta (2d));
		System.out.println ("Zeta(-1) = " + c.zeta (-1d));
		//Apery's constant = 1.20205690315959428539
		System.out.println ("Apery's constant = 1.20205690315959428539");
		System.out.println ("Zeta(3) = " + c.zeta (3d));
		System.out.println ("zeta(4) approx 1.082323233");
		System.out.println ("Zeta(4) = " + c.zeta (4d));
		System.out.println ();
	}

	public static void gammaApprox (Combinatorics<Double> c)
	{
		int terms = 2000;
		double dx = 1.0 / terms, sum = 0;
		for (int i = 0; i < terms; i++)
		{
			sum = sum + c.H (i*dx) * dx;
		}
		System.out.println ("gamma = " + gamma);
		System.out.println ("gamma Approx = " + sum);
		System.out.println ();
	}


	// binomialCoefficient

	public static void bcTest (CombinatoricsTesting<Double> c)
	{
		System.out.println ("binomial");
		System.out.println (c.factorial(5.0));
		System.out.println (c.bc(5,3));
		System.out.println (c.bc(6,4));
		System.out.println (c.bc(6,3));
		System.out.println ();
	}
	public T bc (int n, int k) { return binomialCoefficient (manager.newScalar(n), manager.newScalar(k)); }


	// Bernoulli

	public static void bernoulliTest (Combinatorics<Double> c)
	{
		System.out.println ("first Bernoulli numbers");
		for (int i=1; i<20; i++) { System.out.print (c.B (i)); System.out.print ("  "); } System.out.println ();
		for (int i=1; i<20; i++) { System.out.print (c.B2 (i)); System.out.print ("  "); } System.out.println ();
		System.out.println (); 
	}

	//optimizedBernoulli

	public void testOptBernoulli ()
	{
		System.out.println ("===");
		for (int i=1; i<31; i++) System.out.println (bernoulli (i));

		System.out.println ("===");
		for (int i=1; i<31; i++) System.out.println (manager.toDecimalString (optimizedBernoulli (i)));

		System.out.println ("=== errors in float version");
		for (int i=1; i<31; i++) System.out.println (bernoulli (i) - manager.toNumber (optimizedBernoulli (i)).doubleValue());
		System.out.println ("===");
	}


	/**
	 * unit test
	 * @param args not used
	 */
	public static void main (String... args)
	{
		FactorizationFieldManager ffm = new FactorizationFieldManager ();
		net.myorb.math.expressions.controls.FactorizedEvaluationControl.initializeFactorizationTable();
		new CombinatoricsTesting<Factorization>(ffm, null).testOptBernoulli ();

		PowerLibrary<Double> lib = new HighSpeedMathLibrary ();
		CombinatoricsTesting<Double> c = new CombinatoricsTesting<Double>
		(new DoubleFloatingFieldManager (), lib);

		bcTest (c);
		zetaTest (c);
		bernoulliTest (c);

		gammaApprox (c);
		gammaTest (c);

		Htest (c, lib);
		Htest (c);
	}

}
