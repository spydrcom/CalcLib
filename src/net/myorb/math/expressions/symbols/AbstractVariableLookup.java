
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.SymbolMap;

/**
 * boiler-plate class providing for anonymous construction of Variable lookup objects
 * @author Michael Druckman
 */
public abstract class AbstractVariableLookup extends NamedObject
	implements SymbolMap.VariableLookup
{

	/**
	 * construct based on variable name
	 * @param name the name of the variable or other symbol
	 */
	public AbstractVariableLookup (String name) { super (name); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.VariableLookup#rename(java.lang.String)
	 */
	public void rename (String to) { name = to; }

}
