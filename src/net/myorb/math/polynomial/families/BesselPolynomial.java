
package net.myorb.math.polynomial.families;

import net.myorb.math.polynomial.families.bessel.*;
import net.myorb.math.specialfunctions.bessel.SphericalFirstKind;

import net.myorb.math.polynomial.PolynomialFamilyManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.PolynomialFamily;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

/**
 * support for Bessel (Jn) polynomial based algorithms
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class BesselPolynomial<T> extends Polynomial<T>
	implements PolynomialFamily<T>
{


	public BesselPolynomial (SpaceManager<T> manager)
	{
		super (manager); init ();
	}
	public BesselPolynomial () { super (null); }


	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#init(net.myorb.math.SpaceManager)
	 */
	public void init (SpaceManager<T> manager)
	{ this.manager = manager; init (); }
	public void init ()
	{
		this.psm = new PolynomialSpaceManager<T>(manager);
	}
	protected PolynomialSpaceManager<T> psm;


	/**
	 * use function inter-dependencies to generate series Jn
	 * @param upTo highest order of functions to be generated
	 * @return the list of generated functions
	 */
	public PolynomialFamilyManager.PowerFunctionList<T> recurrence (int upTo)
	{
		return new BesselRecurrenceFormula<T> (psm, TERMS).constructFuntions (upTo);
	}
	public static int TERMS = 25;


	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#getName()
	 */
	public String getName () { return "Bessel"; }


	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#getFunctions(java.lang.String, int)
	 */
	public PolynomialFamilyManager.PowerFunctionList<T> getPolynomialFunctions (String identifier, int upTo)
	{
		if (identifier.startsWith ("j"))
		{
			return getSphericalFunctions (upTo);
		}
		return recurrence (upTo);
	}
	PolynomialFamilyManager.PowerFunctionList<T> getSphericalFunctions (int upTo)
	{
		PolynomialFamilyManager.PowerFunctionList<T> list = new PolynomialFamilyManager.PowerFunctionList<T>();
		for (int n=0; n<=upTo; n++) list.add (SphericalFirstKind.getSphericalFirstKindPolynomial (n, 0, TERMS, psm));
		return list;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#getIdentifier(java.lang.String)
	 */
	public String getIdentifier (String kind)
	{
		if (kind.toUpperCase ().startsWith ("F")) return "J";
		else return "j";
	}


	/**
	 * unit test
	 * @param args not used
	 */
	public static void main (String[] args)
	{
		BesselPolynomial<Double> J =
			new BesselPolynomial<Double> (new Polynomial.RealNumbers ());
		PolynomialFamilyManager.PowerFunctionList<Double> list = J.recurrence (10);
		for (Polynomial.PowerFunction<Double> f : list) { System.out.println (J.psm.dump (f)); }
		//PolynomialFamilyManager.dump (list, J.getSpaceManager ());
	}


}

