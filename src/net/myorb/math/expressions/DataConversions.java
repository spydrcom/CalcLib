
package net.myorb.math.expressions;

import net.myorb.math.computational.Fourier;
import net.myorb.math.computational.Regression;

import net.myorb.math.polynomial.families.ChebyshevPolynomial;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

import net.myorb.math.expressions.charting.DisplayGraph;

import net.myorb.math.matrices.*;
import net.myorb.data.abstractions.Function;
import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.DataSequence;
import net.myorb.math.*;

import net.myorb.data.abstractions.ManagedSpace;

import java.util.ArrayList;
import java.util.List;

/**
 * provide data conversion methods
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class DataConversions<T>
{


	/**
	 * conversions depend on space manager
	 * @param spaceManager a processor for values in the space
	 */
	public DataConversions (ExpressionSpaceManager<T> spaceManager)
	{
		this.spaceManager = spaceManager;
		this.valueManager = new ValueManager<T> ();
	}
	protected ExpressionSpaceManager<T> spaceManager;
	protected ValueManager<T> valueManager;


	/**
	 * convert generic to float
	 * @param value a generic value
	 * @return the equivalent double float
	 */
	public Double convert (T value) { return spaceManager.convertToDouble (value); }

	/**
	 * @param value managed generic value
	 * @return float equivalent
	 */
	public Double convert (ValueManager.GenericValue value) { return spaceManager.convertToDouble (valueManager.toDiscrete (value)); }

	/**
	 * @param value float value as double
	 * @return managed generic equivalent
	 */
	public ValueManager.GenericValue convert (double value) { return valueManager.newDiscreteValue (spaceManager.convertFromDouble (value)); }


	/*
	 * generic to real and real to generic
	 */


	/**
	 * convert real data to generic
	 * @param real the list of real values
	 * @param generic the converted generic list
	 */
	public void convertToGeneric (List<Double> real, List<T> generic)
	{ for (Double v : real) generic.add (spaceManager.convertFromDouble (v)); }

	public void convertToGeneric (double[] real, List<T> generic)
	{ for (Double v : real) generic.add (spaceManager.convertFromDouble (v)); }


	/**
	 * convert generic data to real
	 * @param generic the list of generic values
	 * @param real the converted real list
	 */
	public void convertToReal (List<T> generic, List<Double> real)
	{ for (T v : generic) real.add (convert (v)); }


	/**
	 * convert sequence to structure coordinates
	 * @param sequence the sequence of data values
	 * @param x the x-axis component sequence
	 * @param y the y-axis component sequence
	 */
	public void convertToStructure (List<T> sequence, List<Double> x, List<Double> y)
	{
		ExpressionComponentSpaceManager<T>
			csm = (ExpressionComponentSpaceManager<T>) spaceManager;
		//spaceManager.convertToStructure (sequence, x, y);
		for (T t : sequence)
		{
			x.add (csm.component (t, 0));
			y.add (csm.component (t, 1));
		}
	}


	/*
	 * coefficient conversions
	 */


	/**
	 * convert list of generic values to real Polynomial Coefficients
	 * @param transform the list of generic values
	 * @return a Polynomial Coefficients object
	 */
	public Polynomial.Coefficients<Double> convert (List<T> transform)
	{
		Polynomial.Coefficients<Double> c = new Polynomial.Coefficients<Double> ();
		convertToReal (transform, c);
		return c;
	}
	public DataSequence<Double> convertToSeries (List<T> transform)
	{
		DataSequence<Double> c = new DataSequence<Double> ();
		convertToReal (transform, c);
		return c;
	}
	public DataSequence<Double> convertToSeries (ValueManager.GenericValue v)
	{
		DataSequence<Double> c = new DataSequence<Double> ();
		convertToReal (valueManager.toArray (v), c);
		return c;
	}


	/*
	 * data sequences
	 */


	/**
	 * convert a generic list to a data sequence
	 * @param values the list of generic values
	 * @return a data sequence object
	 */
	public DataSequence<T> seq (List<T> values)
	{
		DataSequence<T> sequence;
		(sequence = new DataSequence<T>()).addAll (values);
		return sequence;
	}

	public DataSequence<Double> seqToDouble (DataSequence<T> data)
	{
		DataSequence<Double> converted = new DataSequence<Double> ();
		convertToReal (data, converted);
		return converted;
	}

	public DataSequence2D<Double> seq2D (DataSequence2D<T> data)
	{
		DataSequence2D<Double> converted = new DataSequence2D<Double>();
		convertToReal (data.xAxis, converted.xAxis); convertToReal (data.yAxis, converted.yAxis);
		return converted;
	}
	
	public Vector<T> fromSequence (DataSequence<T> data)
	{
		Vector<T> v;
		(v = new Vector<T> (spaceManager)).load (data);
		return v;
	}


	/*
	 * time series
	 */


	/**
	 * convert a generic list to a time series
	 * @param array the list of generic values
	 * @return a new time series object
	 */
	public Fourier.TimeSeries newTimeSeries (List<T> array)
	{
		Fourier.TimeSeries series = new Fourier.TimeSeries ();
		convertToReal (array, series);
		return series;
	}


	/**
	 * convert a generic list to Fourier Series Coefficients
	 * @param values the list of values
	 * @return Coefficients object
	 */
	public Fourier.SeriesCoefficients newFourierCoefficients (List<T> values)
	{
		Fourier.SeriesCoefficients coefficients = new Fourier.SeriesCoefficients ();
		convertToReal (values, coefficients);
		return coefficients;
	}


	/*
	 * array/vector conversion
	 */


	/**
	 * construct vector with items taken from value manager
	 * @param items a value manager generic value holding an array
	 * @return the loaded vector object
	 */
	public Vector<T> toVector (ValueManager.GenericValue items)
	{
		return toVector (valueManager.toArray (items));
	}
	public Vector<T> toVector (List<T> items)
	{
		Vector<T> v = new Vector<T> (spaceManager);
		v.load (items);
		return v;
	}


	/**
	 * convert a vector of values to a ValueManager array
	 * @param v the vector to be converted
	 * @return a ValueManager array
	 */
	public ValueManager.GenericValue vectorToArray (VectorAccess<T> v)
	{
		return valueManager.newDimensionedValue (toArray (v));
	}


	/**
	 * get values from vector object
	 * @param v vector to read from
	 * @return the raw values list
	 */
	public ValueManager.RawValueList<T> toArray (VectorAccess<T> v)
	{
		ValueManager.RawValueList<T> values =
				new ValueManager.RawValueList<T> ();
		for (int i = 1; i <= v.size (); i++)
		{ values.add (v.get (i)); }
		return values;
	}


	/**
	 * convert vector to array
	 * @param v a vector holding values
	 * @return the generic value holding array
	 */
	public ValueManager.GenericValue vectorToArray (Vector<T> v)
	{
		ValueManager.RawValueList<T> array;
		v.addToList (array = new ValueManager.RawValueList<T> ());
		return valueManager.newDimensionedValue (array);
	}


	/*
	 * wrapper for transform functions
	 */


	/**
	 * provide coordinate translation
	 *  to allow generic function to operate on real data
	 * @param transform the function defined with generic parameters
	 * @return the function wrapped to operate on real values
	 */
	public DisplayGraph.RealFunction toRealFunction (Function<T> transform)
	{
		return new RealFunction<T>(transform, spaceManager);
	}
	public Function<T> toGenericFunction (Function<Double> transform)
	{
		return new GenericFunction<T>(transform, spaceManager);
	}
	public Function<Double> toReal1DFunction (SymbolMap.ExecutableUnaryOperator transform)
	{
		return new RealFunction<T>(toFunction (transform), spaceManager);
	}
	public Function<T> toGeneric1DFunction (SymbolMap.ExecutableUnaryOperator transform)
	{
		return new GenericFunction<T>(toFunction (transform), spaceManager);
	}
	public Function<T> toSimpleFunction (SymbolMap.ExecutableUnaryOperator transform)
	{
		return new TransformWrapper<T>(transform, spaceManager, valueManager);
	}
	public Function<T> toSimpleFunction (SymbolMap.Named transform)
	{
		if (transform != null && transform instanceof SymbolMap.ParameterizedFunction)
		{ return toSimpleFunction ((SymbolMap.ParameterizedFunction) transform); }
		else throw new RuntimeException ("Invalid function reference: " + transform.getName ());
	}


	/**
	 * @param transform a transform recognized and multi-dimensional function
	 * @return transform treated as multi-dimensional function
	 */
	@SuppressWarnings("unchecked") public MultiDimensional.Function<T>
	toFunction (SymbolMap.ExecutableUnaryOperator transform)
	{ return (MultiDimensional.Function<T>) transform; }


	/*
	 * change the domain of the transform
	 */


	/**
	 * construct a polynomial
	 *  function for a regression model
	 * @param model the regression model for the polynomial
	 * @param polynomial the implementation of the polynomial type being used
	 * @return the power function
	 */
	public Function<Double> getTransform
	(Regression.Model<T> model, Polynomial<Double> polynomial)
	{ return polynomial.getPolynomialFunction (convert (model.getCoefficients ())); }
	public Function<Double> getOrdinaryTransform (Regression.Model<T> model) { return getTransform (model, getOrdinary ()); }
	public Function<Double> getChebyshevTransform (Regression.Model<T> model) { return getTransform (model, getChebyshev ()); }
	public Polynomial<Double> getChebyshev () { return new ChebyshevPolynomial<Double> (new DoubleFloatingFieldManager ()); }
	public Polynomial<Double> getOrdinary () { return new Polynomial<Double> (new DoubleFloatingFieldManager ()); }


	/**
	 * @param managed an object that maintains data type
	 * @return the expression manager for the type
	 */
	public static ExpressionSpaceManager<Double> toExpression (ManagedSpace<Double> managed)
	{ return (ExpressionSpaceManager<Double>) managed.getSpaceDescription (); }


}


/**
 * treat operator as function
 * @param <T> data type
 */
class TransformWrapper<T> implements Function<T>
{

	TransformWrapper
		(
			SymbolMap.ExecutableUnaryOperator function,
			ExpressionSpaceManager<T> spaceManager,
			ValueManager<T> valueManager
		)
	{
		this.spaceManager = spaceManager;
		this.function = function;
		this.vm = valueManager;
	}
	SymbolMap.ExecutableUnaryOperator function;
	ExpressionSpaceManager<T> spaceManager;
	ValueManager<T> vm;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceManager<T> getSpaceDescription() { return spaceManager; }
	public SpaceManager<T> getSpaceManager() { return spaceManager; }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		ValueManager.GenericValue result =
				function.execute (vm.newDiscreteValue (x));
		return vm.toDiscrete (result);
	}
	
}


/**
 * treat any function as simple function on a real number
 * @param <T> data type
 */
class GenericFunction<T> implements Function<T>
{

	GenericFunction (MultiDimensional.Function<T> functionND, ExpressionSpaceManager<T> spaceManager)
	{ this.sm = spaceManager; this.transform = null; this.functionND = functionND; }
	MultiDimensional.Function<T> functionND = null;

	GenericFunction (Function<Double> transform, ExpressionSpaceManager<T> spaceManager)
	{ this.sm = spaceManager; this.transform = transform; }
	protected ExpressionSpaceManager<T> sm;
	protected Function<Double> transform;

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#eval(java.lang.Object)
	 */
	public T eval(T x)
	{
		if (transform != null)
		{ return sm.convertFromDouble (transform.eval (sm.convertToDouble (x))); }
		List<T> dataPoint = new ArrayList<T> (); dataPoint.add (x);
		return functionND.f (dataPoint);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<T> getSpaceDescription() { return sm; }
	public SpaceManager<T> getSpaceManager() { return sm; }

}


/**
 * convert generic data to real and back
 *  to allow generic function to operate on real data
 * @param <T> type on which operations are to be executed
 */
class RealFunction<T>
	implements Function<Double>, DisplayGraph.RealFunction,
					MultiDimensional.Function<Double>
{

	public RealFunction
	(Function<T> transform, ExpressionSpaceManager<T> manager)
	{ this.spaceManager = manager; this.transform = transform; }
	ExpressionSpaceManager<T> spaceManager;
	Function<T> transform = null;

	public RealFunction
	(MultiDimensional.Function<T> transform, ExpressionSpaceManager<T> manager)
	{ this.spaceManager = manager; this.mdTransform = transform; }
	MultiDimensional.Function<T> mdTransform = null;

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#f(java.lang.Object)
	 */
	public Double eval (Double x)
	{
		if (transform == null) return f (new Double[]{x});
		T calc = transform.eval (spaceManager.convertFromDouble (x));
		return spaceManager.convertToDouble (calc);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.MultiDimensional.Function#f(T[])
	 */
	public Double f (Double... x)
	{
		List<Double> dataPoint = new ArrayList<Double> ();
		for (int i=0; i<x.length; i++) dataPoint.add (x[i]);
		return f(dataPoint);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.MultiDimensional.Function#f(java.util.List)
	 */
	public Double f(List<Double> dataPoint)
	{
		if (mdTransform == null)
			return eval (dataPoint.get (0));
		else
		{
			List<T>
			tDataPoint = new ArrayList<T> ();
			for (int i=0; i<dataPoint.size(); i++)
				tDataPoint.add (spaceManager.convertFromDouble (dataPoint.get (i)));
			return spaceManager.convertToDouble (mdTransform.f (tDataPoint));
		}
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<Double>
	getSpaceDescription () { return esm; }
	public SpaceManager<Double> getSpaceManager() { return esm; }
	ExpressionFloatingFieldManager esm = new ExpressionFloatingFieldManager ();

}

