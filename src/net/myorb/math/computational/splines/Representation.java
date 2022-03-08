
package net.myorb.math.computational.splines;

import java.util.List;

/**
 * provide access to the descriptions of segments of a spline 
 * @author Michael Druckman
 */
public interface Representation
{

	/**
	 * @return a list of descriptions of the segments of a spline
	 */
	List<SegmentRepresentation> getSegmentList ();

}
