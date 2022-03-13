
package net.myorb.math.computational.integration;

/**
 * expose the ability to perform integration 
 * 	over a range in the real domain space
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface RealDomainIntegration<T>
{
	/**
	 * compute the integral of the described function
	 *  over the real number domain range specified in the call
	 * @param lo the lo end of integral range in function coordinates
	 * @param hi the hi end of integral range in function coordinates
	 * @return the computed value of the integral for the specified range
	 */
	public T evalIntegralOver
	(
		double lo, double hi
	);
}
