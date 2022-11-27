
package net.myorb.math.matrices.colt;

//import net.myorb.math.*;
import net.myorb.math.matrices.*;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;

public class ColtMatrix extends DoubleMatrix2D
{

	public ColtMatrix (Matrix<Double> m)
	{
		this.m = m;
		this.setUp (m.rowCount (), m.columnCount ());
	}
	Matrix<Double> m;

	public double getQuick(int row, int column)
	{
		return m.get (row+1, column+1);
	}

	public DoubleMatrix2D like(int rows, int columns)
	{
		Matrix<Double> newM = new Matrix<Double>(rows, columns, m.getSpaceDescription ());
		return new ColtMatrix (newM);
	}

	protected DoubleMatrix1D like1D(int size, int zero, int stride)
	{
		return null;
	}

	public DoubleMatrix1D like1D (int size)
	{
		return new ColtVector (new Vector<Double> (size, m.getSpaceDescription ()));
	}

	public void setQuick(int row, int column, double value)
	{
		m.set (row+1, column+1, value);
	}

	protected DoubleMatrix2D viewSelectionLike (int[] rowOffsets, int[] columnOffsets)
	{
		return null;
	}

	public DoubleMatrix1D viewColumn (int column)
	{
		int n = m.rowCount ();
		DoubleMatrix1D v = like1D (n);
		for (int r = 1; r <= n; r++) v.set (r-1, m.get (r, column+1));
		return v;
	}

	final static long serialVersionUID = 1l;

}
