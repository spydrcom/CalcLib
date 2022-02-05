
package net.myorb.math.computational.integration.transforms;

import net.myorb.math.expressions.evaluationstates.Environment;

/**
 * generic evaluation of the Hilbert Transform nucleus function
 *  1 / ( pi * (u - t) ), transform is symmetric so inverse matches transform
 * @param <T> data type for calculations
 * @author Michael Druckman
 */
public class HilbertNucleus<T> extends NucleusCore<T>
{


	/*
	 * !! K_H(u,t) = 1 / ( pi * (u - t) )
	 * (symmetric)
	 */

	public HilbertNucleus
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
		return "KI_H";
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.transforms.NucleusCore#setConstants()
	 */
	public void setConstants ()
	{ PI = manager.invert (manager.convertFromDouble (Math.PI)); }
	protected T PI;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T t)
	{
		T ut = manager.add (getU (), manager.negate (t));
		T result = manager.multiply (PI, manager.invert (ut));
		return result;
	}


}

