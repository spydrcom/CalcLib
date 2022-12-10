
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;

public class ClMathCommonSolutionProduct <T>
	implements ClMathBIF.FieldAccess, ValueManager.PortableValue <T>
{

	public ClMathCommonSolutionProduct () {}
	public ClMathCommonSolutionProduct (Object content) { this.content = content; }

	/**
	 * @param content the content to be wrapped in this solution
	 */
	public void setProductContent (Object content) { this.content = content; }

	/**
	 * @return the content wrapped in this solution
	 */
	public Object getProductContent () { return content; }
	protected Object content;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.ClMathBIF.FieldAccess#getFieldNamed(java.lang.String)
	 */
	public ValueManager.GenericValue getFieldNamed (String name) { return ClMathBIF.getField (name, content); }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return content.toString (); }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Portable.AsJson#toJson(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public JsonValue toJson (ExpressionSpaceManager <T> manager)
	{
		return ( ( ValueManager.PortableValue <T> ) content ).toJson (manager);
	}

}
