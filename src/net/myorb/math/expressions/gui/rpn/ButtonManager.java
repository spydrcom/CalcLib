
package net.myorb.math.expressions.gui.rpn;

import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.AbstractButton;

import java.awt.*;
import java.awt.event.*;

import java.util.Map;

/**
 * coordinate button function
 * @author Michael Druckman
 */
public class ButtonManager
{

	/**
	 * request operation performance from calling layer
	 */
	public interface OperationRequest
	{
		void perform (String operation);
	}

	/**
	 * map the button interactions
	 * @param component the base type of a managed button
	 * @param tip the tool tip to connect to the button
	 * @param key additional key mapping for this button
	 * @return the swing button object as an abstract
	 */
	public static AbstractButton newButton
		(
			MappedButtonComponent component,
			String tip, String key
		)
	{
		String name = component.getName ();				// this is the name of the button
		AbstractButton b = component.getButton ();		// the is the swing implementation of the button
		Map<String,Object> map = component.getMap ();	// the is the map collecting GUI component links
		map.put (name + "$ACT", component);				// this maps the name to the action listener
		if (key != null) map.put (key, b);				// additional mapping for hot key access
		b.setToolTipText (tip);							// add the tool tip to the button object
		map.put (name, b);								// map the button to its name
		return b;
	}
	public static AbstractButton newButton
	(MappedButtonComponent component, String tip)
	{
		return newButton (component, tip, null);
	}

	/**
	 * get the mapped request processing object
	 * @param map the map collecting the component links
	 * @return the request processor object
	 */
	public static OperationRequest getRequest (Map<String,Object> map)
	{
		return (OperationRequest) map.get ("REQUEST");
	}

	/**
	 * execute an operation using the request object
	 * @param map the map collecting the component links
	 * @param operation the name of the requested operation
	 */
	public static void executeRequest (Map<String,Object> map, String operation)
	{
		OperationRequest request = getRequest (map);

		if (request == null)
		{
			System.err.println ("Request object not found");
			return;
		}

		request.perform (operation);
		coreReset (map);
	}

	/**
	 * identify the GUI component that fields key strokes
	 * @param map the map collecting the component links
	 * @param c the component that processes key strokes
	 */
	public static void mapAsCore (Map<String,Object> map, Component c)
	{
		map.put (CORE_IDENTIFIER, c);
	}

	/**
	 * the focus must always be reset
	 *  to the key stroke processing object
	 * @param map the map collecting the component links
	 */
	public static void coreReset (Map<String,Object> map)
	{
		Component core = (Component) map.get (CORE_IDENTIFIER);
		core.setFocusable (true); core.requestFocusInWindow ();
	}
	public static final String CORE_IDENTIFIER = "CORE$COMPONENT";

}


/**
 * associate a button with a name and its link map
 */
class MappedButtonComponent extends ButtonManager implements ActionListener
{

	/**
	 * the name and map to link to the button
	 * @param map the map collecting the component links
	 * @param name the name assigned to this button
	 */
	MappedButtonComponent (Map<String,Object> map, String name)
	{ this.map = map; this.name = name; }

	/**
	 * button will use THIS object as action listener
	 * @param b the button implementation
	 * @return the button just linked
	 */
	AbstractButton setListener (AbstractButton b) { b.addActionListener (this); return b; }

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent arg0) {}

	/**
	 * get the map
	 * @return the link map
	 */
	Map<String,Object> getMap () { return map; }
	Map<String,Object> map;

	/**
	 * get the button
	 * @return the button object
	 */
	AbstractButton getButton () { return null; }

	/**
	 * get the name
	 * @return the name
	 */
	String getName () { return name; }
	String name;
}


/**
 * manager for a toggled button
 */
class Toggled extends MappedButtonComponent
{
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rpn.MappedButtonComponent#getButton()
	 */
	AbstractButton getButton ()
	{ return setListener (button = new JToggleButton (name)); }	
	Toggled (String name, Map<String,Object> map) { super (map, name); }
	JToggleButton button;
}


/**
 * a vanilla button
 */
class Button extends MappedButtonComponent
{
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rpn.MappedButtonComponent#getButton()
	 */
	AbstractButton getButton ()
	{ return setListener (button = new JButton (name)); }
	Button (Map<String,Object> map, String name) { super (map, name); }
	JButton button;
}


/**
 * vanilla button connected to the operation processor
 */
class CommonButton extends Button
{
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rpn.Button#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0)
	{ ButtonManager.executeRequest (map, altName); }
	CommonButton (Map<String,Object> map, String name, String altName) { super (map, name); this.altName = altName; }
	CommonButton (Map<String,Object> map, String name) { this (map, name, name); }
	String altName;
}

