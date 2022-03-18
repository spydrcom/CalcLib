
package net.myorb.math.computational.splines;

import net.myorb.math.computational.Spline;
import net.myorb.math.expressions.ExpressionComponentSpaceManager;

import net.myorb.data.abstractions.SimpleStreamIO.TextSource;
import net.myorb.data.abstractions.SpaceDescription;

import net.myorb.data.notations.json.*;

import net.myorb.math.SpaceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * describe a function that maps the curve of best fit as programmed
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class FittedFunction <T> implements Spline.Operations <T>
{


	/**
	 * a full descriptor of the structure of the spline fitted by the function
	 * @param <T> type on which operations are to be executed
	 */
	public interface FittedSegmentRepresentation <T> 
			extends SegmentRepresentation
	{
		/**
		 * get a function that maps the segment
		 * @return a function that maps the represented segment
		 */
		SegmentFunction<T> getSegmentFunction ();

		/**
		 * identify segment if maps to value
		 * @param value the point to locate a segment for
		 * @return the function for the segment, or NULL if not a match
		 */
		SegmentFunction<T> checkFor (double value);
	}


	public FittedFunction
		(
			ExpressionComponentSpaceManager <T> mgr,
			SplineMechanisms spline
		)
	{
		this.segments = new ArrayList<>();
		this.spline = spline;
		this.mgr = mgr;
	}
	protected List< FittedSegmentRepresentation <T> > segments;
	protected ExpressionComponentSpaceManager <T> mgr;
	protected SplineMechanisms spline;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		return findSegment (x).eval (x);
	}


	/**
	 * find a segment descriptor that covers the parameter
	 * @param x the parameter value to find a segment function for
	 * @return the segment function that covers the parameter, or NULL if none
	 */
	public SegmentFunction<T> findSegment (T x)
	{
		SegmentFunction<T> f;
		double p = mgr.component (x, 0);
		for (FittedSegmentRepresentation<T> segment : segments)
		{ if ((f = segment.checkFor (p)) != null) return f; }
		return null;
	}


	/**
	 * use the spline to compute the integral over a range
	 * @param lo the lo end of integral range in function coordinates
	 * @param hi the hi end of integral range in function coordinates
	 * @return the computed value of the integral for the specified range
	 */
	public T evalIntegralOver
		(
			double lo, double hi
		)
	{
		T result = mgr.getZero ();
		for (FittedSegmentRepresentation<T> segment : segments)
		{
			T portion = segment.getSegmentFunction ()
				.evalIntegralContribution (lo, hi);
			result = mgr.add (result, portion);
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Spline.Operations#evalIntegral()
	 */
	public T evalIntegral ()
	{
		T result = mgr.getZero ();
		for (FittedSegmentRepresentation<T> segment : segments)
		{
			T portion = segment.getSegmentFunction ().evalIntegral ();
			result = mgr.add (result, portion);
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<T> getSpaceDescription () { return mgr; }
	public SpaceManager<T> getSpaceManager () { return mgr; }


	/*
	 * process elements of JSON tree
	 */

	/**
	 * add to segments list from a JSON descriptor node
	 * @param descriptor the object node describing a segment
	 */
	public void processSplineSegment (JsonSemantics.JsonObject descriptor)
	{
		segments.add (new Segment<T> (descriptor, mgr, spline));
	}

	/**
	 * process the arrayed elements of a JSON array
	 * @param segments the JSON array of segment descriptors
	 */
	public void processSplineSegments (JsonSemantics.JsonArray segments)
	{
		for (JsonLowLevel.JsonValue segment : segments)
		{
			processSplineSegment ((JsonSemantics.JsonObject) segment);
		}
	}

	/**
	 * parse the sections node taken from a JSON object
	 * @param description the object node describing the spline segments
	 */
	public void processSplineDescription (JsonSemantics.JsonObject description)
	{
		processSplineSegments ((JsonSemantics.JsonArray) description.getMemberCalled ("Sections"));
	}

	/**
	 * read a JSON tree describing a spline
	 * @param source a text source referring to the JSON tree
	 * @throws JsonTokenParser.Unexpected for unrecognized tokens
	 * @throws Exception for other forms of errors
	 */
	public void readFrom (TextSource source) throws JsonTokenParser.Unexpected, Exception
	{
		JsonSemantics.JsonValue json = JsonReader.readFrom (source);
		processSplineDescription ((JsonSemantics.JsonObject) json);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Spline.Operations#getRepresentation()
	 */
	public Representation getRepresentation ()
	{
		return new Representation ()
		{
			public List<SegmentRepresentation> getSegmentList ()
			{
				List<SegmentRepresentation> list = new ArrayList<>();
				list.addAll (segments);
				return list;
			}
			public String getInterpretation ()
			{
				return spline.getInterpreterPath ();
			}
		};
	}

}


/**
 * a SegmentRepresentation parsed from a JSON tree node
 * @param <T> type on which operations are to be executed
 */
class Segment<T> implements FittedFunction.FittedSegmentRepresentation<T>
{


	public Segment
		(
			JsonSemantics.JsonObject descriptor,
			ExpressionComponentSpaceManager<T> mgr,
			SplineMechanisms spline
		)
	{
		this.mgr = mgr;
		this.spline = spline;
		this.lo = lookup (descriptor, "lo");
		this.unit = lookup (descriptor, "unit");
		this.slope = lookup (descriptor, "slope");
		this.delta = lookup (descriptor, "delta");
		this.error = lookup (descriptor, "error");
		this.processCoefficients (descriptor);
		this.hi = lookup (descriptor, "hi");
		this.connectFunction ();
	}
	protected ExpressionComponentSpaceManager<T> mgr;
	protected SplineMechanisms spline;


	/**
	 * allocate a segment function object for this segment
	 */
	public void connectFunction ()
	{ this.segmentFunction = new SegmentFunction<T> (this, mgr, spline); }
	public SegmentFunction<T> getSegmentFunction () { return this.segmentFunction; }
	protected SegmentFunction<T> segmentFunction;


	/**
	 * get the numeric value of a named member of a JSON object
	 * @param descriptor the object being parsed
	 * @param member name of the member being read
	 * @return the value of the named member
	 */
	public double lookup (JsonSemantics.JsonObject descriptor, String member)
	{
		return ((JsonSemantics.JsonNumber) descriptor
				.getMemberCalled (member)).getNumber ()
				.doubleValue ();
	}


	/**
	 * get the coefficients array from a JSON object
	 * @param descriptor node holding coefficients for all components
	 */
	public void processCoefficients
		(JsonSemantics.JsonObject descriptor)
	{
		componentCoefficients = new ArrayList<List<Double>>();

		processCoefficients
		(
			(JsonSemantics.JsonArray)
			descriptor.getMemberCalled ("coefficients")
		);
	}


	/**
	 * add a list of coefficients to the list held per component
	 * @param components the JSON array holding coefficients for all components
	 */
	public void processCoefficients (JsonSemantics.JsonArray components)
	{
		for (int i = 0; i < components.size (); i++)
		{
			JsonSemantics.JsonArray c =
				(JsonSemantics.JsonArray) components.get (i);
			List<Double> coefs = JsonTools.toFloatList (c);
			componentCoefficients.add (coefs);
		}
	}


	/**
	 * check for the segment holding a give value mapping
	 * @param value the parameter value to find a segment for
	 * @return the segment function for matches or NULL if no match
	 */
	public SegmentFunction<T> checkFor (double value)
	{
		if (value >= lo && value <= hi)
		{ return segmentFunction; }
		else return null;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getComponentCount()
	 */
	public int getComponentCount ()
	{ return componentCoefficients.size (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getCoefficientsFor(int)
	 */
	public List<Double>
		getCoefficientsFor (int component)
	{ return componentCoefficients.get (component); }
	protected List<List<Double>> componentCoefficients;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getSegmentLo()
	 */
	public double getSegmentLo () { return lo; }
	public double getUnitSlope () { return unit; }
	public double getSegmentHi () { return hi; }
	protected double lo, hi, unit;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getSegmentDelta()
	 */
	public double getSegmentDelta () { return delta; }
	public double getSegmentError () { return error; }
	public double getSegmentSlope () { return slope; }
	protected double delta, error, slope;


}

