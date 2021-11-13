
package net.myorb.math.polynomial.families;

import net.myorb.math.polynomial.families.jacobi.*;

import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.polynomial.PolynomialFamilyManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.PolynomialFamily;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

/**
 * support for Jacobi polynomial based algorithms
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class JacobiPolynomial<T> extends Polynomial<T>
			implements PolynomialFamily<T>
{

	public JacobiPolynomial
	(SpaceManager<T> manager, T a, T b) { super (manager); init (); this.a = a; this.b = b; }
	public JacobiPolynomial () { super (null); }
	protected T a, b;

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#init(net.myorb.math.SpaceManager)
	 */
	public void init (SpaceManager<T> manager)
	{ this.manager = manager; init (); }
	public void init ()
	{
		this.psm = new JacobiPolynomialSpaceManager<T>(manager);
	}
	protected PolynomialSpaceManager<T> psm;

	/**
	* use function inter-dependencies to generate series
	* @param upTo highest order of functions to be generated
	* @return the list of generated functions
	*/
	public PolynomialFamilyManager.PowerFunctionList<T> recurrence (int upTo)
	{
		return new SimpleJacobiRecurrenceFormula<T> (psm, a, b).constructFuntions (upTo);
	}

	/* (non-Javadoc)
	* @see net.myorb.math.PolynomialFamily#getName()
	*/
	public String getName () { return "Jacobi"; }
	
	/* (non-Javadoc)
	* @see net.myorb.math.PolynomialFamily#getFunctions(java.lang.String, int)
	*/
	public PolynomialFamilyManager.PowerFunctionList<T> getPolynomialFunctions (String identifier, int upTo)
	{
		return recurrence (upTo);
	}
	
	/* (non-Javadoc)
	* @see net.myorb.math.PolynomialFamily#getIdentifier(java.lang.String)
	*/
	public String getIdentifier (String kind)
	{
		String parameters[] = kind.split (",");
		if (parameters.length != 2) throw new RuntimeException ("Format of KIND must be 'a,b'");
		a = parseParameter (parameters[0], manager);
		b = parseParameter (parameters[1], manager);
		return "P";
	}
	public static <T> T parseParameter (String text, SpaceManager<T> manager)
	{
		return
		((ExpressionSpaceManager<T>) manager)
		.convertFromDouble (Double.parseDouble (text));
	}
	
	/**
	* unit test
	* @param args not used
	*/
	public static void main (String[] args)
	{
		Polynomial.RealNumbers mgr = new Polynomial.RealNumbers ();
		JacobiPolynomial<Double> P = new JacobiPolynomial<Double> (mgr, 0.2, 0.4);
		PolynomialFamilyManager.dump (P.recurrence (10), mgr);
	}

}

class SimpleJacobiRecurrenceFormula<T> extends JacobiRecurrenceFormula<T>
{

	public SimpleJacobiRecurrenceFormula
	(PolynomialSpaceManager<T> psm, T a, T b)
	{ super (psm); setParameters (a, b); }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.families.gegenbauer.GegenbauerRecurrenceFormula#seedRecurrence()
	 */
	public void seedRecurrence () { useSimpleSeed (); }

	private static final long serialVersionUID = -214642895928363453L;
}

