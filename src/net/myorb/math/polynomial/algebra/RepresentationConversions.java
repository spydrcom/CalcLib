
package net.myorb.math.polynomial.algebra;

import net.myorb.data.notations.json.JsonSemantics;
import net.myorb.data.notations.json.JsonLowLevel;

/**
 * conversions between representations of polynomials
 * @author Michael Druckman
 */
public class RepresentationConversions extends Elements
{


	/**
	 * symbols that identify types of JSON nodes
	 */
	public enum NodeTypes {Identifier, Value, BinaryOP}


	// translation from JSON representation

	/**
	 * translation of expression tree to element tree
	 * @param tree the JSON expression tree description
	 * @return the equivalent element tree
	 */
	public static Equation translate (JsonLowLevel.JsonValue tree)
	{
		Equation equation = new Equation ();
		recognize (tree, equation);
		return equation;
	}


	/**
	 * recognize a node from node-type
	 * @param node the JSON node to recognize
	 * @param parent the element node being built
	 */
	public static void recognize (JsonLowLevel.JsonValue node, Factor parent)
	{
		switch (recognizeType (node))
		{
			case BinaryOP:		add (translateOperation (node), parent);			break;
			case Identifier:	add (new Variable (member ("Name", node)), parent); break;
			case Value: 		add (new Constant (node.toString ()), parent);		break;
			default:			throw new RuntimeException ("Unrecognized node");
		}
	}
	static String member (String called, JsonLowLevel.JsonValue in)
	{
		return ((JsonSemantics.JsonObject) in).getMemberString (called);
	}


	/**
	 * determine type of JSON node
	 * @param node the JSON node to recognize
	 * @return the type of the node
	 */
	public static NodeTypes recognizeType (JsonLowLevel.JsonValue node)
	{
		if (isNumeric (node)) return NodeTypes.Value;
		else return NodeTypes.valueOf (member ("NodeType", node));
	}
	static boolean isNumeric (JsonLowLevel.JsonValue node)
	{
		return node.getJsonValueType () == JsonLowLevel.JsonValue.ValueTypes.NUMERIC;
	}


	/**
	 * translation of expression tree node to element node
	 * @param node the JSON node to translate
	 * @return the translated element factor
	 */
	public static Factor translateOperation (JsonLowLevel.JsonValue node)
	{
		JsonSemantics.JsonObject object =
			(JsonSemantics.JsonObject) node;
		Factor parent = identifyOperation (object);
		recognize ( object.getMemberCalled ("Left"), parent );
		recognize ( object.getMemberCalled ("Right"), rightParentFor (parent) );
		return parent;
	}
	public static Factor rightParentFor (Factor parent)
	{
		Factor rightParent = parent;
		if (parent instanceof Difference)
		{
			rightParent = new Product ();
			add (new Constant ("-1"), rightParent);
			add (rightParent, parent);
		}
		return rightParent;
	}
	public static Factor identifyOperation (JsonSemantics.JsonObject node)
	{
		switch ( member ("OpName", node).charAt (0) )
		{
			case '+': return new Sum ();
			case '-': return new Difference ();
			case '*': return new Product ();
			case '^': return new Power ();
		}
		throw new RuntimeException ("Unrecognized operation");		
	}


}
