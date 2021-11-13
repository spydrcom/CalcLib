
package net.myorb.junit;

import net.myorb.math.*;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

import org.junit.*;

/**
 * JUnit test showing number of decimal places of precision demonstrated by math library
 * @author Michael Druckman
 */
public class PrecisionTests extends AbstractTestBase
{


	static MathLib mlib = new MathLib ();


	public void displayApproximationTest (double x, double s)
	{
		double chk = s * s, e = lib.abs (x - chk) / x;
		assertPrecision (e, "sqrt(" + x + ")");
	}


	double PI = 3.14159265358979323;
	int SQRT_TEST_RUNS = 100*1000;


	@BeforeClass
    public static void setUpClass() throws Exception
    {
	   	System.out.println ("=========================================");
		System.out.println ("-  Library precision tests");
		System.out.println ("===");
		System.out.println ();
		initClass ();
    }


	@Before
    public void setUp() throws Exception
    {
		initTest (); changeTolerance (13);
    }
 

    /**
     * ATAN2 tested over entire circle in 1 degree increments
     */
    @Test
    public void atanTest()
    {
		System.out.println ("--");
		System.out.println ("-- tests for ATAN method");
		System.out.println ("--");

		double RUNS = 360, inc = 2*PI/RUNS;
		for (double x = -PI+inc; x < PI; x += inc)
		{
			double e = mlib.runAtanTest (x);
			assertPrecision (e, "atan(" + x + ")");
		}
    }


    /**
     * ASIN tested over circle quadrant in 1 degree increments
     */
    @Test
    public void asinTest()
    {
		System.out.println ("--");
		System.out.println ("-- tests for ASIN method");
		System.out.println ("--");

		double RUNS = 90, inc = PI/(2*RUNS);
		for (double x = PI/2; x > -PI/2; x -= inc)
		{
			double e = mlib.runAsinTest (x);
			assertPrecision (e, "asin(" + x + ")");
		}
    }


    /**
     * EXP tested above and below 1
     */
    @Test
    public void expTest()
    {
    	changeTolerance (14);
    	System.out.println ("--");
		System.out.println ("-- tests for LN/EXP methods");
		System.out.println ("--");

		double RUNS = 6000;
		for (double x = 1; x <= RUNS; x++)
		{
			double e = mlib.runExpTest (x);
			assertPrecision (e, "exp(" + x + ")");
		}

		double inc = 1.0 / RUNS;
		for (double x = 1-inc; x > 0; x -= inc)
		{
			double e = mlib.runExpTest (x);
			assertPrecision (e, "exp(" + x + ")");
		}
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
		{ displayApproximationTest (x, mlib.sqrt (x)); }
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
		{ displayApproximationTest (x, mlib.sqrt (x)); }
    }


	@Test
    public void atanPI24Test ()
	{
		double atanPI24 = mlib.runAtanPI24Test (), e = atanPI24 - PI/24;
		System.out.println ("atan="+atanPI24+"   PI/24="+(PI/24)+"   error="+e);
		assertPrecision (e, "atan(PI/24)");
	}


	@After
    public void tearDown() throws Exception
    {
    	completeTest ();
    }
 

	@AfterClass
    public static void tearDownClass() throws Exception
    {
		completeClass ();
    }


}


class MathLib extends OptimizedMathLibrary<Double>
{

	static DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();
	public MathLib () { super (mgr); lib.setTolerance (0.0000000000001); }

	/* (non-Javadoc)
	 * @see net.myorb.math.TrigLib#atanHook(java.lang.Object)
	 */
	public Double atanHook (Double x) { return lib.atan (x); }
	protected HighSpeedMathLibrary lib = new HighSpeedMathLibrary ();

	double runAtanTest (double x)
	{
		double s = sin (x), c = cos (x), arc = atan (s, c), e = abs (arc - x);
		System.out.println ("x=" + x + "   s=" + s + "   c=" + c + "   a=" + arc + "   e=" + e);
		return e;
	}

	double runAsinTest (double x)
	{
		double s = sin (x), arc = lib.asin (s), e = abs (arc - x);
		System.out.println ("x=" + x + "   s=" + s + "   a=" + arc + "   e=" + e);
		return e;
	}

	double runExpTest (double x) // testing high speed lib
	{
		double log = lib.ln (x), exp = lib.exp (log), e = abs (exp - x) / x;
		//double log = lib.logBinary (x), exp = lib.exp2 (log), e = abs (exp - x) / x;
		//double log = lib.logCommon (x), exp = lib.exp10 (log), e = abs (exp - x) / x;
		//System.out.println ("x=" + x + "   log=" + log + "   exp=" + exp + "   e=" + e);
		return e;
	}

	double runExpTest2 (double x) // testing optimized lib
	{
		double log = ln (x), exp = exp (log), e = abs (exp - x) / x;
		//System.out.println ("x=" + x + "   log=" + log + "   exp=" + exp + "   e=" + e);
		return e;
	}

	public double sqrt (double x) { return lib.sqrt (x); }

	double runAtanPI24Test () // testing optimized lib
	{
		double rad2=sqrt(2), rad3=sqrt(3), rad6=rad2*rad3;
		System.out.println ("rad2="+rad2+"   rad3="+rad3+"   rad6="+rad6);
		double atanNumerator = rad2+rad3-3, atanDenominator = 2*rad2-rad6+rad3-1;
		System.out.println ("atanNumerator="+atanNumerator+"   atanDenominator="+atanDenominator);
		double atanPI24 = atan (atanNumerator, atanDenominator);
		return atanPI24;
	}

}

