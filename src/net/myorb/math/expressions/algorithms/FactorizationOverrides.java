
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.primenumbers.*;

import java.math.BigInteger;

import java.util.List;

/**
 * implementations of algorithms specific to factored values
 * @author Michael Druckman
 */
public class FactorizationOverrides extends AlgorithmCore <Factorization>
{

	public FactorizationOverrides (Environment <Factorization> environment)
	{ super (environment); this.abstractions = new PrimeFormulas (environment); }
	protected PrimeFormulas abstractions = null;

//	protected ValueManager <Factorization> valueManager; // in super
//	protected Environment <Factorization> environment;  // in super
//	protected ExpressionSpaceManager<T> spaceManager;  // in super

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
	 * apply algorithm to fraction
	 * @param parameter the value being processed
	 * @param formula the operation to apply to the value
	 * @return the computed result
	 */
	ValueManager.GenericValue process
	(ValueManager.GenericValue parameter, PrimeFormulas.BigOp formula)
	{
		Distribution fraction = Factorization.normalize
			(valueManager.toDiscrete (parameter), spaceManager);
		Distribution.normalize (fraction);
		return process (fraction, formula);
	}
	ValueManager.GenericValue process
	(Distribution fraction, PrimeFormulas.BigOp formula)
	{
		BigInteger
			num = fraction.getNumerator ().reduce (),
			den = fraction.getDenominator ().reduce ();
		return abstractions.bundle (formula.op (num, den));
	}


	/**
	 * get floor of factored value
	 * @param parameter stack constructed parameter object
	 * @return the computed result
	 */
	public ValueManager.GenericValue floor (ValueManager.GenericValue parameter)
	{
		return process (parameter, (x,y) -> floor (x,y));
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
		return process (parameter, (x,y) -> ceil (x,y));
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
		return process (parameter, (x,y) -> round (x,y));
	}
	BigInteger round (BigInteger num, BigInteger den)
	{
		BigInteger [] frac = num.divideAndRemainder (den);
		if (frac [1].compareTo (BigInteger.ZERO) == 0) return frac [0];
		BigInteger adjusted = frac [1].multiply (BigInteger.valueOf (2));
		if (adjusted.compareTo (den) >= 0) return frac [0].add (BigInteger.ONE);
		return frac [0];
	}


}

