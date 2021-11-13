
package net.myorb.math.characteristics;

import net.myorb.math.*;
import net.myorb.math.computational.PolynomialRoots;
import net.myorb.math.matrices.transforms.*;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.matrices.*;

import java.util.List;

/**
 * algorithms for evaluations of matrices providing eigenvalues and eigenvectors
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class EigenvaluesAndEigenvectors<T> extends Arithmetic<T>
{


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 * @param lib an implementation of the power library
	 */
	public EigenvaluesAndEigenvectors
		(SpaceManager<T> manager, PowerLibrary<T> lib)
	{
		super (manager); this.lib = lib; 
		this.roots = new PolynomialRoots<T> (manager, lib);
		this.matrixOperations = new MatrixOperations<T> (manager);
		this.polyManager = new PolynomialSpaceManager<T> (manager);
		this.vonMises = new VonMises<T> (manager);

	}
	protected PolynomialSpaceManager<T> polyManager;
	protected MatrixOperations<T> matrixOperations;
	protected PolynomialRoots<T> roots;
	protected VonMises<T> vonMises;
	protected PowerLibrary<T> lib;


	/**
	 * compute matrix m - I*lambda
	 * @param m matrix to be used in computation
	 * @param lambda the eigenvalue to multimply into identity
	 * @return computed matrix
	 */
	public Matrix<T> eigenvalueEquation (MatrixAccess<T> m, T lambda)
	{
		return matrixOperations.sum (m, matrixOperations.times (matrixOperations.identity (m.columnCount ()), neg (lambda)));
	}


	/**
	 * solve homogeneous equation set from m-I*lambda
	 * @param m the matrix describing the original problem set
	 * @param lambda the eigenvalue (from characteristic polynomial roots) to match with an eigenvector
	 * @return an eigenvector solution of the space defined in the solution set
	 */
	public Vector<T> findEigenvectorFor (Matrix<T> m, T lambda)
	{
		return new SimultaneousEquations<T> (manager).findHomogeneousSolution (eigenvalueEquation (m, lambda));
	}


	/**
	 * find the dominant eigenvector for the 
	 *  specified matrix using Von Mises power iterations
	 * @param m the matrix describing the original problem set
	 * @param maxIterations the maximum allowed iteration count
	 * @param toleranceScale the definition of tolerance
	 * @return the vector computed
	 */
	public Vector<T> findDominantEigenvectorFor (Matrix<T> m, int maxIterations, int toleranceScale)
	{
		T ONE = discrete (1);
		vonMises.setToleranceScale (toleranceScale);
		Vector<T> v = new Vector<T> (m.rowCount (), manager);
		for (int i = 1; i <= v.size(); i++) v.set (i, ONE);
		vonMises.setIterationMaximum (maxIterations);
		v = vonMises.executePowerIterations (m, v);
		return v;
	}


	/**
	 * compute the dominant eigenvalue and eigenvector 
	 *  for the specified matrix using Von Mises power iterations
	 * @param m the matrix describing the original problem set to be evaluated
	 * @param eigenvector a vector object that will be updated to contain the dominant eigenvector
	 * @param maxIterations the maximum allowed iteration count
	 * @param toleranceScale the definition of tolerance
	 * @return the computed dominant eigenvalue
	 */
	public T findDominantEigensystemMemberFor 
	(Matrix<T> m, Vector<T> eigenvector, int maxIterations, int toleranceScale)
	{
		Vector<T> v = findDominantEigenvectorFor (m, maxIterations, toleranceScale);
		T eigenvalue = vonMises.computePowerIteration (m, v, eigenvector);
		return eigenvalue;
	}


	/**
	 * verify eigenvalue/eigenvector provide 
	 *  valid solution on system defined by matrix m
	 * @param m the matrix describing the original problem set to be evaluated
	 * @param eigenvector the vector to be used in verification of the solution
	 * @param eigenvalue the eigenvalue to be used in verification of the solution
	 * @param toleranceScale definition of the tolerance of the verification
	 * @return TRUE = solution is verified with tolerance
	 */
	public boolean checkSolution
	(Matrix<T> m, Vector<T> eigenvector, T eigenvalue, int toleranceScale)
	{
		VectorAccess<T> product =
			matrixOperations.product (eigenvalueEquation (m, eigenvalue), 
					matrixOperations.columnMatrix (eigenvector)).getColAccess (1);
		Tolerances<T> checker = new Tolerances<T> (manager); checker.setToleranceDefaults (this.lib);
		checker.setToleranceScale (toleranceScale);
		
		for (int i = 1; i <= product.size (); i++)
		{
			if (!checker.withinTolerance (product.get (i))) return false;
		}
		return true;
	}


	/**
	 * compute the characteristic polynomial of the specified matrix
	 * @param m matrix of power functions to be used in computation
	 * @return the characteristic polynomial of the matrix
	 */
	public Polynomial.PowerFunction<T> computeCharacteristicPolynomial
			(MatrixAccess<Polynomial.PowerFunction<T>> m)
	{
		MatrixOperations<Polynomial.PowerFunction<T>> matrixOperations =
			new MatrixOperations<Polynomial.PowerFunction<T>> (polyManager);
		Matrix<Polynomial.PowerFunction<T>> I = matrixOperations.identity (m.columnCount ());
		Polynomial.PowerFunction<T> fOfX = roots.linearFunctionOfX (discrete (-1), discrete (0));
		Matrix<Polynomial.PowerFunction<T>> sum = matrixOperations.sum (m, matrixOperations.times (fOfX, I));
		Polynomial.PowerFunction<T> det = matrixOperations.det (sum);
		return det;
	}


	/**
	 * convert matrix of constants to a matrix of constant functions
	 * @param m a matrix of constants to be converted
	 * @return the matrix of constant functions
	 */
	public Matrix<Polynomial.PowerFunction<T>> convertToPolynomialMatrix (MatrixAccess<T> m)
	{
		Matrix<Polynomial.PowerFunction<T>> functionMatrix =
			new Matrix<Polynomial.PowerFunction<T>> (m.rowCount(), m.columnCount(), polyManager);
		for (int r = 1; r <= m.rowCount(); r++)
		{
			for (int c = 1; c <= m.columnCount(); c++)
			{
				T element = m.get (r, c);
				Polynomial.PowerFunction<T> f =
					polyManager.getPolynomialFunction (polyManager.newCoefficients (element));
				functionMatrix.set (r, c, f);
			}
		}
		return functionMatrix;
	}


	/**
	 * compute the characteristic
	 *  polynomial of the specified matrix
	 * @param m a matrix of constants to be evaluated
	 * @return the characteristic polynomial of the matrix
	 */
	public Polynomial.PowerFunction<T>
		computeCharacteristicPolynomialFor (MatrixAccess<T> m)
	{ return computeCharacteristicPolynomial (convertToPolynomialMatrix (m)); }


	/**
	 * Compute companion matrix for polynomial
	 * @param p the polynomial power function to convert
	 * @return companion matrix
	 */
	public MatrixAccess<T>
		computeCompanionMatrixFor (Polynomial.PowerFunction<T> p)
	{ throw new RuntimeException ("Companion matrix not implemented"); }


	/**
	 * compute the eigenvalues of the specified matrix
	 * @param m a matrix of constants to be evaluated
	 * @return a list of eigenvalues
	 */
	public List<T> computeEigenvaluesFor (MatrixAccess<T> m)
	{
		Polynomial.PowerFunction<T> p =
			computeCharacteristicPolynomialFor (m);
		return roots.evaluateEquation (p);
	}


}

