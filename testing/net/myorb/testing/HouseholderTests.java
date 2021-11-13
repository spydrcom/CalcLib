
package net.myorb.testing;

import net.myorb.math.SpaceManager;
import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.SimultaneousEquations;
import net.myorb.math.matrices.transforms.Householder;
import net.myorb.math.realnumbers.FloatingFieldManager;
import net.myorb.math.matrices.VectorOperations;
import net.myorb.math.matrices.Vector;

public class HouseholderTests<T> extends Householder<T>
{


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public HouseholderTests
	(SpaceManager<T> manager)
	{
		super (manager);
	}


	void runTest (Matrix<T> a)
	{
		int n = a.rowCount ();
		Value<T> a21 = forValue (a.get (2, 1));

		Value<T> sum = forValue (0), ax;
		for (int j = 2; j <= n; j++) { ax = forValue (a.get (j, 1)); sum = sum.plus (ax.squared ()); }

		Value<T> alpha = sgn (a21).negate ().times (sqrt (sum));
		Value<T> r = sqrt (alpha.squared ().minus (a21.times (alpha)).over (forValue (2)));
		System.out.println ("alpha=" + alpha + " r="+r);
		Value<T> twoR = r.times (forValue (2));
		Value<T> twoRinv = twoR.inverted ();

		Vector<T> v = new Vector<T> (n, manager);

		v.set (1, discrete (0));
		Value<T> v2 = a21.minus (alpha).times (twoRinv);
		v.set (2, v2.getUnderlying ());

		for (int k = 3; k <= n; k++)
		{
			v.set (k, X (twoRinv.getUnderlying(), a.get (k, 1)));
		}

		System.out.print ("v=");
		vecs.show (v);
		
		System.out.println ("Householder P = I - 2v*v =");
		Matrix<T> vsq = vecs.dyadicProduct (v, v);
		Matrix<T> vsq2 = matrices.times (discrete (-2), vsq);
		Matrix<T> p = matrices.sum (matrices.identity (n), vsq2);
		matrices.show (p);

		System.out.println ("P * P = (P is involutory)");
		matrices.show (matrices.product (p, p));

		Matrix<T> pa = matrices.product (p, a);
		Matrix<T> pap = matrices.product (pa, p);
		System.out.println ("PAP =");
		matrices.show (pap);
	}


	void runTest2 (Matrix<T> a)
	{
		Matrix<T> an = a;
		int n = a.rowCount ();
		for (int i = 1; i < n-1; i++)
		{
			System.out.println ("PAP ("+i+") =");
			Matrix<T> pap = computeHouseholderPAP (an, i);
			matrices.show (pap);
			an = pap;
		}
	}


	void runTest3 (Matrix<T> a)
	{
		int n = a.rowCount ();
		Matrix<T> PAPnth = computeHouseholderPAPnth (a);
		System.out.println ("PAP ("+n+") =");
		matrices.show (PAPnth);
	}


	static void runTests (SpaceManager<Float> manager)
	{
		Matrix<Float> a = new Matrix<Float> (4, 4, manager);
		SimultaneousEquations<Float> matrices = new SimultaneousEquations<Float> (manager);
		VectorOperations<Float> vops = new VectorOperations<Float> (manager);

		System.out.println ();
		System.out.println ("A =");
		matrices.setRow (1, a, vops.V ( 4f, 1f, -2f,  2f));
		matrices.setRow (2, a, vops.V ( 1f, 2f,  0f,  1f));
		matrices.setRow (3, a, vops.V (-2f, 0f,  3f, -2f));
		matrices.setRow (4, a, vops.V ( 2f, 1f, -2f, -1f));
		matrices.show (a);

		new HouseholderTests<Float> (manager).runTest (a);
		new HouseholderTests<Float> (manager).runTest2 (a);
		new HouseholderTests<Float> (manager).runTest3 (a);
	}


	/**
	 * execute tests on complex objects
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		FloatingFieldManager manager = new FloatingFieldManager ();
		runTests (manager);
	}



}
