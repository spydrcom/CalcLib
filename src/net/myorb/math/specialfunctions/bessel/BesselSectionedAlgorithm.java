
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.computational.integration.RealIntegrandFunctionBase;
import net.myorb.math.computational.integration.SectionedAlgorithm;
import net.myorb.math.computational.integration.Configuration;

import net.myorb.utilities.Clib;

import java.util.Map;

/**
 * describe Bessel integral equations as sum of two terms
 * @author Michael Druckman
 */
public class BesselSectionedAlgorithm extends SectionedAlgorithm
{

	public static class IntegrationConfiguration extends Configuration {}

	/**
	 * extended Bessel integrand holds value of order (alpha)
	 */
	public static class BesselSectionIntegrand extends RealIntegrandFunctionBase
	{
		public BesselSectionIntegrand (double a) { this.a = a; }
		public double getAlpha () { return a; }
		protected double a;
	}

	public BesselSectionedAlgorithm
		(
			BesselSectionIntegrand section1,
			BesselSectionIntegrand section2,
			Map<String,Object> parameters,
			int infinity
		)
	{
		addFirstSection (section1, parameters);
		addSecondSection (section2, parameters, infinity);
	}

	/**
	 * INTEGRAL [0 &lt; t &lt; pi] ( integrand (x, t) * delta t )
	 * @param integrand a function to integrate over t shaped by x
	 * @param parameters description of the quadrature algorithm
	 */
	public void addFirstSection
		(
			BesselSectionIntegrand integrand,
			Map<String,Object> parameters
		)
	{
		this.addSection (1, null, integrand, parameters, 0, Math.PI);
	}

	/**
	 * INTEGRAL [0 &lt; t &lt; INFINITY] ( integrand (x, t) * delta t )
	 * @param integrand a function to integrate over t shaped by x
	 * @param parameters description of the quadrature algorithm
	 * @param infinity the approximation value for infinity
	 */
	public void addSecondSection
		(
			BesselSectionIntegrand integrand,
			Map<String,Object> parameters,
			int infinity
		)
	{
		this.addSection (-1, null, integrand, parameters, 0, infinity);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.SectionedAlgorithm#eval(java.lang.Double)
	 */
	public Double eval (Double x)
	{
		return super.eval (x) / Math.PI;
	}

}


/**
 * special extended version for sin(a*pi) coefficient
 */
class BesselCommonSectionedAlgorithm extends BesselSectionedAlgorithm
{

	public BesselCommonSectionedAlgorithm
		(
			BesselSectionIntegrand section1,
			BesselSectionIntegrand section2,
			Map<String,Object> parameters,
			int infinity
		)
	{
		super
		(
			section1, section2,
			parameters, infinity
		);
	}

	/**
	 * - sin ( a * pi ) *
	 *  INTEGRAL [0 < t < INFINITY] ( integrand (x, t) * <*> t )
	 * @param integrand a function to integrate over t shaped by x
	 * @param parameters description of the quadrature algorithm
	 * @param infinity the approximation value for infinity
	 */
	public void addSecondSection
		(
			BesselSectionIntegrand integrand,
			Map<String,Object> parameters,
			int infinity
		)
	{
		double a;
		a = integrand.getAlpha ();
		if (Clib.isint (a)) return;				// sin (n * pi) == 0

		double c = - Math.sin (a * Math.PI);
		this.addSection (c, null, integrand, parameters, 0, infinity);
	}

}

