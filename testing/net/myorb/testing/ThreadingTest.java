
package net.myorb.testing;

import java.util.Date;

import net.myorb.math.matrices.*;

public class ThreadingTest extends net.myorb.math.computational.Fourier
{

	static VectorOperations<Double> vectorOperations;
	static MatrixOperations<Double> matrixOperations;

	public Matrix<Double> constructMatrix (Double omega, int harmonics)
	{
		Matrix<Double> m = new Matrix<Double> (harmonics, harmonics, manager);
		for (int t = 1; t <= harmonics; t++) addHarmonics (t, omega*t, harmonics, m);
		return m;
	}


	public Matrix<Double> comatrix (MatrixAccess<Double> m)
	{
		int rows = m.rowCount (), cols = m.columnCount ();
		Matrix<Double> result = new Matrix<Double> (rows, cols, manager);

		for (int c = 1; c <= cols; c++)
		{
			CoMat runner = new CoMat (c, m.getMinor(c), result);
			new Thread (runner).start();
		}

		return result;
	}


	public void runTest ()
	{
		Date start = new Date ();
		Double omega = frequency (30d);

		vectorOperations =
			new VectorOperations<Double> (manager);
		matrixOperations = new MatrixOperations<Double> (manager);

		Matrix<Double>coffecients = constructMatrix (omega, 9);
		Matrix<Double> comat = comatrix (coffecients);
		
		try { Thread.sleep(20000); } catch (Exception e) {}
		matrixOperations.show(comat);

		Date finish = new Date ();
		System.out.println ("---");
		long millis = finish.getTime() - start.getTime();
		System.out.println (millis + "ms");
		System.out.println ("---");
	}


	/**
	 * execution starting point
	 * @param args not used
	 */
	public static void main (String... args)
	{
		new ThreadingTest ().runTest ();
	}

}

class CoMat implements Runnable
{
	CoMat (int col, MinorAccess<Double> m, MatrixAccess<Double> result)
	{
		this.minor = m;
		this.result = result;
		this.col = col;
	}

	MinorAccess<Double> minor;
	MatrixAccess<Double> result;
	int col;

	public void run ()
	{
		Date start = new Date ();
		System.out.println ("start c = " + col);

					for (int r = 1; r <= result.rowCount(); r++)
					{
						result.set (r, col, cofactor (minor, r, col));		// the access object covers all rows for the column originally set
					}

		Date finish = new Date ();
		long millis = finish.getTime() - start.getTime();
		System.out.println ("end c = " + col + ", " + millis + "ms");
	}

	public Double cofactor (MinorAccess<Double> access, int row, int col)
	{
		Double result = ThreadingTest.matrixOperations.det (access.getMinorAbsent (row));
		if ((row + col) % 2 == 1) result = - (result);
		return result;
	}

}


