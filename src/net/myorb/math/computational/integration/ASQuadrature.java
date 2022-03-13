
package net.myorb.math.computational.integration;

import net.myorb.math.computational.Spline;

/**
 * quadrature using adaptive spline algorithm (Chebyshev T-polynomial)
 * @author Michael Druckman
 */
public class ASQuadrature extends CommonQuadrature
	implements Quadrature.Integral
{


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#eval(double, double, double)
	 */
	public double eval (double x, double lo, double hi)
	{
		integrand.setParameter (x);
		Spline.Operations<Double> s = splineFactory.generateSpline
				(integrand, lo, hi, parameters);
		return s.evalIntegral ();
	}


	/**
	 * use factory parameter to construct object that exports SplineFactory functionality
	 */
	public void buildFactory ()
	{ splineFactory = Spline.buildFactoryFrom (parameters); }
	protected Spline.Factory<Double> splineFactory;


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

