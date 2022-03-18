
package net.myorb.math.expressions.algorithms;

import net.myorb.math.computational.Spline;
import net.myorb.math.computational.Parameterization;

import net.myorb.math.computational.integration.Configuration;
import net.myorb.math.computational.splines.SplineTool.Algorithm;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.symbols.LibraryObject;

import net.myorb.math.expressions.SymbolMap;

import java.util.Map;

/**
 * a manager for building factory objects 
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ClMathSpline <T> extends InstanciableFunctionLibrary <T>
		implements SymbolMap.FactoryForImports
{


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.FactoryForImports#importSymbolFrom(java.lang.String, java.util.Map)
	 */
	public SymbolMap.Named importSymbolFrom
	(String named, Map<String, Object> configuration)
	{
		this.sym = named;
		this.options = Parameterization.copy (configuration);
		return generateTool (sym);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getInstance(java.lang.String, net.myorb.math.expressions.symbols.LibraryObject)
	 */
	public SymbolMap.Named getInstance (String sym, LibraryObject<T> lib)
	{
		this.sym = sym;
		this.options = Parameterization.copy (lib.getParameterization ());
		return generateTool (sym);
	}
	protected String sym;


	/**
	 * @param sym the name of the symbol
	 * @return the created spline tool
	 */
	public SplineTool <T> generateTool (String sym)
	{
		SplineTool <T> tool = new SplineTool <T> (sym, options);
		tool.setEnvironment (environment);
		return tool;
	}
	protected Parameterization.Hash options;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getIterationConsumerDescription()
	 */
	public Map<String, Object> getIterationConsumerDescription ()
	{
		return new Parameterization.Hash (sym, "CLASSPATH", ClMathSpline.class, options);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#buildIterationConsumer(java.util.Map)
	 */
	public IterationConsumer buildIterationConsumer (Map<String, Object> options)
	{
		return null;
	}


}


/**
 * description of the tool as placed in the symbol table
 * @param <T> data type being processed
 */
class SplineTool <T> implements SymbolMap.Named, Algorithm <T>
{

	public SplineTool (String name, Parameterization.Hash options)
	{
		this.configuration = new Configuration (options);
		this.name = name;
	}

	public void setEnvironment (Environment<T> environment)
	{
		this.environment = environment;
	}
	protected Environment<T> environment;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getName()
	 */
	public String getName () { return name; }
	protected String name;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getSymbolType()
	 */
	public SymbolMap.SymbolType getSymbolType () {
		return SymbolMap.SymbolType.UNARY;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineTool.Algorithm#getConfiguration()
	 */
	public Configuration getConfiguration () {
		return configuration;
	}
	protected Configuration configuration;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineTool.Algorithm#buildFactory()
	 */
	public Spline.Factory <T> buildFactory () {
		Spline.Factory <T> f = Spline.buildFactoryFrom (configuration);
		environment.provideAccessTo (f);
		return f;
	}

}

