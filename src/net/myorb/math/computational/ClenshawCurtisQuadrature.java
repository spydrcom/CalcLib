
package net.myorb.math.computational;

import net.myorb.math.computational.dct.*;

import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.DataConversions;
import net.myorb.data.abstractions.Function;

import java.util.List;

/**
 * implementation algorithms of Clenshaw-Curtis quadrature
 * @param <T> data type used in operations
 * @author Michael Druckman
 */
public class ClenshawCurtisQuadrature<T>
{


	// INTEGRAL ||(-1,1) f(x) dx = INTEGRAL ||(0,pi) f(cos t) sin(t) dt
	// as described in:   https://en.wikipedia.org/wiki/Clenshaw-Curtis_quadrature

	/*
	 * Unlike computation of arbitrary integrals, Fourier-series integrations for periodic functions (like f(cos(theta}), by construction),
	 * up to the Nyquist frequency k=N, are accurately computed by the N+1 equally spaced and equally weighted points 
	 * (
	 *   except the endpoints are weighted by 1/2, to avoid double-counting, 
	 *   equivalent to the trapezoidal rule or the Euler–Maclaurin formula
	 * ).
	 * That is, we approximate the cosine-series integral by the type-I discrete cosine transform (DCT):	 
	 */


	/**
	 * compute integral of DCT transform
	 * @param t the transform to integrate
	 * @return the computed integral over [0,PI]
	 */
	public static double integrate (DCT.Transform<Double> t)
	{
		return integrate (t.getCoefficients ()) * t.getSlope ();
	}


	/**
	 * compute integral of DCT transform
	 * @param t the transform to integrate
	 * @param manager a data type manager for the type used
	 * @return the computed integral over [0,PI]
	 * @param <T> data type used in operations
	 */
	public static <T> T integrate (DCT.Transform<T> t, ExpressionSpaceManager<T> manager)
	{
		double integralResult = integrate (t.getCoefficients ());
		T convertedToType = manager.convertFromDouble (integralResult);
		return manager.multiply (t.getSlope (), convertedToType);
	}


	/**
	 * compute integral of Discrete Cosine Transform from coefficients
	 * @param a the coefficients calculated in DCT transform of function
	 * @return the computed integral over [0,PI]
	 */
	public static double integrate (List<Double> a)
	{
		double sum = 0.0;
		int N = a.size () - 1;
		for (int twoK = 2; twoK <= N; twoK += 2)
		{ sum += a.get (twoK) / (1.0 - twoK*twoK); }
		return a.get (0) + 2.0 * sum;
	}


	// transformed integral:  INTEGRAL ||(0,pi) f (cos t) sin (t) dt = a#0 + SIGMA [ 1 <= k <= INFINITY ] ( 2 * a#(2*k) / ( 1 - (2*k)^2 ))


	/**
	 * shortcut using a#2k coefficients of DCT
	 * @param f the function being transformed for integral [-1,1]
	 * @param N the number of samples to take to build the transform integral
	 * @return the computed integral over [-1,1]
	 */
	public static double integrate (Function<Double> f, int N)
	{
		return integrate (EvenCoefficientCalculator.computeCoefficients (f, N));
	}


	/**
	 * compute integral of f over interval [lo,hi]
	 * @param f the function being transformed for integral [lo,hi]
	 * @param lo the lo bound of the interval over which integration is computed
	 * @param hi the hi bound of the interval over which integration is computed
	 * @param N the number of samples to take to build the transform integral
	 * @return the computed integral over [lo,hi]
	 */
	public static double integrate
	(Function<Double> f, double lo, double hi, int N)
	{
		LinearCoordinateChange<Double> lcc =
			new LinearCoordinateChange<Double> (lo, hi, f, new DoubleFloatingFieldManager ());
		return integrate (lcc.functionWithAdjustedDomain (), N) * lcc.getSlope ();
	}


	/**
	 * execute DCT on function for number of samples.
	 *  function must be defined on the interval [-1,1]
	 * @param f the function being transformed to new domain
	 * @param N the number of samples to take to build the transform
	 * @return the coefficients of the new transform
	 */
	public static List<Double> computeCoefficients
			(Function<Double> f, int N)
	{
		return UnbiasedCoefficientCalculator.computeCoefficients (f, N);
	}


	/**
	 * type II transform, transform is even function
	 * @param f the function being transformed to new domain
	 * @param N the number of samples to take to build the transform
	 * @return the coefficients of the new transform
	 */
	public static List<Double> computeCoefficientsTypeII
			(Function<Double> f, int N)
	{
		return EvenCoefficientCalculator.computeCoefficients (f, N);				// 2K algorithm is smaller computation effort
	}


	/**
	 * type III transform, transform is DCT-II inverse
	 * @param f the function being transformed to new domain
	 * @param N the number of samples to take to build the transform
	 * @return the coefficients of the new transform
	 */
	public static List<Double> computeIdctCoefficients
		(Function<Double> f, int N)
	{
		throw new RuntimeException ("Inverse DCT type not supported");
	}


	/**
	 * choose component computation algorithm based on type
	 * @param f the function being transformed to new domain
	 * @param N the number of samples to take to build the transform
	 * @param t the type of DCT transform to be generated (odd, even, ...)
	 * @return the coefficients of the new transform
	 */
	public static List<Double>
	computeCoefficients (Function<Double> f, int N, DCT.Type t)
	{
		switch (t)
		{
			case  I:  return computeCoefficients (f, N);
			case III: return computeIdctCoefficients (f, N);
			case II:  return computeCoefficientsTypeII (f, N);
			default:  throw new RuntimeException ("DCT type not supported");
		}
	}


	/**
	 * construct transform for function
	 * @param f the function being transformed to new domain
	 * @param N the number of samples to take to build the transform
	 * @param t the type of transform to be generated (even, odd, ...)
	 * @return the transform object based on the coefficients
	 */
	public DCT.Transform<T> getTransform (Function<T> f, int N, DCT.Type t)
	{
		T slope = sm.getOne ();
		DataConversions<T> cvt = new DataConversions<T> (sm);
		if (f instanceof LinearCoordinateChange.StdFunction) slope = ((LinearCoordinateChange.StdFunction<T>) f).getSlope ();
		return new DCTransform<T> (computeCoefficients (cvt.toRealFunction (f), N, t), slope, sm);
	}


	/**
	 * construct transform for function
	 * @param f the function being transformed to new domain
	 * @param lo the lo end of the interval to be mapped by the transform
	 * @param hi the hi end of the interval to be mapped by the transform
	 * @param N the number of samples to take to build the transform
	 * @param t the type of transform to be generated (even, odd)
	 * @return the transform object based on the coefficients
	 */
	public DCT.Transform<T> getTransform (Function<T> f, T lo, T hi, int N, DCT.Type t)
	{
		DataConversions<T> cvt = new DataConversions<T>(sm);
		LinearCoordinateChange<T> lcc = new LinearCoordinateChange<T> (lo, hi, f, sm);
		return new DCTransform<T> (computeCoefficients (cvt.toRealFunction (lcc.functionWithAdjustedDomain ()), N, t), lcc.getSlope (), sm);
	}


	public ClenshawCurtisQuadrature (ExpressionSpaceManager<T> sm)
	{
		this.sm = sm;
	}
	ExpressionSpaceManager<T> sm;


}

