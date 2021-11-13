
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.KeywordMap;
import net.myorb.math.expressions.SymbolMap;

import java.lang.reflect.Method;

import java.util.HashMap;

/**
 * implementation of algorithm library abstraction
 * @author Michael Druckman
 */
public class Configuration
{


	/**
	 * name of library is mapped to class implementing algorithms
	 */
	public static class LibraryMap extends HashMap<String,Object>
	{ private static final long serialVersionUID = 1L; }


	/**
	 * description of object that identifies library objects
	 */
	@SuppressWarnings("rawtypes")
	public interface LibraryCollector
	{
		/**
		 * pass parameters for configuration of library
		 * @param parameters the unparsed text of the configuration parameters
		 */
		void configure (String parameters);
		
		/**
		 * add objects collected into library map
		 * @param libraryMap map name to library object
		 */
		void addLibraries (LibraryMap libraryMap);

		/**
		 * add commands specific to this library
		 * @param keywords the keyword map associated with the running engine
		 */
		void addKeywords (KeywordMap keywords);

		/**
		 * get access to environment that describes data used by libraries
		 * @return the environment object used by the collected libraries
		 */
		Environment getEnvironment ();
	}


	/**
	 * get instance of library collection object named by class path
	 * @param qualifiedClassName the path to the library collection object to be instanced
	 * @return the new instance of the library collection object named
	 * @throws Exception for failure to instance named collector
	 */
	public static LibraryCollector getLibraryCollector (String qualifiedClassName) throws Exception
	{
		try { return (LibraryCollector) Class.forName (qualifiedClassName).newInstance (); }
		catch (Exception x) { throw new Exception ("Library error: " + qualifiedClassName); }
	}


	/**
	 * map library named
	 * @param libraryMap a library map object for the collection
	 * @param qualifiedClassNameOfLibraryCollector the path to the library collection object to be instanced
	 * @param parameters the text of configuration parameters being passed to collector
	 * @return the new instance of the library collection object named
	 * @throws Exception for failure to instance named collector
	 */
	public static LibraryCollector accumulateLibraries
	(LibraryMap libraryMap, String qualifiedClassNameOfLibraryCollector, String parameters)
	throws Exception
	{
		LibraryCollector collector = getLibraryCollector (qualifiedClassNameOfLibraryCollector);
		if (parameters != null) collector.configure (parameters);
		collector.addLibraries (libraryMap);
		return collector;
	}

	/**
	 * add extension object to library list
	 * @param classpath the path to the extension object
	 * @param extensionName the name to be given to the extension object
	 * @param libraryMap the map to be extended
	 * @return the allocated object
	 */
	public static Object addExtensionLibrary (String classpath, String extensionName, Configuration.LibraryMap libraryMap)
	{
		Object item;
		try { libraryMap.put (extensionName, item = Class.forName (classpath).newInstance ()); }
		catch (Exception e) { throw new RuntimeException ("Library reference not found"); }
		return item;
	}

	/**
	 * import operator implementation from library
	 * @param fromLibrary the library object expected to contain operator
	 * @param intoTable the symbol table to put the operator implementation in
	 * @param description the description of the operator to be added to symbol table
	 * @param operatorName the name of the operator to be found in the library
	 * @param operatorSymbol the symbol to be associated with the new operator
	 * @param usingPrecedence the precedence to associate with the operator
	 * @return the named symbol generated for configuration item
	 * @throws Exception for failure to find operator
	 */
	public static SymbolMap.Named importOperator
	(Object fromLibrary, SymbolMap intoTable, String description, String operatorName, String operatorSymbol, int usingPrecedence)
	throws Exception
	{
		SymbolMap.Named n;
		Method m = fromLibrary.getClass ().getMethod ("get" + operatorName + "Algorithm", String.class, int.class);
		intoTable.add (n = (SymbolMap.Named) m.invoke (fromLibrary, operatorSymbol, usingPrecedence), description);
		return n;
	}


	/**
	 * import operator implementation from library
	 * @param libraries the map to be used to locate library
	 * @param libraryName the name of the library expected to contain operator
	 * @param description the description of the operator to be added to symbol table
	 * @param operatorName the name of the operator to be found in the library
	 * @param operatorSymbol the symbol to be associated with the new operator
	 * @param usingPrecedence the precedence to associate with the operator
	 * @param intoTable the symbol table to put the implementation in
	 * @throws Exception for failure to find operator
	 */
	public static void importOperator
	(LibraryMap libraries, String libraryName, String description, String operatorName, String operatorSymbol, int usingPrecedence, SymbolMap intoTable)
	throws Exception
	{
		importOperator (libraries.get (libraryName), intoTable, description, operatorName, operatorSymbol, usingPrecedence);
	}


	/**
	 * import function implementation from library
	 * @param fromLibrary the library object expected to contain function
	 * @param intoTable the symbol table to put the function implementation in
	 * @param description the description of the function to be added to symbol table
	 * @param functionName the name of the function to be found in the library
	 * @param functionSymbol the symbol to be associated with the function
	 * @return the named symbol allocated for the new function
	 * @throws Exception for failure to find function
	 */
	public static SymbolMap.Named importFunction
	(Object fromLibrary, SymbolMap intoTable, String description, String functionName, String functionSymbol)
	throws Exception
	{
		SymbolMap.Named n;
		Method m = fromLibrary.getClass ().getMethod ("get" + functionName + "Algorithm", String.class);
		intoTable.add (n = (SymbolMap.Named) m.invoke (fromLibrary, functionSymbol), description);
		return n;
	}


	/**
	 * import function implementation from library
	 * @param libraries the map to be used to locate library
	 * @param libraryName the name of the library expected to contain function
	 * @param description the description of the function to be added to symbol table
	 * @param functionName the name of the function to be found in the library
	 * @param functionSymbol the symbol to be associated with the function
	 * @param intoTable the symbol table to put the implementation in
	 * @throws Exception for failure to find operator
	 */
	public static void importFunction
	(LibraryMap libraries, String libraryName, String description, String functionName, String functionSymbol, SymbolMap intoTable)
	throws Exception
	{
		importFunction (libraries.get (libraryName), intoTable, functionName, functionSymbol, description);
	}


	/**
	 * import named value from library
	 * @param fromLibrary the library object expected to contain value
	 * @param intoTable the symbol table to put the value implementation in
	 * @param description the description of the value to be added to symbol table
	 * @param valueName the name of the value to be found in the library
	 * @param valueSymbol the symbol to be associated with the value
	 * @throws Exception for failure to find value
	 */
	public static void importValue
	(Object fromLibrary, SymbolMap intoTable, String description, String valueName, String valueSymbol)
	throws Exception
	{
		Method m = fromLibrary.getClass ().getMethod
				("get" + valueName + "Value", String.class);
		intoTable.add ((SymbolMap.Named) m.invoke (fromLibrary, valueSymbol), description);
	}


	/**
	 * import named value from library
	 * @param libraries the map to be used to locate library
	 * @param libraryName the name of the library expected to contain value
	 * @param description the description of the value to be added to symbol table
	 * @param valueName the name of the value to be found in the library
	 * @param valueSymbol the symbol to be associated with the value
	 * @param intoTable the symbol table to put the value in
	 * @throws Exception for failure to find value
	 */
	public static void importValue
	(LibraryMap libraries, String libraryName, String description, String valueName, String valueSymbol, SymbolMap intoTable)
	throws Exception
	{
		importValue (libraries.get (libraryName), intoTable, valueName, valueSymbol, description);
	}


}

