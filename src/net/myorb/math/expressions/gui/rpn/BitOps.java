
package net.myorb.math.expressions.gui.rpn;

import javax.swing.AbstractButton;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.GridLayout;

import java.util.Map;

public class BitOps extends ButtonManager
{

	public static JPanel getPanel (Map<String,Object> map)
	{
		JPanel panel = new JPanel ();
		panel.setLayout (new GridLayout(2,4));

		AbstractButton
		lshift = newButton (new CommonButton (map, "<<"), "Left shift"),
		rshift = newButton (new CommonButton (map, ">>"), "Right shift"),
		ceil = newButton (new CommonButton (map, "CEIL"), "Ceiling operator"),
		floor = newButton (new CommonButton (map, "FLOOR"), "Floor operator"),
		abs = newButton (new CommonButton (map, "abs"), "Absolute value operator"),
		sgn = newButton (new CommonButton (map, "sgn"), "Sign operator"),
		mod = newButton (new CommonButton (map, "mod"), "Modulo operator"),
		rem = newButton (new CommonButton (map, "%"), "Remainder of division");

		panel.add (lshift);  panel.add (mod);  panel.add (abs);  panel.add (ceil);
		panel.add (rshift);  panel.add (rem);  panel.add (sgn);  panel.add (floor);

		panel.setPreferredSize (new Dimension (300, 100));
		return panel;
	}

}