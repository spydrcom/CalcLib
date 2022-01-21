
package net.myorb.math.expressions.tree;

/**
 * the atomic node type
 * @author Michael Druckman
 */
public interface Element
{

	/**
	 * the types of elements in expression trees
	 */
	public enum Types
	{
		LITERAL,									// multiple sub-classes, all with common treatment
		CONSTANT, TEXT,								// sub-types of literal values for compile time constants and strings
		AGGREGATE, IDENTIFIER,						// aggregate is carried as value list until recognized as numeric vector
		OPERATOR, INVOCATION, BINARY_OPERATION,		// represented as structures with properties making tree nodes
		RANGE_DESCRIPTOR, CALCULUS_DESCRIPTOR,		// represented as structures with sub-tree nodes
		SUBEXPRESSION								// special case, wrapper for single node
	}

	/**
	 * @param type the type in question
	 * @return TRUE = type matches
	 */
	boolean isOfType (Types type);

	/**
	 * @return the type of the element, never null
	 */
	Types getElementType ();

}
