
package net.myorb.math.expressions.algorithms;

import net.myorb.math.polynomial.PolynomialFamilyManager;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;

import net.myorb.math.expressions.symbols.AbstractParameterizedFunction;
import net.myorb.math.expressions.symbols.AssignedVariableStorage;
import net.myorb.math.expressions.symbols.OperationObject;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.FunctionDefinition;


import net.myorb.math.expressions.charting.Plot3DMesh;
import net.myorb.math.expressions.charting.DisplayGraphLibraryInterface;
import net.myorb.math.expressions.charting.colormappings.LegacyColorSchemeConfiguration;
import net.myorb.math.expressions.charting.Plot3DContour;
import net.myorb.math.expressions.charting.DisplayGraph;
import net.myorb.math.expressions.charting.Tracking;

import net.myorb.math.expressions.KeywordMap;
import net.myorb.math.expressions.ConventionalNotations;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.PrettyPrinter;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.utilities.Configurable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;

/**
 * environment configuration using library abstraction
 * @author Michael Druckman
 */
public class ConfigurationManager extends Configuration
{


	public ConfigurationManager () {}
	public ConfigurationManager (BufferedReader reader) throws Exception { importSymbols (reader); }
	public ConfigurationManager (Reader reader) throws Exception { this (new BufferedReader (reader)); }
	public ConfigurationManager (FileReader reader) throws Exception { this (new BufferedReader (reader)); }
	public ConfigurationManager (String path) throws Exception { this (new FileReader (path)); }


	/**
	 * construct identified library collector
	 * @param qualifiedClassNameOfLibraryCollector the path to the library collection object to be instanced
	 * @param parameters text of parameters to be passed for configuration
	 * @throws Exception for failure to instance named collector
	 */
	public void accumulateLibraries
	(String qualifiedClassNameOfLibraryCollector, String parameters)
	throws Exception
	{
		setLibraryCollector
		(
			accumulateLibraries
			(
				libraryMap, qualifiedClassNameOfLibraryCollector, parameters
			)
		);
	}
	LibraryMap libraryMap = new LibraryMap ();


	/**
	 * establish environment symbol table and library collector
	 * @param libraryCollector an object acting as library collector
	 */
	public void setLibraryCollector (LibraryCollector libraryCollector)
	{
		this.symbolTable =
			libraryCollector.getEnvironment ().getSymbolMap ();
		this.collector = libraryCollector;
	}
	LibraryCollector collector = null;


	/**
	 * get the Environment object
	 * associated with most recently instanced library collector
	 * @return the associated Environment object
	 */
	@SuppressWarnings("rawtypes") public Environment getEnvironment () { return collector.getEnvironment (); }
	@SuppressWarnings("rawtypes") public void addCommands (KeywordMap keywords) { collector.addKeywords (keywords); }
	public SymbolMap getSymbolMap () { return symbolTable; }
	SymbolMap symbolTable = null;


	/**
	 * provide a path to an extended library
	 * @param name the name to be given to the library
	 * @param classpath the path to the library object
	 * @return the new library entry
	 */
	public Object addExtendedLibrary (String name, String classpath)
	{
		return addExtensionLibrary (classpath, name, libraryMap);
	}


	/**
	 * import operator implementation from library
	 * @param libraryName the name of the library expected to contain operator
	 * @param description the description of the operator to be added to symbol table
	 * @param operatorName the name of the operator to be found in the library
	 * @param operatorSymbol the symbol to be associated with the new operator
	 * @param usingPrecedence the precedence to associate with the operator
	 * @return the named symbol generated for configuration item
	 * @throws Exception for failure to find operator
	 */
	public SymbolMap.Named importOperator
	(String libraryName, String description, String operatorName, String operatorSymbol, int usingPrecedence)
	throws Exception
	{
		return importOperator (libraryMap.get (libraryName), symbolTable, description, operatorName, operatorSymbol, usingPrecedence);
	}


	/**
	 * import function implementation from library
	 * @param libraryName the name of the library expected to contain function
	 * @param description the description of the function to be added to symbol table
	 * @param functionName the name of the function to be found in the library
	 * @param functionSymbol the symbol to be associated with the function
	 * @return the named symbol allocated for the new function
	 * @throws Exception for failure to find function
	 */
	public SymbolMap.Named importFunction
	(String libraryName, String description, String functionName, String functionSymbol)
	throws Exception
	{
		return importFunction (libraryMap.get (libraryName), symbolTable, description, functionName, functionSymbol);
	}


	/**
	 * parse comma separated element of a text line
	 * @param elements the text elements to be parsed into import
	 * @throws Exception for failure to process elements
	 */
	public void importSymbol (String elements) throws Exception
	{
		System.out.println (elements);
		importSymbol (elements.split (","));
	}


	/**
	 * import elements from stream reader
	 * @param reader a buffered reader containing import requests
	 * @throws Exception for failure to process elements
	 */
	public void importSymbols (BufferedReader reader) throws Exception
	{
		String line;
		while ((line = reader.readLine ()) != null)
		{ importSymbol (line); }
	}


	/**
	 * import named value from library
	 * @param libraryName the name of the library expected to contain value
	 * @param description the description of the value to be added to symbol table
	 * @param valueName the name of the value to be found in the library
	 * @param valueSymbol the symbol to be associated with the value
	 * @throws Exception for failure to find value
	 */
	public void importValue
	(String libraryName, String description, String valueName, String valueSymbol)
	throws Exception
	{
		importValue (libraryMap.get (libraryName), symbolTable, description, valueName, valueSymbol);
	}


	/**
	 * import named constant into symbol table
	 * @param constantSymbol the symbol to be assigned with value
	 * @param constantValue the value of the symbol
	 * @param description a help description
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" }) public void importConstant
	(String constantSymbol, String constantValue, String description)
	{
		ValueManager.GenericValue value;
		Environment e = collector.getEnvironment ();
		ValueManager vm = e.getValueManager (); ExpressionSpaceManager sm = e.getSpaceManager ();
		(value =  vm.newDiscreteValue (sm.evaluate (constantValue))).setName (constantSymbol);
		SymbolMap.Named item = new AssignedVariableStorage (constantSymbol, value);
		symbolTable.add (item, description);
	}


	/**
	 * use graphics library identified in configuration file
	 * @param path the path to the library class
	 */
	public void useGraphicsLibrary (String path)
	{
		try
		{
			Object library = Class.forName (path).newInstance ();
			DisplayGraph.setActiveChartLibrary ( (DisplayGraphLibraryInterface) library );
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Unable to load graphics library: " + path + ", default used", e);
		}
	}

	/**
	 * @param parameters text of parameters from config file
	 */
	public void processGraphicsParameters (String parameters)
	{
		System.out.print ("Graphics parameter processing: ");
		for (String p : parameters.split (";"))
		{
			System.out.print (p);
			System.out.print ("; ");
			String q[] = p.split ("=");
			switch (GraphicsParameters.valueOf (q[0]))
			{
				case MeshPlotEdgeSize:
					Plot3DMesh.setMeshPlotEdgeSize (Integer.parseInt (q[1]));
					break;
				case ContourPlotEdgeSize:
					Plot3DContour.setContourPlotEdgeSize (Integer.parseInt (q[1]));
					LegacyColorSchemeConfiguration.postColorSchemeFactories ();
					break;
			}
		}
		System.out.println ();
	}
	public enum GraphicsParameters {MeshPlotEdgeSize, ContourPlotEdgeSize}


	/**
	 * @param fileName name of file to use
	 * @param <T> data type
	 */
	public <T> void useTrackingFile (String fileName)
	{
		try
		{
			Tracking.setTrackingFile (fileName);
			@SuppressWarnings("unchecked") Environment<T> env = getEnvironment ();
			Tracking.loadEquations (new FunctionDefinition<T> (env));
		} catch (Exception e) { e.printStackTrace (); }
	}


	/**
	 * use Math Markup library identified in configuration file
	 * @param path the path to the library class
	 */
	public void useRenderingLibrary (String path)
	{
		try
		{
			Object library = Class.forName (path).newInstance ();
			PrettyPrinter.setMathMarkupRenderer ((PrettyPrinter.MathMarkupRendering) library);
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Unable to load rendering library: " + path + ", not implemented in this configuration");
		}
	}


	/**
	 * import Special Function Family description
	 * @param type P = Polynomial or S = Special Function
	 * @param name the name of the special function Family
	 * @param path the path to the package holding the family description
	 */
	@SuppressWarnings("unchecked")
	public void importFunctionFamily (String type, String name, String path)
	{
		if (type.startsWith ("P"))
		{ PolynomialFamilyManager.importFamilyDescription (name, path, getEnvironment ()); }
		else SpecialFunctionFamilyManager.importFamilyDescription (name, path, getEnvironment ());
	}


	/**
	 * import symbols using command line lexical elements
	 *  "CONSTANT", "add a named constant to the symbol table"
	 *  "LIBRARY", "the class path to a library accumulation object"
	 *  "EXTEND", "name", "the class path to a library extension object"
	 *  "GRAPHICS", "the class path to an expression charting library"
	 *  "RENDERING", "the class path to an expression rendering library"
	 *  "VALUE", "library name", "value description", "value name", "symbol"
	 *  "FUNCTION", "library name", "function description", "function name", "symbol" { , "notation", "configuration" }
	 *  "OPERATOR", "library name", "operator description", "operator name", "symbol", "precedence" { , "notation", "configuration" }
	 *  "POLYNOMIAL", "family name", "optional full classpath, default is net.myorb.math"
	 *  "SPECIAL", "family name", "optional full classpath, default is net.myorb.math"
	 * @param elements the text that defines an symbol object being imported
	 * @throws Exception for failure to instance a named object
	 */
	public void importSymbol (String[] elements) throws Exception
	{
		if (elements[0].startsWith ("V"))
		{
			importValue (elements[1], elements[2], elements[3], elements[4]);
		}
		else if (elements[0].startsWith ("O"))
		{
			SymbolMap.Named n = importOperator
			(
				elements[1], elements[2], elements[3], elements[4], Integer.parseInt (elements[5])
			);
			if (elements.length > 6) { ConventionalNotations.setNotationFor (elements[4], elements[6]); }
			if (elements.length > 7) { ( (OperationObject) n).addParameterization (elements[7]); }
		}
		else if (elements[0].startsWith ("F"))
		{
			SymbolMap.Named n = importFunction (elements[1], elements[2], elements[3], elements[4]);
			if (elements.length > 5) { ConventionalNotations.setNotationFor (elements[4], elements[5]); }
			if (elements.length > 6) { ( (AbstractParameterizedFunction) n).addParameterization (elements[6]); }
		}
		else if (elements[0].startsWith ("C"))
		{
			importConstant (elements[1], elements[2], elements[3]);
		}
		else if (elements[0].startsWith ("P") || elements[0].startsWith ("S"))
		{
			importFunctionFamily (elements[0], elements[1], elements[2]);
		}
		else if (elements[0].startsWith ("R"))
		{
			useRenderingLibrary (elements[1]);
		}
		else if (elements[0].startsWith ("G"))
		{
			useGraphicsLibrary (elements[1]);
			if (elements.length > 3) { processGraphicsParameters (elements[3]); }
		}
		else if (elements[0].startsWith ("T"))
		{
			useTrackingFile (elements[1]);
		}
		else if (elements[0].startsWith ("L"))
		{
			String parameter =
				elements.length > 2? elements[2]: null;
			accumulateLibraries (elements[1], parameter);
		}
		else if (elements[0].startsWith ("E"))
		{
			Object newLibraryEntry = addExtendedLibrary (elements[1], elements[2]);
			if (elements.length > 3) configure (newLibraryEntry, elements[3]);
		}
	}


	/**
	 * pass configuration data to the object 
	 * @param newLibraryEntry the new Library object
	 * @param configurationText the text of the configuration data
	 */
	void configure (Object newLibraryEntry, String configurationText)
	{
		((Configurable) newLibraryEntry).configure (configurationText);
	}


}

