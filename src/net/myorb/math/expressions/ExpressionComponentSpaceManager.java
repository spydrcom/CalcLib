
package net.myorb.math.expressions;

import net.myorb.math.expressions.charting.MultiDimensionalUtilities;

import net.myorb.charting.DisplayGraphTypes;

/**
 * treatments for working with values that break into components
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface ExpressionComponentSpaceManager<T>
	extends ExpressionSpaceManager <T>, ComponentAccess <T>,
		MultiDimensionalUtilities.ContextProperties
{


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
	String[] componentIdentifiers ();

	/**
	 * give a set of traditional colors for plots of this type
	 * @param colors display color choices for this type
	 */
	void assignColors (DisplayGraphTypes.Colors colors);

}
