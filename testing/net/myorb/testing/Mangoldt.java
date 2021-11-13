
package net.myorb.testing;

import net.myorb.math.primenumbers.*;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;


public class Mangoldt
{

	static FactorizationImplementation impl;
	public static void main (String[] args)
	{
		impl = new FactorizationImplementation (5000);
		impl.initFactorizationsWithStats ();
		for (int i = 1; i < 5000; i++)
		{
			Factorization f = impl.factorsFor (i);

			FactorCollection fc = f.getFactors ();
			Map<BigInteger,Integer> factors = fc.getFactorMap ();
			Set<BigInteger> primes = factors.keySet ();
			if (primes.size() != 1) continue;

			BigInteger[] a = new BigInteger[1];
			primes.toArray (a);
			System.out.println ("value " + i + " = " + a[0] + "^" + factors.get(a[0]));
		}
	}

}
