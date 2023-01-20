
package net.myorb.math.specialfunctions.polylog;

import net.myorb.math.computational.PolylogQuadrature;
import net.myorb.math.computational.integration.polylog.AmdeberhanEta;

import net.myorb.math.complexnumbers.CommonComplexFunctionBase;
import net.myorb.math.complexnumbers.ComplexValue;

import java.util.Map;

/**
 * Amdeberhan eta integral evaluations
 * @author Michael Druckman
 */
public class Amdeberhan extends CommonComplexFunctionBase
{


	public Amdeberhan () { this ("AMD"); }
	public Amdeberhan (String name) { super (name); this.PQ = new PolylogQuadrature (); }


	/**
	 * evaluation of Amdeberhan Eta integral at parameter
	 * @param s parameter to function
	 * @return computed value
	 */
	public ComplexValue<Double> evaluateAmdeberhanEtaIntegralAt (ComplexValue<Double> s)
	{ return PQ.computeCauchySchlomilch (new AmdeberhanEta (), s); }
	protected PolylogQuadrature PQ;


	/*
	 * implementation of function
	 */

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> z)
	{
		return evaluateAmdeberhanEtaIntegralAt (z);
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

