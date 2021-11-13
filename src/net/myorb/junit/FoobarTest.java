
package net.myorb.junit;

import org.junit.*;

/**
 * boiler plate JUnit test for Math Library tests
 * @author Michael Druckman
 */
public class FoobarTest extends AbstractTestBase
{

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
     * first of sequence of tests
     */
    @Test
    public void testOneThing()
    {
        // Code that tests one thing
		System.out.println ("---");
		System.out.println ("-  NAME test");
		System.out.println ("---");
		System.out.println ();
    	//Assert.fail ("Boilerplate test, not implemented");
    }

    /**
     * next of sequence of tests
     */
    @Test
    public void testAnotherThing()
    {
        // Code that tests another thing
		System.out.println ("---");
		System.out.println ("-  NAME test");
		System.out.println ("---");
		System.out.println ();
    	//Assert.fail ("Boilerplate test, not implemented");
    }

    /**
     * last of sequence of tests
     */
    @Test
    public void testSomethingElse()
    {
        // Code that tests something else
		System.out.println ("---");
		System.out.println ("-  NAME test");
		System.out.println ("---");
		System.out.println ();
    	//Assert.fail ("Boilerplate test, not implemented");
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

