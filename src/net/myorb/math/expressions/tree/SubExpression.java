
package net.myorb.math.expressions.tree;

import net.myorb.data.notations.json.JsonSemantics;
import net.myorb.data.abstractions.SimpleUtilities;

import java.util.ArrayList;

/**
 * expression is same as SubExpression except parent is null
 * @param <T> value type of expressions
 * @author Michael Druckman
 */
public class SubExpression<T> extends ArrayList<Element>
	implements Element, Valued<T>, JsonBinding.JsonRepresentation<T>
{

	public SubExpression (SubExpression<T> parent)
	{
		this.parent = parent;
		if (parent != null) this.root = parent.root;
		this.isAnAggregateNode = false;
	}
	public SubExpression (Element element) { this (null); add (element); }
	public SubExpression (SubExpression<T> parent, SubExpression<T> child)
	{ this (parent); parent.add (this); }

	public SubExpression<T> getParent () { return parent; }
	public Expression<T> getRoot () { return root; }
	protected SubExpression<T> parent = null;
	protected Expression<T> root = null;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.LexicalAnalysis.Valued#getValue()
	 */
	public T getValue () { return value; }
	public void setValue (T value) { this.value = value; }
	protected T value;

	/**
	 * cast Object to SubExpression when appropriate
	 * @param from source Object for the cast
	 * @return Object cast to SubExpression
	 * @param <T> data type
	 */
	public static <T> SubExpression<T> cast (Object from)
	{
		@SuppressWarnings ("unchecked") SubExpression<T>
		s = SimpleUtilities.verifyClass (from, SubExpression.class);
		return s;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#toString()
	 */
	public String toString ()
	{
		StringBuffer buf;
		addElements (buf = new StringBuffer ());
		if (isInAggregateNode ()) return buf.toString ();
		else return toFencedString (buf.toString ());
	}
	public String toFencedString (String content)
	{
		return new StringBuffer ().append ("{ ").append (content).append (" }").toString ();
	}
	public void addElements (StringBuffer buf)
	{
		for (Element e : this) buf.append (e.toString ()).append (" ");
	}

	/**
	 * determine if this is an aggregate value node
	 * @return TRUE = this is child of aggregate
	 */
	public boolean isInAggregateNode ()
	{ return parent != null && parent.isAggregateValueRoot (); }
	public boolean isAggregateValueRoot () { return isAnAggregateNode; }
	protected boolean isAnAggregateNode;

	/**
	 * node is changed to aggregate
	 */
	public void changeToAggregate ()
	{
		moveContentsToChild ().addThisToComponentsList ();
		isAnAggregateNode = true;
	}

	/**
	 * reorganize node to have child node holding contents
	 * @return child of parent holding content
	 */
	public SubExpression<T> moveContentsToChild ()
	{
		SubExpression<T> child = new SubExpression<T> (this);
		child.addAll (this); replaceContents (child);
		return child;
	}

	/**
	 * replace parent contents with new child
	 * @param child the child to be put into parent
	 */
	public void replaceContents (Element child) { clear (); add (child); }

	/**
	 * add this node to components list for expression
	 */
	public void addThisToComponentsList () { root.components.add (this); }

	/**
	 * replace invocation id/parameter with descriptor element
	 * @param identifierNode identifier node index of invocation
	 * @param parameterNode parameter node index of invocation
	 * @param withDescriptor new descriptor element
	 */
	public void substituteInvocationNodes
	(int identifierNode, int parameterNode, Element withDescriptor)
	{ set (identifierNode, withDescriptor); remove (parameterNode); }

	/**
	 * add new child to parent node
	 * @return the new child
	 */
	public SubExpression<T> spawn ()
	{
		SubExpression<T> child;
		add (child = new SubExpression<T> (this));
		return child;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#getJson()
	 */
	public JsonSemantics.JsonValue toJson ()
	{
		if (this.size() == 0) return JsonSemantics.getNull ();
		return JsonBinding.toJson (this.get (0));
	}
	public Element fromJson (JsonSemantics.JsonValue context, JsonRestore<T> restoreManager) throws Exception { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.Element#getElementType()
	 */
	public Types getElementType () { return Types.SUBEXPRESSION; }
	public boolean isOfType (Types type) { return type == Types.SUBEXPRESSION; }

	private static final long serialVersionUID = 4631091369067707554L;

}

