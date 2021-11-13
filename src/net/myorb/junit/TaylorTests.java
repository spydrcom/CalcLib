
package net.myorb.junit;

import net.myorb.math.Polynomial.PowerFunction;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.*;

import org.junit.*;

/**
 * testing taylor expansion algorithms
 * @author Michael Druckman
 */
public class TaylorTests extends AbstractTestBase
{


	static PolynomialSpaceManager<Double> psm = new PolynomialSpaceManager<Double> (mgr);
	static OptimizedMathLibrary<Double> lib = new OptimizedMathLibrary<Double> (mgr);
	static TaylorPolynomials<Double> taylor = new TaylorPolynomials<Double> (mgr);


	double PI = 3.14159265358979323;


	@BeforeClass
    public static void setUpClass() throws Exception
    {
		lib.setToleranceScale (6);
	   	System.out.println ("=========================================");
		System.out.println ("-  Taylor series tests");
		System.out.println ("===");
		initClass ();
    }


	@Before
    public void setUp() throws Exception
    {
		initTest (); changeTolerance (6);
    }


    @Test
    public void testSqrt()
    {
		System.out.println ("---");
		System.out.println ("-  SQRT");
		System.out.println ("---");
		System.out.println ();

		changeTolerance (3);
		PowerFunction<Double> f = taylor.getSqrtSeries (80);
		System.out.println (psm.toString (f));

		testSqrt ("sqrt(2)=", 2);
		testSqrt ("sqrt(3)=", 3);
		testSqrt ("sqrt(4)=", 4);
		testSqrt ("sqrt(5)=", 5);
		testSqrt ("sqrt(9)=", 9);
		testSqrt ("sqrt(16)=", 16);
		testSqrt ("sqrt(25)=", 25);
    }
    public void testSqrt (String msg, double x)
    {
    	double s = lib.sqrt (x);
		System.out.print (msg + s + ", ");
		assertEquality (s*s, x, msg + s);
    }

    @Test
    public void testExp()
    {
		System.out.println ("---");
		System.out.println ("-  EXP");
		System.out.println ("---");
		System.out.println ();

		PowerFunction<Double> exp = taylor.getExpSeries (20);
		System.out.println ("exp(x) = " + psm.toString (exp));
		System.out.print ("exp(1) = " + exp.eval (1.0) + ", ");
		assertEquality (exp.eval(1.0), 2.718281828, "exp(1)=e");
    }

    @Test
    public void testArtanh()
    {
		System.out.println ("---");
		System.out.println ("-  ARTANH");
		System.out.println ("---");
		System.out.println ();

		PowerFunction<Double> artanh = taylor.getArtanhSeries (20);
		System.out.println ("artanh(x) = " + psm.toString (artanh));
		System.out.print ("artanh(1) = " + artanh.eval (1.0) + ", ");
		assertEquality (artanh.eval(1.0), 2.1332555, "artanh(1)=e");
    }

    @Test
    public void testLog()
    {
		System.out.println ("---");
		System.out.println ("-  LOG");
		System.out.println ("---");
		System.out.println ();

		PowerFunction<Double> ln = taylor.getLnSeries (20);
		System.out.println ("ln(x) = " + psm.toString (ln));
		PowerFunction<Double> exp = taylor.getExpSeries (20);
		System.out.print ("exp (ln(5)) = " + exp.eval (taylor.ln (5.0)) + ", ");
		assertEquality (exp.eval (taylor.ln (5.0)), 5.0, "exp(ln(5))");
		System.out.print ("exp (ln(0.5)) = " + exp.eval (taylor.ln (0.5)) + ", ");
		assertEquality (exp.eval (taylor.ln (0.5)), 0.5, "exp(ln(.5))");
    }

    @Test
    public void testCos()
    {
		System.out.println ("---");
		System.out.println ("-  COS");
		System.out.println ("---");
		System.out.println ();

		PowerFunction<Double> cos = taylor.getCosSeries (10);
		System.out.println ("cos(x) = " + psm.toString (cos));
		System.out.print ("cos(PI/4) = " + cos.eval (PI/4.0) + ", ");
		assertEquality (cos.eval (PI/4.0), 0.707106, "cos(pi/4)");
    }

    @Test
    public void testSin()
    {
		System.out.println ("---");
		System.out.println ("-  SIN");
		System.out.println ("---");
		System.out.println ();

		PowerFunction<Double> sin = taylor.getSinSeries (10);
		System.out.println ("sin(x) = " + psm.toString (sin));
		System.out.print ("sin(PI/4) = " + sin.eval (PI/4.0) + ", ");
		assertEquality (sin.eval (PI/4.0), 0.707106, "sin(pi/4)");
    }

    @Test
    public void testAsin()
    {
		System.out.println ("---");
		System.out.println ("-  ASIN");
		System.out.println ("---");
		System.out.println ();

		PowerFunction<Double>
		asin = taylor.getAsinSeries (50);
		System.out.println ("asin (x) = " + psm.toString (asin));
		System.out.print ("6*asin(0.5) = " + asin.eval (0.5)*6 + ", ");
		Assert.assertEquals ("asin(.5)", asin.eval (0.5)*6, PI, 6);
		assertEquality (asin.eval (0.5)*6, PI, "asin(0.5)*6=pi");
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

