
package net.myorb.testing;

import net.myorb.math.computational.iterative.Ramanujan;
import net.myorb.math.primenumbers.Factorization;

/**
 * evaluation of the Ramanujan series for computation of PI
 * - this is the entry point and driver for specification of the iteration count
 * @author Michael Druckman
 */
public class RamanujanTest extends Ramanujan <Factorization>
{

	RamanujanTest ()
	{
		super (FactorizationCore.mgr);
		this.init ();
	}

	/**
	 * @param iterations number of iterations to run
	 * @return the computed sum after specified iterations
	 */
	public Factorization run (int iterations)
	{
		initializeSummation ();
		for (int i=1; i<=iterations; i++)
		{ applyIteration (); }
		return summation;
	}

	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{
		FactorizationCore.init (1000*1000);
		System.out.println (new RamanujanTest ().run (15));
	}

}
