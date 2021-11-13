
package net.myorb.junit;

import net.myorb.math.Polynomial.PowerFunction;
import net.myorb.math.computational.PolynomialFunctionCharacteristics.CharacteristicAttributes;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.computational.*;
import net.myorb.math.*;

import java.util.List;
import org.junit.*;

/**
 * JUnit tests for polynomial root computation algorithms
 */
public class RootsTest extends AbstractTestBase
{


	static PolynomialSpaceManager<Double> psm = new PolynomialSpaceManager<Double> (mgr);
	static PolynomialRoots<Double> roots = new PolynomialRoots<Double> (mgr, lib);
	static Polynomial<Double> poly = new Polynomial<Double> (mgr);


	/**
	 * display polynomial and roots
	 * @param pf the power function wrapping the polynomial
	 * @param l the list of roots for display
	 * @param names names for roots
	 */
	public void rootsDisplay (Polynomial.PowerFunction<Double> pf, List<Double> l, String[] names)
	{
		psm.show (pf);
		System.out.println ();
		System.out.println ("---");
		rootsDump (names, pf.getCoefficients (), l);
		System.out.println ("===");
		System.out.println ();
	}
	public void rootsDisplay (Polynomial.PowerFunction<Double> pf, List<Double> l)
	{ rootsDisplay (pf, l, new String[]{"Q first root", "Q second root"}); }


	@BeforeClass
    public static void setUpClass() throws Exception
    {
        // Code executed before the first test method    
		cpc = psm.newCoefficients (-72.0, 36.0, 4.0, -1.0);
	   	System.out.println ("=========================================");
		System.out.println ("-  Polynomial roots tests");
		System.out.println ("===");
		System.out.println ();
		initClass ();
    }
	static Polynomial.Coefficients<Double> cpc;


	@Before
    public void setUp() throws Exception
    {
        // Code executed before each test    
		changeTolerance (6);
		initTest ();
    }
 

	/**
	 * solve simple polynomial with quadratic equation
	 */
	@Test
	public void quadraticTest ()
	{
    	System.out.println ("---");
		System.out.println ("-  Quadratic equation tests");
		System.out.println ("---");
		System.out.println ();

		Double a = 1.0, b = -5.0, c = 6.0;
 		Polynomial.Coefficients<Double> p = psm.newCoefficients (c, b, a);
		Polynomial.PowerFunction<Double> pf = poly.getPolynomialFunction (p);
		rootsDisplay (pf, roots.quadratic (a, b, c));
	}

    /**
     * solve alternate polynomial with quadratic equation using power function wrapper
     */
    @Test
	public void quadraticTest2 ()
    {
		System.out.println ("---");
		System.out.println ("-  Quadratic equation applied to power function");
		System.out.println ("---");
		System.out.println ();

 		Polynomial.Coefficients<Double> p = psm.newCoefficients (-72.0, -90.0, 24.0);
		Polynomial.PowerFunction<Double> pf = poly.getPolynomialFunction (p);
		rootsDisplay (pf, roots.quadratic (p));
    }

    /**
     * solve golden ratio polynomial with quadratic equation
     */
    @Test
	public void goldenRatioTest ()
    {
    	System.out.println ("---");
		System.out.println ("-  Golden ratio polynomial solution");
		System.out.println ("---");
		System.out.println ();

		Double a = 1.0, b = -1.0, c = -1.0;
 		Polynomial.Coefficients<Double> p = psm.newCoefficients (c, b, a);
		Polynomial.PowerFunction<Double> pf = poly.getPolynomialFunction (p);
		rootsDisplay (pf, roots.quadratic (a, b, c), new String[]{"PHI", "phi"});
    }

    /**
     * test square root computation methods
     */
    @Test
	public void squareRootTest ()
    {
		double result;

		System.out.println ("---");
		System.out.println ("-  fast SQRT test");
		System.out.println ("---");
		System.out.println ();

		System.out.println ("fastSqrt(2) = " + (result = roots.fastSqrt (2.0)));
		assertEquality (result*result, 2, "sqrt(2)");
		System.out.println ();

		Polynomial.Coefficients<Double>
			coefficients = psm.newCoefficients (-2.0, 0.0, 1.0);
		Double root = roots.newtonRaphsonMethod (coefficients, 1.0);
		functionValidationDump ("NR sqrt(2)", coefficients, root, 0.0);
		assertEquality (root*root, 2, "NR sqrt(2)");
		System.out.println ("===");
		System.out.println ();
    }

    /**
     * test Newton method
     */
    @Test
    public void testNewtonRoots()
    {
		System.out.println ("--");
		System.out.println ("-- tests for Newton-Raphson method");
		System.out.println ("--");

		// (4x-1) * (5x-4) = 20*x^2 - 16*x - 5*x + 4
		System.out.println ("20x^2 - 21x + 4 ===");
		// roots are 0.25 and 0.80
		System.out.println ("--");

		Polynomial.Coefficients<Double>
			coefficients = psm.newCoefficients (4.0, -21.0, 20.0);
       	Double root = roots.newtonRaphsonMethod (coefficients, 0.0);
		functionValidationDump ("NR " + coefficients + " @0.0 ", coefficients, root, 0.0);
		assertEquality (root, 0.25, "NR root=0.25");

		root = roots.newtonRaphsonMethod (coefficients, 1.0);
		functionValidationDump ("NR " + coefficients + " @1.0 ", coefficients, root, 0.0);
		assertEquality (root, 0.8, "NR root=0.8");

		// derivative hits zero at local max or min, method would have division by zero error

		try
		{
			root = roots.newtonRaphsonMethod (coefficients, 0.525);
			functionValidationDump ("NR " + coefficients + " @0.525 ", coefficients, root, 0.0);
		}
		catch (Exception e)
		{
			System.out.println ("*** NR " + coefficients + " @0.525 *** " + e.getLocalizedMessage ());
			System.out.println ();
		}

		root = roots.newtonRaphsonMethod (cpc, 0.1);
		functionValidationDump ("NR " + cpc + " @0.1 ", cpc, root, 0.0);
		assertEquality (root, 1.80177, "NR root=1.80177");
		System.out.println ("===");
		System.out.println ();
    }

    /**
     * test Laguerre algorithm
     */
    @Test
    public void testLaguerreMethod()
    {
		System.out.println ("--");
		System.out.println ("-- tests for Laguerre method");
		System.out.println ("--");

		Polynomial.Coefficients<Double>
			coefficients = psm.newCoefficients (4.0, -21.0, 20.0);
		Double laguerreRoot = roots.laguerreMethod (coefficients, 0.0);
		functionValidationDump ("LG " + coefficients + " @0.0 ", coefficients, laguerreRoot, 0.0);
		assertEquality (laguerreRoot, 0.25, "NR root=0.25");

		laguerreRoot = roots.laguerreMethod (coefficients, 1.0);
		functionValidationDump ("LG " + coefficients + " @1.0 ", coefficients, laguerreRoot, 0.0);
		assertEquality (laguerreRoot, 0.8, "NR root=0.8");

		laguerreRoot = roots.laguerreMethod (cpc, 1.5);
		functionValidationDump ("LG " + cpc + " @1.5 ", cpc, laguerreRoot, 0.0);
		assertEquality (laguerreRoot, 1.80177, "NR root=1.80177");

		laguerreRoot = roots.laguerreMethod (cpc, 7.2);
		changeTolerance (2); functionValidationDump ("LG " + cpc + " @7.2 ", cpc, laguerreRoot, 0.0);
		assertEquality (laguerreRoot, 7.5153, "NR root=7.5153");
		System.out.println ("===");
		System.out.println ();
    }

    /**
     * test root bisection algorithm
     */
    @Test
    public void testBisectionRoots()
    {
		// tests for bisection method

		System.out.println ("--");
		System.out.println ("-- tests for bisection method");
		System.out.println ("--");

		Polynomial.Coefficients<Double>
			coefficients = new Polynomial.Coefficients<Double> ();
		coefficients.add (4.0); coefficients.add (-21.0); coefficients.add (20.0);
		PowerFunction<Double> p = roots.getPolynomialFunction (coefficients);

		Double bisectionRoot = new FunctionRoots<Double> (mgr, lib).bisectionMethod (p, 0.0, 0.5);
		functionValidationDump ("BS " + coefficients + " @0.0-0.5 ", coefficients, bisectionRoot, 0.0);
		assertEquality (bisectionRoot, 0.25, "NR root=0.25");

		changeTolerance (4);
		bisectionRoot = new FunctionRoots<Double> (mgr, lib).bisectionMethod (p, 0.6, 2.0);
		functionValidationDump ("BS " + coefficients + " @0.6-2.0 ", coefficients, bisectionRoot, 0.0);
		assertEquality (bisectionRoot, 0.8, "NR root=0.8");
		System.out.println ("===");
		System.out.println ();
    }

    /**
     * construct polynomials and reverse engineer the roots
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void testPolynomialRoots()
    {
    	changeTolerance (4);
		System.out.println ();
		System.out.println ("======================================================");
		System.out.println ("Manufactured Polynomials");
		System.out.println ("------------------------");
		System.out.println ();

       	List<Double> l;
		Polynomial.Coefficients<Double>
			p1 = psm.newCoefficients (2.0, -4.0, 3.0),
			p2 = psm.newCoefficients (1.0, 4.0, 2.0);
		PowerFunction<Double> pf = psm.multiply
		(
			psm.getPolynomialFunction(p1), psm.getPolynomialFunction(p2)
		);
		System.out.println (psm.toString (pf));
		System.out.println (l = roots.evaluateEquation (pf));
		System.out.println ();

		rootsDump (new String[]{"first", "second"}, pf.getCoefficients(), l);

		System.out.println ();
		System.out.println ("======================================================");
		System.out.println ();

		pf = psm.getPolynomialFunction
			(psm.newCoefficients (5.0, 1.0));
		pf = multiply (pf, psm.newCoefficients (-4.0, 1.0));
		pf = multiply (pf, psm.newCoefficients (-1.0, 5.0));
		pf = multiply (pf, psm.newCoefficients (-7.0, 1.0));
		pf = multiply (pf, psm.newCoefficients (-12.0, 1.0));
		pf = multiply (pf, psm.newCoefficients (-3.0, 2.0));

		changeTolerance (3);
		System.out.println (psm.toString (pf));
		System.out.println (l = roots.evaluateEquation (pf));
		System.out.println ();

		String[] rootNames = new String[]{"Root 1", "Root 2", "Root 3", "Root 4", "Root 5", "Root 6"};
		rootsDump (rootNames, pf.getCoefficients (), l);

		System.out.println ();
		System.out.print ("--- try actual roots "); changeTolerance (10);
		System.out.println ();

		l = roots.newList (-5.0, 0.2, 1.5, 4.0, 7.0, 12.0);
		rootsDump (rootNames, pf.getCoefficients (), l);

		System.out.println ();
		System.out.println ("======================================================");
		System.out.println ();

		List<CharacteristicAttributes> cs = PolynomialFloatingFunctionCharacteristics.characterize (pf);
		for (CharacteristicAttributes c : cs) { System.out.println (PolynomialFloatingFunctionCharacteristics.toString (c)); }

		System.out.println ();
		System.out.println ("======================================================");
		System.out.println ();

		System.out.println (PolynomialFloatingFunctionCharacteristics.toXml (cs));

		System.out.println ();
		System.out.println ("======================================================");
		System.out.println ();
    }

    /**
     * check the values of derivatives of the CPC function
     */
    @Test
    public void testCPC()
    {
       	Double root;
       	changeTolerance (5);
		System.out.println ();
		System.out.println ("cpc' zero");
		System.out.println ("---------");
		Polynomial.Coefficients<Double>
			coefficients = new Polynomial.Coefficients<Double> ();
		PowerFunction<Double> p = roots.getPolynomialFunction (cpc);
		PowerFunction<Double> d = roots.getFunctionDerivative (p);
		root = roots.newtonRaphsonMethod (coefficients = d.getCoefficients (), 0.0);
		functionValidationDump ("NR " + coefficients + " D ", coefficients, root, 0.0);
		assertEquality (root, -2.3785, "NR root=-2.3785");
		functionValidationDump (cpc.toString (), cpc, root, -121.54115);

		Double xapprox = root-0.1;
		System.out.println ("before Derivative zero");
		Double nrroot = roots.newtonRaphsonMethod (cpc, xapprox);
		functionValidationDump ("NR " + cpc + " @" + xapprox + " ", cpc, nrroot, 0.0);

		xapprox = root+0.1;
		System.out.println ("after Derivative zero");
		nrroot = roots.newtonRaphsonMethod (cpc, xapprox);
		functionValidationDump ("NR " + cpc + " @" + xapprox + " ", cpc, nrroot, 0.0);

       	List<Double> l;
		changeTolerance (4);
		System.out.println (l = roots.evaluateEquation (cpc));

		System.out.println ();
		rootsDump (new String[]{"Eval 1", "Eval 2", "Eval 3"}, cpc, l);
		System.out.println ("polynomial => " + psm.toString (p));
		System.out.println ("derivative => " + psm.toString (d));
		System.out.println ("===");
		System.out.println ();
    }
    
    @After
    public void tearDown() throws Exception
    {
        // Code executed after each test
    	completeTest ();
    }
 
    @AfterClass
    public static void tearDownClass() throws Exception
    {
        // Code executed after the last test method
    	completeClass ();
    }

	/**
	 * run validations for a list of roots
	 * @param tag the tags to use to identify each root
	 * @param coefficients the coefficients list that define the function
	 * @param x the value of X at which to evaluate the function looking for zeroes
	 */
	public void rootsDump
	(String[] tag, Polynomial.Coefficients<Double> coefficients, List<Double> x)
	{
		for (int i = 0; i < tag.length; i++) functionValidationDump (tag[i], coefficients, x.get(i), 0.0);
	}

	/**
	 * validate function value for specific input
	 * @param tag an identifier for the validation step
	 * @param coefficients the coefficients list that define the function
	 * @param x the value of X at which to evaluate the function for this validation
	 * @param shouldBe the value that the function should have at specified X
	 */
	public void functionValidationDump
	(String tag, Polynomial.Coefficients<Double> coefficients, Double x, Double shouldBe)
	{
		System.out.println (tag + " x = " + x + ", f(x) expected " + shouldBe);
		Double polyEvaluated = roots.evaluatePolynomial (coefficients, x);

		assertEquality
		(
			polyEvaluated, shouldBe,
			"Root must evaluate in function within tolerance of " +
			shouldBe + ", " + polyEvaluated + " found"
		);

		System.out.println ("+++");
		System.out.println ();
	}

    /**
     * multiply polynomials
     * @param p power function of first polynomial
     * @param c coefficients of second polynomial
     * @return product as a power function
     */
    PowerFunction<Double> multiply (PowerFunction<Double> p, Polynomial.Coefficients<Double> c)
    { return psm.multiply (p, psm.getPolynomialFunction (c)); }

}
