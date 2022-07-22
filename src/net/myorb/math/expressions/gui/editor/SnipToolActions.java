
package net.myorb.math.expressions.gui.editor;

import net.myorb.math.expressions.evaluationstates.Environment;

import javax.swing.text.JTextComponent;

import java.awt.event.ActionListener;
//import java.awt.event.ActionEvent;

/**
 * implementation of the editor actions
 * @author Michael Druckman
 */
public class SnipToolActions
{

	/**
	 * @param environment access to display components
	 */
	public void setEnvironment (Environment<?> environment)
	{
		this.environment = environment;
	}
	protected Environment<?> environment;

	public void process (String source)
	{
		environment.getControl ().execute (source);
	}

	/**
	 * @param source the component holding original source
	 */
	public void setSource (JTextComponent source)
	{
		this.source = source;
	}
	protected JTextComponent source;

	/**
	 * @param tool the snip tool class using these actions
	 */
	public void connectTool (SnipTool tool)
	{
		this.tool = tool;
	}
	protected SnipTool tool;

	/*
	 * Open and Edit action items
	 */

	public ActionListener getOpenAction ()
	{
		return (e) ->
		{
			throw new RuntimeException ("NOT implemented");
		};
	}

	public ActionListener getSaveAction ()
	{
		return (e) ->
		{
			throw new RuntimeException ("NOT implemented");
		};
	}

	public ActionListener getCopyAction ()
	{
		return (e) ->
		{
			throw new RuntimeException ("NOT implemented");
		};
	}

	/*
	 * Execute action items
	 */

	/**
	 * @param line a text line to execute
	 */
	public void exec (String line)
	{
		source.setText (source.getText () + line);
		process (line);
	}

	/**
	 * execute a block of text
	 * @param text the block of text lines
	 * @param sep a separation character for breaking the lines
	 */
	public void exec (String text, String sep)
	{
		//dump (text);
		String[] lines = text.split (sep);
		for (String line : lines) exec (line + "\r\n");
	}
	public void dump (String text)
	{
		for (int i = 0; i < text.length (); i++)
		{
			System.out.println (Integer.toHexString (text.charAt (i)));
		}
	}

	/**
	 * execute full contents of editor
	 * @return the action implementation
	 */
	public ActionListener getExecAllAction ()
	{
		return (e) ->
		{
			exec (tool.getText (), "\r");
		};
	}

	/**
	 * execute selected contents of editor
	 * @return the action implementation
	 */
	public ActionListener getExecSelectedAction ()
	{
		return (e) ->
		{
			exec (tool.getSelectedText (), "\n");
		};
	}

}

