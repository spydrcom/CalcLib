
package net.myorb.math.expressions.gui.editor;

import net.myorb.gui.components.TextComponentStreams;
import net.myorb.gui.components.SimpleScreenIO.TabPanel;
import net.myorb.gui.components.SimpleScreenIO.TabbedPanel;

import net.myorb.data.abstractions.SimpleStreamIO;

import javax.swing.text.JTextComponent;
import javax.swing.JTabbedPane;
import javax.swing.JComponent;

import java.util.ArrayList;
import java.io.File;

public class SnipToolComponents
{


	/**
	 * copy file contents to editor
	 * @param file source of content
	 */
	static void copy (File file)
	{
		try
		{ SimpleStreamIO.processTextFile (file, getComponentSink ()); }
		catch (Exception e) {}
	}


	/**
	 * @return text sink for selected tab
	 */
	static SimpleStreamIO.TextSink getComponentSink ()
	{
		return properties.streamsForSelectedTab ().getTextSink ();
	}

	/**
	 * @return text source for selected tab
	 */
	static SimpleStreamIO.TextSource getComponentSource ()
	{
		return properties.streamsForSelectedTab ().getTextSource ();
	}


	static void setName (String name) { properties.setName (name); }

	static TabPanel add (String name) { return properties.add (name); }

	static JComponent getTabbedPanel () { return properties.getTabbedPanel (); }

	static TabbedPanel buildTabbedPanel () { return properties.buildTabbedPanel (); }

	static void addEditorToList (JTextComponent editor) { properties.addEditorToList (editor); }

	static JTextComponent getTextContainer () { return properties.getTextContainer (); }

	static SnipToolComponentProperties properties = new SnipToolComponentProperties ();

	static void setToRequestedName () throws Exception
	{ properties.setToRequestedName (); }


}


class SnipToolComponentProperties
{


	/**
	 * @return source and sink streams for selected tab
	 */
	TextComponentStreams streamsForSelectedTab ()
	{
		return new TextComponentStreams (getTextContainer (), false, '\n');
	}


	/**
	 * @return TabbedPanel as constructed
	 */
	TabbedPanel buildTabbedPanel ()
	{
		tabs = new TabbedPanel ();
		tabs.setTabPlacement (JTabbedPane.LEFT);
		contents = new ArrayList<JTextComponent>();
		return tabs;
	}


	/**
	 * @return TabbedPanel Component
	 */
	JComponent getTabbedPanel ()
	{
		return tabs.toComponent ();
	}


	/**
	 * @param name the name to be added
	 * @return the panel added
	 */
	TabPanel add (String name)
	{
		TabPanel t;
		tabs.addTab (name, t = new TabPanel (tabs));
		tabs.setSelectedComponent (t);
		return t;
	}


	/**
	 * request name for tab
	 * @throws Exception for GUI error
	 */
	void setToRequestedName () throws Exception
	{
		setName (SnipToolSupport.requestName (tabs));
	}


	/**
	 * @param name the name to give the tab
	 */
	void setName (String name)
	{
		tabs.setTitleAt (tabs.getSelectedIndex (), name);
	}
	TabbedPanel tabs;


	/**
	 * @return the currently selected tab contents
	 */
	JTextComponent getTextContainer ()
	{
		return contents.get (tabs.getSelectedIndex ());
	}

	/**
	 * @param editor Text Component to be added
	 */
	void addEditorToList (JTextComponent editor)
	{
		contents.add (editor);
	}
	ArrayList<JTextComponent> contents;

}

