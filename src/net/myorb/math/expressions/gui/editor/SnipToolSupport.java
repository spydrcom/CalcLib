
package net.myorb.math.expressions.gui.editor;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.gui.EnvironmentCore;
import net.myorb.math.expressions.gui.DisplayConsole;

import net.myorb.gui.components.SimpleScreenIO;

import java.awt.Component;

/**
 * helper class for Snip tool features
 * @author Michael Druckman
 */
public class SnipToolSupport extends SimpleScreenIO
{


	/**
	 * locate source environment component
	 * @param environment access to display components
	 * @return the text component with source of text to edit
	 */
	public static Object getDisplay (Environment<?> environment)
	{
		DisplayConsole.StreamProperties
			guiMap = environment.getControl ().getGuiMap ();
		return guiMap.get (EnvironmentCore.CoreDisplayComponent);
	}


	/**
	 * @param forWidget related Component
	 * @return the name provided by screen request
	 * @throws Exception for errors in the input request
	 */
	public static String requestName (Component forWidget) throws Exception
	{
		return requestTextInput (forWidget, "Name for Tab", "", "NA");
	}


	/**
	 * @param fullName files name with file type extended
	 * @return name with type removed
	 */
	public static String shortNameFor (String fullName)
	{		
		int dot = fullName.lastIndexOf ('.');
		if (dot > 0) return fullName.substring (0, dot);
		return fullName;
	}


}

