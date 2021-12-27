
package net.myorb.math.computational.integration;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import java.util.ArrayList;
import java.util.Map;

/**
 * implementation of algorithms with forms 
 *  of two sections each computed with an integral
 * @author Michael Druckman
 */
public class SectionedAlgorithm implements Function<Double>
{

	/**
	 * a description for each term of the algorithm.
	 * the model assume the presence of an integral per term.
	 * a coefficient is required per term but may be 1, (-1)^n would be part of the function.
	 * additionally a function of the same domain as the integral may be an included factor, null if not present.
	 * the parameter hash is allowed individually for each term to specify different algorithms.
	 * the lo and hi range value for the integration are separate for each term.
	 * ranges may be the same but must be specified for each term.
	 */
	public static class SectionDescription implements Function<Double>
	{

		public SectionDescription
			(
				double coefficient,
				Function<Double> dependentFunction,
				RealIntegrandFunctionBase integrand,
				Map<String,Object> parameters,
				double lo, double hi
			)
		{
			this.coefficient = coefficient; this.dependentFunction = dependentFunction;
			this.integral = new Quadrature (integrand, parameters).getIntegral ();
			this.integrand = integrand; this.lo = lo; this.hi = hi;
		}
		Quadrature.Integral integral;

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
		 */
		public Double eval (Double x)
		{
			double computed = coefficient;
			if (dependentFunction != null) computed *= dependentFunction.eval (x);
			computed *= integral.eval (x, lo, hi);
			return computed;
		}
		Function<Double> dependentFunction;
		double lo, hi, coefficient;

		public SpaceDescription<Double> getSpaceDescription () { return integrand.getSpaceDescription (); }
		public SpaceManager<Double> getSpaceManager () { return integrand.getSpaceDescription (); }
		RealIntegrandFunctionBase integrand;

	}

	/**
	 * add described section to list of sections
	 * @param section the described section
	 */
	public void addSection (SectionDescription section) { sections.add (section); }
	ArrayList<SectionDescription> sections = new ArrayList<>();

	/**
	 * build SectionDescription
	 *  and add to list of sections
	 * @param coefficient the coefficient for the term
	 * @param dependentFunction a factor of a function dependent on the variable
	 * @param integrand the integrand to be processed in the quadrature algorithm
	 * @param parameters the parameter hash to use in quadrature configuration
	 * @param lo the low value of the integration range for this section
	 * @param hi the high value of the integration range
	 */
	public void addSection
		(
			double coefficient,
			Function<Double> dependentFunction,
			RealIntegrandFunctionBase integrand,
			Map<String,Object> parameters,
			double lo, double hi
		)
	{
		addSection
		(
			new SectionDescription
			(
				coefficient,
				dependentFunction,
				integrand,
				parameters,
				lo, hi
			)
		);
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public Double eval (Double x)
	{
		double computed = 0.0;
		for (SectionDescription section : sections)
		{ computed += section.eval (x); }
		return computed;
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<Double> getSpaceDescription () { return RealIntegrandFunctionBase.manager; }
	public SpaceManager<Double> getSpaceManager () { return RealIntegrandFunctionBase.manager; }

}
