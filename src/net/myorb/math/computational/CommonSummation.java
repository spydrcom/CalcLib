
package net.myorb.math.computational;

import net.myorb.math.computational.iterative.IterationFoundations;

import net.myorb.math.SpaceManager;

/**
 * summation of factored terms with possible alternating sign
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public abstract class CommonSummation <T> extends IterationFoundations <T>
{

	public CommonSummation (SpaceManager <T> manager)
	{
		this.manager = manager;
	}
	protected SpaceManager <T> manager;

	/**
	 * compute running sum of a pair of factors
	 * @param n the number of terms
	 * @return the computed sum
	 */
	public T computeSum (int n)
	{
		return computeSum (1, n);
	}

	/**
	 * compute running sum of a pair of factors
	 * - starting point is specified rather than assumed
	 * @param from the starting point to be used
	 * @param to the upper limit of terms
	 * @return the computed sum
	 */
	public T computeSum (int from, int to)
	{
		T sum = manager.getZero (), term = null;

		for (int l = from; l <= to; l++)
		{
			term = manager.multiply
				(
					factor1 (to, l), factor2 (to, l)
				);
			if (precisionCheck != null)
			{
				term = precisionCheck.adjust (term);
			}

			if (shortCircuit != null)
				if (shortCircuit.terminateSummation (term))
					return manager.add (sum, term);
			sum = manager.add (sum, term);
		}

		return sum;
	}

	public abstract T factor1 (int n, int k);
	public abstract T factor2 (int n, int k);

	/**
	 * support for terms with alternating signs
	 * @param term the value of the term to process
	 * @param whenOdd the index to select the sign from
	 * @return negative term value for odd index values
	 */
	public T alternating (T term, int whenOdd)
	{
		return whenOdd % 2 == 1 ? manager.negate (term) : term;
	}

}
