
package net.myorb.math.computational.integration;

import net.myorb.math.computational.TanhSinhQuadratureAlgorithms;
import net.myorb.math.computational.TanhSinhQuadratureTables;

import java.util.Map;

/**
 * quadrature using Tanh-Sinh algorithm
 * @author Michael Druckman
 */
public class TSQuadrature implements Quadrature.Integral
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

	public TSQuadrature
		(
			RealIntegrandFunctionBase integrand,
			Map<String,Object> parameters
		)
	{
		this.targetAbsoluteError =
			Configuration.getPrecision (parameters);
		this.stats = new TanhSinhQuadratureTables.ErrorEvaluation ();
		this.integrand = integrand;
	}
	protected TanhSinhQuadratureTables.ErrorEvaluation stats;
	protected RealIntegrandFunctionBase integrand;
	protected double targetAbsoluteError;

}
