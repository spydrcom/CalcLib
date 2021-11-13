
package net.myorb.math.computational;

import java.util.List;

/**
 * implementation of ContributionManager
 *  for generic integral supporting N-Dimensional functions
 * @author Michael Druckman
 */
public class MultiDimensionalRealIntegralContributionManager
	implements MultiDimensionalAccumulation.ContributionManager<Double, Double>
{

	public MultiDimensionalRealIntegralContributionManager
	(MultiDimensionalRealIntegralSupport master, List<Double> hi)
	{ this.hi = hi;  this.master = master; }

	/**
	 * provide spacing value for dimension
	 * @param dimension the dimension space to evaluate
	 * @return the appropriate delta value
	 */
	public double getDeltaFor (int dimension) { return master.getDeltaFor (dimension); }
	protected MultiDimensionalRealIntegralSupport master;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalAccumulation.ContributionManager#getContributionFrom(java.util.List)
	 */
	public Double computeContributionFrom (List<Double> portions) { return master.evaluateIntegrandAt (portions); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalAccumulation.ContributionManager#nextContributionFor(int, java.util.List)
	 */
	public void nextContributionFor (int dimension, List<Double> portions)
	{ portions.set (dimension, portions.get (dimension) + getDeltaFor (dimension)); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalAccumulation.ContributionManager#isPortionComplete(java.util.List, int)
	 */
	public boolean isPortionComplete (List<Double> portions, int dimension)
	{ return portions.get (dimension) > hi.get (dimension); }
	protected List<Double> hi;

}

