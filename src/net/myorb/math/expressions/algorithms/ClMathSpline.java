
package net.myorb.math.expressions.algorithms;

import net.myorb.math.computational.Spline;
import net.myorb.math.computational.Parameterization;

import net.myorb.math.computational.integration.Configuration;
import net.myorb.math.computational.splines.SplineTool.Algorithm;

import net.myorb.math.expressions.SymbolMap;

/**
 * a manager for building factory objects 
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ClMathSpline <T> extends ClMathLibraryFoundation <T>
{

	/**
	 * @param sym the name of the symbol
	 * @return the created spline tool
	 */
	public SplineTool <T> generateTool (String sym)
	{
		SplineTool <T> tool = new SplineTool <T> (sym, options);
		tool.extractEnvironment (environment);
		return tool;
	}

}


/**
 * description of the tool as placed in the symbol table
 * @param <T> data type being processed
 */
class SplineTool <T> extends ClMathToolInstanceFoundation <T>
		implements Algorithm <T>
{

	public SplineTool (String name, Parameterization.Hash options)
	{
		this.configuration = new Configuration (options);
		this.symbolType = SymbolMap.SymbolType.UNARY;
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineTool.Algorithm#buildFactory()
	 */
	public Spline.Factory <T> buildFactory ()
	{
		Spline.Factory <T> f = Spline.buildFactoryFrom (configuration);
		environment.provideAccessTo (f);
		return f;
	}

}

