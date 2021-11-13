
package net.myorb.junit;

import net.myorb.math.*;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;

/**
 * testing type manager for prime number factorizations
 * @author Michael Druckman
 */
public class PrimTest extends AbstractTestBase
{


	int SQRT_TEST_RUNS = 100 * 1000;


	static final DoubleFloatingFieldManager dmgr = new DoubleFloatingFieldManager ();
	PowerPrimitives<Double> prim = new PowerPrimitives<Double>(dmgr);


	public void displayApproximationTest (double x, double s, int root)
	{
		double chk = prim.toThe (s, root), e = lib.abs (x - chk) / x;
		assertPrecision (e, "root(" + x + ", " + root + ")");
	}


	/**
	 * Code executed before the first test method
	 * @throws Exception generic error conditions
	 */
	@BeforeClass
    public static void setUpClass() throws Exception
    {
 	   	System.out.println ("=========================================");
		System.out.println ("-  Description of tests");
		System.out.println ("===");
		initClass ();
    }


	/**
     * Code executed before each test
	 * @throws Exception generic error conditions
     */
    @Before
    public void setUp() throws Exception
    {
    	initTest (); changeTolerance (6);
    }


    /**
	 * SQRT for domain 0 LT X LT 1
	 */
	@Test
    public void sqrtTestLt1 ()
    {
		System.out.println ("--");
		System.out.println ("-- tests for SQRT method, X < 1");
		System.out.println ("--");

		for (double x = 1.0/SQRT_TEST_RUNS; x <= 1; x+=1.0/SQRT_TEST_RUNS)
		{
			displayApproximationTest (x, prim.cubeRoot (x), 3);
		}
    }


    /**
	 * CUBE for domain 0 LT X LT 1
	 */
	@Test
    public void cubeTestLt1 ()
    {
		System.out.println ("--");
		System.out.println ("-- tests for CUBE method, X < 1");
		System.out.println ("--");

		for (double x = 1.0/SQRT_TEST_RUNS; x <= 1; x+=1.0/SQRT_TEST_RUNS)
		{
			displayApproximationTest (x, prim.cubeRoot (x), 3);
		}
    }


	/**
	 * SQRT for domain 1 LT X LT SQRT_TEST_RUNS
	 */
	@Test
    public void sqrtTestGt1 ()
    {
		System.out.println ("--");
		System.out.println ("-- tests for SQRT method, X > 1");
		System.out.println ("--");

		for (double x = 2; x <= SQRT_TEST_RUNS+1; x++)
		{
			displayApproximationTest (x, prim.sqrt (x), 2);
		}
    }


	/**
	 * CUBE for domain 1 LT X LT SQRT_TEST_RUNS
	 */
	@Test
    public void cubeTestGt1 ()
    {
		System.out.println ("--");
		System.out.println ("-- tests for CUBE method, X > 1");
		System.out.println ("--");

		for (double x = 2; x <= SQRT_TEST_RUNS+1; x++)
		{
			displayApproximationTest (x, prim.cubeRoot (x), 3);
		}
    }


	/**
     * Code executed after each test
	 * @throws Exception generic error conditions
     */
    @After
    public void tearDown() throws Exception
    {
    	completeTest ();
    }
 

    /**
     * Code executed after the last test method
	 * @throws Exception generic error conditions
     */
    @AfterClass
    public static void tearDownClass() throws Exception
    {
     	completeClass ();
    }


}

