
package net.myorb.math.expressions.gui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * use label component to display values with fluid changes
 * @author Michael Druckman
 */
public class TextLineDisplay
{

	/**
	 * allocate a label and format
	 * @return the new label object
	 */
	public static JLabel newDisplayLine ()
	{
		JLabel display = new JLabel ("   ", JLabel.RIGHT);
		display.setBorder (BorderFactory.createCompoundBorder
				(
					BorderFactory.createRaisedBevelBorder (),
					BorderFactory.createLoweredBevelBorder ())
				);
		display.setForeground (Color.BLACK);
		display.setBackground (Color.WHITE);
		return display;
	}

	/**
	 * build panel of lines
	 * @param lines number of lines to include
	 */
	void buildPanel (int lines)
	{
		panel = new JPanel ();
		GridBagLayout gridbag = new GridBagLayout ();
        GridBagConstraints c = new GridBagConstraints ();
		c.fill = GridBagConstraints.HORIZONTAL; c.gridx = 0; c.gridy = 0;
		panel.setPreferredSize (new Dimension (360, 75));
		panel.setLayout (gridbag);
		
		for (int i = 0; i < lines; i++)
		{
			JLabel l = newDisplayLine ();
			l.setPreferredSize (new Dimension (360, 25));
			panel.add (l, c); c.gridy++;
			l.setVisible (false);
			textLines.add (l);
		}
		panel.setVisible (false);
	}

	/**
	 * fill the panel with text from a list
	 * @param items the list of items to place in list
	 */
	@SuppressWarnings("rawtypes")
	public void fill (List items)
	{
		panel.setVisible (false);
		int n, max = textLines.size ();
		if (items == null || (n = items.size ()) == 0) return;

		for (int i = 0; i < max; i++)
		{
			textLines.get (i).setVisible (false);
		}

		for (int i = 0; i < n; i++)
		{
			if (max == i) break;
			JLabel l = textLines.get (i);
			l.setText (items.get (i).toString ());
			l.setVisible (true);
		}

		panel.setVisible (true);
	}
	List<JLabel> textLines;

	/**
	 * get the number of lines in this panel
	 * @return number of lines
	 */
	public int getSize () { return textLines.size (); }

	/**
	 * get the panel of text line objects
	 * @return the associated panel
	 */
	public JPanel getPanel ()
	{ return panel; }
	JPanel panel;

	/**
	 * construct panel of specified number of lines
	 * @param lines number of lines to be included
	 */
	public TextLineDisplay (int lines)
	{
		textLines = new ArrayList<JLabel>();
		buildPanel (lines);
	}

}
