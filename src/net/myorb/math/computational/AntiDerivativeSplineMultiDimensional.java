
package net.myorb.math.computational;

import net.myorb.data.abstractions.SimpleUtilities;

import net.myorb.math.MultiDimensional;
import net.myorb.math.SpaceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * generic Anti Derivative Spline implementation (Multi-Dimensional version)
 * @param <T> data type used in spline
 * @author Michael Druckman
 */
public class AntiDerivativeSplineMultiDimensional<T> implements MultiDimensional.Function<T>
{


	/**
	 * segments across the domain of the function 
	 *  describe the contribution of each of those segments
	 * @param <T> data type used in the segments
	 */
	public interface SegmentManager<T>
	{
		/**
		 * @return number of dimensions
		 */
		int getDimensionCount ();

		/**
		 * @param forDimension number of dimension
		 * @return the number of segments by dimension
		 */
		int getSegmentCount (int forDimension);

		/**
		 * @param forDimension number of dimension
		 * @return the LO end of the domain of the first segment
		 */
		T getFirstSegmentBase (int forDimension);

		/**
		 * @param forDimension number of dimension
		 * @param forSegment the index of the segment
		 * @return the HI domain value for the segment
		 */
		T getHi (int forDimension, int forSegment);

		/**
		 * get contribution from spline
		 * @param segmentAt the matrix coordinates of the segment
		 * @return the area value for the segment
		 */
		T getContributionFrom (List<Integer> segmentAt);

		/**
		 * compute contribution outside segments
		 * @param dimensionLo the lo end of interval for dimension
		 * @param dimensionHi the hi end of interval for dimension
		 * @param includedSegments the highest segment number for each dimension
		 * @return the contribution for the aggregate [LO,HI] domain intervals
		 */
		T getContributionFrom
		(
			List<T> dimensionLo, List<T> dimensionHi,
			List<Integer> includedSegments
		);
	}


	/**
	 * @param segmentManager the description of the segments of the function
	 * @param typeManager the manager for the data type used in the function
	 */
	public AntiDerivativeSplineMultiDimensional
	(SegmentManager<T> segmentManager, SpaceManager<T> typeManager)
	{
		this.genericManager =
			new MultiDimensionalSplineSegmentAccumulationManager<T>
				(segmentManager, typeManager);
		this.segmentManager = segmentManager;
		this.ZERO = typeManager.getZero ();
		this.typeManager = typeManager;
	}
	protected SegmentManager<T> segmentManager;
	protected SpaceManager<T> typeManager;
	protected T ZERO;


	/**
	 * call the function given a list of parameters per variable
	 * @param dataPoint the data point as a list of a parameter per variable
	 * @return the value of the function at the point
	 */
	public T f (List<T> dataPoint)
	{
		checkForLoSegments (dataPoint);
		List<T> additional = new ArrayList<T>();
		List<Integer> includedSegments = new ArrayList<Integer>();
		selectIncludedSegments (dataPoint, includedSegments, additional);
		return calculate (includedSegments, additional);
	}
	@SuppressWarnings("unchecked") public T f (T... x) { return f (SimpleUtilities.toList (x)); }


	/**
	 * @param dataPoint the data point passed to the function as parameter
	 * @throws RuntimeException for data points lower than lowest segment describes
	 */
	public void checkForLoSegments (List<T> dataPoint) throws RuntimeException
	{
		for (int d = 0; d < segmentManager.getDimensionCount (); d++)
		{
			T
				parameter = dataPoint.get (d),
				dimensionLo = segmentManager.getFirstSegmentBase (d);
			if (typeManager.lessThan (parameter, dimensionLo))
			{
				throw new RuntimeException ("Lowest segment for dimension " + d + " does not cover parameter");
			}
		}
	}


	/**
	 * @param dataPoint the data point passed to the function as parameter
	 * @param includedSegments the highest segment number for each dimension
	 * @param additional the interval for each dimension beyond the segment description
	 */
	public void selectIncludedSegments
		(
			List<T> dataPoint,
			List<Integer> includedSegments,
			List<T> additional
		)
	{
		for (int d = 0; d < segmentManager.getDimensionCount (); d++)
		{
			T hi = ZERO, parameter = dataPoint.get (d);
			int s = segmentManager.getSegmentCount (d) - 1;
			
			while (s >= 0)
			{
				hi = segmentManager.getHi (d, s);
				if (!typeManager.lessThan (parameter, hi)) break;
				s--;
			}

			if (s >= 0)
				parameter = typeManager.add
					(parameter, typeManager.negate (hi));
			//System.out.println (parameter);
			additional.add (parameter);
			includedSegments.add (s);
		}
	}


	/**
	 * @param includedSegments the highest segment number for each dimension
	 * @param additional the interval for each dimension beyond the segment description
	 * @return the accumulation of all included segments and additional contributions
	 */
	public T calculate
		(
			List<Integer> includedSegments,
			List<T> additional
		)
	{
		T accumulation =
			genericManager.accumulateSegmentComponents (includedSegments);
		List<T> extraLo = new ArrayList<T>(), extraHi = new ArrayList<T>();

		for (int d = 0; d < segmentManager.getDimensionCount (); d++)
		{
			T extra = additional.get (d);

			int highestSegment = includedSegments.get (d);
			T hi = segmentManager.getHi (d, highestSegment);

			extraLo.add (hi); extraHi.add (typeManager.add (hi, extra));
		}

		T extras =
			segmentManager.getContributionFrom
					(extraLo, extraHi, includedSegments);
		return typeManager.add (accumulation, extras);
	}
	protected MultiDimensionalSplineSegmentAccumulationManager<T> genericManager;


	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<T> getSpaceDescription () { return typeManager; }


}
