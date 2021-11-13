
package net.myorb.math.computational;

import net.myorb.math.Polynomial;
import net.myorb.math.SpaceManager;
import net.myorb.data.abstractions.Function;

/**
 * implementation of change of interval to [-1, 1]
 * @param <T> the data type used in operations
 * @author Michael Druckman
 */
public class LinearCoordinateChange<T> extends TransformExtensions<T>
{


	// INTEGRAL ||(a,b) f(x) dx = (b - a)/2 INTEGRAL ||(-1,1) f( (a+b)/2 + x * (b-a)/2 ) dx
	// as described in:   https://en.wikipedia.org/wiki/Gaussian_quadrature


	/**
	 * function call interface standardized to [-1,1]
	 */
	public interface StdFunction<T>
		extends Function<T>
	{

		/*
		 * standardized function uses interval [-1,1]
		 * non-standard [lo,hi] functions use interval change f (intercept + slope * x)
		 */

		/**
		 * @return the slope of the interval change
		 */
		T getSlope ();
		T getIntercept ();
		Polynomial.PowerFunction<T> describeLine ();

	}


	/**
	 * define the line of the interval
	 * @param lo the lo bound of the original interval
	 * @param hi the hi bound of the original interval
	 * @param f the function that will be evaluated
	 * @param manager data type manager
	 */
	public LinearCoordinateChange
		(
			T lo, T hi, Function<T> f, SpaceManager<T> manager
		)
	{
		super (f);
		T HALF = manager.invert (manager.newScalar (2));
		this.slope = manager.multiply (manager.add (hi, manager.negate (lo)), HALF);
	    this.intercept = manager.multiply (manager.add (hi, lo), HALF);
	    this.manager = manager;
	}
	protected SpaceManager<T> manager;
	protected T slope, intercept;


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		if (manager.isNegative (intercept))
		{
			return slope.toString () + " * x - " + manager.negate (intercept).toString ();
		}
		else
		{
			return slope.toString () + " * x + " + intercept.toString ();
		}
	}


	/**
	 * @return slope of transform
	 */
	public T getSlope ()
	{
		return slope;
	}


	/**
	 * @return intercept of transform
	 */
	public T getIntercept ()
	{
		return intercept;
	}


	/**
	 * @return description of calculated line
	 */
	public Polynomial.PowerFunction<T> describeLine ()
	{
		return new Polynomial<T> (manager).linearFunctionOfX (slope, intercept);
	}


	/**
	 * evaluate the function
	 * @param x the value on the new [-1,1] interval
	 * @return the function value at x
	 */
	public T eval (T x)
	{
		return algorithm.eval (manager.add (intercept, manager.multiply (slope, x)));
	}


	/**
	 * @return function object with Adjusted Domain
	 */
	public StdFunction<T> functionWithAdjustedDomain ()
	{
		return new FunctionWithAdjustedDomain<T> (this);
	}


}


/**
 * wrapper for adjusted function
 */
class FunctionWithAdjustedDomain<T> implements LinearCoordinateChange.StdFunction<T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#eval(java.lang.Object)
	 */
	public T eval (T x) { return linearCoordinateChange.eval (x); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.LinearCoordinateChange.RealFunctionObject#getSlope()
	 */
	public T getSlope () { return linearCoordinateChange.getSlope (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.LinearCoordinateChange.StdFunction#getIntercept()
	 */
	public T getIntercept () { return linearCoordinateChange.getIntercept (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.LinearCoordinateChange.StdFunction#describeLine()
	 */
	public Polynomial.PowerFunction<T> describeLine () { return linearCoordinateChange.describeLine (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<T> getSpaceDescription () { return linearCoordinateChange.manager; }
	public SpaceManager<T> getSpaceManager () { return linearCoordinateChange.manager; }

	FunctionWithAdjustedDomain
	(LinearCoordinateChange<T> linearCoordinateChange)
	{ this.linearCoordinateChange = linearCoordinateChange; }
	LinearCoordinateChange<T> linearCoordinateChange;

}

