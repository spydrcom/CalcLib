
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.symbols.AbstractParameterizedFunction;
import net.myorb.math.expressions.symbols.AbstractUnaryOperator;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.TrigIdentities;

/**
 * implementations of algorithms computing trigonometric operations
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class TrigPrimitives<T> extends AlgorithmCore<T>
{


	/**
	 * object depends on data structures of the environment
	 * @param environment the environment object holding value management objects
	 * @param trigLibrary library for trig functions
	 */
	public TrigPrimitives (Environment<T> environment, TrigIdentities<T> trigLibrary)
	{ super (environment); this.trigLibrary = trigLibrary; }
	protected TrigIdentities<T> trigLibrary = null;


	/*
	 * operator and function algorithm implementations
	 */


	/**
	 * implement operator - sin
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getSinAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return valueManager.newDiscreteValue (trigLibrary.sin (valueManager.toDiscrete (parameter)));
			}
		};
	}

	/**
	 * implement operator - sinsq (sine squared)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getSinSqAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				T sineValue = trigLibrary.sin (valueManager.toDiscrete (parameter));
				T sinsq = spaceManager.multiply (sineValue, sineValue);
				return valueManager.newDiscreteValue (sinsq);
			}

			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{
				return using.formatUnaryPrefixOperation ("sin\u00B2", operand);
			}
		};
	}

	/**
	 * implement operator - sincb (sine cubed)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getSinCbAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				T sineValue = trigLibrary.sin (valueManager.toDiscrete (parameter));
				T sinsq = spaceManager.multiply (sineValue, sineValue);
				T sincb = spaceManager.multiply (sinsq, sineValue);
				return valueManager.newDiscreteValue (sincb);
			}

			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{
				return using.formatUnaryPrefixOperation ("sin\u00B3", operand);
			}
		};
	}


	/**
	 * implement operator - cos
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getCosAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return valueManager.newDiscreteValue (trigLibrary.cos (valueManager.toDiscrete (parameter)));
			}
		};
	}

	/**
	 * implement operator - cossq (cosine squared)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getCosSqAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				T cosineValue = trigLibrary.cos (valueManager.toDiscrete (parameter));
				T cossq = spaceManager.multiply (cosineValue, cosineValue);
				return valueManager.newDiscreteValue (cossq);
			}

			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{
				return using.formatUnaryPrefixOperation ("cos\u00B2", operand);
			}
		};
	}

	/**
	 * implement operator - coscb (cosine cubed)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getCosCbAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				T cosineValue = trigLibrary.cos (valueManager.toDiscrete (parameter));
				T cossq = spaceManager.multiply (cosineValue, cosineValue);
				T coscb = spaceManager.multiply (cossq, cosineValue);
				return valueManager.newDiscreteValue (coscb);
			}

			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{
				return using.formatUnaryPrefixOperation ("cos\u00B3", operand);
			}
		};
	}


	/**
	 * implement operator - tan
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getTanAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return valueManager.newDiscreteValue (trigLibrary.tan (valueManager.toDiscrete (parameter)));
			}
		};
	}

	/**
	 * implement operator - tansq (tangent squared)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getTanSqAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				T tangentValue = trigLibrary.tan (valueManager.toDiscrete (parameter));
				T tansq = spaceManager.multiply (tangentValue, tangentValue);
				return valueManager.newDiscreteValue (tansq);
			}

			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{
				return using.formatUnaryPrefixOperation ("tan\u00B2", operand);
			}
		};
	}

	/**
	 * implement operator - tancb (tangent cubed)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getTanCbAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				T tangentValue = trigLibrary.tan (valueManager.toDiscrete (parameter));
				T tansq = spaceManager.multiply (tangentValue, tangentValue);
				T tancb = spaceManager.multiply (tansq, tangentValue);
				return valueManager.newDiscreteValue (tancb);
			}

			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{
				return using.formatUnaryPrefixOperation ("tan\u00B3", operand);
			}
		};
	}


	/**
	 * implement operator - asin
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getAsinAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{ return valueManager.newDiscreteValue (trigLibrary.asin (valueManager.toDiscrete (parameter))); }
		};
	}


	/**
	 * implement function - atan
	 * @param symbol the symbol associated with this object
	 * @return function implementation object
	 */
	public AbstractParameterizedFunction getAtanAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{ return valueManager.newDiscreteValue (trigLibrary.atan (valueManager.toArray  (parameter))); }
		};
	}


}

