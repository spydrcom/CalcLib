
package net.myorb.junit;

import net.myorb.math.expressions.controls.FloatingEvaluationControl;
import net.myorb.math.expressions.evaluationstates.*;
import net.myorb.math.polynomial.PolynomialFamilyManager;

import org.junit.*;

/**
 * testing polynomial coefficient computations
 * @author Michael Druckman
 */
public class PolynomialTests extends AbstractTestBase
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
		System.out.println ("-  Polynomial Family Computations");
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
    	initTest (); changeTolerance (12);
    }
 
    /**
     * Bernoulli family
     * @throws Exception for lookup error
     */
    @Test
    public void testBernoulli() throws Exception
    {
		System.out.println ("---");
		System.out.println ("-  Bernoulli family");
		System.out.println ("---");
		System.out.println ();

		PolynomialFamilyManager.importFamilyDescription
		("Bernoulli", PolynomialFamilyManager.DEFAULT_POLYNOMIAL_PACKAGE, environment);
		eval.execute ("FAMILY Bernoulli 5");

		eval.execute ("correct1 = (-1/2, 1)");
		eval.execute ("correct2 = (1/6, -1, 1)");
		eval.execute ("correct3 = (0, 1/2, -3/2, 1)");
		eval.execute ("correct4 = (-1/30, 0, 1, -2, 1)");

		eval.execute ("error = SIGMA (abs (b1 - correct1))");
		assertPrecision (eval.lookup ("error"), "Bernoulli recurrence (b1)");

		eval.execute ("error = SIGMA (abs (b2 - correct2))");
		assertPrecision (eval.lookup ("error"), "Bernoulli recurrence (b2)");

		eval.execute ("error = SIGMA (abs (b3 - correct3))");
		assertPrecision (eval.lookup ("error"), "Bernoulli recurrence (b3)");

		eval.execute ("error = SIGMA (abs (b4 - correct4))");
		assertPrecision (eval.lookup ("error"), "Bernoulli recurrence (b4)");
	}

    /**
     * Hermite family
     * @throws Exception for lookup error
     */
    @Test
    public void testHermite() throws Exception
    {
		System.out.println ("---");
		System.out.println ("-  Hermite family");
		System.out.println ("---");
		System.out.println ();

		PolynomialFamilyManager.importFamilyDescription
		("Hermite", PolynomialFamilyManager.DEFAULT_POLYNOMIAL_PACKAGE, environment);
		eval.execute ("FAMILY Hermite 5");

		eval.execute ("correct1 = (0, 2)");
		eval.execute ("correct2 = (-2, 0, 4)");
		eval.execute ("correct3 = (0, -12, 0, 8)");
		eval.execute ("correct4 = (12, 0, -48, 0, 16)");
		eval.execute ("correct5 = (0, 120, 0, -160, 0, 32)");

		eval.execute ("error = SIGMA (abs (h1 - correct1))");
		assertPrecision (eval.lookup ("error"), "Hermite recurrence (h1)");

		eval.execute ("error = SIGMA (abs (h2 - correct2))");
		assertPrecision (eval.lookup ("error"), "Hermite recurrence (h2)");

		eval.execute ("error = SIGMA (abs (h3 - correct3))");
		assertPrecision (eval.lookup ("error"), "Hermite recurrence (h3)");

		eval.execute ("error = SIGMA (abs (h4 - correct4))");
		assertPrecision (eval.lookup ("error"), "Hermite recurrence (h4)");

		eval.execute ("error = SIGMA (abs (h5 - correct5))");
		assertPrecision (eval.lookup ("error"), "Hermite recurrence (h5)");

    }

    /**
     * Laguerre family
     * @throws Exception for lookup error
     */
    @Test
    public void testLaguerre() throws Exception
    {
		System.out.println ("---");
		System.out.println ("-  Laguerre family");
		System.out.println ("---");
		System.out.println ();

		PolynomialFamilyManager.importFamilyDescription
		("Laguerre", PolynomialFamilyManager.DEFAULT_POLYNOMIAL_PACKAGE, environment);
		eval.execute ("FAMILY Laguerre 5");

		eval.execute ("correct1 = 1/1! * (1, -1)");
		eval.execute ("correct2 = 1/2! * (2, -4, 1)");
		eval.execute ("correct3 = 1/3! * (6, -18, 9, -1)");
		eval.execute ("correct4 = 1/4! * (24, -96, 72, -16, 1)");

		eval.execute ("error = SIGMA (abs (l1 - correct1))");
		assertPrecision (eval.lookup ("error"), "Laguerre recurrence (l1)");

		eval.execute ("error = SIGMA (abs (l2 - correct2))");
		assertPrecision (eval.lookup ("error"), "Laguerre recurrence (l2)");

		eval.execute ("error = SIGMA (abs (l3 - correct3))");
		assertPrecision (eval.lookup ("error"), "Laguerre recurrence (l3)");

		eval.execute ("error = SIGMA (abs (l4 - correct4))");
		assertPrecision (eval.lookup ("error"), "Laguerre recurrence (l4)");

    }

    /**
     * Chebyshev family
     * @throws Exception for lookup error
     */
    @Test
    public void testChebyshevT()  throws Exception
    {
		System.out.println ("---");
		System.out.println ("-  Chebyshev T family");
		System.out.println ("---");
		System.out.println ();

		PolynomialFamilyManager.importFamilyDescription
		("Chebyshev", "net.myorb.math.polynomial.families", environment);
		eval.execute ("FAMILY Chebyshev 6 first");

		eval.execute ("correct1 = (0, 1)");
		eval.execute ("correct2 = (-1, 0, 2)");
		eval.execute ("correct3 = (0, -3, 0, 4)");
		eval.execute ("correct4 = (1, 0, -8, 0, 8)");
		eval.execute ("correct5 = (0, 5, 0, -20, 0, 16)");

		eval.execute ("error = SIGMA (abs (t1 - correct1))");
		assertPrecision (eval.lookup ("error"), "Chebyshev recurrence (t1)");

		eval.execute ("error = SIGMA (abs (t2 - correct2))");
		assertPrecision (eval.lookup ("error"), "Chebyshev recurrence (t2)");

		eval.execute ("error = SIGMA (abs (t3 - correct3))");
		assertPrecision (eval.lookup ("error"), "Chebyshev recurrence (t3)");

		eval.execute ("error = SIGMA (abs (t4 - correct4))");
		assertPrecision (eval.lookup ("error"), "Chebyshev recurrence (t4)");

		eval.execute ("error = SIGMA (abs (t5 - correct5))");
		assertPrecision (eval.lookup ("error"), "Chebyshev recurrence (t5)");

    }

    /**
     * Chebyshev family
     * @throws Exception for lookup error
     */
    @Test
    public void testChebyshevU() throws Exception
    {
		System.out.println ("---");
		System.out.println ("-  Chebyshev U family");
		System.out.println ("---");
		System.out.println ();

		PolynomialFamilyManager.importFamilyDescription
		("Chebyshev", "net.myorb.math.polynomial.families", environment);
		eval.execute ("FAMILY Chebyshev 6 second");

		eval.execute ("correct1 = (0, 2)");
		eval.execute ("correct2 = (-1, 0, 4)");
		eval.execute ("correct3 = (0, -4, 0, 8)");
		eval.execute ("correct4 = (1, 0, -12, 0, 16)");
		eval.execute ("correct5 = (0, 6, 0, -32, 0, 32)");

		eval.execute ("error = SIGMA (abs (u1 - correct1))");
		assertPrecision (eval.lookup ("error"), "Chebyshev recurrence (u1)");

		eval.execute ("error = SIGMA (abs (u2 - correct2))");
		assertPrecision (eval.lookup ("error"), "Chebyshev recurrence (u2)");

		eval.execute ("error = SIGMA (abs (u3 - correct3))");
		assertPrecision (eval.lookup ("error"), "Chebyshev recurrence (u3)");

		eval.execute ("error = SIGMA (abs (u4 - correct4))");
		assertPrecision (eval.lookup ("error"), "Chebyshev recurrence (u4)");

		eval.execute ("error = SIGMA (abs (u5 - correct5))");
		assertPrecision (eval.lookup ("error"), "Chebyshev recurrence (u5)");

    }

    /**
     * Legendre family
     * @throws Exception for lookup error
     */
    @Test
    public void testLegendre() throws Exception
    {
		System.out.println ("---");
		System.out.println ("-  Legendre family");
		System.out.println ("---");
		System.out.println ();

		PolynomialFamilyManager.importFamilyDescription
		("Legendre", PolynomialFamilyManager.DEFAULT_POLYNOMIAL_PACKAGE, environment);
		eval.execute ("FAMILY Legendre 6 first");

		eval.execute ("correct1 = (0, 1)");
		eval.execute ("correct2 = 1/2 * (-1, 0, 3)");
		eval.execute ("correct3 = 1/2 * (0, -3, 0, 5)");
		eval.execute ("correct4 = 1/8 * (3, 0, -30, 0, 35)");
		eval.execute ("correct5 = 1/8 * (0, 15, 0, -70, 0, 63)");

		eval.execute ("error = SIGMA (abs (p1 - correct1))");
		assertPrecision (eval.lookup ("error"), "Legendre recurrence (p1)");

		eval.execute ("error = SIGMA (abs (p2 - correct2))");
		assertPrecision (eval.lookup ("error"), "Legendre recurrence (p2)");

		eval.execute ("error = SIGMA (abs (p3 - correct3))");
		assertPrecision (eval.lookup ("error"), "Legendre recurrence (p3)");

		eval.execute ("error = SIGMA (abs (p4 - correct4))");
		assertPrecision (eval.lookup ("error"), "Legendre recurrence (p4)");

		eval.execute ("error = SIGMA (abs (p5 - correct5))");
		assertPrecision (eval.lookup ("error"), "Legendre recurrence (p5)");

    }

    /**
     * Gegenbauer family
     * @throws Exception for lookup error
     */
    @Test
    public void testGegenbauer() throws Exception
    {
		System.out.println ("---");
		System.out.println ("-  Gegenbauer family");
		System.out.println ("---");
		System.out.println ();

		PolynomialFamilyManager.importFamilyDescription
		("Legendre", PolynomialFamilyManager.DEFAULT_POLYNOMIAL_PACKAGE, environment);
		eval.execute ("FAMILY Legendre 6 C");

		eval.execute ("correct1 = (0, 1)");
		eval.execute ("correct2 = 1/2 * (-1, 0, 3)");
		eval.execute ("correct3 = 1/2 * (0, -3, 0, 5)");
		eval.execute ("correct4 = 1/8 * (3, 0, -30, 0, 35)");
		eval.execute ("correct5 = 1/8 * (0, 15, 0, -70, 0, 63)");

		eval.execute ("error = SIGMA (abs (c1 - correct1))");
		assertPrecision (eval.lookup ("error"), "Gegenbauer (lambda=1/2) recurrence (c1)");

		eval.execute ("error = SIGMA (abs (c2 - correct2))");
		assertPrecision (eval.lookup ("error"), "Gegenbauer (lambda=1/2) recurrence (c2)");

		eval.execute ("error = SIGMA (abs (c3 - correct3))");
		assertPrecision (eval.lookup ("error"), "Gegenbauer (lambda=1/2) recurrence (c3)");

		eval.execute ("error = SIGMA (abs (c4 - correct4))");
		assertPrecision (eval.lookup ("error"), "Gegenbauer (lambda=1/2) recurrence (c4)");

		eval.execute ("error = SIGMA (abs (c5 - correct5))");
		assertPrecision (eval.lookup ("error"), "Gegenbauer (lambda=1/2) recurrence (c5)");

    }

    /**
     * Jacobi family
     * @throws Exception for lookup error
     */
    @Test
    public void testJacobi() throws Exception
    {
		System.out.println ("---");
		System.out.println ("-  Jacobi family");
		System.out.println ("---");
		System.out.println ();

		PolynomialFamilyManager.importFamilyDescription
		("Legendre", PolynomialFamilyManager.DEFAULT_POLYNOMIAL_PACKAGE, environment);
		eval.execute ("FAMILY Legendre 6 J");

		eval.execute ("correct1 = (0, 1)");
		eval.execute ("correct2 = 1/2 * (-1, 0, 3)");
		eval.execute ("correct3 = 1/2 * (0, -3, 0, 5)");
		eval.execute ("correct4 = 1/8 * (3, 0, -30, 0, 35)");
		eval.execute ("correct5 = 1/8 * (0, 15, 0, -70, 0, 63)");

		eval.execute ("error = SIGMA (abs (j1 - correct1))");
		assertPrecision (eval.lookup ("error"), "Jacobi (a=0,b=0) recurrence (j1)");

		eval.execute ("error = SIGMA (abs (j2 - correct2))");
		assertPrecision (eval.lookup ("error"), "Jacobi (a=0,b=0) recurrence (j2)");

		eval.execute ("error = SIGMA (abs (j3 - correct3))");
		assertPrecision (eval.lookup ("error"), "Jacobi (a=0,b=0) recurrence (j3)");

		eval.execute ("error = SIGMA (abs (j4 - correct4))");
		assertPrecision (eval.lookup ("error"), "Jacobi (a=0,b=0) recurrence (j4)");

		eval.execute ("error = SIGMA (abs (j5 - correct5))");
		assertPrecision (eval.lookup ("error"), "Jacobi (a=0,b=0) recurrence (j5)");

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

