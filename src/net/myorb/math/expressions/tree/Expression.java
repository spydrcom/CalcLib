
package net.myorb.math.expressions.tree;

import net.myorb.math.expressions.SymbolMap;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

/**
 * expression is same as SubExpression except parent is null
 * @param <T> value type of expressions
 * @author Michael Druckman
 */
public class Expression<T> extends SubExpression<T>
{

	/**
	 * parent is null, root is THIS
	 */
	public Expression () { super (null); root = this; }

	/**
	 * maps of identifiers and operators
	 */
	protected Map<String,LexicalAnalysis.Identifier<T>> identifiers = new HashMap<> ();
	protected Map<String,LexicalAnalysis.Operator> operators = new HashMap<> ();

	/**
	 * components list provides initial inspection of sub-expressions
	 * @return list of sub-expressions in this expression
	 */
	public List<SubExpression<T>> getComponents () { return components; }
	protected List<SubExpression<T>> invocations = new ArrayList<> ();
	protected List<SubExpression<T>> components = new ArrayList<> ();
	protected List<Element> descriptors = new ArrayList<> ();

	/**
	 * copy context to another expression
	 * @param expression destination of copies
	 */
	public void duplicateContext (Expression<T> expression)
	{
		expression.components = components;
		expression.identifiers = identifiers;
		expression.invocations = invocations;
		expression.operators = operators;
	}

	/**
	 * @return hash of symbols with configuration parameters
	 */
	public Map<String,Map<String,String>> describeImports ()
	{
		Map<String,Map<String,String>> hash = null;
		if (imports.size () != 0) hash = hashOfImports ();
		return hash;
	}
	public Map<String,Map<String,String>> hashOfImports ()
	{
		Map<String,Map<String,String>> hash =
				new HashMap<String,Map<String,String>>();
		for (String name : imports.keySet ())
		{
			SymbolMap.Named sym = imports.get (name);
			SymbolMap.ConfiguredImport parms = (SymbolMap.ConfiguredImport) sym;
			hash.put (name, describeImport (parms.getConfiguration ()));
		}
		return hash;
	}
	public Map<String,String> describeImport (Map<String,Object> config)
	{
		Map<String,String> items = new HashMap<String,String>();
		for (String item : config.keySet ()) { items.put (item, config.get (item).toString ()); }
		return items;
	}
	protected Map<String,SymbolMap.Named> imports = new HashMap<> ();

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.SubExpression#toString()
	 */
	public String toString ()
	{
		StringBuffer buf;
		addElements (buf = new StringBuffer ());
		return buf.toString ();
	}

	private static final long serialVersionUID = -3813116162860748886L;
}


/**
 * show forced precedence in binary operations
 * @param <T> data type
 */
class FencedExpression<T> extends Expression<T>
{
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.SubExpression#toString()
	 */
	public String toString ()
	{
		StringBuffer buf = new StringBuffer (" ( ");
		addElements (buf); buf.append (" ) ");
		return buf.toString ();
	}
	private static final long serialVersionUID = -2586797526521732127L;
}

