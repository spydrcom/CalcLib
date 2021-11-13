
package net.myorb.math.expressions.controls;

import net.myorb.math.expressions.algorithms.JrePowerLibrary;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.math.HighSpeedMathLibrary;
import net.myorb.math.expressions.*;

/**
 * build an evaluation control object for Double floating data expressions
 * @author Michael Druckman
 */
public class FloatingEvaluationControl extends EvaluationControl<Double>
{

	/**
	 * evaluation requires a floating expression field manager
	 */
	public static ExpressionFloatingFieldManager
			mgr = new ExpressionFloatingFieldManager ();
	public static final String DOMAIN_TYPE = mgr.getName ();
	public static HighSpeedMathLibrary HSML = new HighSpeedMathLibrary ();
	public static JrePowerLibrary JPL = new JrePowerLibrary ();

	/**
	 * use expression field manager to instance evaluation control
	 * @param environment properties of the environment
	 * @param gui TRUE = start the GUI
	 */
	public FloatingEvaluationControl (Environment<Double> environment, Boolean gui)
	{
		super (environment, new SymbolTableManager<Double>(environment), gui);
		initializeFloatingSymbolTable ();
	}
	public FloatingEvaluationControl (Boolean gui)
	{
		//this (new Environment<Double>(mgr, HSML), gui);
		this (new Environment<Double>(mgr, JPL), gui);
	}
	public FloatingEvaluationControl () { this (true); }

	/**
	 * indicate that the high speed math library should be used
	 */
	public void initializeFloatingSymbolTable ()
	{
		useSpeedLibrary (); usePowerLibrary (HSML);
	}

	/**
	 * provide Runnable object as driver for this domain choice
	 * @return a Runnable entry point for this choice
	 */
	public static Runnable getDriver ()
	{
		return new Runnable ()
		{
			public void run ()
			{
				new FloatingEvaluationControl ();
			}
			public String toString () { return DOMAIN_TYPE; }
		};
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return "CALCLIB - " + DOMAIN_TYPE + " Domain";
	}

}
