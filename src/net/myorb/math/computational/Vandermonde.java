
package net.myorb.math.computational;

import net.myorb.math.matrices.*;
import net.myorb.data.abstractions.DataSequence;
import net.myorb.math.*;

/**
 * use linear algebra to compute polynomial coefficients given data sequences
 * @param <T> the types of coefficients in the polynomial terms
 * @author Michael Druckman
 */
public class Vandermonde<T> extends Matrix<T>
{


	/**
	 * prepare matrix based on data sequence source
	 * @param dataSequence the data sequence to be used for equations
	 * @param manager the data type manager
	 */
	public Vandermonde
	(DataSequence<T> dataSequence, SpaceManager<T> manager)
	{
		super (dataSequence.size (), dataSequence.size (), manager);
		populate (dataSequence);
	}


	/**
	 * for superclass that has different population algorithm
	 * @param size the size of the matrix, assumed square
	 * @param manager the data type manager
	 */
	public Vandermonde
	(int size, SpaceManager<T> manager)
	{
		super (size, size, manager);
	}


	/**
	 * populate the matrix with data from the sample set
	 * @param dataSequence the sample set being used for interpolation
	 */
	public void populate
	(DataSequence<T> dataSequence)
	{
		for (int r = 1; r <= rows; r++)
		{
			T value = manager.getOne ();
			T x = dataSequence.get (r - 1);
			for (int c = 1; c <= cols; c++)
			{
				set (r, c, value);
				value = manager.multiply (value, x);
			}
		}
	}


	/**
	 * solve for polynomial coefficients using matrix inversion
	 * @param solution a coefficients object to be filled with the results
	 * @param dataSequence the y-axis data sequence counter-part to the matrix population sequence
	 * @param manager a manager for the data type
	 */
	public void inverseSolution
	(Polynomial.Coefficients<T> solution, DataSequence<T> dataSequence, SpaceManager<T> manager)
	{
		new SimultaneousEquations<T> (manager).inverseSolution (this, columnFor (dataSequence)).addToList (solution);
	}


	/**
	 * solve for polynomial coefficients using substitution determinant
	 * @param solution a coefficients object to be filled with the results
	 * @param dataSequence the y-axis data sequence counter-part to the matrix population sequence
	 * @param manager a manager for the data type
	 */
	public void substitutionSolution
	(Polynomial.Coefficients<T> solution, DataSequence<T> dataSequence, SpaceManager<T> manager)
	{
		new SimultaneousEquations<T> (manager).solve (this, columnFor (dataSequence)).addToList (solution);
	}


	/**
	 * solve for polynomial coefficients using Gaussian Elimination
	 * @param solution a coefficients object to be filled with the results
	 * @param dataSequence the y-axis data sequence counter-part to the matrix population sequence
	 * @param manager a manager for the data type
	 */
	public void gaussSolution
	(Polynomial.Coefficients<T> solution, DataSequence<T> dataSequence, SpaceManager<T> manager)
	{
		VectorAccess<T> result =
			new SimultaneousEquations<T> (manager)
				.applyGaussianElimination (this, columnFor (dataSequence));
		new VectorOperations<T>(manager).addToList (solution, result);
	}


	/**
	 * treat data sequence as column vector
	 * @param data the data sequence being described
	 * @return a column vector
	 */
	public Vector<T> columnFor (DataSequence<T> data)
	{
		Vector<T> v;
		(v = new Vector<T> (manager)).load (data);
		return v;
	}


}

