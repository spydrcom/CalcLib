
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.algorithms.QuadratureBase;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.symbols.AbstractVectorReduction.Range;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

/**
 * configuration object for Liouville Calculus algorithms
 * @author Michael Druckman
 */
public class LiouvilleCalculus <T> implements Quadrature.Integral, Environment.AccessAcceptance <T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.LibraryObject.InstanceGenerator#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment<T> environment)
	{
		this.manager = environment.getSpaceManager ();
		this.library = environment.getLibrary ();
		this.environment = environment;
		configure (environment);
	}
	protected ExtendedPowerLibrary<T> library;
	protected Environment<T> environment;
	protected SpaceManager<T> manager;

	public LiouvilleCalculus
		(
			RealIntegrandFunctionBase integrand,
			Configuration parameters
		)
	{
		//System.out.println ("Liouville " + parameters);
		this.quad = parameters.getParameter ("quad");
		this.integrand = integrand;
	}
	RealIntegrandFunctionBase integrand;
	String quad;

	void configure (Environment<T> environment)
	{
		//System.out.println (environment.getSymbolMap().get(quad));

		this.integral = 
		( (QuadratureBase.AlgorithmExposure) environment.getSymbolMap ().get (quad) )
		.getAlgorithm ().getIntegral (integrand);
	}
	Quadrature.Integral integral;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#eval(double, double, double)
	 */
	public double eval (double x, double lo, double hi)
	{
		return this.integral.eval (x, lo, hi);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#getErrorEstimate()
	 */
	public double getErrorEstimate ()
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#getEvaluationCount()
	 */
	public int getEvaluationCount ()
	{
		return 0;
	}

	public static String specialCaseRenderSection (Range range, NodeFormatting using, Configuration parameters)
	{
		return "";
	}

}
