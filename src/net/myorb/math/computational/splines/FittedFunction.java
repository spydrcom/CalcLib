
package net.myorb.math.computational.splines;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;

import net.myorb.data.abstractions.SimpleStreamIO.TextSource;
import net.myorb.data.abstractions.SpaceDescription;

import net.myorb.data.notations.json.*;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * describe a function that maps the curve of best fit as programmed
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class FittedFunction<T> implements Function<T>
{


	public FittedFunction
		(
			ExpressionComponentSpaceManager<T> mgr
		)
	{
		this.segments = new ArrayList<>();
		this.mgr = mgr;
	}
	protected ExpressionComponentSpaceManager<T> mgr;
	protected List<Segment<T>> segments;


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
	SegmentFunction<T> findSegment (T x)
	{
		SegmentFunction<T> f;
		double p = mgr.component (x, 0);
		for (Segment<T> segment : segments)
		{ if ((f = segment.checkFor (p)) != null) return f; }
		return null;
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
	void processSplineSegment (JsonSemantics.JsonObject descriptor)
	{
		segments.add (new Segment<T> (descriptor, mgr));
	}

	/**
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
	 * @param description the object node describing the spline segments
	 */
	void processSplineDescription (JsonSemantics.JsonObject description)
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


}


/**
 * a SegmentRepresentation parsed from a JSON tree node
 * @param <T> type on which operations are to be executed
 */
class Segment<T> implements SegmentRepresentation
{


	Segment
		(
			JsonSemantics.JsonObject descriptor,
			ExpressionComponentSpaceManager<T> mgr
		)
	{
		this.lo = lookup (descriptor, "lo");
		this.unit = lookup (descriptor, "unit");
		this.processCoefficients (descriptor);
		this.hi = lookup (descriptor, "hi");
		this.connectFunction (mgr);
	}


	/**
	 * allocate a segment function object for this segment
	 * @param mgr the Component Manager for the data type
	 */
	void connectFunction (ExpressionComponentSpaceManager<T> mgr)
	{ this.segmentFunction = new SegmentFunction<T> (this, mgr); }
	protected SegmentFunction<T> segmentFunction;


	/**
	 * @param descriptor the object being parsed
	 * @param member name of the member being read
	 * @return the value of the named member
	 */
	double lookup (JsonSemantics.JsonObject descriptor, String member)
	{
		return ((JsonSemantics.JsonNumber) descriptor.getMemberCalled (member))
				.getNumber ().doubleValue ();
	}


	/**
	 * @param descriptor node holding coefficients for all components
	 */
	void processCoefficients
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
	 * @param components the JSON array 
	 * 			holding coefficients for all components
	 */
	void processCoefficients (JsonSemantics.JsonArray components)
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
	SegmentFunction<T> checkFor (double value)
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
	public double getSegmentDelta () { return 0; }
	public double getSegmentError () { return 0; }
	public double getSegmentSlope () { return 0; }


}

