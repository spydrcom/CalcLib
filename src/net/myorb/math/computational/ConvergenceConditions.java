
package net.myorb.math.computational;

/**
 * determine if a change shown a series has adequately converged
 * @param <T> data type to use for determination
 * @author Michael Druckman
 */
public interface ConvergenceConditions<T>
{
	/**
	 * @param delta the change seen for an iteration
	 * @return TRUE = convergence seen, FALSE = not converged
	 */
	boolean isWithinTolerance (T delta);
}
