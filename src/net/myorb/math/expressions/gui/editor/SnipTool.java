
package net.myorb.math.expressions.gui.editor;

import net.myorb.math.expressions.evaluationstates.Environment;

/**
 * an editor specifically for tool content
 * @author Michael Druckman
 */
public class SnipTool extends SnipToolDisplays
{


	/**
	 * add a tab for a new snip
	 * @param environment access to display components
	 */
	public static void addSnip (Environment<?> environment)
	{
		if (frame == null)
		{ new SnipTool (environment); }
		frame.forceToScreen ();
		add ();
	}


	/**
	 * construct singleton version of object
	 * @param environment access to display components
	 */
	public SnipTool (Environment<?> environment)
	{
		buildPanel ();
		this.environment = environment;
		prepareSnipToolActions (environment);
		actions.connectTool (this);
		show ();
	}
	protected Environment<?> environment;


	/**
	 * build display frame and show
	 */
	public void show ()
	{
		buildFrame ().showOrHide (wXh (W, H));
	}


}

