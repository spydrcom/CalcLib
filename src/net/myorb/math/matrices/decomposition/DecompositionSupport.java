
package net.myorb.math.matrices.decomposition;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.math.expressions.DataIO;
import net.myorb.math.matrices.*;
import net.myorb.math.linalg.*;

import java.util.ArrayList;

/**
 * support for matrix operations implementing linear algebra algorithms
 * @author Michael Druckman
 */
public class DecompositionSupport
{


	/**
	 * data manager for machine supported double float
	 */
	public static final ExpressionFloatingFieldManager manager = new ExpressionFloatingFieldManager ();

	/**
	 * support for operations on matrix object
	 */
	public static final MatrixOperations <Double> matOPs = new MatrixOperations <> (manager);

	/**
	 * tab-delimited data file IO control object
	 */
	public static final DataIO <Double> IO = new DataIO <> (manager);


	/**
	 * integer data for pivot tables and other index types
	 */
	public static class IndexList extends ArrayList <Integer>
	{
		public IndexList (int n)
		{ for (int i = 0; i <= n; i++) add (0); }
		private static final long serialVersionUID = -515767469590923087L;
	}


	/**
	 * a vector representation specifically for double values.
	 * - this is understood to be storage for request and solution values
	 */
	public static class VEC extends SolutionPrimitives.Content <Double>
		implements SolutionPrimitives.RequestedResultVector, SolutionPrimitives.SolutionVector
	{
		protected VEC (int n)
		{ super (n, DecompositionSupport.manager); }
		protected  VEC (double [] v) { this (v.length-1); }
		public static VEC alloc (int n) { return new VEC (n); }
		public VEC () { this (0); }

		/**
		 * @param v array of values to hold as contents
		 * @return the new vector object
		 */
		public static VEC enclose (double [] v)
		{
			VEC V = new VEC (v);
			for (int i = 1; i < v.length; i++) { V.set (i, v[i]);}
			return V;
		}
		public static VEC enclose (int [] v)
		{
			VEC V = new VEC (v.length);
			for (int i = 0; i < v.length; i++) { V.set (i+1, (double)v[i]);}
			return V;
		}

		/**
		 * @param v source of values
		 */
		public void copyFrom (double [] v)
		{
			setCells (this, v);
		}

		/**
		 * @return an array of the value held
		 */
		public double [] retrieve ()
		{
			double [] cells = new double [this.size () + 1];
			getCells (this, cells);
			return cells;
		}
	}


	/**
	 * a matrix representation specifically for double values.
	 * - this is understood to be a collection of decomposed matrix contents
	 */
	public static class MAT extends Matrix <Double>
			implements SolutionPrimitives.Decomposition
	{
		protected  MAT (int n, int m)
		{ super (n, m, DecompositionSupport.manager); }
		protected  MAT (double [][] m) { this (m.length-1, m[0].length-1); }
		public static MAT alloc (int n, int m) { return new MAT (n, m); }
		public static MAT alloc (int n) { return new MAT (n, n); }
		public MAT () { this (0, 0); }

		/**
		 * @param m a matrix holding data to copy
		 * @return a new MAT with the copied values
		 */
		public static MAT enclose (Matrix <Double> m)
		{
			return enclose (extract (m));
		}

		/**
		 * @param m a 2D array with values to copy into cells
		 */
		public void copyFrom (double [][] m)
		{
			for (int r = 1; r < m.length; r++)
			{ setCells (getRowAccess (r), m [r]); }
		}

		/**
		 * @param m a 2D array with values to use as cells
		 * @return the new MAT object
		 */
		public static MAT enclose (double [][] m)
		{
			MAT M = new MAT (m);
			M.copyFrom (m);
			return M;
		}

		/**
		 * @return the data held returned as 2D array
		 */
		public double [][] retrieve ()
		{ return extract (this); }
		
		/**
		 * @param as a name to place in banner heading output
		 */
		public void show (String as)
		{
			System.out.println ();
			System.out.println ("===");
			System.out.println ("===  " + as);
			System.out.println ("===");
			show ();
		}

		/**
		 * show to sys output
		 */
		public void show ()
		{
			print (this);
			if (isSingular) System.out.println ("SINGULAR MATRIX");
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.linalg.SolutionPrimitives.Decomposition#store(net.myorb.data.abstractions.SimpleStreamIO.TextSink)
		 */
		public void store (SimpleStreamIO.TextSink to)
		{
			throw new RuntimeException ("not implemented");
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.linalg.SolutionPrimitives.Decomposition#load(net.myorb.data.abstractions.SimpleStreamIO.TextSource)
		 */
		public void load (SimpleStreamIO.TextSource from)
		{
			throw new RuntimeException ("not implemented");
		}

		/**
		 * @return TRUE when object flagged as singular
		 */
		public boolean isSingular () { return isSingular; }
		public void setSingular (boolean isSingular)
		{ this.isSingular = isSingular; }
		boolean isSingular = false;
	}


	/**
	 * @param access the access logic to a vector
	 * @param cells the array to fill from vector
	 */
	public static void getCells (VectorAccess <Double> access, double [] cells)
	{
		for (int i = 1; i < cells.length; i++) { cells[i] = access.get (i); }
	}


	/**
	 * @param access the access logic to a vector
	 * @param cells the value of the cells to write into vector
	 */
	public static void setCells (VectorAccess <Double> access, double [] cells)
	{
		VectorOperations.copyContent (access, VEC.enclose (cells));
	}


	/**
	 * @param m source matrix
	 * @return matrix contents as 2D array
	 */
	public static double [][] extract (Matrix <Double> m)
	{
		int
			rows = m.rowCount (),
			cols = m.columnCount ();
		double [][] A = new double [rows+1][cols+1];

		for (int r = 1; r <= rows; r++)
		{ getCells (m.getRowAccess (r), A[r]); }
		return A;
	}


	/**
	 * exchange row contents
	 * @param r1 one of the rows
	 * @param r2 another of the rows
	 * @param in the source matrix
	 */
	public static void interchange
	(int r1, int r2, Matrix <Double> in)
	{
		Vector <Double>
			r1cells = in.getRow (r1), r2cells = in.getRow (r2);
		VectorOperations.copyContent (in.getRowAccess (r1), r2cells);
		VectorOperations.copyContent (in.getRowAccess (r2), r1cells);
	}



	/**
	 * @param row source row
	 * @param col source column
	 * @param upTo the highest index in product
	 * @param L the lower triangle source
	 * @param U the upper triangle source
	 * @return rowXcol
	 */
	public static double crossProduct
		(
			int row, int col, int upTo,
			Matrix <Double> L, Matrix <Double> U
		)
	{
		double sum = 0.0;
		for (int k = 1; k < upTo; k++)
		{ sum += L.get (row, k) * U.get (k, col); }
		return sum;
	}


	/**
	 * @param row the row of focus cell
	 * @param col the column of focus cell
	 * @param upTo the highest index in product
	 * @param A the matrix being evaluated
	 * @return cell minus rowXcol product
	 */
	public static double productDifference
		(
			int row, int col, int upTo,
			Matrix <Double> A
		)
	{
		double result;
		double product = crossProduct (row, col, upTo, A, A);
		A.set (row, col, result = A.get (row, col) - product);
		return result;
	}


	/**
	 * @param a the matrix being evaluated
	 * @return vector of values giving scale (1/max) of each row
	 */
	public static Vector <Double> scaleVector (Matrix <Double> a)
	{
		Vector <Double> v;

		int n = a.rowCount ();
		v = new Vector <> (n, manager);					// v stores the implicit scaling of each row.

		for (int i = 1; i <= n; i++)					// Loop over rows to get the implicit 
		{												// scaling information.
			v.set (i, 1.0 / biggestOf (i, a));			// Save the scaling.
		}

		return v;
	}


	/**
	 * find largest value in row
	 * @param row the number of the row
	 * @param in the matrix being evaluated
	 * @return largest value in row
	 */
	public static double biggestOf (int row, Matrix <Double> in)
	{
		int n = in.columnCount ();
		double biggest = 0.0, temp;

		for (int col = 1; col <= n; col++)
		{
			temp = Math.abs (in.get (row, col));
			if ( temp > biggest ) biggest = temp;
		}

		check (biggest);
		return biggest;
	}


	/**
	 * @param value the value to check
	 * @throws RuntimeException for zero value indicating det==0
	 */
	public static void check (double value) throws RuntimeException
	{
		if (value == 0.0) throw new RuntimeException ("Singular matrix");
	}


	/**
	 * @param a one side of triangle
	 * @param b other side of triangle
	 * @return hypotenuse size
	 */
	public static double pythag (double a, double b)
	{
		double absa=Math.abs(a), absb=Math.abs(b);
		//Computes (a^2+b^2)^1/2 without destructive underflow or overflow.
		if (absa > absb) return absa*Math.sqrt(1.0+Math.pow (absb/absa, 2));
		else return (absb == 0.0 ? 0.0 : Math.sqrt(1.0+Math.pow (absa/absb, 2)));
	}


	/**
	 * @param x value to check
	 * @return TRUE for != 0
	 */
	public static boolean test (int x) { return x != 0; }

	/**
	 * @param x value to check
	 * @return TRUE for != 0
	 */
	public static boolean test (double x) { return x != 0; }


	/**
	 * @param x source of value
	 * @param y source of sign
	 * @return product of two
	 */
	public static double SIGN (double x, double y)
	{
		//return Math.abs(x) * (y < 0? -1.0: 1.0);
		return Math.abs(x) * Math.signum (y);
	}


	/**
	 * @param row values to fill row
	 * @param num number of row in matrix
	 * @param m the matrix
	 */
	public static void copyRow
	(double [] row, int num, Matrix <Double> m)
	{
		setCells (m.getRowAccess (num), row);
	}


	/**
	 * sys out in TDF format
	 * @param m matrix to print
	 */
	public static void print (Matrix <Double> m)
	{
		for (int r = 1; r <= m.rowCount (); r++)
		{
			for (int c = 1; c <= m.columnCount (); c++)
			{
				System.out.print (m.get (r, c));
				System.out.print ("\t ");
			}
			System.out.println ();
		}
	}


	/**
	 * sys out in TDF format
	 * @param v vector to print
	 */
	public static void print (double [] v)
	{
		for (int c = 1; c < v.length; c++)
		{
			System.out.print (v [c]);
			System.out.print ("\t ");
		}
		System.out.println ();
	}


	/**
	 * sys out in TDF format
	 * @param m matrix to print
	 */
	public static void print (double [] [] m)
	{
		for (int r = 1; r < m.length; r++)
		{
			for (int c = 1; c < m[0].length; c++)
			{
				System.out.print (m [r][c]);
				System.out.print ("\t ");
			}
			System.out.println ();
		}
		System.out.println ();
	}


}

