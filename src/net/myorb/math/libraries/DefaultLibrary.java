
package net.myorb.math.libraries;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.HighSpeedMathLibrary;

import net.myorb.math.expressions.algorithms.*;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.expressions.KeywordMap;

/**
 * collector object for prime real number data type
 * @author Michael Druckman
 */
@SuppressWarnings("rawtypes")
public class DefaultLibrary implements Configuration.LibraryCollector
{

	/*
	 * construction of environment for this library
	 */

	static ExpressionFloatingFieldManager
		mgr = new ExpressionFloatingFieldManager ();
	static HighSpeedMathLibrary masterLibrary = new HighSpeedMathLibrary ();
	static ExtendedPowerLibrary<Double> systemLibrary = new JrePowerLibrary ();

	static Environment environment =
			//new Environment<Double> (mgr, masterLibrary);
			new Environment<Double> (mgr, systemLibrary);
	public Environment getEnvironment () { return environment; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.Configuration.LibraryCollector#addLibraries(net.myorb.math.expressions.algorithms.Configuration.LibraryMap)
	 */
	@SuppressWarnings("unchecked")
	public void addLibraries (Configuration.LibraryMap libraryMap)
	{
		libraryMap.put ("calculus", new CalculusPrimitives<Double> (environment));
		libraryMap.put ("combinatorics", new ComboPrimitives<Double> (environment));
		libraryMap.put ("algebraic", new AlgebraicPrimitives<Double> (environment));
		libraryMap.put ("arithmetic", new ArithmeticPrimitives<Double> (environment));
		libraryMap.put ("trig", new TrigHSPrimitives<Double> (environment, masterLibrary, masterLibrary));
		addAbstractedTypes (new MatrixPrimitives<Double> (environment), libraryMap);
		libraryMap.put ("stats", new StatisticsPrimitives<Double> (environment));
		libraryMap.put ("boolean", new BooleanPrimitives<Double> (environment));
		libraryMap.put ("power", new PowerPrimitives<Double> (environment));
	}
	void addAbstractedTypes (Object abstractedPrimitives, Configuration.LibraryMap libraryMap)
	{
		libraryMap.put ("matrix", abstractedPrimitives);
		libraryMap.put ("polynomial", abstractedPrimitives);
		libraryMap.put ("vector", abstractedPrimitives);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return "Implementation of Real Numbers domain - " + DefaultLibrary.class.getName ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.Configuration.LibraryCollector#addKeywords(net.myorb.math.expressions.KeywordMap)
	 */
	public void addKeywords (KeywordMap keywords) {}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.Configuration.LibraryCollector#configure(java.lang.String)
	 */
	public void configure (String parameters)
	{ System.out.println (parameters); }

}

