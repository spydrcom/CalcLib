
package net.myorb.math;

import net.myorb.data.abstractions.Function;

/**
 * processing for calculus transforms
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public interface TransformCalculus<T>
{

	/**
	 * produce the function that is the derivative of the source
	 * @param function source polynomial to use to find the derivative
	 * @return the derivative power function
	 */
	public Function<T> getFunctionDerivative (Function<T> function);


	/**
	 * produce the function that is the anti-derivative of the source
	 * @param function source polynomial to use to find the anti-derivative
	 * @return the anti-derivative power function
	 */
	public Function<T> getFunctionIntegral (Function<T> function);



}
