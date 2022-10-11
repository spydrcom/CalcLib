
package net.myorb.testing;

import net.myorb.math.computational.DirichletEta;

import net.myorb.math.computational.CyclicAspects;

import net.myorb.math.realnumbers.RealFunctionWrapper;

import java.util.List;

public class MaxMinOf extends DirichletEta
{

	static CyclicAspects aspects = new CyclicAspects ();

	static void compute (RealFunctionWrapper f)
	{
		aspects.setFunction (f.toCommonFunction ());
		List <Double> c = aspects.find (0, 1, 1E-8);
		System.out.println (c);

		double loEnd = aspects.integralOver (c);
		double hiEnd = aspects.eval (1, 20);
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
