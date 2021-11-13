
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.SymbolMap;

/**
 * base class for symbols holding identifying name
 * @author Michael Druckman
 */
public abstract class NamedObject implements SymbolMap.Named
{

	/**
	 * symbols are identified by a name
	 * @param name the text of the name for this symbol
	 */
	public NamedObject (String name) { this.name = name; this.exposed = false; }

	/**
	 * built-in symbol has been requested for display
	 * @return TRUE = should be shown
	 */
	public boolean isExposed () { return this.exposed; }
	public void expose () { this.exposed = true; }
	protected boolean exposed;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#formatPretty()
	 */
	public String formatPretty () { return toString (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getName()
	 */
	public String getName () { return name; }
	protected String name;

}
