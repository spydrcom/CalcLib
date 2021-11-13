
package net.myorb.math.computational;

import net.myorb.math.SpaceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * a generic mechanism for computing the
 *  accumulation of segments of multi-dimension objects
 * @author Michael Druckman
 */
public class MultiDimensionalAccumulation<T,C>
{


	/**
	 * break space across dimension into portions
	 * @param <T> the data type manager for contributions
	 * @param <C> the description of a portion
	 */
	public interface ContributionManager<T,C>
	{

		/**
		 * @param portions a description of a portion of the space
		 * @param dimension the dimension to be checked for completion
		 * @return TRUE : dimension is complete
		 */
		boolean isPortionComplete (List<C> portions, int dimension);

		/**
		 * @param dimension the dimension to be updated to next portion
		 * @param portions the description of the current portion
		 */
		void nextContributionFor (int dimension, List<C> portions);

		/**
		 * @param portions the description of the portions to evaluate
		 * @return the contribution for specified portions
		 */
		T computeContributionFrom (List<C> portions);
	}


	/**
	 * @param cmgr a ContributionManager for the accumulation
	 * @param tmgr a space manager for the type of the accumulation
	 */
	public MultiDimensionalAccumulation
	(ContributionManager<T,C> cmgr, SpaceManager<T> tmgr)
	{
		this.cmgr = cmgr; this.tmgr = tmgr;
	}
	protected ContributionManager<T,C> cmgr;
	protected SpaceManager<T> tmgr;


	/**
	 * @param contributors a list of keys that identify portions
	 * @return the accumulation of all included contributors
	 */
	public T accumulateFrom (List<C> contributors)
	{
		return new AccumulationEngine<T,C> (cmgr, tmgr)
		.initialize (contributors)
		.accumulatePortions ();
	}


}


/**
 * maintain properties in place of passing parameters.
 *  overhead is reduced for partial reduction of execution time.
 * @param <T> the managed data accumulation type
 * @param <C> the contribution ID type
 */
class AccumulationEngine<T,C>
{


	/**
	 * accumulations of contributions of this portion
	 */
	public void accumulateContributionsOfPortion ()
	{
		while (!cmgr.isPortionComplete (portion, highest))			// entire block of last dimension done in each outer iteration
		{
			T contrib = cmgr.computeContributionFrom (portion);		// computeContributionFrom identified component
			accumulation = tmgr.add (accumulation, contrib);		// add contribution into accumulation
			cmgr.nextContributionFor (highest, portion);			// next portion
		}
	}
	protected T accumulation;


	/**
	 * @return TRUE : more portions to come
	 */
	public boolean haveMorePortions ()
	{
		for
			(
				int dimension = highest;							// start in last dimension and work down
				cmgr.isPortionComplete (portion, dimension);		// check for additional portions in current dim
				cmgr.nextContributionFor (dimension, portion)
			)
		{
			portion.set (dimension, from.get (dimension));			// reset to original contributor for dimension
			if (--dimension < 0) return false;						// done with last contributor, no more portions
		}
		return true;												// a new portion was found
	}
	protected int highest;


	/**
	 * @return accumulations with this portion
	 */
	public T accumulatePortions ()
	{
		do { accumulateContributionsOfPortion (); }
		while (haveMorePortions ());
		return accumulation;
	}


	/**
	 * @param contributors the identification of the starting point
	 */
	public AccumulationEngine<T,C> initialize
		(
			List<C> contributors
		)
	{
		this.portion = new ArrayList<C> ();							// identification of current portion
		this.accumulation = tmgr.getZero ();						// generating a sum starting at zero
		this.highest = contributors.size () - 1;					// highest index is highest dimension
		this.portion.addAll (contributors);							// portion starts as the initial contributors
		this.from = contributors;									// remember starting points
		return this;
	}
	protected List<C> portion, from;


	/**
	 * @param cmgr a ContributionManager for the accumulation
	 * @param tmgr a space manager for the type of the accumulation
	 */
	public AccumulationEngine
		(
			MultiDimensionalAccumulation.ContributionManager<T,C> cmgr,
			SpaceManager<T> tmgr
		)
	{
		this.cmgr = cmgr; this.tmgr = tmgr;
	}
	protected MultiDimensionalAccumulation.ContributionManager<T,C> cmgr;
	protected SpaceManager<T> tmgr;


}

