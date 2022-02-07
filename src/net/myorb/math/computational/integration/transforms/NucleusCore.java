
package net.myorb.math.computational.integration.transforms;

import net.myorb.math.expressions.algorithms.CyclicAndPowerLibrary;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * base support class for implementation of kernel nucleus objects
 * @param <T> data type for calculations
 * @author Michael Druckman
 */
public class NucleusCore<T> implements Function<T>
{


	public NucleusCore
		(
			Environment<T> environment,
			TransformParameters parameters
		)
	{
		this.parameters = parameters;
		this.setType (parameters.getType ());
		this.processEnvironment (environment);
	}
	protected TransformParameters parameters;


	/**
	 * @param type the value of the type parameter
	 */
	public void setType (String type) {}


	/**
	 * @param environment the core resource for application managers
	 */
	public void processEnvironment (Environment<T> environment)
	{
		this.lib = environment.getCyclicAndPowerLibrary ();
		this.manager = environment.getSpaceManager ();
		this.vm = environment.getValueManager ();
		this.environment = environment;
		this.establishVariable ();
		this.setConstants ();
	}
	protected ExpressionSpaceManager<T> manager;
	protected CyclicAndPowerLibrary<T> lib;
	protected Environment<T> environment;
	protected ValueManager<T> vm;


	/**
	 * @return name to use in transform render
	 */
	public String getKernelName () { return "K"; }

	public boolean isKernelInverse () { return parameters.isInverse (); }


	/**
	 * compute appropriate constant(s) based on Kernel type
	 */
	public void setConstants () {}


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T t)
	{
		throw new RuntimeException ("Transform not implemented");
	}


	/**
	 * @return the value of the transform variable
	 */
	public T getU ()
	{
		return vm.toDiscrete (transformVariable.getValue ());
	}


	/**
	 * prepare the transform variable
	 */
	public void establishVariable ()
	{
		this.transformVariable = (SymbolMap.VariableLookup) environment
				.getSymbolMap ().get (parameters.getBasis ());
	}
	protected SymbolMap.VariableLookup transformVariable;


	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<T> getSpaceManager () { return manager; }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceManager<T> getSpaceDescription () { return manager; }


}

