
package net.myorb.math.expressions.algorithms;

import net.myorb.math.primenumbers.Factorization;

import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.ValueManager;

import java.math.BigInteger;
import java.util.*;

/**
 * implementation of operations specific to prime numbers
 * @author Michael Druckman
 */
public class PrimeFormulas extends FactorizationFormulas <Factorization>
{

	/**
	 * describe profile of BIG operators
	 */
	public interface BigOp
	{
		/**
		 * binary function profile
		 * @param left the parameter left of operator
		 * @param right the parameter right of operator
		 * @return the computed result
		 */
		BigInteger op (BigInteger left, BigInteger right);
	}

	/**
	 * pull values from a bundled parameter list
	 * @param values the GenericValue holding parameters
	 * @return the parameters as an array
	 */
	public BigInteger [] extract (ValueManager.GenericValue values)
	{
		@SuppressWarnings("unchecked")
		ValueManager.DimensionedValue <Factorization>
			parameterList = (ValueManager.DimensionedValue <Factorization>) values;
		BigInteger [] integerValues = new BigInteger [parameterList.getValues ().size ()];
		for (int i = 0; i < integerValues.length; i++) integerValues [i] = parameterList.getValues ().get (i).reduce ();
		return integerValues;
	}

	/**
	 * process a binary operation
	 * @param values the parameters to the operator
	 * @param formula the formula to apply
	 * @return the computed result
	 */
	public ValueManager.GenericValue process (ValueManager.GenericValue values, BigOp formula)
	{
		BigInteger [] p = extract (values);
		Factorization result = factoredMgr.bigScalar
			(formula.op (p [0], p [1]));
		return valueManager.newDiscreteValue (result);
	}

	/**
	 * compute primorial of parameter - #
	 * @param value the parameter passed to the function (must be integer)
	 * @return calculated primorial
	 */
	public ValueManager.GenericValue primorial (ValueManager.GenericValue value)
	{
		Factorization parm = valueManager.toDiscrete (value);
		BigInteger product = BigInteger.ONE; int n = spaceManager.toNumber (parm).intValue ();
		List <BigInteger> primes = Factorization.getImplementation ().getPrimesUpTo (n);
		for (BigInteger factor : primes) product = product.multiply (factor);
		Factorization result = factoredMgr.bigScalar (product);
		return valueManager.newDiscreteValue (result);
	}

	/**
	 * MOD function
	 * @param values the parameter passed to the function (must be integers)
	 * @return left modulus right
	 */
	public ValueManager.GenericValue mod (ValueManager.GenericValue values)
	{
		return process (values, (x,y) -> x.mod (y));
	}

	/**
	 * MODPOW function
	 * @param values the parameters passed to the function  (must be integers)
	 * @return (p1 ^ p2) modulus p3
	 */
	public ValueManager.GenericValue modPow (ValueManager.GenericValue values)
	{
		BigInteger [] p = extract (values);
		BigInteger exp = p [0].modPow (p [1], p [2]);
		Factorization result = factoredMgr.bigScalar (exp);
		return valueManager.newDiscreteValue (result);
	}

	/**
	 * REM operator - %
	 * @param values the parameter passed to the function (must be integers)
	 * @return left%right
	 */
	public ValueManager.GenericValue rem (ValueManager.GenericValue values)
	{
		return process (values, (x,y) -> x.remainder (y));
	}

	/**
	 * DIVREM function
	 * @param values the parameter passed to the function (must be integers)
	 * @return array of left/right and left%right
	 */
	public ValueManager.GenericValue divRem (ValueManager.GenericValue values)
	{
		BigInteger [] p = extract (values);
		BigInteger [] results = p [0].divideAndRemainder (p [1]);
		List <Factorization> computed = new ArrayList <> ();
		computed.add (factoredMgr.bigScalar (results [0]));
		computed.add (factoredMgr.bigScalar (results [1]));
		return valueManager.newDimensionedValue (computed);
	}

	public PrimeFormulas (Environment <Factorization> environment)
	{ super (environment); this.factoredMgr  = (ExpressionFactorizedFieldManager) spaceManager; }
	protected ExpressionFactorizedFieldManager factoredMgr;

}
