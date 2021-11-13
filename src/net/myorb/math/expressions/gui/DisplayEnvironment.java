
package net.myorb.math.expressions.gui;

import net.myorb.gui.components.*;

import javax.swing.RootPaneContainer;

import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JComponent;
import javax.swing.JMenuBar;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import java.util.Map;

/**
 * split components display symbols, stored data, functions, and scripts.
 *  capture positions of components within GUI display so refresh rebuilds correctly
 * @author Michael Druckman
 */
public class DisplayEnvironment
{


	/*
	 * component names
	 */

	public static final String
	EnvironmentFrame = "EnvironmentFrame",
	ScriptFiles = "ScriptFiles", DataFiles = "DataFiles",
	FunctionTable = "FunctionTable", SymbolTable = "SymbolTable",
	SymbolSplit = "SymbolSplit", FileSplit = "FileSplit", EnvSplit = "EnvSplit";


	/**
	 * build the split component
	 * @param left the left side component
	 * @param right the right side component
	 * @param type the swing type identifier
	 * @param dividedAt the divider location
	 * @return the split as a component
	 */
	public static JComponent split
		(JComponent left, JComponent right, int type, int dividedAt)
	{
		JSplitPane pane = new JSplitPane (type, left, right);
		pane.setDividerLocation (dividedAt);
		return pane;
	}
	public static
		JComponent hSplit (JComponent left, JComponent right)
	{ return split (left, right, JSplitPane.HORIZONTAL_SPLIT, 120); }
	public static JComponent vSplit (JComponent left, JComponent right)
	{ return split (left, right, JSplitPane.VERTICAL_SPLIT, 300); }


	/**
	 * split files section from symbols section
	 * @param coreMap the named components map
	 * @param menuBar the menu to be applied to components
	 * @return the split as a component
	 */
	public static JComponent sectionSplit
	(EnvironmentCore.CoreMap coreMap, JMenuBar menuBar)
	{
		JComponent c = hSplit
			(
				DisplayFiles.fileSplit (coreMap, menuBar),
				DisplaySymbols.symbolSplit (coreMap, menuBar)
			);
		c.setPreferredSize (new Dimension (300, 600));
		coreMap.put (EnvSplit, c);
		return c;
	}


	/**
	 * construct the GUI from the component map
	 * @param coreMap the component map
	 */
	public static void showEnvironment (EnvironmentCore.CoreMap coreMap)
	{
		JMenuBar menuBar = new JMenuBar ();
		EnvironmentState state = new EnvironmentState (coreMap);
		DisplayFrame f = new DisplayFrame
			(
				sectionSplit (coreMap, menuBar), "CALCLIB Environment", menuBar
			);
		f.setIcon ("images/icon-env.gif");
		coreMap.put (EnvironmentFrame, f.showAt (state.getLocation ()));
		state.copyState ();
	}


}


/**
 * capture the position parameters of the GUI
 */
@SuppressWarnings ("serial")
class EnvironmentState extends WidgetIndex
{
	

	/**
	 * @param components the components map for the widget
	 */
	EnvironmentState (EnvironmentCore.CoreMap components)
	{
		this.environmentFrame = (RootPaneContainer) components.get
			(DisplayEnvironment.EnvironmentFrame);
		this.components = components;
		copyComponents ();

		if (this.environmentFrame != null)
		{
			this.properties = capturePropertySettings ();
		}
	}
	RootPaneContainer environmentFrame = null;
	Properties properties = null;


	/**
	 * copy the component map into this index
	 */
	void copyComponents ()
	{
		Object object;
		for (String item : components.keySet ())
		{
			if ((object = components.get (item)) instanceof JScrollPane)
			{
				JScrollPane scroll = (JScrollPane) object;
				Component viewed = scroll.getViewport ().getComponent (0);
				this.put (item + "$VIEWED", viewed); this.put (item, scroll);
			}
			else if (object instanceof Component) this.put (item, (Component) object);
		}
	}
	Map <String, Object> components;


	/**
	 * properties of each component are updated to captured settings
	 */
	void copyState ()
	{
		if (environmentFrame != null)
		{
			try
			{
				copyComponents ();
				applyPropertySettings (properties);
				location = ((Component) environmentFrame).getLocation ();
				GuiToolkit.dispose (environmentFrame);
			} catch (Exception e) {}
		}
	}
	Point getLocation () { return location; }
	Point location = null;


}

