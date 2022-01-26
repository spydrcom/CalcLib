
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.symbols.LibraryObject;
import net.myorb.math.expressions.SymbolMap.Named;

import java.util.Map;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

/**
 * manage parameterization of functions configured in-line
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public abstract class InstanciableFunctionLibrary<T> extends CommonOperatorLibrary<T>
			implements LibraryObject.InstanceGenerator<T>, Environment.AccessAcceptance<T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.LibraryObject.InstanceGenerator#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment<T> environment)
	{
		this.manager = environment.getSpaceManager ();
		this.library = environment.getLibrary ();
		this.environment = environment;
	}
	protected ExtendedPowerLibrary<T> library;
	protected Environment<T> environment;
	protected SpaceManager<T> manager;


	/**
	 * @param sym the name for the symbol
	 * @param lib the library managing the parameterization
	 * @return a newly constructed Named Symbol
	 */
	public abstract Named getInstance (String sym, LibraryObject<T> lib);

	/**
	 * @param options the consumer description hash
	 * @return the recreated consumer object
	 */
	public abstract IterationConsumer buildIterationConsumer (Map<String, Object> options);

	/**
	 * @return a hash of objects needed to recreate the consumer
	 */
	public abstract Map<String, Object> getIterationConsumerDescription ();


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.LibraryObject.InstanceGenerator#newInstance(java.lang.String, net.myorb.math.expressions.symbols.LibraryObject)
	 */
	public void newInstance (String sym, LibraryObject<T> lib)
	{
		Named symbol = getInstance (sym, lib);
		Object description = lib.getParameterization ().get ("description");
		environment.getSymbolMap ().add (symbol, description.toString ());
	}

	/**
	 * @param declaring the name of the symbol to be declared
	 * @param libName the name of the LibraryObject describing the symbol
	 */
	public void newInstance (String declaring, String libName)
	{
		@SuppressWarnings("unchecked")
		LibraryObject<T> lib = (LibraryObject<T>)
			environment.getSymbolMap ().get (libName);
		newInstance (declaring, lib);
	}

}
