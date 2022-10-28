
package net.myorb.math.computational;

import net.myorb.math.computational.integration.polylog.ComplexExponentComponents;
import net.myorb.math.computational.integration.polylog.CyclicAspects;

import net.myorb.math.complexnumbers.ComplexFoundation.Complex;
import net.myorb.math.complexnumbers.ComplexValue;

import java.util.List;
import java.util.Map;

/**
 * core functions for polylog integrals
 * @author Michael Druckman
 */
public class PolylogQuadrature extends CyclicAspects
{


	protected int
	multiplier = 10000,
	halfCycleSegments = 4,
	domainPoints = 1600,
	infinity = 50;

	public void addConfiguration (Map <String, Object> parameters)
	{
		this.multiplier = configure ("multiplier", multiplier, parameters);
		this.halfCycleSegments = configure ("segments", halfCycleSegments, parameters);
		this.domainPoints = configure ("points", domainPoints, parameters);
		this.infinity = configure ("infinity", infinity, parameters);
	}
	int configure (String name, int defaultValue, Map <String, Object> parameters)
	{
		Object parameter = parameters.get (name);
		if (parameter == null) return defaultValue;
		return Integer.parseInt (parameter.toString ());
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

