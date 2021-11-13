
package net.myorb.testing;

import net.myorb.math.complexnumbers.*;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.math.computational.PolynomialRoots;

import java.util.List;

/**
 * 
 * run unit tests on complex library
 * 
 * @author Michael Druckman
 *
 */
public class ComplexLibTest extends ComplexLibrary<Double>
{

	/**
	 * construct monitor object for complex data display
	 */
	static Monitor.Imaginary complexMonitor = new Monitor.Imaginary ();

	/**
	 * test series uses Double as complex component type
	 */
	public ComplexLibTest ()
	{
		super (new DoubleFloatingFieldManager (), null);
	}

	/**
	 * test runner for complex data objects
	 */
	public void runTests ()
	{
		// display cis(pi) using cos + i*sin
		complexMonitor.activity ("cis(pi) = ", cis (PI ()));
		System.out.println ();
		
		ComplexValue<Double> z;
		// compute and display exp (i*pi)
		complexMonitor.activity ("i*pi = ", (z = I (PI ())));
		complexMonitor.activity ("e^(i*pi) = ", exp (z));
		System.out.println ();
		
		z = C (-3d, 0d);
		complexMonitor.activity ("sqrt(-3) = ", z.csqrt());

		// compute SQRT of real negative numbers using ln and exp
		complexMonitor.activity ("sqrt(-2) = ", isqrt (real (-2)));
		complexMonitor.activity ("sqrt(-4) = ", isqrt (real (-4)));
		System.out.println ();
		
		PolynomialRoots<ComplexValue<Double>> roots =
			new PolynomialRoots<ComplexValue<Double>> (complexmanager, this);
		CoordinateSystems<Double> cs = new OptimizedCoordinateSystems ();

		List<ComplexValue<Double>> zeroes = roots.quadratic (C(-1), C(1), C(-1));

		System.out.println ("-x^2 + x - 1");
		System.out.println ("root 1 = " + zeroes.get (0));
		System.out.println (">>> to polar = " + cs.newPolarInstance (zeroes.get (0)));

		System.out.println ("root 2 = " + zeroes.get (1));
		System.out.println (">>> to polar = " + cs.newPolarInstance (zeroes.get (1)));
		System.out.println ("===");
		System.out.println ();
		
		zeroes = new OptimizedCubicEquation ().test ();
		System.out.println (">>> to polar = " + cs.newPolarInstance (zeroes.get (1)));
	}

	/**
	 * execute tests on complex objects
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		new ComplexLibTest ().runTests ();
	}

}
