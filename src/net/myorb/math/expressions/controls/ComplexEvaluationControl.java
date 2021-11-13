
package net.myorb.math.expressions.controls;

import net.myorb.math.expressions.EvaluationControl;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.managers.*;
import net.myorb.math.complexnumbers.*;

/**
 * build an evaluation control object for complex data expressions
 * @author Michael Druckman
 */
public class ComplexEvaluationControl
	extends EvaluationControl<ComplexValue<Double>>
{


	public static ExpressionComplexFieldManager
		manager = new ExpressionComplexFieldManager ();
	public static final String DOMAIN_TYPE = manager.getName ();


	/**
	 * use expression field manager to instance evaluation control
	 * @param environment properties of the environment
	 */
	public ComplexEvaluationControl (Environment<ComplexValue<Double>> environment)
	{
		super
		(
			environment,
			new ComplexSymbolTableManager (environment),
			true
		);
		engine.getKeywordMap ().addComplexKeywordMap ();
		initializeComplexSymbolTable ();
	}
	public ComplexEvaluationControl ()
	{
		this (new Environment<ComplexValue<Double>> (manager));
		manager.setEvaluationControl (this);
	}


	/**
	 * indicate that the optimized complex library should be used
	 */
	public void initializeComplexSymbolTable ()
	{
		HSComplexSupportImplementation hsml = new HSComplexSupportImplementation ();
		usePowerLibrary (new OptimizedComplexLibrary (hsml));
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
				new ComplexEvaluationControl ();
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

