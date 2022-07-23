
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


	public Cycles (double edge)
	{
		if (edge < 1E-4) scaleFactor = 1E8;
		else if (edge < 1E-6) scaleFactor = 1E10;
		else if (edge < 1E-8) scaleFactor = 1E12;
		//else scaleFactor = 1000 / edge;
		cache = new Cache ();
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
	public class EvalPoint
	{

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
		boolean EQ (double x, double y) { return scale(x) == scale(y); }

		/**
		 * @param x value on the x axis
	 	 * @param y value on the y axis
		 * @return this
		 */
		public EvalPoint set (double x, double y)
		{ this.x = x; this.y = y; return this; }

		public EvalPoint
		(double x, double y) { set (x, y); }
		double x, y;

	}


	/**
	 * a list of points having already been seen over iterations
	 */
	public class Cache extends ArrayList<EvalPoint>
	{

		/**
		 * recycle or create new
		 * @param x value on the x axis
	 	 * @param y value on the y axis
	 	 * @return a point set to x,y
		 */
		public EvalPoint allocate (double x, double y)
		{
			int available = size ();
			if (available == 0) return new EvalPoint (x, y);
			else return remove (available - 1).set (x, y);
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

		/**
		 * process first member of cycle
		 * @param x value on the x axis
	 	 * @param y value on the y axis
		 */
		public void start (double x, double y)
		{
			this.recycle ();
			this.add (recycled.allocate (x, y));
		}

		private static final long serialVersionUID = 8589277167486179337L;

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
		if (iteration == 0)
		{ cache.start (x, y); return false; }
		else if (iteration > MAX_ITERATION) return false;
		return cache.findOrPut (recycled.allocate (x, y));
	}


	/**
	 * prepare a cache as a buffer for recycled points
	 * @param toBuffer buffer holding recycled entries
	 * @return new empty Cache when buffer is null
	 */
	public Cache setBuffer (Cache toBuffer)
	{
		if ((this.recycled = toBuffer) == null)
		{ this.recycled = new Cache (); }
		return this.recycled;
	}
	protected Cache cache = null, recycled = null;


}

