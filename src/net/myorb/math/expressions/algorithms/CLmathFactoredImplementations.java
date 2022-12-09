
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.primenumbers.Factorization;

/**
 * library interface implementation for integer domain classes
 * @author Michael Druckman
 */
public class CLmathFactoredImplementations
	extends CLmathGenericImplementations <Factorization>
{

	static ExpressionFactorizedFieldManager realMgr = new ExpressionFactorizedFieldManager ();

	public CLmathFactoredImplementations ()
	{
		this (null);
	}

	public CLmathFactoredImplementations (Environment <Factorization> environment)
	{
		super (realMgr, realMgr, null, environment);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#initGamma(java.lang.String)
	 */
	public void initGamma (String parameter) {}

}
