
package net.myorb.math.specialfunctions;

import net.myorb.math.computational.ADSplineRealSegmentManager;

import net.myorb.math.Function;

/**
 * general support for Exponential Integral Functions
 * @author Michael Druckman
 */
public class ExponentialIntegral
{

	/**
	 * describe the Anti Derivative as segments covering portions of the domain
	 */
	public static class Segments extends ADSplineRealSegmentManager
	{

		public static final double[]
		UP_TO = new double[]{ -100, -10, -1, -1E-1, -1E-6, -1E-10, 1E-10, 1E-6, 1E-1, 1, 10 },
		AREA = new double[]
			{
				   -3.63628E-46,			//   -2E4 .. -100
				   -4.1569689231E-6,		//   -1E2 .. -10
				   -0.2193797774265859,		//    -10 .. -1
				   -1.603540024023,			//     -1 .. -1E-1
				  -11.4153,					//  -1E-1 .. -1E-6
				   -9.210339,				//  -1E-6 .. -1E-10		interval up to just before asymptote
					
				   -7.222555076591419E-5,	// -1E-10 ..  1E-10		offset used adjusts the computation of the root to 8E-16 error from 1E-5
//				   	0.0,					// -1E-10 ..  1E-10		ALTERNATIVE: crosses x=0 asymptote, assume net is zero for [-1E-10,1E-10]

				    9.21034137,				//  1E-10 ..  1E-6		2E-6 is a very small tick across asymptote interval
				   11.615481,				//   1E-6 ..  1E-1
				    3.51793063,				//   1E-1 ..  1
				 2490.333858425				//      1 ..  10
			};

		public Segments () { super (UP_TO, AREA); }

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.ADSplineRealSegmentManager#getFirstSegmentBase()
		 */
		public Double getFirstSegmentBase () { return -2000d; }

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.ADSplineRealSegmentManager#eval(java.lang.Double)
		 */
		public Double eval (Double x)  { return ExponentialIntegral.EiPrime (x); }
		
	}

	/*
	 * Ei(x) = exp x INTEGRAL [0<t<INF] ((exp -xt) / (1-t)) dt
	 */

	/**
	 * @param x parameter value
	 * @return INTEGRAL [-INFINITY:x] exp(t)/t dt
	 */
	public static double Ei (double x)
	{
		if (spline == null)
		{ spline = getEiSpline (); }
		return spline.eval (x);
	}
	public static Function<Double> getEiSpline ()
	{ return new Segments ().newSplineInstance (); }
	static Function<Double> spline = null;

	/*
	 * E1(x) = -gamma -ln x + INTEGRAL [0<t<x] (1 - exp -t)/t dt
	 * E1(x) = x * INTEGRAL [1<t<INFINITY] (exp -xt * ln t) dt
	 */

	/**
	 * @param x parameter value
	 * @return INTEGRAL [x:INFINITY] exp(-t)/t dt
	 */
	public static double E1 (double x)
	{
		return -Ei (-x);
	}

	/**
	 * @param x parameter value
	 * @return Ei ( ln x )
	 */
	public static double li (double x)
	{
		if (x == 0) return 0.0;
		return Ei (Math.log (x));
	}

	/**
	 * @param t parameter value
	 * @return exp ( t ) / t
	 */
	public static double EiPrime (double t)
	{
		return Math.exp (t) / t;
	}

	/**
	 * Eulerian Li function
	 * @param x parameter value
	 * @return li(x) - li(2)
	 */
	public static double Li (double x)
	{
		return li (x) - li2;
	}
	public static double li2 = 1.045163780117492784;

}


