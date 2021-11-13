
package net.myorb.math.specialfunctions;

import net.myorb.math.FamilyOfFunctions;

/**
 * properties of a family of special functions
 * @param <T> the data type to operate on
 * @author Michael Druckman
 */
public interface SpecialFunctionsFamily<T> extends FamilyOfFunctions<T>
{

	/**
	 * @param kind the kind of functions of this family
	 * @param upTo the highest order requested
	 * @return the list of functions
	 */
	SpecialFunctionFamilyManager.FunctionList<T> getFunctions (String kind, int upTo);

	/**
	 * @param kind the sub-family kind
	 * @return TRUE = polynomial
	 */
	boolean isPolynomial (String kind);

}

