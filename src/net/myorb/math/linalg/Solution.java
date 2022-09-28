
package net.myorb.math.linalg;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.matrices.Vector;

/**
 * linear algebra solutions interface
 * @author Michael Druckman
 */
public interface Solution
{

	/**
	 * request a solution
	 * @param criteria the vector to solve for
	 * @return the solution Coefficients calculated
	 */
	GeneratingFunctions.Coefficients <Double> solve (Vector <Double> criteria);

}
