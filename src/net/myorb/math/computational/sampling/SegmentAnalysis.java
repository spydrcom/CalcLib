
package net.myorb.math.computational.sampling;

import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.data.abstractions.CommonDataStructures;

import net.myorb.math.Function;

/**
 * descriptions of the data collection as samples
 * @param <T> data type used in Arithmetic operations
 * @author Michael Druckman
 */
public class SegmentAnalysis <T> extends CommonDataStructures
{


	/**
	 * description of objects formatting value displays
	 * @param <SampleType> data type description
	 */
	public interface SampleFormatter <SampleType>
	{
		/**
		 * @param sample a data item from a sample set
		 * @return the value formatted as string
		 */
		String format (SampleType sample);
	}

	/**
	 * a list describing a sequenced set of samples
	 * @param <SampleType> data type description
	 */
	public class SampleSet <SampleType> extends ItemList <SampleType>
	{

		public SampleSet
		(SampleFormatter <SampleType> formatter) { this.formatter = formatter; }
		SampleFormatter <SampleType> formatter;

		/* (non-Javadoc)
		 * @see java.util.AbstractCollection#toString()
		 */
		public String toString ()
		{
			StringBuffer buffer = new StringBuffer ();
			for (int i = 0; i < this.size (); i+=20)
			{
				SampleType sample = get (i);
				buffer.append (formatter.format (sample));
			}
			return buffer.toString ();
		}

		private static final long serialVersionUID = 4668063682466361795L;
	}


	/**
	 * description of a functions as a sequenced set of X/Y coordinate points
	 */
	public class CartesianSampleSet
	{

		public CartesianSampleSet (SampleFormatter <T> formatter)
		{
			X = new SampleSet <> (formatter);
			Y = new SampleSet <> (formatter);
		}

		/**
		 * add one X/Y point to the set
		 * @param x the value of the X coordinate
		 * @param y the value of the Y coordinate
		 */
		void add (T x, T y)
		{ X.add (x); Y.add (y); }
		SampleSet <T> X, Y;

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString () { return "(X: " + X + ", Y: " + Y + ")"; }

		/**
		 * compute value of function at specified point
		 * @param x the X value coordinate of the point to identify
		 * @return the value computed for Y given the specified x
		 */
		public T computeYfor (T x)
		{
			int n = approximateIndexFor (x);
			if (manager.lessThan (x, X.get (n)))
			{ while (manager.lessThan (x, X.get (++n))) ; n--; }
			else while (manager.lessThan (x, X.get (--n))) ;				
			return computeYfor (x, n);
		}

		/**
		 * given proximity of parameter x to closest point in the set
		 * compute an approximation of y for the given x estimating the Y proximity
		 * @param x the value of the parameter x to use for this evaluation
		 * @param n the estimate for the point index within the sample set
		 * @return the evaluation of the Y for the given X
		 */
		public T computeYfor (T x, int n)
		{
			T hiX = X.get (n+1), loX = X.get (n), dx = diff (hiX, loX);
			T relativeX = diff (x, loX), ratio = portion (relativeX, dx);
			T hiY = Y.get (n+1), loY = Y.get (n), dy = diff (hiY, loY);
			return manager.add (loY, manager.multiply (ratio, dy));
		}

		/**
		 * estimate within the sequence the index closest to the specified X
		 * @param x the value of the parameter x to use for this evaluation
		 * @return the index within the sequence estimated for X
		 */
		public int approximateIndexFor (T x)
		{
			T first = X.get (0), second = X.get (1), dx = diff (second, first);
			T span = diff (x, first), ratio = portion (span, dx);
			return manager.convertToDouble (ratio).intValue ();
		}

	}


	/**
	 * compute a sample sequence for f
	 * @param f the function being evaluated
	 * @param a the point a where the Taylor series focus is
	 * @param dx the run value to use for the derivative approximations
	 * @param proximity the linear distance on each side of point a
	 * @param formatter the formatter to assign to the sequence
	 * @return a sample set for the function evaluation
	 */
	public CartesianSampleSet sample
		(
			Function <T> f, T a, T dx, T proximity, SampleFormatter <T> formatter
		)
	{
		T max = manager.add ( a, proximity ), x = diff ( a, proximity );
		CartesianSampleSet functionSamples = new CartesianSampleSet (formatter);
	
		while ( manager.lessThan ( x, max ) )
		{
			functionSamples.add ( x, f.eval (x) );
			x = manager.add ( x, dx );
		}
	
		return functionSamples;
	}


	/**
	 * implementation of subtraction for samples
	 * @param p the value to be subtracted from
	 * @param q the subtrahend
	 * @return the difference
	 */
	public T diff (T p, T q)
	{
		return manager.add (p, manager.negate (q));
	}


	/**
	 * implementation of division for samples
	 * @param p the numerator for the operation
	 * @param q the denominator for the operation
	 * @return the quotient
	 */
	public T portion (T p, T q)
	{
		return manager.multiply (p, manager.invert (q));
	}


	public SegmentAnalysis
	(ExpressionSpaceManager <T> manager) { this.manager = manager; }
	protected ExpressionSpaceManager <T> manager;


}

