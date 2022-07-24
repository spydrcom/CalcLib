
package net.myorb.math.expressions.gui.editor;

import net.myorb.gui.components.FileDrop;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import java.awt.Component;

import java.io.File;

/**
 * processing methods for execution of editor requests
 * @author Michael Druckman
 */
public class SnipToolProcessing extends SnipToolMenu
{


	public static int W = 800, H = 500, margain = 100;


	/**
	 * @return JEditorPane with copied source text in scroll bars
	 */
	static JScrollPane buildEditor ()
	{
		JEditorPane editor = new SnipEditor ();
		editor.setText (actions.getSource ().getSelectedText ());
		JScrollPane s = new JScrollPane (editor);
		s.setPreferredSize
		(
			wXh (W - margain, H - margain)
		);
		SnipToolComponents.addEditorToList (editor);
		return s;
	}


	/**
	 * add a tab with a name
	 * @param name a name for the tab
	 */
	static void add (String name)
	{
		SnipToolComponents.add (name).add (buildEditor ());
	}


	/**
	 * add tab for file contents
	 * @param files content to read
	 */
	static void process (File file)
	{
		String name =
			SnipToolSupport.shortNameFor (file.getName ());
		add (name); SnipToolComponents.copy (file);
	}


	/**
	 * @param component related component
	 */
	static void connectDrop (Component component)
	{
		FileDrop.simpleFileDrop (component, (f) -> { process (f); });
	}


}

