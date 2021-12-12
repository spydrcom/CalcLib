
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.Function;
import net.myorb.math.SpaceManager;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

public abstract class RealIntegrandFunctionBase implements Function<Double>
{

	public RealIntegrandFunctionBase (double x, double a)
	{ this.a = a; this.x = x; }
	protected double x, a;


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

