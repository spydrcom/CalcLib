
package net.myorb.math.expressions.algorithms;

import net.myorb.math.primenumbers.Factorization;
import net.myorb.math.primenumbers.FactorAdjustment;
import net.myorb.math.primenumbers.FactorizationPrimitives;

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

	public PrimeFormulas (Environment <Factorization> environment)
	{
		super (environment);
		this.factoredMgr  = (ExpressionFactorizedFieldManager) spaceManager;
		this.helpers = new FactorizationPrimitives (environment);
	}
	protected ExpressionFactorizedFieldManager factoredMgr;
	protected FactorizationPrimitives helpers;

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
		return helpers.bundle (product);
	}

	/**
	 * MOD function
	 * @param values the parameter passed to the function (must be integers)
	 * @return left modulus right
	 */
	public ValueManager.GenericValue mod (ValueManager.GenericValue values)
	{
		return helpers.process (values, (x,y) -> x.mod (y));
	}

	/**
	 * MODPOW function
	 * @param values the parameters passed to the function  (must be integers)
	 * @return (p1 ^ p2) modulus p3
	 */
	public ValueManager.GenericValue modPow (ValueManager.GenericValue values)
	{
		BigInteger [] p = helpers.extract (values);
		return helpers.bundle (p [0].modPow (p [1], p [2]));
	}

	/**
	 * REM function
	 * @param values the parameter passed to the function (must be integers)
	 * @return rem(p1,p2)
	 */
	public ValueManager.GenericValue rem (ValueManager.GenericValue values)
	{
		return helpers.process (values, (x,y) -> x.remainder (y));
	}

	/**
	 * REM operator /%
	 * @param left parameter left of operator
	 * @param right parameter right of operator
	 * @return array of left%right
	 */
	public ValueManager.GenericValue rem
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		Factorization
			lF = valueManager.toDiscrete (left),
			rF = valueManager.toDiscrete (right);
		BigInteger lI = lF.reduce (), rI = rF.reduce ();
		return helpers.bundle (lI.remainder (rI));
	}

	/**
	 * DIVREM function
	 * @param values the parameter passed to the function (must be integers)
	 * @return array of left/right and left%right
	 */
	public ValueManager.GenericValue divRem (ValueManager.GenericValue values)
	{
		BigInteger [] p = helpers.extract (values);
		return helpers.bundle (p [0].divideAndRemainder (p [1]));
	}

	/**
	 * DIVREM function /%
	 * @param left parameter left of operator
	 * @param right parameter right of operator
	 * @return array of left/right and left%right
	 */
	public ValueManager.GenericValue divRem
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		Factorization
			lF = valueManager.toDiscrete (left),
			rF = valueManager.toDiscrete (right);
		BigInteger lI = lF.reduce (), rI = rF.reduce ();
		return helpers.bundle (lI.divideAndRemainder (rI));
	}

	/**
	 * LSH operator <<
	 * @param left parameter left of operator
	 * @param right parameter right of operator
	 * @return array of left%right
	 */
	public ValueManager.GenericValue lsh
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		Factorization
			lF = valueManager.toDiscrete (left),
			rF = valueManager.toDiscrete (right);
		BigInteger lI = lF.reduce (), rI = rF.reduce ();
		return helpers.bundle (lI.shiftLeft (rI.intValue ()));
	}

	/**
	 * RSH operator >>
	 * @param left parameter left of operator
	 * @param right parameter right of operator
	 * @return array of left%right
	 */
	public ValueManager.GenericValue rsh
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		Factorization
			lF = valueManager.toDiscrete (left),
			rF = valueManager.toDiscrete (right);
		BigInteger lI = lF.reduce (), rI = rF.reduce ();
		return helpers.bundle (lI.shiftRight (rI.intValue ()));
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
		Factorization
			lF = valueManager.toDiscrete (left),
			rF = valueManager.toDiscrete (right);
		BigInteger lI = lF.reduce (), rI = rF.reduce ();
		return helpers.bundle (lI.pow (rI.intValue ()));
	}

	/**
	 * GCD function
	 * @param left parameter left of operator
	 * @param right parameter right of operator
	 * @return array of left%right
	 */
	public ValueManager.GenericValue gcd (ValueManager.GenericValue values)
	{
		BigInteger [] p = helpers.extract (values);
		return helpers.bundle (p [0].gcd (p [1]));
	}

	/**
	 * apply factor adjustment algorithms
	 * @param parameter the parameter from the request
	 * @return the computed results
	 */
	public ValueManager.GenericValue fudge (ValueManager.GenericValue parameter)
	{
		Factorization f = valueManager.toDiscrete (parameter);
		Factorization result = new FactorAdjustment ().substituteAndAnalyze (f);
		return valueManager.newDiscreteValue (result);
	}

	/**
	 * compute Nth prime number
	 * @param parameter the parameter from the request
	 * @return the computed results
	 */
	public ValueManager.GenericValue Pn (ValueManager.GenericValue parameter)
	{
		BigInteger result = BigInteger.ONE;
		Factorization n = valueManager.toDiscrete (parameter);
		if (n != null)
		{
			result =
				Factorization.getImplementation ()
					.getNthPrime (n.reduce ().intValue ());
		}
		return helpers.bundle (result);
	}

}

