
package net.myorb.math.computational;

import net.myorb.math.polynomial.PolynomialSpaceManager;

import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.DataSequence;
import net.myorb.math.*;

/**
 * Lagrange interpolation based on cross products of data set components
 * @param <T> the underlying data type
 * @author Michael Druckman
 */
public class LagrangeInterpolation<T>
{

	/**
	 * @param dataSet the data being used as interpolation source
	 * @throws RuntimeException condition raised when x.size != y.size
	 */
	public void verifyDataSet (DataSequence2D<T> dataSet) throws RuntimeException
	{
		dataPoints = dataSet.verifyDataSet ();
	}
	protected int dataPoints;

	/**
	 * outer layer of Lagrange computation is term summation
	 * @param dataSet a sample set of 2 dimensional data points
	 * @return the polynomial descriptor
	 */
	public Polynomial.PowerFunction<T> forSequence (DataSequence2D<T> dataSet)
	{
		verifyDataSet (dataSet);
		Polynomial.PowerFunction<T> p = polynomialSpaceManager.getZero (), term;
		for (int i = 0; i < dataPoints; i++)
		{
			term = polynomialSpaceManager.times
				(dataSet.yAxis.get (i), iThLagrangeTerm (i, dataSet.xAxis));
			p = polynomialSpaceManager.add (p, term);
		}
		return p;
	}

	/**
	 * inner layer of Lagrange
	 *  computation is product of xAxis values
	 * @param i the number of the outer summation term
	 * @param xSamples the xAxis samples for the interpolation
	 * @return the polynomial descriptor of the intermediate computation
	 */
	public Polynomial.PowerFunction<T> iThLagrangeTerm
		(int i, DataSequence<T> xSamples)
	{
		T xi = xSamples.get (i);
		Polynomial.PowerFunction<T> term = polynomialSpaceManager.getOne (), factor;
		for (int j = 0; j < dataPoints; j++)
		{
			if (i == j) continue;
			T xjNeg = spaceManager.negate (xSamples.get (j));					// -x[j]
			T xiMxjInv = spaceManager.invert (spaceManager.add (xi, xjNeg));	// 1 / ( x[i] - x[j] )
			factor = polynomialSpaceManager.times (xiMxjInv, xPlus (xjNeg));	// ( X - x[j] ) / ( x[i] - x[j] )
			term = polynomialSpaceManager.multiply (term, factor);
		}
		return term;
	}

	/**
	 * @param constant a constant value
	 * @return a polynomial representation of (X + constant)
	 */
	public Polynomial.PowerFunction<T> xPlus (T constant)
	{
		return polynomialSpaceManager.add (X, polynomialSpaceManager.constantFunction (constant));
	}

	public LagrangeInterpolation (SpaceManager<T> spaceManager)
	{
		this.polynomialSpaceManager = new PolynomialSpaceManager<T> (spaceManager);
		this.X = polynomialSpaceManager.newVariable ();
		this.spaceManager = spaceManager;
	}
	protected PolynomialSpaceManager<T> polynomialSpaceManager;
	protected Polynomial.PowerFunction<T> X;
	protected SpaceManager<T> spaceManager;

}
