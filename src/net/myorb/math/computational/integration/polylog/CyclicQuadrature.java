
package net.myorb.math.computational.integration.polylog;

import net.myorb.math.computational.integration.DefiniteIntegral;

import java.util.List;

/**
 * perform quadrature for function with known cyclic aspects
 * @author Michael Druckman
 */
public class CyclicQuadrature extends CyclicAspects
{


	/*
	 * algorithm to use and configuration processing
	 */


	/**
	 * change the integration algorithm being used
	 * @param integrationAlgorithm the algorithm to be used
	 */
	public void setIntegrationAlgorithm (DefiniteIntegral integrationAlgorithm)
	{ this.integrationAlgorithm = integrationAlgorithm; }
	protected DefiniteIntegral integrationAlgorithm;


	/**
	 * identify a target error value.
	 * - the smaller the target the more work done
	 * - too small and the algorithm falls apart and results go crazy
	 * @param to use this value for target error
	 */
	public void setTargetError (double to)
	{
		this.integrationAlgorithm.setTargetError (to);
	}


	/**
	 * compute function integral
	 * @param lo the low end of an integration range
	 * @param hi the high end of an integration range
	 * @return the calculated integral
	 */
	public double eval (double lo, double hi)
	{
		double result = integrationAlgorithm.eval (f, lo, hi);

		if (TRC)
		{
			System.out.println (lo + ".." + hi);
			System.out.println ("\t : E=" + integrationAlgorithm.getErrorEstimate ());
			System.out.println ("\t O=" + integrationAlgorithm.getEvaluationCount ());
			System.out.println ("\t I=" + result);
		}

		return result;
	}


	/*
	 * segmented integral calculus
	 */


	/**
	 * compute series of integrals over domain points
	 * @param points the list of domain points to establish the series
	 * @return the calculated integral
	 */
	public double integralOver (List <Double> points)
	{
		aggregateError = 0; evaluations = 0;
		double h, result = 0, l = points.get (0);
		for (int i = 1; i < points.size (); i++)
		{
			result += eval (l, h = points.get (i));
			evaluations += integrationAlgorithm.getEvaluationCount ();
			aggregateError += integrationAlgorithm.getErrorEstimate ();

			if (TRC)
			{
				System.out.println ("\t AGG=" + result);
				System.out.println ();
			}

			l = h;
		}
		return result;
	}


	/*
	 * meta-data for processing
	 */


	public double getAggregateError () { return aggregateError; }
	public int getEvaluationCount () { return evaluations; }
	// the aggregate error and count of evaluations
	protected double aggregateError;
	protected int evaluations;


	/**
	 * initialize quadrature structures
	 */
	public CyclicQuadrature ()
	{
		this.integrationAlgorithm = new PolylogQuadratureUsingTSH ();
		this.integrationAlgorithm.setTargetError (1E-10);
	}


}

