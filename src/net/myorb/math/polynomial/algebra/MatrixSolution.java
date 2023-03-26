
package net.myorb.math.polynomial.algebra;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.matrices.MatrixOperations;
import net.myorb.math.matrices.VectorAccess;
import net.myorb.math.matrices.Matrix;

import java.util.*;

/**
 * linear algebra solution for system of equations
 * @author Michael Druckman
 */
public class MatrixSolution <T> extends Utilities
{


	public MatrixSolution (Environment <T> environment)
	{
		this.environment = environment;
		this.manager = environment.getSpaceManager ();
		this.stream = environment.getOutStream ();
	}
	protected Environment <T> environment;
	protected ExpressionSpaceManager <T> manager;
	protected java.io.PrintStream stream;


	/**
	 * a set of equation to solve with linear algebra
	 */
	public static class SystemOfEquations extends ArrayList <Factor>
	{ private static final long serialVersionUID = 2065886325470713453L; }


	/**
	 * @param equations a System Of Equations to solve
	 * @return the computed solution vector
	 */
	public Matrix <T> solve (SystemOfEquations equations)
	{
		this.mapReferences (equations);
		stream.println (symbolOrder);

		int N = symbolOrder.size ();
		this.ops = new MatrixOperations<T> (manager);
		this.solutionMatrix = new Matrix <> (N, N, manager);
		this.solutionVector = new Matrix <> (N, 1, manager);
		this.zero (solutionVector.getColAccess (1));

		this.loadSolution (equations, N);
		ops.show (stream, solutionMatrix); stream.println ();
		ops.show (stream, solutionVector); stream.println ();

		stream.println ("Inverse of Solution");
		Matrix<T> SMI = ops.inv (solutionMatrix);
		ops.show (stream, SMI); stream.println ();

		stream.println ("Inverse product");
		Matrix<T> S = ops.product (SMI, solutionVector);
		ops.show (stream, S);
		return S;
	}
	protected Matrix <T> solutionMatrix, solutionVector;
	protected MatrixOperations <T> ops;


	/**
	 * build matrix and vector for the solution
	 * @param equations the equations being analyzed
	 * @param N the order of the polynomial
	 */
	public void loadSolution (SystemOfEquations equations, int N)
	{
		for (int i = 1; i <= N; i++)
		{
			T value = loadEquation
			(
				(Sum) equations.get ( i - 1 ),
				solutionMatrix.getRowAccess (i)
			);
			solutionVector.set (i, 1, value);
		}
	}


	/**
	 * translate an equation to a matrix row
	 * @param equation the equation being analyzed
	 * @param vector the row vector of the matrix for this equation
	 * @return the constant value to use in the solution vector
	 */
	public T loadEquation (Sum equation, VectorAccess <T> vector)
	{
		Integer column;
		this.zero (vector);
		T value = manager.getZero ();

		for (Factor factor : equation)
		{
			T scalar = scalarFor (factor);

			if ( ( column = columnFor (factor) ) == null )
			{
				T offset = manager.negate (scalar);
				value = manager.add (value, offset);
			}
			else
			{
				if (column < 1 || column > vector.size ())
				{
					System.out.println ("Factor in error: " + factor);
					continue;
				}
				T cell = manager.add (vector.get (column), scalar);
				vector.set (column, cell);
			}
		}

		return value;
	}


	/**
	 * determine the matrix column referenced by a factor
	 * @param factor the factor taken from an equation product
	 * @return the index for recognized symbol or null for a constant
	 */
	Integer columnFor (Factor factor)
	{
		SymbolicReferences symbolFound;
		factor.identify (symbolFound = new SymbolicReferences ());
		return indexOf (symbolFound);
	}


	/**
	 * determine treatment for a symbol set
	 * @param symbolFound the set of references found
	 * @return the column number or null for a constant
	 */
	Integer indexOf (SymbolicReferences symbolFound)
	{
		if (symbolFound.isEmpty ()) return null;
		if (symbolFound.size () > 1) throw new RuntimeException ("Term not reduced");
		String symbol = symbolFound.toArray (EMPTY) [0];
		return symbolIndex.get (symbol);
	}
	protected static final String [] EMPTY = new String [] {};


	/**
	 * get the scalar for a factor
	 * @param factor the factor to read
	 * @return the scalar as a managed value
	 */
	T scalarFor (Factor factor)
	{
		double value = 1.0;
		if (factor instanceof Constant)
		{
			value = Constant.getValueFrom (factor);
		}
		else if (factor instanceof Product)
		{
			for (Factor item : (Product) factor)
			{
				if (item instanceof Constant)
				{
					value = Constant.getValueFrom (item);
				}
			}
		}
		return manager.convertFromDouble (value);
	}


	/**
	 * @param vector the vector to set to all-zero
	 */
	public void zero (VectorAccess <T> vector)
	{
		for (int i = 1; i <= vector.size (); i++) vector.set (i, manager.getZero ());
	}


	/**
	 * cross reference symbols to matrix columns
	 * @param equations the equations being analyzed
	 */
	void mapReferences (SystemOfEquations equations)
	{
		int N = 1;
		SymbolicReferences symbolsSeen = new SymbolicReferences ();
		for (Factor factor : equations) factor.identify (symbolsSeen);

		symbolOrder.addAll (symbolsSeen);
		symbolOrder.sort (null);

		for (String symbol : symbolOrder)
		{
			symbolIndex.put (symbol, N++);
		}
	}
	protected Map <String, Integer> symbolIndex = new HashMap <> ();
	protected List <String> symbolOrder = new ArrayList <> ();


}

