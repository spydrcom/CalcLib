
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.symbols.AbstractBinaryOperator;
import net.myorb.math.expressions.symbols.AbstractParameterizedFunction;
import net.myorb.math.expressions.symbols.AbstractUnaryPostfixOperator;

import net.myorb.math.expressions.evaluationstates.Environment;

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
		this.overrides = new FactorizationOverrides (environment);
		this.formulas = new PrimeFormulas (environment);
	}
	protected FactorizationOverrides overrides = null;
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
	 * implement operator - Rem %
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getRemOpAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				return formulas.rem (left, right);
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

	/**
	 * implement operator - DIVREM /%
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getDivRemOpAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				return formulas.divRem (left, right);
			}
		};
	}

	/**
	 * implement function - LCM
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getLcmAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{
				return formulas.lcm (parameters);
			}
		};
	}

	/**
	 * implement function - GCD
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getGcdAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{
				return formulas.gcd (parameters);
			}
		};
	}

	/**
	 * implement function - GCF
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getGcfAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{
				return formulas.gcf (parameters);
			}
		};
	}

	/**
	 * implement operator - Lsh <<
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getLshAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				return formulas.lsh (left, right);
			}
		};
	}

	/**
	 * implement operator - Rsh >>
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getRshAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				return formulas.rsh (left, right);
			}
		};
	}

	/**
	 * implement operator - POW ^
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getPowAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				return formulas.pow (left, right);
			}
		};
	}

	/**
	 * implement function - FLOOR
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getFloorAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return overrides.floor (parameter);
			}
		};
	}
	/**
	 * implement function - CEIL
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getCeilAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return overrides.ceil (parameter);
			}
		};
	}
	/**
	 * implement function - ROUND
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getRoundAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return overrides.round (parameter);
			}
		};
	}

}
