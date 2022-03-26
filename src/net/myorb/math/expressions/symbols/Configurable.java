
package net.myorb.math.expressions.symbols;

import java.util.Map;

/**
 * configuration for object that require parameterization
 * @author Michael Druckman
 */
public interface Configurable
{

	/**
	 * send a parameter map to a configurable object
	 * @param parameters the configuration map
	 */
	void addConfiguration (Map<String, Object> parameters);

}
