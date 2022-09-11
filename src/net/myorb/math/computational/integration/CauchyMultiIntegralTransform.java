
package net.myorb.math.computational.integration;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.splines.GenericSplineQuad.AccessToTarget;
import net.myorb.math.expressions.DataConversions;
import net.myorb.math.expressions.algorithms.ParameterManager;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.tree.RangeNodeDigest;
import net.myorb.math.specialfunctions.Gamma;

/**
 * function description of expression in integral target
 * @param <T> data type being processed
 */
public class CauchyMultiIntegralTransform <T>
	extends RealIntegrandFunctionBase
		implements AccessToTarget
{

	public CauchyMultiIntegralTransform
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

