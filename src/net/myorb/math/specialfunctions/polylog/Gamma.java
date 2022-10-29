
package net.myorb.math.specialfunctions.polylog;

import net.myorb.math.computational.PolylogQuadrature;
import net.myorb.math.computational.integration.polylog.BernoulliGamma;

import net.myorb.math.complexnumbers.CommonFunctionBase;
import net.myorb.math.complexnumbers.ComplexValue;

import java.util.Map;

/**
 * Gamma function computed from Bernoulli integral
 * @author Michael Druckman
 */
public class Gamma extends CommonFunctionBase
{


	public Gamma () { super ("GAMMA"); this.PQ = new PolylogQuadrature (); }


	/**
	 * evaluation of Bernoulli Gamma integral at parameter
	 * @param s parameter to function
	 * @return computed value
	 */
	public ComplexValue<Double> evaluateBernoulliGammaIntegralAt (ComplexValue<Double> s)
	{ return PQ.computeCauchySchlomilch (new BernoulliGamma (), s); }
	protected PolylogQuadrature PQ;


	/*
	 * implementation of function
	 */

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> z)
	{
		return evaluateBernoulliGammaIntegralAt (z);
	}


	/*
	 * accept configuration
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CommonFunctionBase#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{
		super.addConfiguration (parameters);
		PQ.addConfiguration (parameters);
	}


}

