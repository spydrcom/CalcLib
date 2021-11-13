
package net.myorb.math.expressions;

import net.myorb.math.expressions.evaluationstates.*;

import net.myorb.math.expressions.ValueManager.EmptyParameterList;

import java.util.List;

/**
 * treat array as function of domain specified in metadata.
 *  extrapolation of function values between array elements allows function to appear continuous.
 *  other functionalities available include derivative, integral, charting, and interpolation.
 *  an interval function allows a sub-domain of a function to be evaluated atonomously.
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ArrayFunction<T>
{


	/**
	 * get access to control objects from central object store
	 * @param environment the central object store object
	 */
	public ArrayFunction (Environment<T> environment)
	{
		this.conversion =
			new ExtendedDataConversions<T>
				(this.environment = environment);
		this.valueManager = environment.getValueManager ();
		this.spaceManager = environment.getSpaceManager ();
	}
	protected ExpressionSpaceManager<T> spaceManager;
	protected ValueManager<T> valueManager;
	protected Environment<T> environment;


	public ExtendedDataConversions<T>
	getExtendedDataConversions () { return conversion; }
	protected ExtendedDataConversions<T> conversion;


	/**
	 * get the metadata descriptor off a value
	 * @param value the value describing an array
	 * @return the array descriptor
	 */
	protected Arrays.Descriptor<T>
	getArrayMetadataFor (ValueManager.GenericValue value)
	{ 	
		if (valueManager.isParameterList (value))
		{
			try { value = valueManager.delist (value); }
			catch (EmptyParameterList e) { e.printStackTrace (); }
		}
		return environment.getArrayMetadataFor (value);
	}


	/*
	 * array function evaluation
	 */

	/**
	 * array contains function values.
	 *  binary operation performs evaluation on function at specified point.
	 *  left side parameter is array which represents function as extrapolated sequence.
	 *  right side parameter is the x-axis value to be evaluated in this function.
	 * @param left the array with function values and metadata
	 * @param right discrete value to evaluate function at
	 * @return the discrete function value
	 */
	public ValueManager.GenericValue arrayFunctionEval
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		Arrays.Descriptor<T> dsc =
			getArrayMetadataFor (left);				// metadata must be found attached to array
		List<T> y = valueManager.toArray (left);	// the array contains the y-axis values for the domain
		T x = valueManager.toDiscrete (right);		// right side of the operator is the x-axis value
		return arrayFunctionEval (x, y, dsc);		// evaluate function at X using descriptor
	}

	/**
	 * give evaluation parameters, compute function value
	 * @param x the x-axis coordinate at which to evaluate function
	 * @param y the values of the array that extrapolate into a continuous function
	 * @param array a descriptor for the array taken from metadata captured as array was populated
	 * @return the value of the function at x
	 */
	public ValueManager.GenericValue arrayFunctionEval
		(T x, List<T> y, Arrays.Descriptor<T> array)
	{
		array.checkConstraintsAgainst (x);			// x must be within constraint boundaries
		T xOffset = computeXaxisOffset				// compute count of array index values from first
			(x, array.getDelta (), array.getLo ());	// offset is [ ( X - array.lo ) / array.delta ] as count of deltas + fraction
		return extrapolateFrom (y, xOffset);		// extrapolate from array entry offset into Y elements list
	}

	/**
	 * extrapolate between two array elements
	 * @param y the list of array elements which constitute the function
	 * @param xOffset the computed offset into the array, characteristic is full element count, mantissa is partial element
	 * @return the extrapolated value
	 */
	public ValueManager.GenericValue extrapolateFrom (List<T> y, T xOffset)
	{
		T result;
		ExpressionSpaceManager<T> sm = spaceManager;
		int index = sm.toNumber (xOffset).intValue ();

		if (index == y.size()-1)
			result = y.get (index);
		else
		{
			T yBase = y.get (index), next = y.get (index + 1),
				dif = sm.add (next, sm.negate (yBase));
			T rem = sm.add (xOffset, sm.newScalar (-index));
			result =  sm.add (yBase, sm.multiply (rem, dif));
		}

		return valueManager.newDiscreteValue (result);		
	}

	/**
	 * compute the x-axis offset from the beginning of the array.
	 *  the computed offset into the array will be a real value which identifies an element and an offset from that element.
	 *  the characteristic of the value is the full element count, the mantissa is a fraction of the element difference.
	 *  the mapping between the two elements is assumed to be a linear equivalence, this will be an error source.
	 * @param x the value on the x-axis the function is to be evaluated at
	 * @param delta the x-axis value increment between array elements
	 * @param lo the lowest x-axis value of the domain constraint
	 * @return the computed x-axis offset
	 */
	public T computeXaxisOffset (T x, T delta, T lo)
	{
		T negLo = spaceManager.negate (lo), offLo  = spaceManager.add (x, negLo),
		 offset = spaceManager.multiply (spaceManager.invert (delta), offLo);
		return offset;
	}


	/*
	 * array function interval evaluation
	 */

	/**
	 * reduce the domain of the array to a subset specified by parameters
	 * @param parameters a parameter list object holding array, lo, and hi values
	 * @return the value representation of the array interval specified
	 */
	public ValueManager.GenericValue arrayInterval (ValueManager.GenericValue parameters)
	{
		List<ValueManager.GenericValue> values = ((ValueManager.ValueList)parameters).getValues ();
		ValueManager.DimensionedValue<T> array = valueManager.getDimensionedValue (values.get (0));
		Arrays.Descriptor<T> dsc = getArrayMetadataFor (array);

		T lo = valueManager.toDiscreteValue (values.get (1)).getValue ();
		T hi = valueManager.toDiscreteValue (values.get (2)).getValue ();
		dsc.checkConstraintsAgainst (lo); dsc.checkConstraintsAgainst (hi);

		int loIndex = indexOffset (lo, dsc), hiIndex = indexOffset (hi, dsc);
		List<T> elements = array.getValues ().subList (loIndex, hiIndex + 1);

		ValueManager.GenericValue result = valueManager.newDimensionedValue (elements);
		setMetadata (elements, arrayIntervalMetadata (lo, hi, dsc), result);
		return result;
	}
	
	/**
	 * translate a value to an element of the array
	 * @param forValue the x-axis value from the domain to translate to an array index
	 * @param dsc a descriptor for the array taken from metadata captured as array was populated
	 * @return the array index translation for the x-axis coordinate specified
	 */
	public int indexOffset (T forValue, Arrays.Descriptor<T> dsc)
	{
		T offset = computeXaxisOffset (forValue, dsc.getDelta (), dsc.getLo ());
		return spaceManager.toNumber (offset).intValue ();
	}

	/**
	 * metadata for the result
	 *  of an array interval operation
	 * @param lo the lo end of the domain constraint
	 * @param hi the hi end of the domain constraint
	 * @param source description of the source of the operation
	 * @return the descriptor for the operation result
	 */
	public ArrayFunctionDescriptor<T> arrayIntervalMetadata
			(T lo, T hi, Arrays.Descriptor<T> source)
	{
		ArrayFunctionDescriptor<T>
		dsc = source.describeReducedInterval (lo, hi, null);
		dsc.setExpression (source.getExpression ());
		return dsc;
	}


	/*
	 * array function integral computation
	 */

	/**
	 * build integral of function described by array.
	 *  second and third parameters optionally specify an interval
	 * @param parameters array and metadata holding function description
	 * @return array with metadata describing the area under the curve
	 */
	public ValueManager.GenericValue arrayIntegral (ValueManager.GenericValue parameters)
	{
		ValueManager.GenericValue p =
		(parameters instanceof ValueManager.ValueList)? arrayInterval (parameters): parameters;
		return arrayIntegralDefiniteInterval (p);
	}


	/**
	 * build integral of function described by array.
	 *  interval is the entire domain specified in the metadata
	 * @param parameter array and metadata holding function description
	 * @return array with metadata describing the area under the curve
	 */
	public ValueManager.GenericValue arrayIntegralDefiniteInterval
			(ValueManager.GenericValue parameter)
	{
		Arrays.Descriptor<T> dsc =
			getArrayMetadataFor (parameter);				// metadata must be found attached to array
		List<T> y = valueManager.toArray (parameter);		// the array contains the y-axis values for the domain

		ValueManager.RawValueList<T> elements = arrayIntegral (y, dsc.getDelta ());
		ValueManager.GenericValue result = valueManager.newDimensionedValue (elements);
		setMetadata (elements, arrayIntegralMetadata (dsc), result);
		return result;
	}

	/**
	 * each element of the new array function is computed
	 *  as the sum of the previous area computation plus the area computed of the next source element
	 * @param y the source elements taken form the array function supplied as the source of theis integration request
	 * @param delta the x-axis value increment between array elements
	 * @return the integral as an array function
	 */
	public ValueManager.RawValueList<T> arrayIntegral (List<T> y, T delta)
	{
		ValueManager.RawValueList<T>
			computed = new ValueManager.RawValueList<T> ();
		T runningSum = spaceManager.getZero ();
		for (T v : y)
		{
			T area = spaceManager.multiply (v, delta);
			runningSum = spaceManager.add (runningSum, area);
			computed.add (runningSum);
		}
		return computed;
	}

	/**
	 * construct a metadata record for the new integral result
	 * @param source the descriptor from the source array function
	 * @return a descriptor record with the metadata for the integral array function
	 */
	public ArrayFunctionDescriptor<T> arrayIntegralMetadata (Arrays.Descriptor<T> source)
	{
		return source.describeSimilarInterval ("INTEGRAL");
	}


	/*
	 * array function derivative computation
	 */

	/**
	 * build derivative of function described by array
	 * @param parameter array and metadata holding function description
	 * @return array with metadata describing the rise to run ratios
	 */
	public ValueManager.GenericValue arrayDerivative (ValueManager.GenericValue parameter)
	{
		Arrays.Descriptor<T> dsc =
			getArrayMetadataFor (parameter);				// meta-data must be found attached to array
		List<T> y = valueManager.toArray (parameter);		// the array contains the y-axis values for the domain

		ValueManager.RawValueList<T> elements = arrayDerivative (y, dsc.getDelta ());
		ValueManager.GenericValue result = valueManager.newDimensionedValue (elements);
		setMetadata (elements, arrayDerivativeMetadata (dsc), result);
		return result;
	}

	/**
	 * compute the slope between each pair of points in the source function
	 * @param y the source elements taken form the array function supplied as the source of theis derivative request
	 * @param delta the x-axis value increment between array elements
	 * @return the derivative as an array function
	 */
	public ValueManager.RawValueList<T> arrayDerivative (List<T> y, T delta)
	{
		ValueManager.RawValueList<T> elements =
				new ValueManager.RawValueList<T> ();
		T prev = y.get (0), overRun = spaceManager.invert (delta);
		for (int i = 1; i < y.size (); i++)
		{
			T next = y.get (i);
			T rise = spaceManager.add (next, spaceManager.negate (prev));
			T slope = spaceManager.multiply (rise, overRun);
			elements.add (slope);
			prev = next;
		}
		return elements;
	}

	/**
	 * construct a metadata record for the new derivative result
	 * @param source the descriptor from the source array function
	 * @return a descriptor record with the metadata for the derivative array function
	 */
	public ArrayFunctionDescriptor<T> arrayDerivativeMetadata (Arrays.Descriptor<T> source)
	{
		T halfDelta = spaceManager.multiply
		(source.getDelta (), spaceManager.invert (spaceManager.newScalar (2)));
		ArrayFunctionDescriptor<T> dsc = source.describeReducedInterval
		(
			spaceManager.add (source.getLo (), halfDelta),
			spaceManager.add (source.getHi (), spaceManager.negate (halfDelta)),
			"DERIVATIVE"
		);
		return dsc;
	}


	/**
	 * generate a macro
	 *  that can be used as a function transform
	 * @param y the values of y-axis in the array elements
	 * @param descriptor the descriptor for the array
	 * @return the macro object
	 */
	public ExpressionMacro<T> genMacro
		(
			List<T> y, Arrays.Descriptor<T> descriptor
		)
	{
		return new ArrayFunctionMacro<T>
		(
			y, descriptor, this, valueManager
		);
	}


	/**
	 * build metadata descriptions of vector operations
	 * @param op the operation done to the source vector
	 * @param elements the elements of the operation result
	 * @param sourceDescriptor the descriptor of the source
	 * @return metadata for the operation result vector
	 */
	public ValueManager.Metadata vectorOpMetadata
		(String op, List<T> elements, Arrays.Descriptor<T> sourceDescriptor)
	{
		ArrayFunctionDescriptor<T> metadata = sourceDescriptor.describeSimilarInterval (op);
		metadata.setExpressionMacro (new ArrayFunctionMacro<T> (elements, metadata, this, valueManager));
		return metadata;
	}


	/**
	 * set the result value object
	 *  with descriptive metadata and a macro
	 *  for evaluation of the function described
	 * @param elements the elements of the operation result
	 * @param function the descriptor of the array as a function
	 * @param result the value object the metadata will connect to
	 */
	public void setMetadata
	(List<T> elements, ArrayFunctionDescriptor<T> function, ValueManager.GenericValue result)
	{ result.setMetadata (function); function.setExpressionMacro (genMacro (elements, function)); }


}

