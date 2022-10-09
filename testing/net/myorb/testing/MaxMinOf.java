
package net.myorb.testing;

import net.myorb.math.realnumbers.RealFunctionWrapper;
import net.myorb.math.computational.DirichletEta;
import net.myorb.math.computational.MaxMin;

import java.util.List;

public class MaxMinOf extends DirichletEta
{

	static MaxMin maxMin = new MaxMin ();

	static void compute (RealFunctionWrapper f)
	{
		maxMin.setFunction (f.toCommonFunction ());
		List <Double> c = maxMin.find (0, 1, 1E-8);
		System.out.println (c);

		double loEnd = maxMin.integralOver (c);
		double hiEnd = maxMin.eval (1, 20);
		double full = loEnd+hiEnd;

		System.out.println ("0..1:  " + loEnd);
		System.out.println ("0..20:  " + full);
	}

	public static void main (String[] args)
	{
		double sigma = 14.1347;
		System.out.println ("Re: "); compute (new RealFunctionWrapper ((x) -> cosSigmaTmu (x, sigma, 0.5)));
		System.out.println ("Im: "); compute (new RealFunctionWrapper ((x) -> sinSigmaTmu (x, sigma, 0.5)));
	}

}
