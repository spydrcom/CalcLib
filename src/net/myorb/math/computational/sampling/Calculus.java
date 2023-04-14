
package net.myorb.math.computational.sampling;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.Function;

public class Calculus <T> extends SegmentAnalysis <T>
{


	public class Derivatives extends SampleSet <CartesianSampleSet>
	{ private static final long serialVersionUID = -5097814593602609949L; }


	public void evaluate
		(
			Function <T> f, T a,
			int order, T dx, T proximity,
			Derivatives derivatives
		)
	{
		derivatives.add ( sample (f, a, dx, proximity) );
		for (int n = 1; n <= order; n++) { next (derivatives); }
	}


	public void next (Derivatives derivatives)
	{
		int previous = derivatives.size () - 1;
		CartesianSampleSet prior = derivatives.get (previous);
		next (prior, derivatives);
	}


	public void next (CartesianSampleSet prior, Derivatives to)
	{
		CartesianSampleSet prime = new CartesianSampleSet ();
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

		System.out.println (prime.Y);
		to.add (prime);
	}


	public Calculus
	(ExpressionSpaceManager <T> manager)
	{ super (manager); }


}

