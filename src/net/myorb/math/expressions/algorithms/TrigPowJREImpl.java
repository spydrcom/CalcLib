
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.TrigPowImplementation;

/**
 * provide full complement of trigonometric functions using identity equations.
 *  this layer provide an object for TrigPow based on real numbers (Double)
 * @author Michael Druckman
 */
public class TrigPowJREImpl extends TrigPowImplementation<Double>
	implements TrigPowImplementation.SubAtomic<Double>
{

	public TrigPowJREImpl
	(Environment<Double> environment)
	{ this (environment.getSpaceManager ()); }

	public TrigPowJREImpl (ExpressionSpaceManager<Double> manager)
	{ super (null, manager); super.quarks = this; }

	public Double atan (Double x) { return Math.atan (x); }
	public Double atan (Double x, Double y) { return Math.atan2 (x, y); }
	public Double asin (Double x) { return Math.asin (x); }

	public Double sin (Double x) { return Math.sin (x); }
	public Double cos (Double x) { return Math.cos (x); }

	public Double pow (Double x, int n) { return Math.pow (x, n); }
	public Double sqrt (Double x) { return Math.sqrt (x); }
	
	public Double exp (Double x) { return Math.exp (x); }
	public Double ln (Double x) { return Math.log (x); }

}
