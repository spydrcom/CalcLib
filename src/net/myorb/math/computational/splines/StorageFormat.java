
package net.myorb.math.computational.splines;

import net.myorb.math.expressions.tree.JsonBinding;
import net.myorb.data.notations.json.JsonSemantics;

import java.util.List;

/**
 * provide JSON representation for a function spline
 * @author Michael Druckman
 */
public class StorageFormat
{


	/**
	 * @param rep a spline segment representation to be described
	 * @return JSON description of segment
	 */
	public static JsonSemantics.JsonValue describeSplineSegment (SegmentRepresentation rep)
	{
		JsonSemantics.JsonObject description = new JsonSemantics.JsonObject () ;
		description.addMember ("delta", new JsonSemantics.JsonNumber (rep.getSegmentDelta ()));
		description.addMember ("error", new JsonSemantics.JsonNumber (rep.getSegmentError ()));
		description.addMember ("slope", new JsonSemantics.JsonNumber (rep.getSegmentSlope ()));
		description.addMember ("unit", new JsonSemantics.JsonNumber (rep.getUnitSlope ()));
		description.addMember ("lo", new JsonSemantics.JsonNumber (rep.getSegmentLo ()));
		description.addMember ("hi", new JsonSemantics.JsonNumber (rep.getSegmentHi ()));
		description.addMember ("coefficients", getCoefficientsFor (rep));
		description.setOrderedMembersList (DESCRIPTION_MEMBERS);
		return description;
	}
	public static final String[] DESCRIPTION_MEMBERS = {"lo", "hi", "delta", "error", "slope", "unit", "coefficients"};


	/**
	 * @param rep a spline segment representation to be described
	 * @return JSON array description of spline coefficients for segment
	 */
	public static JsonSemantics.JsonValue getCoefficientsFor (SegmentRepresentation rep)
	{
		List <Double> a;
		JsonSemantics.JsonArray cmpnts =
				new JsonSemantics.JsonArray ();
		for (int i = 0; i < rep.getComponentCount (); i++)
		{
			if ((a = rep.getCoefficientsFor (i)) == null) continue;
			cmpnts.add (new JsonSemantics.JsonArray (a));
		}
		return cmpnts;
	}


	/**
	 * @param segments a representation for the elements of the spline
	 * @return JSON object holding the elements of the spline
	 */
	public static JsonSemantics.JsonValue describe (Representation segments)
	{
		List<SegmentRepresentation>
			segmentList = segments.getSegmentList ();
		if (segmentList == null) return JsonSemantics.getNull ();
		JsonSemantics.JsonArray description = new JsonSemantics.JsonArray () ;
		for (SegmentRepresentation s : segmentList) description.add (describeSplineSegment (s));
		return description;
	}


	/**
	 * @param name the name to give to the spline function
	 * @param parameter the name for the parameter to the function (indicative of type)
	 * @param description the text of a description for the defined function
	 * @param segments a representation for the elements of the spline
	 * @return JSON expression tree representation of spline
	 */
	public static JsonSemantics.JsonValue express
		(
			String name, String parameter, String description,
			Representation segments
		)
	{
		JsonSemantics.JsonObject tree = new JsonSemantics.JsonObject ();
		tree.addMemberNamed ("Name", new JsonSemantics.JsonString (name));
		tree.addMemberNamed ("Parameter", new JsonSemantics.JsonString (parameter));
		tree.addMemberNamed ("Description", new JsonSemantics.JsonString (description));
		tree.addMemberNamed ("NodeType", new JsonSemantics.JsonString (JsonBinding.NodeTypes.Sectioned.toString ()));
		tree.addMemberNamed ("Interpreter", new JsonSemantics.JsonString (segments.getInterpretation ()));
		tree.addMemberNamed ("Sections", describe (segments));
		tree.setOrderedMembersList (EXPRESS_MEMBERS);
		return tree;
	}
	public static final String[] EXPRESS_MEMBERS = {"Name", "Parameter", "Description", "Interpreter", "NodeType", "Sections"};


}

