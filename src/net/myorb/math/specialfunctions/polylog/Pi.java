
package net.myorb.math.specialfunctions.polylog;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.complexnumbers.CommonComplexFunctionBase;

import net.myorb.math.computational.integration.polylog.GaussPi;
import net.myorb.math.computational.PolylogQuadrature;

import java.util.Map;

/**
 * Pi function computed from Gauss integral
 * @author Michael Druckman
 */
public class Pi extends CommonComplexFunctionBase
{


	// PI(z) = GAMMA(z+1) = z*GAMMA(z) = INTEGRAL [0 <= t <= INFINITY] ( exp(-t) * t^z * <*> t )

	// Pi () { super (1600, 4, 50, 1E-10, false); } // 1600, 4, 50, 1E-8, false used in successful GAMMA tests


	public Pi () { super ("PI"); this.PQ = new PolylogQuadrature (); }


	/**
	 * evaluation of Gauss PI integral at parameter
	 * @param s parameter to function
	 * @return computed value
	 */
	public ComplexValue<Double> evaluateGaussPiIntegralAt (ComplexValue<Double> s)
	{ return PQ.computeCauchySchlomilch (new GaussPi (), s); }
	protected PolylogQuadrature PQ;


	/*
	 * implementation of function
	 */

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> z)
	{
		return evaluateGaussPiIntegralAt (z);
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

