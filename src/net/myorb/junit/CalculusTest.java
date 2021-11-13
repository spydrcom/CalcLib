
package net.myorb.junit;

import net.myorb.math.computational.Calculus;
import net.myorb.math.polynomial.PolynomialSpaceManager;

import net.myorb.math.TaylorPolynomials;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import org.junit.*;

/**
 * testing calculus library methods
 * @author Michael Druckman
 */
public class CalculusTest extends Calculus<Double> implements Function<Double>
{


	static AbstractTestBase testing = new AbstractTestBase ();
	static PolynomialSpaceManager<Double> psm = new PolynomialSpaceManager<Double> (AbstractTestBase.mgr);
	static TaylorPolynomials<Double> taylor = new TaylorPolynomials<Double> (AbstractTestBase.mgr);
	static Double delta = 1d / 30000d;


	/* (non-Javadoc)
	 * @see net.myorb.math.Function#f(java.lang.Object)
	 */
	public Double eval (Double x)
	{
		// f(x) = sqrt (1 - x^2)
		return sroot (manager.add (manager.getOne (), manager.negate (manager.multiply (x, x))));
	}
	public SpaceManager<Double> getSpaceManager () { return manager;}
	public SpaceManager<Double> getSpaceDescription () { return manager;}
	public CalculusTest () { super (AbstractTestBase.mgr, delta); setLibrary (AbstractTestBase.mlib); }


	@BeforeClass
    public static void setUpClass() throws Exception
    {
		AbstractTestBase.initClass ();
		AbstractTestBase.changeTolerance (4);
        // Code executed before the first test method    
	   	System.out.println ("=========================================");
		System.out.println ("-  Calculus library tests");
		System.out.println ("===");
		System.out.println ();
    }

    @Before
    public void setUp() throws Exception
    {
        // Code executed before each test
    	testing.initTest ();
    }
 
    @Test
    public void testAreaUnderCurve ()
    {
    	System.out.println ("+++");
		System.out.println ("-  Area under curve");
		System.out.println ("+++");
		System.out.println ();

    	Double result =
    		integral (this, discrete (0), discrete (1));					// area from 0 - 1
		System.out.println ("integral * 4 = " + (4 * result));
		testing.assertEquality (4 * result, 3.141592653, "integral = pi/4");
    }

    @Test
    public void testPathOfCurve ()
    {
    	System.out.println ("+++");
		System.out.println ("-  Distance along path");
		System.out.println ("+++");
		System.out.println ();

 		Double result =
 			lineIntegral (this, discrete (0), discrete (1));				// perimeter from 0 - 1
		System.out.println ("line integral * 2 = " + (2 * result));
		testing.assertEquality (2 * result, 3.141592653, "line integral = pi/2");
    }

    @After
    public void tearDown() throws Exception
    {
        // Code executed after each test
    	testing.completeTest ();
    }
 
    @AfterClass
    public static void tearDownClass() throws Exception
    {
        // Code executed after the last test method
    	AbstractTestBase.completeClass ();
    }

}
