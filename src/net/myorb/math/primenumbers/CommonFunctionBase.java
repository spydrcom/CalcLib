
package net.myorb.math.primenumbers;

import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;
import net.myorb.math.expressions.symbols.ImportedFunctionWrapper;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.data.abstractions.Configurable;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import java.util.Map;

/**
 * common base for Factorization functions
 * @author Michael Druckman
 */
public abstract class CommonFunctionBase
	implements Environment.AccessAcceptance < Factorization >,
		Function < Factorization >, Configurable
{


	public static ExpressionFactorizedFieldManager manager = new ExpressionFactorizedFieldManager ();


	/**
	 * @param named the name to give the symbol
	 */
	public CommonFunctionBase (String named)
	{
		this.named = named;
	}


	/*
	 * implementation of function
	 */

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public abstract Factorization eval (Factorization z);

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<Factorization> getSpaceDescription () { return manager; }

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<Factorization> getSpaceManager () { return manager; }


	/*
	 * post to symbol table
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment <Factorization> environment)
	{
		environment.getSymbolMap ().add
		(
			new ImportedFunctionWrapper <Factorization>
			(named, "z", this)
		);
	}
	protected String named;


	/*
	 * get symbol name from configuration parameters
	 */

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Configurable#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{ if (parameters.containsKey (NAMED)) this.named = parameters.get (NAMED).toString (); }
	public static String NAMED = "named";


}

