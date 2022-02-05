
package net.myorb.math.computational.integration.transforms;

import net.myorb.math.expressions.evaluationstates.Environment;

/**
 * generic evaluation of the Laplace Transform nucleus function
 *  e ^ ( - u * t ) for transform, exp (u*t) / (2*pi*i) for inverse
 * @param <T> data type for calculations
 * @author Michael Druckman
 */
public class LaplaceNucleus<T> extends NucleusCore<T>
{


	/*
	 * !! K_L(u,t) = exp (-u*t)
	 * !! KI_L(t,u) = exp (u*t) / (2*pi*i)
	 * */

	public LaplaceNucleus
		(
			Environment<T> environment,
			TransformParameters parameters
		)
	{
		super (environment, parameters);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.transforms.NucleusCore#getKernelName()
	 */
	public String getKernelName ()
	{
		if ( ! parameters.isInverse () )
		{ return "K_L"; }
		return "KI_L";
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.transforms.NucleusCore#setConstants()
	 */
	public void setConstants ()
	{
		if ( ! parameters.isInverse () ) return;

		this.TWO_PI_I = manager.invert
			(
				manager.multiply
				(
					manager.convertFromDouble (2.0 * Math.PI),
					lib.sqrt (manager.newScalar (-1))
				)
			);
	}
	protected T TWO_PI_I;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T t)
	{
		T result, ut = manager.multiply (getU (), t);

		if ( ! parameters.isInverse () )
		{
			result = lib.exp (manager.negate (ut));
		}
		else
		{
			result = manager.multiply (TWO_PI_I, lib.exp (ut));
		}

		return result;
	}


}
