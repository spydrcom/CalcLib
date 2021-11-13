
package net.myorb.testing;

import net.myorb.math.TrigLib;
import net.myorb.math.matrices.*;
import net.myorb.math.realnumbers.FloatingFieldManager;

public class TimeSeriesTest extends TrigLib<Float>
{


	TimeSeriesTest ()
	{
		super (manager);
	}


	Float freq (Float period)
	{
		return 2.0f * manager.getPi () / period;
	}


	void addHarmonics (int t, Float wt, Matrix<Float> m)
	{
		for (int n=1; n<=5; n++) m.set (t, n, cos (n*wt));
	}


	Matrix<Float> getMatrix (Float omega)
	{
		Matrix<Float> m = new Matrix<Float> (5, 5, manager);
		for (int t=1; t<=5; t++) addHarmonics (t, omega*t, m);
		return mops.inv (m);
	}


	Float compute (Matrix<Float> coef, Float omega, Float t) 
	{
		Float result = 0f;
		for (int n=1; n<=5; n++)
		{ result = result + coef.get (n, 1) * cos (n * omega * t); }
		return result;
	}


	static final FloatingFieldManager manager = new FloatingFieldManager ();
	static final MatrixOperations<Float> mops = new MatrixOperations<Float> (manager);
	static final VectorOperations<Float> vops = new VectorOperations<Float> (manager);


	public static void main (String... args)
	{
		Float omega;
		TimeSeriesTest test = new TimeSeriesTest ();
		Matrix<Float> m = test.getMatrix (omega = test.freq (30f));
		System.out.println ("m =========");
		mops.show (m);

		System.out.println ("series =========");
		Matrix<Float> series = new Matrix<Float> (5, 1, manager);
		mops.setCol (1, series, vops.V (112f, 114f, 113f, 110f, 106f));
		mops.show (series);

		System.out.println ("coef =========");
		Matrix<Float> coef = mops.product (m, series);
		mops.show (coef);

		System.out.println ("computed =========");

		for (int t=1; t<=10; t++)
		{
			Float value = test.compute (coef, omega, (float)t);
			System.out.println (value);
		}
	}


}
