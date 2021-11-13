
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.ValueManager.GenericValue;

import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.math.SpaceManager;

import java.util.ArrayList;

/**
 * implementations of common iteration consumer algorithms
 * @author Michael Druckman
 */
public class IterationConsumerImplementations
{


	/**
	 * the available consumers
	 */
	public enum Names
	{
		AggregateConsumer, SummationConsumer, IntegralConsumer, ProductConsumer, PlotConsumer
	}


	/**
	 * get consumer instance by name
	 * @param named the name of the consumer
	 * @param using a data type manager for the consumer
	 * @return the new consumer instance
	 * @param <T> data type
	 */
	public static <T> IterationConsumer getIterationConsumer (String named, SpaceManager<T> using)
	{
		switch (Names.valueOf (named))
		{
			case AggregateConsumer: return getArrayIterationConsumer (using);
			case SummationConsumer: return getSummationIterationConsumer (using);
			case IntegralConsumer: return getIntegralIterationConsumer (using);
			case ProductConsumer: return getProductIterationConsumer (using);
			case PlotConsumer: return getPlotIterationConsumer (using);
			default: throw new RuntimeException ("Internal Error");
		}
	}


	/**
	 * result of iterations is aggregate array
	 * @param manager the type manager for the discrete data type
	 * @return the new consumer object
	 * @param <T> data type
	 */
	public static <T> IterationConsumer
	getArrayIterationConsumer (SpaceManager<T> manager)
	{ return new AggregateConsumer<T>(manager); }

	/**
	 * result of iterations is sum of values
	 * @param manager the type manager for the discrete data type
	 * @return the new consumer object
	 * @param <T> data type
	 */
	public static <T> IterationConsumer
	getSummationIterationConsumer (SpaceManager<T> manager)
	{ return new SummationConsumer<T>(manager); }

	/**
	 * result of iterations is sum of values.
	 *  same functionality as Summation, appearance modified for calculus notation
	 * @param manager the type manager for the discrete data type
	 * @return the new consumer object
	 * @param <T> data type
	 */
	public static <T> IterationConsumer
	getIntegralIterationConsumer (SpaceManager<T> manager)
	{ return new IntegralConsumer<T>(manager); }

	/**
	 * result of iterations is product of values
	 * @param manager the type manager for the discrete data type
	 * @return the new consumer object
	 * @param <T> data type
	 */
	public static <T> IterationConsumer
	getProductIterationConsumer (SpaceManager<T> manager)
	{ return new ProductConsumer<T>(manager); }

	/**
	 * result of iterations is 2D plot points
	 * @param manager the type manager for the discrete data type
	 * @return the new consumer object
	 * @param <T> data type
	 */
	public static <T> IterationConsumer
	getPlotIterationConsumer (SpaceManager<T> manager)
	{ return new PlotConsumer<T>(manager); }

}


/**
 * data type manager for consumers
 * @param <T> data type
 */
abstract class AbstractConsumer<T> implements IterationConsumer
{

	AbstractConsumer (SpaceManager<T> manager)
	{ this.valueManager = new ValueManager<T> (); this.manager = manager; }
	protected ValueManager<T> valueManager; protected SpaceManager<T> manager;

	/**
	 * @return most recent setting of IterationValue as discrete generic
	 */
	public T getIterationValue () { return valueManager.toDiscrete (iterationValue); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#setIterationValue(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public void setIterationValue (GenericValue value) { this.iterationValue = value; }
	protected GenericValue iterationValue;

	/**
	 * @param initialValue the value to start the evaluation from
	 */
	public void init (T initialValue) { aggregateValue = initialValue; }
	protected T aggregateValue;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#getCalculatedResult()
	 */
	public GenericValue getCalculatedResult () { return valueManager.newDiscreteValue (aggregateValue); }

	/**
	 * trace each iteration
	 */
	void dump () { if (TRACE) System.out.println (iterationValue + ": " + v + " = " + aggregateValue); }
	public static final boolean TRACE = false;
	protected T v;

}


/**
 * sum of iteration values (SIGMA operator)
 * @param <T> data type
 */
class SummationConsumer<T> extends AbstractConsumer<T>
{

	SummationConsumer (SpaceManager<T> manager) { super (manager); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#accept(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public void accept (GenericValue value) { aggregateValue = manager.add (aggregateValue, v = valueManager.toDiscrete (value)); dump (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#init()
	 */
	public void init () { init (manager.getZero ()); }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "SIGMA"; }

}


/**
 * sum of iteration values (INTEGRAL operator)
 * @param <T> data type
 */
class IntegralConsumer<T> extends AbstractConsumer<T>
{

	IntegralConsumer (SpaceManager<T> manager) { super (manager); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#accept(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public void accept (GenericValue value) { aggregateValue = manager.add (aggregateValue, v = valueManager.toDiscrete (value)); dump (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#init()
	 */
	public void init () { init (manager.getZero ()); }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "INTEGRAL"; }

}


/**
 * product of iteration values (PI operator)
 * @param <T> data type
 */
class ProductConsumer<T> extends AbstractConsumer<T>
{

	ProductConsumer (SpaceManager<T> manager) { super (manager); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#accept(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public void accept (GenericValue value) { aggregateValue = manager.multiply (aggregateValue, v = valueManager.toDiscrete (value)); dump (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#init()
	 */
	public void init () { init (manager.getOne ()); }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "PI"; }

}


/**
 * aggregation of iteration values (ARRAY operator)
 * @param <T> data type
 */
class AggregateConsumer<T> extends AbstractConsumer<T>
{

	AggregateConsumer (SpaceManager<T> manager) { super (manager); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#accept(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public void accept (GenericValue value) { aggregate.add (valueManager.toDiscrete (value)); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#getCalculatedResult()
	 */
	public GenericValue getCalculatedResult () { return valueManager.newDimensionedValue (aggregate); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#init()
	 */
	public void init () { this.aggregate = new ArrayList<T>(); }
	protected ArrayList<T> aggregate;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return ""; }

}


/**
 * plot of iteration values (PLOT operator)
 * @param <T> data type
 */
class PlotConsumer<T> extends AbstractConsumer<T>
{

	PlotConsumer (SpaceManager<T> manager) { super (manager); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#accept(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public void accept (GenericValue value) { sequence.addSample (getIterationValue (), v = valueManager.toDiscrete (value)); dump (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#getCalculatedResult()
	 */
	public GenericValue getCalculatedResult () { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#init()
	 */
	public void init () { this.sequence = new DataSequence2D<T>(); }
	protected DataSequence2D<T> sequence;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "PLOT"; }

}

