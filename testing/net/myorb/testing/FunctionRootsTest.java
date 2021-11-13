
package net.myorb.testing;

import net.myorb.math.*;
import net.myorb.math.computational.*;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

/**
 * unit test for function roots
 * @author Michael Druckman
 */
public class FunctionRootsTest extends FunctionRoots<Double>
	implements FunctionRoots.IterationStatusMonitor<Double>
{

	static DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();
	static ExponentiationLib<Double> lib = new ExponentiationLib<Double> (mgr);
	FunctionRootsTest () { super (mgr, lib); this.setIterationStatus (this); }

	public static void main (String[] args)
	{
		FunctionRootsTest roots =
			new FunctionRootsTest ();
		roots.setToleranceScale (14);

		System.out.println ();
		start (); System.out.println ("x^2 - 2 = 0 (Bisection Method)");
		roots.bisectionMethod
		(
				new SQRT (), 1.0, 1.5
				//new LOG (), 2.0, 3.0
				//new SIN (), 3.0, 3.5
		);
		stop (); System.out.println ("==="); System.out.println ();

		start (); System.out.println ("ln x - 1 = 0 (Linear Interpolation Method)");
		roots.linearInterpolationMethod
		(
				//new SQRT (), 1.0, 1.5
				new LOG (), 2.0, 3.0
				//new SIN (), 3.0, 3.5
		);
		stop (); System.out.println ("==="); System.out.println ();

		start (); System.out.println ("sin x = 0 (Secant Method)");
		roots.secantMethod
		(
				//new SQRT (), 1.0, 1.5
				//new LOG (), 2.0, 3.0
				new SIN (), 3.0, 3.5
		);
		stop (); System.out.println ("==="); System.out.println ();

		start (); System.out.println ("Newton Second Order");
		roots.newtonSecondOrderMethod
		(new LOG (), new LOGprime (), new LOGprime2 (), 3.0);
		stop (); System.out.println ("==="); System.out.println ();

		start (); System.out.println ("Newton First Order");
		roots.newtonRaphsonMethod
		(new LOG (), new LOGprime (), 3.0);
		stop (); System.out.println ("==="); System.out.println ();

		double delta = 1E-2;
		start (); System.out.println ("Newton Approximation (e)");
		roots.newtonMethodApproximated (new LOG (), 2.5, delta);
		stop (); System.out.println ("==="); System.out.println ();

		start (); System.out.println ("Newton Approximation (SQRT 2)");
		roots.newtonMethodApproximated (new SQRT (), 1.5, delta);
		stop (); System.out.println ("==="); System.out.println ();

		start (); System.out.println ("Newton Approximation (PI)");
		roots.newtonMethodApproximated (new SIN (), 3.1, delta);
		stop (); System.out.println ("==="); System.out.println ();

		start (); System.out.println ("Newton Approximation (Ei)");
		roots.newtonMethodApproximated (new Ei (), 0.5, delta);
		stop (); System.out.println ("==="); System.out.println ();
	}

	public void post(Double lo, Double hi, Double mid, Double fmid)
	{
		System.out.println (lo + " .. " + hi + " [" + mid + "] => " + fmid);
	}

	public static void start () { stamp = System.currentTimeMillis (); }
	public static void stop () { show (System.currentTimeMillis () - stamp); }
	public static void show (long time) { System.out.println (time+"ms"); }
	static long stamp;

}

class Base implements Function<Double>
{
	DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();
	public SpaceManager<Double> getSpaceDescription() { return mgr; }
	public SpaceManager<Double> getSpaceManager() { return mgr; }
	public Double eval (Double x) { return null; }	
}

class SQRT extends Base
{
	public Double eval (Double x) { return x*x - 2; }	
}

class SIN extends Base
{
	public Double eval (Double x) { return Math.sin (x); }	
}

class LOG extends Base
{
	public Double eval (Double x) { return Math.log (x) - 1; }	
}

class LOGprime extends Base
{
	public Double eval (Double x) { return 1/x; }	
}

class LOGprime2 extends Base
{
	public Double eval (Double x) { return -1/(x*x); }	
}

class Ei extends Base
{
	public Double eval (Double x)
	{
		return net.myorb.math.specialfunctions
			.ExponentialIntegral.Ei (x);
	}	
}
