
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.symbols.AbstractParameterizedFunction;
import net.myorb.math.expressions.symbols.AbstractUnaryOperator;
import net.myorb.math.HighSpeedMathLibrary;
import net.myorb.math.TrigIdentities;

import java.util.ArrayList;
import java.util.List;

/**
 * implementations of algorithms computing trigonometric operations.
 * this class extends TrigPrimitives changing implementation to more efficient algorithms.
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class TrigHSPrimitives<T> extends TrigPrimitives<T>
{


	/**
	 * object depends on data structures of the environment
	 * @param environment the environment object holding value management objects
	 * @param trigLibrary a library for the trigonometric identities
	 * @param speedLibrary optimized for speed
	 */
	public TrigHSPrimitives
	(Environment<T> environment, TrigIdentities<T> trigLibrary, HighSpeedMathLibrary speedLibrary)
	{ super (environment, trigLibrary); this.speedLibrary = speedLibrary; }
	protected HighSpeedMathLibrary speedLibrary = null;


	/*
	 * operator and function algorithm implementations
	 */


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
			{
				Double p = spaceManager.toNumber (valueManager.toDiscrete (parameter)).doubleValue ();
				return valueManager.newDiscreteValue (spaceManager.convertFromDouble (speedLibrary.asin (p))); }
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
			{
				List<Double> doubleArray = new ArrayList<Double>();
				List<T> parameterArray = valueManager.toArray (parameter);
				for (T item : parameterArray) { doubleArray.add (spaceManager.convertToDouble (item)); }
				return valueManager.newDiscreteValue (spaceManager.convertFromDouble (speedLibrary.atan (doubleArray)));
			}
		};
	}


}
