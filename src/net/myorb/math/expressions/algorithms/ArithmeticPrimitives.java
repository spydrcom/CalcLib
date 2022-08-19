
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.gui.rendering.MathMarkupNodes;
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
public class ArithmeticPrimitives<T> extends AlgorithmCore<T>
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
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.SymbolMap.UnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
			 */
			public ValueManager.GenericValue
			execute (ValueManager.GenericValue parameter)
			{
				DimensionedDataSupport<T> support = getDimensionedDataSupport ();

				if (valueManager.isDimensioned (parameter))
				{
					return valueManager.newDimensionedValue (support.vectorNegate (parameter));
				}
				else
				{
					T discreteValue = valueManager.toDiscrete (parameter);
					return valueManager.newDiscreteValue (spaceManager.negate (discreteValue));
				}
			}

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.SymbolMap.UnaryOperator#markupForDisplay(java.lang.String, java.lang.String)
			 */
			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{
				return using.formatOperatorReference (OperatorNomenclature.SUBTRACTION_OPERATOR) + using.formatBracket (operand);
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
				else
				{
					T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
					return valueManager.newDiscreteValue (spaceManager.add (leftDiscrete, rightDiscrete));
				}
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
				DimensionedDataSupport<T> support = getDimensionedDataSupport ();

				if (support.dimensionedParameterPresent (left, right))
				{
					return support.dimensionedSubtract (left, right);
				}
				else
				{
					T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
					return valueManager.newDiscreteValue (spaceManager.add (leftDiscrete, spaceManager.negate (rightDiscrete)));
				}
			}
			public String shortCircuit (String left, String right)
			{
				/* bug fix for render of binary operation 0-x, where 0 was inserted to allow binary operator - to represent unary negate */
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
				{
					return support.dimensionedMultiply (left, right);
				}
				else
				{
					T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
					return valueManager.newDiscreteValue (spaceManager.multiply (leftDiscrete, rightDiscrete));
				}
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
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
				return valueManager.newDiscreteValue (spaceManager.multiply (leftDiscrete, spaceManager.invert (rightDiscrete)));
			}

			public String markupForDisplay (String operator, String firstOperand, String secondOperand, boolean lfence, boolean rfence, NodeFormatting using)
			{
				return using.formatOverUnderOperation (firstOperand, secondOperand);
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
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
				return valueManager.newDiscreteValue (spaceManager.multiply (leftDiscrete, spaceManager.invert (rightDiscrete)));
			}

			public String markupForDisplay (String operator, String firstOperand, String secondOperand, boolean lfence, boolean rfence, NodeFormatting using)
			{
				String left = using.formatParenthetical (firstOperand, lfence), right = using.formatParenthetical (secondOperand, rfence);
				return using.formatBinaryOperation (left, OperatorNomenclature.DIVISION_OPERATOR, right);
			}
		};
	}


	/**
	 * implement operator - -&gt;
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getLambdaAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				String lefts = left.toString (), rights = right.toString ();

				String parameters = lefts.substring (1, lefts.length () - 1);
				String funcBody = rights.substring (1, rights.length () - 1);

				LambdaExpressions<T> lambda =
					environment.getLambdaExpressionProcessor ();
				environment.provideAccessTo (lambda);

				ValueManager.GenericValue value =
					lambda.processDeclaration (parameters, funcBody);
					//lambda.processDeclaration (parameters, "(" + funcBody + ")");
					// this must align properly with the precedence assigned the operator
					// the parenthesis in the first version offset a 9 precedence on ->
					// absent the parenthesis seems aligned with a 7 precedence
					// this may yet be shown in error
				return value;
			}

			public String markupForDisplay
				(
					String operator, String firstOperand, String secondOperand,
					boolean lfence, boolean rfence, NodeFormatting using
				)
			{
				String sp = MathMarkupNodes.space ("10");
				String op = sp + using.formatOperatorReference (LAMBDA_NOTATION) + sp;
				String left = using.formatParenthetical (firstOperand, lfence), right = using.formatParenthetical (secondOperand, rfence);
				//return using.formatBinaryOperation (left, OperatorNomenclature.LAMBDA_EXPRESSION_INDICATOR, right);
				return left + op + right;
			}
		};
	}
	public static final String LAMBDA_NOTATION = "\u039E"; // 2016 3D6 39E


	/**
	 * implement operator - @|
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getDerefAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.SymbolMap.UnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
			 */
			public ValueManager.GenericValue
			execute (ValueManager.GenericValue parameter)
			{
				return null;
			}
		};
	
	}


}

