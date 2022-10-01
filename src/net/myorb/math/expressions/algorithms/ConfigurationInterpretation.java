
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.data.abstractions.CommonCommandParser.TokenDescriptor;
import net.myorb.data.abstractions.ConfigurationParser;

import java.util.Map;

/**
 * a processor for Configuration parameters allowing many formats
 * @author Michael Druckman
 */
public class ConfigurationInterpretation implements ConfigurationParser.Interpreter
{


	public ConfigurationInterpretation
	(Map <String, Object> parameters, Environment<?> environment)
	{
		this.symbols = environment.getSymbolMap ();
		this.parameters = parameters;
	}
	protected Map <String, Object> parameters;
	protected SymbolMap symbols;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ConfigurationParser.Interpreter#process(java.lang.String, net.myorb.data.abstractions.CommonCommandParser.TokenDescriptor)
	 */
	public void process (String symbol, TokenDescriptor token)
	{
		switch (token.getTokenType ())
		{
			case IDN:
				// identifier to be found in symbol table
				parameters.put (symbol, lookup (token.getTokenImage ()));
				break;
			case QOT:
				// simple string as parameter value
				ConfigurationParser.addParameterValue (parameters, token, symbol);
				break;
			case FLT:
				// a float value
				parameters.put (symbol, Double.parseDouble (token.getTokenImage ()));
				break;
			case DEC:
				// a decimal value
				parameters.put (symbol, new java.math.BigDecimal (token.getTokenImage ()));
				break;
			case INT:
				// an integer value
				parameters.put (symbol, Integer.parseInt (token.getTokenImage ()));
				break;
			default:
				// not a legal value association
				throw new RuntimeException ("Expected configuration: " + token.getTokenImage ());
		}
	}


	/**
	 * get value as NamedConstant in symbol table
	 * @param ValueIdentifier the name of the constant
	 * @return the object holding the value
	 */
	private Object lookup (String ValueIdentifier)
	{
		SymbolMap.Named sym;
		if ((sym = symbols.lookup (ValueIdentifier)) == null)
		{
			throw new RuntimeException ("Unrecognized symbol: " + ValueIdentifier);
		}
		else if ( ! (sym instanceof SymbolMap.NamedConstant) )
		{
			throw new RuntimeException ("Illegal symbol: " + ValueIdentifier);
		}
		return ( (SymbolMap.NamedConstant) sym ).getValue ();
	}


}

