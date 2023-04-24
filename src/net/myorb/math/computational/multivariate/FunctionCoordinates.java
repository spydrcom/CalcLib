
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
public class FunctionCoordinates <T>
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
	public class Coordinates extends CommonDataStructures.ItemList <Double>
	{
		//TODO: additional context required to allow points to become parameters
		private static final long serialVersionUID = 6909479746233500672L;
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
			return processVectorElementList ( processList (V) );
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
		return computed;
	}


	// error messages
	static final String
	UNRECOGNIZED = "Parameter value is not recognized as a multivariate point",
	INVALID = "Parameter value is not a multivariate point";


}


//(2, 2, 3)
//DATA net.myorb.math.expressions.DimensionedValueStorage
//RawValueList<T> getValues ();
//VEC OP grad
//TARGET F
//TYPE net.myorb.math.expressions.symbols.DefinedFunction
//BODY ( x ^ 2 + y - z , x - y , z - x ^ 2 ) 


//vector value

//[(2, 2, 3)]
//DATA net.myorb.math.expressions.ValueListStorage
//ValueManager.GenericValueList getValues ()
//VEC OP grad
//TARGET H
//TYPE net.myorb.math.expressions.symbols.DefinedFunction
//BODY ( ( V # 0 ) ^ 2 + V # 1 - V # 2 , V # 0 - V # 1 , V # 2 - ( V # 0 ) ^ 2 ) 


//complex value

//((3 + 4*i))
//DATA net.myorb.math.expressions.DimensionedValueStorage
//RawValueList<T> getValues ();
//VEC OP grad
//TARGET K
//TYPE net.myorb.math.expressions.symbols.DefinedFunction
//BODY 1 - c ^ 2 

