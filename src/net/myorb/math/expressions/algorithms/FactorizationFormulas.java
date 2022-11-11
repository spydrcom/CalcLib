
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.primenumbers.*;

import java.math.BigInteger;

import java.util.List;
import java.util.Map;

/**
 * implementation of prime factor abstractions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class FactorizationFormulas <T>
{

	/**
	 * get access to control object from central object store
	 * @param environment the central object store object
	 */
	public FactorizationFormulas (Environment <T> environment)
	{
		this.spaceManager = environment.getSpaceManager ();
		this.valueManager = environment.getValueManager ();
	}
	protected ExpressionSpaceManager <T> spaceManager;
	protected ValueManager <T> valueManager;


	/*
	 * array operators on prime numbers
	 */


	/**
	 * get array of primes
	 * @param parameters stack constructed parameter object
	 * @return an array of the primes
	 */
	public ValueManager.GenericValue primes (ValueManager.GenericValue parameters)
	{
		FactorizationManager.checkImplementation ();
		ValueManager.RawValueList<T> array = new ValueManager.RawValueList<T> ();
		int limit = valueManager.toInt (parameters, spaceManager);
		List<BigInteger> source = Factorization.getImplementation ().getPrimesUpTo (limit);
		for (BigInteger v : source) { array.add (spaceManager.newScalar (v.intValue ())); }
		return valueManager.newDimensionedValue (array);
	}


	/**
	 * get factors of integer value
	 * @param parameters stack constructed parameter object
	 * @return an array of the factors
	 */
	public ValueManager.GenericValue factors (ValueManager.GenericValue parameters)
	{
		int source = valueManager.toInt (parameters, spaceManager);
		Factorization f = FactorizationManager.forValue (source);
		Map<BigInteger,Integer> m = f.getFactors ().getFactorMap ();
		BigInteger[] primes = m.keySet ().toArray (new BigInteger[1]);
		ValueManager.RawValueList<T> array = new ValueManager.RawValueList<T> ();
		java.util.Arrays.sort (primes);

		for (BigInteger prime : primes)
		{
			int exp = m.get (prime);
			T p = spaceManager.newScalar (prime.intValue ());
			for (int i = 1; i <= exp; i++) array.add (p);
		}

		return valueManager.newDimensionedValue (array);
	}


	/**
	 * get GCF of integer values
	 * @param parameters stack constructed parameter object
	 * @return the computed result
	 */
	public ValueManager.GenericValue gcf (ValueManager.GenericValue parameters)
	{
		FactorizationManager.checkImplementation ();
		List <T> parameterList = valueManager.getDimensionedValue
				(parameters).getValues ();
		int left = spaceManager.toNumber (parameterList.get (0)).intValue (),
			right = spaceManager.toNumber (parameterList.get (1)).intValue ();
		Factorization x = FactorizationManager.forValue (left), y = FactorizationManager.forValue (right);
		T value = spaceManager.newScalar (Distribution.GCF (x, y).reduce ().intValue ());
		return valueManager.newDiscreteValue (value);
	}


	/**
	 * get LCM of integer values
	 * @param parameters stack constructed parameter object
	 * @return the computed result
	 */
	public ValueManager.GenericValue lcm (ValueManager.GenericValue parameters)
	{
		FactorizationManager.checkImplementation ();
		List <T> parameterList = valueManager.getDimensionedValue
				(parameters).getValues ();
		int left = spaceManager.toNumber (parameterList.get (0)).intValue (),
			right = spaceManager.toNumber (parameterList.get (1)).intValue ();
		Factorization x = FactorizationManager.forValue (left), y = FactorizationManager.forValue (right);
		T value = spaceManager.newScalar (Distribution.LCM (x, y).reduce ().intValue ());
		return valueManager.newDiscreteValue (value);
	}


}

