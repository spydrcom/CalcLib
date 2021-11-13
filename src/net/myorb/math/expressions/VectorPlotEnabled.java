
package net.myorb.math.expressions;

import net.myorb.charting.DisplayGraphTypes;
import net.myorb.math.expressions.evaluationstates.Environment;

import java.util.List;

/**
 * build series from execution of functions
 *  which have the ability to process an entire vector with single call
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface VectorPlotEnabled<T>
{
	/**
	 * construct graph point series
	 *  for values that have multiple components
	 * @param domainDescription the parameters of the real domain
	 * @param series the series of points per component to be constructed
	 * @param environment the control state of the simulation
	 */
	public void evaluateSeries
	(
		TypedRangeDescription.TypedRangeProperties<T> domainDescription,
		List<DisplayGraphTypes.Point.Series> series, 
		Environment<T> environment
	);
}
