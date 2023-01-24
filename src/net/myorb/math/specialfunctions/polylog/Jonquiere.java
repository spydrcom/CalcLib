
package net.myorb.math.specialfunctions.polylog;

import net.myorb.math.computational.integration.polylog.JonquierePolylog;
import net.myorb.math.expressions.symbols.CommonRealDomainSubset;

import java.util.Map;

/**
 * Li function computed from Jonquiere equations
 * - generic type breaks out to object specific to session data type
 * - real domain treated as wrapper of complex version
 * @author Michael Druckman
 */
public class Jonquiere <T> extends CommonRealDomainSubset <T>
{
	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CommonFunctionBase#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{
		this.setDefiningOccurrence (new LiAnalyticContinuation ());
		super.addConfiguration (parameters);
	}
}

/**
 * complex analytic continuation of Li function
 */
class LiAnalyticContinuation extends CommonRealDomainSubset.ComplexDefinition
{
	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CommonFunctionBase#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{
		Object terms; int s;					// order of Li and number of terms
		super.addConfiguration (parameters);

		try { s = Integer.parseInt (parameters.get ("s").toString ()); }
		catch (Exception e) { throw new RuntimeException (ORDER_ERROR_TEXT); }

		if ((terms = parameters.get ("terms")) == null) this.setimplementation (JonquierePolylog.Li (s));
		else this.setimplementation (JonquierePolylog.Li (s, Integer.parseInt (terms.toString ())));
		JonquierePolylog.useStirling = parameters.get ("stirling") != null;
	}
	static String ORDER_ERROR_TEXT = "Configration parameter 's' must identify the order of Li desired";
	LiAnalyticContinuation () { super ("Li"); }
}
