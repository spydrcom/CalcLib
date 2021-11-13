
package net.myorb.math.computational;

import net.myorb.math.polynomial.families.ChebyshevPolynomial;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.*;
import net.myorb.math.matrices.*;

import java.util.List;

/**
 * use spline description to perform evaluation
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class VC31SplineProcessor<T> extends ChebyshevPolynomial<T>
{


	/**
	 * requires manager for data type
	 * @param environment the central computation environment state
	 */
	public VC31SplineProcessor (Environment<T> environment)
	{
		super (environment.getSpaceManager ());
		this.matrixOperations = new MatrixOperations<T> (manager);
		this.conversion = environment.getConversionManager ();
		this.valueManager = environment.getValueManager ();
		this.environment = environment;
	}
	protected Environment<T> environment;
	protected MatrixOperations<T> matrixOperations;
	protected DataConversions<T> conversion;
	protected ValueManager<T> valueManager;
	private T TWO = manager.newScalar (2);


	/**
	 * evaluate spline at specified value
	 * @param coefficients the matrix of coefficients of the segments of the spline
	 * @param lowestKnot the value of the lowest represented knot
	 * @param x the value of X at which to evaluate function
	 * @return the evaluated result
	 */
	public T eval (Matrix<T> coefficients, T lowestKnot, T x)
	{
		if (manager.lessThan (x, lowestKnot))
		{
			throw new RuntimeException ("Spline constraint error, value below lo");
		}

		int row = 1;
		T knot = manager.add (lowestKnot, TWO);
		while (manager.lessThan (knot, x))
		{
			knot = manager.add (knot, TWO); row++;
		}
		
		if (row > coefficients.rowCount ())
		{
			throw new RuntimeException ("Spline constraint error, value above hi");
		}

		Coefficients<T> c; coefficients.getRow (row).addToList (c = new Coefficients<T> ());
		T translation = manager.add (lowestKnot, manager.newScalar (2 * row - 1));
		T evalAt = manager.add (x, manager.negate (translation));
		return evaluatePolynomial (c, evalAt);
	}



	/**
	 * solve LUx = b
	 * @param L the lower triangle
	 * @param U the upper triangle
	 * @param b the equated values
	 * @param p optional pivot
	 * @return the solution
	 */
	public Vector<T> luXb (Matrix<T> L, Matrix<T> U, Vector<T> b, Vector<T> p)
	{ return matrixOperations.getTriangularOperations ().luXb (L, U, b, p); }


	/**
	 * use VC31 to solve LUx = b
	 * @param b vector of b solution values
	 * @return vector of x solution values
	 */
	public Vector<T> vc31 (Vector<T> b)
	{
		Matrix<T>
		L = valueManager.toMatrix (environment.getValue ("VC31L")),
		U = valueManager.toMatrix (environment.getValue ("VC31U")),
		P = valueManager.toMatrix (environment.getValue ("VC31P"));
		return luXb (L, U, b, P.getCol (1));
	}


	/**
	 * solve LUx = b
	 * @param L the lower triangle
	 * @param U the upper triangle
	 * @param pivot optional pivot
	 * @param solution the b vector
	 * @return the X vector of values
	 */
	public ValueManager.GenericValue luXb
	(Matrix<T> L, Matrix<T> U, Vector<T> pivot, ValueManager.GenericValue solution)
	{
		Vector<T> b = conversion.toVector (solution);
		return conversion.vectorToArray (luXb (L, U, b, pivot));
	}


	/*
	 *                        start                                                          low                 knot                  high
	 *    0   1   2   3   4     5   6   7   8   9   10  11  12  13  14  15   16  17  18  19  20  21  22  23  24   25    26  27  28  29  30  
	 *    .   .   .   .   .     .   .   .   .   .   .   .   .   .    .   .   .   .   .   .   .   .   .   .   .     .    .   .   .   .    .   
	 *  -1.5  .4  .3  .2  .1   -1   .9  .8  .7  .6  .5  .4  .3  .2  .1   0   .1  .2  .3  .4  .5  .6  .7  .8  .9    1    .1  .2  .3  .4  1.5  
	 *   [  low end knot  ]    [   standard Chebyshev constraints specify interval of [-1, 1] for best behavior    ]
	 *   [    not used    ]
	 *   [Runge phenomenon]
	 * 
	 *  30   31   32  33   34   35   36   37   38   39   40  41  42  43  44  45   46  47  48  49  50  
	 *   .   .   .    .    .    .    .    .    .    .    .   .   .   .   .    .   .   .   .   .   .   
	 *  1.5  .6  .7   .8   .9   2    .1   .2   .3   .4   .5  .6  .7  .8  .9   3   .1  .2  .3  .4  .5  
	 *                                                   low                 knot                 high
	 */


	/**
	 * use VC31 interpolation methods to build a spline matrix
	 * @param values the list of function values to be interpolated
	 * @return a matrix with a set of spline segment coefficients per row
	 */
	public Matrix<T> vc31Spline (List<T> values)
	{
		int n = values.size (), rows = (n - 11) / 20, end = 30, r = 1;
		Matrix<T> coefficients = new Matrix<T> (rows, 31, manager);
		List<T> a31; Vector<T> segment;

		while (end < n)
		{
			a31 = values.subList (end - 30, end + 1);
			segment = vc31 (conversion.toVector (a31));
			matrixOperations.setRow (r++, coefficients, segment);
			end += 20;
		}

		return coefficients;
	}


}

