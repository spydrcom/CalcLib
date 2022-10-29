
package net.myorb.math.computational.integration;

import net.myorb.math.Function;

/**
 * description of a common definite integral
 * @author Michael Druckman
 */
public interface DefiniteIntegral extends IntegralMetadata
{

	/**
	 * evaluate the integral of a function
	 * @param integrand the function that is the target of the integral
	 * @param lo the low point of the domain from which to start the computation
	 * @param hi the high point of the domain at which to end the computation
	 * @return the resulting computation
	 */
	public double eval (Function <Double> integrand, double lo, double hi);

	/**
	 * identify an error amount that will balance the compute time choice
	 * @param targetError the value to attempt to realize
	 */
	public void setTargetError (double targetError);

}
