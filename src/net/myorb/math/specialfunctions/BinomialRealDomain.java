
package net.myorb.math.specialfunctions;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

/**
 * Binomial Coefficient on real domain using Gamma from spline
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class BinomialRealDomain extends Binomial <Double>
{

	public BinomialRealDomain
		(
			SpaceManager <Double> manager,
			ExtendedPowerLibrary <Double> lib
		)
	{
		super
		(manager, lib, null);
		this.gamma = new Gamma ();
		this.sflib = (x) -> gamma.eval (x);
	}
	Gamma gamma;

}
