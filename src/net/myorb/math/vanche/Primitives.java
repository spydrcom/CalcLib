
package net.myorb.math.vanche;

import net.myorb.math.linalg.TriangularMatrix;

import net.myorb.math.Polynomial.PowerFunction;
import net.myorb.math.polynomial.PolynomialFamilyManager;
import net.myorb.math.polynomial.families.ChebyshevPolynomial;
import net.myorb.math.polynomial.families.chebyshev.ChebyshevSplineFunction;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.Vector;

import net.myorb.math.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * primitive data descriptions for VanChe standard
 * @author Michael Druckman
 */
public class Primitives
{


	/**
	 * this implementation limits the data type to Double
	 */
	public static final ExpressionFloatingFieldManager manager = new ExpressionFloatingFieldManager ();


	/**
	 * the basic vector type for samples and solutions
	 */
	public static class VancheVector extends Vector <Double>
	{

		/**
		 * new empty vector, use common data manager
		 */
		public VancheVector ()
		{
			super (Primitives.manager);
		}

		/**
		 * @param source vector of data to add to this object
		 */
		public VancheVector (Vector <Double> source)
		{
			super (source.getSpaceDescription ());
			source.addToList (elements);
		}
	}


	/**
	 * sample vectors have methods for population of the content
	 */
	public static class SampleVector extends VancheVector
	{

		/**
		 * @param points sample data to include in vector
		 */
		public SampleVector (List <Double> points)
		{
			this.load (points);
		}

		/**
		 * @param points sample data to include in vector
		 */
		public SampleVector (double[] points)
		{
			ArrayList<Double> list = new ArrayList<Double> ();
			for (double t : points) list.add (t);
			this.load (list);
		}

		/**
		 * samples taken from function evaluations
		 * @param f the function to use for evaluations
		 * @param xAxisBase the lowest domain value to use in interpolation
		 * @param tick the portion equal to a VANCHE standard tick
		 * @return the vector of values
		 */
		public static SampleVector forFunction
			(
				Function<Double> f,
				double xAxisBase,
				double tick
			)
		{
			double d [] = new double [VANCHE_SIZE],							// construct domain of standard size
				delta = VANCHE_STANDARD_TICK / tick, x = xAxisBase;			// prepare for offset and tick multiplier
			for (int i = 0; i < d.length; i++) { d [i] = x; x += delta; }	// standard evenly spaced domain points
			return forFunction (f, d);										// compute samples over domain
		}

		/**
		 * samples taken from function evaluations
		 * @param f the function to use for evaluations
		 * @param domain the values along x-axis to use for interpolation
		 * @return the vector of values
		 */
		public static SampleVector forFunction
			(
				Function<Double> f,
				double [] domain
			)
		{
			double [] samples = new double [VANCHE_SIZE];
			for (int i = 0; i < samples.length; i++)
			{ samples [i] = f.eval (domain [i]); }
			return new SampleVector (samples);
		}

	}


	/**
	 * solution vectors have methods for distribution of results
	 */
	public static class SolutionVector extends VancheVector
	{

		/**
		 * @param source a vector of values to use as the solution
		 */
		public SolutionVector (Vector<Double> source)
		{
			super (source);
		}

		/**
		 * @return an array of the solution contents
		 */
		public Double[] getContents ()
		{
			return elements.toArray (manager.getEmptyArray ());
		}

	}


	/**
	 * specifically call out the 31 domain points
	 * used in the VanChe standard interpolation
	 */
	public static final
		double [] DOMAIN = new double []
	{
		-1.5, -1.4, -1.3, -1.2, -1.1,
		-1.0, -0.9, -0.8, -0.7, -0.6,
		-0.5, -0.4, -0.3, -0.2, -0.1,
		 0.0,
		 0.1,  0.2,  0.3,  0.4,  0.5,
		 0.6,  0.7,  0.8,  0.9,  1.0,
		 1.1,  1.2,  1.3,  1.4,  1.5
	};
	public static final int  VANCHE_SIZE  = DOMAIN.length;
	public static final double VANCHE_STANDARD_TICK = 0.1;


	/**
	 * Vandermonde matrix for Chebyshev T coefficients
	 */
	public static class VMatrix extends Matrix<Double>
	{
		public VMatrix ()
		{
			super (VANCHE_SIZE, VANCHE_SIZE, Primitives.manager);
		}
	}


	/**
	 * @param A the matrix to hold the VanChe constants
	 */
	public static void construct (VMatrix A)
	{
		int N = A.getEdgeCount ();
		ChebyshevPolynomial <Double> chebyshev =
			new ChebyshevPolynomial <Double> (A.getSpaceDescription ());
		PolynomialFamilyManager.PowerFunctionList <Double> T = chebyshev.getT (N);

		for (int n = 0; n < N; n++)
		{
			int col = n + 1;

			PowerFunction <Double> Tn = T.get (n);

			for (int row = 1; row <= N; row++)
			{
				A.set ( row, col, Tn.eval ( DOMAIN [ row - 1 ] ) );
			}
		}
	}


	/**
	 * perform decomposition returning completed object
	 * @param A the Vandermonde matrix holding Chebyshev equation values
	 * @return the decomposition representation of the VanChe matrix
	 */
	public static TriangularMatrix.Decomposition <Double> decompose (VMatrix A)
	{
		return TriangularMatrix.decompose (A);
	}


	/**
	 * build the VanChe matrix and perform the decomposition
	 * @return the decomposition representation of the VanChe matrix
	 */
	public static TriangularMatrix.Decomposition <Double> getVancheLud ()
	{
		VMatrix A;
		construct (A = new VMatrix ());
		return decompose (A);
	}


	/**
	 * compute solution vector for specified samples
	 * @param samples a vector of points to be interpolated
	 * @param using the decomposition representation to use for solution
	 * @return the vector of computed results
	 */
	public static SolutionVector solveFor (SampleVector samples, TriangularMatrix.Decomposition <Double> using)
	{
		return new SolutionVector (TriangularMatrix.solve (samples, using));
	}


	/**
	 * compute Chebyshev coefficients for samples
	 * @param samples vector of points to be interpolated
	 * @return the solution vector holding Chebyshev T coefficients
	 */
	public static SolutionVector solveFor (SampleVector samples)
	{
		return solveFor (samples, getVancheLud ());
	}


	/**
	 * build a Chebyshev spline for the samples
	 * @param points vector of points to be interpolated
	 * @return a Chebyshev T spline function
	 */
	public static ChebyshevSplineFunction <Double> splineFor (SampleVector points)
	{
		return new ChebyshevSplineFunction <Double> (solveFor (points).getContents (), manager);
	}


	/**
	 * build a Chebyshev spline for the samples
	 * @param samples array of points to be interpolated
	 * @return a Chebyshev T spline function
	 */
	public static ChebyshevSplineFunction <Double> splineFor (double[] samples)
	{
		return splineFor (new SampleVector (samples));
	}


	/**
	 * build spline for function
	 * @param f the function to be interpolated
	 * @return a Chebyshev T spline function
	 */
	public static ChebyshevSplineFunction <Double> splineFor (Function<Double> f)
	{
		return splineFor (SampleVector.forFunction (f, DOMAIN));
	}


	/**
	 * build spline for samples
	 *  with x-axis shifted and expanded
	 * @param samples vector of points to be interpolated
	 * @param xAxisBase the value of x to map to Chebyshev base
	 * @param tick the multiplier for the domain
	 * @return a Chebyshev T spline function
	 */
	public static ChebyshevSplineFunction <Double> splineFor
		(
			SampleVector samples,
			double xAxisBase,
			double tick
		)
	{
		ChebyshevSplineFunction <Double> spline =
			new ChebyshevSplineFunction <Double>
			(
				solveFor (samples).getContents (),
				ChebyshevSplineFunction.CHEBYSHEV_SPLINE_BASE, tick,
				manager
			);
		spline.setShift (xAxisBase);
		return spline;
	}


	/**
	 * build spline for function
	 *  with x-axis shifted and expanded
	 * @param f the function to be interpolated
	 * @param xAxisBase the value of x to map to Chebyshev base
	 * @param tick the multiplier for the domain
	 * @return a Chebyshev T spline function
	 */
	public static ChebyshevSplineFunction <Double> splineFor
		(
			Function<Double> f,
			double xAxisBase,
			double tick
		)
	{
		SampleVector samples =
			SampleVector.forFunction (f, xAxisBase, tick);
		return splineFor (samples, xAxisBase, tick);
	}


}

