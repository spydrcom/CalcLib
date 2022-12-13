
package net.myorb.math.computational.iterative;

import net.myorb.math.computational.Combinatorics;

import net.myorb.math.SpaceManager;

/**
 * evaluation of the Ramanujan series for computation of PI
 * @author Michael Druckman
 */
public class Ramanujan <T> extends IterationFoundations <T>
{


	// 1 / pi = ( 2 * sqrt(2) / 9801 ) * SIGMA [0 <= k <= INFINITY] ( (4*k)! * (1103 + 26390*k) / ((k!)^4 * 396 ^ (4*k)) )


	public Ramanujan
	(SpaceManager <T> manager) { this.manager = manager; }
	protected SpaceManager <T> manager;


	/**
	 * initialization of required constants and library objects
	 */
	public void init ()
	{
		this.combo =
			new Combinatorics <> (manager, null);
		K26390 = manager.newScalar (26390);
		K1103 = manager.newScalar (1103);
		K396 = manager.newScalar (396);
	}
	protected Combinatorics <T> combo;
	protected T K1103, K26390, K396;


	/**
	 * compute the value of the specified term
	 * @param k the value of the summation index
	 * @return the value of the specified term
	 */
	public T computeIteration (int k)
	{
		int k4; T kT, k4T = manager.newScalar (k4 = 4 * k);
		T kF = combo.factorial (kT = manager.newScalar (k)), k4F = combo.factorial (k4T);
		T N = manager.multiply (k4F, manager.add (K1103, manager.multiply (K26390, kT)));
		T D = manager.multiply (manager.pow (kF, 4), manager.pow (K396, k4));
		return manager.multiply (N, manager.invert (D));
	}


	/**
	 * compute the specified term and add into the summation
	 * @param k the value of the summation index
	 */
	public void applyIteration (int k)
	{
		this.setX (manager.newScalar (k)); this.setDelta (computeIteration (k));
		this.summation = manager.add (this.summation, this.getDelta ());
	}
	public T summation;


	/**
	 * run the next iteration in the series
	 */
	public void applyIteration ()
	{
		applyIteration ( k += 1 );
		System.out.println (this);
		System.out.println ();
	}
	int k;


	/**
	 * iteration index starts at zero
	 * - summation is also initialized to zero
	 */
	public void initializeSummation ()
	{
		this.summation = manager.getZero ();
		applyIteration ( k = 0 );
	}


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

