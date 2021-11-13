
package net.myorb.math.expressions.gui.rpn;

import net.myorb.math.expressions.gui.*;
import net.myorb.math.expressions.*;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;

import java.util.Map;

/**
 * data entry and modes of processing
 * @author Michael Druckman
 */
public class Keypad extends ButtonManager
{


	/**
	 * the keys of the keypad
	 * @param digitMap a map of the components
	 */
	public void buildKeySet (Map<String,Object> digitMap)
	{
		D1  = newButton (new CommonButton (digitMap, "1"), "1");
		D2  = newButton (new CommonButton (digitMap, "2"), "2");
		D3  = newButton (new CommonButton (digitMap, "3"), "3");
		D4  = newButton (new CommonButton (digitMap, "4"), "4");
		D5  = newButton (new CommonButton (digitMap, "5"), "5");
		D6  = newButton (new CommonButton (digitMap, "6"), "6");
		D7  = newButton (new CommonButton (digitMap, "7"), "7");
		D8  = newButton (new CommonButton (digitMap, "8"), "8");
		D9  = newButton (new CommonButton (digitMap, "9"), "9");
		D0  = newButton (new CommonButton (digitMap, "0"), "0");
		DA  = newButton (new CommonButton (digitMap, "A"), "A");
		DB  = newButton (new CommonButton (digitMap, "B"), "B");
		DC  = newButton (new CommonButton (digitMap, "C"), "C");
		DD  = newButton (new CommonButton (digitMap, "D"), "D");
		DE  = newButton (new CommonButton (digitMap, "E"), "E");
		DF  = newButton (new CommonButton (digitMap, "F"), "F");
		neg = newButton (new CommonButton (digitMap, "+|-", "NEGATE"), "negate value", "~");
		dot = newButton (new CommonButton (digitMap, "."), ".");
		hotKeyFilter = digitMap;
		neg.setEnabled (false);
	}


	public static final String[] ALL_DIGITS = new String[]
	{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
	public static void reset (Map<String,Object> map) { set (true, map, ALL_DIGITS); }
	public static void set (boolean state, Map<String,Object> buttonMap, String... items)
	{ for (String name : items) ((AbstractButton) buttonMap.get (name)).setEnabled (state); }
	private AbstractButton D0, D1, D2, D3, D4, D5, D6, D7, D8, D9;
	private AbstractButton DA, DB, DC, DD, DE, DF, neg, dot;


	/**
	 * build keypad 0-9
	 * @param panel the panel to insert to
	 */
	public void makeDecimalPanel (JPanel panel)
	{
		panel.removeAll ();
		panel.setVisible (false);
		panel.setLayout (new GridLayout(4,3));
		panel.add (D7);  panel.add (D8); panel.add (D9);
		panel.add (D4);  panel.add (D5); panel.add (D6);
		panel.add (D1);  panel.add (D2); panel.add (D3);
		panel.add (dot); panel.add (D0); panel.add (neg);
		panel.setVisible (true);
		panel.repaint ();
	}
	public void useDecimalPanel (Map<String,Object> map, String... except)
	{
		reset (map);
		makeDecimalPanel (DIGITS);
		set (false, map, "A", "B", "C", "D", "E", "F");
		set (false, map, except);
		MASTER.repaint ();
	}


	/**
	 * build keypad 0-9 and A-F
	 * @param panel the panel to insert to
	 */
	public void makeHexidecimalPanel (JPanel panel)
	{
		panel.removeAll ();
		panel.setVisible (false);
		panel.setLayout (new GridLayout(4,4));
		panel.add (D0); panel.add (D1); panel.add (D2); panel.add (D3);
		panel.add (D4); panel.add (D5); panel.add (D6); panel.add (D7);
		panel.add (D8); panel.add (D9); panel.add (DA); panel.add (DB);
		panel.add (DC); panel.add (DD); panel.add (DE); panel.add (DF);
		panel.setVisible (true);
		panel.repaint ();
	}
	public void useHexidecimalPanel (Map<String,Object> map)
	{
		reset (map);
		makeHexidecimalPanel (DIGITS);
		MASTER.repaint ();
	}


	/**
	 * filter for keyboard entries
	 * @param input the key that was pressed
	 */
	public void processHotkey (String input)
	{
		Object o = hotKeyFilter.get (input);
		if (o != null && o instanceof AbstractButton && ((AbstractButton)o).isEnabled ())
		{ ButtonManager.executeRequest (hotKeyFilter, ((AbstractButton)o).getText ()); }
	}
	Map<String,Object> hotKeyFilter;


	/**
	 * construct the full keypad panel
	 * @param map a map of the collected components
	 * @return a panel for the display
	 */
	public JPanel getPanel (Map<String,Object> map)
	{
		buildKeySet (map);
		map.put ("KEYPAD", this);
		GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;

		JPanel panel = new JPanel ();
		panel.setLayout (gridbag);
		MASTER = panel;

		AbstractButton bin, oct, dec, hex;
		ButtonGroup group = new ButtonGroup ();
		group.add (bin = newButton (new BinButton (map), "Binary"));
		group.add (oct = newButton (new OctButton (map), "Octal"));
		group.add (dec = newButton (new DecButton (map), "Decimal"));
		group.add (hex = newButton (new HexButton (map), "Hexidecimal"));
		map.put ("RADIX", "10"); dec.setSelected (true);

		JPanel radixPanel = new JPanel ();
		radixPanel.setLayout (new GridLayout(4,1));
		radixPanel.add (bin); radixPanel.add (oct);
		radixPanel.add (dec); radixPanel.add (hex);
		gridbag.setConstraints (radixPanel, c);
		panel.add (radixPanel);

		DIGITS = new JPanel ();
		gridbag.setConstraints (DIGITS, c);
		useDecimalPanel (map);
		panel.add (DIGITS);

		AbstractButton
		plus = newButton (new CommonButton (map, "+"), "add"),
		minus = newButton (new CommonButton (map, "-"), "subtract"),
		times = newButton (new CommonButton (map, "*"), "multiply"),
		over = newButton (new CommonButton (map, "/"), "divide");
		plus.setEnabled (false); minus.setEnabled (false);
		times.setEnabled (false); over.setEnabled (false);

		JPanel opsPanel = new JPanel ();
		opsPanel.setLayout (new GridLayout(4,1));
		opsPanel.add (plus); opsPanel.add (minus);
		opsPanel.add (times); opsPanel.add (over); 
		gridbag.setConstraints (opsPanel, c);
		panel.add (opsPanel);

		AbstractButton
		clear = newButton (new CommonButton (map, "CE"), "Clear Entry"),
		clearAll = newButton (new CommonButton (map, "ALL"), "Clear All"),
		sciNot = newButton (new CommonButton (map, "*10^"), "Scientific Notation decimal point shift"),
		xch = newButton (new CommonButton (map, "<>", "Xch"), "Exchange top");
		clearAll.setEnabled (false); clear.setEnabled (false);
		sciNot.setEnabled (false); xch.setEnabled (false);

		JPanel editsPanel = new JPanel ();
		editsPanel.setLayout (new GridLayout(4,1));
		editsPanel.add (clear); editsPanel.add (clearAll);
		editsPanel.add (xch); editsPanel.add (sciNot); 
		gridbag.setConstraints (editsPanel, c);
		panel.add (editsPanel);

		AccumulatorMonitorForKeypad.connect (map);
		panel.setPreferredSize (new Dimension (350, 100));
		return panel;
	}
	JPanel MASTER, DIGITS;


}


/**
 * base class for handling radix processing
 */
class Radix extends Toggled
{
	Radix (Map<String,Object> map, String name)
	{ super (name, map); kp = (Keypad) map.get ("KEYPAD"); }
	Keypad kp;
}

/**
 * binary radix selection
 */
class BinButton extends Radix
{
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rpn.MappedButtonComponent#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		kp.useDecimalPanel (map, "2", "3", "4", "5", "6", "7", "8", "9");
		map.put ("RADIX", "2"); executeRequest (map, "BIN");
	}
	BinButton (Map<String,Object> map) { super (map, "BIN"); }
}

/**
 * octal radix selection
 */
class OctButton extends Radix
{
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rpn.MappedButtonComponent#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		kp.useDecimalPanel (map, "8", "9");
		map.put ("RADIX", "8"); executeRequest (map, "OCT");
	}
	OctButton (Map<String,Object> map) { super (map, "OCT"); }
}

/**
 * decimal radix selection
 */
class DecButton extends Radix
{
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rpn.MappedButtonComponent#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{ kp.useDecimalPanel (map); map.put ("RADIX", "10"); executeRequest (map, "DEC"); }
	DecButton (Map<String,Object> map) { super (map, "DEC"); }
}

/**
 * hex radix selection
 */
class HexButton extends Radix
{
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rpn.MappedButtonComponent#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{ kp.useHexidecimalPanel (map); map.put ("RADIX", "16"); executeRequest (map, "HEX"); }
	HexButton (Map<String,Object> map) { super (map, "HEX"); }
}

/**
 * connect accumulator to keypad panel
 */
class AccumulatorMonitorForKeypad implements Accumulator.AccumulatorStatusMonitor, ValueStack.StackStatusMonitor
{

	@SuppressWarnings("rawtypes")
	public static void connect (Map<String, Object> guiMap)
	{
		AccumulatorMonitorForKeypad
		monitor = new AccumulatorMonitorForKeypad (guiMap);
		guiMap.put ("AccumulatorDisplay", monitor.getAccumulatorDisplay ());
		ValueStack valueStack = (ValueStack) guiMap.get ("valueStack");
		Accumulator accumulator = (Accumulator) guiMap.get ("ACCUMULATOR");
		accumulator.setAccumulatorStatusMonitor (monitor);
		valueStack.addStackListener (monitor);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rpn.Accumulator.AccumulatorStatusMonitor#updateAccumulatorValue(java.lang.String)
	 */
	public void updateAccumulatorValue (String newValue)
	{
		if (newValue.length() == 0) newValue = " ";
		accumulatorDisplay.setText (newValue);
	}
	public JLabel getAccumulatorDisplay ()
	{
		accumulatorDisplay = TextLineDisplay.newDisplayLine ();
		return accumulatorDisplay;
	}
	protected JLabel accumulatorDisplay;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rpn.Accumulator.AccumulatorStatusMonitor#updateAccumulatorStatus(boolean)
	 */
	public void updateAccumulatorStatus (boolean accumulatorIsActive)
	{
		ce.setEnabled (accumulatorIsActive); // entry must be present
		sn.setEnabled (accumulatorIsActive);
		
		boolean accumulatorIsInactive = !accumulatorIsActive;

		dec.setEnabled (accumulatorIsInactive); // no radix change
		bin.setEnabled (accumulatorIsInactive); // while accum active
		oct.setEnabled (accumulatorIsInactive);
		hex.setEnabled (accumulatorIsInactive);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ValueStack.StackStatusMonitor#stackSizeIs(int)
	 */
	public void stackSizeIs (int entries)
	{
		p.setEnabled (entries >= 2);
		m.setEnabled (entries >= 2);
		t.setEnabled (entries >= 2);
		d.setEnabled (entries >= 2);
		dlt.setEnabled (entries >= 2);
		all.setEnabled (entries >= 1);
		n.setEnabled (entries >= 1);
	}

	/**
	 * find named buttons in GUI map
	 */
	public void identifyButtons ()
	{
		dlt = (AbstractButton) guiMap.get ("<>");	// exchange
		all = (AbstractButton) guiMap.get ("ALL");	// clear stack
		ce  = (AbstractButton) guiMap.get ("CE");	// clear entry
		sn  = (AbstractButton) guiMap.get ("*10^");	// scientific notation
		hex = (AbstractButton) guiMap.get ("HEX");	// hexidecimal radix
		dec = (AbstractButton) guiMap.get ("DEC");	// decimal radix
		bin = (AbstractButton) guiMap.get ("BIN");	// binary radix
		oct = (AbstractButton) guiMap.get ("OCT");	// octal radix
		p   = (AbstractButton) guiMap.get ("+");	// +
		m   = (AbstractButton) guiMap.get ("-");	// -
		t   = (AbstractButton) guiMap.get ("*");	// *
		d   = (AbstractButton) guiMap.get ("/");	// /
		n   = (AbstractButton) guiMap.get ("+|-");	// neg
	}
	AbstractButton sn, dec, bin, oct, hex, ce, dlt, all;
	AbstractButton p, m, t, d, n;

	public AccumulatorMonitorForKeypad (Map<String, Object> guiMap)
	{ this.guiMap = guiMap; identifyButtons (); }
	Map<String,Object> guiMap;

}

