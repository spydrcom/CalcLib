
package net.myorb.testing;

import net.myorb.math.*;
import net.myorb.math.computational.Calculus;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

public class IntegrationTests<T> extends Calculus<T> implements Function<T>
{


	/**
	 * values are constructed based on type manager for components
	 * @param manager the manager for the component type
	 * @param delta x-axis increment
	 */
	@SuppressWarnings("unchecked")
	public IntegrationTests (SpaceManager<T> manager, T delta)
	{ super (manager, delta); lib = new OptimizedMathLibrary<T> (manager); setLibrary (lib); }


	/* (non-Javadoc)
	 * @see net.myorb.math.Function#f(java.lang.Object)
	 */
	public T eval (T x)
	{
		// f(x) = sqrt (1 - x^2)
		return sroot (manager.add (manager.getOne (), manager.negate (manager.multiply (x, x))));
	}
	public SpaceManager<T> getSpaceDescription () { return manager;}


	public void runTests ()
	{
		T result = integral (this, discrete (0), discrete (1));		// area from 0 - 1
		System.out.println (X (result, discrete (4)));

		result = lineIntegral (this, discrete (0), discrete (1));	// perimeter from 0 - 1
		System.out.println (X (result, discrete (2)));
	}


	/**
	 * execute tests on complex objects
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		Double delta = 1d / 10000d;
		DoubleFloatingFieldManager manager = new DoubleFloatingFieldManager ();
		IntegrationTests<Double> tests = new IntegrationTests<Double> (manager, delta);
		tests.runTests ();
	}


}

