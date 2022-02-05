
package net.myorb.math.computational;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.polynomial.families.LaguerrePolynomial;
import net.myorb.math.polynomial.PolynomialFamilyManager;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.data.abstractions.SimpleUtilities;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;
import net.myorb.math.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * Gauss-Laguerre Quadrature
 * @author Michael Druckman
 */
public class GLQuadrature
{


	/*
	 * integrals of the form INTEGRAL [0 <= x <= INFINITY] ( f(x) * exp (-x) dx )
	 * 

	 	SIGMA [1<=i<=n] ( w#i * f(x#i) )

		x#i is i-th root of L#n(x) and w#i is:
		w#i = x#i / [ (n+1)^2 * ( L#(n+1) (x#i) )^2 ]

		generalized:
		w#i = GAMMA (n+alpha+1) * x#i / [ n! * (n+1)^2 * ( L#(n+1)^(alpha) (x#i) )^2 ]
		for the form x^alpha * f(x) * exp (-x)
	 *
	 */


	/**
	 * an object that holds pairs of roots and weights
	 */
	public static class Laguerre
	{
		public Laguerre
		(double[] roots, double[] weights)
		{ setRoots (roots); setWeights (weights); }
		public void setWeights(double[] weights) { this.weights = weights; }
		public void setRoots(double[] roots) { this.roots = roots; }
		public double[] getWeights() { return weights; }
		public double[] getRoots() { return roots; }
		double[] roots, weights;
	}
	public static class LaguerreLists extends Laguerre
	{
		public List<Double> getRootsList()
		{
			return SimpleUtilities.toList (roots);
		}
		public void setRootsList(List<Double> roots)
		{
			SimpleUtilities.toFloats
			(
				roots.toArray (new Number[]{}),
				this.roots = new double[roots.size ()]
			);
		}
		public void setWeights(List<Double> weights)
		{
			SimpleUtilities.toFloats
			(
				weights.toArray (new Number[]{}),
				this.weights = new double[weights.size ()]
			);
		}
		public LaguerreLists () { super (null, null); }
	}


	/**
	 * build Laguerre polynomials to be used in approximation
	 * @param upTo the highest order of polynomial required
	 * @return a list of polynomial functions
	 */
	public static PolynomialFamilyManager.PowerFunctionList <Double> getLaguerrePolynomials (int upTo)
	{
		return new LaguerrePolynomial <> (new ExpressionFloatingFieldManager ()).recurrence (upTo);
	}


	/**
	 * use iterative root approximation
	 * @param function the Laguerre polynomial
	 * @param domainHi the HI end of the domain that holds all the roots
	 * @return the list of roots
	 */
	public static List <Double> computeRoots (Polynomial.PowerFunction <Double> function, double domainHi)
	{
		return IterativeRootApproximationOverDomain.locateRoots (function, 0d, domainHi, 0.01, 10);
	}


	/**
	 * compute the weights based on the roots
	 * @param roots the roots of the Laguerre polynomial used for the table
	 * @param poly the Laguerre polynomials
	 * @return the list of weights
	 */
	public static List <Double> computeWeights
		(
			List <Double> roots, PolynomialFamilyManager.PowerFunctionList <Double> poly
		)
	{
		int n = roots.size ();
		double nSquared = squared (n + 1);
		List <Double> w = new ArrayList <> ();
		Polynomial.PowerFunction <Double> f = poly.get (n + 1);
		for (double root : roots)
		{
			// w#i = x#i / [ (n+1)^2 * ( L#(n+1) (x#i) )^2 ]
			w.add ( root / ( nSquared * squared (f.eval (root)) ) );
		}
		return w;
	}
	static double squared (double x) { return Math.pow (x, 2); }


	/**
	 * @param laguerre the Laguerre weights table
	 * @param poly the Laguerre polynomials
	 */
	public static void computeWeights
	(LaguerreLists laguerre, PolynomialFamilyManager.PowerFunctionList <Double> poly)
	{ laguerre.setWeights (computeWeights (laguerre.getRootsList (), poly)); }


	/**
	 * @param laguerre the Laguerre weights table
	 * @param forOrder the order of the polynomial for table
	 * @param domainHi the domain HI value holding all roots
	 */
	public static void computeWeights (LaguerreLists laguerre, int forOrder, double domainHi)
	{
		PolynomialFamilyManager.PowerFunctionList <Double> poly = getLaguerrePolynomials (forOrder + 1);
		laguerre.setRootsList (computeRoots (poly.get (forOrder), domainHi)); computeWeights (laguerre, poly);
	}


	/**
	 * @param forOrder the order of the table
	 * @param domainHi the domain HI value holding all roots
	 * @return the Laguerre weights table
	 */
	public static LaguerreLists computeWeights (int forOrder, double domainHi)
	{
		LaguerreLists laguerre = new LaguerreLists ();
		computeWeights (laguerre, forOrder, domainHi);
		return laguerre;
	}


	/**
	 * use GLQ for approximation of integral
	 * @param f the function being integrated
	 * @param using the Laguerre weights table
	 * @return the approximation of the integral
	 */
	public static double approximateIntegral (Function <Double> f, Laguerre using)
	{
		double sum = 0.0;
		for (int i = 0; i < using.roots.length; i++)
		{ sum += using.weights [i] * f.eval ( using.roots [i] ); }
		return sum;
	}


	/**
	 * check calculated roots, print errors
	 */
	public static void checkRoots ()
	{
		PolynomialFamilyManager.PowerFunctionList <Double>
			poly =  getLaguerrePolynomials (25);
		Polynomial.PowerFunction <Double> L = poly.get (20);
		List <Double> roots = computeRoots (L, 100d);

		for (double root : roots)
		{
			System.out.print (root); System.out.print (" - ");
			System.out.println (L.eval (root));
		}
	}


	/**
	 * run unit test
	 * @param args not used
	 */
	public static void main (String... args)
	{
		computeGamma (new L20 ());
		computeGamma (new L25 ());
		computeGamma (new L30 ());
//		computeL30 ();
//		checkRoots ();
	}


	/**
	 * gamma(x) = INTEGRAL t^(x-1) * e^(-t) dt
	 * @param L table to use for calculation
	 */
	public static void computeGamma (Laguerre L)
	{
		System.out.println
		(
			approximateIntegral
			(
				new Integrand ()
				{
					// gamma(1.5) = sqrt(pi) / 2 = 0.8862269254
					public Double eval (Double x) { return Math.pow (x, 0.5); }
				}, L
			)
		);
		System.out.println
		(
			approximateIntegral
			(
				new Integrand ()
				{
					// gamma(5) = 4! = 24
					public Double eval (Double x) { return Math.pow (x, 4); }
				}, L
			)
		);
		System.out.println
		(
			approximateIntegral
			(
				new Integrand ()
				{
					public Double eval (Double x) { return Math.pow (x, 5); }
				}, L
			)
		);
	}
	public static class Integrand implements Function <Double>
	{
		@Override public Double eval(Double x) { return null; }
		@Override public SpaceDescription<Double> getSpaceDescription() { return getSpaceManager(); }
		@Override public SpaceManager<Double> getSpaceManager()
		{ return new ExpressionFloatingFieldManager (); }
	}


	/**
	 * compute and show L20
	 */
	public static void computeL20 ()
	{
		int n = 20;
		PolynomialFamilyManager.PowerFunctionList <Double>
			poly = getLaguerrePolynomials (n + 1);
		List <Double> roots = computeRoots (poly.get (n), 75d);
		show ("roots", roots); show ("weights", computeWeights (roots, poly));
	}

	/**
	 * compute and show L20 (using encapsulated method)
	 */
	public static void computeL20encapsulated ()
	{
		int n = 20; show ("roots", computeWeights (n, 75d));
	}

	/**
	 * compute and show L25
	 */
	public static void computeL25 ()
	{
		int n = 25; show ("roots", computeWeights (n, 100d));
	}

	/**
	 * compute and show L30
	 */
	public static void computeL30 ()
	{
		int n = 30; show ("roots", computeWeights (n, 110d));
	}

	/**
	 * console display of computed results
	 * @param title a title for the display
	 * @param values the values to be displayed
	 */
	public static void show (String title, LaguerreLists values)
	{
		show ("roots", values.roots); show ("weights", values.weights);
	}
	public static void show (String title, double[] values)
	{
		List <Double> l = new ArrayList <> ();
		for (double d : values) l.add (d);
		show (title, l);
	}
	public static void show (String title, List <Double> values)
	{
		System.out.println (); System.out.println (title);
		for (double v : values) System.out.println (v);
	}


}


/**
 * roots and weights based on Laguerre L30
 */
class L30 extends GLQuadrature.LaguerreLists
{
	L30 () { GLQuadrature.computeWeights (this, 30, 110d); }
}


/**
 * roots and weights based on Laguerre L25
 */
class L25 extends GLQuadrature.LaguerreLists
{
	L25 () { GLQuadrature.computeWeights (this, 25, 100d); }
}


/**
 * roots and weights based on Laguerre L20.
 *  tables of pre-computed constants are used.
 *  this seems to hit sweet spot, L25 and L30 fall off.
 */
class L20 extends GLQuadrature.Laguerre
{
	public static final double
		ROOTS [] =
		{
			0.07053988969198875, 0.37212681800161224, 0.9165821024832469,  1.7073065310284765, 2.749199255311559,
			4.048925313834116,   5.615174970794864,   7.4590174544346635,  9.594392861887455, 12.038802573498543,
			14.814293392628478,	17.948895626051375,  21.478788114679286,  25.451703044816878, 29.932554409031265,
			35.01343440059207,	40.83305696725378,   47.61999407426332,	  55.81079574393782,  66.52441652620064
		},
		W [] =
		{
			0.16874680185111385,    0.2912543620060787,     0.26668610286692185,    0.16600245326932433,   0.07482606466875187,
			0.024964417309030824,   0.00620255084249277,    0.0011449623911279415,  1.5574177195012636E-4, 1.5401440848906043E-5,
			1.0864862431542966E-6,  5.33012153757656E-8,    1.757980397068417E-9,   3.725503778824593E-11, 4.767525042236824E-13,
			3.3728461776696913E-15, 1.1550141051887747E-17, 1.5395222493614018E-20, 5.286442647829175E-24, 1.6564566122984816E-28
		};
	L20 () { super (ROOTS, W); }
}


/**
 * roots and weights based on Laguerre L9
 */
class L9 extends GLQuadrature.Laguerre
{
	public static final double
		ROOTS [] =
		{
			0.152322228, 0.807220023, 2.005135156, 3.783473973, 6.204956778,
			9.372985252, 13.46623691, 18.83359779, 26.37407189
		},
		W [] =
		{
			0.336126422, 0.41121398, 0.199287525, 0.047460563, 0.005599627,
			0.00030525, 6.59212E-06, 4.11077E-08, 3.29087E-11
		};
	L9 () { super (ROOTS, W); }
}

