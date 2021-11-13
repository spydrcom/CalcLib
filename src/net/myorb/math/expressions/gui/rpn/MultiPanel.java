
package net.myorb.math.expressions.gui.rpn;

import net.myorb.math.expressions.gui.*;
import net.myorb.math.expressions.gui.rpn.ButtonManager.OperationRequest;
import net.myorb.gui.components.*;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * compact GUI totally driven by tabbed group selection
 * @author Michael Druckman
 */
public class MultiPanel
{


	public interface RequestProcessor extends OperationRequest {}


	/**
	 * panel with tabbed pane as core component
	 * @param map a mapping of all components in the GUI
	 * @return the panel holding the tabbed pane
	 */
	public static JPanel getPanel (Map<String,Object> map)
	{
		Keypad kp = new Keypad ();
		JPanel panel = new JPanel ();
		JTabbedPane core = new JTabbedPane ();

		GridBagLayout gridbag = new GridBagLayout ();
        GridBagConstraints c = new GridBagConstraints ();
		c.fill = GridBagConstraints.HORIZONTAL; c.gridx = 0;
		panel.setLayout (gridbag);

		Map<String,Object> hotKeys = new HashMap<String,Object>();
		core.addKeyListener (new CoreListener (kp, core, hotKeys, map));
		ButtonManager.mapAsCore (map, core);

		Component k, e, t, i, p, u, s;								// the components that will have hot key selection
		p = core.add ("KeyPad", kp.getPanel (map));
		e = core.add ("Exponentiation", PowerPanel.getPanel (map));
		t = core.add ("Trigonometry", TrigPanel.getPanel (map));

		core.add ("Combinatorics", ComboPanel.getPanel (map));		// no hot keys, mouse selection only
		core.add ("Statistics", StatsPanel.getPanel (map));
		core.add ("Compare", ComparePanel.getPanel (map));
		core.add ("Logical", LogicPanel.getPanel (map));

		i = core.add ("Integer/Bit", BitOps.getPanel (map));		// these have hot keys
		k = core.add ("Constants", ConstantsPanel.getPanel (map));
		u = core.add ("User Def Symbols", UserDefined.getPanel (map));
		s = core.add ("Stack", StackOps.getPanel (map));

		c.gridy = 0; panel.add (core, c);							// the tabbed pane is the central focus of the App

		hotKeys.put (ControlKeys.F1, p); // keypad
		hotKeys.put (ControlKeys.F2, e); // exponentiation
		hotKeys.put (ControlKeys.F3, t); // Trigonometry
		hotKeys.put (ControlKeys.F4, i); // Integer					// these tabs can be selected by their Function keys
		hotKeys.put (ControlKeys.F5, k); // constants
		hotKeys.put (ControlKeys.F6, u); // User
		hotKeys.put (ControlKeys.F7, s); // Stack

		JLabel accumulatorDisplay =
			(JLabel) map.get ("AccumulatorDisplay");
		c.gridy++; panel.add (accumulatorDisplay, c);

		TextLineDisplay stackDisplay = new TextLineDisplay (3);
		c.gridy++; panel.add (stackDisplay.getPanel (), c);
		map.put ("StackDisplay", stackDisplay);

		panel.setPreferredSize (new Dimension (360, 275));
		return panel;
	}


	/**
	 * present the RPN GUI as a frame
	 * @param request an operation request object
	 * @param map the map of the GUI components initially holding the request objects
	 */
	public static void presentRequestDialog
	(RequestProcessor request, Map<String,Object> map)
	{
		map.put ("REQUEST", request);

		new DisplayFrame (getPanel (map), "RPN").show ();
	}


	public static void presentTestFrame ()
	{
		final Map<String,Object> map = new HashMap<String,Object> ();

		presentRequestDialog
		(
			new RequestProcessor ()
			{
				public void perform (String operation)
				{
					System.out.println (operation + " - " + map.get ("RADIX"));
				}
			},
			map
		);
	}


	/**
	 * unit test
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		presentTestFrame ();
	}


}


/**
 * field events for hot keys
 * @author Michael Druckman
 */
class CoreListener extends KeyAdapter
{


	public CoreListener
		(
			Keypad pad, JTabbedPane core,
			Map<String, Object> hotKeys,
			Map<String, Object> guiMap
		)
	{
		this.pad = pad; this.core = core;
		this.hotKeys = hotKeys; this.guiMap = guiMap;
	}
	Map<String,Object> hotKeys, guiMap;
	Keypad pad; JTabbedPane core;


	/* (non-Javadoc)
	 * @see java.awt.event.KeyAdapter#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped (KeyEvent e)
	{
//		System.out.println
//		(
//				"TYP - '" + e.getKeyChar() +
//				"' [" + Integer.toString(e.getKeyChar()) +
//				", " + e.getKeyCode() +
//				"] - MOD " + e.getModifiers ()
//		);
		String pressed = ("" + e.getKeyChar ()).toUpperCase ();
		pad.processHotkey (pressed);
	}


	/* (non-Javadoc)
	 * @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e)
	{
//		System.out.println
//		(
//				"PRS - '" + e.getKeyChar() +
//				"' [" + Integer.toString(e.getKeyChar()) +
//				", " + e.getKeyCode() +
//				"] - MOD " + e.getModifiers ()
//		);
		//if (e.getKeyChar() != 65535) return;

		Object o;
		String symbol = ControlKeys.CHARACTER_MAP.get (e.getKeyCode ());
		if ((o = hotKeys.get (symbol)) != null) core.setSelectedComponent ((Component)o);
		else if (symbol != null) ButtonManager.executeRequest (guiMap, symbol);
	}

}

