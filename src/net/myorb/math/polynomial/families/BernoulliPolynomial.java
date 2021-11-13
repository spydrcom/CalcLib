
package net.myorb.math.polynomial.families;

import net.myorb.math.polynomial.PolynomialFamily;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.PolynomialFamilyManager;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.computational.Combinatorics;

import net.myorb.math.PowerPrimitives;
import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

/**
 * support for Bernoulli polynomial based algorithms
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class BernoulliPolynomial<T> extends Polynomial<T>
	implements PolynomialFamily<T>
{

	public BernoulliPolynomial
	(SpaceManager<T> manager) { super (manager); init (); }
	public BernoulliPolynomial () { super (null); }
	
	/* (non-Javadoc)
	* @see net.myorb.math.PolynomialFamily#init(net.myorb.math.SpaceManager)
	*/
	public void init (SpaceManager<T> manager)
	{ this.manager = manager; init (); }
	public void init ()
	{
		this.psm = new BernoulliPolynomialSpaceManager<T>(manager);
		this.X = psm.getPolynomialFunction (newCoefficients (manager.getZero (), manager.getOne ()));
		this.combinatorics = new Combinatorics<T> (manager, new PowerPrimitives<T> (manager));
	}
	protected PolynomialSpaceManager<T> psm;
	protected Combinatorics<T> combinatorics;
	protected Polynomial.PowerFunction<T> X;
	
	
	/**
	* calculate each coefficient independently
	* @param upTo highest order of functions to be generated
	* @return the list of generated functions
	*/
	public PolynomialFamilyManager.PowerFunctionList<T> bruteForce (int upTo)
	{
		PolynomialFamilyManager.PowerFunctionList<T>  result =
				new PolynomialFamilyManager.PowerFunctionList<T> ();

		for (int n=0; n<=upTo; n++)
		{
			Polynomial.PowerFunction<T> Bn = psm.getZero ();

			for (int k=0; k<=n; k++)
			{
				Bn = psm.addTermFor (coef (n, k), X, k, Bn);
			}

			result.add (Bn);
		}

		return result;
	}

	/**
	 * @param n highest order
	 * @param k sequence index
	 * @return n##k * B(n - k)
	 */
	T coef (int n, int k)
	{ return manager.multiply (choose (n, k), combinatorics.firstKindBernoulli (n - k)); }
	T choose (int n, int k) { return combinatorics.binomialCoefficient (manager.newScalar (n), manager.newScalar (k)); }
	
	/* (non-Javadoc)
	* @see net.myorb.math.PolynomialFamily#getName()
	*/
	public String getName () { return "Bernoulli"; }
	
	/* (non-Javadoc)
	* @see net.myorb.math.PolynomialFamily#getFunctions(java.lang.String, int)
	*/
	public PolynomialFamilyManager.PowerFunctionList<T> getPolynomialFunctions (String identifier, int upTo)
	{
		return bruteForce (upTo);
	}
	
	/* (non-Javadoc)
	* @see net.myorb.math.PolynomialFamily#getIdentifier(java.lang.String)
	*/
	public String getIdentifier (String kind) { return "B"; }
	
	/**
	* unit test
	* @param args not used
	*/
	public static void main (String[] args)
	{
		ExpressionFloatingFieldManager mgr =
			new ExpressionFloatingFieldManager ();
		BernoulliPolynomial<Double> B = new BernoulliPolynomial<Double> (mgr);
		PolynomialFamilyManager.dump (B.bruteForce (10), mgr);

		for (int i=0; i<10;i++)
		{
			System.out.println (B.combinatorics.firstKindBernoulli (i));
		}
	}

}

class BernoulliPolynomialSpaceManager<T> extends PolynomialSpaceManager<T>
{

	/**
	* @param manager data type manager is required
	*/
	public BernoulliPolynomialSpaceManager
	(SpaceManager<T> manager)
	{
		super (manager);
	}
	
	///* (non-Javadoc)
	//* @see net.myorb.math.PolynomialSpaceManager#formatTerm(int, java.lang.Object, java.lang.StringBuffer)
	//*/
	//public void formatTerm (int termNo, T c, StringBuffer buffer)
	//{
	//// the constant for the term is displayed for c ~= 1
	//if (!formatTermOperation (c, termNo, buffer)) buffer.append (" * ");
	//
	//// then the Hermite H function of (x) replaces the traditional x^n
	//buffer.append ("H[").append (termNo).append ("](x)");
	//}
	
}
