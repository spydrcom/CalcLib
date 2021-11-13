
package net.myorb.math.computational;

import net.myorb.data.abstractions.Function;
import net.myorb.data.abstractions.SpaceDescription;

/**
 * extensions available to functional algorithms providing transforms
 * @param <T> the data type used in operations
 * @author Michael Druckman
 */
public class TransformExtensions<T> implements Function<T>
{

	/**
	 * sum of function eval
	 *  of positive and negative parameter values
	 * @param f the function for the evaluation
	 * @param x the parameter to the function
	 * @return sum of parameter evals
	 * @param <T> data type
	 */
	public static <T> T smp (Function<T> f, T x)
	{
		SpaceDescription<T> mgr = f.getSpaceDescription ();
		return mgr.add (f.eval (x), f.eval (mgr.negate (x)));
	}

	/**
	 * sum of function eval
	 *  of positive and negative parameter values i.e. f(x)+f(-x)
	 * see net.myorb.math.computational.TransformExtensions.smp
	 * @param x the parameter to the function
	 * @return sum of parameter evals
	 */
	public T evalSmp (T x) { return smp (this, x); }

	/* (non-Javadoc)
	 * @see net.myorb.math.ManagedSpace#getSpaceManager()
	 */
	public SpaceDescription<T> getSpaceDescription () { return algorithm.getSpaceDescription (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		return algorithm.eval (x);
	}

	/**
	 * @param algorithm the underlying algorithm
	 */
	public TransformExtensions (Function<T> algorithm)
	{
		this.algorithm = algorithm;
	}
	protected Function<T> algorithm;

	/**
	 * provide SMP as function
	 * @param algorithm the underlying algorithm
	 * @return SMP function based on algorithm
	 * @param <T> data type
	 */
	public static <T> Function<T> getSmpFunction (Function<T> algorithm)
	{
		return new SmpFunction<T> (algorithm);
	}

}

/**
 * wrapper for algorithm
 * @param <T> data type
 */
class SmpFunction<T> extends TransformExtensions<T>
{
	/* (non-Javadoc)
	 * @see net.myorb.math.computational.TransformExtensions#eval(java.lang.Object)
	 */
	public T eval (T x) { return smp (algorithm, x); }
	SmpFunction (Function<T> f) { super (f); }
}
