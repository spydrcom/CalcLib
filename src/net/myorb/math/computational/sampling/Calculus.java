
package net.myorb.math.computational.sampling;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.Function;

/**
 * implementation of algorithms approximating function derivatives
 * @param <T> data type used in Arithmetic operations
 * @author Michael Druckman
 */
public class Calculus <T> extends SegmentAnalysis <T>
{


	public Calculus
	(SampleFormatter <T> formatter, ExpressionSpaceManager <T> manager)
	{ super (manager); this.formatter = formatter; }
	SampleFormatter <T> formatter;


	/**
	 * captures of the evaluation of function derivatives
	 */
	public class Derivatives extends ItemList <CartesianSampleSet>
	{
		/**
		 * format the values of the list of sample sets
		 */
		public void display ()
		{
			for (CartesianSampleSet S : this)
			{ System.out.println (S); }
		}
		private static final long serialVersionUID = -5097814593602609949L;
	}


	/**
	 * collect derivatives of a function
	 * @param f the function being evaluated
	 * @param a the point a where the Taylor series focus is
	 * @param order the ultimate order of the polynomial being built
	 * @param dx the run value to use for the derivative approximations
	 * @param proximity the linear distance on each side of point a
	 * @param derivatives the list collecting derivative samples
	 */
	public void evaluate
		(
			Function <T> f, T a,
			int order, T dx, T proximity,
			Derivatives derivatives
		)
	{
		derivatives.add ( sample (f, a, dx, proximity, formatter) );
		for (int n = 1; n <= order; n++) { next (derivatives); }
	}


	/**
	 * use last in list to compute next for list
	 * @param derivatives the list collecting derivative samples
	 */
	public void next (Derivatives derivatives)
	{
		int previous = derivatives.size () - 1;
		CartesianSampleSet prior = derivatives.get (previous);
		next (prior, derivatives);
	}


	/**
	 * capture next derivative iteration
	 * @param prior the previous derivative samples
	 * @param to the list collecting derivative samples
	 */
	public void next (CartesianSampleSet prior, Derivatives to)
	{
		CartesianSampleSet prime = new CartesianSampleSet (formatter);
		T HALF = manager.invert (manager.newScalar (2));
		T x = prior.X.get (0), y = prior.Y.get (0);
		int next = 1, last = prior.X.size () - 1;

		while ( next <= last )
		{
			T lastX = x, lastY = y;

			x = prior.X.get (next); y = prior.Y.get (next);
			T rise = diff (y, lastY), run = diff (x, lastX);

			T evaluatedAt = manager.add
				(
					lastX, manager.multiply (run, HALF)
				);
			T derivativeApproximation = manager.multiply
				(
					rise, manager.invert (run)
				);
			prime.add
			(
				evaluatedAt,
				derivativeApproximation
			);

			next++;
		}

		to.add (prime);
	}


}

