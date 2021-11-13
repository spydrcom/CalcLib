
package net.myorb.junit;

import net.myorb.math.*;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.math.characteristics.EigenvaluesAndEigenvectors;
import net.myorb.math.computational.PolynomialRoots;
import net.myorb.math.matrices.*;
import net.myorb.math.polynomial.PolynomialSpaceManager;

import java.util.List;

import org.junit.*;

/**
 * testing linear algebra library methods
 * @author Michael Druckman
 */
public class EigenTests  extends AbstractTestBase
{


	static DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();

	static OptimizedMathLibrary<Double> lib = new OptimizedMathLibrary<Double> (mgr);
	static PolynomialSpaceManager<Double> pfm = new PolynomialSpaceManager<Double> (mgr);
	static Matrix<Polynomial.PowerFunction<Double>> m3x3 = new Matrix<Polynomial.PowerFunction<Double>> (3, 3, pfm);
	static EigenvaluesAndEigenvectors<Double> eigen = new EigenvaluesAndEigenvectors<Double> (mgr, lib);
	static PolynomialRoots<Double> roots = new PolynomialRoots<Double> (mgr, lib);
	static MatrixOperations<Double> matrices = new MatrixOperations<Double> (mgr);
	static VectorOperations<Double> vops = new VectorOperations<Double> (mgr);

	static Matrix<Double> m3x3dbl = new Matrix<Double> (3, 3, mgr);

	static
	{
		matrices.setRow (1, m3x3dbl, vops.V (4d, -1d, 2d));
		matrices.setRow (2, m3x3dbl, vops.V (6d, 3d, 1d));
		matrices.setRow (3, m3x3dbl, vops.V (6d, 3d, -3d));
	}


	@BeforeClass
    public static void setUpClass() throws Exception {
        // Code executed before the first test method    
    	System.out.println ("=========================================");
		System.out.println ("-  Eigensystem tests");
		System.out.println ("===");
		System.out.println ();
		initClass ();
    }

    @Before
    public void setUp() throws Exception {
        // Code executed before each test
    	initTest (); changeTolerance (8);
    }
 
    @Test
    public void detTest ()
    {
		System.out.println ("---");
		System.out.println ("-  detTest");
		System.out.println ("---");
		System.out.println ();

		MatrixOperations<Polynomial.PowerFunction<Double>> pfops =
			new MatrixOperations<Polynomial.PowerFunction<Double>> (pfm);

		pfops.setRow (1, m3x3, PolynomialFunctionOperations.makeRow (4d, -1d, 2d));
		pfops.setRow (2, m3x3, PolynomialFunctionOperations.makeRow (6d, 3d, 1d));
		pfops.setRow (3, m3x3, PolynomialFunctionOperations.makeRow (6d, 3d, -3d));

		Polynomial.PowerFunction<Double> det =
			PolynomialFunctionOperations.characteristicPolynomial (m3x3);
		List<Double> r = roots.evaluateEquation (det);

		System.out.println (pfm.toString (det));
		System.out.println (r);
		System.out.println ();

		for (double v : r)
		{
			double detV = det.eval (v);
			System.out.print ("eigenvalue = " + v);
			System.out.println (" det(m - I*lambda) = " + detV);
			assertEquality (detV, 0, "det " + v);
			System.out.println ("-");
			System.out.println ();
		}

		System.out.println ("===");
		System.out.println ();
    }

    @Test
    public void eigenTest()
    {
		System.out.println ("---");
		System.out.println ("-  eigenTest");
		System.out.println ("---");
		System.out.println ();
		
		Polynomial.PowerFunction<Double> p =
			eigen.computeCharacteristicPolynomialFor (m3x3dbl);
		List<Double> r = roots.evaluateEquation (p);

		System.out.println (pfm.toString (p));
		System.out.println (r);
		System.out.println ();

		for (double lambda : r)
		{
			System.out.println ("charPoly (" + lambda + ") = " + p.eval (lambda));
			System.out.println ("+++");

			Matrix<Double> mmIl =
				eigen.eigenvalueEquation (m3x3dbl, lambda);
			matrices.show (mmIl);

			System.out.println ("+++");
			System.out.println ();

			double det = matrices.det (mmIl);
			System.out.println ("det = " + det + " (" + lambda + ")");
			assertEquality (det, 0, "det " + lambda);

			System.out.println ();
			System.out.println ("===");
			System.out.println ();
		}

		System.out.println ("eigenvalues ");
		System.out.println (eigen.computeEigenvaluesFor (m3x3dbl));
		System.out.println ("===");
		System.out.println ();
    }

    @Test
    public void dominantEigensystem()
    {
		System.out.println ("---");
		System.out.println ("-  dominantEigensystem");
		System.out.println ("---");
		System.out.println ();

		Vector<Double> eigenvector = new Vector<Double> (mgr);
		Double eigenvalue = eigen.findDominantEigensystemMemberFor (m3x3dbl, eigenvector, 20, 5);
		vops.show (eigenvector); System.out.println (eigenvalue);

		boolean check = eigen.checkSolution (m3x3dbl, eigenvector, eigenvalue, 2);
		System.out.println (check? "Verified !!!": "Check failed !!!");
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

}
