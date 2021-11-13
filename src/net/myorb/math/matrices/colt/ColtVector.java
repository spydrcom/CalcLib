
package net.myorb.math.matrices.colt;

import net.myorb.math.matrices.*;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;

public class ColtVector extends DoubleMatrix1D
{

	public ColtVector (Vector<Double> v)
	{
		this.v = v;
	}
	Vector<Double> v;

	public double getQuick(int index)
	{
		return v.get(index+1);
	}

	public DoubleMatrix1D like(int size)
	{
		return new ColtVector (new Vector<Double> (size, v.getSpaceDescription ()));
	}

	public DoubleMatrix2D like2D(int rows, int columns)
	{
		Matrix<Double> newM = new Matrix<Double>(rows, columns, v.getSpaceDescription ());
		return new ColtMatrix (newM);
	}

	public void setQuick(int index, double value)
	{
		v.set (index, value);
	}

	protected DoubleMatrix1D viewSelectionLike(int[] offsets)
	{
		return null;
	}

	final static long serialVersionUID = 2l;

}
