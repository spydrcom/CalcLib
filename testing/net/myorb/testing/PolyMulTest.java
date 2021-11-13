
package net.myorb.testing;

import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.math.*;
import net.myorb.math.polynomial.PolynomialSpaceManager;

public class PolyMulTest extends PolynomialSpaceManager<Double>
{


	public PolyMulTest ()
	{ super (new DoubleFloatingFieldManager ()); }


	public static void main(String[] args)
	{
		PolyMulTest p = new PolyMulTest ();

		Polynomial.PowerFunction<Double>
		XP1 = p.getPolynomialFunction (p.newCoefficients (1d, 1d)),
		XP2 = p.getPolynomialFunction (p.newCoefficients (2d, 1d)),
		XM1 = p.getPolynomialFunction (p.newCoefficients (-1d, 1d));

		Polynomial.PowerFunction<Double>
		XP1XM1 = p.multiply (XP1, XM1),
		XP1XP2 = p.multiply (XP1, XP2);

		Polynomial.PowerFunction<Double>
		X4 = p.multiply (XP1XM1, XP1XP2);

		System.out.println (p.toString (XP1));
		System.out.println (p.toString (XP1XM1));
		System.out.println (p.toString (X4));
		System.out.println ("===");

		Polynomial.PowerFunction<Double> R =
			p.getPolynomialFunction (p.newCoefficients ());;
		Polynomial.PowerFunction<Double>
		Q = p.divide (X4, XP1, R);
		p.dump (Q, R);

		Q = p.divide (Q, XP1, R);
		p.dump (Q, R);

		Polynomial.PowerFunction<Double>
		Q2 = p.divide (Q, XP1, R);
		p.dump (Q2, R);

		Q = p.divide (X4, XM1, R);
		p.dump (Q, R);

		Q2 = p.divide (Q, XM1, R);
		p.dump (Q2, R);

		Polynomial.PowerFunction<Double>
			X5 = p.multiply (Q, XP1);
		System.out.println (p.toString (X5));
	}


	void dump (Polynomial.PowerFunction<Double> Q, Polynomial.PowerFunction<Double> R)
	{
		System.out.println ("Q = "+toString (Q));
		if (isZero (R)) System.out.println ("Zero remainder");
		else System.out.println ("R = "+toString (R));
		System.out.println ("===");
	}

}
