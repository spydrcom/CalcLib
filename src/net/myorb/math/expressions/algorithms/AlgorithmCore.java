
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.PowerPrimitives;

/**
 * base class supporting algorithm implementations
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class AlgorithmCore<T> extends OperatorNomenclature
{


	/**
	 * object depends on data structures of the environment
	 * @param environment the environment object holding value management objects
	 */
	public AlgorithmCore (Environment<T> environment)
	{
		this (environment.getSpaceManager (), environment.getValueManager ());
		this.environment = environment;
	}

	/**
	 * for instances where environment is not available.
	 *  managers are set directly; loss of environment eliminates array processing
	 * @param spaceManager a space manager for manipulation of data type
	 * @param valueManager the generic processing of values
	 */
	public AlgorithmCore (ExpressionSpaceManager<T> spaceManager, ValueManager<T> valueManager)
	{
		this.spaceManager = spaceManager;
		this.valueManager = valueManager;
	}
	protected ExpressionSpaceManager<T> spaceManager = null;
	protected ValueManager<T> valueManager = null;


	/**
	 * simple access to power algorithms
	 */
	public class Primitives extends PowerPrimitives<T>
	{ public Primitives () { super (spaceManager); } }


	/**
	 * construct support object on reference
	 * @return an implementation of the support object
	 */
	public DimensionedDataSupport<T> getDimensionedDataSupport ()
	{
		if (this.dimensionedDataSupport == null)
		{ this.dimensionedDataSupport = new DimensionedDataSupport<T> (environment); }
		return dimensionedDataSupport;
	}
	protected DimensionedDataSupport<T> dimensionedDataSupport = null;
	protected Environment<T> environment = null;


	/**
	 * add appropriate name to generic value
	 * @param value the value to be wrapped as generic
	 * @param name the name to be given to the value
	 * @return generic value object with name
	 */
	public ValueManager.GenericValue namedValue (T value, String name)
	{
		ValueManager.GenericValue v =
			valueManager.newDiscreteValue (value);
		v.setName (name);
		return v;
	}


	/**
	 * compute absolute value
	 * @param parameter the source value operated on
	 * @return abs(parameter)
	 */
	public ValueManager.GenericValue abs (ValueManager.GenericValue parameter)
	{
		if (valueManager.isDimensioned (parameter))
		{
			ValueManager.RawValueList<T> values =
				valueManager.toDimensionedValue (parameter).getValues ();
			return valueManager.newDimensionedValue (abs (values));
		}
		return valueManager.newDiscreteValue
		(abs (valueManager.toDiscrete (parameter)));
	}
	ValueManager.RawValueList<T> abs (ValueManager.RawValueList<T> x)
	{
		ValueManager.RawValueList<T> results =
			new ValueManager.RawValueList<T> ();
		for (T v : x) { results.add (abs (v)); }
		return results;
	}
	T abs (T x) { return spaceManager.isNegative (x)? spaceManager.negate (x): x; }


}

