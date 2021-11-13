
package net.myorb.math.specialfunctions;

import net.myorb.math.primenumbers.FactorizationImplementation;

import net.myorb.math.PowerLibrary;
import net.myorb.math.SpaceManager;

import net.myorb.data.abstractions.SpaceConversion;

import java.math.BigInteger;

/**
 * implementation of generic Euler Product version of Zeta function
 * @param <T>  type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class EulerProduct<T> extends Zeta<T>
{


	/**
	 * @param manager a type manager for T
	 * @param powerLibrary an implementation of exp for type T
	 * @param conversion a conversion object for double to T
	 */
	public EulerProduct
		(
			SpaceManager<T> manager,
			PowerLibrary<T> powerLibrary,
			SpaceConversion<T> conversion
		)
	{
		super (manager, powerLibrary);
		this.conversion = conversion;
	}
	SpaceConversion<T> conversion;
	boolean useAltForm = true;


	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.Zeta#eval(java.lang.Object)
	 */
	public T eval (T parameter)
	{
		T one = manager.getOne ();
		T product = manager.getOne ();
		T negOne = manager.negate (one);
		T offset = useAltForm? negOne: one;
		T exp, factor;

		for (T log : logs)
		{
			exp = powerLibrary.exp (manager.multiply (parameter, log));

			if (useAltForm)
			{
				factor = manager.multiply (exp, manager.invert (manager.add (offset, exp)));
			}
			else
			{
				factor = manager.invert (manager.add (offset, manager.negate (exp)));
			}

			product = manager.multiply (product, factor);
		}

		return product;
	}
	// !! EulerZeta (s) = PI [0 <= n < LENGTH logP] ( 1 / ( 1 - exp (- s * logP#n) ) )

	/*
	 * alternate form uses:
	 * 
	 * z = exp ( s * logP#n )
	 * factor#n = z / ( z - 1 )
	 * zeta(s) = PI factor#n
	 * 
	 */

	/**
	 * @param tableSize size of factorization table to be made available
	 */
	public void initTable (int tableSize)
	{
		if (impl != null) return;
		impl = new FactorizationImplementation (tableSize);
		impl.initFactorizationsWithStats ();
	}
	static FactorizationImplementation impl = null;

	/**
	 * cache list of natural logarithms of primes
	 * @param hiPrime the maximum prime value to use
	 */
	public void configure (int hiPrime)
	{
		double logp;
		int count = 0;
		initTable (hiPrime);

		for (BigInteger prime : impl.getPrimesUpTo (hiPrime))
		{
			logp = Math.log (prime.doubleValue ());
			if ( ! useAltForm ) logp = - logp;
			logs.add (conversion.convertFromDouble (logp));
			count++;
		}

		System.out.println ("Euler Product initialized, " + count + " primes prepared as factors");
	}

}
