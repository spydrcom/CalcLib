
package net.myorb.math.computational.integration;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.splines.GenericSplineQuad.AccessToTarget;

import net.myorb.math.expressions.algorithms.ParameterManager;
import net.myorb.math.expressions.algorithms.QuadratureBase;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.tree.RangeNodeDigest;
import net.myorb.math.expressions.DataConversions;

import net.myorb.math.specialfunctions.Gamma;
import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

/**
 * configuration object for Liouville Calculus algorithms
 * @author Michael Druckman
 */
public class LiouvilleCalculus <T>
	implements Quadrature.Integral, Quadrature.UsingTransform <T>,
		Environment.AccessAcceptance <T>
{


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


	public LiouvilleCalculus
		(
			RealIntegrandFunctionBase integrand,
			Configuration parameters
		)
	{
		this.quad = parameters.getParameter ("quad");
		this.integrand = integrand;
	}
	protected RealIntegrandFunctionBase integrand;
	protected String quad;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.UsingTransform#constructIntegral(net.myorb.math.expressions.tree.RangeNodeDigest, net.myorb.math.computational.Parameterization.Hash)
	 */
	public Quadrature.Integral constructIntegral
		(
			RangeNodeDigest <T> digest,
			Parameterization.Hash options
		)
	{
		this.integrand = new FracQuadIntegrand <T> (digest, options, environment);
		this.integral = ( (QuadratureBase.AlgorithmExposure) environment.getSymbolMap ().get (quad) )
					.getAlgorithm ().getIntegral (integrand);
		environment.provideAccessTo (this.integral);
		return this;
	}
	protected Quadrature.Integral integral;


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


/**
 * function description of expression in integral target
 * @param <T> data type being processed
 */
class FracQuadIntegrand <T>
	extends RealIntegrandFunctionBase
	implements AccessToTarget
{

	FracQuadIntegrand
		(
			RangeNodeDigest <T> digest,
			Parameterization.Hash options,
			Environment <T> environment
		)
	{
		digest.initializeLocalVariable ();
		this.cvt = environment.getConversionManager ();
		this.parameters = new ParameterManager <T> (environment);
		this.prepareMu (digest, options);
		this.digest = digest;
	}

	/**
	 * compute value of specified constant
	 * @param source text of a formula
	 * @return the double equivalent
	 */
	double eval (String source)
	{
		parameters.setExpression (source);
		return cvt.toDouble (parameters.eval ());
	}
	protected ParameterManager <T> parameters;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.ClMathQuad.AccessToTarget#getTargetAccess()
	 */
	public RangeNodeDigest <T> getTargetAccess () { return digest; }
	protected RangeNodeDigest <T> digest;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.RealIntegrandFunctionBase#eval(java.lang.Double)
	 */
	public Double eval (Double t)
	{
		digest.setLocalVariableValue (cvt.toGeneric (t));
		double integrandValue = cvt.toDouble (digest.evaluateTarget ());
		return integrandValue * mu (t);
	}
	protected DataConversions <T> cvt;
	
	/**
	 * compute the values needed for the mu computation
	 * @param digest the digest of the integrand consumer
	 * @param options the options specified on the consumer
	 */
	void prepareMu
		(
			RangeNodeDigest <T> digest,
			Parameterization.Hash options
		)
	{
		this.GAMMA = new Gamma ();
		this.orderText = options.get ("order").toString ();
		this.upperBound = cvt.toDouble (digest.getHiBnd ());
		double orderValue = eval (this.orderText);
		this.parameterize (orderValue);

		String id = null;
		if (orderValue < 0)
		{ this.orderText = this.orderText.substring (1); id = "D"; }
		options.put ("ID", id==null? "I": id);

		options.put ("orderNode", "<mn>"+this.orderText+"</mn>");
		options.put ("alpha", alpha);
		options.put ("K", k);
	}
	protected double upperBound, exponent, coef;
	protected String orderText;
	protected Gamma GAMMA;

	/**
	 * compute constants based on requested order
	 * @param p the order of the operation
	 */
	void parameterize (double p)
	{
		this.alpha = p;
		while (alpha < 1) { alpha += 1; k++; }
		this.coef = 1 / GAMMA.eval (alpha);
		this.exponent = alpha - 1;
		this.p = p;
	}
	protected int k = 0;		// order of required derivatives	}	p + alpha = k
	protected double p = 1;		// requested order of result		}	and
	protected double alpha;		// order of the integral			}	alpha > 1 to avoid asymptote

	/**
	 * the factor from the Cauchy equation
	 * @param t the value of the integrand variable
	 * @return the computed mu factor
	 */
	double mu (double t)
	{
		return coef * Math.pow (upperBound - t, exponent);
	}

}

