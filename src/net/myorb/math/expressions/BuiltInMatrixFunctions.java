
package net.myorb.math.expressions;

import net.myorb.math.linalg.SolutionPrimitives;
import net.myorb.math.characteristics.EigenvaluesAndEigenvectors;
import net.myorb.math.polynomial.families.ChebyshevPolynomial;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.algorithms.ClMathSysEQ;
import net.myorb.math.expressions.StructureStorage;

import net.myorb.math.matrices.decomposition.GenericLUD;
import net.myorb.math.matrices.decomposition.GenericQRD;
import net.myorb.math.matrices.decomposition.ColtLUD;

import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.DataSequence;

import net.myorb.math.computational.*;
import net.myorb.math.matrices.*;
import net.myorb.math.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * implementation of built-in matrix functions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class BuiltInMatrixFunctions<T> extends BuiltInPolynomialFunctions<T>
{


	public BuiltInMatrixFunctions
	(Environment<T> environment, PowerLibrary<T> powerLibrary)
	{
		super (environment, powerLibrary);
		this.matrixOperations = new MatrixOperations<T> (spaceManager);
		this.vc31SplineEngine = new VC31SplineProcessor<T> (environment);
		this.eigenvaluesAndEigenvectors = new EigenvaluesAndEigenvectors<T> (spaceManager, powerLibrary);
		this.simEq = new SimultaneousEquations<T> (spaceManager);
		this.environment = environment;
	}
	protected EigenvaluesAndEigenvectors<T> eigenvaluesAndEigenvectors;
	protected VC31SplineProcessor<T> vc31SplineEngine;
	protected MatrixOperations<T> matrixOperations;
	protected SimultaneousEquations<T> simEq;
	protected Environment<T> environment;


	/**
	 * separate parameters from a value list
	 * - functions with multiple parameters wrap them in a ValueList
	 * @param values the generic value holding a value list
	 * @return a java util list of the parameters
	 */
	public static List <ValueManager.GenericValue>
		getParameters (ValueManager.GenericValue values)
	{
		return ( ( ValueManager.ValueList ) values ).getValues ();
	}


	/*
	 * construct and optionally initialize a new matrix object
	 */

	/**
	 * construct new matrix from array
	 * @param values the parameters to the call
	 * @return new matrix populated from array
	 */
	public ValueManager.GenericValue newMatrix (ValueManager.GenericValue values)
	{
		if (valueManager.isDimensioned (values))
		{ return newMatrix (valueManager.toDimensionedValue (values).getValues ()); }
		else  return newMatrix ( (ValueManager.ValueList) values );
	}

	/**
	 * process a value list
	 * - last two parameters will be row and column counts
	 * - cases having 3 parameters have the first entry as an initialization value
	 * @param values taken from dimensioned object
	 * @return the described matrix
	 */
	public ValueManager.GenericValue newMatrix (List <T> values)
	{
		int n = values.size ();
		T init = spaceManager.getZero ();
		if (n == 3) init = values.get (0);
		T rows = values.get (n-2), cols = values.get (n-1);
		int irows = spaceManager.toNumber (rows).intValue ();
		int icols = spaceManager.toNumber (cols).intValue ();
		Matrix<T> m = new Matrix<T> (irows, icols, init, spaceManager);
		return valueManager.newMatrix (m);
	}

	/**
	 * process a value list having 3 parameters
	 * - first parameter will necessarily be an array of values to place in matrix
	 * - array may be a single value which will be used to initialize all matrix cells
	 * - second and third will be row and column counts in that order
	 * @param parameterList recognized as value list
	 * @return the described matrix
	 */
	public ValueManager.GenericValue newMatrix (ValueManager.ValueList parameterList)
	{
		int rows = environment.intParameter (1, parameterList), cols = environment.intParameter (2, parameterList);
		List <T> init = valueManager.toArray (parameterList.getValues ().get (0));
		Matrix<T> m = new Matrix<T> (rows, cols, init, spaceManager);
		return valueManager.newMatrix (m);
	}


	/*
	 * matrix indexing and vector extraction
	 */

	/**
	 * matrix element indexing
	 * @param m the matrix object to be indexed
	 * @param indicies the index value(s) identifying element
	 * @return the value of the specified element
	 */
	public ValueManager.GenericValue index (Matrix<T> m, ValueManager.GenericValue indicies)
	{
		if (valueManager.isArray (indicies))
		{
			List<T> indexValues = valueManager.toArray (indicies);
			int r = spaceManager.toNumber (indexValues.get (0)).intValue ();
			int c = spaceManager.toNumber (indexValues.get (1)).intValue ();
			return valueManager.newDiscreteValue (m.get (r, c));
		}
		else
		{
			int index = valueManager.toInt (indicies, spaceManager);
			return conversion.vectorToArray (m.getRow (index));
		}
	}
	public ValueManager.GenericValue diagIndex
	(ValueManager.GenericValue matrix, ValueManager.GenericValue index)
	{
		Matrix<T> m = valueManager.toMatrix (matrix);
		int n = valueManager.toInt (index, spaceManager);
		return conversion.vectorToArray (matrixOperations.diag (m, n));
	}
	public ValueManager.GenericValue rowIndex
	(ValueManager.GenericValue matrix, ValueManager.GenericValue index)
	{
		int n = valueManager.toInt (index, spaceManager);
		return conversion.vectorToArray (valueManager.toMatrix (matrix).getRow (n));
	}
	public ValueManager.GenericValue colIndex
	(ValueManager.GenericValue matrix, ValueManager.GenericValue index)
	{
		int n = valueManager.toInt (index, spaceManager);
		return conversion.vectorToArray (valueManager.toMatrix (matrix).getCol (n));
	}

	/**
	 * read row vector from matrix
	 * @param values the parameters to the call
	 * @return a value holding the vector values
	 */
	public ValueManager.GenericValue row (ValueManager.GenericValue values)
	{
		ValueManager.ValueList parameterList;
		int n = environment.intParameter (1, parameterList = (ValueManager.ValueList)values);
		return conversion.vectorToArray (valueManager.toMatrix (parameterList.getValues ().get (0)).getRow (n));
	}

	/**
	 * read column vector from matrix
	 * @param values the parameters to the call
	 * @return a value holding the vector values
	 */
	public ValueManager.GenericValue column (ValueManager.GenericValue values)
	{
		ValueManager.ValueList parameterList;
		int n = environment.intParameter (1, parameterList = (ValueManager.ValueList)values);
		return conversion.vectorToArray (valueManager.toMatrix (parameterList.getValues ().get (0)).getCol (n));
	}

	/**
	 * collect values along diag of matrix
	 * @param values the parameters to call, matrix and diag offset
	 * @return array of values along diag of matrix
	 */
	public ValueManager.GenericValue
	diag (ValueManager.GenericValue values) { return diag ((ValueManager.ValueList)values); }
	public ValueManager.GenericValue diag (ValueManager.ValueList values)
	{
		List<T> array;
		List<ValueManager.GenericValue> items = values.getValues ();

		matrixOperations.diag
			(
				valueManager.toMatrix (items.get (0)),
				valueManager.toInt (items.get (1), spaceManager)
			).addToList (array = new ArrayList<T>());

		return valueManager.newDimensionedValue (array);
	}


	/*
	 * arithmetic addition
	 */

	/**
	 * compute sum of two matrices
	 * @param values the parameters to the call
	 * @return the computed result
	 */
	public ValueManager.GenericValue matadd (ValueManager.GenericValue values)
	{
		ValueManager.ValueList parameterList = (ValueManager.ValueList)values;
		List<ValueManager.GenericValue> plistValues = parameterList.getValues ();
		Matrix<T> left = valueManager.toMatrix (plistValues.get (0));
		Matrix<T> right = valueManager.toMatrix (plistValues.get (1));
		return valueManager.newMatrix (add (left, right));
	}
	public Matrix<T> add (Matrix<T> left, Matrix<T> right)
	{
		return matrixOperations.sum (left, right);
	}
	public Matrix<T> negate (Matrix<T> m)
	{
		return scale (spaceManager.newScalar (-1), m);
	}


	/*
	 * arithmetic multiplication
	 */

	/**
	 * compute product of two matrices
	 * @param values the parameters to the call
	 * @return the computed result
	 */
	public ValueManager.GenericValue matmul (ValueManager.GenericValue values)
	{
		List<ValueManager.GenericValue>
		plistValues = ((ValueManager.ValueList)values).getValues ();
		ValueManager.GenericValue leftVal = plistValues.get (0), rightVal = plistValues.get (1);
		return valueManager.newMatrix (multiply (leftVal, rightVal));
	}
	public Matrix<T> multiply (ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		if (valueManager.isDiscrete (left))
			return scale (valueManager.toDiscrete (left), valueManager.toMatrix (right));
		else if (valueManager.isDiscrete (right))
			return scale (valueManager.toDiscrete (right), valueManager.toMatrix (left));
		else
		{
			Matrix<T> l, r;
			if (valueManager.isArray (left))
			{
				r = valueManager.toMatrix (right);
				List<T> a = valueManager.toArray (left);
				l = matrixOperations.rowMatrix (a);
			}
			else if (valueManager.isArray (right))
			{
				l = valueManager.toMatrix (left);
				List<T> a = valueManager.toArray (right);
				r = matrixOperations.columnMatrix (a);
			}
			else
			{
				l = valueManager.toMatrix (left);
				r = valueManager.toMatrix (right);
			}
			return matrixOperations.product (l, r);
		}
	}
	public Matrix<T> scale (T scalar, Matrix<T> m)
	{
		return matrixOperations.times (scalar, m);
	}
	public ValueManager.GenericValue matscale (T scalar, Matrix<T> m)
	{
		return valueManager.newMatrix (scale (scalar, m));
	}


	/*
	 * abstract products
	 */

	/**
	 * compute tensor product of two matrices
	 * @param left left side argument for operation
	 * @param right right side argument for operation
	 * @return the computed result
	 */
	public ValueManager.GenericValue tensor
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		Matrix<T> leftMat = valueManager.toMatrix (left),
			rightMat = valueManager.toMatrix (right);
		Matrix<T> result = matrixOperations.tensor (leftMat, rightMat);
		return valueManager.newMatrix (result);
	}

	/**
	 * compute power of a matrix
	 * @param m the matrix being multiplied
	 * @param exponent the integer exponent value
	 * @return the computed matrix
	 */
	public Matrix<T> toThe (Matrix<T> m, int exponent)
	{
		Matrix<T> result = m;
		for (int i=2; i<= exponent; i++)
		{ result = matrixOperations.product (result, m); }
		return result;
	}


	/*
	 * common matrix primitives
	 */

	/**
	 * construct identity matrix
	 * @param values the parameters to the call
	 * @return the computed result
	 */
	public ValueManager.GenericValue identity (ValueManager.GenericValue values)
	{
		Matrix<T> computedResult = matrixOperations.identity (valueManager.toInt (values, spaceManager));
		return valueManager.newMatrix (computedResult);
	}

	/**
	 * compute determinant of matrix
	 * @param values the parameters to the call
	 * @return the computed result
	 */
	public ValueManager.GenericValue det (ValueManager.GenericValue values)
	{
		T computedResult = matrixOperations.det (valueManager.toMatrix (values));
		return valueManager.newDiscreteValue (computedResult);
	}

	/**
	 * compute multiplicative inverse of matrix using adjugate
	 * @param values the parameters to the call
	 * @return the computed result
	 */
	public ValueManager.GenericValue inv (ValueManager.GenericValue values)
	{
		Matrix<T> computedResult = matrixOperations.inv (valueManager.toMatrix (values));
		return valueManager.newMatrix (computedResult);
	}

	/**
	 * compute trace of matrix
	 * @param values the parameters to the call
	 * @return the computed result
	 */
	public ValueManager.GenericValue trace (ValueManager.GenericValue values)
	{
		T computedResult = matrixOperations.tr (valueManager.toMatrix (values));
		return valueManager.newDiscreteValue (computedResult);
	}

	/**
	 * compte transpose of matrix
	 * @param values the parameters to the call
	 * @return the computed result
	 */
	public ValueManager.GenericValue transpose (ValueManager.GenericValue values)
	{
		Matrix<T> computedResult = matrixOperations.transpose (valueManager.toMatrix (values));
		return valueManager.newMatrix (computedResult);
	}

	/**
	 * compute adjugate of matrix
	 * @param values the parameters to the call
	 * @return the computed result
	 */
	public ValueManager.GenericValue adj (ValueManager.GenericValue values)
	{
		Matrix<T> computedResult = matrixOperations.adj (valueManager.toMatrix (values));
		return valueManager.newMatrix (computedResult);
	}


	/*
	 * macro scale abstractions
	 */

	/**
	 * construct augmented matrix
	 * @param values the parameters to the call
	 * @return the computed result
	 */
	public ValueManager.GenericValue augmented (ValueManager.GenericValue values)
	{
		List <ValueManager.GenericValue> P = getParameters (values);
		ValueManager.GenericValue first = P.get (0), second = P.get (1), mat, vec;
		if (valueManager.isMatrix (first)) { mat = first; vec = second; } else { mat = second; vec = first; }

		MatrixAccess<T> aug = simEq.buildAugmentedMatrix
			(valueManager.toMatrix (mat), conversion.toVector (valueManager.toArray (vec)));
		return valueManager.newMatrix (matrixOperations.copy (aug));
	}

	/**
	 * construct minor matrix from array
	 * @param values the parameters to the call
	 * @return new matrix populated from array
	 */
	public ValueManager.GenericValue minor (ValueManager.GenericValue values)
	{
		ValueManager.ValueList parameterList = (ValueManager.ValueList)values;
		List<ValueManager.GenericValue> plistValues = parameterList.getValues ();
		int row = environment.intParameter (1, parameterList), col = environment.intParameter (2, parameterList);
		Matrix<T> m = matrixOperations.minor (valueManager.toMatrix (plistValues.get (0)), row, col);
		return valueManager.newMatrix (m);
	}

	/**
	 * compute cofactor of minor matrix
	 * @param values the parameters to the call
	 * @return the computed result
	 */
	public ValueManager.GenericValue cofactor (ValueManager.GenericValue values)
	{
		ValueManager.ValueList parameterList = (ValueManager.ValueList)values;
		List<ValueManager.GenericValue> plistValues = parameterList.getValues ();
		int row = environment.intParameter (1, parameterList), col = environment.intParameter (2, parameterList);
		T computedResult = matrixOperations.cofactor (valueManager.toMatrix (plistValues.get (0)), row, col);
		return valueManager.newDiscreteValue (computedResult);
	}

	/**
	 * compute comatrix of matrix
	 * @param values the parameters to the call
	 * @return the computed result
	 */
	public ValueManager.GenericValue comatrix (ValueManager.GenericValue values)
	{
		Matrix<T> computedResult = matrixOperations.comatrix (valueManager.toMatrix (values));
		return valueManager.newMatrix (computedResult);
	}

	/**
	 * compute the companion matrix for a polynomial
	 * @param values an array of coefficients to the polynomial
	 * @return the companion matrix
	 */
	public ValueManager.GenericValue companion (ValueManager.GenericValue values)
	{
		Polynomial.PowerFunction<T> p = poly.getPolynomialFunction (getCoefficients (values));
		MatrixAccess<T> computedResult = eigenvaluesAndEigenvectors.computeCompanionMatrixFor (p);
		return valueManager.newMatrix (matrixOperations.copy (computedResult));
	}


	/*
	 * Linear Algebra
	 */

	/**
	 * solve system with gaussian elimination
	 * @param values the parameters to the call
	 * @return the computed result
	 */
	public ValueManager.GenericValue gaussian (ValueManager.GenericValue values)
	{
		List <ValueManager.GenericValue> P = getParameters (values);
		ValueManager.GenericValue first = P.get (0), second = P.get (1), mat, vec;
		if (valueManager.isMatrix (first)) { mat = first; vec = second; } else { mat = second; vec = first; }

		VectorAccess<T> solution = simEq.applyGaussianElimination
			(valueManager.toMatrix (mat), conversion.toVector (valueManager.toArray (vec)));
		return valueManager.newDimensionedValue (conversion.toArray (solution));
	}

	/**
	 * solve system with column substitution and determinants
	 * @param values the parameters to the call
	 * @return the computed result
	 */
	public ValueManager.GenericValue solve (ValueManager.GenericValue values)
	{
		List <ValueManager.GenericValue> P = getParameters (values);
		ValueManager.GenericValue first = P.get (0), second = P.get (1), mat, vec;
		if (valueManager.isMatrix (first)) { mat = first; vec = second; } else { mat = second; vec = first; }

		VectorAccess<T> solution = simEq.solve
			(valueManager.toMatrix (mat), conversion.toVector (valueManager.toArray (vec)));
		return valueManager.newDimensionedValue (conversion.toArray (solution));
	}


	/*
	 * Linear Algebra (decomposition, solution, det, inverse, ...)
	 */

	/**
	 * perform (GENERIC) QR decomposition
	 * @param values the parameters to the call
	 * @return the decomposition of the source matrix
	 */
	public ValueManager.GenericValue qrd (ValueManager.GenericValue values)
	{
		Matrix <T> source = valueManager.toMatrix (values);
		GenericQRD <T> QR = new GenericQRD <T> (spaceManager);
		return valueManager.newMatrix (QR.decompose (source).asMatrix ());
	}

	/**
	 * compute (GENERIC) solution to QR problem set
	 * @param values the parameters to the call
	 * @return the solution computed
	 */
	public ValueManager.GenericValue qrsolve (ValueManager.GenericValue values)
	{
		List <ValueManager.GenericValue> P = getParameters (values);
		Matrix<T> decomposedMatrix = valueManager.toMatrix (P.get (0));
		Vector <T> request = conversion.toVector (valueManager.toArray (P.get (1)));
		List <T> solution = qrsolve (decomposedMatrix, request).getElementsList ();
		return valueManager.newDimensionedValue (solution);
	}
	Vector <T> qrsolve (Matrix<T> DM, Vector <T> request)
	{
		GenericQRD <T> QR = new GenericQRD <T> (spaceManager);
		SolutionPrimitives.Content <T> C = new SolutionPrimitives.Content <T> (request);
		@SuppressWarnings("unchecked") Vector <T> solution = (Vector <T>) QR.solve (QR.load (DM), C);
		return solution;
	}


	/*
	 * LINALG library implementations
	 */

	/**
	 * matrix decomposition by library instance
	 * @param parameters the parameters to the call
	 * @return a generic wrapper holding a decomposition object
	 */
	public ValueManager.GenericValue decompose (ValueManager.GenericValue parameters)
	{
		List <ValueManager.GenericValue> P = getParameters (parameters);
		@SuppressWarnings("unchecked") ClMathSysEQ.SolutionManager <T> solMgr =
			(ClMathSysEQ.SolutionManager <T>) valueManager.getStructuredObject (P.get (1));
		SolutionPrimitives <T> solution = solMgr.provideSolution ();

//		@SuppressWarnings("unchecked") SolutionPrimitives <T>
//			// the library instance reference appears as an identifier to be loaded
//			solution = (SolutionPrimitives <T>) valueManager.getStructuredObject (P.get (1));

		return valueManager.newStructure (solMgr.wrap (solution.decompose (valueManager.toMatrix (P.get (0)))));
	}

	/**
	 * solution to a system of equations
	 * - a matrix decomposition should be P(0)
	 * @param parameters the parameters to the call
	 * @return a dimensioned object holding the computed solution
	 */
	public ValueManager.GenericValue solveSOE (ValueManager.GenericValue parameters)
	{
		List <ValueManager.GenericValue> P = getParameters (parameters);
		@SuppressWarnings("unchecked") ClMathSysEQ.SolutionProduct <T> libraryInstance =
				(ClMathSysEQ.SolutionProduct <T>) ((StructureStorage) P.get (0)).getStructure ();
		ValueManager.GenericValue content = P.get (1);

		if (valueManager.isMatrix (content))
		{ return solveSOE (libraryInstance, valueManager.toMatrix (content)); }
		return solveSOE (libraryInstance, toVector (valueManager.toDimensionedValue (content)));
	}
	@SuppressWarnings("unchecked") ValueManager.GenericValue
		solveSOE (ClMathSysEQ.SolutionProduct <T> libraryInstance, Matrix<T> m)
	{
		SolutionPrimitives <T>
			provided = libraryInstance.provideSolution ();
		if (provided instanceof SolutionPrimitives.MatrixSolution)
		{
			SolutionPrimitives.MatrixSolution <T> matSol =
					(SolutionPrimitives.MatrixSolution <T>) provided;
			return valueManager.newMatrix (matSol.solve (libraryInstance.getProduct (), m));
		}
		throw new RuntimeException ("Solution algorithm does not support full matrix request");
	}
	@SuppressWarnings("unchecked") ValueManager.GenericValue
		solveSOE (ClMathSysEQ.SolutionProduct <T> libraryInstance, SolutionPrimitives.Content <T> b)
	{
		SolutionPrimitives.Content <T> x =
			(SolutionPrimitives.Content<T>) libraryInstance
				.provideSolution ().solve (libraryInstance.getProduct (), b);
		return valueManager.newDimensionedValue (x.getElementsList ());
	}
	SolutionPrimitives.Content <T> toVector (ValueManager.DimensionedValue <T> dim)
	{
		List <T> values = dim.getValues ();
		SolutionPrimitives.Content <T> vector =
			new SolutionPrimitives.Content <T> (values.size (), spaceManager);
		int i = 1; for (T v : values) vector.set (i++, v);
		return vector;
	}


	/*
	 * triangular matrix specific
	 */

	/**
	 * upper triangular matrix
	 * @param values the parameters to the call
	 * @return the computed result
	 */
	public ValueManager.GenericValue triu (ValueManager.GenericValue values)
	{
		if (values instanceof ValueManager.ValueList)
		{ return triu ((ValueManager.ValueList)values); }
		Matrix<T> computedResult = matrixOperations.triu (valueManager.toMatrix (values), 0);
		return valueManager.newMatrix (computedResult);
	}
	public ValueManager.GenericValue triu (ValueManager.ValueList values)
	{
		List<ValueManager.GenericValue>
				items = values.getValues ();
		Matrix<T> computedResult = matrixOperations.triu
			(valueManager.toMatrix (items.get (0)), valueManager.toInt (items.get (1), spaceManager));
		return valueManager.newMatrix (computedResult);
	}

	/**
	 * lower triangular matrix
	 * @param values the parameters to the call
	 * @return the computed result
	 */
	public ValueManager.GenericValue tril (ValueManager.GenericValue values)
	{
		if (values instanceof ValueManager.ValueList)
		{ return tril ((ValueManager.ValueList)values); }
		Matrix<T> computedResult = matrixOperations.tril (valueManager.toMatrix (values), 0);
		return valueManager.newMatrix (computedResult);
	}
	public ValueManager.GenericValue tril (ValueManager.ValueList values)
	{
		List<ValueManager.GenericValue>
				items = values.getValues ();
		Matrix<T> computedResult = matrixOperations.tril
			(valueManager.toMatrix (items.get (0)), valueManager.toInt (items.get (1), spaceManager));
		return valueManager.newMatrix (computedResult);
	}

	/**
	 * reorder a vector to match a pivot list
	 * @param parameters the vector and the pivot list as parameters
	 * @return the altered vector
	 */
	public ValueManager.GenericValue pivot (ValueManager.GenericValue parameters)
	{
		List<ValueManager.GenericValue>
			values = valueManager.toList (parameters);
		ValueManager.GenericValue orderVal = values.get (1);
		Vector<T> reordered = matrixOperations.getTriangularOperations ().pivot
			(conversion.toVector (values.get (0)), getOrder (orderVal));
		return conversion.vectorToArray (reordered);
	}
	Vector<T> getOrder (ValueManager.GenericValue orderVal)
	{
		return valueManager.isMatrix (orderVal)?
		valueManager.toMatrix (orderVal).getCol (1):
		conversion.toVector (orderVal);
	}

	/**
	 * solve LUx = b
	 * - using triangular matrix decomposition (L, U, b, P)
	 * - this solution is coded in CalcLib generic fashion so can use any domain
	 * @param parameters list of parameters from command line
	 * @return the resulting value (typically array)
	 */
	public ValueManager.GenericValue luXb (ValueManager.GenericValue parameters)
	{
		Vector<T> pivot = null;
		List<ValueManager.GenericValue>
			values = valueManager.toList (parameters);
		// pivot vector is optional and order is literal absent P
		if (values.size() > 3) pivot = getOrder (values.get (3));
		ValueManager.GenericValue solution = values.get (2);
		Matrix<T> L = valueManager.toMatrix (values.get (0)),
			U = valueManager.toMatrix (values.get (1));
		return vc31SplineEngine.luXb (L, U, pivot, solution);
	}

	/**
	 * compute LU decomposition
	 * - triangular matrix decomposition on real domain
	 * - this decomposition uses the Colt library which only supports double float
	 * @param parameters list of parameters from command line
	 * @return the resulting value (pivot as array)
	 */
	public ValueManager.GenericValue LUD (ValueManager.GenericValue parameters)
	{
		List <ValueManager.GenericValue> P = getParameters (parameters);
		Matrix<T> L = valueManager.toMatrix (P.get (1)), U = valueManager.toMatrix (P.get (2));
		List<T> pivot = ludDecomposition (valueManager.toMatrix (P.get (0)), L, U);
		return valueManager.newDimensionedValue (pivot);
	}
	List<T> ludDecomposition (Matrix<T> A, Matrix<T> L, Matrix<T> U)
	{
		Vector <T> pivot = new Vector <T> (L.rowCount (), spaceManager);
		new ColtLUD ().decompose (A, L, U, pivot);
		return pivot.getElementsList ();		
	}

	/**
	 * compute matrix DET
	 * - using triangular matrix decomposition
	 * - this solution is coded in CalcLib generic fashion so can use any domain
	 * @param value the parameters to the call which must be only one source matrix
	 * @return the decomposition of the source matrix
	 */
	public ValueManager.GenericValue LUDDET (ValueManager.GenericValue value)
	{
		Matrix <T> source = valueManager.toMatrix (value);
		GenericLUD <T> LUD = new GenericLUD <T> (spaceManager);
		return valueManager.newDiscreteValue (LUD.det (source));
	}

	/**
	 * compute matrix inverse
	 * - using triangular matrix decomposition
	 * - this solution is coded in CalcLib generic fashion so can use any domain
	 * @param value the parameters to the call which must be only one source matrix
	 * @return the decomposition of the source matrix
	 */
	public ValueManager.GenericValue LUDINV (ValueManager.GenericValue value)
	{
		Matrix <T> source = valueManager.toMatrix (value);
		GenericLUD <T> LUD = new GenericLUD <T> (spaceManager);
		return valueManager.newMatrix (LUD.inv (source));
	}


	/*
	 * Linear Algebra (specific to spline generation)
	 */

	/**
	 * compute Chebyshev coefficients using VC31 constants
	 * @param parameters the function values to be interpolated
	 * @return the list of Chebyshev coefficients
	 */
	public ValueManager.GenericValue vc31NonSpline (List<T> parameters)
	{
		Regression<T> regression = new Regression<T> (environment);
		ChebyshevPolynomial.Coefficients<T> coefficients; Regression.Model<T> model;
		Vector<T> y = conversion.toVector (parameters); DataSequence2D<T> data = vc31xy (y);
		vc31SplineEngine.vc31 (y).addToList (coefficients = new ChebyshevPolynomial.Coefficients<T>());
		processRegression (model = regression.useChebyshevModel (coefficients, data), "Y", "VC31 Interpolation");
		regression.chartChebyshevRegression (model, "VC31 Interpolation"); // show stats and display scatter chart
		return model.coefficientsWithMetadata (null);
	}
	DataSequence2D<T> vc31xy (Vector<T> y)
	{
		int j = 6;
		DataSequence2D<T> data = new DataSequence2D<T>();
		for (int i = 0; i < 21; i++) data.addSample (vc21x (i), y.get (j++));
		return data;
	}
	T vc21x (int x) { return spaceManager.convertFromDouble ((double)(x - 10) / 10.0); }

	/**
	 * use VC31 interpolation methods to describe data sequence
	 * @param parameters the list of function values to be interpolated
	 * @return appropriate representation for segment (array) or spline (matrix)
	 */
	public ValueManager.GenericValue vc31 (ValueManager.GenericValue parameters)
	{
		List<T> values = valueManager.toArray (parameters);
		int n = values.size (), rows = (n - 11) / 20; if (rows == 1) return vc31NonSpline (values);
		if (rows < 1) throw new RuntimeException ("Too few data points, minimum is 31");
		return valueManager.newMatrix (vc31SplineEngine.vc31Spline (values));
	}

	/**
	 * evaluate call to VC31 spline function
	 * @param parameters the parameters to the call
	 * @return the value calculated
	 */
	public ValueManager.GenericValue vc31SplineEval (ValueManager.GenericValue parameters)
	{
		List<ValueManager.GenericValue> values = valueManager.toList (parameters);
		ValueManager.GenericValue p0 = values.get (0), p1 = values.get (1), p2 = values.get (2);
		T lowestKnot = valueManager.toDiscrete (p1), x = valueManager.toDiscrete (p2);
		T result = vc31SplineEngine.eval (valueManager.toMatrix (p0), lowestKnot, x);
		return valueManager.newDiscreteValue (result);
	}

	/**
	 * Vandermonde matrix for a Chebyshev interpolation
	 * @param values the x-axis values to be used constructing the matrix
	 * @return the Vandermonde matrix for the Chebyshev interpolation
	 */
	public ValueManager.GenericValue vanche (DataSequence<T> values)
	{
		Matrix<T> computedResult = new VandermondeChebyshev<T> (values, spaceManager);
		return valueManager.newMatrix (computedResult);
	}

	/**
	 * construct a zero knot for odd or even functions
	 * @param parameters the array to use for knot content
	 * @return array containing knot content
	 */
	public ValueManager.GenericValue genknot (ValueManager.GenericValue parameters)
	{
		List<T> values = valueManager.toArray (parameters), knot = new ArrayList<T> ();
		for (int i = 5; i > 0; i--) knot.add (values.get (i));
		return valueManager.newDimensionedValue (knot);
	}


	/*
	 * Eigensystems
	 */

	/**
	 * compute
	 *  characteristic polynomial for matrix
	 * @param values the parameters to the call
	 * @return the coefficients that define the polynomial
	 */
	public ValueManager.GenericValue characteristic (ValueManager.GenericValue values)
	{
		PrintStream out = environment.getOutStream ();
		Matrix<T> m = valueManager.toMatrix (values);
		Polynomial.PowerFunction<T> poly = eigenvaluesAndEigenvectors.computeCharacteristicPolynomialFor (m);

		out.println ();
		out.print ("Characteristic polynomial :    ");
		out.println (polynomialSpaceManager.toString (poly));
		out.println ();

		return valueManager.newDimensionedValue (poly.getCoefficients ());
	}

	/**
	 * compute Von Mises dominant eigen-pair
	 * @param values the parameters to the call
	 * @return the eigenvector value array
	 */
	public ValueManager.GenericValue eig (ValueManager.GenericValue values)
	{
		List<T> contents = new ArrayList<T> ();
		ValueManager.ValueList parameterList = (ValueManager.ValueList)values;
		List<ValueManager.GenericValue> plistValues = parameterList.getValues ();
		Matrix<T> m = valueManager.toMatrix (plistValues.get (0));
		ValueManager.GenericValue eVecSym = plistValues.get (1);
		String eVecSymName = eVecSym.getName ();

		int maxIterations = environment.intParameterWithDefault (2, parameterList, 25);
		int precisionScale = environment.intParameterWithDefault (3, parameterList, 6);

		Vector<T> eVector = new Vector<T> (spaceManager);
		T eValue = eigenvaluesAndEigenvectors.findDominantEigensystemMemberFor
			(m, eVector, maxIterations, precisionScale);
		eVector.addToList (contents);
		
		environment.setSymbol (eVecSymName, valueManager.newDimensionedValue (contents));
		PrintStream out = environment.getOutStream ();

		out.println ();
		out.println ("Von Mises dominant Eigen-Pair");
		out.println (" Dominant Eigenvalue is:  " + eValue);
		out.println ("Dominant Eigenvector is:  " + contents);
		out.println ();

		return valueManager.newDiscreteValue (eValue);
	}


}

