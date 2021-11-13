
package net.myorb.math.computational;

import net.myorb.math.computational.AntiDerivativeSplineMultiDimensional.SegmentManager;

import net.myorb.math.SpaceManager;

import java.util.List;

/**
 * generic segment accumulation implementation
 * @param <T> data type for accumulation
 * @author Michael Druckman
 */
public class MultiDimensionalSplineSegmentAccumulationManager<T>
	implements MultiDimensionalAccumulation.ContributionManager<T, Integer>
{

	/**
	 * @param segmentManager the segment management object for the spline
	 * @param tmgr the type manager for the accumulation data type
	 */
	public MultiDimensionalSplineSegmentAccumulationManager
		(SegmentManager<T> segmentManager, SpaceManager<T> tmgr)
	{
		this.accumulator =
			new MultiDimensionalAccumulation<T, Integer>(this, tmgr);
		this.segmentManager = segmentManager;
	}
	protected MultiDimensionalAccumulation<T, Integer> accumulator;
	protected SegmentManager<T> segmentManager;

	/**
	 * @param includedSegments the ID for the segment across all dimensions
	 * @return the computed accumulation
	 */
	public T accumulateSegmentComponents (List<Integer> includedSegments)
	{
		return accumulator.accumulateFrom (includedSegments);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalAccumulation.ContributionManager#isPortionComplete(java.util.List, int)
	 */
	public boolean isPortionComplete (List<Integer> portions, int dimension)
	{ return portions.get (dimension) < 0; }


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalAccumulation.ContributionManager#nextContributionFor(int, java.util.List)
	 */
	public void nextContributionFor (int dimension, List<Integer> portions)
	{ portions.set (dimension, portions.get (dimension) - 1); }


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalAccumulation.ContributionManager#computeContributionFrom(java.util.List)
	 */
	public T computeContributionFrom (List<Integer> portions)
	{
		return segmentManager.getContributionFrom (portions);
	}

}
