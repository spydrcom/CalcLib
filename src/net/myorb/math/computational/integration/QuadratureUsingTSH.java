
package net.myorb.math.computational.integration;

import net.myorb.math.computational.TanhSinhQuadratureAlgorithms;
import net.myorb.math.computational.TanhSinhQuadratureTables;

import net.myorb.math.Function;

/**
 * Polylog Quadrature to be performed using TSH
 * @author Michael Druckman
 */
public class QuadratureUsingTSH implements DefiniteIntegral
{


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.DefiniteIntegral#eval(net.myorb.math.Function, double, double)
	 */
	public double eval (Function <Double> integrand, double lo, double hi)
	{
		return TanhSinhQuadratureAlgorithms.Integrate
		(integrand, lo, hi, targetAbsoluteError, stats);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.IntegralMetadata#getErrorEstimate()
	 */
	public double getErrorEstimate ()
	{
		return stats.errorEstimate;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.IntegralMetadata#getEvaluationCount()
	 */
	public int getEvaluationCount ()
	{
		return stats.numFunctionEvaluations;
	}
	protected TanhSinhQuadratureTables.ErrorEvaluation stats;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.DefiniteIntegral#setTargetError(double)
	 */
	public void setTargetError (double targetError)
	{
		this.targetAbsoluteError = targetError;
	}
	protected double targetAbsoluteError;


	public QuadratureUsingTSH ()
	{
		this.stats = new TanhSinhQuadratureTables.ErrorEvaluation ();
		this.targetAbsoluteError = 1E-4;
	}


}

