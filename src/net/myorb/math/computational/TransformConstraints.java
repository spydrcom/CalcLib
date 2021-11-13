
package net.myorb.math.computational;

import net.myorb.math.expressions.evaluationstates.Arrays;
import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.math.*;

/**
 * use analysis of data sequence to determine function domain constraints
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class TransformConstraints<T> implements Arrays.ConstrainedDomain<T>
{

	public TransformConstraints
	(T lo, T hi, T delta, Polynomial<T> polynomial, SpaceManager<T> manager)
	{
		this.hi = hi; this.lo = lo; this.delta = delta;
		this.polynomial = polynomial;
		this.manager = manager;
	}
	public TransformConstraints
	(DataSequence2D<T> data, Polynomial<T> polynomial, SpaceManager<T> manager)
	{
		this.polynomial = polynomial;
		this.manager = manager;
		analyze (data);
	}
	Polynomial<T> polynomial;
	SpaceManager<T> manager;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.ConstrainedDomain#checkConstraintsAgainst(java.lang.Object)
	 */
	public void checkConstraintsAgainst(T x)
	{
		if (manager.lessThan (x, lo) || manager.lessThan (hi, x))
		{
			throw new RuntimeException ("Value not within domain constraints");
		}
	}
	
	/**
	 * the data sequence used in regression to construct this transform.
	 *  the lo and hi values of the constratint will be set to the lo/hi of the data set
	 * @param data the data sequence that generated this transform
	 */
	public void analyze (DataSequence2D<T> data)
	{
		this.data = data;
		Statistics<T> stats = new Statistics<T> (manager);
		hi = stats.max (data.xAxis); lo = stats.min (data.xAxis);
		T perUnit = manager.invert (manager.newScalar (data.xAxis.size () - 1));
		delta = manager.multiply (manager.add (hi, manager.negate (lo)), perUnit);
	}
	DataSequence2D<T> data;
	T hi, lo, delta;

	/**
	 * connected polynomial class establishes type of polynomial.
	 *  most important is recognition of Chebyshev polynomials which
	 *  require special evaluation method to compute transform values
	 * @return the polynomial object used to evaluate transform
	 */
	public Polynomial<T> getPolynomial()
	{
		return polynomial;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.ConstrainedDomain#getDelta()
	 */
	public T getDelta()
	{
		return delta;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.ConstrainedDomain#getHi()
	 */
	public T getHi()
	{
		return hi;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.ConstrainedDomain#getLo()
	 */
	public T getLo()
	{
		return lo;
	}

}

