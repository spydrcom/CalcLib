
package net.myorb.math.computational.integration.transforms;

import net.myorb.math.expressions.evaluationstates.Environment;

/**
 * generic evaluation of the Mellin Transform nucleus function
 *  t ^ (u-1), t ^ (-u) / (2*pi*i) for inverse
 * @param <T> data type for calculations
 * @author Michael Druckman
 */
public class MellinNucleus<T> extends NucleusCore<T>
{


	/*
	 * !! K_M(u,t) = t ^ (u-1)
	 * !! KI_M(t,u) = t ^ (-u) / (2*pi*i)
	 */

	public MellinNucleus
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
		{ return "K_M"; }
		return "KI_M";
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.transforms.NucleusCore#setConstants()
	 */
	public void setConstants ()
	{
		if ( ! parameters.isInverse () ) return;

		this.NEG_ONE = manager.newScalar ( -1 );

		this.TWO_PI_I = manager.invert
		(
			manager.multiply
			(
				manager.convertFromDouble (2.0 * Math.PI),
				lib.sqrt (NEG_ONE)
			)
		);
	}
	protected T TWO_PI_I, NEG_ONE;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T t)
	{
		T result, u = getU ();

		if ( ! parameters.isInverse () )
		{
			result = lib.power (t, manager.add (u, NEG_ONE));
		}
		else
		{
			result = manager.multiply
			(
				TWO_PI_I,
				lib.power (t, manager.negate (u))
			);
		}

		return result;
	}


}

