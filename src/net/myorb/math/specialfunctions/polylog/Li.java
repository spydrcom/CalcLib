
package net.myorb.math.specialfunctions.polylog;

import net.myorb.math.computational.PolylogQuadrature;
import net.myorb.math.computational.integration.polylog.BoseEinstein;

import net.myorb.math.complexnumbers.CommonFunctionBase;
import net.myorb.math.complexnumbers.ComplexValue;

import java.util.Map;

/**
 * Li function computed from Bose integral
 * @author Michael Druckman
 */
public class Li extends CommonFunctionBase
{


	public Li (ComplexValue<Double> s)
	{ super ("Li"); this.PQ = new PolylogQuadrature (); }
	protected ComplexValue<Double> s;


	/**
	 * evaluation of Bose Li integral at parameter
	 * @param z parameter to function
	 * @return computed value
	 */
	public ComplexValue<Double> evaluateBoseLiIntegralAt (ComplexValue<Double> z)
	{ return PQ.computeCauchySchlomilch (new BoseEinstein (s), z); }
	protected PolylogQuadrature PQ;


	/*
	 * implementation of function
	 */

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> z)
	{
		return evaluateBoseLiIntegralAt (z);
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

