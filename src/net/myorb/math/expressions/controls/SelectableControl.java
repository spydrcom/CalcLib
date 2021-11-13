
package net.myorb.math.expressions.controls;

import javax.swing.JOptionPane;

/**
 * main entry allowing domain type selection
 * @author Michael Druckman
 */
public class SelectableControl
{
	public static final Object[] CHOICES =
		new Object[]
		{
			FloatingEvaluationControl.getDriver (),
			FactorizedEvaluationControl.getDriver (),	// each driver describes a different mathematical domain
			ComplexEvaluationControl.getDriver (),
			RefactoredControlTest.getDriver ()
		};
	public static void main(String[] args)
	{
		Object choice = JOptionPane.showInputDialog		// swing dialog implements selection
		(
			null, "Select Domain", "CALCLIB",
			JOptionPane.PLAIN_MESSAGE,
			null, CHOICES, ""
		);
		if (choice == null) return;
		((Runnable)choice).run ();						// driver objects implement Runnable
	}
}
