
package net.myorb.math.computational;

import net.myorb.data.abstractions.Function;
//import net.myorb.math.Function;
import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial.PowerFunction;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.DataConversions;

import net.myorb.math.computational.dct.*;

import java.util.List;

/**
 * Discrete Cosine Transform of function giving harmonic sequence coefficients
 * @param <T> the data type used in operations
 * @author Michael Druckman
 */
public class DCT<T> extends ChebyshevRecursiveCosineMultiples
{


	/**
	 * transform type
	 */
	public enum Type {I, II, III, IV}


	/**
	 * make coefficients available
	 */
	public interface Transform<T>
		extends LinearCoordinateChange.StdFunction<T>
	{
		List<Double> getCoefficients ();
	}


	/**
	 * cast function to Discrete Cosine Transform
	 * @param f reference to function being cast
	 * @return the transform object
	 * @param <T> data type used
	 */
	public static <T> Transform<T> toTransform (Function<T> f)
	{
		if (f instanceof Transform)
		{ return (Transform<T>) f; }
		else return null;
	}


	/**
	 * evaluate at t the value of a
	 *  function defined by coefficients in a
	 * @param t the value on the domain interval [0,PI]
	 * @param a the coefficients that define the function
	 * @return the value of the function at t
	 */
	public static double eval (double t, List<Double> a) { return eval (t, a, a.size () - 1); }

	/**
	 * compute harmonic multiples
	 * @param t the value on the domain interval [0,PI]
	 * @param a the coefficients that define the function
	 * @param N the count of harmonics to be used
	 * @return the value of the function at t
	 */
	public static double eval (double t, List<Double> a, int N) { return eval (a, multiplesOfCosOf (t, N), N); }

	/**
	 * the cosine series for f(cos t):  
	 * 		f(cos t) = a#0/2 + SIGMA [ 1 &lt;= k &lt;= INFINITY ] ( a#k cos (k * t) )
	 * @param a the list of coefficients for the function
	 * @param cosMultiples harmonic cosine list
	 * @param N count of list items
	 * @return function value
	 */
	public static double eval (List<Double> a, List<Double> cosMultiples, int N)
	{ return a.get (0) / 2 + CoefficientCalculator.dot (a, cosMultiples, 1, N); }


	/**
	 * render function based on computed coefficients
	 * @param coefficients the list of coefficients a#k computed by DCT
	 * @param sm a space manager for real data types
	 * @return a function on the interval [0,PI]
	 * @param <T> type of data in transform
	 */
	public static <T> Transform<T> getTransform
	(List<Double> coefficients, SpaceManager<T> sm)
	{ return new DCTransform<T> (coefficients, sm); }


	/**
	 * @param f function on interval [-1,1]
	 * @param N the number of samples to be used
	 * @return object implementing DCT.Transform type II
	 * @param <T> type of data in transform
	 */
	public static <T> Transform<T> transformFor (Function<T> f, int N)
	{
		ExpressionSpaceManager<T>
			sm = (ExpressionSpaceManager<T>) f.getSpaceDescription ();
		Function<Double> realFunction = new DataConversions<T> (sm).toRealFunction (f);
		return getTransform (EvenCoefficientCalculator.computeCoefficients (realFunction, N), sm);
	}


}


/**
 * a wrapper for a set of coefficients that define a function on the [0,PI] interval.
 * instances of DCTransform present interface of LinearCoordinateChange.RealFunctionObject
 * providing simple eval(x) where x must be [0,PI]
 */
class DCTransform<T> implements DCT.Transform<T>
{
	/* (non-Javadoc)
	 * @see net.myorb.math.Function#eval(java.lang.Object)
	 */
	public T eval (T t)
	{
		ExpressionSpaceManager<T> esm = (ExpressionSpaceManager<T>)sm;
		return esm.convertFromDouble (DCT.eval (esm.convertToDouble (t), a));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.DCT.Transform#getCoefficients()
	 */
	public List<Double> getCoefficients () { return a; }
	public PowerFunction<T> describeLine() { return null; }
	public T getIntercept() { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.LinearCoordinateChange.RealFunctionObject#getSlope()
	 */
	public T getSlope () { return slope; }
	public void setSlope (T slope)
	{ this.slope = slope; }
	T slope;

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<T> getSpaceDescription () { return sm; }
	public SpaceManager<T> getSpaceManager () { return sm; }

	DCTransform (List<Double> coefficients, T slope, SpaceManager<T> sm)
	{ this (coefficients, sm); this.setSlope (slope); }
	SpaceManager<T> sm;

	DCTransform (List<Double> coefficients, SpaceManager<T> sm)
	{ this.a = coefficients; this.sm = sm; }
	List<Double> a;

}



