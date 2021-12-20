
package net.myorb.math.computational;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * the most simple and basic form of integration by Trapezoid slices
 * @param <T> the data type for the calculations
 * @author Michael Druckman
 */
public class TrapezoidIntegration<T>
{

	public TrapezoidIntegration (Function<T> integrand, boolean adjusted)
	{
		this (integrand); this.includeAdjustment = adjusted;
	}
	public TrapezoidIntegration (Function<T> integrand)
	{
		this.integrand = integrand;
		this.mgr = integrand.getSpaceManager ();
		this.MINUS_HALF = mgr.invert (mgr.newScalar (-2));
	}
	boolean includeAdjustment = true;
	Function<T> integrand;
	SpaceManager<T> mgr;
	T MINUS_HALF;

	/**
	 * @param lo the lo end of the computation
	 * @param hi the hi end of the computation
	 * @param delta the delta between samples for the computation
	 * @return the computed result
	 */
	public T eval (T lo, T hi, T delta)
	{
		T x = lo,
			sum = mgr.getZero (), last = sum;
		if (mgr.lessThan (hi, lo)) return sum;
		T first = integrand.eval (x), next = first;

		while (true)
		{
			last = next;
			sum = mgr.add (sum, last);
			x = mgr.add (x, delta);
			if (mgr.lessThan (hi, x)) break;
			next = integrand.eval (x);
		}

		// first and last have been added into sum fully
		// must adjust by removing 1/2 their sum from the total
		// an alternative but valid approach to the Trapezoid Rule
		// https://en.wikipedia.org/wiki/Trapezoidal_rule

		if (includeAdjustment)
		{
			T adjust = mgr.multiply
				(mgr.add (first, last), MINUS_HALF);
			sum = mgr.add (sum, adjust);
		}

		return mgr.multiply (sum, delta);
	}

}
