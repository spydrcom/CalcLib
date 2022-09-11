
package net.myorb.math.computational.integration;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.expressions.algorithms.QuadratureBase;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.tree.RangeNodeDigest;
import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

/**
 * configuration core object for fractional Calculus algorithms
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class QuadratureCore <T>
	implements Quadrature.Integral, Quadrature.UsingTransform <T>,
		Environment.AccessAcceptance <T>
{


	public QuadratureCore (String quadratureSymbol)
	{
		this.quad = quadratureSymbol;
	}
	protected String quad = "UNKNOWN";


	/* (non-Javadoc)
	* @see net.myorb.math.expressions.symbols.LibraryObject.InstanceGenerator#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	*/
	public void setEnvironment (Environment<T> environment)
	{
		this.manager = environment.getSpaceManager ();
		this.library = environment.getLibrary ();
		this.environment = environment;
	}
	protected ExtendedPowerLibrary <T> library;
	protected Environment <T> environment;
	protected SpaceManager <T> manager;


	/**
	 * get access to an algorithm
	 * @param called the name of the symbol exposing the algorithm
	 * @return the exposure object
	 */
	public QuadratureBase.AlgorithmExposure getAccess (String called)
	{
		return (QuadratureBase.AlgorithmExposure) environment.getSymbolMap ().get (called);
	}


	/**
	 * set the local integral object
	 * @param named the symbol name for the quadrature object
	 */
	public void useQuadratureMethod (String named)
	{
		this.integral = getAccess (named)
				.getAlgorithm ().getIntegral (this.integrand);
		this.environment.provideAccessTo (this.integral);
	}


	/**
	 * set the local integrand object
	 * @param digest the digest that holds the declaration
	 * @param options the parameters for the integrand
	 */
	public void constructIntegrand
		(
			RangeNodeDigest <T> digest,
			Parameterization.Hash options
		)
	{
		throw new RuntimeException ("Integration target not set");
	}


	/**
	 * identify the target of quadrature operations
	 * @param integrand the object to operate on
	 */
	public void setIntegrand (RealIntegrandFunctionBase integrand)
	{
		this.integrand = integrand;
	}
	protected RealIntegrandFunctionBase integrand;


	/* (non-Javadoc)
	* @see net.myorb.math.computational.integration.Quadrature.UsingTransform#constructIntegral(net.myorb.math.expressions.tree.RangeNodeDigest, net.myorb.math.computational.Parameterization.Hash)
	*/
	public Quadrature.Integral constructIntegral
		(
			RangeNodeDigest <T> digest,
			Parameterization.Hash options
		)
	{
		constructIntegrand (digest, options);
		useQuadratureMethod (quad);
		return this;
	}
	protected Quadrature.Integral integral;


	/*
	 * implementation of Quadrature.Integral
	 */

	/* (non-Javadoc)
	* @see net.myorb.math.computational.integration.Quadrature.Integral#eval(double, double, double)
	*/
	public double eval (double x, double lo, double hi)
	{
		// evaluate integral using specified quadrature object
		return this.integral.eval (x, lo, hi);
	}
	
	/* (non-Javadoc)
	* @see net.myorb.math.computational.integration.Quadrature.Integral#getErrorEstimate()
	*/
	public double getErrorEstimate () { return 0; }
	
	/* (non-Javadoc)
	* @see net.myorb.math.computational.integration.Quadrature.Integral#getEvaluationCount()
	*/
	public int getEvaluationCount () { return 0; }


}

