
package net.myorb.math.computational.multivariate;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.ValueManager.DimensionedValue;
import net.myorb.math.expressions.ValueManager.DiscreteValue;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.data.abstractions.CommonDataStructures;

/**
 * the data management for elements of vectors
 * @param <T> the data type used in the Operations
 * @author Michael Druckman
 */
public class DataManagers <T> extends CommonDataStructures
{


	/**
	 * extract a DiscreteValue
	 * @param value a DiscreteValue from a generic wrapper
	 * @return the managed value
	 */
	@SuppressWarnings("unchecked")
	public T getValueFrom (GenericValue value)
	{ return ( (DiscreteValue <T>) value ).getValue (); }

	/**
	 * @param value a DiscreteValue from a generic wrapper
	 * @return the value converted to double
	 */
	public double getDoubleValueFrom (GenericValue value)
	{ return manager.convertToDouble ( getValueFrom (value) ); }

	/**
	 * @param value a DiscreteValue from a generic wrapper
	 * @return the verified discrete value converted to double
	 * @throws RuntimeException value not from a discrete wrapper
	 */
	public double getDiscreteValueFrom
			(GenericValue value)
	throws RuntimeException
	{
		if (value instanceof DiscreteValue)
		{  return getDoubleValueFrom (value);  }
		throw new RuntimeException (UNRECOGNIZED);
	}


	// lists of generic and managed values

	/**
	 * 	a list of values in generic wrappers
	 */
	@SuppressWarnings("serial")
	public static class GenericList extends ItemList <GenericValue>
	{
		GenericList (java.util.List <GenericValue> list) { addAll (list); }
		GenericList (ValueManager.ValueList list) { this ( list.getValues () ); }
	}

	/**
	 * a list of managed values
	 */
	@SuppressWarnings("serial") public class ValueList extends ItemList <T>
	{ ValueList () {} ValueList (java.util.List <T> list) { addAll (list); } }


	// list conversions from generic types

	/**
	 * get list from DimensionedValue
	 * @param value a parameter passed as DimensionedValue
	 * @return the RawValueList linked to the DimensionedValue
	 */
	@SuppressWarnings({"rawtypes","unchecked"})
	public ValueList processList (GenericValue value)
	{ return new ValueList ( ( (DimensionedValue) value ).getValues () ); }


	// support class constructor exposes manager objects from environment

	/**
	 * get management objects from environment
	 * @param environment the execution environment description
	 */
	public DataManagers (Environment <T> environment)
	{
		this.manager = environment.getSpaceManager ();
		this.valueManager = environment.getValueManager ();
		if (manager instanceof ExpressionComponentSpaceManager)
		{ this.compManager = (ExpressionComponentSpaceManager <T>) manager; }
		this.environment = environment;
	}
	protected ExpressionComponentSpaceManager <T> compManager = null;
	protected ExpressionSpaceManager <T> manager = null;
	protected ValueManager <T> valueManager = null;
	protected Environment <T> environment = null;


	// error messages
	static final String
	UNRECOGNIZED = "Parameter value is not recognized as a multivariate point",
	INVALID = "Parameter value is not a multivariate point";


}

