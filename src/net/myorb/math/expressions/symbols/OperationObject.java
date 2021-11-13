
package net.myorb.math.expressions.symbols;

import java.util.Map;

import net.myorb.math.expressions.SymbolMap;

/**
 * base class for operators holding name and precedence
 * @author Michael Druckman
 */
public abstract class OperationObject extends NamedObject
	implements SymbolMap.Operation, Parameterization
{

	/**
	 * construct based on
	 *  symbol name and operator precedence
	 * @param name the name of the variable or other symbol
	 * @param precedence the precedence to apply to this operator
	 */
	public OperationObject (String name, int precedence)
	{
		super (name); this.precedence = precedence;
	}

	/**
	 * return display text for function parameters
	 * @return a description of the parameter list
	 */
	public String displayParameters () { return ""; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.Parameterization#addParameterization(java.lang.String)
	 */
	public void addParameterization (String options) {}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.Parameterization#addParameterization(java.lang.String, java.lang.String)
	 */
	public void addParameterization (String symbol, String value) {}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.Parameterization#addParameterization(java.util.Map)
	 */
	public void addParameterization (Map<String,Object> options) {}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Operation#getPrecedence()
	 */
	public int getPrecedence () { return precedence; }
	protected int precedence;

}
