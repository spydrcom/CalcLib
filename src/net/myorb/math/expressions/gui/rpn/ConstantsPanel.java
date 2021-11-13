
package net.myorb.math.expressions.gui.rpn;

import javax.swing.AbstractButton;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.GridLayout;

import java.util.Map;

public class ConstantsPanel extends ButtonManager
{


	public static JPanel getPanel (Map<String,Object> map)
	{
		JPanel panel = new JPanel ();
		panel.setLayout (new GridLayout(3,4));

		AbstractButton
		b = newButton (new CommonButton (map, "Bn"), "Bernoulli numbers"),
		h = newButton (new CommonButton (map, "Hn"), "Harmonic numbers"),
		k = newButton (new CommonButton (map, "\u039A", "KAPPA"), "Catalan's constant"),
		p = newButton (new CommonButton (map, "\u03A1", "RHO"), "universal parabolic constant"),
		g = newButton (new CommonButton (map, "\u03D2", "gamma"), "Euler/Masheroni constant"),
		phi = newButton (new CommonButton (map, "\u03C6", "phi"), "golden ration constant"),
		psi = newButton (new CommonButton (map, "\u03C8", "psi"), "reciprocal Fibonacci constant"),
		l = newButton (new CommonButton (map, "\u03BB", "lambda"), "Conway's lambda constant"),
		a = newButton (new CommonButton (map, "\u03B6", "Apery"), "Apery's zeta(3) constant"),
		i = newButton (new CommonButton (map, "\u03AF", "i"), "y-axis unit for complex numbers"),
		e = newButton (new CommonButton (map, "\u0435", "e"), "irrational value of e"),
		pi = newButton (new CommonButton (map, "\u03C0", "pi"), "irrational value of pi");

		panel.add (b);  panel.add (phi); panel.add (p);  panel.add (e);
		panel.add (a);  panel.add (g);   panel.add (l);  panel.add (pi);
		panel.add (h);  panel.add (psi); panel.add (k);  panel.add (i);

		panel.setPreferredSize (new Dimension (300, 100));
		return panel;
	}


}
