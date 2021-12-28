
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * wrapper for real number domain implementation.
 *  numerical integration in generic operations being prohibitive,
 *  an alternative could be to use real numbers for implementing integrands,
 *  which would force the need for conversion back-and-forth to generic.
 * @param <T> data type being converted
 * @author Michael Druckman
 */
public class RealDomainImplementation<T> implements Function<T>
{

	public RealDomainImplementation
	(ExpressionSpaceManager<T> sm)
	{ this.sm = sm; }

	/**
	 * hook for implementation with real number domain/range
	 * @param parameter the real domain value for the parameter
	 * @return the computed result in real range
	 */
	public double evalReal (double parameter)
	{
		throw new RuntimeException ("No implementation provided");
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		double digest = evalReal
			(sm.convertToDouble (x));
		return sm.convertFromDouble (digest);
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<T> getSpaceDescription () { return sm; }
	public SpaceManager<T> getSpaceManager () { return sm; }
	protected ExpressionSpaceManager<T> sm;

}
