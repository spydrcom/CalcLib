
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.*;
import net.myorb.math.expressions.evaluationstates.Arrays;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.computational.Statistics;
import net.myorb.math.matrices.*;

import java.util.ArrayList;
import java.util.List;

/**
 * implementations of algorithms computing operations on arrays and vectors
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class DimensionedDataSupport<T>
{


	/**
	 * object depends on data structures of the environment
	 * @param environment the environment object holding value management objects
	 */
	public DimensionedDataSupport (Environment<T> environment)
	{
		this.spaceManager = environment.getSpaceManager ();
		this.vectorOperations = new VectorOperations<T> (spaceManager);
		this.conversion = new ExtendedDataConversions<T> (environment);
		this.functions = new ArrayFunction<T> (environment);
		this.valueManager = environment.getValueManager ();
		this.stats = new Statistics<T>(spaceManager);
		this.setLibrary (environment);
	}
	protected ExpressionSpaceManager<T> spaceManager = null;
	protected ExtendedDataConversions<T> conversion = null;
	protected VectorOperations<T> vectorOperations = null;
	protected ValueManager<T> valueManager = null;
	protected ArrayFunction<T> functions = null;
	protected Statistics<T> stats = null;


	/*
	 * configuration of dimensioned data abstraction algorithms
	 */


	/**
	 * identify power library to use
	 * @param environment access to the evaluation environment
	 */
	public void setLibrary (Environment<T> environment)
	{
		this.abstractions = new BuiltInArrayAbstractions<T> (environment);
		abstractions.setPowerLibrary (environment.getLibrary ());
	}
	protected BuiltInArrayAbstractions<T> abstractions = null;


	/*
	 * test for dimensioned references in operation found in expression
	 */


	/**
	 * look for dimensioned data
	 * @param left left side of operator
	 * @param right right side of operator
	 * @return TRUE = dimensioned parameter is present
	 */
	public boolean dimensionedParameterPresent
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		return valueManager.isDimensioned (left) || valueManager.isDimensioned (right);
	}


	/*
	 * operator selection for vectors and matrices
	 */


	/**
	 * addition operator processing
	 * @param left left side of operator
	 * @param right right side of operator
	 * @return computed result
	 */
	public ValueManager.GenericValue dimensionedAdd
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		if (valueManager.isArray (left))
			return valueManager.newDimensionedValue (vectorAdd (valueManager.toArray (left), valueManager.toArray (right)));
		return valueManager.newMatrix (abstractions.getMatrixLibrary ().add (valueManager.toMatrix (left), valueManager.toMatrix (right)));
	}

	/**
	 * subtraction operator processing
	 * @param left left side of operator
	 * @param right right side of operator
	 * @return computed result
	 */
	public ValueManager.GenericValue dimensionedSubtract
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		if (valueManager.isArray (left))
			return valueManager.newDimensionedValue (vectorAdd (valueManager.toArray (left), vectorNegate (right)));
		return valueManager.newMatrix (abstractions.getMatrixLibrary ().add (valueManager.toMatrix (left), valueManager.toMatrix (right)));
	}

	/**
	 * multiplication operator processing
	 * @param left left side of operator
	 * @param right right side of operator
	 * @return computed result
	 */
	public ValueManager.GenericValue dimensionedMultiply
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		if (valueManager.isMatrix (left) || valueManager.isMatrix (right))
			return valueManager.newMatrix (abstractions.getMatrixLibrary ().multiply (left, right));
		return vectorMultiply (left, right);
	}


	/**
	 * exponentiation operator processing
	 * @param left left side of operator
	 * @param right right side of operator
	 * @return computed result
	 */
	public ValueManager.GenericValue dimensionedPow
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		if (!valueManager.isMatrix (left))
			throw new RuntimeException ("Invalid exponentiation operation");
		return valueManager.newMatrix (abstractions.getMatrixLibrary ().toThe
		(valueManager.toMatrix (left), valueManager.toInt (right, spaceManager)));
	}


	/*
	 * implementation of arithmetic operators applied to arrays
	 */


	/**
	 * negate the elements of a vector
	 * @param v the array to treat as a vector
	 * @return the elements of the negated vector
	 */
	public ValueManager.RawValueList<T> vectorNegate (ValueManager.GenericValue v)
	{
		return conversion.toArray (conversion.toVector (v).negate ());
	}


	/**
	 * addition of two vectors
	 * @param left the left side of the operator
	 * @param right the right side of the operator
	 * @return the elements of the sum vector
	 */
	public ValueManager.RawValueList<T> vectorAdd (List<T> left, List<T> right)
	{
		return conversion.toArray (conversion.toVector (left).plus (conversion.toVector (right)));
	}


	/**
	 * multiply vector by scalar
	 * @param scalar the discrete value of the scalar
	 * @param v the generic array value to treat as a vector
	 * @return the scaled vector converted to an array value
	 */
	@SuppressWarnings("unchecked")
	public ValueManager.GenericValue vectorScale (T scalar, ValueManager.GenericValue v)
	{
		ValueManager.Metadata metadata;
		List<T> array = conversion.toArray (conversion.toVector (v).times (scalar));
		ValueManager.GenericValue result = valueManager.newDimensionedValue (array);
		if ((metadata = v.getMetadata ()) != null)
		{
			if (metadata instanceof Arrays.Descriptor)
			{
				String op = spaceManager.toDecimalString (scalar) + " *";
				Arrays.Descriptor<T> dsc = (Arrays.Descriptor<T>) metadata;
				metadata = functions.vectorOpMetadata (op, array, dsc);
			}
			result.setMetadata (metadata);
		}
		return result;
	}


	/**
	 * vector cross product
	 *  operation implementation
	 * @param left the left side of the operator
	 * @param right the right side of the operator
	 * @return cross product result
	 */
	public ValueManager.GenericValue crossProduct
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		List<T> array = conversion.toArray
		(
			vectorOperations.crossProduct
			(
					conversion.toVector (left),
					conversion.toVector (right)
			)
		);
		return valueManager.newDimensionedValue (array);
	}


	/**
	 * multiplication operations on arrays/vectors.
	 *  identifies as vector scale or cross product
	 * @param left the left side of the operator
	 * @param right the right side of the operator
	 * @return the product array as a value
	 */
	public ValueManager.GenericValue vectorMultiply
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		ValueManager.GenericValue d = left, a = left;

		if (valueManager.isDiscrete (left)) a = right;
		else if (valueManager.isDiscrete (right)) d = right;
		else return crossProduct (left, right);

		T discrete = valueManager.toDiscrete (d);
		return vectorScale (discrete, a);
	}


	/*
	 * vector dot product and cross product operators
	 */


	/**
	 * vector dot product of two arrays
	 * @param parameters stack constructed parameter object
	 * @return the scalar dot product
	 */
	public ValueManager.GenericValue dot (ValueManager.GenericValue parameters)
	{
		ValueManager.ValueList parameterList = (ValueManager.ValueList)parameters;
		List<ValueManager.GenericValue> values = parameterList.getValues ();
		return dot (values.get(0), values.get(1));
	}


	/**
	 * vector dot product of two arrays
	 * @param left left side array reference
	 * @param right right side array reference
	 * @return computed dot product
	 */
	public ValueManager.GenericValue dot
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		Vector<T> lParm = conversion.toVector (valueManager.toArray (left));
		Vector<T> rParm = conversion.toVector (valueManager.toArray (right));
		T result = vectorOperations.dotProduct (lParm, rParm);
		return valueManager.newDiscreteValue (result);
	}


	/**
	 * dyadic product of vectors
	 * @param parameters stack constructed parameter object
	 * @return computed matrix
	 */
	public Matrix<T> dyadicProduct (ValueManager.GenericValue parameters)
	{
		ValueManager.ValueList
			parameterList = (ValueManager.ValueList)parameters;
		List<ValueManager.GenericValue> values = parameterList.getValues ();
		Vector<T> l = conversion.toVector (valueManager.toArray (values.get (0)));
		Vector<T> r = conversion.toVector (valueManager.toArray (values.get (1)));
		return vectorOperations.dyadicProduct (l, r);
	}


	/*
	 * general primitive processing for arrays
	 */


	/**
	 * array element indexing
	 * @param left the reference to the array
	 * @param right the index value identifying element
	 * @return the value of the specified element
	 */
	public ValueManager.GenericValue index (ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		if (valueManager.isMatrix (left))
		{ return abstractions.getMatrixLibrary ().index (valueManager.toMatrix (left), right); }
		else return valueManager.applyIndex (left, valueManager.toInt (right, spaceManager));
	}


	/**
	 * Append a series of arrays into one long array
	 * @param values the series of arrays to be concatenated
	 * @return the array with all values appended
	 */
	public ValueManager.GenericValue append (ValueManager.GenericValue values)
	{
		List<T> result = new ArrayList<T> ();
		ValueManager.ValueList parameterList = (ValueManager.ValueList)values;
		List<ValueManager.GenericValue> arrays = parameterList.getValues ();
		for (ValueManager.GenericValue array : arrays)
		{
			result.addAll (valueManager.toArray (array));
		}
		return valueManager.newDimensionedValue (result);
	}


	/**
	 * standard mathematical SIGMA function.
	 *  the sum of all elements of the array are computed
	 * @param values the values that comprise the array
	 * @return the scalar sum of elements
	 */
	public ValueManager.GenericValue sigma (ValueManager.GenericValue values)
	{
		T sum = spaceManager.getZero ();
		List<T> array = valueManager.toArray (values);
		for (T item : array) { sum = spaceManager.add (sum, item); }
		return valueManager.newDiscreteValue (sum);
	}


	/**
	 * standard mathematical PI function.
	 *  the product of all elements of the array are computed
	 * @param values the values that comprise the array
	 * @return the scalar product of elements
	 */
	public ValueManager.GenericValue pi (ValueManager.GenericValue values)
	{
		T product = spaceManager.getOne ();
		List<T> array = valueManager.toArray (values);
		for (T item : array) { product = spaceManager.multiply (product, item); }
		return valueManager.newDiscreteValue (product);
	}


	/**
	 * get the length of an array
	 * @param values the parameter passed to the function
	 * @return length of the array
	 */
	public ValueManager.GenericValue length (ValueManager.GenericValue values)
	{
		List<T> array = valueManager.toArray (values);
		T l = spaceManager.newScalar (array.size ());
		return valueManager.newDiscreteValue (l);
	}


}

