
package net.myorb.math.expressions.controls;

import net.myorb.math.primenumbers.*;

public class TestFactorizedControlObject
extends FactorizedEvaluationControl
{


	public void primesTest ()
	{

		execute ("prim = PRIMES (100)");
		execute ("fact = FACTORS (123456)");
		execute ("pifact = PI fact");

		execute ("gval = gcf (18, 27)");
		execute ("lval = lcm (18, 27)");

		execute ("SHOW Symbols");

	}

	public static void main(String[] args)
	{

		initializeFactorizationTable ();

		//TestFactorizedControlObject t =
			new TestFactorizedControlObject ();

		//t.primesTest ();

//		t.rootTest ("2", "1.4");
//		t.rootTest ("3", "17/10");
//		t.rootTest ("5", "2");
//		t.rootTest ("17", "17/5");
//		t.rootTest ("100", "9");
//		t.rootTest ("25", "6");
//		t.rootTest ("10000", "101");

	}

	public void rootTest (String parm, String approx) throws Exception
	{
		System.out.println ();
		System.out.println ("===");
		System.out.println ("=  New Test (" + parm + ", " + approx + ")");
		System.out.println ("===");
		System.out.println ();

		execute ("parm = " + parm);
		execute ("approx = " + approx);

		execute ("f = (-parm, 0, 1)");
		execute ("fPrime = POLYDER f");

		execute ("limit = 1 / 1000^5");
		Factorization limit = lookup ("limit");

		int i;
		for (i = 1; i <= 20; i++)
		{
			Factorization error = iterate (i);
			if (environment.getSpaceManager ().lessThan (error, limit)) break;
		}

		System.out.println ();
		System.out.println ("Test completed after " + i + " iterations");
		System.out.println ();
	}

	public Factorization iterate (int i) throws Exception
	{
		execute ("iter = " + i);
		execute ("y = f +*^ approx");
		execute ("yPrime = fPrime +*^ approx");
		execute ("approx = approx - y/yPrime");
		execute ("error = f +*^ approx");
		execute ("PRETTYPRINT approx");
		execute ("SHOW Symbols");
		return lookup ("error");
	}

}
