
package net.myorb.testing;

import net.myorb.math.matrices.*;
import net.myorb.math.matrices.transforms.*;
import net.myorb.math.realnumbers.FloatingFieldManager;
import net.myorb.math.SpaceManager;

public class VonMisesTests<T> extends VonMises<T>
{

	/**
	 * values are constructed based on type manager for components
	 * @param manager the manager for the component type
	 */
	public VonMisesTests
		(SpaceManager<T> manager)
	{
		super (manager);
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

		System.out.println ("det = " + matrices.det (a));

		VonMises<Float> vm = new VonMises<Float> (manager);
		Vector<Float> b = vops.V (1f, 1f, 1f, 1f);

		vm.setToleranceScale (6);
		vm.setIterationMaximum(20);

		b = vm.executePowerIterations (a, b);
		//[0.718046, 0.22115299, -0.55735135, 0.3533565]
		System.out.print ("final b = ");
		vops.show (b);
		
		Vector<Float> eigenvector = new Vector<Float> (manager);
		Float eigenvalue = vm.computePowerIteration (a, b, eigenvector);

		System.out.print ("eigenvector = "); vops.show (eigenvector);
		System.out.println ("eigenvalue = " + eigenvalue);

		Matrix<Float> I = matrices.identity (b.size ());
		Matrix<Float> negLambdaI = matrices.times (I, -eigenvalue);
		Matrix<Float> aMinusIlambda = matrices.sum (a, negLambdaI);

		System.out.println ("---");
		System.out.println ("A - lambda*I ="); matrices.show (aMinusIlambda);
		System.out.println ("det = " + matrices.det (aMinusIlambda));
		System.out.println ("===");

		System.out.println ("product check");
		matrices.show (matrices.product (aMinusIlambda, matrices.columnMatrix (eigenvector)));
		System.out.println ("===");
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

