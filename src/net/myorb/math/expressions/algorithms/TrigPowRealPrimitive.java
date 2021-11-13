
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.expressions.algorithms.TrigPowJREImpl;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager;

/**
 * expose TrigPowPrimitives for use in debug platform
 * @author Michael Druckman
 */
public class TrigPowRealPrimitive extends TrigPowPrimitives<Double>
{

	public static ExpressionSpaceManager<Double> spaceManager = new ExpressionFloatingFieldManager ();
	public static ValueManager<Double> valueManager = new ValueManager<Double> ();

	/**
	 * constant managers are passed to super constructor.
	 *  TrigPowImplementation is provided using JRE Quarks implementation
	 */
	public TrigPowRealPrimitive ()
	{
		super (spaceManager, valueManager, new TrigPowJREImpl (spaceManager));
	}

}
