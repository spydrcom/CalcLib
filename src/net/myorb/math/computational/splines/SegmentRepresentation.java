
package net.myorb.math.computational.splines;

import java.util.List;

/**
 * provide access to the descriptive elements of a spline segment
 * @author Michael Druckman
 */
public interface SegmentRepresentation
{

	/**
	 * @return number of components in the data format
	 */
	int getComponentCount ();

	/**
	 * @param component the index identifying the component of the data format
	 * @return a list of the coefficients for the spline of specified component
	 */
	List<Double> getCoefficientsFor (int component);

	/**
	 * @return the lo end of the range of the described segment
	 */
	double getSegmentLo ();

	/**
	 * @return the hi end of the range of the described segment
	 */
	double getSegmentHi ();

	/**
	 * @return the delta value between ticks of the sample model
	 */
	double getSegmentDelta ();

	/**
	 * @return the SSE computed for this segment when the spline was built
	 */
	double getSegmentError ();

	/**
	 * @return the slope computed for the translation of coordinates
	 */
	double getSegmentSlope ();

	/**
	 * @return the coordinate translation multiplier
	 */
	double getUnitSlope ();

}
