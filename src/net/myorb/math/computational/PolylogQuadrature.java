
package net.myorb.math.computational;

import net.myorb.math.computational.integration.QuadratureFunctionality;

import net.myorb.math.computational.integration.polylog.ComplexExponentComponents;
import net.myorb.math.computational.integration.polylog.CyclicQuadrature;

import net.myorb.math.complexnumbers.ComplexFoundation.Complex;
import net.myorb.math.complexnumbers.ComplexValue;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * core functions for polylog integrals
 * @author Michael Druckman
 */
public class PolylogQuadrature extends CyclicQuadrature
{


	/**
	 * add a Quadrature Method to available options
	 * @param method the method to be added
	 */
	public static void addQuadratureMethod
	(QuadratureFunctionality method) { quadratureMethods.put (method.toString (), method); }
	static Map <String, QuadratureFunctionality> quadratureMethods = new HashMap <> ();


	/*
	 * configuration parameters
	 *  for the cyclic aspects algorithms
	 */
	protected int
	multiplier = 10000,			// a scaling factor to increase cycle amplitude
	halfCycleSegments = 4,		// a divisor for the portion of a cycle to target for integration
	domainPoints = 1600,		// the number of half-cycles to evaluate in the range 0..1
	infinity = 50;				// a value for the upper bound of the definite integral


	/**
	 * @param parameters name-value pairs that establish configuration
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{
		this.multiplier = configure ("multiplier", multiplier, parameters);
		this.halfCycleSegments = configure ("segments", halfCycleSegments, parameters);
		this.domainPoints = configure ("points", domainPoints, parameters);
		this.infinity = configure ("infinity", infinity, parameters);
		this.configureQuadratureMethod (parameters);
	}
	int configure (String name, int defaultValue, Map <String, Object> parameters)
	{
		Object parameter = parameters.get (name);
		if (parameter == null) return defaultValue;
		return Integer.parseInt (parameter.toString ());
	}


	/**
	 * @param parameters name-value pairs that establish segment quadrature configuration
	 */
	public void configureQuadratureMethod (Map <String, Object> parameters)
	{
		Object methodName = parameters.get ("method");
		if (methodName != null) establish (quadratureMethods.get (methodName), parameters);
	}
	public void establish (QuadratureFunctionality method, Map <String, Object> parameters)
	{
		if (method == null)
		{ throw new RuntimeException ("Unrecognized quadrature method"); }
		this.setIntegrationAlgorithm (method.getQuadratureImplementation (parameters));
	}


	/**
	 * calculate integral
	 * @param f function to treated as integrand
	 * @param c cycle points computed from parameter sigma
	 * @return the calculated integral
	 */
	public double computeIntegral (FunctionBody f, List <Double> c)
	{
		this.setFunction (f);
		double I = integralOver (c);

		if (TRC)
		{
			System.out.println ();
			System.out.println (c);
			System.out.println ();

			System.out.print ("I=" + I);
			System.out.print (" - N=" + c.size ());
			System.out.print (" - E=" + this.getAggregateError ());
			System.out.print (" - O=" + this.getEvaluationCount ());

			System.out.println ();
			System.out.println ();
		}

		return I;
	}


	/**
	 * @param formula the implementation of the mu factor
	 * @param s parameter to the integral
	 * @return computed integral
	 */
	public ComplexValue<Double> computeCauchySchlomilch
		(
			ComplexExponentComponents formula,
			ComplexValue<Double> s
		)
	{
		double sigma = s.Im (), Re, Im;
		List <Double> c = computeCycleSyncPoints
				(infinity, sigma, halfCycleSegments, domainPoints);
		Re = computeIntegral ( (x) -> formula.cosSigmaTmu (x, s) * multiplier, c);
		Im = computeIntegral ( (x) -> formula.sinSigmaTmu (x, s) * multiplier, c);
		return new Complex (Re / multiplier, Im / multiplier);
	}


}

