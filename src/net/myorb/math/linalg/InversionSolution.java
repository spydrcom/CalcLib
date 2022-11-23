
package net.myorb.math.linalg;

import net.myorb.math.matrices.*;
import net.myorb.math.SpaceManager;

import java.util.Arrays;

/**
 * generic implementation of matrix inversion
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class InversionSolution <T>
	implements SolutionPrimitives.Invertable <T>
{


	public InversionSolution
	(SolutionPrimitives <T> using) { this.using = using; }
	protected SolutionPrimitives <T> using;


	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives.Invertable#inv(net.myorb.math.matrices.Matrix)
	 */
	@Override public Matrix <T> inv (Matrix <T> source)
	{
		this.N = source.getEdgeCount ();
		this.matD = using.decompose (source);
		this.manager = source.getSpaceManager ();
		this.computeInversion ();
		return inverted;
	}
	protected SolutionPrimitives.Decomposition matD;
	protected SpaceManager <T> manager;
	protected int N;


	/**
	 * compute solution column by column
	 */
	@SuppressWarnings("unchecked") public void computeInversion ()
	{
		SolutionPrimitives.Content <T> solution;

		this.inverted = new Matrix <T> (N, N, manager);

		for (int j = 1; j <= N; j++)
		{
			solution =
				(SolutionPrimitives.Content <T>)
					using.solve (matD, columnResultFor (j));
			VectorOperations.copyContent (this.inverted.getColAccess (j), solution);
		}
	}
	protected Matrix <T> inverted;


	/**
	 * @param col number of column to describe
	 * @return the content describing a column vector
	 */
	SolutionPrimitives.Content <T> columnResultFor (int col)
	{
		int [] content = new int [N+1];
		Arrays.fill (content, 0); content[col] = 1;
		return new SolutionPrimitives.Content <T> (content, manager);
	}


}

