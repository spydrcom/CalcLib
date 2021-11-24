
package net.myorb.math.expressions.gui;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.EvaluationControlI;
import net.myorb.math.expressions.gui.DisplayIO.CommandProcessor;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.gui.components.StreamDisplay;

import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * GUI map entry lookup
 * @author Michael Druckman
 */
public class EnvironmentCore
{


	/**
	 * core symbols for top level of display structure tree
	 */
	public static final String
		CoreSymbolMap = EvaluationControlI.CoreSymbolMap,
		CoreExecutionEnvironment = EvaluationControlI.CoreExecutionEnvironment,
		CoreMainConsole = EvaluationControlI.CoreMainConsole,
		CoreCommandProcessor = "CommandProcessor",
		CoreCommandLine = "CommandLine",
		CoreDisplayArea = "DisplayArea"
	;


	/**
	 * collections of objects that are the core of the application
	 */
	public static class CoreMap extends StreamDisplay.StreamProperties
	{ private static final long serialVersionUID = 8858923398178137723L; }


	/**
	 * get the user symbol map
	 * @param coreMap the map holding the GUI objects
	 * @return the user symbol map
	 */
	public static SymbolMap getSymbolMap (CoreMap coreMap)
	{
		return (SymbolMap) coreMap.get (CoreSymbolMap);
	}
	public static SymbolMap getSymbolMap (CommandProcessor processor)
	{ return getSymbolMap (processor.getMap ()); }


	/**
	 * get the command processor for the app
	 * @param properties the map holding the GUI objects
	 * @return the command processor object
	 */
	public static CommandProcessor getCommandProcessor (DisplayConsole.StreamProperties properties)
	{
		return (CommandProcessor) properties.get (CoreCommandProcessor);
	}


	/**
	 * get the master object repository for the app
	 * @param properties the map holding the GUI objects
	 * @return the central object repository
	 */
	public static Environment <?> getExecutionEnvironment
	(DisplayConsole.StreamProperties properties)
	{
		return (Environment <?>) properties.get (CoreExecutionEnvironment);
	}

	/**
	 * get the typed form of the environment object
	 * @param processor a command processor instance
	 * @return the typed form of the environment object
	 * @param <T> data type
	 */
	@SuppressWarnings ("unchecked") public static <T> Environment <T>
		getExecutionEnvironment (CommandProcessor processor)
	{
		return (Environment <T>) processor.getEnvironment ();
	}


	/**
	 * get the display area scroll
	 * @param coreMap the map holding the GUI objects
	 * @return the scroll of the display area
	 */
	public static JScrollPane getDisplayArea (CoreMap coreMap)
	{
		return (JScrollPane) coreMap.get (CoreDisplayArea);
	}


	/**
	 * get the command prompt
	 * @param coreMap the map holding the GUI objects
	 * @return the prompt object
	 */
	public static JTextField getCommandLine (CoreMap coreMap)
	{
		return (JTextField) coreMap.get (CoreCommandLine);
	}


}

