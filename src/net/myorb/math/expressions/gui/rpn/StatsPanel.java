
package net.myorb.math.expressions.gui.rpn;

import javax.swing.AbstractButton;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.GridLayout;

import java.util.Map;

public class StatsPanel extends ButtonManager
{

	public static JPanel getPanel (Map<String,Object> map)
	{
		JPanel panel = new JPanel ();
		panel.setLayout (new GridLayout(2,4));

		AbstractButton
		mean = newButton (new CommonButton (map, "Mean"), "Arithmetic mean"),
		median = newButton (new CommonButton (map, "Median"), "Median value"),
		var = newButton (new CommonButton (map, "Var"), "Variance of samples"),
		cov = newButton (new CommonButton (map, "Cov"), "Covariance of samples"),
		sum = newButton (new CommonButton (map, "Sum"), "Sum of Samples"),
		sumSq = newButton (new CommonButton (map, "Sum ^2"), "Sum of Squares"),
		mDev = newButton (new CommonButton (map, "Dev"), "Mean Deviation"),
		sDev = newButton (new CommonButton (map, "Std Dev"), "Standard Deviation");

		panel.add (sum);   panel.add (mean);   panel.add (var);  panel.add (mDev);
		panel.add (sumSq); panel.add (median); panel.add (cov);  panel.add (sDev);

		panel.setPreferredSize (new Dimension (300, 100));
		return panel;
	}

}