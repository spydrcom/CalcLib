
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.symbols.AbstractParameterizedFunction;
import net.myorb.math.expressions.symbols.AbstractUnaryPostfixOperator;
//import net.myorb.math.expressions.symbols.AbstractUnaryOperator;

import net.myorb.math.primenumbers.Factorization;

import net.myorb.math.expressions.ValueManager;

/**
 * implementations of algorithms specific to prime number functions
 * @author Michael Druckman
 */
public class PrimePrimitives extends FactorizationPrimitives
{

	public PrimePrimitives (Environment <Factorization> environment)
	{
		super (environment);
		this.formulas = new PrimeFormulas (environment);
	}
	protected PrimeFormulas formulas = null;

	/**
	 * implement unary operator - PRIMORIAL
	 * @param symbol the symbol associated with this object
	 * @param precedence the precedence of the operation
	 * @return operation implementation object
	 */
	public AbstractUnaryPostfixOperator getPrimorialAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryPostfixOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return formulas.primorial (parameter);
			}
		};
	}

	/**
	 * implement function - MOD
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getModAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{
				return formulas.mod (parameters);
			}
		};
	}

	/**
	 * implement function - MODPOW
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getModPowAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{
				return formulas.modPow (parameters);
			}
		};
	}

	/**
	 * implement function - REM
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getRemAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{
				return formulas.rem (parameters);
			}
		};
	}

	/**
	 * implement function - DIVREM
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getDivRemAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{
				return formulas.divRem (parameters);
			}
		};
	}

}
