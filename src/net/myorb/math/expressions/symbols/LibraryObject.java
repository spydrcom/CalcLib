
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.SymbolMap.SymbolType;
import net.myorb.math.expressions.evaluationstates.Environment;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * description of an external class with methods to be used as imported functions
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class LibraryObject<T> extends OperationObject implements SymbolMap.Library
{


	/**
	 * generate a function to be configured for session
	 * @param <T> data type being processed
	 */
	public interface InstanceGenerator<T>
	{
		/**
		 * @param sym name to give to instance
		 * @param lib description of function to generate
		 */
		void newInstance (String sym, LibraryObject<T> lib);

		/**
		 * @param environment the description of the session
		 */
		void setEnvironment (Environment<T> environment);
	}


	/**
	 * @param classpath the path to the library object
	 * @param name the name given to this library
	 * @param methods a map of the methods
	 */
	public LibraryObject
	(String classpath, String name, Map<String, Method> methods)
	{
		super (name, 0);
		this.methods = methods;
		this.classpath = classpath;
	}


	/**
	 * @param sym the name for the symbol
	 * @param environment the session environment object
	 * @throws Exception for any errors found with type instances
	 */
	public void newInstance
	(String sym, Environment<T> environment)
	throws Exception
	{
		@SuppressWarnings("unchecked")
		InstanceGenerator<T> generator = (InstanceGenerator<T>)
			Class.forName (classpath).newInstance ();
		generator.setEnvironment (environment);
		generator.newInstance (sym, this);
	}


	/**
	 * @param sym the name for the symbol
	 * @param lib the name of the library to be used for configuration
	 * @param environment the session environment object
	 * @param <T> data type being processed
	 */
	public static <T> void newInstance
	(String sym, String lib, Environment<T> environment)
	{
		try
		{
			@SuppressWarnings("unchecked")
			LibraryObject<T> library = (LibraryObject<T>)
				environment.getSymbolMap ().get (lib);
			library.newInstance (sym, environment);
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Unable to generate instance for " + sym, e);
		}
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getSymbolType()
	 */
	public SymbolType getSymbolType () { return SymbolType.LIBRARY; }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Library#getMethods()
	 */
	public Map<String, Method> getMethods ()
	{
		return methods;
	}
	Map<String, Method> methods;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.NamedObject#formatPretty()
	 */
	public String formatPretty ()
	{
		String description = classpath;
		if (this.options != null && this.options.size() != 0)
		{
			description += " - " + this.options.toString ();
		}
		return description;
	}
	String classpath;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.OperationObject#addParameterization(java.lang.String, java.lang.String)
	 */
	public void addParameterization (String symbol, String value)
	{ this.options.put (symbol, value); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.OperationObject#addParameterization(java.util.Map)
	 */
	public void addParameterization (Map<String,Object> options)
	{ this.options.putAll (options); }
	Map<String,Object> options;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Library#getParameterization()
	 */
	public Map<String, Object> getParameterization ()
	{ return this.options; }


	/**
	 * @param environment the description of the application environment
	 */
	public void setEnvironment (Environment<T> environment)
	{
		this.options = new HashMap<String,Object>();
		this.environment = environment;
	}
	Environment<T> environment;


}
