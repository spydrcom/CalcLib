
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.symbols.AbstractUnaryOperator;
import net.myorb.math.expressions.symbols.AbstractBuiltinVariableLookup;
import net.myorb.math.expressions.symbols.AbstractParameterizedFunction;
import net.myorb.math.expressions.symbols.AbstractBinaryOperator;

import net.myorb.math.expressions.ValueManager;

/**
 * implementations of algorithms computing basic algebraic functions
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class AlgebraicPrimitives<T> extends AlgorithmCore<T>
{


	/**
	 * object depends on data structures of the environment
	 * @param environment the environment object holding value management objects
	 */
	public AlgebraicPrimitives (Environment<T> environment)
	{
		super (environment);
	}


	/*
	 * operator and function algorithm implementations
	 */


	/**
	 * implement operator - abs
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getAbsAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return abs (parameter);
			}

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.symbols.AbstractUnaryOperator#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
			 */
			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{
				return using.formatOperatorReference ("|") + operand + using.formatOperatorReference ("|");
			}
		};
	}


	/**
	 * implement operator - sgn
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getSgnAlgorithm (String symbol, int precedence)
	{
		return translationFor (symbol, precedence, (p) -> Math.signum (p));
	}


	/**
	 * implement operator - +/-
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getPlusMinusAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
				T minus = spaceManager.add (leftDiscrete, spaceManager.negate (rightDiscrete)), plus = spaceManager.add (leftDiscrete, rightDiscrete);
				ValueManager.RawValueList<T> values = new ValueManager.RawValueList<T> (); values.add (plus); values.add (minus);
				return valueManager.newDimensionedValue (values);
			}
		};
	}


	/**
	 * implement operator - -/+
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getMinusPlusAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
				T minus = spaceManager.add (leftDiscrete, spaceManager.negate (rightDiscrete)), plus = spaceManager.add (leftDiscrete, rightDiscrete);
				ValueManager.RawValueList<T> values = new ValueManager.RawValueList<T> (); values.add (minus); values.add (plus);
				return valueManager.newDimensionedValue (values);
			}
		};
	}


	/**
	 * implement operator - floordiv
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getFloorDivAlgorithm (String symbol, int precedence)
	{
		return translationFor (symbol, precedence, (l, r) -> Math.floorDiv ( (long) l, (long) r ) );
	}


	/**
	 * implement operator - floor
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getFloorAlgorithm (String symbol, int precedence)
	{
		return translationFor (symbol, precedence, (p) -> Math.floor (p));
	}


	/**
	 * implement operator - ceil
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getCeilAlgorithm (String symbol, int precedence)
	{
		return translationFor (symbol, precedence, (p) -> Math.ceil (p));
	}


	/**
	 * identify value - pi
	 * @param symbol the symbol associated with this object
	 * @return variable object referencing value
	 */
	public AbstractBuiltinVariableLookup getPiValue (String symbol)
	{
		return new AbstractBuiltinVariableLookup (symbol)
		{
			public ValueManager.GenericValue getValue ()
			{ return namedValue (spaceManager.getPi (), symbol); }
		};
	}


	/**
	 * identify value - e
	 * @param symbol the symbol associated with this object
	 * @return variable object referencing value
	 */
	public AbstractBuiltinVariableLookup getEValue (String symbol)
	{
		return new AbstractBuiltinVariableLookup (symbol)
		{
			public ValueManager.GenericValue getValue ()
			{ return namedValue (spaceManager.convertFromDouble (e), symbol); }
		};
	}
	public static final Double e = 2.7182818284590452353602874713527;


	/**
	 * implement function - Test (for debugging use)
	 * @param symbol the symbol associated with this object
	 * @return function implementation object
	 */
	public AbstractParameterizedFunction getTestAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{
				ValueManager.ValueList parameterList = (ValueManager.ValueList) parameters;
				//List<ValueManager.GenericValue> parameterListValues = parameterList.getValues ();

				for (ValueManager.GenericValue v : parameterList.getValues ())
				{
					System.out.println (v);
				}

				return valueManager.newDiscreteValue (spaceManager.getZero ());
			}
		};
	}


}

