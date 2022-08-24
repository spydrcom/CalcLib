
package net.myorb.math.expressions;

import net.myorb.charting.DisplayGraphTypes;

/**
 * provide for working with values that break into components
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface ExpressionComponentSpaceManager<T>
	extends ExpressionSpaceManager<T>
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

	/**
	 * distance of a value from the origin
	 * @param value the value that holds components
	 * @return distance from the origin
	 */
	double magnitude (T value);

	/**
	 * return label names for axis display in plots
	 * @return traditional axis labels for this type
	 */
	String[] axisLabels ();

	/**
	 * give a set of traditional colors for plots of this type
	 * @param colors display color choices for this type
	 */
	void assignColors (DisplayGraphTypes.Colors colors);

}
