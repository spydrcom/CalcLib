
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.SymbolMap.SymbolType;
import net.myorb.math.expressions.evaluationstates.Environment;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * description of an external class with methods to be used as imported functions
 * @author Michael Druckman
 */
public class LibraryObject<T> extends OperationObject implements SymbolMap.Library
{

	public LibraryObject
	(String classpath, String name, Map<String, Method> methods)
	{
		super (name, 0);
		this.methods = methods;
		this.classpath = classpath;
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

	/**
	 * @param environment the description of the application environment
	 */
	public void setEnvironment (Environment<T> environment)
	{
		this.options = new HashMap<String,Object>();
		this.environment = environment;
	}
	Environment<T> environment;

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

}
