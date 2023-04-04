
package net.myorb.math.polynomial;

/**
 * identify naming conventions used in specific polynomial family
 * @author Michael Druckman
 */
public interface NamingConventions
{

	/**
	 * @return the conventional name used for polynomial
	 */
	String getPolynomialNameConvention ();

	/**
	 * @return the conventional name used for coefficients
	 */
	String getCoefficientNameConvention ();

	/**
	 * @return the conventional name used for parameters
	 */
	String getParameterNameConvention ();

}
