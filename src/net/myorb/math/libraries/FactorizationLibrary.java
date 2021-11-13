
package net.myorb.math.libraries;

import net.myorb.math.expressions.KeywordMap;
import net.myorb.math.expressions.algorithms.*;
import net.myorb.math.expressions.algorithms.PowerPrimitives;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;
import net.myorb.math.primenumbers.sieves.SieveOfEratosthenes;
import net.myorb.math.primenumbers.ReportGenerators;
import net.myorb.math.primenumbers.Factorization;
import net.myorb.math.*;

/**
 * collector object for prime factorization data type
 * @author Michael Druckman
 */
@SuppressWarnings("rawtypes")
public class FactorizationLibrary implements Configuration.LibraryCollector
{


	/*
	 * construction of environment for this library
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.Configuration.LibraryCollector#configure(java.lang.String)
	 */
	public void configure (String parameter)
	{
		initializeFactorizationTable (Integer.parseInt (parameter));
		ExpressionFactorizedFieldManager mgr = new ExpressionFactorizedFieldManager ();
		ExtendedPowerLibrary<Factorization> masterLibrary = new ExponentiationLib<Factorization> (mgr);
		environment = new Environment<Factorization> (mgr, masterLibrary);
		trigLib = new TrigIdentities<Factorization> (mgr);
	}
	ExponentiationLib<Factorization> masterLibrary;
	TrigIdentities<Factorization> trigLib;
	Environment environment = null;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.Configuration.LibraryCollector#addLibraries(net.myorb.math.expressions.algorithms.Configuration.LibraryMap)
	 */
	@SuppressWarnings("unchecked")
	public void addLibraries (Configuration.LibraryMap libraryMap)
	{
		libraryMap.put ("factorization", new FactorizationPrimitives (environment));
		libraryMap.put ("calculus", new CalculusPrimitives<Double> (environment));
		libraryMap.put ("combinatorics", new ComboPrimitives<Double> (environment));
		libraryMap.put ("algebraic", new AlgebraicPrimitives<Double> (environment));
		libraryMap.put ("arithmetic", new ArithmeticPrimitives<Double> (environment));
		libraryMap.put ("trig", new TrigPrimitives<Factorization> (environment, trigLib));
		libraryMap.put ("polynomial", new PolynomialPrimitives<Double> (environment));
		libraryMap.put ("boolean", new BooleanPrimitives<Double> (environment));
		libraryMap.put ("vector", new VectorPrimitives<Double> (environment));
		libraryMap.put ("power", new PowerPrimitives<Double> (environment));
	}


	/**
	 * construct primes lookup table
	 * @param size count of factors to collect
	 */
	public void initializeFactorizationTable (int size)
	{
		ReportGenerators support;
		Factorization.setImplementation (support = new ReportGenerators (size));
		support.initFactorizationsWithStats (new SieveOfEratosthenes (support));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.Configuration.LibraryCollector#getEnvironment()
	 */
	public Environment getEnvironment () { return environment; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.Configuration.LibraryCollector#addKeywords(net.myorb.math.expressions.KeywordMap)
	 */
	public void addKeywords (KeywordMap keywords) {}


}

