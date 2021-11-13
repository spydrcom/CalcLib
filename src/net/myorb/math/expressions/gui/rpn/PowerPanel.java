
package net.myorb.math.expressions.gui.rpn;

import javax.swing.AbstractButton;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.GridLayout;

import java.util.Map;

public class PowerPanel extends ButtonManager
{


	public static JPanel getPanel (Map<String,Object> map)
	{
		JPanel panel = new JPanel ();
		panel.setLayout (new GridLayout(4,4));

		AbstractButton
		xiv = newButton (new CommonButton (map, "1/x", "INVERTED"), "Multiplicative inverse"),
		xsq = newButton (new CommonButton (map, "x^2", "SQUARED"), "Value squared"),
		xcb = newButton (new CommonButton (map, "x^3", "CUBED"), "Value cubed"),
		x2y = newButton (new CommonButton (map, "x^y", "^"), "Value to arbitrary power"),
		sqr = newButton (new CommonButton (map, "\u221A", "sqrt"), "Square root"),
		cbr = newButton (new CommonButton (map, "\u221B", "CROOT"), "Cube root"),
		nth = newButton (new CommonButton (map, "n\u221A", "\\"), "Nth root"),
		log2 = newButton (new CommonButton (map, "Log2"), "Binary log"),
		ln = newButton (new CommonButton (map, "Ln", "ln"), "Natural log"),
		log10 = newButton (new CommonButton (map, "Log10"), "Common log"),
		logb = newButton (new CommonButton (map, "LogB"), "Log base b (arbitrary base)"),
		twox = newButton (new CommonButton (map, "2^x", "TwoToX"), "Two to the x power"),
		ex = newButton (new CommonButton (map, "e^x", "exp"), "e to the x power"),
		tenx = newButton (new CommonButton (map, "10^x", "TenToX"), "10 to the x power"),
		ai = newButton (new CommonButton (map, "+|-", "NEGATE"), "Additive inverse", "~"),
		cis = newButton (new CommonButton (map, "e^iX", "cis"), "cos x + i sin x");

		panel.add (xiv); panel.add (ai);  panel.add (log2);  panel.add (twox);
		panel.add (xsq); panel.add (sqr); panel.add (ln);    panel.add (ex);
		panel.add (xcb); panel.add (cbr); panel.add (log10); panel.add (tenx);
		panel.add (x2y); panel.add (nth); panel.add (logb);  panel.add (cis);

		panel.setPreferredSize (new Dimension (300, 100));
		return panel;
	}


}

