
package net.myorb.math.complexnumbers;

import net.myorb.math.computational.integration.polylog.CyclicAspects;
import net.myorb.math.expressions.symbols.ImportedFunctionWrapper;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.data.abstractions.Configurable;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import java.util.Map;

/**
 * common base for complex functions
 * @author Michael Druckman
 */
public abstract class CommonFunctionBase extends CyclicAspects
	implements Environment.AccessAcceptance < ComplexValue <Double> >,
		Function < ComplexValue <Double> >, Configurable
{


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
	public abstract ComplexValue<Double> eval (ComplexValue<Double> z);

	public SpaceDescription<ComplexValue<Double>> getSpaceDescription()
	{
		return ComplexSpaceCore.manager;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<ComplexValue<Double>> getSpaceManager()
	{
		return ComplexSpaceCore.manager;
	}


	/*
	 * post to symbol table
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment <ComplexValue<Double>> environment)
	{
		environment.getSymbolMap ().add
		(
			new ImportedFunctionWrapper <ComplexValue<Double>>
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

