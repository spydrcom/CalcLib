
package net.myorb.testing;

import net.myorb.math.realnumbers.*;
import net.myorb.math.*;

/**
 * 
 * test library algorithms using simple floating data
 * 
 * @author Michael Druckman
 *
 */
public class FloatingLibraryTest
{

	
	/**
	 * monitor object for display of test results
	 */
	static Monitor.Float floatMonitor = new Monitor.Float ();

	
	/**
	 * 
	 * extend Exponentiation library to allow simple coding of tests
	 * 
	 * @author Michael Druckman
	 *
	 */
	static class ExponentiationTest extends ExponentiationLib<Double>
	{
		static final Double E = 2.7182818284590452353602874713527, Ln10 = 2.30258509299404568;

		ExponentiationTest () { super (new DoubleFloatingFieldManager ()); }
		
		void runTestSeries ()
		{
			System.out.println ("Exponentiation Library <Double>");
			System.out.println ();

			System.out.println ("> POW");
			floatMonitor.activity ("2^3 = ", pow (2.0, 3), 8.0);
			floatMonitor.activity ("5^3 = ", pow (5.0, 3), 125.0);
			floatMonitor.activity ("2^4 = ", pow (2.0, 4), 16.0);
			System.out.println ();
			
			System.out.println ("> EXP");
			floatMonitor.activity ("e ^ 2*ln 10 = ", exp (2.0 * ln (10.0)), 100.0);
			floatMonitor.activity ("e^1 = ", exp (1.0), E);
			System.out.println ();

			System.out.println ("> LOG");
			floatMonitor.activity ("ln 5 = ", ln(5.0));
			floatMonitor.activity ("ln 10 = ", ln(10.0), Ln10);
			floatMonitor.activity ("ln .1 = ", ln(0.1), -Ln10);
			floatMonitor.activity ("ln e^5 = ", ln(exp (5.0)), 5.0);
			floatMonitor.activity ("ln e^2 = ", ln(exp (2.0)), 2.0);
			System.out.println ();
			
			System.out.println ("> SQRT");
			floatMonitor.activity ("sqrt(4) = ", sqrt (4.0), 2.0);
			floatMonitor.activity ("sqrt(25) = ", sqrt (25.0), 5.0);
			floatMonitor.activity ("sqrt(100) = ", sqrt (100.0), 10.0);
			floatMonitor.activityErrorSquared ("sqrt(2) = ", sqrt (2.0), 2.0);
			System.out.println ();
		}
	}


	/**
	 * 
	 * extend trig library to allow simple coding of tests
	 * 
	 * @author Michael Druckman
	 *
	 */
	static class TrigTest extends TrigLib<Double>
	{
		static final Double PI = 3.14159265358979323;

		TrigTest () { super (new DoubleFloatingFieldManager ()); }
		
		void runTestSeries ()
		{
			System.out.println ("Trig Library <Double>");
			System.out.println ();

			System.out.println ("> SIN");
			floatMonitor.activity ("sin(0) = ", sin (0.0));
			floatMonitor.activity ("sin(pi/6) = ", sin (PI/6), 0.5);
			floatMonitor.activityErrorSquared ("sin(pi/4) = ", sin (PI/4), 0.5);
			floatMonitor.activityErrorSquared ("sin(pi/3) = ", sin (PI/3), 0.75);
			floatMonitor.activity ("sin(pi/2) = ", sin (PI/2), 1.0);
			floatMonitor.activityErrorSquared ("sin(pi/4) = ", sin (PI/4), 0.5);
			floatMonitor.activityErrorSquared ("sin(5*pi/4) = ", sin (5*PI/4), 0.5);
			floatMonitor.activityErrorSquared ("sin(-pi/4) = ", sin (-PI/4), 0.5);
			System.out.println ();
			
			System.out.println ("> COS");
			floatMonitor.activity ("cos(0) = ", cos (0.0), 1.0);
			floatMonitor.activity ("cos(pi/3) = ", cos (PI/3), 0.5);
			floatMonitor.activityErrorSquared ("cos(pi/4) = ", cos (PI/4), 0.5);
			floatMonitor.activityErrorSquared ("cos(pi/6) = ", cos (PI/6), 0.75);
			floatMonitor.activity ("cos(pi/2) = ", cos (PI/2), 0.0);
			floatMonitor.activity ("cos(pi) = ", cos (PI), -1.0);
			floatMonitor.activityErrorSquared ("cos(7*pi/4) = ", cos (7*PI/4), 0.5);
			floatMonitor.activityErrorSquared ("cos(-pi/4) = ", cos (-PI/4), 0.5);
			System.out.println ();
			
			System.out.println ("> ATAN");
			Double piComputedAtan = 4 * atan (1.0);									// PI = 4 atan (1) from tan (PI/4) = 1
			floatMonitor.activity ("4*atan(1) = ", piComputedAtan, PI);

			Double SQRT3 = 1.7320508;								// PI = 3 atan (sqrt(3)) from tan (PI/3) = sqrt(3)
			Double computedAtan2 = atan (SQRT3, 1.0) * 3;
			floatMonitor.activity ("3*atan(sqrt(3)) = ", computedAtan2, PI);		// Q1 +/+
			computedAtan2 = atan (-SQRT3, 1.0) * 3;
			floatMonitor.activity ("3*atan(-sqrt(3)) = ", computedAtan2, -PI);		// Q4 -/+
			computedAtan2 = atan (-SQRT3, -1.0) * 3;
			floatMonitor.activity ("3*atan(-sqrt(3),-1) = ", computedAtan2, -2*PI);	// Q3 -/-
			computedAtan2 = atan (SQRT3, -1.0) * 3;
			floatMonitor.activity ("3*atan(sqrt(3),-1) = ", computedAtan2, 2*PI);	// Q2 +/-
			System.out.println ();

			System.out.println ("> ASIN");
			Double piComputedAsin = 6 * asin (0.5);									// PI = 6 asin (0.5) from sin (PI/6) = 0.5
			floatMonitor.activity ("6*asin(0.5) = ", piComputedAsin, PI);
			System.out.println ();
		}
	}

	
	/**
	 * execute series of tests
	 * @param args not used
	 */
	public static void main (String... args)
	{

		new ExponentiationTest ().runTestSeries ();
		new TrigTest ().runTestSeries ();

	}


}
