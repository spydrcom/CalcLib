
package net.myorb.math.expressions.charting;

import net.myorb.charting.DisplayGraphTypes;

import net.myorb.gui.components.SimpleScreenIO.*;
import net.myorb.gui.components.SimpleScreenIO;

import javax.swing.*;

import java.awt.Color;
import java.awt.GridLayout;

public class LegendDisplay
{

	public static void show (DisplayGraphTypes.LegendWidgets legend)
	{
		Panel P = new Panel ();
		P.setLayout ( new GridLayout ( 0, 1 ) );
		P.setBackground (Color.WHITE);

		Widget [] widgets = legend.toArray (EMPTY);
		for (int n = widgets.length-1; n >= 0; n--)
		{
			P.add ( (JLabel) widgets [n] );
		}
		SimpleScreenIO.show ( P, "Legend", 50, 200 );
	}
	static final Widget [] EMPTY = new Widget[]{};

}
