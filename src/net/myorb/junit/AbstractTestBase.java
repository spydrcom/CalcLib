
package net.myorb.junit;

import net.myorb.math.*;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

import org.junit.Assert;

import java.util.Date;

/**
 * common methods used as boiler plate for Math Library tests
 * @author Michael Druckman
 */
public class AbstractTestBase
{


	protected static DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();
	protected static OptimizedMathLibrary<Double> lib = new OptimizedMathLibrary<Double> (mgr);
	protected static HighSpeedMathLibrary mlib = new HighSpeedMathLibrary ();


	/**
	 * use tolerance checking mechanism to assert equality
	 * @param value the value being validated by assertion check
	 * @param comparedTo the expected value for use in validation
	 * @param message the text to be asserted if validation fails
	 */
	public void assertEquality (double value, double comparedTo, String message)
	{
		doAssertion (mlib.abs (value - comparedTo), message);
		System.out.println (value + " = " + comparedTo + " within specified tolerance, VERIFIED !!!");
	}
	public void assertEquality (double[] value, double[] comparedTo, String message)
	{
		for (int i = 0; i < value.length; i++)
		{
			doAssertion (mlib.abs (value[i] - comparedTo[i]), message);
		}
	}
	public void assertPrecision (double value, String message)
	{
		double error = mlib.abs (value);
		doAssertion (error, message);
	}
	public void doAssertion (double error, String message)
	{
		Assert.assertTrue (message, lib.withinTolerance (error));
		if (error > hiError) hiError = error;
		sumError += error;
		assertions++;
	}


	/**
	 * track changes to tolerance as tests run
	 * @param to the new value being set for tolerance
	 */
	public static void changeTolerance (int to)
	{
		double t = mlib.pow (10.0, -to);
		if (lib.getTolerance() == t) return;
		System.out.println ("................... Tolerance change from " +
			lib.getTolerance() + " to " + t + " ...................");
		lib.setToleranceScale (to);
		System.out.println ();
	}


	protected int assertions; double hiError, sumError;
    protected static int totalAssertions;


    /**
     * common initialization of JUnit class
     */
    public static void initClass ()
    {
		System.out.println ();
		totalAssertions = 0;
    }


    /**
     * common initialization of JUnit test
     */
    public void initTest ()
    {
    	start = new Date ();
		System.out.println ();
    	assertions = 0;
    	sumError = 0;
    	hiError = 0;
    }
    protected Date start;


    /**
     * common termination of JUnit test
     */
    public void completeTest ()
    {
		Date end = new Date ();

		if (assertions == 0) System.out.println (">>> No Assertions Made <<<");
		else System.out.println ("high error = " + hiError + "   avg error = " + sumError/assertions);
		System.out.println ("---");
		System.out.println ();

    	totalAssertions += assertions;
		System.out.println ("[ " + assertions + " assertions validated ]");
		
		long millis = end.getTime () - start.getTime ();
    	System.out.println ("[ time of test = " + millis + "ms ]");
    	System.out.println ();
    }


    /**
     * common termination of JUnit class
     */
    public static void completeClass ()
    {
		System.out.println ();
		System.out.println ("===");
		System.out.println ();

		System.out.println ("[ " + totalAssertions + " assertions validated total across all tests ]");
		System.out.println ();

    	System.out.println ("===");
		System.out.println ("-  END tests");
		System.out.println ("===");
		System.out.println ();
    }


}


