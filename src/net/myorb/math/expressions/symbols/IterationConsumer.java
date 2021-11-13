
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.ValueManager.GenericValue;

/**
 * processor of iteration values
 * @author Michael Druckman
 */
public interface IterationConsumer
{
	/**
	 * establish initial value
	 */
	void init ();

	/**
	 * @param value the calculated value
	 */
	void accept (GenericValue value);

	/**
	 * @param value the local variable value
	 */
	void setIterationValue (GenericValue value);

	/**
	 * @return the aggregate of the iterations
	 */
	GenericValue getCalculatedResult ();
}
