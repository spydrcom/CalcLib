
package net.myorb.math.expressions.algorithms;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.integration.Configuration;

import net.myorb.math.expressions.evaluationstates.Environment;

/**
 * manage parameterization of iterative solutions
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ClMathIterative <T> extends ClMathLibraryFoundation <T>
{


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.ClMathLibraryFoundation#generateTool(java.lang.String)
	 */
	public IterationTool <T> generateTool (String sym)
	{
		IterationTool <T> tool = new IterationTool <T> (sym, options);
		tool.setEnvironment (environment);
		return tool;
	}


}


/**
 * description of the content as placed in the symbol table
 * @param <T> data type being processed
 */
class IterationTool <T> extends ClMathToolInstanceFoundation <T>
{


	public IterationTool
		(
			String name, Parameterization.Hash options,
			Environment <T> environment
		)
	{
		this (name, options);
		this.extractEnvironment (environment);
	}

	public IterationTool (String name, Parameterization.Hash options)
	{
		this.configuration = new Configuration (options);
		this.name = name;
	}


	/**
	 * provide the environment to the tool
	 * @param environment the session control structure
	 */
	public void setEnvironment (Environment <T> environment)
	{
		this.extractEnvironment (environment);
	}


	/**
	 * wrap data as a compiled Solution Product
	 * @param content the content that forms the product
	 * @return the data as a Solution Product
	 */
	public ClMathCommonSolutionProduct <T>
			wrap (Object content)
	{ return new SolutionProduct (content); }


	/**
	 * associate a Decomposition with the parent solution
	 */
	class SolutionProduct extends ClMathCommonSolutionProduct <T>
	{

		SolutionProduct () {}

		SolutionProduct (Object content)
		{ this.setProductContent (content); }

	}


}

