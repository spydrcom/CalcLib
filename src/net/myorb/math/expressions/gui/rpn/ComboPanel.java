
package net.myorb.math.expressions.gui.rpn;

import javax.swing.AbstractButton;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.GridLayout;

import java.util.Map;

public class ComboPanel extends ButtonManager
{


	public static JPanel getPanel (Map<String,Object> map)
	{
		JPanel panel = new JPanel ();
		panel.setLayout (new GridLayout(2,4));

		AbstractButton
		fact = newButton (new CommonButton (map, "!"), "Factorial"),
		ffact = newButton (new CommonButton (map, "#/"), "Falling factorial"),
		rfact = newButton (new CommonButton (map, "/#"), "Raising factorial"),
		bc = newButton (new CommonButton (map, "(n##m)", "##"), "Binomial coefficient"),
		gamma = newButton (new CommonButton (map, "\u0393", "GAMMA"), "Gamma function"),
		logGamma = newButton (new CommonButton (map, "log\u0393", "LOGGAMMA"), "log Gamma"),
		zeta = newButton (new CommonButton (map, "\u03B6(x)", "zeta"), "Zeta function"),
		hx = newButton (new CommonButton (map, "H(x)"), "Harmonic numbers");

		panel.add (bc);  panel.add (ffact);  panel.add (rfact); panel.add (fact);
		panel.add (gamma);  panel.add (logGamma);  panel.add (hx); panel.add (zeta);

		panel.setPreferredSize (new Dimension (300, 100));
		return panel;
	}


}
