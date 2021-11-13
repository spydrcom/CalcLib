
package net.myorb.math.specialfunctions;

import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.math.computational.ADSplineRealSegmentManager;
import net.myorb.math.polynomial.families.ChebyshevPolynomial;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * spline calculation of error function
 * @author Michael Druckman
 */
public class Erf extends ChebyshevPolynomial<Double> implements Function<Double>

{

	public static final double
	THRESHOLD = 1000, ERF2 = 0.9953, SLOPE = (1 - ERF2) / THRESHOLD,
	SQRT_PI = Math.sqrt (Math.PI);

	/**
	 * Chebyshev polynomial coefficients for domain [0,2]
	 *   domain is offset to [-1,1] so erf(x)[0 &lt; x &lt; 2]
	 *     will translate to poly(x-1)[0 &lt; x &lt;= 2]
	 */
	public static final Double[] CHEBYSHEV_COEFFICIENTS = new Double[]
			{
					 0.66156025503653,
					 0.483056378690527,
					-0.172342735513892,
					 0.017377616976514,
					 0.008750143168885,
					-0.002925448947483,
					-0.000121293244539,
					 0.000156170476681,
					-0.000135398205416,
					-0.000014717196241,
					-0.000060971241569
			};


	public Erf ()
	{
		super (new DoubleFloatingFieldManager ());
		coefficients = (new GeneratingFunctions<Double>(manager))
		.toCoefficients (CHEBYSHEV_COEFFICIENTS);
	}
	GeneratingFunctions.Coefficients<Double> coefficients;


	/**
	 * use Clenshaw generating function
	 *  to evaluate Chebyshev polynomial for domain (0-2)
	 * @param x value of parameter within domain of (0-2)
	 * @return erf(x) as calculated by polynomial
	 */
	public Double evalByPolynomial (Double x)
	{
		Value<Double> v = forValue (x - 1);								// spline has shifted x-axis
		Value<Double> result = evaluatePolynomialV (coefficients, v);	// Chebyshev requires [0-1] domain
		return result.getUnderlying ();
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.Function#eval(java.lang.Object)
	 */
	public Double eval (Double x)
	{
		if (x < 0)
			return - eval (-x);
		else if (x == 0) return 0.0;
		else if (x > THRESHOLD) return 1.0;
		else if (x > 2) return approximateAsymptote (x);
		return evalByPolynomial (x);
	}
	double approximateAsymptote (double x)
	{
		return ERF2 + SLOPE * (x - 2);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<Double> getSpaceDescription ()
	{
		return manager;
	}


	/**
	 * describe the Anti Derivative as segments covering portions of the domain
	 */
	public static class Segments extends ADSplineRealSegmentManager
	{

		public static final double[]
		UP_TO = new double[]{ 1, 2, 3, 100 },
		AREA = new double[]
			{
				0.746824132812425 * 2 / SQRT_PI,
				0.1352572579499941 * 2 / SQRT_PI,
				0.4125957497099527E-2 * 2 / SQRT_PI,
				1.957719323773444E-5 * 2 / SQRT_PI
			};

		public Segments () { super (UP_TO, AREA); }

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.ADSplineRealSegmentManager#getFirstSegmentBase()
		 */
		public Double getFirstSegmentBase () { return 0.0; }

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.ADSplineRealSegmentManager#eval(java.lang.Double)
		 */
		public Double eval (Double x)  { return erfPrime (x); }
		
	}

	/**
	 * @param x parameter value
	 * @return 2 / sqrt(pi) * INTEGRAL [ 0 : x ] exp(-t^2) dt
	 */
	public static double erf (double x)
	{
		if (spline == null)
		{ spline = new Segments ().newSplineInstance (); }
		return spline.eval (x);
	}
	static Function<Double> spline = null;


	/**
	 * @param x parameter to function
	 * @return exp (-x^2)
	 */
	public static double erfPrime (double x)
	{
		return 2 * Math.exp (-x*x) / SQRT_PI;
	}


}
