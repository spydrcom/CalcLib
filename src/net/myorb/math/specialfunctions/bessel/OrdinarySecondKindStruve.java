
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription;

public class OrdinarySecondKindStruve extends UnderlyingOperators
{

	// K#a = H#a(x) - Y#a(x)

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.UnderlyingOperators#getFunction(java.lang.Object, int, net.myorb.math.polynomial.PolynomialSpaceManager)
	 */
	@Override
	public <T> FunctionDescription<T> getFunction
	(T parameter, int termCount, PolynomialSpaceManager<T> psm)
	{
		throw new RuntimeException ("Unimplemented");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.UnderlyingOperators#getFunction(java.lang.Object, int, net.myorb.math.ExtendedPowerLibrary, net.myorb.math.polynomial.PolynomialSpaceManager)
	 */
	@Override
	public <T> FunctionDescription<T> getFunction
	(T parameter, int termCount, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		throw new RuntimeException ("Unimplemented");
	}

}