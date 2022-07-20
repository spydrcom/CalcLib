
package net.myorb.math.expressions.charting.fractals;

import java.util.ArrayList;

/**
 * detector for cycles appearing in iterations
 * @author Michael Druckman
 */
public class Cycles
{


	static final int MAX_ITERATION = 10;


	public Cycles (double edge)
	{ scaleFactor = 1000 / edge; }
	protected double scaleFactor;


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

		EvalPoint (double x, double y) { this.x = x; this.y = y; }
		double x, y;

	}


	/**
	 * a list of points having already been seen over iterations
	 */
	public static class Cache extends ArrayList<EvalPoint>
	{
		
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
		{
			cache = new Cache ();
			return false;
		}
		else if (iteration > MAX_ITERATION) return false;

		EvalPoint p = new  EvalPoint (x, y);
		boolean check = cache.hasSeen (p);

		if (!check)
		{
			cache.add (p);
		}
//		else
//		{
//			System.out.println ("cycle found @" + p + " >  " + cache);
//		}

		return check;
	}
	protected Cache cache;


}

