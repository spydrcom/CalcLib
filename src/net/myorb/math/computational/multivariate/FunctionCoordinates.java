
package net.myorb.math.computational.multivariate;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.ValueManager.GenericValue;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.data.abstractions.CommonDataStructures;

import java.util.List;

/**
 * implementations of algorithms specific to treatment of Multivariate data points
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class FunctionCoordinates <T> extends CommonDataStructures
{


	public FunctionCoordinates (Environment <T> environment)
	{
		this.manager = environment.getSpaceManager ();
		this.valueManager = environment.getValueManager ();
		if ( manager instanceof ExpressionComponentSpaceManager )
		{ this.compManager = (ExpressionComponentSpaceManager <T>) manager; }
		this.multivariateDataType = compManager != null && compManager.getComponentCount () > 1;
		this.dataTypeDimensions = multivariateDataType ? compManager.getComponentCount () : 0;
		this.environment = environment;
	}
	protected ExpressionComponentSpaceManager <T> compManager = null;
	protected ExpressionSpaceManager <T> manager = null;
	protected ValueManager <T> valueManager = null;
	protected Environment <T> environment = null;
	protected boolean multivariateDataType;
	protected int dataTypeDimensions;


	/**
	 * Coordinates are treated as a list of real values
	 */
	public static class Coordinates extends ItemList <Double>
	{
		/**
		 * identify the mechanisms used to treat the point as a generic value
		 */
		public enum Packaging { COMPONENT, DIMENSIONED, ELEMENTS, INDIVIDUAL }
		protected Packaging packaging;

		/**
		 * @return a real value array of the coordinate list
		 */
		public double [] toVector ()
		{
			double [] array = new double [size ()];
			for (int n = 0; n < array.length; n++) array[n] = get (n);
			return array;
		}

		public Coordinates plus (double offset, int inDimension)
		{
			Coordinates sum = dup ();
			sum.set (inDimension, this.get (inDimension) + offset);
			return sum;
		}

		public Coordinates minus (Coordinates other)
		{
			Coordinates dif = new Coordinates ();
			for (int n = 0; n < this.size (); n++)
			{
				double thisOne = get (n).doubleValue (),
					otherOne = other.get (n).doubleValue ();
				dif.add ( thisOne - otherOne );
			}
			return dif;
		}

		public Coordinates dup ()
		{
			Coordinates copy = new Coordinates ();
			copy.packaging = this.packaging;
			copy.addAll (this);
			return copy;
		}

		//TODO: additional context required to allow points to become parameters
		private static final long serialVersionUID = 6909479746233500672L;
	}


	/**
	 * translate coordinates to a generic value
	 * @param coordinates the coordinate computed as an evaluation point
	 * @return the vector as a generic value
	 */
	public ValueManager.GenericValue represent (Coordinates coordinates)
	{
		switch (coordinates.packaging)
		{
			case COMPONENT:
				return representComponents (coordinates);
			case DIMENSIONED:
				return representDimensioned (coordinates);
			case ELEMENTS:
				return representElements (coordinates);
			case INDIVIDUAL:
				break;
			default:
				break;
		}
		throw new RuntimeException ("Unable to represent coordinates");
	}


	/**
	 * build a generic representation of a component based vector
	 * @param coordinates the Coordinates representation of the vector
	 * @return the constructed GenericValue representation
	 */
	public ValueManager.GenericValue representComponents (Coordinates coordinates)
	{
		T parameter = compManager.construct ( coordinates.toVector () );
		return valueManager.newDimensionedValue ( new ItemList <T> (parameter) );
	}


	/**
	 * build a generic representation of arrayed elements
	 * @param coordinates the Coordinates representation of the vector
	 * @return the constructed GenericValue representation
	 */
	public ValueManager.GenericValue representElements (Coordinates coordinates)
	{
		ValueManager.RawValueList <T> values =
			new ValueManager.RawValueList <T> ();
		for (int n = 0; n < coordinates.size (); n++)
		{ values.add (manager.convertFromDouble (coordinates.get (n))); }
		return valueManager.newDimensionedValue (values);
	}


	/**
	 * build a generic representation of a dimensioned vector
	 * @param coordinates the Coordinates representation of the vector
	 * @return the constructed GenericValue representation
	 */
	public ValueManager.GenericValue representDimensioned (Coordinates coordinates)
	{
		ValueManager.GenericValueList list =
			new ValueManager.GenericValueList ();
		list.add ( representElements (coordinates) );
		return valueManager.newValueList (list);
	}


	/**
	 * evaluate a point passed as a parameter
	 * @param parameter the parameter called from the execution engine
	 * @return the parameter value(s) as a Coordinates object
	 */
	public Coordinates evaluate (ValueManager.GenericValue parameter)
	{
		List <T> values;

		// any number of simple values
		//  will come in as a Dimensioned Value
		// multiple component value will be a 1-D object 
		//  with the parameter as index 0 (notably complex values)
		if ( parameter instanceof ValueManager.DimensionedValue )
		{
			if ( ( values = processList (parameter) ).size () > 1 )
			{
				// multiple simple single parameter values
				//  combined set of parameter treated as vector
				return processVectorElementList ( values );
			}
			else if ( multivariateDataType )
			{
				// complex values end up here
				//  as would (other) multiple dimension
				//  data structures that have component managers
				return processComponents ( values.get (0) );
			}
		}

		// single multiple component value
		else if ( parameter instanceof ValueManager.DiscreteValue )
		{
			return processComponents (parameter);
		}

		// simple values
		// (array value objects in particular)
		// show up in value list objects as single items
		else if ( parameter instanceof ValueManager.ValueList )
		{
			ValueManager.ValueList valueList =
					(ValueManager.ValueList) parameter;
			return processVectorElements (valueList.getValues ());
		}

		throw new RuntimeException (INVALID);
	}


	/**
	 * get list from DimensionedValue
	 * @param value a parameter passed as DimensionedValue
	 * @return the RawValueList linked to the DimensionedValue
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	List <T> processList (ValueManager.GenericValue value)
	{ return ( (ValueManager.DimensionedValue) value ).getValues (); }


	/**
	 * treatment for elements making vector objects
	 * @param values a list of values to treat as a vector
	 * @return the equivalent Coordinates object
	 */
	public Coordinates processVectorElements
		(List <GenericValue> values)
	{
		GenericValue V = values.get (0);

		// test for short-circuit
		// - where the first value is a vector
		if ( V instanceof ValueManager.DimensionedValue )
		{
			Coordinates computed =
				processVectorElementList ( processList (V) );
			computed.packaging = Coordinates.Packaging.DIMENSIONED;
			return computed;
		}

		return processIndividualElements (values);
	}


	/**
	 * treatment for a list of generic values
	 * @param values individual values passed as a list
	 * @return the equivalent Coordinates object
	 */
	public Coordinates processIndividualElements
		(List <GenericValue> values)
	{
		GenericValue V;
		dataTypeDimensions = values.size ();
		Coordinates computed = new Coordinates ();

		for (int n = 0; n < dataTypeDimensions; n++)
		{
			if ( (V = values.get (n)) instanceof ValueManager.DiscreteValue )
			{
				@SuppressWarnings("unchecked")
				T value = ( (ValueManager.DiscreteValue <T>) V ).getValue ();
				computed.add ( manager.convertToDouble (value) );
			}
			else throw new RuntimeException (UNRECOGNIZED);
		}

		computed.packaging = Coordinates.Packaging.INDIVIDUAL;
		return computed;
	}


	/**
	 * treatment for a list of data values
	 * @param values a list of data values to treat as a vector
	 * @return the equivalent Coordinates object
	 */
	public Coordinates processVectorElementList (List <T> values)
	{
		dataTypeDimensions = values.size ();
		Coordinates computed = new Coordinates ();
		for (int n = 0; n < dataTypeDimensions; n++)
		{ computed.add ( manager.convertToDouble (values.get (n)) ); }
		computed.packaging = Coordinates.Packaging.ELEMENTS;
		return computed;
	}


	/**
	 * treatment for data objects
	 *  that contain multiple components
	 * @param value a multiple component data object
	 * @return the equivalent Coordinates object
	 */
	public Coordinates processComponents (T value)
	{
		Coordinates computed = new Coordinates ();
		for (int n = 0; n < dataTypeDimensions; n++)
		{ computed.add (compManager.component (value, n)); }
		computed.packaging = Coordinates.Packaging.COMPONENT;
		return computed;
	}
	@SuppressWarnings("unchecked")
	public Coordinates processComponents (ValueManager.GenericValue value)
	{
		return processComponents ( ( (ValueManager.DiscreteValue <T>) value ).getValue () );
	}


	// error messages
	static final String
	UNRECOGNIZED = "Parameter value is not recognized as a multivariate point",
	INVALID = "Parameter value is not a multivariate point";


}

