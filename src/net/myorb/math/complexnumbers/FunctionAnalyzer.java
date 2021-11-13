
package net.myorb.math.complexnumbers;

import net.myorb.math.Polynomial;

import java.util.List;

/**
 * analyze a function and determine roots
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface FunctionAnalyzer<T>
{
	/**
	 * get the roots of a polynomial
	 * @param polynomial the function to be analyzed
	 * @return a list of roots
	 */
	List<T> analyze (Polynomial.PowerFunction<T> polynomial);
}
