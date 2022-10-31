
package net.myorb.math.specialfunctions.polylog;

import net.myorb.math.specialfunctions.PolylogRatioFormulas;
import net.myorb.math.complexnumbers.ComplexValue;

/**
 * eta function computed from Amdeberhan integral
 * @author Michael Druckman
 */
public class Eta extends Amdeberhan
{


	public Eta () { this ("eta"); }
	public Eta (String name) { super (name); changeGamma (); }

	public void changeGamma ()
	{
		// default is Lanczos if no change is made
//		PolylogRatioFormulas.cplxLib.setGammaFunction (new PiOverZ ());
		PolylogRatioFormulas.cplxLib.setGammaFunction (new Gamma ());
	}


	/*
	 * implementation of function
	 */

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> z)
	{
		return PolylogRatioFormulas.etaFromAmdeberhan (z, evaluateAmdeberhanEtaIntegralAt (z));
	}


}

/**
 * alternative formula for GAMMA function
 * - extending the PI function with evaluation being PI(z)/z
 */
class PiOverZ extends Pi
{
	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.polylog.Pi#eval(net.myorb.math.complexnumbers.ComplexValue)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> z)
	{
		return PolylogRatioFormulas.manager.multiply
		(
			PolylogRatioFormulas.manager.invert (z),
			evaluateGaussPiIntegralAt (z)
		);
	}
}
