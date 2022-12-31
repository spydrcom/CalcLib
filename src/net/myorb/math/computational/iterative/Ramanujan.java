
package net.myorb.math.computational.iterative;

import net.myorb.math.SpaceManager;

/**
 * evaluation of the Ramanujan series for computation of PI
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class Ramanujan <T> extends IterationFoundations <T>
{


	// 1 / pi = ( 2 * sqrt(2) / 9801 ) * SIGMA [0 <= k <= INFINITY] ( (4*k)! * (1103 + 26390*k) / ((k!)^4 * 396 ^ (4*k)) )


	public Ramanujan (SpaceManager <T> manager)
	{ this.IT = new IterationTools <T> (manager); this.init (); }
	protected IterationTools <T> IT;


	/**
	 * initialization of required constants and library objects
	 */
	public void init ()
	{
		K26390 = IT.S (26390);
		K1103 = IT.S (1103);
		K396 = IT.S (396);
	}
	protected T K1103, K26390, K396;


	/**
	 * compute the value of the specified term
	 * @param k the value of the summation index
	 * @return the value of the specified term
	 */
	public T computeIteration (int k)
	{
		int k4 = 4 * k;
		T kT = IT.S (k), kF = IT.combo.factorial (k), k4F = IT.combo.factorial (k4);
		T N = IT.productOf (k4F, IT.sumOf (K1103, IT.productOf (K26390, kT)));
		T D = IT.productOf (IT.POW (kF, 4), IT.POW (K396, k4));
		return IT.productOf (N, IT.oneOver (D));
	}


	/**
	 * compute the specified term and add into the summation
	 * @param k the value of the summation index
	 */
	public void applyIteration (int k)
	{
		this.setX (IT.S (k)); this.setDelta (computeIteration (k));
		this.summation = IT.sumOf (this.summation, this.getDelta ());
	}
	protected T summation;


	/**
	 * run the next iteration in the series
	 */
	public void applyIteration ()
	{
		applyIteration ( k += 1 );
		trace ();
	}
	protected int k;


	/**
	 * iteration index starts at zero
	 * - summation is also initialized to zero
	 */
	public void initializeSummation ()
	{
		this.summation = IT.Z;
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
	public String toString (T x) { return IT.manager.toDecimalString (x); }


}

