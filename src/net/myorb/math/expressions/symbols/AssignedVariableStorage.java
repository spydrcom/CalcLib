
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.SymbolMap.SymbolType;

/**
 * symbol table entry holding value assigned to named variable
 * @author Michael Druckman
 */
public class AssignedVariableStorage extends AbstractVariableLookup
{

	/**
	 * assignment provides linkage between name and value
	 * @param name the name of the object to use as storage
	 * @param value the value to be assigned
	 */
	public AssignedVariableStorage (String name, ValueManager.GenericValue value)
	{ super (name); this.value = value; if (value != null) this.value.setName (name); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.VariableLookup#getValue()
	 */
	public ValueManager.GenericValue getValue () { return value; }
	ValueManager.GenericValue value;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getSymbolType()
	 */
	public SymbolType getSymbolType () { return SymbolType.ASSIGNMENT; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return value.toString (); }

}
