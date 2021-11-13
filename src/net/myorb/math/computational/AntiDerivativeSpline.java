
package net.myorb.math.computational;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * generic Anti Derivative Spline implementation
 * @author Michael Druckman
 */
public class AntiDerivativeSpline<T> implements Function<T>
{

	/**
	 * segments across the domain of the function 
	 *  describe the area of each of those segments
	 * @param <T> data type used in the segments
	 */
	public interface SegmentManager<T>
	{
		/**
		 * @return the number of segments
		 */
		int getSegmentCount ();

		/**
		 * @return the LO end of the domain of the first segment
		 */
		T getFirstSegmentBase ();

		/**
		 * @param forSegment the index of the segment
		 * @return the HI domain value for the segment
		 */
		T getSegmentHi (int forSegment);

		/**
		 * @param forSegment the index of the segment
		 * @return the area value for the segment
		 */
		T getSegmentArea (int forSegment);

		/**
		 * compute area of a specified interval
		 * @param lo the LO domain for the interval
		 * @param hi the HI domain for the interval
		 * @return the area for the [LO,HI] domain interval
		 */
		T getAreaBetween (T lo, T hi);
	}

	/**
	 * @param segmentManager the description of the segments of the function
	 * @param typeManager the manager for the data type used in the function
	 */
	public AntiDerivativeSpline
	(SegmentManager<T> segmentManager, SpaceManager<T> typeManager)
	{
		this.segmentManager = segmentManager;
		this.typeManager = typeManager;
	}
	protected SegmentManager<T> segmentManager;
	protected SpaceManager<T> typeManager;

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		T ZERO = typeManager.getZero ();
		T coveredThru = segmentManager.getFirstSegmentBase ();
		T negX = typeManager.negate (x);
		T areaToPoint = ZERO;
		T next, nextMinusX;

		for (int i = 0; i < segmentManager.getSegmentCount (); i++)
		{
			next = segmentManager.getSegmentHi (i);
			nextMinusX = typeManager.add (next, negX);

			if (typeManager.lessThan (x, next))
			{
				T beyondCovered =
					typeManager.add (x, typeManager.negate (coveredThru));
				if (typeManager.lessThan (nextMinusX, beyondCovered))
				{
					T segmtArea = segmentManager.getSegmentArea (i);
					areaToPoint = typeManager.add (areaToPoint, segmtArea);
					coveredThru = next;
				}
				break;
			}

			areaToPoint = typeManager.add (areaToPoint, segmentManager.getSegmentArea (i));
			if (typeManager.isZero (nextMinusX)) return areaToPoint;
			coveredThru = next;
		}

		return typeManager.add (areaToPoint, computedAdjustment (coveredThru, x));
	}

	/**
	 * compute excess or additional area around end-point
	 * @param coveredThru the domain end-point covered so far
	 * @param x the domain parameter intended as end-point
	 * @return adjustment to computed area
	 */
	T computedAdjustment (T coveredThru, T x)
	{
		if (typeManager.lessThan (x, coveredThru))
		{ return typeManager.negate (segmentManager.getAreaBetween (x, coveredThru)); }
		else return segmentManager.getAreaBetween (coveredThru, x);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<T> getSpaceDescription () { return typeManager; }
	public SpaceManager<T> getSpaceManager () { return typeManager; }

}
