
package net.myorb.math.expressions.tree;

import net.myorb.math.computational.splines.Representation;
import net.myorb.math.computational.splines.StorageFormat;

import net.myorb.data.notations.json.JsonSemantics;

/**
 * bindings of Element types to JSON representations
 * @author Michael Druckman
 */
public class JsonBinding
{


	/**
	 * node types described with JsonObject
	 */
	public enum NodeTypes
	{
		Range, BinaryOP, Identifier, UnaryOP, Calculus, Segment, Spline, Sectioned, Profile
	}
	// otherwise JsonArray, JsonNumber


	/**
	 * provide to/from translations for JSON
	 * @param <T> data type
	 */
	public interface JsonRepresentation<T>
	{
		/**
		 * @return a JSON Value for the node
		 */
		JsonSemantics.JsonValue toJson ();

		/**
		 * @param context a JSON representation for the item being restored
		 * @param restoreManager a JsonRestore object holding semantic data for the restore
		 * @return the restored Element built from the context supplied
		 * @throws Exception for any errors
		 */
		Element fromJson
			(
				JsonSemantics.JsonValue context,
				JsonRestore<T> restoreManager
			)
		throws Exception;
	}


	/**
	 * extend JSON Object as the representation of a Node of an expression tree
	 */
	public static class Node extends JsonSemantics.JsonObject
	{

		/**
		 * name of member that identifies type of node
		 */
		public static final String DISCRIMINATOR = "NodeType";

		/**
		 * new node with assigned type
		 * @param type the type to be assigned
		 */
		public Node (NodeTypes type) { addNodeTypeFor (type, this); }

		/**
		 * a new node taking properties from a JSON Object
		 * @param object the JSON Object holding the initial properties
		 */
		public Node (JsonSemantics.JsonObject object) { copyMembers (object); }

		/**
		 * construct on value to be cast to object
		 * @param value the JSON Object coming from Value
		 */
		public Node (JsonSemantics.JsonValue value) { this ((JsonSemantics.JsonObject) value); }

		/**
		 * add a member to the node given an object that has JSON representation implemented
		 * @param identifier the name of the member to be added (in the JSON Object sense)
		 * @param rep an Element that has a defined representation in JSON
		 * @param <I> the type of identifiers used
		 * @param <T> the type of data used
		 */
		public <I,T> void addMember (I identifier, JsonRepresentation<T> rep) { addMember (identifier, rep.toJson ()); }

		/**
		 * @return the type of the node from the bound object discriminator
		 */
		public NodeTypes getNodeType () { return getNodeTypeOf (this); }
	}


	/**
	 * add member to node specifying type
	 * @param type the type that applies to the node
	 * @param toNode the node to be amended
	 */
	public static void addNodeTypeFor (NodeTypes type, JsonSemantics.JsonObject toNode)
	{
		toNode.addMember (Node.DISCRIMINATOR, new JsonSemantics.JsonString (type.toString ()));
	}


	/**
	 * read node type from object
	 * @param fromNode the JSON object to read from
	 * @return the type of the node
	 */
	public static NodeTypes getNodeTypeOf (JsonSemantics.JsonObject fromNode)
	{
		return NodeTypes.valueOf (fromNode.getMemberString (Node.DISCRIMINATOR));
	}


	/**
	 * @param object a JSON object
	 * @return TRUE = object represents a node
	 */
	public static boolean isNode (JsonSemantics.JsonObject object)
	{
		return object.getMemberString (Node.DISCRIMINATOR) != null;
	}


	/**
	 * must be object with node type Profile
	 * @param value a parsed JSON object (must be JSON object or cast will fail)
	 * @return value as object
	 */
	public static JsonSemantics.JsonObject performProfileChecks (JsonSemantics.JsonValue value)
	{
		JsonSemantics.JsonObject object = (JsonSemantics.JsonObject) value;
		if ( ! isNode (object) ) throw new RuntimeException ("Source is not an expression representation");
		else if (getNodeTypeOf (object) != NodeTypes.Profile) return null;
		else return object;
	}


	/**
	 * get JSON representation of Element
	 * @param element the element to represent
	 * @return the JsonValue object representation
	 * @param <T> data type
	 */
	@SuppressWarnings ("unchecked")
	public static <T> JsonSemantics.JsonValue toJson (Element element)
	{
		if (element instanceof JsonRepresentation)
		{
			return ((JsonRepresentation<T>) element).toJson ();
		}
		System.out.println
		("JSON conversion error: " + element.toString () + " - " + element.getElementType ());
		return new LexicalAnalysis.Identifier<T> ("'").toJson ();
	}


	/**
	 * produce JSON representation of spline sections
	 * @param representation the spline representation object
	 * @return JSON representation of the spline sections
	 */
	public static JsonSemantics.JsonValue toJson (Representation representation)
	{
		return StorageFormat.describe (representation);
	}


}

