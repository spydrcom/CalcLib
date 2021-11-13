
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.SymbolMap.SymbolType;

/**
 * boiler-plate class providing for anonymous construction of Variable lookup objects.
 *  built-in flavor allows for recognition of variable as not needing import/export
 * @author Michael Druckman
 */
public abstract class AbstractBuiltinVariableLookup extends AbstractVariableLookup
{

	/**
	 * construct based on variable name
	 * @param name the name of the variable or other symbol
	 */
	public AbstractBuiltinVariableLookup (String name) { super (name); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getSymbolType()
	 */
	public SymbolType getSymbolType () { return SymbolType.ASSIGNMENT; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.AbstractVariableLookup#rename(java.lang.String)
	 */
	public void rename (String to) {}

}
