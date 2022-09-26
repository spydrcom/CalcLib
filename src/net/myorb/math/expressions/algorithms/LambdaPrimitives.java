
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.gui.rendering.MathMarkupNodes;

import net.myorb.math.expressions.symbols.AbstractBinaryOperator;
import net.myorb.math.expressions.symbols.AbstractUnaryOperator;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.ValueManager;

/**
 * implementations of pointer and lambda algorithms
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class LambdaPrimitives <T> extends AlgorithmCore <T>
{


	/**
	 * provide emphasis on a declaration operator for display of lambda,
	 *  a unique notation for display of a function declared in a lambda expression
	 * alternatives considered \u2016 \u03D6 \u039E
	 */
	static final String LAMBDA_DECLARATION_OPERATOR;

	static
	{
		String
			SPACE = MathMarkupNodes.space ("10"),										// wider space gives emphasis
			NOTATION = OperatorNomenclature.LAMBDA_DECLARATION_NOTATION,				// the notation chosen for display
			MO = MathMarkupNodes.enclose (NOTATION, MathMarkupNodes.OPERATOR_TAG);		// MO tag for MML use in lambda operator
		LAMBDA_DECLARATION_OPERATOR = SPACE + MO + SPACE;
	}


	/**
	 * object depends on data structures of the environment
	 * @param environment the environment object holding value management objects
	 */
	public LambdaPrimitives (Environment <T> environment)
	{
		super (environment); this.setLambdaProcessor ();
	}


	/**
	 * implement operator - -&gt;
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getLambdaAlgorithm
			(String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				String lefts = left.toString (), rights = right.toString ();

				String parameters = lefts.substring (1, lefts.length () - 1);
				String funcBody = rights.substring (1, rights.length () - 1);

				return lambda.processDeclaration (parameters, funcBody);
			}

			public String markupForDisplay
				(
					String operator, String firstOperand, String secondOperand,
					boolean lfence, boolean rfence, NodeFormatting using
				)
			{
				String left = using.formatParenthetical (firstOperand, lfence),
						right = using.formatParenthetical (secondOperand, rfence);
				return lambda.profileFor (left, using) +
						LAMBDA_DECLARATION_OPERATOR +
						right;
			}
		};
	}
	void setLambdaProcessor ()
	{
		this.lambda = environment.getLambdaExpressionProcessor ();
		environment.provideAccessTo (lambda);
	}
	protected LambdaFunctionPlotter <T> lambda;


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
			public ValueManager.GenericValue
			execute (ValueManager.GenericValue parameter)
			{
				// this needs to be in place to provide a target for configuration
				// the actual operation is executed in-line for efficiency
				return null;
			}

			public String markupForDisplay
			(String operator, String operand, NodeFormatting using)
			{
				// just show parameter name as a function call
				return operand;	// avoid display of the operator
			}
		};
	}


}

