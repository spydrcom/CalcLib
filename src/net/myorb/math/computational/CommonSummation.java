
package net.myorb.math.computational;

import net.myorb.math.SpaceManager;

/**
 * summation of factored terms with possible alternating sign
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public abstract class CommonSummation <T>
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
		T sum = manager.getZero (), term = null;
		for (int l = 1; l <= n; l++)
		{
			term = manager.multiply
				(
					factor1 (n, l), factor2 (n, l)
				);
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
