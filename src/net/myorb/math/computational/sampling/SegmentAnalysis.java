
package net.myorb.math.computational.sampling;

import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.Function;

public class SegmentAnalysis <T>
{


	public class SampleSet <SampleType> extends java.util.ArrayList <SampleType>
	{ private static final long serialVersionUID = 4668063682466361795L; }


	public class CartesianSampleSet
	{

		SampleSet <T> X = new SampleSet <> (), Y = new SampleSet <> ();
		void add (T x, T y) { X.add (x); Y.add (y); }
		
		public T computeYfor (T x)
		{
			int n = approximateIndexFor (x);
			if (manager.lessThan (x, X.get (n)))
			{ while (manager.lessThan (x, X.get (++n))) ; n--; }
			else while (manager.lessThan (x, X.get (--n))) ;				
			return computeYfor (x, n);
		}

		public T computeYfor (T x, int n)
		{
			T hiX = X.get (n+1), loX = X.get (n), dx = diff (hiX, loX);
			T relativeX = diff (x, loX), ratio = portion (relativeX, dx);
			T hiY = Y.get (n+1), loY = Y.get (n), dy = diff (hiY, loY);
			return manager.add (loY, manager.multiply (ratio, dy));
		}

		public int approximateIndexFor (T x)
		{
			T first = X.get (0), second = X.get (1), dx = diff (second, first);
			T span = diff (x, first), ratio = portion (span, dx);
			return manager.convertToDouble (ratio).intValue ();
		}

	}


	public CartesianSampleSet sample
		(
			Function <T> f, T a, T dx, T proximity
		)
	{
		T max = manager.add ( a, proximity ), x = diff ( a, proximity );
		CartesianSampleSet functionSamples = new CartesianSampleSet ();
	
		while ( manager.lessThan ( x, max ) )
		{
			functionSamples.add ( x, f.eval (x) );
			x = manager.add ( x, dx );
		}
	
		return functionSamples;
	}


	public T diff (T p, T q)
	{
		return manager.add (p, manager.negate (q));
	}


	public T portion (T p, T q)
	{
		return manager.multiply (p, manager.invert (q));
	}


	public SegmentAnalysis
	(ExpressionSpaceManager <T> manager) { this.manager = manager; }
	protected ExpressionSpaceManager <T> manager;


}

