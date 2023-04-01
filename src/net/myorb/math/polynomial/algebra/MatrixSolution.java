
package net.myorb.math.polynomial.algebra;

import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.linalg.SolutionApplication;
import net.myorb.math.linalg.SolutionPrimitives;

import net.myorb.math.matrices.MatrixOperations;
import net.myorb.math.matrices.VectorAccess;
import net.myorb.math.matrices.Matrix;

import java.util.*;

/**
 * linear algebra solution for system of equations
 * @author Michael Druckman
 */
public class MatrixSolution <T> extends SolutionData
{


	public MatrixSolution
	(ExpressionSpaceManager <T> manager, java.io.PrintStream stream)
	{
		this.manager = manager; this.stream = stream;
		this.solutionApplication = new SolutionApplication <T> (manager);
		this.ops = new MatrixOperations <T> (manager);
	}
	protected ExpressionSpaceManager <T> manager;
	protected java.io.PrintStream stream;
	protected MatrixOperations <T> ops;


	/**
	 * solve system of equations
	 * @param equations a System Of Equations to solve
	 * @param symbolTable set of symbols used in the solution
	 * @return computed solution
	 */
	public Matrix <T> solve
		(SystemOfEquations equations, SymbolValues symbolTable)
	{
		int N = this.mapReferences (equations);
		this.prepareSolution (N); this.loadSolution (equations, N);
		this.computedSolution = solutionApplication.decompositionSolution
				(solutionMatrix, solutionVector);
		this.postSymbols (symbolTable);
		return computedSolution;
	}


	/**
	 * @param N order of the solution matrix
	 */
	public void prepareSolution (int N)
	{
		this.solutionMatrix = new Matrix <> (N, N, manager);
		this.solutionVector = new Matrix <> (N, 1, manager);
	}
	protected Matrix <T> solutionMatrix, solutionVector, computedSolution;


	/**
	 * identify solution algorithm to be used
	 * @param primitives implementation of SolutionPrimitives
	 */
	public void setPrimitives (SolutionPrimitives <T> primitives)
	{ this.solutionApplication.setPrimitives (primitives); }
	protected SolutionApplication <T> solutionApplication;


	/**
	 * build matrix and vector for the solution
	 * @param equations the equations being analyzed
	 * @param N the order of the polynomial
	 */
	public void loadSolution (SystemOfEquations equations, int N)
	{
		if ( N > equations.size () )
		{ throw new RuntimeException ("Insufficient criteria for solution"); }
		solutionVector.getColAccess (1).fill ( manager.getZero () );
		
		for (int i = 1; i <= N; i++)
		{
			T value = loadEquation
			(
				(Sum) equations.get ( i - 1 ),
				solutionMatrix.getRowAccess (i)
			);
			solutionVector.set (i, 1, value);
		}

		stream.println ("===");
		ops.show (stream, solutionMatrix); stream.println ("===");
		ops.show (stream, solutionVector); stream.println ("==="); stream.println ();
	}


	/**
	 * translate an equation to a matrix row
	 * @param equation the equation being analyzed
	 * @param vector the row vector of the matrix for this equation
	 * @return the constant value to use in the solution vector
	 */
	public T loadEquation (Sum equation, VectorAccess <T> vector)
	{
		Integer column; T value;
		vector.fill (value = manager.getZero ());

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
					stream.println ("Factor in error: " + factor);
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
		return symbolIndex.get (symbolFound.getReferencedSymbol ());
	}


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
	 * cross reference symbols to matrix columns
	 * @param equations the equations being analyzed
	 * @return the count of symbols
	 */
	int mapReferences (SystemOfEquations equations)
	{
		int N = 1;
		SymbolicReferences symbolsSeen = new SymbolicReferences ();
		for (Factor factor : equations) factor.identify (symbolsSeen);
		symbolOrder.addAll (symbolsSeen); symbolOrder.sort (null);

		for (String symbol : symbolOrder)
		{ symbolIndex.put (symbol, N++); }
		stream.println (symbolOrder);
		return symbolOrder.size ();
	}
	protected Map <String, Integer> symbolIndex = new HashMap <> ();
	protected List <String> symbolOrder = new ArrayList <> ();


	/**
	 * update symbol table with computed values
	 * @param symbolTable the collection of symbols
	 */
	public void postSymbols (SymbolValues symbolTable)
	{
		for (int i = 1; i <= computedSolution.rowCount (); i++)
		{
			T computedValue = computedSolution.get (i, 1);
			Constant value = new Constant (manager.convertToDouble (computedValue));
			symbolTable.add (symbolOrder.get (i - 1), value);
		}
	}


}

