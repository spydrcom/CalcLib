
package net.myorb.math.complexnumbers;

import net.myorb.math.specialfunctions.GenericIncompleteGamma;

/**
 * calculation of Incomplete Lower Gamma function.
 *  Incomplete Upper Gamma function is computed from Lower and GAMMA.
 * @author Michael Druckman
 */
public class ComplexGammaIncomplete
	extends GenericIncompleteGamma <ComplexValue <Double>>
{

	/**
	 * assume use of Lanczos GAMMA approximation object
	 * @param gammaFunction the implementation of the GAMMA function
	 */
	public ComplexGammaIncomplete (GammaLanczos<Double> gammaFunction)
	{
		super (gammaFunction, gammaFunction.getLibrary ());
	}

}

