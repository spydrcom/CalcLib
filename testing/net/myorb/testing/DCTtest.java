
package net.myorb.testing;

import net.myorb.math.computational.*;
import net.myorb.math.Polynomial.PowerFunction;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.computational.dct.*;

import net.myorb.data.abstractions.Function;
import net.myorb.math.*;

import java.util.List;

public class DCTtest
{
	public static ExpressionFloatingFieldManager manager = new ExpressionFloatingFieldManager ();

	public static void main (String... args)
	{
		int N = 40;

		Function<Double> f = new Test1 (), circle = new Test3 ();

		System.out.println ();
		DCT.Transform<Double> halfCircleT =
			new ClenshawCurtisQuadrature<Double>(manager).getTransform (circle, N, DCT.Type.I);
		System.out.println ("2*circle[-1,1]="+((ClenshawCurtisQuadrature.integrate (halfCircleT))*2));

		DCT.Transform<Double> circle8th = new ClenshawCurtisQuadrature<Double>(manager).getTransform (circle, 0.0, Math.sqrt(2)/2, N, DCT.Type.I);
		System.out.println ("8*circle[0,2\\2/2]="+((ClenshawCurtisQuadrature.integrate (circle8th)-0.25)*8));

		double crustArea =
				ClenshawCurtisQuadrature.integrate
					(new Test4(), 0, Math.sqrt(2)/2, N);
		double piApproximation = 8*(crustArea + Math.sqrt(2)/4);
		System.out.println ("8*(crust[0,2\\2/2]+slice)=" + piApproximation);

		LinearCoordinateChange<Double> lcc =
			new LinearCoordinateChange<Double> (0.0, 10.0, f, manager);
		LinearCoordinateChange.StdFunction<Double> f11 = lcc.functionWithAdjustedDomain();
		DCT.Transform<Double> t = new ClenshawCurtisQuadrature<Double>(manager).getTransform (f, 0.0, 10.0, N, DCT.Type.I);

		System.out.println(); System.out.println("f(cos(x)) VS dct(x) for 0<=x<=PI");

		double PI10 = Math.PI/10;
		for (double x=0; x<=Math.PI; x+=PI10)
		{
			double fx = f11.eval(Math.cos(x)), tx = t.eval(x);
			System.out.println ("f(cos)=" + fx + " dct=" + tx + " f-dct=" + (fx-tx));
		}

		List<Double> c = UnbiasedCoefficientCalculator.computeCoefficients(f11, N);
		System.out.println(); System.out.println("CCQ f(cos(t)) integral(1k) computation");
		System.out.println(ClenshawCurtisQuadrature.integrate(c)*lcc.getSlope());

		List<Double> c2k = EvenCoefficientCalculator.computeCoefficients(f11, N);
		System.out.println(); System.out.println("CCQ f(cos(t)) integral (2k) computation");
		System.out.println(ClenshawCurtisQuadrature.integrate(c2k)*lcc.getSlope());

		System.out.println(); System.out.println("CCQ f(t) integral (2k) computation");
		System.out.println (ClenshawCurtisQuadrature.integrate (f, 0, 10, N));

		System.out.println(); System.out.println("CCQ f(t) transform integral computation");
		System.out.println (ClenshawCurtisQuadrature.integrate (t));

		double tsq = TanhSinhQuadratureAlgorithms.Integrate (f, 0, 10, 1E-8, null);
		System.out.println(); System.out.println("TSQ f(t) integral computation [0,10]");
		System.out.println(tsq);

		System.out.println(); System.out.println("f11 2k coefficients");
		System.out.println(c2k);

		System.out.println(); System.out.println("transform coefficients");
		System.out.println(t.getCoefficients());

		System.out.println(); System.out.println("ChebyshevRecursiveCosineMultiples");
		System.out.println(ChebyshevRecursiveCosineMultiples.multiplesOfPiOver (8));
	}
}


class Test1 implements Function<Double>
{
	public Double eval (Double x) { return Math.exp(-x/5.0)*(2.0 + Math.sin(2.0*x)); }
	public SpaceManager<Double> getSpaceManager () { return getSpaceDescription (); }
	public SpaceManager<Double> getSpaceDescription () { return DCTtest.manager; }
	public double getSlope () { return 1; }
}

class Test2 implements LinearCoordinateChange.StdFunction<Double>
{
	public Double eval (Double x) { return Math.pow (1.0 - x, 5.0) * Math.pow (x, -1.0/3.0); }
	public SpaceManager<Double> getSpaceDescription () { return DCTtest.manager; }
	public SpaceManager<Double> getSpaceManager () { return getSpaceDescription (); }
	public PowerFunction<Double> describeLine() { return null; }
	public Double getIntercept() { return null; }
	public Double getSlope () { return 1.0; }
}

class Test3 implements LinearCoordinateChange.StdFunction<Double>
{
	public Double eval (Double x) { return Math.sqrt (1 - x*x); }
	public SpaceManager<Double> getSpaceDescription () { return DCTtest.manager; }
	public SpaceManager<Double> getSpaceManager () { return getSpaceDescription (); }
	public PowerFunction<Double> describeLine() { return null; }
	public Double getIntercept() { return null; }
	public Double getSlope () { return 1.0; }
}

/*
	// 45DEG is 1/8 of circle, sin=cos=sqrt(2)/2 
	r2 = 2\2 ; r2o2 = r2 / 2 
	
	crustLineSlope = 1 - r2
	!! crustLine(x) = 1 + crustLineSlope * x
	
	// subtract off the crust line leaving 2 triangles below
	!! crust (x) = f(x) - crustLine(x)
 */


class Test4 extends Test3
{
	double crustSlope = 1 - Math.sqrt (2);
	public Double eval (Double x) { return super.eval (x) - (1 + crustSlope*x); }
	public SpaceManager<Double> getSpaceDescription () { return DCTtest.manager; }
	public Double getSlope () { return 1.0; }
}
