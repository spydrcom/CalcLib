
package net.myorb.math.expressions.gui;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.GreekSymbols;
import net.myorb.math.expressions.ConventionalNotations;
import net.myorb.math.expressions.symbols.OperationObject;

import net.myorb.gui.components.DisplayFrame;
import net.myorb.gui.components.DisplayTable;
import net.myorb.gui.components.MenuManager;

import javax.swing.JComponent;
import javax.swing.JMenuBar;

import java.util.List;

/**
 * table component that displays symbols
 * @author Michael Druckman
 */
public class DisplaySymbols extends MenuManager
{


	/**
	 * @param object simple symbol table Named object
	 * @param description the columns for describing the symbol
	 */
	public static void formatNamed (SymbolMap.Named object, String [] description)
	{
		String notation = GreekSymbols.findNotationFor (object.getName ());
		if (notation != null) description [1] = notation;
		description [2] = object.toString ();
	}


	/**
	 * @param object symbol table Operation object
	 * @param description the columns for describing the symbol
	 */
	public static void formatOperation (OperationObject object, String [] description)
	{
		if (object.isExposed ()) description [1] = object.displayParameters ();
	}


	/**
	 * @param object a symbol table Parameterized Function
	 * @param description the columns for describing the symbol
	 */
	public static void formatFunction
	(String name, SymbolMap.ParameterizedFunction object, String [] description)
	{
		String pretty = ConventionalNotations.determineNotationFor (name);
		String profile = pretty + " (  " + object.getParameterList () + ")";

		description [1] = profile;
		// original treatment as parameter list rather than profile
		// description [1] = object.getParameterList ();
		description [2] = object.formatPretty ();
	}


	/**
	 * format descriptions of specified named symbols
	 * @param symArray the list of names to be included
	 * @param map the map of name to symbol description
	 * @return a matrix holding the tabulated symbols
	 */
	public static String [] [] formatted
		(List <String> symArray, SymbolMap map)
	{
		Object o;
		int size = symArray==null? 0: symArray.size ();
		String name, symTable [] [] = new String [size] [3];

		for (int i = 0; i < size; i++)
		{
			symTable [i] [1] = "";
			o = map.get (name = symArray.get (i));

			if (o instanceof SymbolMap.ParameterizedFunction)
			{ formatFunction (name, (SymbolMap.ParameterizedFunction) o, symTable [i]); }
			else if (o instanceof OperationObject) formatOperation ((OperationObject) o, symTable [i]);
			else formatNamed ((SymbolMap.Named) o, symTable [i]);

			symTable [i] [0] = name; // the pretty printed version causes lookup errors
		}

		return symTable;
	}


	/**
	 * use factory to format symbol list
	 * @param listFactory the factory to use
	 * @return the tabulated list
	 */
	public static String [] [] formatted
		(
			SymbolMap.ListFactory listFactory
		)
	{
		return formatted
			(
				listFactory.getList (), listFactory.getMap ()		// named symbols found within associated map
			);
	}


	/**
	 * produce display table for listed items
	 * @param columnTitles the titles for the table columns
	 * @param listFactory the factory generating the list of names
	 * @return display table component generated
	 */
	public static JComponent tabulate
		(
			String [] columnTitles, SymbolMap.ListFactory listFactory
		)
	{
		return DisplayTable.tabulate
		(
			formatted (listFactory),								// use formatter to produce display matrix
			columnTitles											// titles for formatted columns
		);
	}


	/**
	 * add a table to the components list
	 * @param tabulation the display object to be included
	 * @param menuFactory the factory for the action list for this table
	 * @param menuBar the application menu bar being built
	 * @param coreMap the app core symbol map being built
	 * @param componentName the name to add to the map
	 */
	public static void includeTable
		(
			JComponent tabulation,
			ToolBar.MenuFactory menuFactory, JMenuBar menuBar,
			EnvironmentCore.CoreMap coreMap, String componentName
		)
	{
		ActionList actionItems = menuFactory.getMenuItems											// use specified factory
				(EnvironmentCore.getCommandProcessor (coreMap), tabulation);						//  to build action list for menu
		DisplayTable.getTableInScroll (tabulation).addMouseListener (getMenu (actionItems));		// add menu mouse listener to table
		if (menuBar != null) addToMenuBar (actionItems, menuFactory.getMenuBarTitle (), menuBar);	// add menu to application menu bar
		coreMap.put (componentName, tabulation);													// name table in core symbol map
	}


	/**
	 * build component describing symbols
	 * @param coreMap the app core symbol map
	 * @param menuBar the application menu bar
	 * @return the symbol table display component
	 */
	public static JComponent tabulateSymbols (EnvironmentCore.CoreMap coreMap, JMenuBar menuBar)
	{
		JComponent tabulation = tabulate (SYMBOL_TABLE_COLUMNS, EnvironmentCore.getSymbolMap (coreMap).getSymbolListFactory ());
		includeTable (tabulation, ToolBar.getSymbolTableMenuFactory (), menuBar, coreMap, DisplayEnvironment.SymbolTable);
		return tabulation;
	}
	static final String SYMBOL_TABLE_COLUMNS [] = {"Symbol", "Notation", "Value"};


	/**
	 * build component describing functions
	 * @param coreMap the app core symbol map
	 * @param menuBar the application menu bar
	 * @return the function display component
	 */
	public static JComponent tabulateFunctions (EnvironmentCore.CoreMap coreMap, JMenuBar menuBar)
	{
		JComponent tabulation = tabulate (FUNCTION_TABLE_COLUMNS, EnvironmentCore.getSymbolMap (coreMap).getFunctionListFactory ());
		includeTable (tabulation, ToolBar.getFunctionTableMenuFactory (), menuBar, coreMap, DisplayEnvironment.FunctionTable);
		return tabulation;
	}
	static final String FUNCTION_TABLE_COLUMNS [] = {"Function", "Profile", "Equation"};


	/**
	 * build component 
	 *  describing named symbols/functions
	 * @param coreMap the app core symbol map
	 * @param menuBar the application menu bar
	 * @return the symbol/function display component
	 */
	public static JComponent symbolSplit (EnvironmentCore.CoreMap coreMap, JMenuBar menuBar)
	{
		JComponent splitComponent = DisplayEnvironment.vSplit
			(tabulateSymbols (coreMap, menuBar), tabulateFunctions (coreMap, menuBar));
		coreMap.put (DisplayEnvironment.SymbolSplit, splitComponent);
		return splitComponent;
	}


	/**
	 * show symbols in frame
	 * @param coreMap the core symbol map
	 */
	public static void showSymbols (EnvironmentCore.CoreMap coreMap)
	{
		new DisplayFrame (symbolSplit (coreMap, null), "Symbols").show ();
	}


}

