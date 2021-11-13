
package net.myorb.math.expressions.controls;

import net.myorb.math.primenumbers.*;
import net.myorb.math.primenumbers.sieves.SieveOfEratosthenes;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.EvaluationControl;
import net.myorb.math.expressions.managers.*;
import net.myorb.math.ExponentiationLib;

/**
 * evaluation control object for Factorization data
 * @author Michael Druckman
 */
public class FactorizedEvaluationControl extends EvaluationControl<Factorization>
{

	/**
	 * size to allocate for a static primes table
	 */
	public static final int FACTORIZATION_TABLE_SIZE = 1000 * 1000;

	public static ExpressionFactorizedFieldManager mgr =
		new ExpressionFactorizedFieldManager ();
	public static final String DOMAIN_TYPE = mgr.getName ();

	/**
	 * use expression field manager to instance evaluation control
	 * @param environment the environment object for this engine
	 */
	public FactorizedEvaluationControl (Environment<Factorization> environment)
	{
		super
		(
			environment
		);
		initializeLibrary ();
		this.environment = environment;
	}
	public FactorizedEvaluationControl ()
	{
		this (new Environment<Factorization> (mgr));
	}

	/**
	 * indicate that the optimized library should be used
	 */
	public void initializeLibrary ()
	{
		usePowerLibrary (new ExponentiationLib<Factorization> (spaceManager));
	}

	/**
	 * construct primes lookup table
	 * @param size the entries of the factors table
	 */
	public static void initializeFactorizationTable (int size)
	{
		ReportGenerators support;
		Factorization.setImplementation (support = new ReportGenerators (size));
		support.initFactorizationsWithStats (new SieveOfEratosthenes (support));
	}
	public static void initializeFactorizationTable ()
	{
		initializeFactorizationTable (FACTORIZATION_TABLE_SIZE);
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
				initializeFactorizationTable ();
				new FactorizedEvaluationControl ();
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
