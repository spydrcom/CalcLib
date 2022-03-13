
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.tree.RangeNodeDigest;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.algorithms.ClMathQuad;
import net.myorb.math.expressions.SymbolMap;

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
		integral = findSymbol (environment.getSymbolMap ());
		if (integral == null) throw new RuntimeException ("No spline found for integral");
	}


	/**
	 * search the environment symbol map for the IDs in the digest.
	 *  any spline object found in the named objects list will be used
	 * @param symbols the symbol map for the session found in the environment object
	 * @return the spline object found to be the target of the integration request
	 */
	@SuppressWarnings("unchecked")
	RealDomainIntegration<Double> findSymbol (SymbolMap symbols)
	{
		for (String id : ids)
		{
			Object symbol = symbols.get (id);
			if (symbol instanceof RealDomainIntegration)
			{
				return (RealDomainIntegration<Double>) symbol;
			}
		}
		return null;
	}
	protected Set<String> ids;


	/**
	 * collect the identifiers used in the target of the integration request
	 * @param digest the digest describing the integrand
	 */
	void connectIntegral (RangeNodeDigest<Double> digest)
	{ ids = digest.getTargetExpression ().getIdentifiers (); }


	/**
	 * process the target node of the integral request
	 * @param integrand the target of the integration request
	 */
	@SuppressWarnings("unchecked")
	void connect (RealIntegrandFunctionBase integrand)
	{ connectIntegral (( (ClMathQuad.AccessToTarget<Double>) integrand ).getTargetAccess ()); }


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

