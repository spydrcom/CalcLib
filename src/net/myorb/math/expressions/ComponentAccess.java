
package net.myorb.math.expressions;

/**
 * provide for working with values that break into components
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface ComponentAccess <T>
{

	/**
	 * get the number of components making this structure
	 * @return the number of components
	 */
	int getComponentCount ();

	/**
	 * construct the generic structure from components
	 * @param components the components to use for the process
	 * @return a new structured generic item
	 */
	T construct (double... components);

	/**
	 * evaluate one component of the value
	 * @param value the value being evaluated
	 * @param componentNumber the ID of component (ordinal index)
	 * @return an evaluation of the component
	 */
	double component (T value, int componentNumber);

}
