
package net.myorb.math.expressions.gui;

import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.PrettyPrinter.MathMarkupRendering;

import net.myorb.gui.components.DisplayContainer;
import net.myorb.gui.components.GuiToolkit;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
//import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.GridLayout;
import java.util.List;
import java.awt.Color;

public class RenderDisplay
{


	/**
	 * display a frame for expression renders
	 */
	public static void showRenderFrame ()
	{
		if (f == null)
		{
			f = GuiToolkit.newDisplayContainer
				("Rendered Equations");
			p = new JPanel (new GridLayout (0, 1));
			f.getContentPane ().add (new JScrollPane (p));
			f.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
			GuiToolkit.setIcon (f, GuiToolkit.getIcon ("images/icon-render.gif"));
			p.setBackground (Color.WHITE);
			f.setSize (400, 1000);
		}
		f.setVisible (true);
	}
	static DisplayContainer f = null;
	static JPanel p = null;


	public static void display (List<TokenParser.TokenDescriptor> tokens) throws Exception
	{
		showRenderFrame ();
		String comment = tokens.get (0).getTokenImage ();
		JLabel label = new JLabel (comment.substring (1, comment.length() - 1), JLabel.CENTER);
		for (int i=1; i<tokens.size(); i++) apply (tokens.get (i).getTokenImage (), label);
		p.add (label);
	}
	static void apply (String option, JLabel label)
	{
		switch (option.charAt (0))
		{
		case 'T': label.setVerticalAlignment (JLabel.TOP); return;
		case 'C': label.setVerticalAlignment (JLabel.CENTER); return;
		case 'B': label.setVerticalAlignment (JLabel.BOTTOM); return;

		case 'o': label.setForeground (Color.ORANGE); return;
		case 'g': label.setForeground (Color.GRAY); return;
		case 'b': label.setForeground (Color.BLUE); return;
		case 'r': label.setForeground (Color.RED); return;

		default: label.setForeground (Color.getColor (option));
		}
	}


	/**
	 * add rendered MML to display frame
	 * @param mathMl the text of the markup for the expression
	 * @param ttText tool tip text for this section of tthe display
	 * @throws Exception for any errors
	 */
	public static void display (String mathMl, String ttText) throws Exception
	{
		try
		{
//			JComponent c; showRenderFrame ();
//			p.add (c = renderer.render (mathMl));
//			if (ttText != null) c.setToolTipText (ttText);
		}
		catch (Exception e)
		{
			System.out.println ("*** error " + ttText + e.getMessage());
			//e.printStackTrace();
		}
	}
	public static void setMathMarkupRenderer
	(MathMarkupRendering markupRenderer) { renderer = markupRenderer; }
	static MathMarkupRendering renderer = null;


}
