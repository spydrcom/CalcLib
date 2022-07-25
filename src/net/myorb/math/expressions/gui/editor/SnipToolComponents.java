
package net.myorb.math.expressions.gui.editor;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.TextComponentStreams;

import net.myorb.gui.components.SimpleScreenIO.TabPanel;
import net.myorb.gui.components.SimpleScreenIO.TabbedPanel;

import net.myorb.data.abstractions.SimpleStreamIO;

import javax.swing.text.JTextComponent;

import javax.swing.JTabbedPane;
import javax.swing.JComponent;

import java.util.ArrayList;
import java.io.File;

/**
 * static wrapper for property access
 * @author Michael Druckman
 */
public class SnipToolComponents extends SimpleScreenIO
{


	/**
	 * request a tab name
	 * @return name specified to GUI request for user input
	 * @throws Exception process canceled by user
	 */
	static String requestName () throws Exception
	{
		return SnipToolSupport.requestName (getTabbedPanel ());
	}

	
	/**
	 * write content to file
	 * @param file destination file for save
	 */
	static void saveTo (File file)
	{
		try { SimpleStreamIO.generateTextFile (getComponentSource (), file); }
		catch (Exception e) { e.printStackTrace (); }
	}


	/**
	 * copy file contents to editor
	 * @param file source of content
	 */
	static void copy (File file)
	{
		try
		{ SimpleStreamIO.processTextFile (file, getComponentSink ()); }
		catch (Exception e) { e.printStackTrace (); }
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


	/*
	 * static wrapper for property access
	 */

	static String getName () { return properties.getName (); }

	static void setName (String name) { properties.setName (name); }

	static TabPanel add (String name) { return properties.add (name); }

	static JComponent getTabbedPanel () { return properties.getTabbedPanel (); }

	static TabbedPanel buildTabbedPanel () { return properties.buildTabbedPanel (); }

	static JTextComponent getTextContainer () { return properties.getTextContainer (); }

	static void addEditorToList (JTextComponent editor) { properties.addEditorToList (editor); }


	/*
	 * GUI properties made accessible via bean conventions
	 */

	static SnipToolComponentProperties properties = new SnipToolComponentProperties ();


}


/**
 * a bean holding the display components
 */
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


	/*
	 * manage the tabbed panel
	 */

	/**
	 * @return the name given the selected tab
	 */
	String getName ()
	{
		return tabs.getTitleAt (tabs.getSelectedIndex ());
	}

	/**
	 * @param name the name to give the tab
	 */
	void setName (String name)
	{
		tabs.setTitleAt (tabs.getSelectedIndex (), name);
	}
	protected TabbedPanel tabs;


	/*
	 * manage list of editors
	 */

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
	protected ArrayList<JTextComponent> contents;

}

