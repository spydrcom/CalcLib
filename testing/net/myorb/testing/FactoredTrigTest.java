
package net.myorb.testing;

import net.myorb.math.SpaceManager;
import net.myorb.math.TrigLib;
import net.myorb.math.primenumbers.Factorization;
import net.myorb.math.primenumbers.FactorizationFieldManager;
import net.myorb.math.primenumbers.FactorizationImplementation;
import net.myorb.math.ComputationConfiguration;

/**
 * 
 * run unit tests on trig library based on prime factorizations
 * 
 * @author Michael Druckman
 *
 */
public class FactoredTrigTest extends TrigLib<Factorization>
{


	/**
	 * monitor object for display of test results
	 */
	static Monitor.Factored factoredMonitor = new Monitor.Factored ();


	/**
	 * construct test object
	 * @param manager type manager for Factorization
	 */
	public FactoredTrigTest
	(SpaceManager<Factorization> manager)
	{ super (manager); }


	/**
	 * execute series of tests
	 */
	public void runTests ()
	{
		Double PI = 3.14159265358979323;
		// 355/113 is approximation of pi to 6 decimal places
		Factorization pi = manager.newScalar (355).divideBy (manager.newScalar (113)); // 355/113
		factoredMonitor.activity ("PI = ", pi, PI);

		// use asin(.5) to compute value of pi
		Factorization half = manager.newScalar (1).divideBy(manager.newScalar (2)); // 0.5
		factoredMonitor.activity ("6*asin(0.5) = ", asin (half).multiplyBy (manager.newScalar (6)), PI);

		//lib.markOptionSelected (ComputationConfiguration.DUMP_ITERATIVE_TERM_VALUES);
		//support.markOptionSelected (ComputationConfiguration.DUMP_PRIME_FACTORIZATION);

		Factorization cosPi3, sinPi3, tanPi3;
		factoredMonitor.activity ("cos(0) = ", cos (manager.newScalar(0)), 1.0);
		factoredMonitor.activity ("cos(0/1) = ", cos (piTimes (0, 1)), 1.0);
		factoredMonitor.activityErrorSquared ("cos(pi/6) = ", cos (piTimes (1, 6)), 0.75);
		factoredMonitor.activity ("cos(pi/3) = ", cosPi3 = cos (piTimes (1, 3)), 0.5);
		factoredMonitor.activityErrorSquared ("cos(pi/4) = ", cos (piTimes (1, 4)), 0.5);
		factoredMonitor.activity ("cos(pi/2) = ", cos (piTimes (1, 2)), 0.0);
		factoredMonitor.activityErrorSquared ("cos(7*pi/4) = ", cos (piTimes (7, 4)), 0.5);
		factoredMonitor.activityErrorSquared ("cos(-pi/4) = ", cos (piTimes (-1, 4)), 0.5);
		factoredMonitor.activity ("cos(pi) = ", cos (pi), -1.0);

		support.markOptionSelected (ComputationConfiguration.DUMP_PRIME_FACTORIZATION);
		this.markOptionSelected (ComputationConfiguration.DUMP_ITERATIVE_TERM_VALUES);

		factoredMonitor.activity ("sin(0) = ", sin (manager.newScalar(0)), 0.0);
		factoredMonitor.activity ("sin(0/1) = ", sin (piTimes (0, 1)), 0.0);
		factoredMonitor.activity ("sin(pi/6) = ", sin (piTimes (1, 6)), 0.5);
		factoredMonitor.activityErrorSquared ("sin(pi/4) = ", sin (piTimes (1, 4)), 0.5);
		factoredMonitor.activityErrorSquared ("sin(pi/3) = ", sinPi3 = sin (piTimes (1, 3)), 0.75);
		factoredMonitor.activity ("sin(pi/2) = ", sin (piTimes (1, 2)), 1.0);
		factoredMonitor.activityErrorSquared ("sin(5*pi/4) = ", sin (piTimes (5, 4)), 0.5);
		factoredMonitor.activityErrorSquared ("sin(-pi/4) = ", sin (piTimes (-1, 4)), 0.5);
		factoredMonitor.activity ("sin(pi) = ", sin (pi), 0.0);

		tanPi3 = sinPi3.divideBy (cosPi3);
		factoredMonitor.activityErrorSquared ("tan(pi/3) = ", tanPi3, 3.0);
	}


	/**
	 * execute tests on factorized library
	 * @param args not used
	 */
	public static void main (String... args)
	{
		Factorization.setImplementation
			(support = new FactorizationImplementation (10 * 1000));
		FactorizationFieldManager factMgr = new FactorizationFieldManager ();
		support.initFactorizationsWithStats ();
		System.out.println ();

		new FactoredTrigTest (factMgr).runTests ();

	}
	public static FactorizationImplementation support;


}
