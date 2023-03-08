
package net.myorb.math.expressions.algorithms;

import net.myorb.math.primenumbers.Factorization;
import net.myorb.math.primenumbers.FactorAdjustment;
import net.myorb.math.primenumbers.FactorizationPrimitives;
import net.myorb.math.primenumbers.FactorizationSpecificFunctions;

import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.ValueManager;

import java.math.BigInteger;

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
		this.functions = new FactorizationSpecificFunctions (environment);
		this.helpers = this.functions;
	}
	protected ExpressionFactorizedFieldManager factoredMgr;
	protected FactorizationSpecificFunctions functions;
	protected FactorizationPrimitives helpers;


	/*
	 * provide helper access to primitives layer
	 */

	public ValueManager.GenericValue compute
	(ValueManager.GenericValue value, FactorizationPrimitives.UnaryFactoredOp formula)
	{
		return helpers.processFactoredUnary (value, formula);
	}

	public ValueManager.GenericValue compute
		(
			ValueManager.GenericValue left, ValueManager.GenericValue right,
			FactorizationPrimitives.BinaryFactoredOp formula
		)
	{
		return helpers.processFactoredBinary (left, right, formula);
	}


	/*
	 * helper for function based on operations implemented with BigInteger
	 */


	/**
	 * MOD function
	 * @param values the parameter passed to the function (must be integers)
	 * @return left modulus right
	 */
	public ValueManager.GenericValue mod (ValueManager.GenericValue values)
	{
		return helpers.process ( values, (x, y) -> x.mod (y) );
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
		return helpers.process (values, (x, y) -> x.remainder (y));
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
		return helpers.process ( left, right, (x, y) -> x.remainder (y) );
	}

	/**
	 * DIVREM function
	 * @param values the parameter passed to the function (must be integers)
	 * @return array of left/right and left%right
	 */
	public ValueManager.GenericValue divRem (ValueManager.GenericValue values)
	{
		return helpers.processArray ( values, (x, y) -> x.divideAndRemainder (y) );
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
		return helpers.processArray ( left, right, (x, y) -> x.divideAndRemainder (y) );
	}

	/**
	 * LSH operator
	 * @param left parameter left of operator
	 * @param right parameter right of operator
	 * @return array of left%right
	 */
	public ValueManager.GenericValue lsh
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		return helpers.process ( left, right, (x, y) -> x.shiftLeft (y.intValue ()) );
	}

	/**
	 * RSH operator
	 * @param left parameter left of operator
	 * @param right parameter right of operator
	 * @return array of left%right
	 */
	public ValueManager.GenericValue rsh
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		return helpers.process ( left, right, (x, y) -> x.shiftRight (y.intValue ()) );
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
		return helpers.process ( left, right, (x, y) -> x.pow (y.intValue ()) );
	}

	/**
	 * GCD function
	 * @param values parameters for the operator
	 * @return array of left%right
	 */
	public ValueManager.GenericValue gcd (ValueManager.GenericValue values)
	{
		return helpers.process ( values, (x, y) -> x.gcd (y) );
	}

	/**
	 * apply factor adjustment algorithms
	 * @param parameter the parameter from the request
	 * @return the computed results
	 */
	public ValueManager.GenericValue fudge (ValueManager.GenericValue parameter)
	{
		return helpers.processFactoredUnary ( parameter, (x) -> new FactorAdjustment ().substituteAndAnalyze (x) );
	}

	/**
	 * compute PI function
	 * @param parameter the parameter from the request
	 * @return the computed result
	 */
	public ValueManager.GenericValue PIF (ValueManager.GenericValue parameter)
	{
		return helpers.processUnary (parameter, functions.getPiOp ());
	}

	/**
	 * compute xLx function
	 * @param parameter the parameter from the request
	 * @return the computed result
	 */
	public ValueManager.GenericValue xLx (ValueManager.GenericValue parameter)
	{
		return helpers.processUnary (parameter, functions.getXlxOp ());
	}

	/**
	 * compute li function
	 * @param parameter the parameter from the request
	 * @return the computed result
	 */
	public ValueManager.GenericValue li (ValueManager.GenericValue parameter)
	{
		return helpers.processUnary (parameter, functions.getLiOp ());
	}

	/**
	 * compute T function
	 * @param parameter the parameter from the request
	 * @return the computed result
	 */
	public ValueManager.GenericValue T (ValueManager.GenericValue parameter)
	{
		return helpers.processUnary (parameter, functions.getTOp ());
	}

	/**
	 * compute Nth prime number
	 * @param parameter the parameter from the request
	 * @return the computed result
	 */
	public ValueManager.GenericValue Pn (ValueManager.GenericValue parameter)
	{
		return helpers.processUnary (parameter, functions.getNthPrimeOp ());
	}

	/**
	 * compute Mobius count
	 * @param parameter the parameter from the request
	 * @return the computed result
	 */
	public ValueManager.GenericValue mu (ValueManager.GenericValue parameter)
	{
		return helpers.processUnary (parameter, functions.getMobiusPrimeOp ());
	}

	/**
	 * compute OMEGA count
	 * @param parameter the parameter from the request
	 * @return the computed result
	 */
	public ValueManager.GenericValue OMEGA (ValueManager.GenericValue parameter)
	{
		return helpers.processUnary (parameter, functions.getOmegaMultiplicityFunctionOp ());
	}

	/**
	 * compute omega count
	 * @param parameter the parameter from the request
	 * @return the computed result
	 */
	public ValueManager.GenericValue omega (ValueManager.GenericValue parameter)
	{
		return helpers.processUnary (parameter, functions.getOmegaFunctionOp ());
	}

	/**
	 * compute lambda count
	 * @param parameter the parameter from the request
	 * @return the computed result
	 */
	public ValueManager.GenericValue lambda (ValueManager.GenericValue parameter)
	{
		return helpers.processUnary (parameter, functions.getLambdaFunctionOp ());
	}

}

