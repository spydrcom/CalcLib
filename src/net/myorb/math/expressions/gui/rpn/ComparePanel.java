
package net.myorb.math.expressions.gui.rpn;

import javax.swing.AbstractButton;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.GridLayout;

import java.util.Map;

public class ComparePanel extends ButtonManager
{


	public static JPanel getPanel (Map<String,Object> map)
	{
		JPanel panel = new JPanel ();
		panel.setLayout (new GridLayout(2,3));

		AbstractButton
		lt = newButton (new CommonButton (map, "<"), "Less than"),
		le = newButton (new CommonButton (map, "<="), "Less than or equal to"),
		eq = newButton (new CommonButton (map, "="), "Equal to"),
		ne = newButton (new CommonButton (map, "~="), "Not equal to"),
		gt = newButton (new CommonButton (map, ">"), "Greater than"),
		ge = newButton (new CommonButton (map, ">="), "Greater than or equal to");

		panel.add (lt);  panel.add (le);  panel.add (eq);
		panel.add (gt);  panel.add (ge);  panel.add (ne);

		panel.setPreferredSize (new Dimension (300, 100));
		return panel;
	}


}
