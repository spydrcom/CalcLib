
package net.myorb.math.expressions.charting.colormappings;

import net.myorb.math.expressions.charting.ContourPlotProperties;
import net.myorb.gui.components.SimplePopupRequest;
import net.myorb.charting.ColorSelection;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

/**
 * present list of options for color scheme generators
 * @author Michael Druckman
 */
public class ContourColorSchemeRequest extends SimplePopupRequest<ColorSelection.Factory>
{


	/**
	 * @param called the name that will apply to this scheme
	 * @param scheme the implementation of the scheme
	 */
	public static void addScheme (String called, ColorSelection.Factory scheme)
	{
		SCHEMES.put (called, scheme);
	}
	static HashMap <String,ColorSelection.Factory> SCHEMES = new HashMap <> ();


	/**
	 * enumerated list of factory objects available
	 */
	static class ContourColorSchemes
		extends ArrayList<ColorSelection.Factory>
	{
		public ContourColorSchemes ()
		{ this.addAll (SCHEMES.values ()); }
		private static final long serialVersionUID = 5758872797449622910L;
	}

	/**
	 * @param scheme the name of the scheme to be assigned
	 */
	public static void identify (String scheme)
	{
		if ( ! SCHEMES.containsKey (scheme) )
		{ throw new RuntimeException ("Color scheme not found: " + scheme); }
		ContourPlotProperties.setColorSelectionFactory (SCHEMES.get (scheme));
	}


	/*
	 * description of the field
	 */

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.SimplePopupRequest#getOptions()
	 */
	public List<ColorSelection.Factory> getOptions () { return new ContourColorSchemes (); }

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.SimplePopupRequest#getFieldWidth()
	 */
	public int getFieldWidth () { return 20; }

	/*
	 * description of the frame
	 */

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.SimplePopupRequest#getFrameTitle()
	 */
	public String getFrameTitle () { return "Select Color Scheme Generator"; }

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.SimplePopupRequest#getFrameHeight()
	 */
	public int getFrameHeight () { return 50; }

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.SimplePopupRequest#getFrameWidth()
	 */
	public int getFrameWidth () { return 400; }

	/*
	 * feedback selection to user
	 */

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.SimplePopupRequest#formatNotificationFor(java.lang.Object)
	 */
	public String formatNotificationFor
		(ColorSelection.Factory selectedItem)
	{ return "'" + selectedItem + "' Will Now Be Used"; }

	/*
	 * identify selection to implementation
	 */

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.SimplePopupRequest#setSelectedItem(java.lang.Object)
	 */
	public void setSelectedItem (ColorSelection.Factory item)
	{ ContourPlotProperties.setColorSelectionFactory (item); }

	/*
	 * unit test
	 */

	/**
	 * unit test for menu
	 * @param args not used
	 */
	public static void main (String[] args)
	{
		new ContourColorSchemeRequest ();
	}

}

