
package net.myorb.testing;

import net.myorb.math.*;
import net.myorb.math.computational.FunctionRoots;
import net.myorb.math.computational.PolynomialRoots;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

public class RootsTest<T> extends Polynomial<T>
{


	static DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();

	static ExponentiationLib<Double> lib = new ExponentiationLib<Double> (mgr);
	static PolynomialRoots<Double> roots = new PolynomialRoots<Double> (mgr, lib);
	static PolynomialSpaceManager<Double> pfm = PolynomialSpaceManager.newInstance ();

	static void init ()
	{
		mgr = new DoubleFloatingFieldManager ();
		lib = new ExponentiationLib<Double> (mgr);
		roots = new PolynomialRoots<Double> (mgr, lib);
		pfm = PolynomialSpaceManager.newInstance ();		
	}

	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 * @param lib an implementation of the power library
	 */
	public RootsTest
		(SpaceManager<T> manager, PowerLibrary<T> lib)
	{ super (manager); this.plib = lib; proots = new PolynomialRoots<T> (manager, lib); }
	protected PolynomialRoots<T> proots;
	protected PowerLibrary<T> plib;


	static void quadraticTest ()
	{
		System.out.println ("---");
		System.out.println ("-  Quadratic equation tests");
		System.out.println ("---");
		System.out.println ();

		Double a = 1.0, b = -5.0, c = 6.0;
		System.out.println ("x^2 - 5x + 6 => " + roots.quadratic (a, b, c));
		System.out.println ();

		a = 1.0; b = -1.0; c = -1.0;
		System.out.println ("golden ratio =>   x^2 - x - 1 = 0");
		System.out.println ("conjugate (PHI), phi = " + roots.quadratic (a, b, c));
		System.out.println ();

		System.out.println ("---");
		System.out.println ("-  fast SQRT test");
		System.out.println ("---");
		System.out.println ();

		System.out.println ("fastSqrt(2) = " + roots.fastSqrt (2d));
		System.out.println ();

		System.out.println ("---");
		System.out.println ("-  Quadratic equation applied to power function");
		System.out.println ("---");
		System.out.println ();

		//-72.0, -90.0, 24.0
		Polynomial.Coefficients<Double> p = pfm.newCoefficients (-72.0, -90.0, 24.0);
		System.out.println (p); System.out.println (roots.quadratic (p));
	}


	static void rootDump (String tag, Polynomial.Coefficients<Double> coefficients, Double root)
	{
		System.out.println (tag + " root = " + root);
		Double poly = roots.evaluatePolynomial(coefficients, root);
		System.out.println ("f(root) = " + poly);
		System.out.println ("===");
		System.out.println ();
	}

	static void rootTest ()
	{
		Double x = 1.0;
		Polynomial.Coefficients<Double> coefficients = new Polynomial.Coefficients<Double> ();
		coefficients = pfm.newCoefficients (-2.0, 0.0, 1.0);

		Double root =
			roots.newtonRaphsonMethod
			(
					coefficients, x
			);
		rootDump ("NR sqrt(2)", coefficients, root);

		// (4x-1) * (5x-4) = 20*x^2 - 16*x - 5*x + 4
		coefficients = pfm.newCoefficients (4.0, -21.0, 20.0);
		System.out.println ("20x^2 - 21x + 4 ===");				// roots are 0.25 and 0.80


		// tests for Newton-Raphson method

		System.out.println ("--");
		System.out.println ("-- tests for Newton-Raphson method");
		System.out.println ("--");

		root = roots.newtonRaphsonMethod
			(
					coefficients, 0.0
			);
		rootDump ("NR " + coefficients + " @0.0 ", coefficients, root);

		root = roots.newtonRaphsonMethod
			(
					coefficients, 1.0
			);
		rootDump ("NR " + coefficients + " @1.0 ", coefficients, root);

		// derivative hits zero at local max or min, method would have division by zero error

		try
		{
			root = roots.newtonRaphsonMethod
			(
					coefficients, 0.525
			);
			rootDump ("NR " + coefficients + " @0.525 ", coefficients, root);
		}
		catch (Exception e)
		{
			System.out.println ("*** NR " + coefficients + " @0.525 *** " + e.getLocalizedMessage ());
			System.out.println ();
		}

		// tests for Laguerre method

		System.out.println ("--");
		System.out.println ("-- tests for Laguerre method");
		System.out.println ("--");

		Double laguerreRoot = roots.laguerreMethod (coefficients, 0.0);
		rootDump ("LG " + coefficients + " @0.0 ", coefficients, laguerreRoot);
		laguerreRoot = roots.laguerreMethod (coefficients, 1.0);
		rootDump ("LG " + coefficients + " @1.0 ", coefficients, laguerreRoot);

		// tests for bisection method

		System.out.println ("--");
		System.out.println ("-- tests for bisection method");
		System.out.println ("--");

		PowerFunction<Double> p = roots.getPolynomialFunction (coefficients);
		Double bisectionRoot = new FunctionRoots<Double> (mgr, lib).bisectionMethod (p, 0.0, 0.5);
		rootDump ("BS " + coefficients + " @0.0-0.5 ", coefficients, bisectionRoot);
		bisectionRoot = new FunctionRoots<Double> (mgr, lib).bisectionMethod (p, 0.6, 2.0);
		rootDump ("BS " + coefficients + " @0.6-2.0 ", coefficients, bisectionRoot);

		root = roots.newtonRaphsonMethod (cpc, 0.1);
		rootDump ("NR " + cpc + " @0.1 ", cpc, root);

		laguerreRoot = roots.laguerreMethod (cpc, 1.5);
		rootDump ("LG " + cpc + " @1.5 ", cpc, laguerreRoot);

		laguerreRoot = roots.laguerreMethod (cpc, 7.2);
		rootDump ("LG " + cpc + " @7.2 ", cpc, laguerreRoot);

		System.out.println ("cpc' zero");
		p = roots.getPolynomialFunction (cpc);
		PowerFunction<Double> d = roots.getFunctionDerivative (p);
		root = roots.newtonRaphsonMethod (coefficients = d.getCoefficients (), 0.0);
		rootDump ("NR " + coefficients + " D ", coefficients, root);
		rootDump (cpc.toString (), cpc, root);

		Double xapprox = root-0.1;
		System.out.println ("before Derivative zero");
		Double nrroot = roots.newtonRaphsonMethod (cpc, xapprox);
		rootDump ("NR " + cpc + " @" + xapprox + " ", cpc, nrroot);

		xapprox = root+0.1;
		System.out.println ("after Derivative zero");
		nrroot = roots.newtonRaphsonMethod (cpc, xapprox);
		rootDump ("NR " + cpc + " @" + xapprox + " ", cpc, nrroot);
		
		System.out.println (roots.evaluateEquation (cpc));
		System.out.println ("polynomial => " + pfm.toString (p));
		System.out.println ("derivative => " + pfm.toString (d));
		
		Polynomial.Coefficients<Double>
			p1 = pfm.newCoefficients (2.0, -4.0, 3.0),
			p2 = pfm.newCoefficients (1.0, 4.0, 2.0);
		PowerFunction<Double> pf = pfm.multiply
		(
			pfm.getPolynomialFunction(p1), pfm.getPolynomialFunction(p2)
		);
		System.out.println (pfm.toString (pf));
		System.out.println ("=======================================");

		pf = pfm.getPolynomialFunction
			(pfm.newCoefficients (5.0, 1.0));
		pf = multiply (pf, pfm.newCoefficients (-4.0, 1.0));
		pf = multiply (pf, pfm.newCoefficients (-1.0, 5.0));
		pf = multiply (pf, pfm.newCoefficients (-7.0, 1.0));
		pf = multiply (pf, pfm.newCoefficients (-12.0, 1.0));
		pf = multiply (pf, pfm.newCoefficients (-3.0, 2.0));

		System.out.println (pfm.toString (pf));
		System.out.println (roots.evaluateEquation (pf));
	}
	static PowerFunction<Double> multiply
	(PowerFunction<Double> p, Polynomial.Coefficients<Double> c)
	{ return pfm.multiply (p, pfm.getPolynomialFunction (c)); }

	static double xLogX (double x) { return 1.0 - x * lib.ln (x); }

	static Function<Double> getFunction ()
	{
		return new Function<Double>()
		{
			public Double eval (Double x)
			{
				return xLogX (x);
			}
			public SpaceManager<Double> getSpaceDescription () { return mgr; }
			public SpaceManager<Double> getSpaceManager () { return mgr; }
		};
	}

	static void bsTest ()
	{
		FunctionRoots<Double> fr = new FunctionRoots<Double>(mgr, lib);
		System.out.println (fr.bisectionMethod (getFunction (), 1.5, 2.0));
	}

	/**
	 * execute tests
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		cpc = pfm.newCoefficients (-72.0, 36.0, 4.0, -1.0);
		quadraticTest ();
		rootTest ();
		bsTest ();
	}
	static Polynomial.Coefficients<Double> cpc;


}

