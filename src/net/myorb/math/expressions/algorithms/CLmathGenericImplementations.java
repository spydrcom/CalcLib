
package net.myorb.math.expressions.algorithms;

import net.myorb.math.specialfunctions.GenericIncompleteGamma;
import net.myorb.math.specialfunctions.EulerProduct;
import net.myorb.math.specialfunctions.Zeta;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.data.abstractions.SpaceConversion;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * library interface implementation for generic classes
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public abstract class CLmathGenericImplementations<T>
	extends CLmathPrimitives<T>  implements Environment.AccessAcceptance<T>
{


	protected CLmathGenericImplementations
		(
			SpaceManager<T> manager,
			SpaceConversion<T> conversion,
			ExtendedPowerLibrary<T> library,
			Environment<T> environment
		)
	{
		super (manager);
		this.manager = manager;
		this.conversion = conversion;
		this.environment = environment;
		this.library = library;
	}
	SpaceManager<T> manager;
	SpaceConversion<T> conversion;
	ExtendedPowerLibrary<T> library;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment<T> environment)
	{
		this.environment = environment;
	}
	Environment<T> environment;


	/*
	 * 		Euler Product
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getEulerProductWrapper()
	 */
	public CommonWrapper getEulerProductWrapper ()
	{
		return new CommonWrapper
		(
			new CommonFunction ()
			{
				public T eval (T x) { return eulerProduct.eval (x); }
			}
		);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#initEulerProduct(java.lang.String)
	 */
	public void initEulerProduct (String parameter)
	{
		eulerProduct = new EulerProduct<T> (manager, library, conversion);
		eulerProduct.configure (Integer.parseInt (parameter));
	}
	protected EulerProduct<T> eulerProduct = null;


	/*
	 * 		ZETA
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#zeta(java.lang.Object)
	 */
	public T zeta (T parameter)
	{
		return zeta.eval (parameter);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#initZeta(java.lang.String)
	 */
	public void initZeta (String parameter)
	{
		zeta = new Zeta<T> (manager, library, conversion);
		zeta.configure (Integer.parseInt (parameter));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#initZetaAnalytic(java.lang.String)
	 */
	public void initZetaAnalytic (String parameter)
	{
		throw new RuntimeException ("Unimplemented function: Zeta");
	}
	protected Zeta<T> zeta = null;


	/*
	 * 		GAMMA
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getGammaWrapper()
	 */
	public CommonWrapper getGammaWrapper ()
	{
		return new CommonWrapper
		(
			new CommonFunction () { public T eval (T z) { return GAMMA.eval (z); } }
		);
	}
	protected Function<T> GAMMA = null;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getIncompleteGammaWrapper()
	 */
	public CommonWrapper getIncompleteGammaWrapper ()
	{
		return new CommonWrapper
		(
			new CommonFunction ()
			{
				public T eval (T z, T x) { return incompleteGamma.lower (z, x); }
			}
		);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#initGammaInc()
	 */
	public void initGammaInc ()
	{
		incompleteGamma = new GenericIncompleteGamma<T> (GAMMA, library);
	}
	protected GenericIncompleteGamma<T> incompleteGamma = null;


	/*
	 * 		Bernoulli
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#B(java.lang.Object)
	 */
	public T B (T x)
	{
		throw new RuntimeException ("Unimplemented function: Bernoulli Number");
	}
	public T BP (T n, T x)
	{
		throw new RuntimeException ("Unimplemented function: Bernoulli Polynomial");
	}


	/*
	 * 		Bessel
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getBesselImplementation()
	 */
	public CommonOperatorImplementation getBesselImplementation ()
	{
		return new CommonOperatorImplementation ()
		{

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonOperatorImplementation#configure(java.lang.String)
			 */
			public void configure (String parameters)
			{ bessel = getBesselParameterManager ().configureBesselManager (parameters); }
			protected BesselParameterManager<T> bessel;

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonOperatorImplementation#evaluate(java.lang.Object)
			 */
			public T evaluate (T using) { return bessel.function.eval (using); }

		};
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getBesFunImplementation()
	 */
	public CommonFunctionImplementation getBesFunImplementation ()
	{
		return getBesselParameterManager ().getConfigurableBesselImplementation ();
	}

	/**
	 * @return an instance of the Bessel Parameter Manager
	 */
	ClMathBessel<T> getBesselParameterManager ()
	{
		ClMathBessel<T> bessel = new ClMathBessel<T>();
		bessel.setEnvironment (environment);
		return bessel;
	}


}


