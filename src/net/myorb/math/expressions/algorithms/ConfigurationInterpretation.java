
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.EvaluationControlI;
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
	(Map <String, Object> parameters, Environment <?> environment)
	{
		this.symbols = environment.getSymbolMap ();
		this.parameters = parameters;
	}
	protected Map <String, Object> parameters;
	protected Environment <?> environment;
	protected SymbolMap symbols;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ConfigurationParser.Interpreter#process(java.lang.String, net.myorb.data.abstractions.CommonCommandParser.TokenDescriptor)
	 */
	public void process (String symbol, TokenDescriptor token)
	{
		Object value;

		switch (token.getTokenType ())
		{
			case IDN:
				// identifier to be found in symbol table
				value = lookup (token.getTokenImage ());
				break;
			case INT:
				// an integer value
				value = Integer.parseInt (token.getTokenImage ());
				break;
			case FLT:
				// a float value
				value = Double.parseDouble (token.getTokenImage ());
				break;
			case DEC:
				// a decimal value
				value = new java.math.BigDecimal (token.getTokenImage ());
				break;
			case QOT:
				// simple string as parameter value
				ConfigurationParser.addParameterValue (parameters, token, symbol);
				return;
			default:
				// not a legal value association
				throw new RuntimeException ("Expected configuration: " + token.getTokenImage ());
		}

		parameters.put (symbol, value);
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


	/**
	 * use evaluation control to get value for text
	 * @param value the text to evaluate as expression
	 * @return the resulting computed value 
	 * @param <T> type for operations
	 */
	private <T> T evaluate (String value)
	{
		@SuppressWarnings("unchecked") EvaluationControlI <T> control =
			(EvaluationControlI <T>) environment.getControl ();
		return control.evaluate (value.toString ());
	}


	/**
	 * @return a space manager from the environment
	 * @param <T> type for operations
	 */
	@SuppressWarnings("unchecked")
	private <T> ExpressionSpaceManager <T> getMgr ()
	{
		return (ExpressionSpaceManager <T>) environment.getSpaceManager ();
	}


	/**
	 * get value for symbol table entry
	 * @param name the name of the symbol table entry
	 * @return the value for the symbol
	 * @param <T> type for operations
	 */
	public <T> T getValueFor (String name)
	{
		Object found = lookup (name);
		if (found instanceof Number)
		{
			Number n = (Number) found;
			ExpressionSpaceManager <T> mgr = getMgr ();
			return mgr.convertFromDouble (n.doubleValue ());
		}
		return evaluate (found.toString ());
	}


	/**
	 * get value for symbol table entry as a Number
	 * @param name the name of the symbol table entry
	 * @return the value for the symbol converted to Number
	 */
	public Number getNumericValueFor (String name)
	{
		Object found = lookup (name);
		if (found instanceof Number) return (Number) found;
		return getMgr ().toNumber (evaluate (found.toString ()));

	}


}

