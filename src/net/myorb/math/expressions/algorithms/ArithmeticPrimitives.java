
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.symbols.AbstractBinaryOperator;
import net.myorb.math.expressions.symbols.AbstractUnaryOperator;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.ValueManager;

/**
 * implementations of basic arithmetic algorithms
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class ArithmeticPrimitives<T> extends AlgorithmCore <T>
{


	/**
	 * object depends on data structures of the environment
	 * @param environment the environment object holding value management objects
	 */
	public ArithmeticPrimitives (Environment<T> environment)
	{
		super (environment);
	}


	/*
	 * operator and function algorithm implementations
	 */


	/**
	 * implement operator - NEGATE (unary -)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getNegateAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
					(ValueManager.GenericValue parameter)
			{
				DimensionedDataSupport<T>
						support = getDimensionedDataSupport ();
				if (valueManager.isDimensioned (parameter))
				{
					return valueManager.newDimensionedValue
					(support.vectorNegate (parameter));
				}

				T discreteValue =
						valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue
				(
					spaceManager.negate (discreteValue)
				);
			}

			public String markupForDisplay
					(
						String operator, String operand,
						NodeFormatting using
					)
			{
				return
					using.formatOperatorReference
						(OperatorNomenclature.SUBTRACTION_OPERATOR)
					+ using.formatBracket (operand);
			}
		};
	}


	/**
	 * implement operator - +
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getAdditionAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				DimensionedDataSupport<T> support = getDimensionedDataSupport ();

				if (support.dimensionedParameterPresent (left, right))
				{
					return support.dimensionedAdd (left, right);
				}

				T leftDiscrete = valueManager.toDiscrete (left),
					rightDiscrete = valueManager.toDiscrete (right);
				return valueManager.newDiscreteValue
					(
						spaceManager.add (leftDiscrete, rightDiscrete)
					);
			}
		};
	}


	/**
	 * implement operator - -
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getSubtractionAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				DimensionedDataSupport<T>
					support = getDimensionedDataSupport ();
				if (support.dimensionedParameterPresent (left, right))
				{ return support.dimensionedSubtract (left, right); }

				T leftDiscrete = valueManager.toDiscrete (left),
					rightDiscrete = valueManager.toDiscrete (right);
				return valueManager.newDiscreteValue
				(
					spaceManager.add
					(
						leftDiscrete, spaceManager.negate (rightDiscrete)
					)
				);
			}

			public String shortCircuit (String left, String right)
			{
				/*
				 * bug fix for render of binary operation 0-x,
				 *	where 0 was inserted to allow binary operator - 
				 *	to represent unary negate
				 */
				if ( ! ZERO.equals (left) ) return null; // TOTAL HACK, but ...
				else return NEGATE + right;
			}
			public static final String ZERO = "<mn>0</mn>", NEGATE = "<mo>-</mo>";
		};
	}


	/**
	 * implement operator - *
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getMultiplicationAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				DimensionedDataSupport<T> support = getDimensionedDataSupport ();

				if (support.dimensionedParameterPresent (left, right))
				{ return support.dimensionedMultiply (left, right); }

				T leftDiscrete = valueManager.toDiscrete (left),
					rightDiscrete = valueManager.toDiscrete (right);
				return valueManager.newDiscreteValue
					(
						spaceManager.multiply (leftDiscrete, rightDiscrete)
					);
			}
		};
	}


	/**
	 * implement operator - /
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getDivisionAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
				(
					ValueManager.GenericValue left,
					ValueManager.GenericValue right
				)
			{
				T leftDiscrete = valueManager.toDiscrete (left),
					rightDiscrete = valueManager.toDiscrete (right);
				return valueManager.newDiscreteValue
					(
						spaceManager.multiply
						(
							leftDiscrete, spaceManager.invert (rightDiscrete)
						)
					);
			}

			public String markupForDisplay
				(
					String operator, String firstOperand,
					String secondOperand, boolean lfence,
					boolean rfence, NodeFormatting using
				)
			{
				return using.formatOverUnderOperation
					(firstOperand, secondOperand);
			}
		};
	}


	/**
	 * implement operator - #/#
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getFractionAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
				(
					ValueManager.GenericValue left,
					ValueManager.GenericValue right
				)
			{
				T leftDiscrete = valueManager.toDiscrete (left),
					rightDiscrete = valueManager.toDiscrete (right);
				return valueManager.newDiscreteValue
					(
						spaceManager.multiply
						(
							leftDiscrete, spaceManager.invert (rightDiscrete)
						)
					);
			}

			public String markupForDisplay
				(
					String operator, String firstOperand,
					String secondOperand, boolean lfence,
					boolean rfence, NodeFormatting using
				)
			{
				String left = using.formatParenthetical (firstOperand, lfence),
					right = using.formatParenthetical (secondOperand, rfence);
				return using.formatBinaryOperation
					(
						left, OperatorNomenclature.DIVISION_OPERATOR, right
					);
			}
		};
	}


	/**
	 * implement operator - INRANGE
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getInrangeAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
				(
					ValueManager.GenericValue left,
					ValueManager.GenericValue right
				)
			{
				ValueManager.RawValueList <T> rng =
					valueManager.toDimensionedValue (right).getValues ();
				T lo = rng.get (0), hi = rng.get (1), V = valueManager.toDiscrete (left);

				return valueManager.newDiscreteValue
				(
					spaceManager.lessThan (V, lo) || spaceManager.lessThan (hi, V) ?
						spaceManager.getZero () : spaceManager.getOne ()
				);
			}

			public String markupForDisplay
				(
					String operator, String firstOperand,
					String secondOperand, boolean lfence,
					boolean rfence, NodeFormatting using
				)
			{
				int
				start = secondOperand.indexOf ("\""),
				 end  = secondOperand.indexOf ("\"", start+1);
				String range = secondOperand.substring (start+1, end);
				String ends [] = range.split (","), lo = ends [0], hi = ends [1];

				lo = using.formatNumericReference ( lo.replaceAll (" ", "") );
				hi = using.formatNumericReference ( hi.replaceAll (" ", "") );

				String loSide = using.formatBinaryOperation (lo, LE, firstOperand);
				String eqn = using.formatBinaryOperation (loSide, LE, hi);
				return eqn;
			}
			String LE = OperatorNomenclature.LE__OPERATION_RENDER;
		};
	}


}

