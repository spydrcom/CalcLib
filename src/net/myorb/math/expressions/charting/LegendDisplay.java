
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.charting.colormappings.TemperatureModelColorScheme;

import net.myorb.gui.components.SimpleScreenIO.Widget;
import net.myorb.gui.components.SimpleScreenIO.Label;
import net.myorb.gui.components.SimpleScreenIO.Panel;
import net.myorb.gui.components.SimpleScreenIO;

import net.myorb.charting.DisplayGraphTypes.ScaledColorSelector;
import net.myorb.charting.DisplayGraphTypes;
import net.myorb.charting.Histogram;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.*;

/**
 * swing structure for Legend display
 * @author Michael Druckman
 */
public class LegendDisplay
{


	/**
	 * show frame holding Legend
	 * @param legend pre-built components to be shown
	 */
	public static void show
		(DisplayGraphTypes.LegendWidgets legend)
	{
		int size = 690;
		int vert = 0; Component C; String text;
		int N, H = size / (N = legend.size () - 1), W = 100;
		BufferedImage image = new BufferedImage (W, size-10, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics ();

		for (int r = N; r > 0; r--)
		{
			C = legend.get (r).toComponent (); text = ( (Label) C ).getText ();
			g.setColor (C.getForeground ()); g.fillRect (0, vert, W, H);
			g.setColor (Color.WHITE); g.drawString (text, 5, vert+35);
			vert += H - 1;
		}

		Panel P = new Panel ();
		P.add (new Label (new ImageIcon (image)));
		P.setPreferredSize ( new Dimension ( 60, 400 ) );
		SimpleScreenIO.show ( P, "Legend", 0, size );
	}


	/*
	 * unit test with static histogram
	 */

	public static void main (String [] args)
	{
		Histogram histogram;
		( histogram = new Histogram () ).setRange (100, 1);
		ScaledColorSelector selector = new TemperatureModelColorScheme ();
		DisplayGraphTypes.LegendEntries e = DisplayGraphTypes.legendEntriesFor (10, histogram);
		DisplayGraphTypes.LegendWidgets legend = DisplayGraphTypes.legendWidgetsFor (e, 1, selector);
		show (legend);
	}


	/*
	 * original version using swing components
	 */

	public static void show
		(
			DisplayGraphTypes.LegendWidgets legend,
			boolean originalVersion
		)
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

