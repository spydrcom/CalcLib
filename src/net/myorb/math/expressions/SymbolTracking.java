
package net.myorb.math.expressions;

import net.myorb.math.expressions.evaluationstates.Primitives;
import net.myorb.math.expressions.symbols.AbstractFunction;

import java.util.Set;
import java.util.Map;

/**
 * provide for tracking of symbol values
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class SymbolTracking<T>
{


	public SymbolTracking (Primitives <T> environment)
	{ this.environment = environment; }
	Primitives <T> environment;


	/**
	 * @param tokens the tokens of the function body
	 * @return the text of the definition
	 */
	public static String getFunctionBody
		(TokenParser.TokenSequence tokens)
	{ return TokenParser.toString (tokens); }
	public static <T> String getFunctionBody (AbstractFunction <T> f)
	{ return getFunctionBody (f.getFunctionTokens ()); }


	/**
	 * @param identifier an identifier to be found
	 * @return the function descriptor
	 */
	public AbstractFunction <T> findFunction (String identifier)
	{
		SymbolMap.Named symbol =
			environment.getSymbolMap ().lookup (identifier);
		return AbstractFunction.cast (symbol);
	}


	/**
	 * @param identifier name of variable
	 * @return value of variable
	 */
	public ValueManager.GenericValue findVariable (String identifier)
	{
		SymbolMap.Named symbol =
				environment.getSymbolMap ().lookup (identifier);
		if ( symbol == null || ! (symbol instanceof SymbolMap.VariableLookup) )
		{
			throw new RuntimeException (identifier + " is not a variable");
		}
		SymbolMap.VariableLookup v = (SymbolMap.VariableLookup) symbol;
		return v.getValue ();
	}


	/**
	 * @param identifier an identifier to be found
	 * @param defaultValue a default value for identifiers not found
	 * @param settings the map of names to values
	 */
	public void include
	(String identifier, String defaultValue, Map <String,String> settings)
	{
		if (defaultValue != null) settings.put (identifier, defaultValue);
		SymbolMap.Named symbol = environment.getSymbolMap ().lookup (identifier);	// general symbol table lookup, this is a simple Named entity

		if (symbol instanceof SymbolMap.VariableLookup)								// recognize identifier as variable holding a (constant) value
		{
			SymbolMap.VariableLookup v = (SymbolMap.VariableLookup) symbol;
			settings.put (identifier, v.getValue ().toString ());
			return;
		}

		AbstractFunction <T> f = AbstractFunction.cast (symbol);					// symbol refers to a defined function

		if (f != null)
		{
			settings.put (identifier, getFunctionBody (f));
			return;
		}

		if (symbol != null) { settings.put (identifier, "Built-In"); }
	}


	/**
	 * @param identifiers the identifiers to be found
	 * @param defaultValue a default value for identifiers not found
	 * @param settings the map of names to values
	 */
	public void include (Set <String> identifiers, String defaultValue, Map <String,String> settings)
	{
		for (String id : identifiers) include (id, defaultValue, settings);
	}


	/**
	 * @param settings the map of names to values
	 * @param defaultValue a default value for identifiers not found
	 */
	public void include (Map <String,String> settings, String defaultValue)
	{
		include (settings.keySet (), defaultValue, settings);
	}


}

