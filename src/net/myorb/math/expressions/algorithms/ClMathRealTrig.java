
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.expressions.SymbolMap;

/**
 * Implementation of TrigPow library for use with real numbers (Double).
 * @author Michael Druckman
 */
public class ClMathRealTrig extends ClMathTrig<Double>
	implements SymbolMap.FactoryForImports
{

	public ClMathRealTrig ()
	{
		super (new TrigPowJREImpl (spaceManager));
		this.setFactory (ClMathRealTrig.class.getCanonicalName ());
	}
	public static ExpressionSpaceManager<Double> spaceManager = new ExpressionFloatingFieldManager ();

}
