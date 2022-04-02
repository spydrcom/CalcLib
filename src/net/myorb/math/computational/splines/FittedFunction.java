
package net.myorb.math.computational.splines;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;

import net.myorb.data.abstractions.SimpleStreamIO.TextSource;

import net.myorb.data.notations.json.*;

import java.util.ArrayList;
import java.util.List;

/**
 * describe a function that maps the curve of best fit as programmed
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class FittedFunction <T> extends SegmentOperations <T>
{


	public FittedFunction
		(
			ExpressionComponentSpaceManager <T> mgr,
			SplineMechanisms spline
		)
	{
		super (mgr, spline);
	}


	/*
	 * process elements of JSON tree
	 */

	/**
	 * add to segments list from a JSON descriptor node
	 * @param descriptor the object node describing a segment
	 */
	public void processSplineSegment (JsonSemantics.JsonObject descriptor)
	{
		segments.add (new TransportedSegment <T> (descriptor, mgr, spline));
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

}


/**
 * a SegmentRepresentation parsed from a JSON tree node
 * @param <T> type on which operations are to be executed
 */
class TransportedSegment <T> extends SegmentProperties <T>
	implements SegmentAbilities <T>
{


	public TransportedSegment
		(
			JsonSemantics.JsonObject descriptor,
			ExpressionComponentSpaceManager<T> mgr,
			SplineMechanisms spline
		)
	{
		super (mgr, spline);
		this.initFrom (descriptor);
		this.processCoefficients (descriptor);
		this.connectFunction ();
		this.setMargins ();
	}


	/**
	 * get the coefficients array from a JSON object
	 * @param descriptor node holding coefficients for all components
	 */
	public void processCoefficients
		(JsonSemantics.JsonObject descriptor)
	{
		componentCoefficients = new ArrayList <List <Double>> ();
		processCoefficients (JsonTools.getArrayFrom (descriptor, "coefficients"));
	}


	/**
	 * add a list of coefficients to the list held per component
	 * @param components the JSON array holding coefficients for all components
	 */
	public void processCoefficients (JsonSemantics.JsonArray components)
	{
		for (int i = 0; i < components.size (); i++)
		{
			componentCoefficients.add
			(
				JsonTools.toFloatList (JsonTools.toArray (components.get (i)))
			);
		}
	}


	/**
	 * copy descriptor data to local properties
	 * @param descriptor the JSON object holding the configured data items
	 */
	public void initFrom (JsonSemantics.JsonObject descriptor)
	{
		this.lo = lookup (descriptor, "lo");
		this.unit = lookup (descriptor, "unit");
		this.slope = lookup (descriptor, "slope");
		this.delta = lookup (descriptor, "delta");
		this.error = lookup (descriptor, "error");
		this.hi = lookup (descriptor, "hi");
	}


	/**
	 * get the numeric value of a named member of a JSON object
	 * @param descriptor the object being parsed
	 * @param member name of the member being read
	 * @return the value of the named member
	 */
	public double lookup (JsonSemantics.JsonObject descriptor, String member)
	{ return JsonTools.getValueFrom (descriptor, member); }


}

