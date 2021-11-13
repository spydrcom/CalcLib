
package net.myorb.junit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.myorb.math.expressions.controls.FloatingEvaluationControl;
import net.myorb.math.expressions.evaluationstates.Environment;

/**
 * testing values returned by special functions
 * @author Michael Druckman
 */
public class SpecialFunctionsTests extends AbstractTestBase
{

	FloatingEvaluationControl eval = new FloatingEvaluationControl (false);
	Environment<Double> environment = new Environment<Double> (FloatingEvaluationControl.mgr);

	/**
	 * Code executed before the first test method
	 * @throws Exception generic error conditions
	 */
	@BeforeClass
    public static void setUpClass() throws Exception
    {
 	   	System.out.println ("=========================================");
		System.out.println ("-  Special Function Computations");
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
    	initTest (); changeTolerance (4);
    }
 
    /**
     * Erf function test
     * @throws Exception for lookup error
     */
    @Test
    public void testErf() throws Exception
    {
		System.out.println ("---");
		System.out.println ("-  Error Function");
		System.out.println ("---");
		System.out.println ();

		eval.execute ("LIBRARY SpecFunc net.myorb.math.specialfunctions.Library");
		eval.execute ("!+ ERF_IMPORT(x) = SpecFunc.erf");
		eval.execute ("!! ERF(x) = ERF_IMPORT x");

		eval.execute ("correctErf = ( 0, 0.2227, 0.4284, 0.6039, 0.7421, 0.8427, 0.9103, 0.9523, 0.9763, 0.9891, 0.9953 )");
		eval.execute ("calculated = [ 0 <= x <= 2.1 <> 0.2 ] (ERF (x))");

		eval.execute ("error = SIGMA (abs (calculated - correctErf))");
		assertPrecision (eval.lookup ("error"), "Erf function calculations");
    }

    /**
     * Gamma function test
     * @throws Exception for lookup error
     */
    @Test
    public void testGamma() throws Exception
    {
		System.out.println ("---");
		System.out.println ("-  Gamma Function");
		System.out.println ("---");
		System.out.println ();

		eval.execute ("LIBRARY SpecFunc net.myorb.math.specialfunctions.Library");
		eval.execute ("!+ GAMMA_IMPORT(x) = SpecFunc.gamma");
		eval.execute ("!! GAMMA(x) = GAMMA_IMPORT x");

		eval.execute ("correctGamma = (1, 0.9735, 0.9514, 0.9330, 0.9182, 0.9064, 0.8975, 0.8912, 0.8873, 0.8857, 0.8862, 0.8889, 0.8935, 0.9001, 0.9086, 0.9191, 0.9314, 0.9456, 0.9618, 0.9799, 1)");
		eval.execute ("calculated = [ 1 <= x <= 2.02 <> 0.05 ] (GAMMA (x))"); eval.execute ("prettyprint calculated");
		eval.execute ("errors = calculated - correctGamma"); eval.execute ("prettyprint errors");

		eval.execute ("error = SIGMA (abs (calculated - correctGamma))"); eval.execute ("prettyprint error");
		assertPrecision (eval.lookup ("error"), "Gamma function calculations");

		eval.execute ("!! psi(x) = GAMMA'(x <> 0.0001)/GAMMA(x)");

		eval.execute ("correctPsi = (-0.5883, -0.4247, -0.2884, -0.1686, -0.0615, 0.0364, 0.1253, 0.2092, 0.2845, 0.3558)");
		eval.execute ("calculated = [ 1 <= x <= 2 <> 0.1 ] (psi (x))");

		eval.execute ("error = SIGMA (abs (calculated - correctPsi))");
		assertPrecision (eval.lookup ("error"), "Psi function calculations");

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
