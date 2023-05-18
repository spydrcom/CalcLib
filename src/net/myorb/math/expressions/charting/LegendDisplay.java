
package net.myorb.math.expressions.charting;

import net.myorb.charting.DisplayGraphTypes;

import net.myorb.gui.components.SimpleScreenIO.Widget;
import net.myorb.gui.components.SimpleScreenIO.Panel;
import net.myorb.gui.components.SimpleScreenIO;

import javax.swing.*;
import java.awt.*;

/**
 * swing structure for Legend display
 * @author Michael Druckman
 */
public class LegendDisplay
{

	/**
	 * @param legend pre-built components to be shown
	 */
	public static void show (DisplayGraphTypes.LegendWidgets legend)
	{
		JLabel L;
		Panel widget = new Panel ();
		JPanel inner, P = new JPanel ();
		P.setLayout ( new GridLayout ( 0, 1 ) );
		P.setBackground (Color.WHITE);

		Widget [] widgets = legend.toArray (EMPTY);
		for (int n = widgets.length-1; n >= 0; n--)
		{
			( inner = new Panel () )
				.add ( L = (JLabel) widgets [n] );
			inner.setBackground ( L.getForeground () );
			L.setPreferredSize ( new Dimension ( 100, 50 ) );
			P.add ( inner, JPanel.CENTER_ALIGNMENT );
			L.setForeground ( Color.WHITE );
		}

		widget.add ( P ); widget.setBackground ( Color.WHITE );
		widget.setPreferredSize ( new Dimension ( 60, 400 ) );

		SimpleScreenIO.show ( widget, "Legend", 60, 500 );
	}
	static final Widget [] EMPTY = new Widget[]{};

}
