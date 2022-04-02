
package net.myorb.math.computational.splines;

/**
 * access properties and functionalities of a spline segment
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface SegmentAbilities <T> 
	extends SegmentRepresentation
{

	/**
	 * get a function that maps the segment
	 * @return a function that maps the represented segment
	 */
	SegmentFunction <T> getSegmentFunction ();
	
	/**
	 * identify segment if maps to value
	 * @param value the point to locate a segment for
	 * @param margins ID for margin amount to accommodate rounding error
	 * @return the function for the segment, or NULL if not a match
	 */
	SegmentFunction <T> checkFor (double value, int margins);

}

