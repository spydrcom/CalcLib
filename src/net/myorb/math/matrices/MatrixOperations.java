
package net.myorb.math.matrices;

import net.myorb.math.Polynomial;
import net.myorb.math.ListOperations;
import net.myorb.math.SpaceManager;

import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;

/**
 * operations available on matrix objects
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class MatrixOperations<T> extends ListOperations<T>
{

	/**
	 * values are constructed based on type manager for components
	 * @param manager the manager for the component type
	 */
	public MatrixOperations
		(SpaceManager<T> manager)
	{
		super (manager);
		this.vectorOperations = new VectorOperations<T> (manager);
		this.triangular = new Triangular<T>(manager);
	}

	public VectorOperations<T> getVectorOperations () { return vectorOperations; }
	public Triangular<T> getTriangularOperations () { return triangular; }
	protected VectorOperations<T> vectorOperations;
	protected Triangular<T> triangular;

	/**
	 * set a row of a matrix
	 * @param rowNumber the row number to be changed
	 * @param m the matrix object being updated
	 * @param row a vector of values to insert
	 */
	public void setRow (int rowNumber, MatrixAccess<T> m, VectorAccess<T> row)
	{
		for (int col = 1; col <= row.size (); col++)
		{
			m.set (rowNumber, col, row.get (col));
		}
	}

	/**
	 * set a column of a matrix
	 * @param colNumber the column number to be changed
	 * @param m the matrix object being updated
	 * @param col a vector of values to insert
	 */
	public void setCol (int colNumber, MatrixAccess<T> m, VectorAccess<T> col)
	{
		for (int row = 1; row <= col.size (); row++)
		{
			m.set (row, colNumber, col.get (row));
		}
	}

	/**
	 * copy columns from source to destination
	 * @param source matrix to take columns from
	 * @param destination matrix to update
	 */
	public void copyColumns (MatrixAccess<T> source, MatrixAccess<T> destination)
	{
		for (int c = 1; c <= source.columnCount (); c++) setCol (c, destination, source.getColAccess (c));
	}

	/**
	 * make a copy of a matrix
	 * @param m the matrix to be copied
	 * @return the duplicate matrix
	 */
	public Matrix<T> copy (MatrixAccess<T> m)
	{
		Matrix<T> result = new Matrix<T> (m.rowCount (), m.columnCount (), manager);
		copyColumns (m, result);
		return result;
	}

	/**
	 * copy specified matrix and substitute specified column
	 * @param m original matrix to be copied as starting point before substitution
	 * @param col vector of items to be placed in specified column
	 * @param columnNumber number of column to be updated
	 * @return resulting matrix
	 */
	public Matrix<T> copyAndSubstitute (MatrixAccess<T> m, VectorAccess<T> col, int columnNumber)
	{
		Matrix<T> result = copy (m);
		setCol (columnNumber, result, col);
		return result;
	}

	/**
	 * construct 1xN matrix from a vector
	 * @param rowVector the vector of values to use
	 * @return new row matrix
	 */
	public Matrix<T> rowMatrix (VectorAccess<T> rowVector)
	{
		Matrix<T> matrix = new Matrix<T> (1, rowVector.size (), manager);
		setRow (1, matrix, rowVector);
		return matrix;
	}

	/**
	 * construct 1xN matrix from a list
	 * @param list the list of values to use
	 * @return new row matrix
	 */
	public Matrix<T> rowMatrix (List<T> list)
	{
		int size = list.size ();
		Matrix<T> matrix = new Matrix<T> (1, size, manager);
		for (int c = 1; c <= size; c++) matrix.set (1, c, list.get (c-1));
		return matrix;
	}

	/**
	 * construct Nx1 matrix from a vector
	 * @param columnVector the vector of values to use
	 * @return new column matrix
	 */
	public Matrix<T> columnMatrix (VectorAccess<T> columnVector)
	{
		Matrix<T> matrix = new Matrix<T> (columnVector.size (), 1, manager);
		setCol (1, matrix, columnVector);
		return matrix;
	}

	/**
	 * construct Nx1 matrix from a list
	 * @param list the list of values to use
	 * @return new column matrix
	 */
	public Matrix<T> columnMatrix (List<T> list)
	{
		int size = list.size ();
		Matrix<T> matrix = new Matrix<T> (size, 1, manager);
		for (int r = 1; r <= size; r++) matrix.set (r, 1, list.get (r-1));
		return matrix;
	}

	/**
	 * construct identity matrix of given size
	 * @param size square matrix of sizeXsize
	 * @return identity matrix
	 */
	public Matrix<T> identity (int size)
	{
		T one = discrete (1);
		Matrix<T> result = new Matrix<T> (size, size, manager);
		for (int i = 1; i <= size; i++) result.set (i, i, one);
		return result;
	}

	/**
	 * set items on diagonal
	 * @param m matrix to be modified
	 * @param r starting row number for copy
	 * @param c starting column number
	 * @param items list of elements
	 */
	public void set
	(MatrixAccess<T> m, int r, int c, List<T> items)
	{
		int i = 1, n = items.size (),
			rows = m.rowCount (), cols = m.columnCount ();
		while (r <= rows && c <= cols && i <= n)
		{ m.set (r++, c++, items.get (i++)); }
	}

	/**
	 * get items on diagonal
	 * @param m matrix to use as source
	 * @param r starting row number for copy
	 * @param c starting column number
	 * @return list of elements
	 */
	public List<T> get (MatrixAccess<T> m, int r, int c)
	{
		List<T> array = new ArrayList<T>();
		int rows = m.rowCount (), cols = m.columnCount ();
		while (r <= rows && c <= cols) array.add (m.get (r++, c++));
		return array;
	}

	/**
	 * copy elements of a diagonal
	 * @param m the source matrix to be read
	 * @param number number of diagonal, 0 = center, + = upper, - = lower
	 * @return vector of diagonal elements
	 */
	public Vector<T> diag (MatrixAccess<T> m, int number)
	{
		int r = 1, c = 1;
		if (number < 0) r -= number; else c += number;
		Vector<T> v = new Vector<T>(manager);
		v.load (get (m, r, c));
		return v;
	}

	/**
	 * set elements of a diagonal
	 * @param m the matrix to be updated
	 * @param number number of diagonal, 0 = center, + = upper, - = lower
	 * @param v vector of elements to set diagonal
	 */
	public void setDiag (MatrixAccess<T> m, int number, Vector<T> v)
	{
		int r = 1, c = 1, i = 1, n = v.size (),
			rows = m.rowCount (), cols = m.columnCount ();
		if (number < 0) r -= number; else c += number;
		while (r <= rows && c <= cols && i <= n)
		{ m.set (r++, c++, v.get (i++)); }
	}

	/**
	 * copy diagnoal from matrix to matrix
	 * @param number number of diagonal, 0 = center, + = upper, - = lower
	 * @param from source matrix
	 * @param to destination 
	 */
	public void copyDiag (int number, MatrixAccess<T> from, MatrixAccess<T> to)
	{
		int r = 1, c = 1;
		if (number < 0) r -= number; else c += number;
		int	trows = to.rowCount (), tcols = to.columnCount (),
			frows = from.rowCount (), fcols = from.columnCount ();
		while (r <= frows && c <= fcols && r <= trows && c <= tcols)
		{ to.set (r, c, from.get (r, c)); r++; c++; }
	}

	/**
	 * copy lower triangular matrix
	 * @param m the source matrix to copy from
	 * @param offset the diagonal offset from standard center 0
	 * @return the copied matrix object
	 */
	public Matrix<T> tril (MatrixAccess<T> m, int offset)
	{
		int r = m.rowCount (),
			c = m.columnCount (), n = r > c? -r: -c;
		Matrix<T> tri = new Matrix<T>(r, c, manager);
		for (int j = offset; j > n; j--) copyDiag (j, m, tri);
		return tri;
	}

	/**
	 * copy upper triangular matrix
	 * @param m the source matrix to copy from
	 * @param offset the diagonal offset from standard center 0
	 * @return the copied matrix object
	 */
	public Matrix<T> triu (MatrixAccess<T> m, int offset)
	{
		int r = m.rowCount (),
			c = m.columnCount (), n = r > c? r: c;
		Matrix<T> tri = new Matrix<T>(r, c, manager);
		for (int j = offset; j < n; j++) copyDiag (j, m, tri);
		return tri;
	}

	/**
	 * trace is sum of diagonal elements
	 * @param m the matrix being traced
	 * @return computed result
	 */
	@SuppressWarnings("unchecked")
	public T tr (MatrixAccess<T> m)
	{
		T result = discrete (0);
		int rows = m.rowCount (), cols = m.columnCount ();
		if (rows != cols) raiseException ("Trace not valid for non-square matrices");
		for (int rc = 1; rc <= rows; rc++) result = sumOf (result, m.get (rc, rc));
		return result;
	}

	/**
	 * construct transposed version of matrix
	 * @param m the matrix being transposed
	 * @return transposed version
	 */
	public Matrix<T> transpose (MatrixAccess<T> m)
	{
		int rows = m.rowCount (), cols = m.columnCount ();
		Matrix<T> result = new Matrix<T> (cols, rows, manager);
		VectorAccess<T> row = m.getRowAccess (1);

		for (int r = 1; r <= rows; r++)
		{
			for (int c = 1; c <= cols; c++)
			{ result.set (c, r, row.get (c)); }
			row.nextSpan ();
		}
		return result;
	}

	/**
	 * construct minor matrix
	 * @param m the source matrix to use as source
	 * @param absentRow the row to be deleted
	 * @param absentColumn column to delete
	 * @return resulting matrix
	 */
	public Matrix<T> minor (MatrixAccess<T> m, int absentRow, int absentColumn)
	{
		int rows = m.rowCount (), cols = m.columnCount ();
		Matrix<T> result = new Matrix<T> (rows-1, cols-1, manager);
		minor (m, absentRow, absentColumn, result);
		return result;
	}

	/**
	 * fill result with minor matrix
	 * @param m the source matrix to use as source
	 * @param absentRow the row to be deleted
	 * @param absentColumn column to delete
	 * @param result destination matrix
	 */
	public void minor
		(
			MatrixAccess<T> m,
			int absentRow, int absentColumn,
			MatrixAccess<T> result
		)
	{
		int r = 1, c = 1, edge = m.rowCount();
		for (int row = 1; row <= edge; row++)
		{
			if (row == absentRow) continue;
			for (int col = 1; col <= edge; col++)
			{
				if (col == absentColumn) continue;
				result.set (r, c++, m.get (row, col));
			}
			r++; c = 1;
		}
	}

	/**
	 * compute determinant
	 * of minor matrix and alternate sign
	 * @param m the matrix to use for computation
	 * @param i the row to remove from the matrix
	 * @param j the column to remove
	 * @return computed result
	 */
	public T cofactor (MatrixAccess<T> m, int i, int j)
	{
		T result = det (minor (m, i, j));
		if ((i + j) % 2 == 0) return result;
		return neg (result);
	}

	/**
	 * compute matrix of cofactors
	 * @param m the matrix to use for computation
	 * @return computed result
	 */
	public Matrix<T> comatrix (MatrixAccess<T> m)
	{
		int rows = m.rowCount (), cols = m.columnCount ();
		Matrix<T> result = new Matrix<T> (rows, cols, manager);
		for (int r = 1; r <= rows; r++)
		{
			for (int c = 1; c <= cols; c++)
			{ result.set (r, c, cofactor (m, r, c)); }
		}
		return result;
	}

	/**
	 * adjugate is transpose of the co-factor matrix
	 * @param m the matrix to use for computation
	 * @return adjugate of matrix
	 */
	public Matrix<T> adj (MatrixAccess<T> m)
	{
		return transpose (comatrix (m));
	}

	/**
	 * compute inverse of 2x2 matrix
	 * @param m the matrix to use for computation
	 * @return computed inverse
	 */
	public Matrix<T> invDet2x2 (MatrixAccess<T> m)
	{
		Matrix<T> diag = times (identity (2), tr (m));
		Matrix<T> negM = times (m, manager.newScalar (-1));
		return sum (diag, negM);
	}

	/**
	 * compute inverse matrix of that specified
	 * @param m the matrix to use for computation
	 * @return computed inverse
	 */
	public Matrix<T> inv (MatrixAccess<T> m)
	{
		T determinant = det (m);
		if (manager.isZero (determinant))
		{ throw new RuntimeException ("DET = 0, no inverse can be computed"); }
		Matrix<T> invDet = m.rowCount() == 2? invDet2x2 (m): adj (m);				// determinant * computedInverse
		return times (invDet, inverted (determinant));
	}

	/**
	 * Laplace algorithm for computation of determinant of square matrix
	 * @param m the matrix to use for computation
	 * @return computed result
	 */
	@SuppressWarnings("unchecked")
	public T cofactorExpansion (MatrixAccess<T> m)
	{
		T result = discrete (0);
		for (int i = 1; i <= m.rowCount (); i++)
		{ result = sumOf (result, X (m.get (i, 1), cofactor (m, i, 1))); }
		return result;
	}

	/**
	 * compute determinant of 2x2 matrix
	 * @param m the matrix to use for computation
	 * @return the computed result
	 */
	@SuppressWarnings("unchecked")
	public T det2x2 (MatrixAccess<T> m)
	{
		return sumOf (X (m.get (1, 1), m.get (2, 2)), neg (X (m.get (1, 2), m.get (2, 1))));
	}

	/**
	 * compute determinant of 3x3 matrix
	 * @param m the matrix to use for computation
	 * @return the computed result
	 */
	@SuppressWarnings("unchecked")
	public T det3x3 (MatrixAccess<T> m)
	{
		MinorAccess<T> access = m.getMinor (1);
		T term1 = X (m.get (1, 1), det2x2 (access.getMinorAbsent (1)));
		T term2 = X (m.get (2, 1), det2x2 (access.getMinorAbsent (2)));
		T term3 = X (m.get (3, 1), det2x2 (access.getMinorAbsent (3)));
		return sumOf (term1, neg (term2), term3);
	}

	/**
	 * compute determinant of square matrix
	 * @param m the matrix to use for computation
	 * @return the computed result
	 */
	public T det (MatrixAccess<T> m)
	{
		int rows = m.rowCount (), cols = m.columnCount ();
		if (rows != cols) raiseException ("DET not valid for non-square matrices");
		
		switch (rows)
		{
		case  2:   return det2x2 (m);
		case  3:   return det3x3 (m);
		default:   return cofactorExpansion (m);
		}
	}

	/**
	 * compute sum of two matrices
	 * @param left the left side of sum
	 * @param right the right side of sum
	 * @return computed sum
	 */
	@SuppressWarnings("unchecked")
	public Matrix<T> sum (MatrixAccess<T> left, MatrixAccess<T> right)
	{
		int rows = left.rowCount (), cols = right.columnCount ();
		Matrix<T> result = new Matrix<T> (rows, cols, manager);

		VectorAccess<T>
			rhtrow = right.getRowAccess (1),
			lftrow = left.getRowAccess (1);
		for (int r = 1; r <= rows; r++)
		{
			for (int c = 1; c <= cols; c++)
			{ result.set (r, c, sumOf (lftrow.get (c), rhtrow.get (c))); }
			lftrow.nextSpan (); rhtrow.nextSpan ();
		}

		return result;
	}

	/**
	 * compute a polynomial series with a matrix as the variable
	 * - this is special case for BuiltinPolynomialFunctions allowing matrix parameters
	 * @param coefficients the coefficients of the polynomial
	 * @param x the matrix to use as polynomial variable
	 * @return the computed series
	 */
	public Matrix <T> sumOfSeries
	(Polynomial.Coefficients <T> coefficients, MatrixAccess <T> x)
	{
		MatrixAccess <T> P = check (coefficients, x);	// maintain x^n
		Matrix <T> ID = this.identity (x.rowCount ());	// identity for order
		Matrix <T> sum = this.sum						// sum first 2
			(
				times (ID, coefficients.get (0)),		// C0*ID + C1*P
				times (P, coefficients.get (1))
			);
		for (int n = 2; n < coefficients.size (); n++)	// remaining coefficients
		{
			P = this.product (x, P);					// next x^n

			sum = this.sum
				(
					this.times							// Cn * x^n
					(
						P,
						coefficients.get (n)
					),
					sum
				);
		}
		return sum;
	}
	public MatrixAccess <T> check
	(Polynomial.Coefficients <T> coefficients, MatrixAccess <T> x)
	{
		if (x.columnCount () != x.rowCount ()) throw new RuntimeException (SQUARE);
		if (coefficients.size () < 2) throw new RuntimeException (MINIMUM);
		return x;
	}
	public static final String SQUARE = "Polynomial evaluation requires square matrix",
			MINIMUM = "Polynomial requires 2 coefficients minimum";

	/**
	 * compute product of matrix with scalar
	 * @param m a matrix to use as left factor
	 * @param scalar the value to multiply with each cell
	 * @return the resulting product
	 */
	public Matrix<T> times (MatrixAccess<T> m, T scalar)
	{
		int rows = m.rowCount (), cols = m.columnCount ();
		Matrix<T> result = new Matrix<T> (rows, cols, manager);
		for (int r = 1; r <= rows; r++)
		{
			for (int c = 1; c <= cols; c++)
			{
				result.set (r, c, X (m.get (r, c), scalar)); 			// each element is multiplied by scalar value
			}
		}
		return result;
	}
	public Matrix<T> times (T scalar, MatrixAccess<T> m) { return times (m, scalar); } 	// commutitive

	/**
	 * compute product of two matrices
	 * @param left the left side of the product
	 * @param right the right side of the product
	 * @return the resulting product matrix
	 */
	public Matrix<T> product (MatrixAccess<T> left, MatrixAccess<T> right)
	{
		int rows = left.rowCount (), cols = right.columnCount ();
		Matrix<T> result = new Matrix<T> (rows, cols, manager);
		VectorAccess<T> row = left.getRowAccess (1);
		
		for (int c = 1; c <= cols; c++)
		{
			VectorAccess<T> col = right.getColAccess (c);

			for (int r = 1; r <= rows;  r++)
			{
				T cell = vectorOperations.dotProduct (row, col);
				result.set (r, c, cell);
				row.nextSpan ();
			}

			row.resetSpan ();
		}

		return result;
	}

	/**
	 * product of matrix with vector.
	 *  useful as check for Ax = b solutions
	 * @param left matrix used for left side of product
	 * @param right vector used for right side of product
	 * @return vactor result of product
	 */
	public VectorAccess<T> product
	(MatrixAccess<T> left, VectorAccess<T> right)
	{
		MatrixAccess<T>
			p = product (left, columnMatrix (right));
		return p.getColAccess (1);
	}

	/**
	 * compute tensor product of two matrices
	 * @param left the left side of the product
	 * @param right the right side of the product
	 * @return the resulting product matrix
	 */
	public Matrix<T> tensor (MatrixAccess<T> left, MatrixAccess<T> right)
	{
		int order = left.columnCount(), resultOrder = order * order;
		if (right.columnCount() != order || left.rowCount() != order || right.rowCount() != order)
		{ throw new RuntimeException ("Tensor product for square equally ordered matrices only"); }
		Matrix<T> result = new Matrix<T> (resultOrder, resultOrder, manager);
		int resultRow = 1, resultCol = 1;

		for (int r = 1; r <= order; r++)
		{
			resultCol = 1;
			for (int c = 1; c <= order; c++)
			{
				copyTo (result, times (right, left.get (r, c)), resultRow, resultCol);
				resultCol += order;
			}
			resultRow += order;
		}
		return result;
	}
	void copyTo (Matrix<T> m, Matrix<T> from, int toRow, int toCol)
	{
		int order = from.columnCount();
		for (int r = 1; r <= order; r++)
		{
			int col = toCol;
			for (int c = 1; c <= order; c++)
			{ m.set (toRow, col++, from.get (r, c)); }
			toRow++;
		}
	}

	/**
	 * display matrix
	 * @param m the matrix to be displayed
	 */
	public void show (MatrixAccess<T> m)
	{
		show (System.out, m);
	}
	public void show (PrintStream out, MatrixAccess<T> m)
	{
		int largest = 0;
		VectorAccess<T> row = m.getRowAccess (1);
		String[][] images = new String[m.rowCount()][m.columnCount()];

		for (int r = 1; r <= m.rowCount ();  r++)
		{
			for (int c = 1; c <= m.columnCount (); c++)
			{
				String elementImage =
					manager.toDecimalString (row.get (c));
				int elementImageLength = elementImage.length ();
				largest = elementImageLength > largest? elementImageLength: largest;
				images[r-1][c-1] = elementImage;
			}
			row.nextSpan ();
		}

		print (out, largest, images, m.rowCount (), m.columnCount ());
	}

	/**
	 * data dump with caption
	 * @param caption the text to be displayed
	 * @param m the data source
	 */
	public synchronized void dump (String caption, MatrixAccess<T> m)
	{
		if (!DUMPING) return;

		System.out.println ();
		System.out.println (caption + " ================="); 

		int max = 0;
		String[][] images = new String[m.rowCount()][m.columnCount()];
		for (int r=1; r<=m.rowCount(); r++)
		{
			for (int c=1; c<=m.columnCount(); c++)
			{
				String img = manager.toDecimalString (m.get (r, c));
				max = img.length() > max? img.length(): max;
				images[r-1][c-1] = img;
			}
		}
		print (System.out, max, images, m.rowCount (), m.columnCount ());
	}
	public void print (PrintStream out, int largest, String[][] images, int rows, int cols)
	{
		int width = largest + MINIMUM_SPACING;
		for (int r = 0; r < rows; r++)
		{
			for (int c = 0; c < cols; c++)
				out.print (justify (images[r][c], width));
			out.println ();
		}
		out.println ();
	}
	public String justify (String img, int width)
	{
		String space = SPACES.substring (0, width - img.length ());
		return space + img;
	}
	static final String SPACES =
	"                                                                  " +
	"                                                                  ";
	static final int MINIMUM_SPACING = 5;
	static final boolean DUMPING = true;

}
