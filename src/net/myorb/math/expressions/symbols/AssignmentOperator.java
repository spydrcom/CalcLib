
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.SymbolMap.SymbolType;

/**
 * an operator object that identifies the holding table for the assignment
 * @author Michael Druckman
 */
public class AssignmentOperator extends OperationObject implements SymbolMap.VariableAssignment
{

	/**
	 * construct based on name and storage table
	 * @param name the name of the variable or other symbol
	 * @param symbolMap the map that will store this value
	 */
	public AssignmentOperator (String name, SymbolMap symbolMap)
	{
		super (name, SymbolMap.STORAGE_PRECEDENCE);
		this.symbolMap = symbolMap;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getSymbolType()
	 */
	public SymbolType getSymbolType () { return SymbolType.ASSIGNMENT; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.VariableAssignment#getSymbolMap()
	 */
	public SymbolMap getSymbolMap () { return symbolMap; }
	SymbolMap symbolMap;

}
