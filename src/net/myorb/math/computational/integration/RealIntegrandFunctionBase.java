
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * function implementations for use as 
 * 	integrands for quadrature operations.
 *  intended to support the form f(x,t) dt.
 * @author Michael Druckman
 */
public class RealIntegrandFunctionBase implements Function<Double>
{

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public Double eval (Double t)
	{
		throw new RuntimeException ("Integrand body not provided");
	}

	/**
	 * @param x the parameter value to run
	 */
	public void setParameter (double x) { this.x = x; }
	protected double x;

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<Double> getSpaceManager () { return manager; }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceManager<Double> getSpaceDescription () { return manager; }

	public static final ExpressionSpaceManager<Double> manager = new ExpressionFloatingFieldManager ();

}
