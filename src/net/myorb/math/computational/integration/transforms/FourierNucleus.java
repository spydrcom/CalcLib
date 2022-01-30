
package net.myorb.math.computational.integration.transforms;

import net.myorb.math.specialfunctions.bessel.OrdinaryFirstKind;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;

import net.myorb.math.expressions.algorithms.CyclicAndPowerLibrary;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.polynomial.PolynomialSpaceManager;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * complex evaluation of the Fourier Transform nucleus function
 *  e ^ ( - 2 * PI * i * Xi * t ), or alternatively SIN and COS,
 *  also Hartley and Hankel are available in configuration
 * @param <T> data type for calculations
 * @author Michael Druckman
 */
public class FourierNucleus<T> implements Function<T>
{


	public FourierNucleus
		(
			Environment<T> environment,
			TransformParameters parameters
		)
	{
		this.parameters = parameters;
		this.processEnvironment (environment);
	}
	protected TransformParameters parameters;


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
	 * compute appropriate constant(s) based on Kernel type
	 */
	public void setConstants ()
	{
		switch (parameters.getTransformType ())
		{

			case SIN:
			case COS:

				this.S2PI =
					manager.convertFromDouble
						(Math.sqrt (2.0 / Math.PI));
				break;

			case COMPLEX:

				this.I2PI = manager.multiply
				(
					manager.convertFromDouble
					(
						2.0 * Math.PI *
						(
							parameters.isInverse () ? 1.0 : -1.0
						)
					),
					lib.sqrt (manager.newScalar (-1))
				);
				break;

			case HARTLEY:

				this.S2PI2 =
					manager.convertFromDouble
						(Math.sqrt (1.0 / (2.0 * Math.PI)));
				break;

			case BESSEL: this.getJ ();

			default:

		}
	}
	protected T I2PI, S2PI, S2PI2;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T t)
	{
		T ut = manager.multiply (getU (), t);

		switch (parameters.getTransformType ())
		{

			case BESSEL:
				return manager.multiply (t, Jv.eval (ut));

			case HARTLEY:
				return manager.multiply (S2PI2, cas (ut));

			case COMPLEX:
				return lib.exp (manager.multiply (I2PI, ut));

			case SIN:
				return manager.multiply (S2PI, lib.sin (ut));

			case COS:
				return manager.multiply (S2PI, lib.cos (ut));

		}

		return null;
	}


	/**
	 * cosine and sine as specified in the Hartley definition
	 * @param ut the product of the transform pair of u and t
	 * @return the computed value of cas for the ut product
	 */
	public T cas (T ut)
	{
		return manager.add (lib.sin (ut), lib.cos (ut));
	}


	/**
	 * allocate an object for Bessel Jv
	 */
	public void getJ ()
	{
		int terms = parameters.getValue ("terms").intValue ();
		T v = manager.convertFromDouble (parameters.getValue ("order").doubleValue ());
		PolynomialSpaceManager<T> psm = new PolynomialSpaceManager<T>(manager);
		Jv = OrdinaryFirstKind.getJ (v, terms, lib, psm);
	}
	protected SpecialFunctionFamilyManager.FunctionDescription<T> Jv;


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

