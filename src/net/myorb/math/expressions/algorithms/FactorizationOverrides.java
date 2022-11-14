
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.primenumbers.FactorizationPrimitives;
import net.myorb.math.primenumbers.*;

import java.math.BigInteger;

import java.util.List;

/**
 * implementations of algorithms specific to factored values
 * @author Michael Druckman
 */
public class FactorizationOverrides extends AlgorithmCore <Factorization>
{


	/**
	 * get LCM of integer values
	 * @param parameters stack constructed parameter object
	 * @return the computed result
	 */
	public ValueManager.GenericValue lcm (ValueManager.GenericValue parameters)
	{
		List <Factorization> parameterList =
			valueManager.getDimensionedValue (parameters).getValues ();
		Factorization left = parameterList.get (0), right = parameterList.get (1);
		Factorization value = Distribution.LCM (left, right);
		return valueManager.newDiscreteValue (value);
	}


	/**
	 * get GCF of integer values
	 * @param parameters stack constructed parameter object
	 * @return the computed result
	 */
	public ValueManager.GenericValue gcf (ValueManager.GenericValue parameters)
	{
		List <Factorization> parameterList =
			valueManager.getDimensionedValue (parameters).getValues ();
		Factorization left = parameterList.get (0), right = parameterList.get (1);
		Factorization value = Distribution.GCF (left, right);
		return valueManager.newDiscreteValue (value);
	}


	/**
	 * get floor of factored value
	 * @param parameter stack constructed parameter object
	 * @return the computed result
	 */
	public ValueManager.GenericValue floor (ValueManager.GenericValue parameter)
	{
		return helpers.process ( parameter, (x, y) -> floor (x, y) );
	}
	BigInteger floor (BigInteger num, BigInteger den)
	{
		return num.divideAndRemainder (den) [0];
	}


	/**
	 * get ceiling of factored value
	 * @param parameter stack constructed parameter object
	 * @return the computed result
	 */
	public ValueManager.GenericValue ceil (ValueManager.GenericValue parameter)
	{
		return helpers.process ( parameter, (x, y) -> ceil (x, y) );
	}
	BigInteger ceil (BigInteger num, BigInteger den)
	{
		BigInteger [] frac = num.divideAndRemainder (den);
		if (frac [1].compareTo (BigInteger.ZERO) == 0) return frac [0];
		return frac [0].add (BigInteger.ONE);
	}


	/**
	 * get rounded result of factored value
	 * @param parameter stack constructed parameter object
	 * @return the computed result
	 */
	public ValueManager.GenericValue round (ValueManager.GenericValue parameter)
	{
		return helpers.process ( parameter, (x, y) -> round (x, y) );
	}
	BigInteger round (BigInteger num, BigInteger den)
	{
		BigInteger [] frac = num.divideAndRemainder (den);
		if (frac [1].compareTo (BigInteger.ZERO) == 0) return frac [0];
		BigInteger adjusted = frac [1].multiply (BigInteger.valueOf (2));
		if (adjusted.compareTo (den) >= 0) return frac [0].add (BigInteger.ONE);
		return frac [0];
	}


	/**
	 * POW operator ^
	 * @param left parameter left of operator
	 * @param right parameter right of operator
	 * @return array of left^right
	 */
	public ValueManager.GenericValue pow
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		return helpers.processFactoredBinary ( left, right, (l, r) -> functions.pow (l, r) );
	}


	public FactorizationOverrides (Environment <Factorization> environment)
	{
		super (environment);
		this.abstractions = new PrimeFormulas (environment);
		this.functions = new FactorizationSpecificFunctions (environment);
		this.helpers = this.functions;
	}
	protected FactorizationSpecificFunctions functions = null;
	protected FactorizationPrimitives helpers = null;
	protected PrimeFormulas abstractions = null;


}

