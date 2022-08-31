
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.math.Function;

import java.util.List;

/**
 * symbol descriptor for a User-Defined-Function
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class DefinedFunction <T> extends AbstractFunction <T>
{


	/**
	 * create a symbol table
	 *  entry for a function definition
	 * @param name the name of the function to be created
	 * @param parameterNames the names of the parameters defined for the function
	 * @param functionTokens the token stream that defines the function behavior
	 */
	public DefinedFunction
		(
			String name, List <String> parameterNames,
			TokenParser.TokenSequence functionTokens
		)
	{
		super (name, parameterNames, functionTokens);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ParameterizedFunction#execute(java.util.List)
	 */
	public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
	{
		if (parameters != null)
		{
			if (parameters instanceof ValueManager.ValueList)
			{ copyParameters ((ValueManager.ValueList)parameters); }
			else copyParameters (valueManager.toArray (parameters));
		}

		if (useExpressionTree) return processExpression ();

		if (traceFlow)
		{ constructEngine ().processWithCatch (functionTokens); }
		else constructEngine ().process (functionTokens);
		return topOfStack ();
	}
	boolean traceFlow = false;


	/**
	 * verify function is user defined
	 * @param <T> type of data processed in function
	 * @param functionSymbol the symbol found in symbol table, null if not found
	 * @return the function on declared type
	 */
	public static <T> AbstractFunction <T> verifyAbstractFunction
			(SymbolMap.Named functionSymbol)
	{
		if (functionSymbol == null)
		{ throw new RuntimeException ("Function not found"); }
		return udfCheck (AbstractFunction.cast (functionSymbol));
	}


	/**
	 * verify function is user defined
	 * @param <T> type of data processed in function
	 * @param functionSymbol the symbol found in symbol table, null if not found
	 * @return the function on declared type
	 */
	public static <T> Subroutine <T> verifySubroutine
			(SymbolMap.Named functionSymbol)
	{
		AbstractFunction<T>
			function = verifyAbstractFunction (functionSymbol);
		return udfCheck (Subroutine.cast (function));
	}


	/**
	 * verify symbol as subroutine and wrap as simple function
	 * @param functionSymbol the symbol found in symbol table, null if not found
	 * @param <T> type of data processed in function
	 * @return the function on declared type
	 */
	public static <T> Function <T> verifyFunction
		(SymbolMap.Named functionSymbol)
	{
		Subroutine<T>
			subroutine = verifySubroutine (functionSymbol);
		return subroutine.toSimpleFunction ();
	}


	/**
	 * ensure symbol is a User-Defined-Function
	 * @param functionSymbol the symbol found in symbol table, null if not found
	 * @param <T> type of data processed in function
	 * @return the function on declared type
	 */
	public static <T> Function <T> verifyDefinedFunction
			(SymbolMap.Named functionSymbol)
	{
		AbstractFunction<T>
			function = verifyAbstractFunction (functionSymbol);
		if (functionSymbol instanceof Subroutine) return verifyFunction (function);
		else throw new RuntimeException (UDF_ERROR);
	}


	/**
	 * post a UDF to symbol table
	 * @param name the name of the function
	 * @param parameterNames the list of function parameters
	 * @param functionTokens the token sequence making the function
	 * @param spaceManager a space manager for the data type
	 * @param symbols a symbol map for symbol resolution
	 * @return the newly defined function
	 * @param <T> data type used
	 */
	public static <T> DefinedFunction <T> defineUserFunction
		(
			String name,
			List<String> parameterNames,
			TokenParser.TokenSequence functionTokens,
			ExpressionSpaceManager<T> spaceManager,
			SymbolMap symbols
		)
	{
		DefinedFunction<T> f = new DefinedFunction<T>
			(name, parameterNames, functionTokens);
		f.setSpaceManager (spaceManager);
		f.setSymbolTable (symbols);
		symbols.add (f);
		return f;
	}


	/**
	 * verify symbol is a User-Defined-Function
	 * @param symbol the symbol object being verified
	 * @return the symbol that was passed into the verification
	 * @throws RuntimeException for symbols failing verification
	 * @param <Symbol> the symbol type to verify
	 */
	public static <Symbol>
		Symbol udfCheck (Symbol symbol)
	throws RuntimeException
	{
		if (symbol == null)
		{ throw new RuntimeException (UDF_ERROR); }
		return symbol;
	}
	public static final String UDF_ERROR =
	"Selected symbol is not a user defined function";


}

