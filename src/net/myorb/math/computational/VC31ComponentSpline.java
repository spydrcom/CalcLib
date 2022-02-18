
package net.myorb.math.computational;

import net.myorb.math.matrices.Vector;
import net.myorb.math.polynomial.families.ChebyshevPolynomial;
import net.myorb.math.polynomial.families.chebyshev.ChebyshevPolynomialCalculus;
import net.myorb.math.expressions.ExpressionComponentSpaceManager;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import net.myorb.data.abstractions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * mechanisms for construction of spline segments for multiple component data spaces
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class VC31ComponentSpline<T> implements Function<T>
{


	/**
	 * component index maps to the spline coefficients
	 * for the polynomial that will cover the component
	 * for the segment range connected to this segment
	 */
	public static class SegmentModels
		extends ArrayList<Regression.Model<Double>>
	{ private static final long serialVersionUID = 300341029690956270L; }


	/**
	 * collect segment descriptions.
	 * a domain range is mapped to the spline coefficients.
	 */
	public static class ComponentSpline
	{

		public static final int SPLINE_TICKS = 31;
		public static final double SPLINE_LO = -1.5, SPLINE_HI = 1.5;
		public static final double SPLINE_DELTA = (SPLINE_HI - SPLINE_LO) / (SPLINE_TICKS - 1);
		public static final DataSequence<Double> xAxis = getSplineAxis ();

		static DataSequence<Double> getSplineAxis ()
		{
			return DataSequence.evenlySpaced
			(
				SPLINE_LO, SPLINE_HI, SPLINE_DELTA, VC31LUD.mgr
			);
		}

		public ComponentSpline
			(
				SegmentModels models,
				double lo, double hi
			)
		{
			this.models = models;
			this.lo = lo; this.hi = hi;
			this.functionCoordinatesDelta = deltaFor (lo, hi);
			this.restricted = new boolean[models.size ()];
			Arrays.fill (restricted, false);
		}
		protected double lo, hi, functionCoordinatesDelta;
		protected SegmentModels models;

		/**
		 * compute function coordinate tick size
		 * @param lo the lo end of the function range
		 * @param hi the hi end of the function range
		 * @return function coordinate equivalent to spline tick
		 */
		public static double deltaFor (double lo, double hi)
		{
			return (hi - lo) / (SPLINE_TICKS - 1);
		}

		/**
		 * covert parameter from function to spline coordinate
		 * @param from function parameter coordinate
		 * @return spline parameter coordinate
		 */
		double translate (double from)
		{
			double offset = (from - lo) / functionCoordinatesDelta;
			return SPLINE_LO + offset * SPLINE_DELTA;
		}

		/**
		 * @param component index of component
		 * @return TRUE implies restriction
		 */
		boolean isRestricted (int component)
		{ return restricted[component]; }
		protected boolean[] restricted;

	}


	public VC31ComponentSpline
		(
			Function<T> f,
			ExpressionComponentSpaceManager<T> mgr,
			Parameterization configuration
		)
	{
		this.f = f;
		this.mgr = mgr;
		this.configuration = configuration;
		this.lud = new VC31LUD (configuration);
		this.splineSegments = new ArrayList<ComponentSpline> ();
		this.poly = new ChebyshevPolynomial<Double> (VC31LUD.mgr);
		this.calculus = new ChebyshevPolynomialCalculus<Double>(VC31LUD.mgr);
		this.regression = new Regression<Double> (VC31LUD.mgr);
		this.components = mgr.getComponentCount ();
	}
	protected Function<T> f;
	protected Regression<Double> regression;
	protected Parameterization configuration;
	protected ExpressionComponentSpaceManager<T> mgr;
	protected ChebyshevPolynomialCalculus<Double> calculus;
	protected ChebyshevPolynomial<Double> poly;
	protected int components;
	protected VC31LUD lud;


	/**
	 * build a spline 
	 *  for the function over a range
	 * @param lo the lo end of the range
	 * @param hi the hi end of the range
	 */
	public void addSegment (double lo, double hi)
	{
		ArrayList<T> points = new ArrayList<T> ();
		SegmentModels models = new SegmentModels ();
		calculatePoints (lo, ComponentSpline.deltaFor (lo, hi), points);

		for (int c = 0; c < mgr.getComponentCount (); c++)
		{
			DataSequence<Double> axis = new DataSequence<Double>();
			Vector<Double> v = calculateComponentAxis (c, points, axis);
			models.add (performRegression (v, axis));
		}
		
		splineSegments.add (new ComponentSpline (models, lo, hi));
	}
	protected List<ComponentSpline> splineSegments;


	/**
	 * collect the samples for the solution and regression evaluation
	 * @param component the index of the components being evaluated
	 * @param points the values found at the sample locations
	 * @param axis the computed values along the axis
	 * @return the vector to be solved via LUxB
	 */
	public Vector<Double> calculateComponentAxis
	(int component, ArrayList<T> points, DataSequence<Double> axis)
	{
		double computed; int ticks = ComponentSpline.SPLINE_TICKS;
		Vector<Double> v = new Vector<Double>(ticks, VC31LUD.mgr);
		for (int i = 1; i <= ticks; i++)
		{
			computed = mgr.component (points.get (i-1), component);
			v.set (i, computed); axis.add (computed);
		}
		return v;
	}


	/**
	 * collect evenly spaced sample points
	 * @param lo the lo value of the evaluation range
	 * @param delta the delta forming evenly spaced samples
	 * @param points the values found at the sample locations
	 */
	public void calculatePoints
	(double lo, double delta, ArrayList<T> points)
	{
		double x = lo;
		for (int i = 0; i < ComponentSpline.SPLINE_TICKS; i++)
		{
			points.add (f.eval (mgr.convertFromDouble (x)));
			x += delta;
		}
	}


	/**
	 * get regression model
	 * @param b vector to be solved
	 * @param axis computed values for component being evaluated
	 * @return the regression model from the evaluation of the solution
	 */
	public Regression.Model<Double> performRegression (Vector<Double> b, DataSequence<Double> axis)
	{
		GeneratingFunctions.Coefficients<Double> solution = lud.solve (b);
		DataSequence2D<Double> dataSeq = new DataSequence2D<Double> (ComponentSpline.xAxis, axis);
		Regression.Model<Double> r = regression.useChebyshevModel (solution, dataSeq);
		System.out.println (r);
		return r;
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		double
			p = mgr.convertToDouble (x),
			resultComponents[] = new double[components];
		for (int c = 0; c < components; c++)
		{
			ComponentSpline cs = getComponentSplineFor (c, p);
			resultComponents[c] = cs.models.get (c).eval (cs.translate (p));
		}
		return mgr.construct (resultComponents);
	}


	/**
	 * @param x parameter to evaluation
	 * @return the computed integral value
	 */
	public T evalIntegral (T x)
	{
		double
			p = mgr.convertToDouble (x),
			resultComponents[] = new double[components];
		for (int c = 0; c < components; c++)
		{
			ComponentSpline spline = getComponentSplineFor (c, p);
			ChebyshevPolynomial.Coefficients<Double> cs = spline.models.get (c).getCoefficients ();
			resultComponents[c] = calculus.evaluatePolynomialIntegral (cs, spline.translate (p));
		}
		return mgr.construct (resultComponents);
	}
	public T evalIntegral (T from, T to)
	{
		double
			hi = mgr.convertToDouble (to),
			lo = mgr.convertToDouble (from),
			resultComponents[] = new double[components];
		for (int c = 0; c < components; c++)
		{
			ComponentSpline spline = getComponentSplineFor (c, lo);
			ChebyshevPolynomial.Coefficients<Double> cs = spline.models.get (c).getCoefficients ();
			resultComponents[c] = calculus.evaluatePolynomialIntegral (cs, spline.translate (lo), spline.translate (hi));
		}
		return mgr.construct (resultComponents);
	}


	/**
	 * @param component the component number for the lookup
	 * @param point the parameter to the component function
	 * @return the spline best matched for the segment
	 */
	public ComponentSpline getComponentSplineFor (int component, double point)
	{
		ComponentSpline spline;
		int i = splineSegments.size ();
		while (--i >= 0)
		{
			spline = splineSegments.get (i);
			if (spline.isRestricted (component)) continue;
			if (point > spline.hi) continue;
			if (point < spline.lo) continue;
			return spline;
		}
		throw new RuntimeException ("No segment for parameter");
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<T> getSpaceDescription () { return mgr; }
	public SpaceManager<T> getSpaceManager () { return mgr; }


}

