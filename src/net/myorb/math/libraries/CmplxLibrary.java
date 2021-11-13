
package net.myorb.math.libraries;

import net.myorb.math.TrigIdentities;
import net.myorb.math.ExtendedPowerLibrary;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.complexnumbers.OptimizedComplexLibrary;
import net.myorb.math.complexnumbers.HSComplexSupportImplementation;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;
import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.commands.Plotting;
import net.myorb.math.expressions.algorithms.*;
import net.myorb.math.expressions.KeywordMap;

/**
 * collector object for prime complex number data type
 * @author Michael Druckman
 */
@SuppressWarnings("rawtypes")
public class CmplxLibrary implements Configuration.LibraryCollector
{

	/*
	 * construction of environment for this library
	 */

	static ExpressionComplexFieldManager
		mgr = new ExpressionComplexFieldManager ();
	static HSComplexSupportImplementation hscs = new HSComplexSupportImplementation ();
	static ExtendedPowerLibrary<ComplexValue<Double>> masterLibrary = new OptimizedComplexLibrary (hscs);
	static TrigIdentities<ComplexValue<Double>> trigLib = new TrigIdentities<ComplexValue<Double>> (mgr);

	static Environment environment =
			new Environment<ComplexValue<Double>> (mgr, masterLibrary);
	public Environment getEnvironment () { return environment; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.Configuration.LibraryCollector#addLibraries(net.myorb.math.expressions.algorithms.Configuration.LibraryMap)
	 */
	@SuppressWarnings("unchecked")
	public void addLibraries (Configuration.LibraryMap libraryMap)
	{
		libraryMap.put ("complex", new ComplexPrimitives (environment));
		libraryMap.put ("calculus", new CalculusPrimitives<Double> (environment));
		libraryMap.put ("combinatorics", new ComboPrimitives<Double> (environment));
		libraryMap.put ("algebraic", new AlgebraicPrimitives<Double> (environment));
		libraryMap.put ("arithmetic", new ArithmeticPrimitives<Double> (environment));
		libraryMap.put ("trig", new TrigPrimitives<ComplexValue<Double>> (environment, trigLib));
		addAbstractedTypes (new MatrixPrimitives<Double> (environment), libraryMap);
		libraryMap.put ("stat", new StatisticsPrimitives<Double> (environment));
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
		return "Implementation of Imaginary Numbers domain - " + CmplxLibrary.class.getName ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.Configuration.LibraryCollector#addKeywords(net.myorb.math.expressions.KeywordMap)
	 */
	@SuppressWarnings("unchecked")
	public void addKeywords (KeywordMap keywords)
	{
		Plotting<ComplexValue<Double>> plotting = new Plotting<>(environment);
		keywords.add (OperatorNomenclature.POLAR_ANGULAR_KEYWORD, plotting.constructAngularKeywordCommand ());
		keywords.add (OperatorNomenclature.POLAR_RADIAL_KEYWORD, plotting.constructRadialKeywordCommand ());
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.Configuration.LibraryCollector#configure(java.lang.String)
	 */
	public void configure (String parameters) {}

}

