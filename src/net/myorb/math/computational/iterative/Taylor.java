
package net.myorb.math.computational.iterative;

import net.myorb.math.computational.Combinatorics;

import net.myorb.math.SpaceManager;

/**
 * approximate a function using series expansion methods of Brock Taylor
 * @author Michael Druckman
 */
public class Taylor <T> extends IterationFoundations <T>
{


	//  f(x) = SIGMA [ 0 <= n <= INFINITY ] ( f'n(0)/n! * x^n )


	public Taylor
	(SpaceManager <T> manager) { this.manager = manager; }
	protected SpaceManager <T> manager;


	/**
	 * compute the specified term and add into the summation
	 * @param k the value of the summation index
	 */
	public void applyIteration (int k, T kTHderivative)
	{
		this.setX (manager.newScalar (k));
		this.setDerivativeAtX (kTHderivative);
		T kF = combo.factorial (manager.newScalar (k));
		this.setDelta (manager.multiply (kTHderivative, manager.invert (kF)));
		this.summation = manager.add (this.summation, this.getDelta ());
	}
	public T summation;


	/**
	 * run the next iteration in the series
	 * @param kTHderivative the next derivative value
	 */
	public void applyIteration (T kTHderivative)
	{
		applyIteration ( k += 1, kTHderivative );
		System.out.println (this);
		System.out.println ();
	}
	int k;


	/**
	 * iteration index starts at zero
	 * - summation is also initialized to zero
	 */
	public void initializeSummation (T derivative0)
	{
		this.summation = manager.getZero ();
		this.combo = new Combinatorics <> (manager, null);
		applyIteration ( k = 0, derivative0 );
	}
	protected Combinatorics <T> combo;


	/*
	 * contribution to the trace display
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.iterative.IterationFoundations#add(java.lang.StringBuffer)
	 */
	public void add (StringBuffer buffer)
	{
		buffer.append ("Sigma = ").append (toString (summation));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.iterative.IterationFoundations#toString(java.lang.Object)
	 */
	public String toString (T x) { return manager.toDecimalString (x); }


}

