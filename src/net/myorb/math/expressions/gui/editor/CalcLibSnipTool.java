
package net.myorb.math.expressions.gui.editor;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.gui.editor.SnipTool;

/**
 * CalcLib layer implementing SnipTool
 * @author Michael Druckman
 */
public class CalcLibSnipTool extends SnipTool
{

	/**
	 * add a tab for a new snip
	 * @param environment access to display components
	 */
	public static void addSnip (Environment <?> environment)
	{
		initializeSnip (environment);
		add (environment.getSnipProperties ());
	}

	/**
	 * produce an initial snip on first reference
	 * @param environment access to display components
	 */
	public static void initializeSnip (Environment <?> environment)
	{
		if (frame == null)
		{ new CalcLibSnipTool (environment); }
		frame.forceToScreen ();
	}

	/**
	 * establish snip properties
	 * @param environment access to display components
	 */
	public CalcLibSnipTool (Environment <?> environment)
	{
		super (new SnipProperties (environment));
		environment.setSnipProperties (getSnipToolPropertyAccess ());
	}

	
}
