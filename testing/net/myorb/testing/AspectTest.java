
package net.myorb.testing;

import net.myorb.math.computational.integration.polylog.*;

import java.util.List;

public class AspectTest extends DirichletEta
{

	static CyclicQuadrature aspects = new CyclicQuadrature ();

	static void compute
	(CyclicAspects.FunctionBody f, double sigma)
	{
		aspects.setFunction (f);
		List <Double> c = aspects.computeCycleSyncPoints
					(50, sigma, 4, 800);
		System.out.println (c);

		double loEnd = aspects.integralOver (c);
		//double hiEnd = aspects.eval (1, 20);
		//double full = loEnd+hiEnd;
		double full = loEnd;

		System.out.println ("0..1:  " + loEnd);
		System.out.println ("0..20:  " + full);
	}

	public void test (double sigma)
	{
		System.out.println ("Re: "); compute ((x) -> cosSigmaTmu (x, sigma, 0.5), sigma);
		System.out.println ("Im: "); compute ((x) -> sinSigmaTmu (x, sigma, 0.5), sigma);
	}

	public static void main (String[] args)
	{
		double sigma = 14.1347;
		new AspectTest ().test (sigma);
	}

}
