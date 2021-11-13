
package net.myorb.math.complexnumbers;

import net.myorb.math.Function;

/**
 * implementation of Complex Quadrature,
 *  numerical integration of definite integral in complex space
 * @author Michael Druckman
 */
public class ComplexQuadrature
{

	/**
	 * complex quadrature
	 * @param integrand the function to integrate
	 * @param lo the lo-bound of definite integral
	 * @param hi the hi-bound of definite integral
	 * @return the computed integral value
	 */
	public ComplexValue<Double> quad (Function <ComplexValue<Double>> integrand, double lo, double hi)
	{
		return null;
	}

	//TODO: looking for Gauss quadrature for complex numerical integration implementation, Scipy source translation may be good candidate

}
