
package net.myorb.math.expressions.charting.fractals;

import java.util.ArrayList;

/**
 * detector for cycles appearing in iterations
 * @author Michael Druckman
 */
public class Cycles
{


	static final int MAX_ITERATION = 20;
	static final int LOOP_GRADE = 100;


	void setEdgeSize (double edge)
	{
		if (edge < 1E-4) scaleFactor = 1E8;
		else if (edge < 1E-6) scaleFactor = 1E10;
		else if (edge < 1E-8) scaleFactor = 1E12;
	}
	protected double scaleFactor = 1E6;


	/**
	 * scale a real number to a digit count
	 * @param x the value being checked to point of scale
	 * @return the scaled value as long
	 */
	long scale (double x) { return (long) (scaleFactor * x); }


	/**
	 * a point to hold in cache for cycle detection
	 */
	public static class EvalPoint
	{

		Cycles c;

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString () { return " (" + x + ", " + y + ") "; }

		/**
		 * determine if X and Y values match to scaled number of digits
		 * @param other the other point in the check
		 * @return TRUE when equal
		 */
		boolean EQ (EvalPoint other) { return EQ (other.x, x) && EQ (other.y, y); }

		/**
		 * determine equality to number of digits
		 * @param x value on the x axis
	 	 * @param y value on the y axis
		 * @return TRUE when equal
		 */
		boolean EQ (double x, double y) { return c.scale(x) == c.scale(y); }

		/**
		 * @param x value on the x axis
	 	 * @param y value on the y axis
		 * @return this
		 */
		public EvalPoint set (double x, double y)
		{ this.x = x; this.y = y; return this; }
		double x, y;

		public EvalPoint () {}

	}


	/**
	 * a list of points having already been seen over iterations
	 */
	public static class Cache extends ArrayList<EvalPoint>
	{

		/**
		 * recycle or create new
		 * @param x value on the x axis
	 	 * @param y value on the y axis
		 * @param c the master cycle object
	 	 * @return a point set to x,y
		 */
		public EvalPoint allocate (double x, double y, Cycles c)
		{
			EvalPoint p;
			int available = size ();
			if (available == 0) p = new EvalPoint ();
			else p = remove (available - 1);
			p.set (x, y); p.c = c;
			return p;
		}

		/**
		 * @param p the point being evaluated
		 * @return TRUE implies found
		 */
		public boolean findOrPut (EvalPoint p)
		{
			boolean check;
			check = this.hasSeen (p); this.add (p);
			return check;
		}

		/**
		 * check cache for point indicating a cycle
		 * @param p the point being evaluated
		 * @return TRUE implies seen
		 */
		public boolean hasSeen (EvalPoint p)
		{
			for (EvalPoint c : this)
			{ if (c.EQ (p)) return true; }
			return false;
		}

		/**
		 * move contents to recycle buffer
		 */
		public void recycle ()
		{
			recycled.addAll (this);
			this.clear ();
		}

		private static final long serialVersionUID = 8589277167486179337L;

	}


	/**
	 * allocate a point tracker
	 * @param x value on the x axis
 	 * @param y value on the y axis
	 * @return the allocated point
	 */
	EvalPoint allocate (double x, double y)
	{
		return recycled.allocate (x, y, this);
	}


	/**
	 * determine if an iteration has returned in a cycle
	 * @param iteration current iteration count
	 * @param x value on the x axis
	 * @param y value on the y axis
	 * @return TRUE implies found
	 */
	public boolean loopCheck (int iteration, double x, double y)
	{
		if (iteration > MAX_ITERATION)
		{
			return false;
		}
		else if (iteration == 0)
		{
			cache.recycle ();				// reuse old objects
			cache.add (allocate (x, y));	// start a fresh cache
			return false;					// first one is false
		}
		else return cache.findOrPut (allocate (x, y));
	}


	/*
	 * cycle check tool keeps a cache object per plot
	 * - a static cache keeps recycled evaluation points for all plots
	 * - this should keep memory needs (size and processing) low
	 */

	Cycles () { cache = new Cache (); }
	static Cache recycled = new Cache ();
	protected Cache cache;


}

