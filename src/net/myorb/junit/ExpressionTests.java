
package net.myorb.junit;

import net.myorb.math.expressions.controls.FloatingEvaluationControl;

import org.junit.*;

/**
 * testing syntax parser of expression evaluation layers
 * @author Michael Druckman
 */
public class ExpressionTests extends AbstractTestBase
{

	FloatingEvaluationControl eval = new FloatingEvaluationControl (false);

	/**
	 * Code executed before the first test method
	 * @throws Exception generic error conditions
	 */
	@BeforeClass
    public static void setUpClass() throws Exception
    {
 	   	System.out.println ("=========================================");
		System.out.println ("-  tests of expression evaluation package");
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
     * arsinh test
     * @throws Exception for lookup error
     */
    @Test
    public void arsinhTest() throws Exception
    {
		System.out.println ("---");
		System.out.println ("-  arsinh test");
		System.out.println ("---");
		System.out.println ();

		eval.execute ("!! arsinh(x) = ln (x + sqrt (x^2 + 1)) ");
		eval.execute ("!!   sinh(x) = (exp(x) - exp(-x)) / 2  ");
		eval.execute ("    inverted =  arsinh ( sinh 2 ) ");

		eval.execute ("correctValue = 2");
		eval.execute ("error = correctValue - inverted");
		eval.dump ("error");

		assertPrecision (eval.lookup ("error"), "arsinh computation");
    }

    /**
     * quadratic equation approximation test
     * @throws Exception for lookup error
     */
    @Test
    public void quadraticTest() throws Exception
    {
		System.out.println ("---");
		System.out.println ("-  Quadratic equation test");
		System.out.println ("---");
		System.out.println ();

		eval.execute ("!! d(a,b,c) = b^2 - 4*a*c");
		eval.execute ("!! q1(a,b,c) = (sqrt (d (a,b,c)) - b) / (2*a)");
		eval.execute ("!! q2(a,b,c) = (-sqrt (d (a,b,c)) - b) / (2*a)");

		eval.execute ("PHI =  q1 (1, -1, -1)");
		eval.execute ("phi =  q2 (1, -1, -1)");

		eval.dump ("PHI");
		eval.dump ("phi");

		eval.execute ("correctValue = 1.61803398874989");
		eval.execute ("error = correctValue - PHI");
		eval.dump ("error");

		assertPrecision (eval.lookup("error"), "PHI computation");
    }

    /**
     * taylor exp test
     * @throws Exception for lookup error
     */
    @Test
    public void exponentiationTest () throws Exception
    {
		System.out.println ("---");
		System.out.println ("-  EXP test");
		System.out.println ("---");
		System.out.println ();

		eval.execute ("terms = 20");
		eval.execute ("!! expNth(n) = 1/n!");
		eval.execute ("!! expCoefficients(n) = [0 <= i <= n](expNth(i))");
		eval.execute ("!! poly(coefficients, x) = coefficients . [0 <= i <= LENGTH(coefficients)-1](x^i) ");
		eval.execute ("eSqrt = poly( expCoefficients(terms) , 0.5 )");
		eval.dump ("eSqrt");

		eval.execute ("correctValue = 1.648721270700128146848");
		eval.execute ("error = correctValue - eSqrt");
		eval.dump ("error");

		assertPrecision (eval.lookup("error"), "SQRT(e) computation");
    }

    /**
     * integration approximation test
     * @throws Exception for lookup error
     */
    @Test
    public void integrationApproximationTest() throws Exception
    {
    	changeTolerance (2);
		System.out.println ("---");
		System.out.println ("-  integration approximation test");
		System.out.println ("---");
		System.out.println ();

		eval.execute ("!! series(n) = [0 <= i <= n] (1 / i!) ");
		eval.execute ("!! poly(x,n) = [0 <= i <= n] (x ^ i)  ");
		eval.execute ("!! expx(x,n) = DOT ( series(n), poly(x,n) ) ");

		eval.execute ("dx = 0.01");
		// expx is defined in local expression definition
		eval.execute ("integralEminus1 = SIGMA [0 <= x <= 1 <> dx](expx(x,10)*dx)");
		eval.dump ("integralEminus1");

		eval.execute ("correctValue = 1.718281828459");
		eval.execute ("error = correctValue - integralEminus1");
		eval.dump ("error");

		assertPrecision (eval.lookup("error"), "EXPX integral computation");

		eval.execute ("dx = 0.001");
		// exp is the system built-in function version
		eval.execute ("integralEminus1 = SIGMA [0 <= x <= 1 <> dx](exp(x)*dx)");
		eval.dump ("integralEminus1");

		eval.execute ("error = correctValue - integralEminus1");
		eval.dump ("error");

		assertPrecision (eval.lookup("error"), "EXP integral computation");
    }

    /**
     * PI computation test
     * @throws Exception for lookup error
     */
    @Test
    public void piComputationTest() throws Exception
    {
    	changeTolerance (3);
		System.out.println ("---");
		System.out.println ("-  PI computation test");
		System.out.println ("---");
		System.out.println ();

		eval.execute
		("dx = 0.0001"); //3.141
		// 0.000001 give 6 places of PI
		eval.execute ("!! f(x) = sqrt (1 - x^2)"); // built-in sqrt
		eval.execute ("integral0to1 =  SIGMA [0 <= x <= 1 <> dx](f(x)*dx)");
		eval.execute ("piComputed =  4 * integral0to1");
		eval.dump ("piComputed");

		eval.execute ("correctValue = 3.1415926535897");
		eval.execute ("error = correctValue - piComputed");
		eval.dump ("error");

		assertPrecision (eval.lookup("error"), "PI computation");
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

