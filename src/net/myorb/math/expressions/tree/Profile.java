
package net.myorb.math.expressions.tree;

import net.myorb.math.computational.Spline;
import net.myorb.math.computational.splines.Representation;
import net.myorb.math.expressions.tree.JsonBinding.Node;

import net.myorb.data.notations.json.JsonSemantics;
import net.myorb.data.notations.json.JsonTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * a formal definition of the Profile Node
 * @author Michael Druckman
 */
public class Profile extends JsonBinding.Node
{


	/**
	 * the names of the Profile node members
	 */
	public enum ProfileMembers {Name, Parameter, Parameters, Imports, Interpreter, Sections, Expression, Description}


	/**
	 * parameter name collection object
	 */
	public static class ParameterList
		extends SemanticAnalysis.FunctionCall.FormalParameterList
	{
		public ParameterList () {}
		public ParameterList (String name) { super (name); }
		public ParameterList (List <String> names) { super (names); }
		private static final long serialVersionUID = 2210976733709180094L;
	}


	/**
	 * new profile creation
	 */
	public Profile () { super (JsonBinding.NodeTypes.Profile); }


	/**
	 * a new node taking properties from a JSON Object
	 * @param object the JSON Object (coming from Value) holding the initial properties
	 */
	public Profile (JsonSemantics.JsonObject object) { super (object); }


	/**
	 * attach a tree element to this profile as Expression
	 * @param element the element to be added as the expression
	 */
	public void setExpression (Element element)
	{
		addMember (ProfileMembers.Expression, JsonBinding.toJson (element));
	}


	/**
	 * attach a spline representation to the profile
	 * @param spline the descriptor describing the spline
	 */
	public void setSpline (SectionedSpline <?> spline)
	{
		if (spline != null) setSpline (spline.getSplineWrapper ());
	}


	/**
	 * attach a spline representation to the profile
	 * @param spline the Operations object describing the spline
	 */
	public void setSpline (Spline.Operations <?> spline)
	{
		if (spline != null) setSpline (spline.getRepresentation ());
	}


	/**
	 * attach a spline representation to the profile
	 * @param representation the Representation object describing the spline
	 */
	public void setSpline (Representation representation)
	{
		if (representation == null) return;
		addMember (ProfileMembers.Interpreter,
			JsonSemantics.stringOrNull (representation.getInterpretation ()));
		addMember (ProfileMembers.Sections, JsonBinding.toJson (representation));
	}


	/**
	 * add an imports member to the profile
	 * @param imports hash of configuration parameters for imported symbols
	 */
	public void addImports (Map<String,Map<String,String>> imports)
	{
		if (imports == null || imports.size () == 0) return;
		addMember (ProfileMembers.Imports, JsonTools.symbolHash (imports));
	}


	/**
	 * get the import hash attached to the profile
	 * @return the member Value labeled as Imports
	 */
	public JsonSemantics.JsonValue getImports ()
	{
		return getMember (ProfileMembers.Imports);
	}


	/**
	 * get the expression attached to the profile
	 * @return the member Value labeled as Expression
	 */
	public JsonSemantics.JsonValue getExpression ()
	{
		return getMember (ProfileMembers.Expression);
	}


	/**
	 * get the spline attached to the profile
	 * @return the member Value labeled as Sections
	 */
	public JsonSemantics.JsonValue getSections ()
	{
		return getMember (ProfileMembers.Sections);
	}


	/**
	 * get the identifier attached to the profile
	 * @return the identifier found in the profile
	 */
	public String getProfileIdentifier ()
	{
		return getMemberString (ProfileMembers.Name);
	}


	/**
	 * get the description attached to the profile
	 * @return the description found in the profile
	 */
	public String getProfileDescription ()
	{
		return JsonSemantics.getStringOrNull (getMember (ProfileMembers.Description));
	}
	
	/**
	 * @param description text of a description of the expression
	 */
	public void setProfileDescription (String description)
	{
		addMember (ProfileMembers.Description, JsonSemantics.stringOrNull (description));
	}


	/**
	 * get the parameter list attached to the profile
	 * @return the list of parameter names from the profile
	 */
	public ParameterList getProfileParameters ()
	{
		ParameterList list = new ParameterList ();
		JsonSemantics.JsonValue parameter = getMember (ProfileMembers.Parameter);
		if (parameter != null) { list.add (JsonSemantics.getStringOrNull (parameter)); return list; }
		JsonSemantics.JsonValue parameters = getMember (ProfileMembers.Parameters);
		JsonSemantics.JsonArray array = (JsonSemantics.JsonArray) parameters;
		for (JsonSemantics.JsonValue value : array)
		{
			JsonSemantics.JsonString s =
				(JsonSemantics.JsonString) value;
			list.add (s.getContent ());
		}
		return list;
	}


	/**
	 * format the parameter list from the profile
	 * @return a formatted formal parameter list
	 */
	public String getFormattedParameterList ()
	{
		return getProfileParameters ().getProfile ();
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.notations.json.JsonSemantics.JsonObject#toString()
	 */
	public String toString ()
	{
		return getProfileIdentifier () + " " + getFormattedParameterList ();
	}


	/**
	 * add profile members to node
	 * @param name the name of the function
	 * @param parameters the parameter names in the profile
	 * @param to the node being built
	 */
	public static void addProfileMembers
	(String name, ParameterList parameters, Node to)
	{
		if (parameters.size () == 1)
		{
			to.addMember
			(
				ProfileMembers.Parameter,
				new JsonSemantics.JsonString (parameters.get (0))
			);
		}
		else
		{
			JsonSemantics.JsonArray parameterArray =
					new JsonSemantics.JsonArray ().includingStrings (parameters);
			to.addMember (ProfileMembers.Parameters, parameterArray);
		}
		to.addMember (ProfileMembers.Name, new JsonSemantics.JsonString (name));
	}


	/**
	 * construct profile node
	 * @param name the name of the function
	 * @param parameters the parameter names in the profile
	 * @return a Node containing the profile
	 */
	public static Profile representing (String name, ParameterList parameters)
	{
		Profile profile = new Profile ();
		addProfileMembers (name, parameters, profile);
		return profile;
	}


	/**
	 * convert JSON Object to Profile node.
	 *  Value must be Object or cast error will be seen.
	 *  NodeType == Profile is the JsonBinding check performed.
	 * @param value a JSON object expected to be a Profile node
	 * @return a new Profile object or NULL if checks fail
	 */
	public static Profile toProfile (JsonSemantics.JsonValue value)
	{
		JsonSemantics.JsonObject
			object = JsonBinding.performProfileChecks (value);
		return object == null ? null : new Profile (object);
	}


	/**
	 * validate a Profile node
	 * @param value a JSON object expected to be a Profile node
	 * @return a new Profile object
	 */
	public static Profile representing (JsonSemantics.JsonValue value)
	{
		if (value == null) return null;
		else if (value instanceof JsonSemantics.JsonObject) return toProfile (value);
		else return null;
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.notations.json.JsonSemantics.JsonObject#getMembers()
	 */
	public Object[] getMembers () { return ORDERED_MEMBER_LIST; }


	/**
	 * this constant list causes the members to come out in specified order.
	 * this allows the name, parameters, and description to be seen at the top of the profile.
	 * NodeType must be present in list for JSON source to be recognized as a profile.
	 * Description is also forced out as NULL if otherwise not present.
	 * this all makes the profile a consistent document format.
	 */
	public static final Object[] ORDERED_MEMBER_LIST = new Object[]
	{
		ProfileMembers.Name, ProfileMembers.Parameters, ProfileMembers.Parameter, ProfileMembers.Description,
		ProfileMembers.Imports, ProfileMembers.Interpreter, ProfileMembers.Expression,
		ProfileMembers.Sections, Node.DISCRIMINATOR
	};


	/**
	 * @return an array holding the member names of the profile object
	 */
	public static String[] getNameList ()
	{
		int item = 0;
		String[] names = new String[ORDERED_MEMBER_LIST.length];
		for (Object name : ORDERED_MEMBER_LIST)
		{ names[item++] = name.toString (); }
		return names;
	}


	/**
	 * prepare an ordered list of members present in object
	 */
	public void orderMembersList ()
	{
		List<String> names = new ArrayList<String>();

		for (Object name : ORDERED_MEMBER_LIST)
		{
			if (getMemberCalled (name.toString ()) == null) continue;
			names.add (name.toString ());
		}

		setOrderedMembersList (names.toArray (new String[]{}));
	}


}

