
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.symbols.AbstractParameterizedFunction;

/**
 * implementations of algorithms computing stat operations
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class StatisticsPrimitives<T> extends VectorPrimitives<T>
{


	/**
	 * type manager used to evaluate computations
	 * @param environment access to the evaluation environment
	 */
	public StatisticsPrimitives (Environment<T> environment)
	{
		super (environment);
	}


	/**
	 * implement function - MEAN
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getMeanAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return valueManager.newDiscreteValue (stats.mean (conversion.seq (parameters))); }
		};
	}


	/**
	 * implement function - MEDIAN
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getMedianAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return valueManager.newDiscreteValue (stats.median (conversion.seq (parameters))); }
		};
	}


	/**
	 * implement function - MODE
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getModeAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return valueManager.newDiscreteValue (stats.mode (conversion.seq (parameters))); }
		};
	}


	/**
	 * implement function - STDEV
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getStdDevAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return valueManager.newDiscreteValue (stats.stdDev (conversion.seq (parameters))); }
		};
	}


	/**
	 * implement function - VAR
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getVarAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return valueManager.newDiscreteValue (stats.variance (conversion.seq (parameters))); }
		};
	}


	/**
	 * implement function - COV
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getCovAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return valueManager.newDiscreteValue (stats.cov (conversion.seq (parameters))); }
		};
	}


}

