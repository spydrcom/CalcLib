
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

	}


	/**
	 * object interface for results of cubic spline Interpolation processes
	 * @param <T> type of component values on which operations are to be executed
	 */
	public interface Interpolation <T> extends Function <T>
	{
		/**
		 * @return the description of underlying segment of the Interpolation
		 */
		List < Knot <T> > getKnots ();
	}


	public CubicSpline (SpaceManager <T> sm) { this.sm = sm; }
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
class CubicSplineSegment <T> implements CubicSpline.Knot <T>
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
		this (t, f, f2);
		if (prior == null) return;
		this.h = sm.add (t, sm.negate (prior.t ()));
		this.computeConstants (sm);
		this.prior = prior;
	}
	protected CubicSplineSegment <T> prior = null;

	CubicSplineSegment
		(
			T t,
			Function <T> f,
			Function <T> f2
		)
	{
		this.t = t;
		this.f = f.eval (t);
		this.z = f2.eval (t);
	}

	public void computeConstants (SpaceManager <T> sm)
	{
		this.Ih = sm.invert (h);
		this.SIX = sm.newScalar (6);
		this.SIXTH = sm.invert (SIX);
		this.I6h = sm.multiply (SIXTH, Ih);
		this.sm = sm;
	}
	protected SpaceManager <T> sm;
	protected T SIX, SIXTH;
	protected T Ih, I6h;


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
	public T S (T x)
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
	protected T t, h, f, z;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.CubicSpline.Knot#isInRange(java.lang.Object)
	 */
	public boolean isInRange (T x) throws RuntimeException
	{
		if (sm.lessThan (t, x)) return false;
		if (prior == null) throw new RuntimeException ("Parameter falls below spline low limit");
		if (sm.lessThan (prior.t, x)) return true;
		return false;
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
		this.knots = new ArrayList <Knot <T>> ();

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
	public List <Knot <T>> getKnots () { return knots; }
	protected List <Knot <T>> knots;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription <T> getSpaceDescription () { return sm; }

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager <T> getSpaceManager () { return sm; }


}

