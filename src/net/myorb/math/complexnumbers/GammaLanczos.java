
package net.myorb.math.complexnumbers;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.data.abstractions.SpaceConversion;

import java.util.ArrayList;

/**
 * implementation of Lanczos approximation of GAMMA function
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class GammaLanczos<T>
	implements Function <ComplexValue<T>>
{


	/**
	 * @param complexLib complex library holds all required components
	 */
	public GammaLanczos
		(
			ComplexLibrary<T> complexLib
		)
	{
		this.complexLib = complexLib;
		this.complexValueManager = complexLib.getComplexFieldManager ();
		this.complexConversionManager = complexLib.getComplexSpaceConversion ();
		this.componentManager = complexLib.getComponentManager ();

		this.collectCoefficients ();
		this.prepareConstants ();
	}
	protected SpaceConversion <ComplexValue<T>> complexConversionManager;
	protected SpaceManager <ComplexValue<T>> complexValueManager;


	/**
	 * @param manager component manager for real/imag data types
	 * @param conversion the conversion implementation between double and complex
	 */
	public GammaLanczos
	(SpaceManager<T> manager, SpaceConversion<ComplexValue<T>> conversion)
	{ this (new ComplexLibrary<T> (manager, conversion)); }
	protected SpaceManager <T> componentManager;

	public ComplexLibrary<T> getLibrary () { return complexLib; }
	protected ComplexLibrary<T> complexLib;


	/**
	 * prepare complex object versions of static real constants
	 */
	protected void prepareConstants ()
	{
		this.PI = C (Math.PI).Re ();
		this.EPSILON = C (1E-7).Re ();
		this.COEFFICIENT_COUNT = P.size ();
		this.g = C (COEFFICIENT_COUNT - 1);
		this.P0g = C (2 * 0.99999999999980993);
		this.SQRT2PI = C (Math.sqrt (2 * Math.PI));
		this.ONE = complexValueManager.getOne ();
		this.NEGONE = ONE.negate ();
		this.HALF = C (0.5);
	}
	protected T PI, EPSILON;
	protected int COEFFICIENT_COUNT;
	protected ComplexValue<T> SQRT2PI, HALF;
	protected ComplexValue<T> ONE, NEGONE;
	protected ComplexValue<T> P0g, g;

	/**
	 * @param digits number of digits to identify required precision
	 */
	public void setPrecision (int digits)
	{
		EPSILON = componentManager.invert
			(
				componentManager.newScalar ((int) Math.pow (10, digits))
			);
	}

	/**
	 * The use of the reflection is necessary,
	 * it allows the function to extend the approximation to values of z where 
	 * Re(z) .LT. 0.5 (where the Lanczos method is not valid).
	 * @param z the parameter for gamma computation
	 * @return the function result
	 */
	public ComplexValue<T> reflection (ComplexValue<T> z)
	{
    	ComplexValue<T> sinPiZ = sin (z.times (PI));
    	ComplexValue<T> gamma1mZ = gamma (ONE.plus (z.negate ()));
    	ComplexValue<T> y = sinPiZ.times (gamma1mZ).inverted ().times (PI);
    	// pi / ( sin (pi * z) * gamma (1 - z) );  // Reflection formula
    	return threshold (y);
	}

	/**
	 * approximation of series of ratios of falling/rising factorials
	 * @param z the parameter for gamma computation
	 * @return the sum of the Ag series
	 */
	public ComplexValue<T> Ag (ComplexValue<T> z)
	{
        // A0 = 0.99999999999980993;							// P0(g)/2
    	ComplexValue<T> Ag = P0g.times (HALF);

     	for (int i=0; i<COEFFICIENT_COUNT; i++)
        {
        	Ag = z.plus (C (i + 1)).inverted ().times (P.get (i)).plus (Ag);
        }			// P#i / (z + i) + Ag

    	return Ag;
	}


	/**
	 * @param z the parameter for gamma computation
	 * @return the function result [ gamma (z+1) ]
	 */
	public ComplexValue<T> gammaZplus1 (ComplexValue<T> z)
	{
		ComplexValue<T>
		zPlusHalf = z.plus (HALF),								// z + 1/2
		t = zPlusHalf.plus (g);									// t = z + g + 1/2

        ComplexValue<T> y = SQRT2PI								// sqrt (2*pi)
        		.times
        		(
        			exp ( ln (t).times (zPlusHalf) )			// t ** (z + 1/2)
        		)
        		.times
	        	(
	        		exp (t.negate ())							// exp (-t)
	        	)
	        	.times
				(
					Ag (z)										// SIGMA [ p(g) * z--/z++ ]
				);
        return threshold (y);
	}


	/**
	 * compute gamma approximation
	 * @param z the parameter for gamma computation
	 * @return the function result
	 */
	public ComplexValue<T> gamma (ComplexValue<T> z)
	{
	    if ( ! componentManager.lessThan (z.Re (), HALF.Re ()) )
	    { return gammaZplus1 (z.plus (NEGONE)); }
	    else return reflection (z);
	}


	/**
	 * check for insignificant imaginary part of value
	 * @param z trim imaginary part when less than EPSILON
	 * @return real only value if threshold not met
	 */
	public ComplexValue<T> threshold (ComplexValue<T> z)
	{
		if (componentManager.lessThan (complexLib.abs (z.Im ()), EPSILON))
			return complexLib.C (z.Re ());
		else return z;
	}


	/**
	 * Chebychev Coefficients are collected into array
	 */
	void collectCoefficients ()
	{
		double[] p = new double[]
				{
					  676.5203681218851,
					-1259.1392167224028,
					  771.32342877765313,
					 -176.61502916214059,
					   12.507343278686905,
					   -0.13857109526572012,
					    9.9843695780195716e-6,
					    1.5056327351493116e-7,
				};
		for (double value : p) P.add ( C (value).Re () );
	}
	private ArrayList<T> P = new ArrayList<T> ();


	/**
	 * functions imported from complex library
	 * @param z parameter to function
	 * @return function result
	 */
	public ComplexValue<T> ln (ComplexValue<T> z) { return complexLib.ln (z); }
	public ComplexValue<T> exp (ComplexValue<T> z) { return complexLib.nativeExp (z); }
	public ComplexValue<T> sin (ComplexValue<T> z) { return complexLib.sin (z); }

	/**
	 * convert real value to complex rep
	 * @param re real value supplied as double float
	 * @return complex representation of real value
	 */
	public ComplexValue<T> C (double re) { return complexConversionManager.convertFromDouble (re); }


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<T> eval (ComplexValue<T> z) { return gamma (z); }
	public SpaceDescription<ComplexValue<T>> getSpaceDescription () { return complexValueManager; }
	public SpaceManager<ComplexValue<T>> getSpaceManager ()
	{ return complexValueManager; }


	/*
	 * translated from Python source:
	 * https://en.wikipedia.org/wiki/Lanczos_approximation
	 */


}

