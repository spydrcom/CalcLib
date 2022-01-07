
package net.myorb.math.expressions;

import net.myorb.math.matrices.Matrix;
import net.myorb.math.complexnumbers.ComplexMarker;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.DataSequence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * conversions between internal types and Java types
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ExtendedDataConversions<T> extends DataConversions<T>
{


	/*
	 * descriptions of data types
	 */

	public enum Types {BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, OTHER, COMPLEX, BOOLEAN, MAT, VEC, TXT}
	static Map<String,Types> TYPE_MAP = new HashMap<String,Types>();
	
	// mappings of names to types

	static
	{
		TYPE_MAP.put ("int", Types.INT);
		TYPE_MAP.put ("byte", Types.BYTE);
		TYPE_MAP.put ("short", Types.SHORT);
		TYPE_MAP.put ("long", Types.LONG);
		TYPE_MAP.put ("float", Types.FLOAT);
		TYPE_MAP.put ("double", Types.DOUBLE);
		TYPE_MAP.put ("boolean", Types.BOOLEAN);
		TYPE_MAP.put ("complex", Types.COMPLEX);
		TYPE_MAP.put ("class java.lang.String", Types.TXT);
		TYPE_MAP.put ("class [[D", Types.MAT);
		TYPE_MAP.put ("class [D", Types.VEC);
	}

	public static Types getTypeFor (String description) { return TYPE_MAP.get (description); }


	public ExtendedDataConversions (Environment<T> environment)
	{
		super (environment.getSpaceManager ());
		this.valueManager = environment.getValueManager ();
	}
	protected ValueManager<T> valueManager;


	/**
	 * convert an array object to a data sequence
	 * @param data the generic value object holding the array
	 * @return the data sequence object with a copy of the data
	 */
	public DataSequence<T> seq (ValueManager.GenericValue data)
	{
		return seq (valueManager.toArray (data));
	}


	/**
	 * convert a parameter object to a 2D data sequence
	 * @param data the generic value object holding the arrays
	 * @return the data sequence object with a copy of the data
	 */
	public DataSequence2D<T> seq2D (ValueManager.GenericValue data)
	{
		DataSequence2D<T> ds2d = new DataSequence2D<T>();
		ValueManager.ValueList parameterList = (ValueManager.ValueList)data;
		List<ValueManager.GenericValue> values = parameterList.getValues ();
		ds2d.populate (valueManager.toArray (values.get (0)), valueManager.toArray (values.get (1)));
		return ds2d;
	}


	/**
	 * process object into value
	 * @param object the object to be converted
	 * @param type primitive data type of object
	 * @return the value as a manager generic
	 */
	public ValueManager.GenericValue convertObject (Object object, Types type)
	{
		if (object instanceof Number)
		{
			return convertNumber (object);
		}
		else if (object instanceof ComplexMarker || type == null)
		{
			return valueManager.newStructure (object);
		}
		else
		{
			switch (type)
			{
				case BOOLEAN: return convertBoolean (object);
				case TXT:     return valueManager.newText (object.toString ());
				case MAT:     return matrixFor ((double[][])object);
				case VEC:     return vectorFor ((double[])object);
				default:      return convertNumber (object);
			}
		}
	}
	public ValueManager.GenericValue convertNumber (Object from)
	{
		return valueManager.newDiscreteValue (spaceManager.convertFromDouble (((Number)from).doubleValue ()));
	}
	public ValueManager.GenericValue convertBoolean (Object from)
	{
		return valueManager.newDiscreteValue (spaceManager.newScalar (((Boolean)from).booleanValue()? 1: 0));
	}


	/**
	 * internal format is float.
	 *  convert to object of formal parameter
	 * @param value the internal representation of the value
	 * @param type the formal parameter type
	 * @return converted value object
	 */
	public Object convertToType (T value, Types type)
	{
		if (type == Types.COMPLEX)
		{
			if (value instanceof DiscreteValueStorage)
			{ return storedContents (value); }
			else return value;
		}

		Number number = spaceManager.convertToDouble (value);

		switch (type)
		{
			case INT: return number.intValue ();
			case BYTE: return number.byteValue ();
			case SHORT: return number.shortValue ();
			case LONG: return number.longValue ();
			case FLOAT: return number.floatValue ();
			case BOOLEAN: return number.intValue () != 0;
			default: return number.doubleValue ();
		}
	}
	@SuppressWarnings("unchecked")
	public Object storedContents (T value)
	{
		return ((DiscreteValueStorage<T>)value).value;
	}

	public Object convertToType (ValueManager.GenericValue value, Types type)
	{
		switch (type)
		{
			case MAT: return valueManager.toMatrix (value).toRawCells ();
			case VEC: return doubleArray (valueManager.toDimensionedValue (value).getValues ());
			default:  return convertToType (valueManager.toDiscrete (value), type);
		}
	}
	public double[] doubleArray (List<T> vals)
	{
		int n = 0;
		double[] d = new double[vals.size()];
		for (T v : vals) { d[n++] = spaceManager.convertToDouble (v); }
		return d;
	}


	/**
	 * convert returned vector to array
	 * @param v the array of float values from the call
	 * @return a generic value holding the values
	 */
	public ValueManager.GenericValue vectorFor (double[] v)
	{
		ValueManager.RawValueList<T> items;
		convertToGeneric (v, items = new ValueManager.RawValueList<T> ());
		return valueManager.newDimensionedValue (items);
	}


	/**
	 * convert returned matrix
	 * @param m the 2D-array of float values from the call
	 * @return a generic value holding the matrix
	 */
	public ValueManager.GenericValue matrixFor (double[][] m)
	{
		int rows = m.length, cols = m[0].length;
		Matrix<T> mat = new Matrix<T>(rows, cols, spaceManager);
		for (int r = 0; r < rows; r++)
		{
			for (int c = 0; c < cols; c++)
			{
				mat.set (r+1, c+1, spaceManager.convertFromDouble (m[r][c]));
			}
		}
		return valueManager.newMatrix (mat);
	}


}


