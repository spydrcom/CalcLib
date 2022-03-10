
package net.myorb.math.computational.integration;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.Function;

/**
 * quadrature using adaptive spline algorithm (Chebyshev T-polynomial)
 * @author Michael Druckman
 */
public class ASQuadrature extends CommonQuadrature
	implements Quadrature.Integral
{

	/**
	 * spline generator must have specific quadrature feature
	 */
	public interface QuadratureEnabledSpline
	{
		/**
		 * @return computed integral for the domain over the function
		 */
		public double evalIntegral ();
	}

	/**
	 * objects that provide quadrature functionality in constructed spline models 
	 */
	public interface SplineFactory
	{
		public QuadratureEnabledSpline generateSpline
		(
			Function<Double> f, double lo, double hi,
			Parameterization configuration
		);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#eval(double, double, double)
	 */
	public double eval (double x, double lo, double hi)
	{
		integrand.setParameter (x);
		QuadratureEnabledSpline s = splineFactory.generateSpline
				(integrand, lo, hi, parameters);
		return s.evalIntegral ();
	}

	/**
	 * use factory parameter to construct object that exports SplineFactory functionality
	 */
	public void buildFactory ()
	{
		try
		{
			String factoryName = parameters.getParameter ("factory");
			Object factory = Class.forName (factoryName).newInstance ();
			splineFactory = (SplineFactory) factory;
		}
		catch (Exception e) { throw new RuntimeException ("Factory not available"); }
	}
	protected SplineFactory splineFactory;

	public ASQuadrature
		(
			RealIntegrandFunctionBase integrand,
			Configuration parameters
		)
	{
		super (integrand, parameters);
		buildFactory ();
	}

}
