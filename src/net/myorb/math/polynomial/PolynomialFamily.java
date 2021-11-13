
package net.myorb.math.polynomial;

import net.myorb.math.FamilyOfFunctions;

/**
 * properties of a family of polynomials
 * @param <T> the data type to operate on
 * @author Michael Druckman
 */
public interface PolynomialFamily<T> extends FamilyOfFunctions<T>
{

	/**
	 * @param identifier the identifier for the kind of functions
	 * @param upTo the highest order requested
	 * @return the list of power functions
	 */
	PolynomialFamilyManager.PowerFunctionList<T> getPolynomialFunctions (String identifier, int upTo);

}

