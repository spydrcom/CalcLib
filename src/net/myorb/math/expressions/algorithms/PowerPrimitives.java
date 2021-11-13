
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.symbols.AbstractUnaryOperator;
import net.myorb.math.expressions.symbols.AbstractParameterizedFunction;
import net.myorb.math.expressions.symbols.AbstractUnaryPostfixOperator;
import net.myorb.math.expressions.symbols.AbstractBinaryOperator;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.ExtendedPowerLibrary;

import java.util.List;

/**
 * implementations of algorithms computing power operations
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class PowerPrimitives<T> extends AlgorithmCore<T>
{


	/**
	 * object depends on data structures of the environment
	 * @param environment the environment object holding value management objects
	 */
	public PowerPrimitives (Environment<T> environment)
	{
		super (environment); this.powerLibrary = environment.getLibrary ();
	}
	protected ExtendedPowerLibrary<T> powerLibrary;


	/*
	 * operator and function algorithm implementations
	 */


	/**
	 * implement operator - sqrt
	 * @param symbol the symbol associated
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getSqrtAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{ return valueManager.newDiscreteValue (powerLibrary.sqrt (valueManager.toDiscrete (parameter))); }

			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{ return using.formatSqrtOperation (operand); }
		};
	}


	/**
	 * implement operator - exp
	 * @param symbol the symbol associated
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getExpAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return valueManager.newDiscreteValue (powerLibrary.exp (valueManager.toDiscrete (parameter)));
			}
		};
	}


	/**
	 * implement operator - ln
	 * @param symbol the symbol associated
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getLogAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return valueManager.newDiscreteValue (powerLibrary.ln (valueManager.toDiscrete (parameter)));
			}
		};
	}


	/**
	 * implement operator - !
	 * @param symbol the symbol associated
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryPostfixOperator getFactorialAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryPostfixOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return valueManager.newDiscreteValue (powerLibrary.factorial (valueManager.toDiscrete (parameter)));
			}
		};
	}


	/**
	 * implement operator - !!
	 * @param symbol the symbol associated
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryPostfixOperator getDFactorialAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryPostfixOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return valueManager.newDiscreteValue (powerLibrary.dFactorial (valueManager.toDiscrete (parameter)));
			}
		};
	}


	/**
	 * implement operator - **
	 * @param symbol the symbol associated
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getExponentiationAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				return valueManager.newDiscreteValue (computeXtoY (valueManager.toDiscrete (left), valueManager.toDiscrete (right), powerLibrary));
			}

			public String markupForDisplay
			(String operator, String firstOperand, String secondOperand, boolean lfence, boolean rfence, NodeFormatting using)
			{
				String left = using.formatParenthetical (firstOperand, lfence), right = using.formatParenthetical (secondOperand, rfence);
				return using.formatSuperScript (left, right);
			}
		};
	}


	/**
	 * implement operator - POW
	 * @param symbol the symbol associated
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getPowAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				DimensionedDataSupport<T>
					support = getDimensionedDataSupport ();
				if (support.dimensionedParameterPresent (left, right))
					return support.dimensionedPow (left, right);
				else return power (left, right, powerLibrary);
			}

			public String markupForDisplay
			(String operator, String firstOperand, String secondOperand, boolean lfence, boolean rfence, NodeFormatting using)
			{
				String base = using.formatParenthetical (firstOperand, lfence);
				return using.formatSuperScript (base, secondOperand);
			}
		};
	}


	/**
	 * implement operator - LSH
	 * @param symbol the symbol associated
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getLshAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				T leftDiscrete = valueManager.toDiscrete (left),
					rightDiscrete = valueManager.toDiscrete (right);
				Integer rightInteger = spaceManager.convertToInteger (rightDiscrete);
				T factor = powerLibrary.pow (spaceManager.newScalar (2), rightInteger);
				return valueManager.newDiscreteValue (spaceManager.multiply (leftDiscrete, factor));
			}
		};
	}


	/**
	 * implement operator - RSH
	 * @param symbol the symbol associated
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getRshAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				T leftDiscrete = valueManager.toDiscrete (left),
					rightDiscrete = valueManager.toDiscrete (right);
				Integer rightInteger = spaceManager.convertToInteger (rightDiscrete);
				T factor = powerLibrary.pow (spaceManager.newScalar (2), -rightInteger);
				return valueManager.newDiscreteValue (spaceManager.multiply (leftDiscrete, factor));
			}
		};
	}


	/**
	 * implement operator - \
	 * @param symbol the symbol associated
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getRootAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				return root (left, right);
			}

			public String markupForDisplay
			(String operator, String firstOperand, String secondOperand, boolean lfence, boolean rfence, NodeFormatting using)
			{
				return using.formatRootOperation (secondOperand, firstOperand);
			}
		};
	}


	/**
	 * implement operator - *\
	 * @param symbol the symbol associated
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getRadicalAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				T l = valueManager.toDiscrete (left), r = valueManager.toDiscrete (right);
				//T product = spaceManager.multiply (l, new Primitives ().nThRoot (r, 2));
				T product = spaceManager.multiply (l, powerLibrary.nThRoot (r, 2));
				return valueManager.newDiscreteValue (product);
			}

			public String markupForDisplay
			(String operator, String firstOperand, String secondOperand, boolean lfence, boolean rfence, NodeFormatting using)
			{
				return using.formatBinaryOperation (firstOperand, "\u2062", using.formatSqrtOperation (secondOperand));
			}
		};
	}


	/**
	 * implement operator - REM
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getRemAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				Integer leftDiscrete = spaceManager.convertToInteger (valueManager.toDiscrete (left)),
							rightDiscrete = spaceManager.convertToInteger (valueManager.toDiscrete (right));
				if (leftDiscrete == null || rightDiscrete == null) throw new RuntimeException ("REM requested for non-integer data");
				return valueManager.newDiscreteValue (spaceManager.newScalar (leftDiscrete % rightDiscrete));
			}
		};
	}


	/**
	 * implement function - *10^
	 * @param symbol the symbol associated with this object
	 * @return function implementation object
	 */
	public AbstractParameterizedFunction getDshAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{
				ValueManager.ValueList parameterList = (ValueManager.ValueList)parameters;
				List<ValueManager.GenericValue> parameterListValues = parameterList.getValues();
				int exponent = valueManager.toInt (parameterListValues.get (0), spaceManager);
				ValueManager.GenericValue pl1 = parameterListValues.get (1), pl2 = parameterListValues.get (2);
				T radix = valueManager.toDiscrete (pl1), value = valueManager.toDiscrete (pl2);
				T result = spaceManager.multiply (value, powerLibrary.pow (radix, exponent));
				return valueManager.newDiscreteValue (result);
			}
		};
	}


	/**
	 * implement function - HYPOT
	 * @param symbol the symbol associated
	 * @return function implementation object
	 */
	public AbstractParameterizedFunction getHypotAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return hypot (parameters, powerLibrary); }
		};
	}


	/**
	 * use power library to
	 *  compute x^(1/y) = exp (ln (x) / y)
	 * @param x the base of the requested computation
	 * @param y the exponent (inverted) of the requested computation
	 * @param powerLibrary the library to be used
	 * @return x^(1/y) (the y root)
	 */
	public T computeXrootY (T x, T y, final ExtendedPowerLibrary<T> powerLibrary)
	{
		return computeXtoY (x, spaceManager.invert (y), powerLibrary);
	}


	/**
	 * use power library to
	 *  compute x^y = exp (y * ln (x))
	 * @param x the base of the requested computation
	 * @param y the exponent of the requested computation
	 * @param powerLibrary the library to be used
	 * @return x^y
	 */
	public T computeXtoY (T x, T y, final ExtendedPowerLibrary<T> powerLibrary)
	{
		return powerLibrary.exp ( spaceManager.multiply ( y, powerLibrary.ln (x) ) );
	}


	/**
	 * compute left^right
	 *  appropriately relative to data type present
	 * @param left the base of the exponentiation operator
	 * @param right the exponent of the exponentiation operator
	 * @param powerLibrary the library to be used
	 * @return left^right
	 */
	public ValueManager.GenericValue power
	(ValueManager.GenericValue left, ValueManager.GenericValue right, ExtendedPowerLibrary<T> powerLibrary)
	{
		T base = valueManager.toDiscrete (left), exponent, result;
		Integer iExponent = spaceManager.convertToInteger (exponent = valueManager.toDiscrete (right));
		if (iExponent != null) result = powerLibrary.pow (base, iExponent);
		else result = computeXtoY (base, exponent, powerLibrary);
		return valueManager.newDiscreteValue (result);
	}


	/**
	 * compute left\right
	 *  appropriately relative to data type present
	 * @param left the base of the exponentiation operator
	 * @param right the (inverted) exponent of the exponentiation operator
	 * @return left\right
	 */
	public ValueManager.GenericValue root
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		T leftDiscrete, rightDiscrete = valueManager.toDiscrete (right), result;
		Integer intRoot = spaceManager.convertToInteger (leftDiscrete = valueManager.toDiscrete (left));
		if (intRoot == null || intRoot < 0) result = computeXrootY (leftDiscrete, rightDiscrete, powerLibrary);
		else if (spaceManager.isNegative (rightDiscrete)) result = computeXrootY (rightDiscrete, leftDiscrete, powerLibrary);
		else result = powerLibrary.nThRoot (rightDiscrete, intRoot);
		return valueManager.newDiscreteValue (result);
	}


	/**
	 * root of sum of squares
	 * @param values the array of values to sum
	 * @param powerLibrary the library to be used for SQRT
	 * @return the computed result
	 */
	public ValueManager.GenericValue hypot (ValueManager.GenericValue values, ExtendedPowerLibrary<T> powerLibrary)
	{
		T sum = spaceManager.getZero ();
		List<T> array = valueManager.toArray (values);
		for (T v : array) sum = spaceManager.add (sum, spaceManager.multiply (v, v));
		return valueManager.newDiscreteValue (powerLibrary.sqrt (sum));
	}


}

