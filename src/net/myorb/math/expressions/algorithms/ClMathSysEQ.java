
package net.myorb.math.expressions.algorithms;

import net.myorb.math.linalg.SolutionPrimitives;
import net.myorb.math.linalg.SolutionPrimitives.Decomposition;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.computational.integration.Configuration;
import net.myorb.math.computational.Parameterization;

import net.myorb.reflection.ObjectManagement;

import java.util.Map;

/**
 * manage parameterization of linear algebra solutions
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ClMathSysEQ <T> extends ClMathLibraryFoundation <T>
{


	/**
	 * describe objects that can solve systems of equations
	 * @param <S> data type for solutions
	 */
	public interface SolutionProvider <S>
	{
		/**
		 * @return a solution object provided by an instance
		 */
		SolutionPrimitives <S> provideSolution ();
	}

	public interface SolutionProduct <S> extends SolutionProvider <S>
	{
		/**
		 * @return a Decomposition produced by the associated solution
		 */
		SolutionPrimitives.Decomposition getProduct ();
	}

	public interface SolutionManager <S> extends SolutionProvider <S>
	{
		/**
		 * @param D a Decomposition to be associated
		 * @return a SolutionProduct wrapper object
		 */
		SolutionProduct <S> wrap (SolutionPrimitives.Decomposition D);
	}


	/**
	 * @param sym the name of the symbol
	 * @return the created SysEQ tool
	 */
	public SysEQTool <T> generateTool (String sym)
	{
		SysEQTool <T> tool = new SysEQTool <T> (sym, options);
		tool.setEnvironment (environment);
		return tool;
	}


	/**
	 * build a Solution Manager for restored primitives 
	 * @param solution the solution primitives manager for the data
	 * @param options the configuration parameter for the instance
	 * @param environment the core data source for this session
	 * @param name the name of the instance being constructed
	 * @return a SolutionManager for this library type
	 * @param <T> the data type used in solution
	 */
	public static <T> SolutionManager <T> getSolutionManagerFor
		(
			SolutionPrimitives <T> solution, Map <String, Object> options,
			Environment <T> environment, String name
		)
	{
		return new SysEQTool <> (name, Parameterization.copy (options), solution, environment);
	}


}


/**
 * description of the tool as placed in the symbol table
 * @param <T> data type being processed
 */
class SysEQTool <T> extends ClMathToolInstanceFoundation <T>
	implements ClMathSysEQ.SolutionManager <T>
{


	public SysEQTool (String name, Parameterization.Hash options)
	{ this.name = name; this.configuration = new Configuration (options); }

	public SysEQTool
		(
			String name, Parameterization.Hash options,
			SolutionPrimitives <T> solution,
			Environment <T> environment
		)
	{
		this (name, options);
		this.solution = solution;
		this.solutionPath = solution.getClass ().getCanonicalName ();
		this.extractEnvironment (environment);
	}


	/**
	 * provide the environment to the tool
	 * @param environment the session control structure
	 */
	public void setEnvironment (Environment <T> environment)
	{ this.extractEnvironment (environment); this.configureSolution (); }
	void configureSolution () { this.buildSolution (this.getSolutionPath ()); }
	String getSolutionPath () { return this.configuration.getParameter ("solution"); }


	/**
	 * do reflection construction for solution path
	 * @param solutionPath class path to the solution object
	 * @param parameter the space manager taken from the environment
	 */
	public void buildSolution (String solutionPath)
	{
		try { generateSolution (this.solutionPath = solutionPath); }
		catch (Exception e) { throw new RuntimeException ("Solution build failed", e); }
	}
	protected String solutionPath;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.ClMathSysEQ.SolutionProvider#provideSolution()
	 */
	public SolutionPrimitives <T> provideSolution () { return solution; }
	@SuppressWarnings("unchecked") void generateSolution (String solutionPath) throws Exception
	{ this.solution = ( SolutionPrimitives <T> ) ObjectManagement.doConstruct (solutionPath, this.mgr); }
	protected SolutionPrimitives <T> solution;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.ClMathSysEQ.SolutionManager#wrap(net.myorb.math.linalg.SolutionPrimitives.Decomposition)
	 */
	public ClMathSysEQ.SolutionProduct <T>
		wrap (SolutionPrimitives.Decomposition D)
	{ return new SolutionProduct (D); }


	/**
	 * associate a Decomposition with the parent solution
	 */
	class SolutionProduct extends ClMathCommonSolutionProduct <T>
			implements ClMathSysEQ.SolutionProduct <T>
	{

		SolutionProduct (SolutionPrimitives.Decomposition D) { setProductContent (D); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.ClMathSysEQ.SolutionProduct#getProduct()
		 */
		public Decomposition getProduct () { return (Decomposition) this.content; }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.ClMathSysEQ.SolutionProvider#provideSolution()
		 */
		public SolutionPrimitives <T> provideSolution () { return solution; }

	}


}

