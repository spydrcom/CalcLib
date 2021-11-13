
package net.myorb.math.expressions.gui;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.Component;

/**
 * build application root menu 
 *  from generic action item manager
 * @author Michael Druckman
 */
public class ToolBarGenericMenu
	extends ToolBarGeneric <JMenuItem, JMenu>
{

	/**
	 * @param parent GUI component to use as center
	 */
	public ToolBarGenericMenu (Component parent)
	{
		super (parent);
	}


	/*
	 * implementation of generic collection for menu items
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.ToolBarGeneric#add(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void add (JMenuItem item, JMenu to) { to.add (item); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.ToolBarGeneric#attribute(java.lang.Object, java.awt.event.ActionListener, java.lang.String)
	 */
	@Override
	public void attribute
	(JMenuItem item, ActionListener listener, String tip)
	{
		item.addActionListener (listener);
		item.setToolTipText (tip);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.ToolBarGeneric#construct(java.lang.String)
	 */
	@Override
	public JMenuItem construct (String name) {
		return new JMenuItem (name);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.ToolBarGeneric#addTo(net.myorb.math.expressions.gui.TrackingList, java.lang.Object)
	 */
	@Override
	void addTo (TrackingList list, JMenuItem from) {
		list.addItem (from);
	}


	/**
	 * construct a menu and add to bar
	 * @param name the name of the menu being added
	 * @param bar the menu bar being constructed
	 * @return the menu added to the bar
	 */
	public static JMenu addMenu (String name, JMenuBar bar)
	{
		JMenu menu = new JMenu (name);
		bar.add (menu);
		return menu;
	}


	/**
	 * @param processor the command processor for the application
	 * @param parent GUI component to use as center
	 * @return the application menu bar
	 */
	public static JMenuBar getMasterMenuBar
	(DisplayIO.CommandProcessor processor, Component parent)
	{
		ToolBarGenericMenu tool =
				new ToolBarGenericMenu (parent);
		JMenuBar bar = new JMenuBar ();
		
		tool.home (addMenu ("Home", bar), processor);
		tool.data (addMenu ("Data", bar), processor);
		tool.primes (addMenu ("Primes", bar), processor);
		tool.polynomials (addMenu ("Polynomials", bar), processor);
		tool.matrices (addMenu ("Matrices", bar), processor);
		tool.simulEq (addMenu ("SimulEq", bar), processor);
		tool.regression (addMenu ("Regression", bar), processor);
		tool.statistics (addMenu ("Statistics", bar), processor);
		tool.fractals (addMenu ("Fractals", bar), processor);
		tool.charts (addMenu ("Charts", bar), processor);
		
		return bar;
	}


}

