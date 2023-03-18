
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
		Elements.Sum equation;
		recognize (tree, equation = new Equation (), root);
		return Manipulations.reduceAndCollectTerms
		(
			reduceTermsOf (equation),
			root.getPolynomialVariable ()
		);
	}
	static Elements.Sum reduceTermsOf (Elements.Sum equation)
	{
		Elements.Sum reduced = new Elements.Sum ();
		for (Factor factor : equation)
		{
			reduced = (Elements.Sum) Operations.sumOf
				(reduced, reducedTerm (factor));
		}
		return reduced;
	}
	static Factor reducedTerm ( Factor term )
	{
		if (term instanceof Elements.Product)
		{ return reducedProductsFrom ( (Elements.Product) term ); }
		else return term;
	}
	static Factor reducedProductsFrom ( Elements.Product factors )
	{
		Factor result;
		if ( ( result = getSingleChild (factors) ) == null )
		{ result = reducedProductFrom (factors); }
		return result;
	}
	static Factor reducedProductFrom ( Elements.Product factors )
	{
		int n = factors.size ();
		Factor product = Operations.productOf (factors.get (0), factors.get (1));
		for (int i = 2; i < n; i++) product = Operations.productOf (product, factors.get (i));
		return product;
	}


	/**
	 * process an identifier reference
	 * @param symbolReferenced the name of the identifier
	 * @param parent the Factor that will reference the identifier
	 * @param root the active processor root
	 */
	public static void processIdentifier
		(String symbolReferenced, Factor parent, SeriesExpansion <?> root)
	{
		Factor ref = root.referencesFormalParameter (symbolReferenced) ?
			reduceSingle ( root.getActualParameter () ) :
			new Variable (symbolReferenced);
		add (ref, parent);
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
			case Identifier:	processIdentifier
									(member ("Name", node), parent, root);			break;
			case BinaryOP:		add (translateOperation (node, root), parent);		break;
			case UnaryOP:		add (translateInvocation (node, root), parent);		break;
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
		parent = root.expandSymbol (member ("OpName", object), parameter, root);
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


	// organization of terms to provide representation of subtraction
	
	/**
	 * present negative terms with subtraction
	 * @param root the root element of the tree
	 * @return reordered Element tree
	 */
	public static Factor organizeTerms
			(Factor root)
	{
		if (root instanceof Sum)
		{ return organizeTerms ( (Sum) root ); }
		else return root;
	}
	public static Factor organizeTerms (Sum root)
	{
		Sum positive = new Sum (), negative = new Sum ();
		for (Factor factor : root)
		{
			if (factor instanceof Product)
			{ processFactor ( (Product) factor, positive, negative ); }
			else positive.add (factor);
		}
		return reorderedAsDifference (positive, negative);
	}
	public static void processFactor
		(Product product, Sum positive, Sum negative)
	{
		boolean dup = false;
		Sum choice = positive;
		Product factors = product;
		Factor first = product.get (0);

		if (first instanceof Constant)
		{
			Constant C = (Constant) first;
			double value = - C.getValue ();

			if (value > 0.0)
			{
				choice = negative; factors = new Product ();
				if (value != 1) factors.add (new Constant (value));
				dup = true;
			}
		}
		else if (first instanceof Sum)
		{
			factors = new Product ();
			factors.add (organizeTerms (first));
			dup = true;
		}

		if (dup)
		{
			for (int i = 1; i < product.size (); i++)
			{ factors.add (product.get (i)); }
		}

		choice.add (factors);
	}
	public static Factor reorderedAsDifference
			(Sum positive, Sum negative)
	{
		if (negative.size () == 0) return positive;
		Difference result = new Difference ();
		result.add (positive);
		result.add (negative);
		return result;
	}


}

