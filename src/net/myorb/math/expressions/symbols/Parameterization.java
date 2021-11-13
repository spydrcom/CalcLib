
package net.myorb.math.expressions.symbols;

import java.util.Map;

/**
 * provide hook for configuration
 * @author Michael Druckman
 */
public interface Parameterization
{

	/**
	 * provide modification to functionality from configuration
	 * @param options text of options to use for configuration
	 */
	void addParameterization (String options);

	/**
	 * for name/value pairs (string)
	 * @param symbol the name of the association
	 * @param value the value to assign to the symbol
	 */
	void addParameterization (String symbol, String value);

	/**
	 * for name/value pairs (hash)
	 * @param options a Map of name to object
	 */
	void addParameterization (Map<String,Object> options);

}
