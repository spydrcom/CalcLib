
package net.myorb.math.expressions.gui;

import net.myorb.data.abstractions.ValueDisplayProperties;

import java.io.PrintStream;

/**
 * set the display mode
 * @author Michael Druckman
 */
public class DisplayModeForm extends FormComponents implements FormComponents.Form
{


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.FormComponents.Form#publish()
	 */
	public void publish ()
	{
		ValueDisplayProperties.DEFAULT_DISPLAY_PRECISION = Integer.parseInt (precision.getText ());
		if (mode != null) ValueDisplayProperties.DEFAULT_DISPLAY_MODE = mode.getSelectedItem ().toString ();
		out.println ("Mode set to: " + ValueDisplayProperties.DEFAULT_DISPLAY_MODE + " " + ValueDisplayProperties.DEFAULT_DISPLAY_PRECISION);
		out.println (); out.println ();
	}
	private Field precision;


	/**
	 * @param form the form that implements the publish
	 * @param modes the list of available display modes
	 * @return the panel containing the form
	 */
	public Panel getPanel (DisplayModeForm form, String[] modes)
	{
		Panel p = new Panel ();
		addLabel (p, "Precision");
		String digits = Integer.toString (ValueDisplayProperties.DEFAULT_DISPLAY_PRECISION);
		precision = newSelectedDefaultField (5, digits, p);
		if (modes != null) setModeOptions (p, modes);
		p.add (getButtonPanel (form));
		return p;
	}
	public void setModeOptions (Panel p, String[] modes)
	{ (mode = addCombo (p, modes)).setSelectedItem (ValueDisplayProperties.DEFAULT_DISPLAY_MODE); }
	private ComboBox<String> mode = null;


	/**
	 * @param modes the list of available display modes
	 */
	public DisplayModeForm (String[] modes) { showFrame (getPanel (this, modes), "Set Display Mode"); }
	public DisplayModeForm (String[] modes, PrintStream out) { this (modes); this.out = out; }
	private PrintStream out = System.out;


	/**
	 * a unit test for the form
	 * @param args not used
	 */
	public static void main (String... args)
	{
		//new DisplayModeForm (null);
		new DisplayModeForm (new String[]{"A","B","Decimal", "Mixed", "blah"});
	}


}

