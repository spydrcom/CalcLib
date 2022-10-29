
package net.myorb.math.specialfunctions;

import net.myorb.math.complexnumbers.*;

import net.myorb.math.SpaceManager;

/**
 * library of complex algorithms describing GAMMA function identities
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public abstract class GammaAlgotithms <T>
{


	/**
	 * @return restriction of minimum real value of parameter
	 */
	public abstract int minimumReal ();

	/**
	 * @return restriction of maximum real value of parameter
	 */
	public abstract int maximumReal ();

	/**
	 * @param z the parameter that meets the restriction
	 * @return the computed GAMMA value of the parameter
	 */
	public abstract ComplexValue <T> restrictedGamma (ComplexValue <T> z);


	/**
	 * @param lib a Complex Library providing arithmetic and trig functions
	 */
	@SuppressWarnings("unchecked")
	public GammaAlgotithms (ComplexLibrary <T> lib)
	{
		this.cmgr = lib.getComplexFieldManager ();
		this.minimum  = cmgr.newScalar (this.minimumReal ());
		this.tmgr = (SpaceManager <T>) cmgr.getComponentManager ();
		this.restriction = this.minimum.Re ();
		this.ZERO = this.tmgr.getZero ();
		this.ONE = this.cmgr.getOne ();
		this.PI = this.cmgr.getPi ();
		this.lib = lib;
	}
	protected SpaceManager <T> tmgr;
	protected SpaceManager < ComplexValue <T> > cmgr;
	protected ComplexValue <T> PI, ONE, minimum;
	protected ComplexLibrary <T> lib;
	protected T ZERO, restriction;


	/**
	 * main entry for GAMMA applying restrictions
	 * @param z the parameter to GAMMA needing restriction checks
	 * @return the computed GAMMA value of the parameter
	 */
	public ComplexValue <T> gamma (ComplexValue <T> z)
	{
		T r = z.Re (), max;
		
		if (tmgr.lessThan (r, ZERO))
		{
			return reflection (z);
		}
		else if (tmgr.lessThan (r, restriction))
		{
			return recurrence (z);
		}
		else if (tmgr.lessThan (max = tmgr.newScalar (this.maximumReal ()), r))
		{
			return reverseRecurrence (z, max);
		}
		else return restrictedGamma (z);
	}


	/**
	 * The use of the reflection is necessary,
	 * it allows the function to extend the approximation to values of z where 
	 * Re(z) .LT. 0.5 (where the Lanczos method is not valid).
	 * @param z the parameter for gamma computation
	 * @return the function result
	 */
	public ComplexValue <T> reflection (ComplexValue <T> z)
	{
    	ComplexValue<T> sinPiZ = lib.sin (z.times (PI));
    	ComplexValue<T> gamma1mZ = gamma (ONE.plus (z.negate ()));

    	// pi / ( sin (pi * z) * gamma (1 - z) );  // Reflection formula
    	return sinPiZ.times (gamma1mZ).inverted ().times (PI);
	}


	/**
	 * apply recurrence to raise parameter above minimum
	 * @param z the parameter for gamma computation
	 * @return the function result
	 */
	public ComplexValue <T> recurrence (ComplexValue <T> z)
	{
		// recurrence formula
		// GAMMA z = GAMMA ( z + n + 1 )  /  ( z (z+1) (z+2) ... (z+n) )

		return cmgr.multiply
		(
			restrictedGamma (cmgr.add (z, this.minimum)),
			cmgr.invert (risingFactorial (z))
		);
	}


	/**
	 * Gamma recurrence formula
	 *  GAMMA(z+n) = GAMMA(z) * ( z * (z+1) * ... * (z+n-1) )
	 * @param z the parameter to GAMMA being sought
	 * @param max largest real part allowed
	 * @return GAMMA(z)
	 */
	public ComplexValue <T> reverseRecurrence (ComplexValue <T> z, T max)
	{
		ComplexValue <T> zpn = z,
			product = cmgr.getOne (), none = cmgr.newScalar (-1);
		while (tmgr.lessThan (max, zpn.Re ()))
		{
			zpn = cmgr.add (zpn, none);
			product = cmgr.multiply (product, zpn);
		}
		return cmgr.multiply (restrictedGamma (zpn), product);
	}


	/**
	 * compute rising factorial for z up to minimum-1
	 * @param z the parameter for gamma computation
	 * @return product z (z+1) (z+2) ... (z+n-1)
	 */
	public ComplexValue <T> risingFactorial (ComplexValue <T> z)
	{
		ComplexValue <T> product = z;

		for (int n = minimumReal () - 1; n > 0; n--)
		{
			product = cmgr.multiply
				(
					product,
					cmgr.add (z, cmgr.newScalar (n))
				);
		}

		return product;
	}


}

