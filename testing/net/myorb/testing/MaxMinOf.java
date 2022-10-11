
package net.myorb.testing;

import net.myorb.math.computational.DirichletEta;

import net.myorb.math.computational.CyclicAspects;

import java.util.List;

public class MaxMinOf extends DirichletEta
{

	static CyclicAspects aspects = new CyclicAspects ();

	static void compute (CyclicAspects.FunctionBody f)
	{
		aspects.setFunction (f);
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
		System.out.println ("Re: "); compute ((x) -> cosSigmaTmu (x, sigma, 0.5));
		System.out.println ("Im: "); compute ((x) -> sinSigmaTmu (x, sigma, 0.5));
	}

}
