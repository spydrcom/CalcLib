
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.symbols.AbstractBinaryOperator;
import net.myorb.math.expressions.symbols.AbstractParameterizedFunction;
import net.myorb.math.expressions.symbols.AbstractUnaryOperator;
import net.myorb.math.expressions.symbols.AbstractUnaryPostfixOperator;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.MathMarkupNodes;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.specialfunctions.PochhammerSymbol;
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
	 * implement unary operator - SUBFACTORIAL
	 * @param symbol the symbol associated with this object
	 * @param precedence the precedence of the operation
	 * @return operation implementation object
	 */
	public AbstractUnaryPostfixOperator getDerangementsAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryPostfixOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				Factorization value = valueManager.toDiscrete (parameter);
				Factorization computed = combinatorics.derangementsCount (value);
				return valueManager.newDiscreteValue (computed);
			}

			public String markupForDisplay
			(String operand, boolean fenceOperand, String operator, NodeFormatting using)
			{
				String parm = using.formatParenthetical (operand, fenceOperand);
				return MathMarkupNodes.space ("5") + using.formatOperatorReference ("!") + MathMarkupNodes.space ("2") + parm;
			}
		};
	}

	/**
	 * implement operator - Pochhammer raising
	 * @param symbol the symbol associated with this object
	 * @param precedence the precedence of the operation
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getPochAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			PochhammerSymbol p = new PochhammerSymbol (spaceManager, powerLibrary);

			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				Factorization leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
				return valueManager.newDiscreteValue (p.eval (leftDiscrete, rightDiscrete));
			}

			public String markupForDisplay
			(String operator, String firstOperand, String secondOperand, boolean lfence, boolean rfence, NodeFormatting using)
			{ return using.formatPochhammerRising (firstOperand, secondOperand); }
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
	 * @param precedence the associated precedence
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
	 * @param precedence the associated precedence
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
	 * implement operator - Lsh
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
	 * implement operator - Rsh
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
				return overrides.pow (left, right);
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

	/**
	 * implement function - PIF
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getPIFAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{

			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return formulas.PIF (parameter);
			}

			public String markupForDisplay (String operator, String parameters, NodeFormatting using)
			{
				return using.formatIdentifierReference ("PI") + parameters;
			}
		};
	}

	/**
	 * implement operator - Pn
	 * @param symbol the symbol associated
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getPrimeNAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return formulas.Pn (parameter);
			}

			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{
				return using.formatIdentifierReference ("P_n") + using.formatParenthetical (operand);
			}
		};
	}

	/**
	 * implement operator - PI function
	 * @param symbol the symbol associated
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getPIFunctionAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return formulas.PIF (parameter);
			}

			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{
				return CommonOperatorLibrary.formatIdentifierFor ("pi", using) + using.formatParenthetical (operand);
			}
		};
	}

	/**
	 * implement operator - Stirling Numbers
	 * @param symbol the symbol associated with this object
	 * @param precedence the precedence of the operation
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getSNAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				Factorization n = valueManager.toDiscrete (left), k = valueManager.toDiscrete (right);
				Factorization result = combinatorics.stirlingNumbers1 (n, k);
				return valueManager.newDiscreteValue (result);
			}

			public String markupForDisplay
			(String operator, String firstOperand, String secondOperand, boolean lfence, boolean rfence, NodeFormatting using)
			{ return using.formatBracketed  (firstOperand, secondOperand, NodeFormatting.Bractets.SQUARE); }
		};
	}

	/**
	 * implement operator - Stirling Numbers (second)
	 * @param symbol the symbol associated with this object
	 * @param precedence the precedence of the operation
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getSNSAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				Factorization n = valueManager.toDiscrete (left), k = valueManager.toDiscrete (right);
				Factorization result = combinatorics.stirlingNumbers2 (n, k);
				return valueManager.newDiscreteValue (result);
			}

			public String markupForDisplay
			(String operator, String firstOperand, String secondOperand, boolean lfence, boolean rfence, NodeFormatting using)
			{ return using.formatBracketed  (firstOperand, secondOperand, NodeFormatting.Bractets.CURLY); }
		};
	}
	
}
