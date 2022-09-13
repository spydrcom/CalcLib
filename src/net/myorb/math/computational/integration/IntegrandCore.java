
package net.myorb.math.computational.integration;

import net.myorb.math.computational.splines.GenericSplineQuad.AccessToTarget;
import net.myorb.math.computational.DerivativeApproximation;
import net.myorb.math.computational.Parameterization;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.algorithms.ParameterManager;
import net.myorb.math.expressions.tree.RangeNodeDigest;
import net.myorb.math.expressions.DataConversions;

import net.myorb.math.realnumbers.RealFunctionWrapper;
import net.myorb.math.specialfunctions.Gamma;
import net.myorb.data.abstractions.Function;

/**
 * function description of expression in integral target
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class IntegrandCore <T>
	extends RealIntegrandFunctionBase
	implements AccessToTarget
{


	public IntegrandCore
		(
			RangeNodeDigest <T> digest,
			Parameterization.Hash options,
			Environment <T> environment
		)
	{
		digest.initializeLocalVariable ();
		this.cvt = environment.getConversionManager ();
		this.parameters = new ParameterManager <T> (environment);
		this.environment = environment;
		this.GAMMA = new Gamma ();
		this.options = options;
		this.digest = digest;
	}
	protected Environment <T> environment;
	protected Parameterization.Hash options;
	protected DataConversions <T> cvt;
	protected Gamma GAMMA;


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


	/**
	 * @param derivativeSymbol the text to use to indicate a derivative
	 * @param integralSymbol the text to use to indicate a integral
	 */
	public void processOrder
		(
			String derivativeSymbol, String integralSymbol
		)
	{
		double orderValue = eval
			(this.orderText = options.get ("order").toString ());
		this.parameterize (orderValue);
		String id = integralSymbol;
	
		if (orderValue < 0)
		{
			this.orderText = positive (this.orderText);
			id = derivativeSymbol;
		}
	
		if (id == null) throw new RuntimeException ("Alpha error");
		options.put ("orderNode", "<mn>"+this.orderText+"</mn>");
		options.put ("ID", id);
	}
	String positive (String order) { return order.replace ('-', ' '); }
	protected String orderText;


	/**
	 * compute constants based on requested order
	 * @param p the order of the operation
	 */
	public void parameterize (double p) {}


	/**
	 * the factor in the integral that transforms for multiple integration (ala Cauchy)
	 * @param t the parameter to the mu algorithm
	 * @return the calculated factor
	 */
	public double mu (double t)
	{
		throw new RuntimeException ("mu factor must be implemented in this configuration");
	}
	

	/**
	 * @return the delta value from the parameterization of the library object
	 */
	public double getDelta ()
	{
		return eval (options.get ("delta").toString ());
	}


	/**
	 * prepare a derivative object for the specified function
	 * @param sourceOfDerivative the function to target
	 * @param k the order of the operation 1-2
	 */
	public void prepareDerivatives
		(
			Function <Double> sourceOfDerivative, int k
		)
	{
		double delta = getDelta ();
		this.derivatives = DerivativeApproximation.getDerivativesFor
				(sourceOfDerivative, delta);
		this.selectedDerivative = this.derivatives.forOrder (k);
	}
	protected DerivativeApproximation.Functions <Double> derivatives;
	protected Function <Double> selectedDerivative;


	/**
	 * build derivative for source function
	 * @param sourceOfDerivative the function to target
	 * @param k the order of the operation 1-2
	 * @return the derivative function
	 */
	public Function <Double> getDerivativeFor
		(
			Function <Double> sourceOfDerivative, int k
		)
	{
		prepareDerivatives (sourceOfDerivative, k);
		return selectedDerivative;
	}


	/**
	 * evaluate the digest target
	 * @param t the parameter to the integrand
	 * @return the target evaluated at the parameter
	 */
	public double evaluateTarget (double t)
	{
		digest.setLocalVariableValue (cvt.toGeneric (t));
		return cvt.toDouble (digest.evaluateTarget ());
	}


	/**
	 * @return the digest target treated as a function
	 */
	public Function <Double> getTargetFunction ()
	{
		return new RealFunctionWrapper ( (x) -> evaluateTarget (x) );
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.RealIntegrandFunctionBase#eval(java.lang.Double)
	 */
	public Double eval (Double t)
	{
		return evaluateTarget (t) * mu (t);
	}


}

