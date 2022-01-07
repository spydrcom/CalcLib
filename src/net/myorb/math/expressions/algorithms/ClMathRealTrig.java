
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

/**
 * Implementation of TrigPow library for use with real numbers (Double).
 * @author Michael Druckman
 */
public class ClMathRealTrig extends ClMathTrig<Double>
{

	public ClMathRealTrig () { super (new TrigPowJREImpl (spaceManager)); }
	public static ExpressionSpaceManager<Double> spaceManager = new ExpressionFloatingFieldManager ();

}
