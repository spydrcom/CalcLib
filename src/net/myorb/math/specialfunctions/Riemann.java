
package net.myorb.math.specialfunctions;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * implementation of the approximation of the Riemann Harmonic function
 * @author Michael Druckman
 */
public class Riemann
{

	/*
	 * EP(z,n,x) = (z + b*i) * log(x) / n
	 * I(x,n) = INTEGRAL [EP(-INFINITY,n)<=z<=EP(a,n,x)] ( exp(z) / z * <*> z )
	 * T(x) = Re { SIGMA [1<=n<=INFINITY] ( mu(n)/n * I(x,n) ) }
	 */

	static SpaceManager <Double> manager = new ExpressionFloatingFieldManager ();

	/**
	 * object that defines the harmonic function
	 */
	public static class Harmonic implements Function <Double>
	{
		
		public Harmonic (Double criticalRoot)
		{
			this (new ComplexValue <Double> (0.5, criticalRoot, manager));
		}

		public Harmonic (ComplexValue <Double> seed)
		{ this.a = seed.Re (); this.b = seed.Im (); constants (); }
		double a, b;

		public void constants ()
		{
			offsetAngle = Math.atan (b / a);
			abs = Math.sqrt (a*a + b*b);
		}
		double offsetAngle, abs;

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
		 */
		public Double eval (Double x)
		{
			double logx = Math.log (x), adjustedAngle = b*logx - offsetAngle;
			return Math.pow (x, a) * Math.cos (adjustedAngle) / (abs * logx);
		}

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
		 */
		public SpaceDescription <Double> getSpaceDescription () { return manager; }
		public SpaceManager <Double> getSpaceManager () { return manager; }

	}


	/**
	 * sum the values of the harmonics at parameter x
	 * @param x the parameter to the pi function
	 * @return the sum of harmonics at x
	 */
	public double sumHarmonics (double x)
	{
		double sum = 0.0;
		for (Harmonic harmonic : harmonics)
		{
			sum += harmonic.eval (x);
		}
		return sum;
	}
	Harmonic [] harmonics;


	/**
	 * compute pi function using the harmonic function
	 * @param x the parameter value setting the upper limit of the count of primes
	 * @return the computed estimate of count of primes below x
	 */
	public double pi (double x)
	{
		return Harmonic1.eval (x) - sumHarmonics (x);
	}
	Harmonic Harmonic1 = new Harmonic (new ComplexValue <Double> (1.0, 0.0, manager));


	public Riemann ()
	{
		int n = 0;
		harmonics = new Harmonic [ZEROES.length];
		for (double imag : ZEROES)
		{
			harmonics [n++] = new Harmonic (imag);
		}
	}


	/**
	 * the first 100 zeroes of the zeta function
	 */
	public static final double [] ZEROES =
	new double []
	{
			14.134725142,21.022039639,25.010857580,30.424876126,32.935061588,
			37.586178159,40.918719012,43.327073281,48.005150881,49.773832478,
			52.970321478,56.446247697,59.347044003,60.831778525,65.112544048,
			67.079810529,69.546401711,72.067157674,75.704690699,77.144840069,
			79.337375020,82.910380854,84.735492981,87.425274613,88.809111208,
			92.491899271,94.651344041,95.870634228,98.831194218,101.317851006,
			103.725538040,105.446623052,107.168611184,111.029535543,111.874659177,
			114.320220915,116.226680321,118.790782866,121.370125002,122.946829294,
			124.256818554,127.516683880,129.578704200,131.087688531,133.497737203,
			134.756509753,138.116042055,139.736208952,141.123707404,143.111845808,
			146.000982487,147.422765343,150.053520421,150.925257612,153.024693811,
			156.112909294,157.597591818,158.849988171,161.188964138,163.030709687,
			165.537069188,167.184439978,169.094515416,169.911976479,173.411536520,
			174.754191523,176.441434298,178.377407776,179.916484020,182.207078484,
			184.874467848,185.598783678,187.228922584,189.416158656,192.026656361,
			193.079726604,195.265396680,196.876481841,198.015309676,201.264751944,
			202.493594514,204.189671803,205.394697202,207.906258888,209.576509717,
			211.690862595,213.347919360,214.547044783,216.169538508,219.067596349,
			220.714918839,221.430705555,224.007000255,224.983324670,227.421444280,
			229.337413306,231.250188700,231.987235253,233.693404179,236.524229666
	};


}

