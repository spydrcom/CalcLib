
package net.myorb.math.specialfunctions;

import net.myorb.math.specialfunctions.bessel.*;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.PolynomialFamilyManager;

import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionList;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

import java.util.Map;

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
			case 'H': return new HankelFunctions ().getFunctions (order, upTo, psm);			// Hankel functions

			case 'J': return new OrdinaryFirstKind ().getFunctions (order, upTo, psm);			// Bessel functions
			case 'Y': return new OrdinarySecondKind ().getFunctions (order, upTo, psm);
			case 'K': return new ModifiedSecondKind ().getFunctions (order, upTo, psm);
			case 'I': return new ModifiedFirstKind ().getFunctions (order, upTo, psm);

			case 'h': return new OrdinaryFirstKindStruve ().getFunctions (order, upTo, psm);	// STRUVE functions
			case 'k': return new OrdinarySecondKindStruve ().getFunctions (order, upTo, psm);
			case 'l': return new ModifiedFirstKindStruve ().getFunctions (order, upTo, psm);
			case 'm': return new ModifiedSecondKindStruve ().getFunctions (order, upTo, psm);

			case 'y': return new SphericalSecondKind ().getFunctions (order, upTo, psm);		// Spherical functions
			case 'j': return new SphericalFirstKind ().getFunctions (order, upTo, psm);
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


	/**
	 * @param kind the kind of Bessel function
	 * @param order the order integer or real (no complex yet)
	 * @param terms the number of term for series computations (approximation of infinity)
	 * @param parameters a set of name/value parameters for control of the algorithm
	 * @param lib a library of functions which can operate of the data type
	 * @return a function description for the specified function
	 */
	public SpecialFunctionFamilyManager.FunctionDescription<T> getFunction
	(String kind, T order, int terms, Map<String,Object> parameters, ExtendedPowerLibrary<T> lib)
	{
		switch (getForm (parameters))
		{
			case WEBER:
				switch (kind.charAt (0))
				{
				case 'J': return new OrdinaryFirstKind ().getFunction (order, terms, lib, psm);				// Bessel (Weber) functions
				case 'Y': return new OrdinarySecondKind ().getFunction (order, terms, lib, psm);
				case 'K': return new ModifiedSecondKind ().getFunction (order, terms, lib, psm);
				case 'I': return new ModifiedFirstKind ().getFunction (order, terms, lib, psm);
				}
			case STRUVE:
				switch (kind.charAt (0))
				{
				case 'H': return new OrdinaryFirstKindStruve ().getFunction (order, terms, lib, psm);		// STRUVE functions
				case 'K': return new OrdinarySecondKindStruve ().getFunction (order, terms, lib, psm);
				case 'M': return new ModifiedSecondKindStruve ().getFunction (order, terms, lib, psm);
				case 'L': return new ModifiedFirstKindStruve ().getFunction (order, terms, lib, psm);
				}
			case INTEGRAL:																					// Special Cases (integral algorithm)
				switch (kind.charAt (0))
				{
				case 'J': return new OrdinaryFirstKind ().getSpecialCase (order, terms, parameters, psm);
				case 'K': return new ModifiedSecondKind ().getSpecialCase (order, terms, parameters, psm);
				case 'I': return new ModifiedFirstKind ().getSpecialCase (order, terms, parameters, psm);
				case 'Y': return new OrdinarySecondKind ().getSpecialCase (order, terms, parameters, psm);
				}
			case SPHERICAL:																					// Spherical functions
				switch (kind.charAt (0))
				{
				case 'y': return new SphericalSecondKind ().getFunctions (order.toString (), 1, psm).get(0);
				case 'j': return new SphericalFirstKind ().getFunctions (order.toString (), 1, psm).get (0);
				}
			case DIGAMMA:
				return new OrdinarySecondKind ().getSpecialCase (order, terms, lib, parameters, psm);		// Yn identity with digamma and Jn
			case HANKEL: return new HankelFunctions ().getFunctions (order.toString (), 1, psm).get (0);	// Hankel complex conjugate pair
		}
		throw new RuntimeException ("Bessel function specifications not recognized");
	}


	/**
	 * identify the algorithm to be used.
	 *  each type of function may support multiple algorithm implementations.
	 *  these may be for efficiency or most notable is the LIM [a -&gt; n] to be avoided
	 * @param parameters the parameters given for source configuration of the function
	 * @return an identifier for the form recognized by parameters (default is WEBER)
	 */
	public Forms getForm (Map<String,Object> parameters)
	{
		Object specified;
		if ((specified = parameters.get ("form")) != null)
		{
			try
			{
				String name = specified.toString ();
				return Forms.valueOf (name.toUpperCase ());
			}
			catch (Exception e) {}
		}
		return Forms.WEBER;
	}
	public enum Forms {WEBER, STRUVE, HANKEL, SPHERICAL, INTEGRAL, DIGAMMA}


}

