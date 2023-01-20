
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.data.abstractions.Configurable;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import java.util.Map;

/**
 * common base for function imports
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public abstract class CommonFunctionBase <T>
	implements Environment.AccessAcceptance <T>,
		Function <T>, Configurable
{


	/**
	 * @param functionNamed the name to give the symbol
	 * @param parameterNameConvention the conventional name for parameters
	 */
	public CommonFunctionBase
		(
			String functionNamed,
			String parameterNameConvention
		)
	{
		this.parameterNameConvention = parameterNameConvention;
		this.functionNamed = functionNamed;
	}
	
	
	/*
	* implementation of function
	*/
	
	/* (non-Javadoc)
	* @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	*/
	public abstract T eval (T z);
	
	/* (non-Javadoc)
	* @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	*/
	public SpaceDescription <T> getSpaceDescription () { return manager; }
	
	/* (non-Javadoc)
	* @see net.myorb.math.Function#getSpaceManager()
	*/
	public SpaceManager <T> getSpaceManager () { return manager; }
	
	
	/*
	* post to symbol table
	*/
	
	/* (non-Javadoc)
	* @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	*/
	public void setEnvironment (Environment <T> environment)
	{
		this.setSpaceManager (environment.getSpaceManager ());
		this.post (environment.getSymbolMap ());
	}


	/**
	 * post this function to session symbol map
	 * @param symbols the session symbol map
	 */
	public void post (SymbolMap symbols)
	{
		ImportedFunctionWrapper <T>
			symbol = new ImportedFunctionWrapper <T>
				(functionNamed, parameterNameConvention, this);
		symbols.add (symbol);
	}
	protected String functionNamed, parameterNameConvention;


	/**
	 * identify data type
	 * @param manager the manager object for the type
	 */
	public void setSpaceManager
		(SpaceManager <T> manager) { this.manager = manager; }
	protected SpaceManager <T> manager;
	
	
	/*
	 * get symbol name from configuration parameters
	 */
	
	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Configurable#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{ if (parameters.containsKey (NAMED)) this.functionNamed = parameters.get (NAMED).toString (); }
	public static String NAMED = "named";


}

