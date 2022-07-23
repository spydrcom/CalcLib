
package net.myorb.math.expressions.gui.editor;

import net.myorb.gui.components.DisplayFrame;

import javax.swing.text.JTextComponent;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import java.util.ArrayList;

/**
 * GUI components for Snip edit display
 * @author Michael Druckman
 */
public class SnipToolDisplays extends SnipToolMenu
{


	public static int W = 800, H = 500, margain = 100;


	/**
	 * construct tab panel with left side index
	 */
	static void buildPanel ()
	{
		tabs = new TabbedPanel ();
		tabs.setTabPlacement (JTabbedPane.LEFT);
	}
	static TabbedPanel tabs;


	/**
	 * add a text panel with an index count as name
	 */
	static void add ()
	{
		TabPanel t = new TabPanel (tabs);
		tabs.addTab(Integer.toString (tabCount++), t);
		tabs.setSelectedComponent (t);
		t.add (buildEditor ());
	}
	static int tabCount = 1;


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
		contents.add (editor);
		return s;
	}
	static ArrayList<JTextComponent> contents = new ArrayList<JTextComponent>();


	/**
	 * @return DisplayFrame holding tabs and menu bar
	 */
	static DisplayFrame buildFrame ()
	{
		frame = new DisplayFrame
			(tabs.toComponent (), "Snip Editor");
		setMenuBar (frame);
		return frame;
	}
	static DisplayFrame frame = null;


}

