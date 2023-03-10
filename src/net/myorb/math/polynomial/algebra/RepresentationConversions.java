
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
	public enum NodeTypes {Identifier, Value, BinaryOP, UnaryOP}


	// translation from JSON representation

	/**
	 * translation of expression tree to element tree
	 * @param tree the JSON expression tree description
	 * @param root expansion tree root for this translation
	 * @return the equivalent element tree
	 */
	public static Factor translate
	(JsonLowLevel.JsonValue tree, SeriesExpansion <?> root)
	{
		Factor equation = new Equation ();
		recognize (tree, equation, root);
//		equation = Manipulations.reduceAndCollectTerms
//		(
//			(Elements.Sum) equation, "x"
//		);
		return equation;
	}


	/**
	 * recognize a node from node-type
	 * @param node the JSON node to recognize
	 * @param parent the element node being built
	 * @param root expansion tree root
	 */
	public static void recognize
	(JsonLowLevel.JsonValue node, Factor parent, SeriesExpansion <?> root)
	{
		switch (recognizeType (node))
		{
			case BinaryOP:		add (translateOperation (node, root), parent);		break;
			case UnaryOP:		add (translateInvocation (node, root), parent);		break;
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
	 * process symbol reference to series
	 * @param node the node identified as a UnaryOp
	 * @param root the expansion object controlling this process
	 * @return the factor describing the referenced series
	 */
	public static Factor translateInvocation
	(JsonLowLevel.JsonValue node, SeriesExpansion <?> root)
	{
		Factor parent = new Sum (), parameter = new Sum ();
		JsonSemantics.JsonObject object = (JsonSemantics.JsonObject) node;
		recognize ( object.getMemberCalled ("Parameter"), parameter, root );
		parent = root.expandSymbol (member ("OpName", object), root);
		return parent;
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
	 * @param root expansion tree root
	 */
	public static Factor translateOperation
	(JsonLowLevel.JsonValue node, SeriesExpansion <?> root)
	{
		JsonSemantics.JsonObject object =
			(JsonSemantics.JsonObject) node;
		Factor parent = identifyOperation (object);
		recognize ( object.getMemberCalled ("Left"), parent, root );
		recognize ( object.getMemberCalled ("Right"), rightParentFor (parent), root );
		return parent;
	}
	public static Factor rightParentFor (Factor parent)
	{
		Factor rightParent = parent;
		if (parent instanceof Difference)
		{
			rightParent = new Product ();
			add (new Constant (-1.0), rightParent);
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
