
package net.myorb.math.polynomial;

/**
 * for polynomials that are solutions to differential equations
 * - the solutions require values for the function Y intercept and rate of change at 0
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface InitialConditions <T>
{

	/**
	 * function Y intercept
	 * @return function at 0
	 */
	T getConstantTerm ();

	/**
	 * rate of change at 0
	 * @return function derivative at 0
	 */
	T getFirstDerivativeTerm ();

}
