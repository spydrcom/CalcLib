
package net.myorb.math.polynomial.algebra;

import net.myorb.math.computational.ArithmeticFundamentals;
import net.myorb.math.computational.ArithmeticFundamentals.Scalar;
import net.myorb.math.computational.ArithmeticFundamentals.Conversions;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.linalg.SolutionApplication;
import net.myorb.math.linalg.SolutionPrimitives;

import net.myorb.math.matrices.MatrixOperations;
import net.myorb.math.matrices.VectorAccess;
import net.myorb.math.matrices.Matrix;

/**
 * linear algebra solution for system of equations
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class MatrixSolution <T> extends SolutionData
{


	// allocation of dimensioned objects

	public Matrix <T> MAT (int N) { return new Matrix <> (N, N, manager); }
	public Matrix <T> VEC (int N) { return new Matrix <> (N, 1, manager); }


	/**
	 * map symbols to ordered index within solution
	 */
	static class SymbolIndexMap extends SymbolicMap <Integer>
	{ private static final long serialVersionUID = 1113974645827170797L; }


	/**
	 * the compiled solution matrix used for resolving the system of equations
	 */
	static class WorkProduct <V> extends ValueManager.TableOfValues <V>
	{ private static final long serialVersionUID = 2356189952454085804L; }


	public MatrixSolution
	(ExpressionSpaceManager <T> manager, java.io.PrintStream stream)
	{
		this.manager = manager; this.stream = stream;
		this.converter = ArithmeticFundamentals.getConverter (manager);
		this.solutionApplication = new SolutionApplication <T> (manager);
		this.ops = new MatrixOperations <T> (manager);
	}
	protected Conversions <T> converter;
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
		return computedColumnVectorSolution
		(
			equations,
			new SymbolList (),			// symbols assigned to matrix columns
			new SymbolIndexMap (),		// reference back to matrix column index
			symbolTable					// symbol value assignments
		);
	}
	private Matrix <T> computedColumnVectorSolution
		(
			SystemOfEquations equations,
			SymbolList symbolOrder, SymbolIndexMap indices,
			SymbolValues symbolTable
		)
	{
		int N = this.mapReferences
		( equations, symbolOrder, indices );

		this.setColumnList (symbolOrder);

		return this.postSymbols
		(
			symbolOrder, symbolTable,
			solve (equations, indices, N)
		);
	}


	/**
	 * solve system of equations
	 * @param equations a System Of Equations to solve
	 * @param indices index map built for solution symbols
	 * @param N determined count of symbols from reference map
	 * @return the computed solution from decomposition solution algorithm
	 */
	public Matrix <T> solve (SystemOfEquations equations, SymbolIndexMap indices, int N)
	{
		Matrix <T> solutionMatrix = MAT (N); Matrix <T> solutionVector = VEC (N);
		this.loadSolution ( equations, solutionMatrix, solutionVector, indices, N );
		Matrix <T> computedSolution = this.solutionFor ( solutionMatrix, solutionVector );
		this.buildAugmentedMatrix ( solutionMatrix, solutionVector );
		return computedSolution;
	}


	// key algorithms for computation of the solution


	/**
	 * identify solution algorithm to be used
	 * @param primitives implementation of SolutionPrimitives
	 */
	public void setPrimitives (SolutionPrimitives <T> primitives)
	{ this.solutionApplication.setPrimitives (primitives); }
	protected SolutionApplication <T> solutionApplication;

	private Matrix <T> solutionFor (Matrix <T> solutionMatrix, Matrix <T> solutionVector)
	{ return solutionApplication.decompositionSolution ( solutionMatrix, solutionVector ); }


	/**
	 * construct matrix which represents solution source
	 * @param solutionMatrix the matrix describing the system of equations
	 * @param solutionVector the solution column vector
	 */
	public void buildAugmentedMatrix
		( Matrix <T> solutionMatrix, Matrix <T> solutionVector )
	{
		ItemList <T> row = new ItemList <> ();
		this.augmentedMatrix = new WorkProduct <> ();

		ValueManager <T> valueManager = new ValueManager <> ();

		for (int r = 1; r <= solutionMatrix.rowCount (); r++)
		{
			getColumnElements ( row, solutionMatrix, r );
			augmentedMatrix.add ( valueManager.newDimensionedValue (row) );
		}

		augmentedMatrix.add (valueManager.newDimensionedValue (getColumn (solutionVector, 1)));
	}


	/**
	 * make the work-product available after use
	 * @return the augmented matrix built for the solution
	 */
	public WorkProduct <T> getAugmentedMatrix () { return augmentedMatrix; }
	protected WorkProduct <T> augmentedMatrix;

	/**
	 * @param into the list object to hold the column values
	 * @param from the matrix holding the column of interest
	 * @param columnNumber the index of the column
	 */
	private void getColumnElements (ItemList <T> into, Matrix <T> from, int columnNumber)
	{ into.clear (); into.addAll ( getColumn (from, columnNumber) ); }

	/**
	 * @param from source matrix holding the column of interest
	 * @param columnNumber the index of the column
	 * @return the list of values
	 */
	private ItemList <T> getColumn (Matrix <T> from, int columnNumber)
	{ return new ItemList <T> (from.getCol (columnNumber).getElementsList ()); }

	/**
	 * @return the list of symbols in the solution matrix
	 */
	public SymbolList getColumnList () { return columnList; }
	public void setColumnList (SymbolList columnList) { this.columnList = columnList; }
	protected SymbolList columnList;


	/**
	 * build matrix and vector for the solution
	 * @param equations the equations being analyzed
	 * @param solutionMatrix the matrix of collected coefficient scalars
	 * @param solutionVector the collection of constant terms of equations
	 * @param indices the index map built for all solution symbols
	 * @param N the order of the polynomial
	 */
	public void loadSolution
		(
			SystemOfEquations equations,
			Matrix <T> solutionMatrix, Matrix <T> solutionVector,
			SymbolIndexMap indices, int N
		)
	{
		if ( N > equations.size () )
		{ throw new RuntimeException ("Insufficient criteria for solution"); }
		solutionVector.getColAccess (1).fill ( manager.getZero () );

		for (int i = 1; i <= N; i++)
		{
			T value = loadEquation
			(
				(Sum) equations.get ( i - 1 ),
				solutionMatrix.getRowAccess (i),
				indices
			);
			solutionVector.set (i, 1, value);
		}

		if (showWorkProduct)			// for display to system output
		{
			stream.println ("===");
			ops.show (stream, solutionMatrix); stream.println ("===");
			ops.show (stream, solutionVector); stream.println ("==="); stream.println ();
		}
	}
	protected boolean showWorkProduct = false;


	/**
	 * translate an equation to a matrix row
	 * @param equation the equation being analyzed
	 * @param vector the row vector of the matrix for this equation
	 * @param indices the index map built for all solution symbols
	 * @return the constant value to use in the solution vector
	 */
	public T loadEquation
	(Sum equation, VectorAccess <T> vector, SymbolIndexMap indices)
	{
		Integer column; T value;
		vector.fill (value = manager.getZero ());

		for (Factor factor : equation)
		{
			T scalar = scalarFor (factor);

			if ( ( column = columnFor (factor, indices) ) == null )
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
	 * @param indices the index map built for all solution symbols
	 * @return the index for recognized symbol or null for a constant
	 */
	Integer columnFor (Factor factor, SymbolIndexMap indices)
	{
		SymbolicReferences symbolFound;
		factor.identify ( symbolFound = new SymbolicReferences () );
		return indices.get ( symbolFound.getReferencedSymbol () );
	}


	/**
	 * get the scalar for a factor
	 * @param factor the factor to read
	 * @return the scalar as a managed value
	 */
	T scalarFor (Factor factor)
	{
		Scalar value = converter.getOne ();
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
		return this.converter.convertedFrom (value);
	}


	/**
	 * cross reference symbols to matrix columns
	 * @param equations the equations being analyzed
	 * @param symbolOrder list collecting ordered list of references
	 * @param indices index map to build for symbols
	 * @return the count of symbols
	 */
	int mapReferences
	(SystemOfEquations equations, SymbolList symbolOrder, SymbolIndexMap indices)
	{
		this.mapEquationReferences (equations, symbolOrder);
		int N = 0; for (String S : symbolOrder) indices.put (S, ++N);
		return N;
	}


	/**
	 * identify equation symbol references
	 * @param equations the equations being analyzed
	 * @param symbolOrder resulting ordered list of references
	 */
	void mapEquationReferences
		(SystemOfEquations equations, SymbolList symbolOrder)
	{
		SymbolicReferences symbolsSeen = new SymbolicReferences ();
		for (Factor factor : equations) factor.identify (symbolsSeen);
		symbolOrder.addAll (symbolsSeen); symbolOrder.sort (null);
	}


	/**
	 * update symbol table with computed values
	 * @param symbolOrder ordered list of references
	 * @param symbolTable the collection of symbols in the solution
	 * @param computedSolution matrix holding solution
	 * @return computedSolution matrix
	 */
	public Matrix <T> postSymbols
		(
			SymbolList symbolOrder,
			SymbolValues symbolTable,
			Matrix <T> computedSolution
		)
	{
		for (int i = 1; i <= computedSolution.rowCount (); i++)
		{
			symbolTable.add
			(
				symbolOrder.get ( i - 1 ),

				new Constant
				(
					this.converter,

					this.converter.toScalar
					(
						computedSolution.get (i, 1)
					)
				)
			);
		}
		return computedSolution;
	}


}

