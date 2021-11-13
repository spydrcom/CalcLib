
package net.myorb.math.expressions.gui.rpn;

import javax.swing.AbstractButton;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.GridLayout;

import java.util.Map;

public class LogicPanel extends ButtonManager
{


	public static JPanel getPanel (Map<String,Object> map)
	{
		JPanel panel = new JPanel ();
		panel.setLayout (new GridLayout(4,4));

		AbstractButton
		t = newButton (new CommonButton (map, "TRUE"), "TRUE (independent of variables)"),
		f = newButton (new CommonButton (map, "FALSE"), "FALSE (independent of variables)"),
		x = newButton (new CommonButton (map, "X"), "X (independent of right side variable)"),
		y = newButton (new CommonButton (map, "Y"), "Y (independent of left side variable)"),
		notX = newButton (new CommonButton (map, "~X"), "not X (independent of right side variable)"),
		notY = newButton (new CommonButton (map, "~Y"), "not Y (independent of left side variable)"),
		and = newButton (new CommonButton (map, "&"), "x AND y"),
		nand = newButton (new CommonButton (map, "~&"), "x NAND y"),
		or = newButton (new CommonButton (map, "|"), "x OR y"),
		nor = newButton (new CommonButton (map, "~|"), "x NOR y"),
		xor = newButton (new CommonButton (map, "|~"), "x XOR y"),
		xnor = newButton (new CommonButton (map, "~|~"), "x NXOR y"),
		implies = newButton (new CommonButton (map, "=>>"), "x IMPLIES y"),
		notImplies = newButton (new CommonButton (map, "~=>>"), "x DOES NOT IMPLY y"),
		impliedBy = newButton (new CommonButton (map, "<<="), "x IMPLIED BY y"),
		notImpliedBy = newButton (new CommonButton (map, "~<<="), "x NOT IMPLIED BY y");

		panel.add (t);   panel.add (f);   	panel.add (and);  panel.add (nand);
		panel.add (x);   panel.add (notX);  panel.add (or);   panel.add (nor);
		panel.add (y);   panel.add (notY);  panel.add (xor);  panel.add (xnor);
		panel.add (implies); panel.add (notImplies); panel.add (impliedBy); panel.add (notImpliedBy);

		panel.setPreferredSize (new Dimension (300, 100));
		return panel;
	}


}
