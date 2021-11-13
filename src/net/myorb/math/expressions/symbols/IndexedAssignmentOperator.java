
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.SymbolMap.SymbolType;

/**
 * an operator object that identifies the holding table for the assignment...
 *  plus the value of the index to be used for the assignment to a specific element
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class IndexedAssignmentOperator<T> extends OperationObject implements SymbolMap.IndexedVariableAssignment
{

	/**
	 * construct based on name and storage table
	 * @param name the name of the variable or other symbol
	 * @param indexValues the list of values to be used to identify the element
	 * @param symbolMap the map that will store this value
	 */
	public IndexedAssignmentOperator
	(String name, ValueManager.GenericValue indexValues, SymbolMap symbolMap)
	{
		super (name, SymbolMap.STORAGE_PRECEDENCE);
		this.indexValues = indexValues;
		this.symbolMap = symbolMap;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getSymbolType()
	 */
	public SymbolType getSymbolType () { return SymbolType.ASSIGNMENT; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.IndexedVariableAssignment#getIndexValues()
	 */
	public ValueManager.GenericValue getIndexValues ()
	{
		return indexValues;
	}
	ValueManager.GenericValue indexValues;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.IndexedVariableAssignment#getSymbolMap()
	 */
	public SymbolMap getSymbolMap () { return symbolMap; }
	SymbolMap symbolMap;

}
