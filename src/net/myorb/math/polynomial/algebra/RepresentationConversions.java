
package net.myorb.math.polynomial.algebra;

import net.myorb.math.computational.ArithmeticFundamentals;

import net.myorb.data.notations.json.*;

/**
 * conversions between representations of polynomials
 * @author Michael Druckman
 */
public class RepresentationConversions extends Utilities
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
		Elements.Sum equation; Factor elementTree;
		recognize (tree, equation = new Equation (root.converter), root);
		elementTree = Manipulations.reduceAndCollectTerms
		(
			reduceTermsOf (equation),
			root.getPolynomialVariable (),
			root
		);
		if (SHOW) trace ( tree, elementTree );
		return elementTree;
	}
	static void trace (JsonLowLevel.JsonValue tree, Factor elementTree)
	{
		try { JsonPrettyPrinter.sendTo (tree, System.out); }
		catch (Exception e) { e.printStackTrace (); }
		System.out.println (elementTree);
	}
	protected static boolean SHOW = false;

	/**
	 * apply summation algorithm to a set of terms
	 * @param equation the equation being analyzed
	 * @return modified sum
	 */
	public static Elements.Sum reduceTermsOf (Elements.Sum equation)
	{
		Elements.Sum reduced = new Elements.Sum (equation.converter);
		for (Factor factor : equation)
		{
			reduced = (Elements.Sum) Operations.sumOf
				(reduced, reducedTerm (factor));
		}
		return reduced;
	}

	/**
	 * process product found in term
	 * @param term the term being analyzed
	 * @return modified term
	 */
	public static Factor reducedTerm ( Factor term )
	{
		if (term instanceof Elements.Product)
		{ return reducedProductsFrom ( (Elements.Product) term ); }
		else return term;
	}

	/**
	 * check for singleton factor in product
	 * @param factors product factors being processed
	 * @return modified product
	 */
	public static Factor reducedProductsFrom ( Elements.Product factors )
	{
		Factor result;
		if ( ( result = getSingleChild (factors) ) == null )
		{ result = reducedProductFrom (factors); }
		return result;
	}

	/**
	 * apply product algorithm to a set of factors
	 * @param factors product factors being processed
	 * @return modified product
	 */
	public static Factor reducedProductFrom ( Elements.Product factors )
	{
		int n = factors.size ();
		Factor product = Operations.productOf (factors.get (0), factors.get (1));
		for (int i = 2; i < n; i++) product = Operations.productOf (product, factors.get (i));
		return product;
	}


	// algorithms applied by node type


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
			case Value: 		add (parse (node.toString (), root), parent);		break;
			case BinaryOP:		add (translateOperation (node, root), parent);		break;
			case UnaryOP:		add (translateInvocation (node, root), parent);		break;
			default:			throw new RuntimeException ("Unrecognized node");
		}
	}
	static String member (String called, JsonLowLevel.JsonValue in)
	{ return ( (JsonSemantics.JsonObject) in ).getMemberString (called); }
	static Constant parse (String text, SeriesExpansion <?> root)
	{ return root.getConstantFromNodeImage (text); }


	/**
	 * process an identifier reference
	 * @param symbolReferenced the name of the identifier
	 * @param parent the Factor that will reference the identifier
	 * @param root the active processor root
	 */
	public static void processIdentifier
		(String symbolReferenced, Factor parent, SeriesExpansion <?> root)
	{
		Factor ref = ! root.referencesFormalParameter (symbolReferenced) ?
			new Variable (root.converter, symbolReferenced) :
			reduceSingle ( root.getActualParameter () );
		add (ref, parent);
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
		root.prepareParameterSubstitution (null);
		ArithmeticFundamentals.Conversions <?> converter = root.converter;
		JsonSemantics.JsonObject object = (JsonSemantics.JsonObject) node;
		Factor parent = new Sum (converter), parameter = new Sum (converter);
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
		if ( isNumeric (node) ) return NodeTypes.Value;
		else return NodeTypes.valueOf ( member ("NodeType", node) );
	}
	static boolean isNumeric (JsonLowLevel.JsonValue node)
	{
		return node.getJsonValueType () == JsonLowLevel.JsonValue.ValueTypes.NUMERIC;
	}


	/**
	 * translation of expression tree node to element node
	 * @param node the JSON node to translate into elements
	 * @param root expansion tree root for the series
	 * @return the translated element factor
	 */
	public static Factor translateOperation
	(JsonLowLevel.JsonValue node, SeriesExpansion <?> root)
	{
		JsonSemantics.JsonObject object =
			(JsonSemantics.JsonObject) node;
		Factor parent = identifyOperation (object, root);
		recognize ( object.getMemberCalled ("Left"), parent, root );
		recognize ( object.getMemberCalled ("Right"), rightParentFor (parent), root );
		return parent;
	}


	/**
	 * process right side of Difference
	 * @param parent object to analyze for processing
	 * @return negated product if parent is Difference otherwise just parent
	 */
	public static Factor rightParentFor (Factor parent)
	{
		return parent instanceof Difference ? subtractionChild (parent) : parent;
	}


	/**
	 * build product with negation scalar
	 * @param parent the parent Difference node indicating Subtraction
	 * @return the new Product element
	 */
	public static Factor subtractionChild (Factor parent)
	{
		ArithmeticFundamentals.Conversions <?> converter = parent.getConverter ();
		Constant negativeOne = new Constant (converter, converter.getNegOne ());
		Product newChild = new Product (converter, negativeOne);
		add (newChild, parent);
		return newChild;
	}


	/**
	 * identify operation specified in node
	 * @param node the node to be verified and analyzed
	 * @param root the expansion tree root for the series
	 * @return the object appropriate to specified operation
	 */
	public static Factor identifyOperation
	(JsonSemantics.JsonObject node, SeriesExpansion <?> root)
	{
		switch ( member ("OpName", node).charAt (0) )
		{
			case '+': return new Sum (root.converter);
			case '-': return new Difference (root.converter);
			case '*': return new Product (root.converter);
			case '^': return new Power (root.converter);
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


	/**
	 * process terms of an equation
	 * @param root the root descriptor for the equation
	 * @return an organized sum of terms of the equation
	 */
	public static Factor organizeTerms (Sum root)
	{
		Sum positive = new Sum (root.converter),
				negative = new Sum (root.converter);
		for (Factor factor : root)
		{
			if (factor instanceof Product)
			{ processFactor ( (Product) factor, positive, negative ); }
			else positive.add (factor);
		}
		return reorderedAsDifference (positive, negative);
	}


	/**
	 * determine sign of a product
	 * @param product the product to analyze
	 * @param positive the sum to append for positive products
	 * @param negative the sum to append for negative products
	 */
	public static void processFactor
		(Product product, Sum positive, Sum negative)
	{
		if ( ! product.isEmpty () )
		{
			Product factors = product, subtractionProduct;
			boolean dup = false; Sum choice = positive;
			Factor first = product.getFirstChild ();

			if ( first instanceof Constant )
			{
				if ( ( subtractionProduct = getSubtractionFactor ( first, product ) ) != null )
				{ factors = subtractionProduct; choice = negative; dup = true; }
			}
			else if ( first instanceof Sum )
			{
				factors = new Product (product.converter);
				factors.add ( organizeTerms (first) );
				dup = true;
			}

			if (dup) duplicate (1, product, factors);
			choice.add (factors);
		}
	}


	/**
	 * build product for factors containing negative scalars
	 * @param constant the scalar found in the product sign unknown
	 * @param product the original product found to have a scalar as first child
	 * @return a product for negative factors or null when scalar positive
	 */
	public static Product getSubtractionFactor
		(Factor constant, Product product)
	{
		Constant C; Product result = null;
		if ( ( C = Constant.negated (constant) ).getValue ().isPositive () )
		{ addConstant ( C, result = new Product (product.converter), product ); }
		return result;
	}


	/**
	 * apply constant scalar to product
	 * @param C the constant value of the scalar
	 * @param factors the product being built
	 * @param source the original product
	 */
	public static void addConstant (Constant C, Product factors, Product source)
	{
		if ( C.getValue ().isNot (1.0) || source.isSingleton () )
		{
			// scalar 1 would be redundant in multiple factor product
			// when the value of the product is constant ONE then value must be present
			factors.add (C);
		}
	}


	/**
	 * construct a sum with Negate flags on subtracted terms
	 * @param positive the terms seen as included with addition
	 * @param negative the terms seen as included with subtraction
	 * @return the new version of the sum
	 */
	public static Factor reorderedAsDifference
			(Sum positive, Sum negative)
	{
		Sum result = positive;
		if ( ! negative.isEmpty () )
		{
			add ( positive, result = new Sum (positive.converter) );
			for (Factor term : negative) negate (term, result);
		}
		return result;
	}


}

