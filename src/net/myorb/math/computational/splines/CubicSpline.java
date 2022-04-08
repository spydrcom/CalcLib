
package net.myorb.math.computational.splines;

import net.myorb.math.computational.DerivativeApproximation;
import net.myorb.math.computational.splines.CubicSpline.Knot;
import net.myorb.math.SpaceManager;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.data.abstractions.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * General Expression For an Interpolating Cubic Spline
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class CubicSpline <T>
{


	/**
	 * access to values that define the spline
	 * @param <T> type of component values on which operations are to be executed
	 */
	public interface Knot <T>
	{

		/**
		 * @return value of X at the knot
		 */
		public T t ();												// t#i
		
		/**
		 * @return value of run since previous knot
		 */
		public T h ();												// t#(i) - t#(i-1)

		/**
		 * @return value of function at the knot
		 */
		public T f ();												// f(t#i)

		/**
		 * @return second derivative of function at the knot
		 */
		public T z ();												// f''(t#i)

		/**
		 * @param x parameter to function
		 * @return value of spline at x
		 */
		public T S (T x);											// S#i(x)

		/**
		 * @param x parameter to function
		 * @return TRUE parameter falls in range of segment
		 * @throws RuntimeException when parameter is below limit
		 */
		public boolean isInRange (T x) throws RuntimeException;		// t#(i-1) <= x <= t#i

		/**
		 * @param computer the function that can compute spline values
		 */
		public void setComputer (Function <T> computer);
		public Function <T> getComputer ();

		/**
		 * @return access to prior knot
		 */
		public Knot <T> prior ();

	}


	/**
	 * the collection of knots that cover the domain of the spline
	 * @param <T> type of component values on which operations are to be executed
	 */
	public static class KnotList <T> extends ArrayList < Knot <T> >
	{ private static final long serialVersionUID = -5142967187147035547L; }


	/**
	 * object interface for results of cubic spline Interpolation processes
	 * @param <T> type of component values on which operations are to be executed
	 */
	public interface Interpolation <T> extends Function <T>
	{
		/**
		 * @return the description of underlying segment of the Interpolation
		 */
		KnotList <T> getKnots ();
	}


	public CubicSpline
	(SpaceManager <T> sm) { this.sm = sm; }
	protected SpaceManager <T> sm;


	/**
	 * @param f the function to be fit to
	 * @param knotPoints the X axis points to use as knots
	 * @param delta the value for delta in derivative approximations
	 * @return the Cubic Spline Interpolation of the function
	 */
	public Interpolation <T> interpolationFor (Function <T> f, List <T> knotPoints, T delta)
	{
		Spline <T> spline = new Spline <T> (sm);
		spline.interpolate (f, knotPoints, delta);
		return spline;
	}


}


/**
 * description of the function section between 2 knots
 * @param <T> type of component values on which operations are to be executed
 */
class CubicSplineSegment <T>
	extends CubicSplineComputer <T>
	implements CubicSpline.Knot <T>
{


	CubicSplineSegment
		(
			T t,
			Function <T> f,
			Function <T> f2,
			CubicSplineSegment <T> prior,
			SpaceManager <T> sm
		)
	{
		this (t, f, f2, sm);
		if (prior == null) return;

		this.h = sm.add (t, sm.negate (prior.t ()));
		this.computeConstants (sm); this.prior = prior;
		this.setComputer (this);
	}

	CubicSplineSegment
		(
			T t,
			Function <T> f,
			Function <T> f2,
			SpaceManager <T> sm
		)
	{
		super (sm);
		this.t = t;
		this.f = f.eval (t);
		this.z = f2.eval (t);
	}


}


/**
 * description of the knots describing a function interpolation
 * @param <T> type of component values on which operations are to be executed
 */
class Spline <T> extends DerivativeApproximation <T>
	implements CubicSpline.Interpolation <T>
{


	public Spline (SpaceManager <T> sm)
	{
		super (sm);
	}


	/**
	 * @param f the function to be fit to
	 * @param knotPoints the X axis points to use as knots
	 * @param delta the value for delta in derivative approximations
	 */
	public void interpolate (Function <T> f, List <T> knotPoints, T delta)
	{

		CubicSplineSegment <T> prior = null;
		Function <T> f2 = secondDerivative (f, delta);
		this.knots = new CubicSpline.KnotList <> ();

		for (T t : knotPoints)
		{
			CubicSplineSegment <T> s =
				new CubicSplineSegment <T>
					(
						t, f, f2, prior, sm
					);
			this.knots.add (s);
			prior = s;
		}

	}
	Function <T> secondDerivative (Function <T> f, T delta)
	{
		return getDerivativeApproximationFunctions (f, delta).forOrder (2);
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x) { return locateSegmentFor (x).S (x); }


	/**
	 * @param x parameter to function
	 * @return the segment the parameter is found to fall in
	 * @throws RuntimeException when parameter found to be greater than high limit
	 */
	public Knot <T> locateSegmentFor (T x) throws RuntimeException
	{
		for (Knot <T> k : knots) { if (k.isInRange (x)) return k; }
		throw new RuntimeException ("Parameter falls above spline high limit");
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.CubicSpline.Interpolation#getKnots()
	 */
	public CubicSpline.KnotList <T> getKnots () { return knots; }
	protected CubicSpline.KnotList <T> knots;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription <T> getSpaceDescription () { return sm; }

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager <T> getSpaceManager () { return sm; }


}


/**
 * cubic spline equation evaluated with the generic space manager object
 * @param <T> type of component values on which operations are to be executed
 */
class CubicSplineComputer <T> extends CubicSplineProperties <T> implements Function <T>
{

	CubicSplineComputer (SpaceManager <T> sm)
	{
		super (sm);
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		T toKnot = sm.add (t, sm.negate (x));
		T fromPrior = sm.add (x, sm.negate (prior.t));

		T fromPrior3 = sm.multiply (fromPrior, fromPrior);
		fromPrior3 = sm.multiply (fromPrior3, fromPrior);
		T toKnot3 = sm.multiply (toKnot, toKnot);
		toKnot3 = sm.multiply (toKnot3, toKnot);

		T term1 = sm.multiply (sm.multiply (z, fromPrior3), I6h);
		T term2 = sm.multiply (sm.multiply (prior.z, toKnot3), I6h);
		T terms = sm.add (term1, term2);

		T fOverH = sm.multiply (f, Ih);
		T zhOver6 = sm.multiply (sm.multiply (z, h), SIXTH);
		T dif = sm.add (fOverH, sm.negate (zhOver6));
		T term3 = sm.multiply (fromPrior, dif);
		terms = sm.add (terms, term3);

		fOverH = sm.multiply (prior.f, Ih);
		zhOver6 = sm.multiply (sm.multiply (prior.z, h), SIXTH);
		dif = sm.add (fOverH, sm.negate (zhOver6));
		T term4 = sm.multiply (toKnot, dif);
		terms = sm.add (terms, term4);

		return terms;
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription <T> getSpaceDescription () { return sm; }
	protected SpaceManager <T> sm;


	/**
	 * the calculation of 1/(6h) which is a factor of each term
	 * @param sm manager for the data type
	 */
	public void computeConstants (SpaceManager <T> sm)
	{
		if (h == null) return;
		this.Ih = sm.invert (h);
		this.SIX = sm.newScalar (6);
		this.SIXTH = sm.invert (SIX);
		this.I6h = sm.multiply (SIXTH, Ih);
		this.sm = sm;
	}
	protected T SIX, SIXTH;
	protected T Ih, I6h;


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return "t=" + t + " z=" + z + " f=" + f;
	}


}


/**
 * storage for data that describes spline segment
 * @param <T> type of component values on which operations are to be executed
 */
class CubicSplineProperties <T> implements CubicSpline.Knot <T>
{


	public CubicSplineProperties (SpaceManager <T> sm)
	{
		this.t = sm.getZero ();
		this.h = sm.getZero ();
		this.f = sm.getZero ();
		this.z = sm.getZero ();
		this.sm = sm;
	}
	protected CubicSplineSegment <T> prior = null;
	protected SpaceManager <T> sm;
	protected T t, h, f, z;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.CubicSpline.Knot#prior()
	 */
	public Knot <T> prior () { return prior; }


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.CubicSpline.Knot#t()
	 */
	public T t () { return t; }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.CubicSpline.Knot#h()
	 */
	public T h () { return h; }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.CubicSpline.Knot#f()
	 */
	public T f () { return f; }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.CubicSpline.Knot#z()
	 */
	public T z () { return z; }


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.CubicSpline.Knot#S(java.lang.Object)
	 */
	public T S (T x) { return s.eval (x); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.CubicSpline.Knot#setComputer(net.myorb.data.abstractions.Function)
	 */
	public void setComputer
	(Function <T> computer) { this.s = computer; }
	public Function <T> getComputer () { return s; }
	protected Function <T> s;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.CubicSpline.Knot#isInRange(java.lang.Object)
	 */
	public boolean isInRange (T x) throws RuntimeException
	{
		if ( prior == null  ||  sm.lessThan (t, x) ) return false;
		return ! sm.lessThan (x, prior.t);
	}


}

