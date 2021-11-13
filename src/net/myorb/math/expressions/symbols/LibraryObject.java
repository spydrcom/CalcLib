
package net.myorb.math.expressions.symbols;

import java.lang.reflect.Method;
import java.util.Map;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.SymbolMap.SymbolType;

/**
 * description of an external class with methods to be used as imported functions
 * @author Michael Druckman
 */
public class LibraryObject extends OperationObject implements SymbolMap.Library
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
		return classpath;
	}
	String classpath;

}
