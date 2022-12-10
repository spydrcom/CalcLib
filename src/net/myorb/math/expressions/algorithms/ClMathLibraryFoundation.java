
package net.myorb.math.expressions.algorithms;

import net.myorb.math.computational.Parameterization;

import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.symbols.LibraryObject;
import net.myorb.math.expressions.SymbolMap;

import java.util.Map;

/**
 * manage parameterization of library solutions.
 * - boiler-plate for LIBRARY declared instances in the GUI command language
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public abstract class ClMathLibraryFoundation <T>
		extends InstanciableFunctionLibrary <T>
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
	protected Parameterization.Hash options;


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
	 * produce the tool object
	 * @param sym the name of the symbol
	 * @return the created tool symbol entry
	 */
	public abstract SymbolMap.Named generateTool (String sym);


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getIterationConsumerDescription()
	 */
	public Map <String, Object> getIterationConsumerDescription ()
	{
		return new Parameterization.Hash (sym, "CLASSPATH", this.getClass (), options);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#buildIterationConsumer(java.util.Map)
	 */
	public IterationConsumer buildIterationConsumer (Map<String, Object> options)
	{
		return null;
	}


}
