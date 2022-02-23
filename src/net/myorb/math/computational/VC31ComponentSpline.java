
package net.myorb.math.computational;

import net.myorb.math.matrices.Vector;
import net.myorb.math.polynomial.families.ChebyshevPolynomial;
import net.myorb.math.polynomial.families.chebyshev.ChebyshevPolynomialCalculus;
import net.myorb.math.expressions.ExpressionComponentSpaceManager;

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
	 * a linear sequence of data points
	 */
	public class DataPoints extends ArrayList<T>
	{ private static final long serialVersionUID = 5221548388129835361L; }

	/**
	 * sequence of single dimensional data points
	 */
	public static class Sequence extends DataSequence<Double>
	{
		Sequence () {}
		Sequence (DataSequence<Double> data) { this.addAll (data); }
		private static final long serialVersionUID = 6070890820039699046L;
	}

	/**
	 * (X, Y) coordinate pairs describing a 2 dimensional function mapping
	 */
	public class TwoDimSpace extends DataSequence2D<Double>
	{
		public TwoDimSpace (Sequence x, Sequence y) { super (x, y); }
	}

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
		public static final Sequence xAxisComb = getSplineDomain (SPLINE_LO + SPLINE_DELTA/2);
		public static final Sequence xAxis = getSplineDomain (SPLINE_LO);

		static Sequence getSplineDomain (double starting)
		{
			return new Sequence
			(
				DataSequence.evenlySpaced
				(
					starting, SPLINE_DELTA, SPLINE_TICKS, VC31LUD.mgr
				)
			);
		}

		/**
		 * @param models the spline models for the components
		 * @param lo the lo end of the range described by this sequence
		 * @param hi the hi end of the range described by this sequence
		 */
		public ComponentSpline
			(
				SegmentModels models,
				double lo, double hi
			)
		{
			this.functionCoordinatesDelta = deltaFor (lo, hi);
			this.splineSlope = functionCoordinatesDelta / SPLINE_DELTA;
			this.models = models; this.lo = lo; this.hi = hi;
			this.restricted = new boolean[models.size ()];
			Arrays.fill (restricted, false);
			this.traceSplineSegment ();
		}
		protected double functionCoordinatesDelta, splineSlope;
		protected SegmentModels models;
		protected double lo, hi;

		/**
		 * @return largest SSE found in component models
		 */
		public double maxModelError ()
		{
			double max = 0.0;
			for (Regression.Model<Double> model : models)
			{
				double sse = model.computedSSE (); 
				max = sse>max? sse: max;
			}
			return max;
		}

		/**
		 * @return get the value of th slope used for coordinate translation
		 */
		public double getSlope ()
		{
			return splineSlope;
		}

		/**
		 * get a model for a specific component
		 * @param component the index identifying a component
		 * @return the model to use for the component
		 */
		public Regression.Model<Double> modelFor (int component)
		{
			return models.get (component);
		}

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
		public double translate (double from)
		{
			return SPLINE_LO + (from - lo) / splineSlope;
		}


		/**
		 * generate trace for new segment addition to spline
		 */
		public void traceSplineSegment ()
		{
			System.out.print (">> " + lo + " .. " + hi + ": SSE = ");
			System.out.println (maxModelError ());
		}

		/**
		 * @param component index of component
		 * @return TRUE implies restriction
		 */
		public boolean isRestricted (int component)
		{ return restricted[component]; }
		protected boolean[] restricted;

	}


	/**
	 * @param f a multi-dimensional unary function
	 * @param mgr a space manager for the data type enabled for component manipulation
	 * @param configuration a hash of configuration parameters
	 */
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
	protected Parameterization configuration;
	protected ExpressionComponentSpaceManager<T> mgr;
	protected ChebyshevPolynomialCalculus<Double> calculus;
	protected ChebyshevPolynomial<Double> poly;
	protected Regression<Double> regression;
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
		ComponentSpline spline =
				new ComponentSpline
				(
					generateModels (lo, hi),
					lo, hi
				);
		splineSegments.add (spline);
	}
	protected List<ComponentSpline> splineSegments;


	/**
	 * generate segment models
	 * @param lo the lo end of the range
	 * @param hi the hi end of the range
	 * @return the array of models for the segment
	 */
	public SegmentModels generateModels (double lo, double hi)
	{
		DataPoints points = new DataPoints (), combRange = new DataPoints ();
		generateData (points, combRange, lo, ComponentSpline.deltaFor (lo, hi));
		return generateModels (points, combRange);
	}


	/**
	 * @param points data points collection lo + delta
	 * @param combRange data points collection lo + delta/2
	 * @param lo the starting value for the sequence
	 * @param delta the space between samples
	 */
	public void generateData
		(
			DataPoints points, DataPoints combRange,
			double lo, double delta
		)
	{
		calculatePoints (lo + delta/2, delta, combRange);
		calculatePoints (lo, delta, points);
	}


	/**
	 * @param points data points collection lo + delta
	 * @param combRange data points collection lo + delta/2
	 * @return the models from the components regressions
	 */
	public SegmentModels generateModels (DataPoints points, DataPoints combRange)
	{
		Sequence axis, combSequence;
		SegmentModels models = new SegmentModels ();

		for (int c = 0; c < mgr.getComponentCount (); c++)
		{
			calculateComponentAxis (c, combRange, combSequence = new Sequence ());
			Vector<Double> toBeSolved = calculateComponentAxis (c, points, axis = new Sequence ());
			models.add (performRegression (toBeSolved, axis, combSequence));
		}

		return models;
	}


	/**
	 * collect the samples for the solution and regression evaluation
	 * @param component the index of the components being evaluated
	 * @param points the values found at the sample locations
	 * @param axis the computed values along the axis
	 * @return the vector to be solved via LUxB
	 */
	public Vector<Double> calculateComponentAxis
	(int component, DataPoints points, Sequence axis)
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
	(double lo, double delta, DataPoints points)
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
	 * @param comb computed values for half delta test points on axis
	 * @return the regression model from the evaluation of the solution
	 */
	public Regression.Model<Double> performRegression
		(
			Vector<Double> b, Sequence axis, Sequence comb
		)
	{
		return regression.useChebyshevModel
			(
				lud.solve (b), mergeOf (axis, comb)
			);
	}
	public TwoDimSpace mergeOf
		(
			Sequence regressionPoints,
			Sequence regressionMidPoints
		)
	{
		Sequence x = new Sequence (), y = new Sequence ();
		for (int i = 0; i < ComponentSpline.SPLINE_TICKS; i++)
		{
			x.add (ComponentSpline.xAxis.get (i)); x.add (ComponentSpline.xAxisComb.get (i));
			y.add (regressionPoints.get (i)); y.add (regressionMidPoints.get (i));
		}
		return new TwoDimSpace (x, y);
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
	 * evaluate integral over all segments
	 * @return computed integral
	 */
	public T evalIntegral ()
	{
		double resultComponents[] = new double[components];
		Arrays.fill (resultComponents, 0, components-1, 0.0);

		for (ComponentSpline seg : splineSegments)
		{
			for (int component = 0; component < components; component++)
			{
				resultComponents[component] += evalComponentIntegral (seg, component, seg.lo, seg.hi);
			}
		}

		return mgr.construct (resultComponents);
	}


	/**
	 * @param seg the ComponentSpline for a segment
	 * @param component the index of the component to evaluate
	 * @param lo the lo end of the range for evaluation
	 * @param hi the hi end of the range for evaluation
	 * @return the computed value
	 */
	public double evalComponentIntegral
	(ComponentSpline seg, int component, double lo, double hi)
	{
		ChebyshevPolynomial.Coefficients<Double>
			coefficients = seg.models.get (component).getCoefficients ();
		double l = seg.translate (lo), h = seg.translate (hi), s = seg.getSlope ();
		return s * calculus.evaluatePolynomialIntegral (coefficients, l, h);
	}


	/**
	 * use Chebyshev calculus to
	 *  compute integral of polynomial over range
	 * @param from lo end of integration range
	 * @param to hi end of integration range
	 * @return computed integral for range
	 */
	public T evalIntegral (T from, T to)
	{
		double resultComponents[] = new double[components],
			hi = mgr.convertToDouble (to), lo = mgr.convertToDouble (from);
		for (int c = 0; c < components; c++)
		{
			resultComponents[c] = evalComponentIntegral (getComponentSplineFor (c, lo), c, lo, hi);
		}
		return mgr.construct (resultComponents);
	}


	/**
	 * use Chebyshev calculus to
	 *  compute integral of polynomial at specified point
	 * @param x point at which to perform evaluation
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
			resultComponents[c] = spline.getSlope () * calculus.evaluatePolynomialIntegral (cs, spline.translate (p));
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

