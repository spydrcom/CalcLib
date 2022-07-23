
package net.myorb.math.expressions.gui.editor;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import javax.swing.text.JTextComponent;

import net.myorb.gui.components.DisplayFrame;

import java.util.ArrayList;

/**
 * GUI components for Snip edit display
 * @author Michael Druckman
 */
public class SnipToolDisplays extends SnipToolMenu
{

	static void buildPanel ()
	{
		tabs = new TabbedPanel ();
		tabs.setTabPlacement (JTabbedPane.LEFT);
	}
	static TabbedPanel tabs;


	static void add ()
	{
		TabPanel t = new TabPanel (tabs);
		tabs.addTab(Integer.toString (tabCount++), t);
		tabs.setSelectedComponent (t);
		t.add (buildEditor ());
	}
	static int tabCount = 1;


	static JScrollPane buildEditor ()
	{
		JEditorPane editor = new SnipEditor ();
		editor.setText (actions.getSource ().getSelectedText ());
		JScrollPane s = new JScrollPane (editor);
		s.setPreferredSize (wXh (700, 400));
		contents.add (editor);
		return s;
	}
	static ArrayList<JTextComponent> contents = new ArrayList<JTextComponent>();


	static DisplayFrame buildFrame ()
	{
		frame = new DisplayFrame
			(tabs.toComponent (), "Snip Editor");
		setMenuBar (frame);
		return frame;
	}
	static DisplayFrame frame = null;


}
