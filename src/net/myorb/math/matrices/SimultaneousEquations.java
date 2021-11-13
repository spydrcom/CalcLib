
package net.myorb.math.matrices;

import net.myorb.math.SpaceManager;

/**
 * methods for finding solutions to simultaneous equations
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class SimultaneousEquations<T> extends MatrixOperations<T>
{


	/**
	 * description of a matrix appended with an additional row
	 * @param <T> type of component values on which operations are to be executed
	 */
	public interface AugmentedMatrix<T> extends MatrixAccess<T>
	{
		/**
		 * multiply elements of a row by a scaling factor
		 * @param rowNumber the row number to be updated
		 * @param scaleFactor the factor to use
		 */
		public void scaleRow (int rowNumber, T scaleFactor);

		/**
		 * update a row with a sum of rows and a scaling factor
		 * @param scaleFactor the factor to use to scale the source row
		 * @param sourceRow the row to use as a source to be scaled
		 * @param intoRow the row number to be updated
		 */
		public void scaleAndAddToRow (T scaleFactor, int sourceRow, int intoRow);

		/**
		 * exchange two rows of the matrix
		 * @param row1 the first of the row numbers
		 * @param row2 the second of the rows
		 */
		public void swapRows (int row1, int row2);
	}


	/**
	 * values are constructed based on type manager for components
	 * @param manager the manager for the component type
	 */
	public SimultaneousEquations
		(SpaceManager<T> manager)
	{
		super (manager);
	}


	/**
	 * find solution for Ax = b
	 *  using column substitution and determinant ratios
	 * @param A matrix of linear equations coefficients
	 * @param b vector of equation result constants
	 * @return solution vector
	 */
	public Vector<T> solve (MatrixAccess<T> A, VectorAccess<T> b)
	{
		T detA = det (A);
		if (isZro (detA)) raiseException ("Singular matrix with zero determinant has infinite solutions");
		Vector<T> result = new Vector<T> (A.columnCount (), manager);
		T detAinverted = inverted (detA);

		for (int c = 1; c <= A.columnCount (); c++)
		{ result.set (c, X (det (copyAndSubstitute (A, b, c)), detAinverted)); }
		return result;
	}


	/**
	 * use matrix inversion product to find solution
	 * @param A matrix of linear equations coefficients
	 * @param b vector of equation result constants
	 * @return solution vector
	 */
	public Vector<T> inverseSolution (MatrixAccess<T> A, VectorAccess<T> b)
	{
		return product (inv (A), columnMatrix (b)).getCol (1);
	}


	/**
	 * use SVD methods to find solution to homogeneous simultaneous equations
	 * @param m the singular matrix describing the homogeneous simultaneous equations
	 * @return one of the vectors in the space of solutions
	 */
	public Vector<T> findHomogeneousSolution (Matrix<T> m)
	{
		return new SingularValueDecomposition<T> ().findHomogeneousSolution (m);
	}


	/**
	 * append a matrix with additional column
	 * @param m the original matrix to be copied
	 * @param column the vector of elements to append as a column
	 * @return the constructed augmented matrix
	 */
	public AugmentedMatrix<T> buildAugmentedMatrix (MatrixAccess<T> m, VectorAccess<T> column)
	{
		int columns = m.columnCount () + 1;
		AugmentedMatrixWrapper<T> result = new AugmentedMatrixWrapper<T> (m, this, manager);
		copyColumns (m, result); setCol (columns, result, column);
		return result;
	}


	/**
	 * prepare 'scale and add' operation
	 *  in attempt to zero a specific element
	 * @param a AugmentedMatrix being modified for solution
	 * @param row the row coordinate of the position being zeroed
	 * @param col the column coordinate of the position being zeroed
	 * @param using the row being used to alter specified coordinate
	 */
	public void adjustRowCol (AugmentedMatrix<T> a, int row, int col, int using)
	{
		T scaleFactor = a.get (row, col);													// a[row,col] adjust to become 0
		if (manager.isZero (scaleFactor)) return;
		//System.out.println ("*** row scale " + row + " factor " + scaleFactor);
		a.scaleAndAddToRow (manager.negate (scaleFactor), using, row);
	}


	/**
	 * zero out columns at end of row
	 * @param a the AugmentedMatrix being modified for solution
	 * @param row the row being adjusted
	 * @param n the column count
	 */
	public void adjustRow (AugmentedMatrix<T> a, int row, int n)
	{ for (int c = n; c > row; c--) adjustRowCol (a, row, c, c); }


	/**
	 * find a non-zero element in the column 
	 * @param a the AugmentedMatrix being modified for solution
	 * @param column the column currently being adjusted
	 * @return the row number of a non-zero element
	 */
	public int findNonZeroElementInColumn (AugmentedMatrix<T> a, int column)
	{
		for (int row = column; row <= a.rowCount (); row++)
		{ if (!manager.isZero (a.get (row, column))) return row; }
		String message = "column " + column + " degenerated to zero";
		throw new RuntimeException ("Solution not available, " + message);
	}


	/**
	 * adjust column to 1,0,0,...
	 * @param a the AugmentedMatrix being modified for solution
	 * @param column the column being adjusted
	 */
	public void adjustColumn (AugmentedMatrix<T> a, int column)
	{
		int row = findNonZeroElementInColumn (a, column);
		if (row != column) a.swapRows (row, column);										// force a[column,column] non-zero

		T diagonalElement = a.get (row = column, column);									// a[column,column] must scale to become 1
		T scaleFactor = manager.invert (diagonalElement);
		a.scaleRow (row, scaleFactor);														// multiply row=column by 1/a[column,column]

		for (row = column + 1; row <= a.rowCount (); row++)									// zero remainder of column
		{ adjustRowCol (a, row, column, column); }
	}


	/**
	 * use Gaussian Elimination
	 *  to solve system of equations described by matrix
	 * @param m the matrix describing a system of equations to be solved
	 * @param column a vector of values containing the constants of the equation system to be solved
	 * @return the column vector containing the solution of the system
	 */
	public VectorAccess<T> applyGaussianElimination (MatrixAccess<T> m, VectorAccess<T> column)
	{
		int columns = m.columnCount ();
		AugmentedMatrix<T> a = buildAugmentedMatrix (m, column);
		for (int col = 1; col <= columns; col++) adjustColumn (a, col);
		for (int row = columns - 1; row > 0; row--) adjustRow (a, row, columns);
		return a.getColAccess (a.columnCount ());
	}


}


/**
 * an extended matrix with extra operations for use in row elimination
 * @param <T> type of component values on which operations are to be executed
 */
class AugmentedMatrixWrapper<T> extends Matrix<T> implements SimultaneousEquations.AugmentedMatrix<T>
{

	/**
	 * describe an augmented matrix
	 * @param m the source matrix to be copied
	 * @param ops a matrix operations object to provide update methods
	 * @param manager a manager for the element type
	 */
	public AugmentedMatrixWrapper (MatrixAccess<T> m, MatrixOperations<T> ops, SpaceManager<T> manager)
	{
		super (m.rowCount (), m.columnCount () + 1, manager); this.ops = ops;
	}
	MatrixOperations<T> ops;

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.SimultaneousEquations.AugmentedMatrix#scaleAndAddToRow(java.lang.Object, int, int)
	 */
	@SuppressWarnings("unchecked")
	public void scaleAndAddToRow (T scaleFactor, int sourceRow, int intoRow)
	{
		Vector<T> row = getRow (intoRow);
		Vector<T> source = getRow (sourceRow).times (scaleFactor);
		ops.setRow (intoRow, this, ops.vectorOperations.sum (row, source));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.SimultaneousEquations.AugmentedMatrix#scaleRow(int, java.lang.Object)
	 */
	public void scaleRow (int rowNumber, T scaleFactor)
	{
		Vector<T> row = getRow (rowNumber);
		ops.setRow (rowNumber, this, row.times (scaleFactor));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.SimultaneousEquations.AugmentedMatrix#swapRows(int, int)
	 */
	public void swapRows (int row1, int row2)
	{
		Vector<T> r1 = getRow (row1), r2 = getRow (row2);
		ops.setRow (row2, this, r1); ops.setRow (row1, this, r2);
	}

}


/**
 *
	In linear algebra, the singular value decomposition (SVD) is a factorization of a real or complex matrix. 
	It has many useful applications in signal processing and statistics.

	Formally, the singular value decomposition of an m × n real or complex matrix M is a factorization of the form M = USV*, 
	where U is an m × m real or complex unitary matrix, S is an m * n rectangular diagonal matrix with non-negative real numbers on the diagonal, 
	and V* (the conjugate transpose of V, or simply the transpose of V if V is real) is an n × n real or complex unitary matrix. 
	The diagonal entries S(i,i) of S are known as the singular values of M. 
	
	The m columns of U and the n columns of V are called the left-singular vectors and right-singular vectors of M, respectively.
 *
 * @param <T> type of component values on which operations are to be executed
 */
class SingularValueDecomposition<T>
{

	/*
	 * 
		Calculating the SVD / Numerical approach
		========================================
	 *
		The SVD of a matrix M is typically computed by a two-step procedure. 
		In the first step, the matrix is reduced to a bidiagonal matrix. 

		This takes O(mn2) floating-point operations (flops), assuming that m >= n. 
		The second step is to compute the SVD of the bidiagonal matrix. 

		This step can only be done with an iterative method (as with eigenvalue algorithms). 
		However, in practice it suffices to compute the SVD up to a certain precision, like the machine epsilon. 
		If this precision is considered constant, then the second step takes O(n) iterations, each costing O(n) flops. 
		Thus, the first step is more expensive, and the overall cost is O(mn2) flops (Trefethen & Bau III 1997, Lecture 31).
		
		The first step can be done using Householder reflections for a cost of 4mn2 - 4n3/3 flops, 
		assuming that only the singular values are needed and not the singular vectors. 

		If m is much larger than n then it is advantageous to first reduce the matrix M to a triangular matrix 
		with the QR decomposition and then use Householder reflections to further reduce the matrix to bidiagonal form; 
		the combined cost is 2mn2 + 2n3 flops (Trefethen & Bau III 1997, Lecture 31).
		
		The second step can be done by a variant of the QR algorithm for the computation of eigenvalues, 
		which was first described by Golub & Kahan (1965). The LAPACK subroutine DBDSQR[14] implements this iterative method, 
		with some modifications to cover the case where the singular values are very small (Demmel & Kahan 1990). 
		Together with a first step using Householder reflections and, if appropriate, QR decomposition, 
		this forms the DGESVD[15] routine for the computation of the singular value decomposition.
		
		The same algorithm is implemented in the GNU Scientific Library (GSL). 
		The GSL also offers an alternative method, which uses a one-sided Jacobi orthogonalization in step 2 (GSL Team 2007). 

		This method computes the SVD of the bidiagonal matrix by solving a sequence of 2 * 2 SVD problems, 
		similar to how the Jacobi eigenvalue algorithm solves a sequence of 2 * 2 eigenvalue methods (Golub & Van Loan 1996, §8.6.3). 
		Yet another method for step 2 uses the idea of divide-and-conquer eigenvalue algorithms (Trefethen & Bau III 1997, Lecture 31).
		
		There is an alternative way which is not explicitly using the eigenvalue decomposition	
	 *
	 */

	public Vector<T> findHomogeneousSolution (Matrix<T> m)
	{
		householderReflections (); qRalgorithm ();
		throw new RuntimeException ("SVD methods not implemented");
	}

	/*
	 * 
		In linear algebra, a Householder transformation (also known as Householder reflection or elementary reflector) 
		is a linear transformation that describes a reflection about a plane or hyperplane containing the origin. 
		Householder transformations are widely used in numerical linear algebra, to perform QR decompositions 
		and in the first step of the QR algorithm. The Householder transformation was introduced in 1958 
		by Alston Scott Householder.[1]

		Its analogue over general inner product spaces is the Householder operator.
	 * 
	 */

	public void householderReflections () {}

	/*
	 * 
		In numerical linear algebra, the QR algorithm is an eigenvalue algorithm: 
		that is, a procedure to calculate the eigenvalues and eigenvectors of a matrix. 

		The QR transformation was developed in the late 1950s by John G.F. Francis (England) 
		and by Vera N. Kublanovskaya (USSR), working independently.[1] 

		The basic idea is to perform a QR decomposition, writing the matrix as a product of an 
		orthogonal matrix and an upper triangular matrix, multiply the factors in the reverse order, and iterate.	 
	  *
	  */

	public void qRalgorithm () {}

}
