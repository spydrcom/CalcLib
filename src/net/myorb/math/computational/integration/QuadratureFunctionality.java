
package net.myorb.math.computational.integration;

import java.util.Map;

/**
 * describe access to a Quadrature method
 * @author Michael Druckman
 */
public interface QuadratureFunctionality
{

	/**
	 * get access to a Quadrature Implementation
	 * @param parameters name-value pairs that establish configuration
	 * @return an implementation of the Definite Integral interface
	 */
	DefiniteIntegral getQuadratureImplementation (Map <String, Object> parameters);

}
