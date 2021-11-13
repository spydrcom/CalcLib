
package net.myorb.math.expressions.gui.rendering;

import net.myorb.math.expressions.symbols.UndefinedFunctionSpecification;
//import net.myorb.math.expressions.symbols.AssignedVariableStorage;

import net.myorb.math.expressions.SymbolMap;

/**
 * processing of symbol references using symbol map object
 * @author Michael Druckman
 */
public class SymbolProcessing extends SubExpressionProcessing
{


	/**
	 * context for conversion is a symbol map
	 * @param s the symbol map object that will be used for context
	 */
	public void setSymbolMap (SymbolMap s) { this.s = s; }
	private SymbolMap s;


	/*
	 * symbol recognition
	 */


	/**
	 * convert symbol to operation descriptor
	 * @param namedSymbol the symbol record for the operation
	 * @return the operation object cast from symbol
	 */
	public SymbolMap.Operation identifyOperation (SymbolMap.Named namedSymbol)
	{
		//TODO: AssignedVariableStorage
//		if (namedSymbol instanceof AssignedVariableStorage)
//		{
//			return (SymbolMap.Operation) s.lookup ("#");
//		}
		return (SymbolMap.Operation) namedSymbol;
	}


	/**
	 * identify operation and establish precedence
	 * @param namedSymbol the symbol record for the operation
	 * @return the operation object cast from symbol
	 */
	public SymbolMap.Operation establishOperation (SymbolMap.Named namedSymbol)
	{
		SymbolMap.Operation namedOperation = identifyOperation (namedSymbol);
		setPrecedenceTo (namedOperation);
		return namedOperation;
	}


	/**
	 * get symbol object for given name
	 * @param name the name of the object to be found
	 * @return the symbol object for name
	 */
	public SymbolMap.Named lookupNamedSymbol (String name)
	{
		return (SymbolMap.Named) s.get (name);
	}


	/**
	 * locate symbol by name
	 * @param name the symbol name sought
	 * @return TRUE = found symbol is operation type
	 */
	public boolean isIdentifiedAsOperation (String name)
	{
		return lookupNamedSymbol (name) instanceof SymbolMap.Operation;
	}


	/**
	 * lookup and identify operation in one call
	 * @param name the name of the object to be found
	 * @return the operation object cast from symbol found from name
	 */
	public SymbolMap.Operation lookupOperation (String name)
	{
		return identifyOperation (lookupNamedSymbol (name));
	}


	/**
	 * perform lookup for lastOp
	 * @return the operation object found for lastOp
	 */
	public SymbolMap.Operation lookupLastOperation ()
	{
		return lookupOperation (getLastOp ());
	}


	/*
	 * symbol definitions
	 */


	/**
	 * generate UndefinedFunctionSpecification
	 * @param name symbol to be defined
	 * @return the new symbol object
	 */
	public SymbolMap.Operation postUndefinedFunction (String name)
	{
		SymbolMap.Operation symbol =
			new UndefinedFunctionSpecification (name);
		s.add (symbol);
		return symbol;
	}


	/**
	 * check for symbol specification.
	 *  construct dummy symbol table entry if not present
	 * @param name the name of the symbol
	 * @return the table entry
	 */
	public SymbolMap.Named forceSpecification (String name)
	{
		SymbolMap.Named sym;
		if ((sym = lookupNamedSymbol (name)) == null)
		{ sym = postUndefinedFunction (name); }
		return sym;
	}


	/**
	 * build descriptor for range
	 * @param seq the token stream with marked segment that parser identified as range description
	 */
	public void processRangeDescription (TokenSequence seq)
	{
		setRangeDescription (new RangeDescription (seq.getMarked (), s));
	}


}

