
package net.myorb.math.expressions.algorithms;

import net.myorb.math.computational.Combinatorics;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.symbols.AbstractBinaryOperator;
import net.myorb.math.expressions.symbols.AbstractUnaryOperator;
import net.myorb.math.expressions.ValueManager;

import java.util.List;

/**
 * implementations of algorithms computing combinatorial operations
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class ComboPrimitives<T> extends PowerPrimitives<T>
{


	/**
	 * object depends on data structures of the environment
	 * @param environment the environment object holding value management objects
	 */
	public ComboPrimitives (Environment<T> environment)
	{ super (environment); combinatorics = new Combinatorics<T>(spaceManager, powerLibrary); }
	protected Combinatorics<T> combinatorics = null;


	/*
	 * operator and function algorithm implementations
	 */


	/**
	 * implement operator - binomial coefficient
	 * @param symbol the symbol associated with this object
	 * @param precedence the precedence of the operation
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getBCAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
				return valueManager.newDiscreteValue (combinatorics.binomialCoefficient (leftDiscrete, rightDiscrete));
			}

			public String markupForDisplay
			(String operator, String firstOperand, String secondOperand, boolean lfence, boolean rfence, NodeFormatting using)
			{
				return using.formatBinomialCoefficient (firstOperand, secondOperand);
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
				T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
				int n = spaceManager.toNumber (leftDiscrete).intValue (), k = spaceManager.toNumber (rightDiscrete).intValue ();
				T result = spaceManager.convertFromDouble (Combinatorics.stirlingNumbers1 (n, k));
				return valueManager.newDiscreteValue (result);
			}

			public String markupForDisplay
			(String operator, String firstOperand, String secondOperand, boolean lfence, boolean rfence, NodeFormatting using)
			{
				return using.formatStirlingNumbers (firstOperand, secondOperand);
			}
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
				T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
				int n = spaceManager.toNumber (leftDiscrete).intValue (), k = spaceManager.toNumber (rightDiscrete).intValue ();
				T result = spaceManager.convertFromDouble (Combinatorics.stirlingNumbers2 (n, k));
				return valueManager.newDiscreteValue (result);
			}

			public String markupForDisplay
			(String operator, String firstOperand, String secondOperand, boolean lfence, boolean rfence, NodeFormatting using)
			{
				return using.formatStirlingNumbers (firstOperand, secondOperand);
			}
		};
	}


	/**
	 * implement operator - Euler Numbers
	 * @param symbol the symbol associated with this object
	 * @param precedence the precedence of the operation
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getENAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
				int n = spaceManager.toNumber (leftDiscrete).intValue (), m = spaceManager.toNumber (rightDiscrete).intValue ();
				T result = spaceManager.convertFromDouble (Combinatorics.eulerNumbers (n, m));
				return valueManager.newDiscreteValue (result);
			}

			public String markupForDisplay
			(String operator, String firstOperand, String secondOperand, boolean lfence, boolean rfence, NodeFormatting using)
			{
				return using.formatBinomialCoefficient (firstOperand, secondOperand);
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
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
				return valueManager.newDiscreteValue (combinatorics.raisingPochhammer (leftDiscrete, rightDiscrete));
			}

			public String markupForDisplay
			(String operator, String firstOperand, String secondOperand, boolean lfence, boolean rfence, NodeFormatting using)
			{
				return using.formatPochhammerRising (firstOperand, secondOperand);
			}
		};
	}


	/**
	 * implement operator - factorial raising
	 * @param symbol the symbol associated with this object
	 * @param precedence the precedence of the operation
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getFrisAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
				return valueManager.newDiscreteValue (combinatorics.raisingFactorial (leftDiscrete, rightDiscrete));
			}
		};
	}


	/**
	 * implement operator - factorial falling
	 * @param symbol the symbol associated with this object
	 * @param precedence the precedence of the operation
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getFfalAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
				return valueManager.newDiscreteValue (combinatorics.fallingFactorial (leftDiscrete, rightDiscrete));
			}
		};
	}


	/**
	 * implement operator - B(n)
	 * @param symbol the symbol associated
	 * @param precedence the precedence of the operation
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getBernoulliAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				Integer n = 1, m = 0; T mt = null;

				if (!valueManager.isArray (parameter))
					mt = valueManager.toDiscrete (parameter);
				else
				{
					List<T> values = valueManager.toDiscreteValues (parameter);
					if (values.size () == 1)
						mt = values.get (0);
					else
					{
						n = spaceManager.convertToInteger (values.get (0));
						mt = values.get (1);
					}
				}

				m = spaceManager.convertToInteger (mt);
				T b = combinatorics.firstKindBernoulli (m);
				if (n == 0 && m == 1) b = spaceManager.negate (b);
				return valueManager.newDiscreteValue (b);
			}
		};
	}


	/**
	 * implement operator - gamma
	 * @param symbol the symbol associated
	 * @param precedence the precedence of the operation
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getGammaAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				T functionParameter = valueManager.toDiscrete (parameter), result;
				if (!valueManager.isInt (parameter, spaceManager)) result = combinatorics.gamma (functionParameter);
				else { result = combinatorics.factorial (spaceManager.add (functionParameter, spaceManager.newScalar (-1))); }
				return valueManager.newDiscreteValue (result);
			}
		};
	}


	/**
	 * implement operator - LogGamma
	 * @param symbol the symbol associated
	 * @param precedence the precedence of the operation
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getLogGammaAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{ return valueManager.newDiscreteValue (combinatorics.logGamma (valueManager.toDiscrete (parameter))); }
		};
	}


	/**
	 * implement operator - H(x)
	 * @param symbol the symbol associated
	 * @param precedence the precedence of the operation
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getHarmonicAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				T functionParameter = valueManager.toDiscrete (parameter), result;
				if (!valueManager.isInt (parameter, spaceManager)) result = combinatorics.H (functionParameter);
				else result = combinatorics.H (valueManager.toInt (parameter, spaceManager));
				return valueManager.newDiscreteValue (result);
			}
		};
	}


	/**
	 * implement operator - zeta
	 * @param symbol the symbol associated
	 * @param precedence the precedence of the operation
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getZetaAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{ return valueManager.newDiscreteValue (combinatorics.zeta (valueManager.toDiscrete (parameter))); }
		};
	}


}

