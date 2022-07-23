
package net.myorb.math.expressions.gui.editor;

import net.myorb.math.expressions.evaluationstates.Environment;

import javax.swing.text.JTextComponent;

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
	 * @param name the name to give the tab
	 */
	public void setName (String name)
	{
		tabs.setTitleAt (tabs.getSelectedIndex (), name);
	}


	/**
	 * @return the currently selected tab contents
	 */
	public JTextComponent getTextContainer ()
	{ return contents.get (tabs.getSelectedIndex ()); }


	/**
	 * build display frame and show
	 */
	public void show ()
	{
		buildFrame ().showOrHide (wXh (W, H));
	}


}

