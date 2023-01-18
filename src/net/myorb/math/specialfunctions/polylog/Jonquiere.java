
package net.myorb.math.specialfunctions.polylog;

import net.myorb.math.computational.integration.polylog.JonquierePolylog;

import net.myorb.math.complexnumbers.CommonFunctionBase;
import net.myorb.math.complexnumbers.ComplexValue;

import net.myorb.data.abstractions.Function;

import java.util.Map;

/**
 * Li function computed from Jonquiere equations
 * @author Michael Druckman
 */
public class Jonquiere extends CommonFunctionBase
{


	public Jonquiere () { super ("Li"); }


	/*
	 * implementation of function
	 */

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval
		(ComplexValue<Double> z) { return Li.eval (z); }
	protected Function < ComplexValue<Double> > Li;


	/*
	 * accept configuration
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CommonFunctionBase#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{
		int s; // order of Li to be used
		super.addConfiguration (parameters);
		try { s = Integer.parseInt (parameters.get ("s").toString ()); }
		catch (Exception e) { throw new RuntimeException (ORDER_ERROR_TEXT); }
		Object value = parameters.get ("terms");

		if (value == null)
			Li = JonquierePolylog.Li (s);
		else Li = JonquierePolylog.Li (s, Integer.parseInt (value.toString ()));
	}
	static String ORDER_ERROR_TEXT = "Configration parameter 's' must identify the order of Li desired";


}

