
package net.myorb.math.specialfunctions;

import net.myorb.math.expressions.symbols.CommonRealDomainSubset;

import java.util.Map;

/**
 * beta function computed from Catalan equations
 * - generic type breaks out to object specific to session data type
 * - real domain treated as wrapper of complex version
 * @author Michael Druckman
 */
public class Catalan <T> extends CommonRealDomainSubset <T>
{
	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CommonFunctionBase#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{
		this.setDefiningOccurrence (new CatalanBeta ());
		super.addConfiguration (parameters);
	}
}


/**
 * complex formula for Catalan beta function
 */
class CatalanBeta extends CommonRealDomainSubset.ComplexDefinition
{
	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CommonFunctionBase#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{
		Object terms;
		super.addConfiguration (parameters);

		if ( (terms = parameters.get ("terms")) != null)
		{ termCount = Integer.parseInt (terms.toString ()); }

		this.setimplementation ( (s) -> Beta.eval ( s, termCount ) );
	}
	CatalanBeta () { super ("beta"); }
	int termCount = 100;
}

