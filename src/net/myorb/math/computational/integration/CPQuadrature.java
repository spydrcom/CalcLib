
package net.myorb.math.computational.integration;

import net.myorb.math.computational.splines.GenericSplineQuad;
import net.myorb.math.computational.splines.GenericSplineQuad.AccessToTarget;

import net.myorb.math.expressions.evaluationstates.Environment;

import java.util.Set;

/**
 * quadrature using Chebyshev Polynomial Calculus
 * @author Michael Druckman
 */
public class CPQuadrature extends CommonQuadrature
	implements Quadrature.Integral, Environment.AccessAcceptance<Double>
{


	/* (non-Javadoc)
	* @see net.myorb.math.computational.integration.Quadrature.Integral#eval(double, double, double)
	*/
	public double eval (double x, double lo, double hi)
	{
		integrand.setParameter (x);
		return integral.evalIntegralOver (lo, hi);
	}
	protected RealDomainIntegration<Double> integral;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment<Double> environment)
	{
		integral = GenericSplineQuad.findSymbol (ids, environment);
	}
	protected Set<String> ids;


	/**
	 * process the target node of the integral request
	 * @param integrand the target of the integration request
	 */
	@SuppressWarnings("unchecked")
	void connect (RealIntegrandFunctionBase integrand)
	{
		ids = GenericSplineQuad.connect((AccessToTarget<Double>) integrand);
	}


	public CPQuadrature
		(
			RealIntegrandFunctionBase integrand,
			Configuration parameters
		)
	{
		super (integrand, parameters);
		connect (integrand);
	}


}

