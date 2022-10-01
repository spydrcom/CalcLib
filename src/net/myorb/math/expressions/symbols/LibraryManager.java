
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.OperatorNomenclature;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.FunctionDefinition;

import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;

import net.myorb.data.abstractions.CommonCommandParser.TokenDescriptor;
import net.myorb.data.abstractions.ConfigurationParser;
import net.myorb.data.abstractions.Configurable;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * processing of library definition statements
 *  and the processing, configuration, and instance management
 *  of all symbols associated with the library objects defined.
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class LibraryManager<T>
{


	/**
	 * function definition processing as required by the environment
	 * @param environment the environment object driving the processing
	 * @param definitions a management object for function symbols
	 */
	public LibraryManager (Environment<T> environment, FunctionDefinition<T> definitions)
	{
		this.environment = environment;
		this.definitions = definitions;
	}
	protected FunctionDefinition<T> definitions;
	protected Environment<T> environment;


	/**
	 * import polynomial power functions
	 * @param tokens the token list being processed
	 */
	public void importFamily (List<TokenParser.TokenDescriptor> tokens)
	{
		String
			kind = null, count = null,
			name = tokens.get (1).getTokenImage (),
			second = tokens.get (2).getTokenImage ();
		boolean lambda = false;
		int tokenCount;

		// check for lambda array request

		if (OperatorNomenclature.isIndexReference (second))
		{
			lambda = true;
			tokens.remove (2);
			count = tokens.get (2).getTokenImage ();
		} else count = second;

		// process information about the kind requested

		if ((tokenCount = tokens.size ()) > 3)
		{
			kind = tokens.get (3).getTokenImage ();
			for (int i=4; i<tokenCount; i++)
			{
				kind += tokens.get (i).getTokenImage ();
			}
		}

		// pass request to Family Manager

		SpecialFunctionFamilyManager.importFamilyFunctions
		(
			name, kind, Integer.parseInt (count), lambda, environment
		);
	}


	/**
	 * create a symbol entry for a library
	 * @param libraryName symbol name to be assigned to library
	 * @param classPath full path to object to be treated as library
	 * @param environment the environment object driving the processing
	 * @param <T> type on which operations are to be executed
	 */
	public static <T> void defineLibrary
	(String libraryName, String classPath, Environment<T> environment)
	{
		LibraryObject<T> library;

		try
		{
			Map<String,Method> methods = getMethodMap (Class.forName (classPath));
			library = new LibraryObject<T> (classPath, libraryName, methods);
			library.setEnvironment (environment);
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Requested library not available");
		}

		environment.getSymbolMap ().add (library);
	}


	/**
	 * reset all configured parameters of a library object
	 * @param libraryName the name of the library symbol
	 */
	public void resetLibrary (String libraryName)
	{
		LibraryObject<T> lib = getLib (libraryName);
		lib.getParameterization ().clear ();
	}


	/**
	 * process a library command
	 * @param tokens the tokens of the command string
	 */
	public void processLibrary (List<TokenParser.TokenDescriptor> tokens)
	{
		String libraryName, parameter;

		tokens.remove (0); libraryName = definitions.processName (tokens.get (0));
		tokens.remove (0); parameter = TokenParser.toString (tokens).replace (" ", "");

		if (parameter.toUpperCase ().equals ("RESET"))
		{
			resetLibrary (libraryName);
		}
		else
		{
			defineLibrary (libraryName, parameter, environment);
		}
	}


	/**
	 * @param c class to be mapped
	 * @return a Map of methods from name
	 */
	public static Map<String,Method> getMethodMap (Class<?> c)
	{
		Map<String,Method> methods = new HashMap<String,Method> ();
		for (Method m : c.getMethods ()) { methods.put (m.getName (), m); }
		return methods;
	}


	/**
	 * supply configuration parameters for library
	 * @param tokens the tokens of the configuration string
	 */
	public void configureLibrary (List<TokenParser.TokenDescriptor> tokens)
	{
		String name; Map<String, Object> parameters; tokens.remove (0);
		LibraryObject<T> lib = getLib (name = tokens.remove (0).getTokenImage ());
		if (lib == null) throw new RuntimeException ("Unrecognized library: " + name);
		ConfigurationParser.process (tokens, getProcessor (parameters = lib.getParameterization ()));
		System.out.println ("Lib " + name + " config " + parameters);
	}
	ConfigurationParser.Interpreter getProcessor (Map<String, Object> parameters)
	{
		return new ConfigurationInterpreter (parameters, environment);
	}
	LibraryObject<T> getLib (String name)
	{
		@SuppressWarnings("unchecked")
		LibraryObject <T> lib = ( LibraryObject <T> ) environment
			.getSymbolMap ().get (name);
		return lib;
	}


	/**
	 * supply configuration parameters for library
	 * @param tokens the tokens of the configuration string
	 */
	public void instanceSymbol (List<TokenParser.TokenDescriptor> tokens)
	{
		String lib;
		String newSymbol = tokens.get (1).getTokenImage ();
		String next = tokens.get (2).getTokenImage ();

		if (next.equals ("'"))
		{
			newSymbol += "'";
			next = tokens.get (3).getTokenImage ();
		}

		if (!next.equals ("'")) lib = next;
		else
		{
			newSymbol += "'";
			lib = tokens.get (4).getTokenImage ();
		}

		LibraryObject.newInstance
		(newSymbol, lib, environment);
	}


	/**
	 * initialize an imported object
	 * @param tokens the tokens of the configuration string
	 */
	public void initObject (List<TokenParser.TokenDescriptor> tokens)
	{
		String path = null;
		Object object = null;
		Map<String, Object> parameters;

		parameters = new HashMap<String, Object> ();

		try
		{
			tokens.remove (0);
			path = ConfigurationParser.strip
				(tokens.remove (0).getTokenImage ());
			object = Class.forName (path).newInstance ();
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Unable to construct " + path, e);
		}

		if (object instanceof Configurable)
		{
			ConfigurationParser.configure (tokens, parameters);
			((Configurable) object).addConfiguration (parameters);
		}

		Environment.provideAccess (object, environment);
		System.out.println ("Init " + path + " config " + parameters);
	}


}


/**
 * a processor for Configuration parameters allowing many formats
 */
class ConfigurationInterpreter implements ConfigurationParser.Interpreter
{

	ConfigurationInterpreter (Map <String, Object> parameters, Environment<?> environment)
	{
		this.symbols = environment.getSymbolMap ();
		this.parameters = parameters;
	}
	Map <String, Object> parameters;
	SymbolMap symbols;

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
	Object lookup (String ValueIdentifier)
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

