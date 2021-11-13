
package net.myorb.math;

/**
 * 
 * all functionality around management of value sign (+/-)
 * 
 * @author Michael Druckman
 *
 */
public interface SignManagementOperations
{

	/**
	 * check THIS for negative value
	 * @return TRUE if THIS value is negative
	 */
	public boolean isNegative ();

	/**
	 * set sign flag to match parameter
	 * @param negative TRUE for negative value otherwise false
	 */
	public void setSign (boolean negative);

	/**
	 * invert negation sign for this object
	 */
	public void invertSign ();

	/**
	 * copy the sign flag from another sign manager
	 * @param other the other object that extends sign manager
	 */
	public void copySign (SignManagementOperations other);

	/**
	 * copy the inverted sign flag from another sign manager
	 * @param other the other object that extends sign manager
	 */
	public void copyInvertedSign (SignManagementOperations other);

	/**
	 * for products use XOR of sign flags
	 * @param factor1 the first factor of the product
	 * @param factor2 the second factor of the product
	 */
	public void setSignXor (SignManagementOperations factor1, SignManagementOperations factor2);
	

}
