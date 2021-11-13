
package net.myorb.math.expressions.controls;

import net.myorb.math.HighSpeedMathLibrary;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.expressions.algorithms.ConfiguredEvaluationControl;

public class RefactoredControlTest extends ConfiguredEvaluationControl<Double>
{


	/**
	 * evaluation requires a floating expression field manager
	 */
	static ExpressionFloatingFieldManager mgr = new ExpressionFloatingFieldManager ();


	/**
	 * use expression field manager to instance evaluation control
	 * @param environment properties of the environment
	 * @param gui TRUE = start the GUI
	 */
	public RefactoredControlTest (Environment<Double> environment, Boolean gui)
	{
		super
		(
			environment,
			// new SymbolTableManager(environment), // parameter to deprecated constructor
			gui
		);
	}
	public RefactoredControlTest (Boolean gui)
	{
		this
		(
			new Environment<Double>
				(mgr, new HighSpeedMathLibrary ()),
			gui
		);
//		initializeSymbolTableFromSpaceManager ();
//		usePowerLibrary ();
	}
	public RefactoredControlTest () { this (true); }


	/**
	 * provide Runnable object as driver for this domain choice
	 * @return a Runnable entry point for this choice
	 */
	public static Runnable getDriver ()
	{
		return new Runnable ()
		{
			public void run () { new RefactoredControlTest (); }
			public String toString () { return DOMAIN_TYPE; }
		};
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{ return "CALCLIB (refactor) - " + DOMAIN_TYPE + " Domain"; }
	public static final String DOMAIN_TYPE = "Real Numbers (TEST)";


}

