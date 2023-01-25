
package net.myorb.math.specialfunctions.polylog;

import net.myorb.math.computational.integration.polylog.JonquierePolylog;
import net.myorb.math.expressions.symbols.CommonRealDomainSubset;

import net.myorb.math.complexnumbers.ComplexSpaceCore;
import net.myorb.math.complexnumbers.ComplexValue;

import net.myorb.math.ComputationConfiguration;

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

	LiAnalyticContinuation () { super ("Li"); }

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CommonFunctionBase#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{
		try
		{
			super.addConfiguration (parameters);
			JonquierePolylog.useStirling = parameters.get ("stirling") != null;
			setimplementationFor (parameters.get ("s").toString (), parameters.get ("terms"));
		} catch (Exception e) { throw new RuntimeException (ORDER_ERROR_TEXT, e); }
	}
	static String ORDER_ERROR_TEXT = "Configration parameter 's' must identify the order of Li desired";

	/**
	 * identify the configured function
	 * @param s the specified order of the polylog function
	 * @param terms the specified number of series terms
	 */
	void setimplementationFor (String s, Object terms)
	{
		int N = countOf (terms);
		ComplexValue <Double> complexOrder;

		switch (ComputationConfiguration.typeOf (s))
		{

			case Integer:
				int order = Integer.parseInt (s);
				if (terms == null) this.setimplementation (JonquierePolylog.Li (order));
				else this.setimplementation (JonquierePolylog.Li (order, N));
				return;

			case Real:
				complexOrder = ComplexSpaceCore.RE (Double.parseDouble (s));
				break;

			case Complex:
				complexOrder = ComplexSpaceCore.parseComplex (s);
				break;

			default: throw new RuntimeException ("Invalid format in specified order");

		}

		this.setimplementation
		(
			(z) -> JonquierePolylog.complexPolylog (complexOrder, z, ComplexSpaceCore.RE (N))
		);
	}

	/**
	 * identify term count to use
	 * @param terms the parameter specified as term count
	 * @return the number of terms to use
	 */
	int countOf (Object terms)
	{
		try { return Integer.parseInt (terms.toString ()); }
		catch (Exception e) { return DEFAULT_TERM_COUNT; }
	}
	static final int DEFAULT_TERM_COUNT = 100;

}

