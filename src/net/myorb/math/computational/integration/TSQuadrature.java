
package net.myorb.math.computational.integration;

import net.myorb.math.computational.TanhSinhQuadratureAlgorithms;
import net.myorb.math.computational.TanhSinhQuadratureTables;

/**
 * quadrature using Tanh-Sinh algorithm
 * @author Michael Druckman
 */
public class TSQuadrature extends CommonQuadrature
		implements Quadrature.Integral
{

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#eval(double, double, double)
	 */
	public double eval (double x, double lo, double hi)
	{
		integrand.setParameter (x);
		return TanhSinhQuadratureAlgorithms.Integrate
		(integrand, lo, hi, targetAbsoluteError, stats);
	}
	protected double targetAbsoluteError;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#getErrorEstimate()
	 */
	public double getErrorEstimate ()
	{
		return stats.errorEstimate;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#getEvaluationCount()
	 */
	public int getEvaluationCount ()
	{
		return stats.numFunctionEvaluations;
	}
	protected TanhSinhQuadratureTables.ErrorEvaluation stats;

	public TSQuadrature
		(
			RealIntegrandFunctionBase integrand,
			Configuration parameters
		)
	{
		super (integrand, parameters);
		this.targetAbsoluteError = parameters.getPrecision ();
		this.stats = new TanhSinhQuadratureTables.ErrorEvaluation ();
	}

}
