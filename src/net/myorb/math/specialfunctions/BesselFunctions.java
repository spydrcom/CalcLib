
package net.myorb.math.specialfunctions;

import net.myorb.math.specialfunctions.bessel.*;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.PolynomialFamilyManager;

import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionList;
import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

/**
 * support for describing Bessel functions
 * @author Michael Druckman
 */
public class BesselFunctions<T> implements SpecialFunctionsFamily<T>
{


	/* (non-Javadoc)
	 * @see net.myorb.math.FamilyOfFunctions#getName()
	 */
	public String getName() { return "Bessel"; }


	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionsFamily#isPolynomial(java.lang.String)
	 */
	public boolean isPolynomial (String kind)
	{
		return
			kind.toUpperCase ().startsWith ("F") ||			// first kind
			kind.toUpperCase ().startsWith ("S");			// spherical
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.FamilyOfFunctions#getIdentifier(java.lang.String)
	 */
	public String getIdentifier (String kind) { return (kind.split (","))[0]; }


	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionsFamily#getFunctions(java.lang.String, int)
	 */
	public FunctionList<T> getFunctions (String kind, int upTo)
	{
		String
		parameters[] = kind.split (","), order = parameters[1];
		char id = parameters[0].charAt (0);

		switch (id)
		{
			case 'J': return new OrdinaryFirstKind ().getFunctions (order, upTo, psm);
			case 'Y': return new OrdinarySecondKind ().getFunctions (order, upTo, psm);
			case 'j': return new SphericalFirstKind ().getFunctions (order, upTo, psm);
			case 'y': return new SphericalSecondKind ().getFunctions (order, upTo, psm);
			case 'K': return new ModifiedSecondKind ().getFunctions (order, upTo, psm);
			case 'I': return new ModifiedFirstKind ().getFunctions (order, upTo, psm);
			case 'H': return new HankelFunctions ().getFunctions (order, upTo, psm);
		}

		return null;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.FamilyOfFunctions#init(net.myorb.math.SpaceManager)
	 */
	public void init (SpaceManager<T> manager)
	{
		PolynomialFamilyManager.importFamilyDescription ("Bessel", "*", manager);
		this.psm = new PolynomialSpaceManager<T>(manager);
		this.manager = manager;
	}
	protected PolynomialSpaceManager<T> psm;
	protected SpaceManager<T> manager;


	public SpecialFunctionFamilyManager.FunctionDescription<T> getFunction
	(String kind, T order, int terms, ExtendedPowerLibrary<T> lib)
	{
		switch (kind.charAt (0))
		{
			case 'J': return new OrdinaryFirstKind ().getFunction (order, terms, lib, psm);
			case 'Y': return new OrdinarySecondKind ().getFunction (order, terms, lib, psm);
			case 'K': return new ModifiedSecondKind ().getFunction (order, terms, lib, psm);
			case 'I': return new ModifiedFirstKind ().getFunction (order, terms, lib, psm);
		}

		return null;
	}


}

