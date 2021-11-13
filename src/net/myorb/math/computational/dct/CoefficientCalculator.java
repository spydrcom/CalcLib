
package net.myorb.math.computational.dct;

import net.myorb.math.computational.ChebyshevRecursiveCosineMultiples;

import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.DataSequence;

import java.util.ArrayList;
import java.util.List;

/**
 * compute dot products over cosine harmonic series
 * @author Michael Druckman
 */
public class CoefficientCalculator extends ChebyshevRecursiveCosineMultiples
{

	/**
	 * @param fCosPiN sequence of function evaluations of cosine multiples
	 * @param N number of factors to be included
	 */
	CoefficientCalculator (DataSequence2D<Double> fCosPiN, int N)
	{ cosParameters = fCosPiN.xAxis; functionEvaluations = fCosPiN.yAxis; this.harCount = N - 1; }
	protected DataSequence<Double> functionEvaluations, cosParameters;
	protected int harCount; // count of harmonics

	/**
	 * @param data the sequence of function computations
	 * @return the number of data points
	 */
	public static int domainSize (DataSequence2D<Double> data)
	{
		return data.xAxis.size () - 1;
	}

	/**
	 * @param coefficientCount number of coefficients
	 */
	protected void setCoefficientCount (int coefficientCount)
	{
		this.coefficientCount = coefficientCount;
		this.N2 = 2.0 / coefficientCount; 
	}
	protected int coefficientCount;
	protected double N2;

	/**
	 * first and last terms are weighted
	 * @param plus value added as positive for odd terms
	 * @param minus value added as negative for odd terms
	 * @return THIS calculator
	 */
	public CoefficientCalculator setBases (double plus, double minus)
	{ this.even = plus + minus;  this.odd = plus - minus; return this; }
	protected double even, odd;
	
	/**
	 * building list of coefficients
	 * @param value new value for coefficients list
	 */
	public void add (double value) { coef.add (value); }
	public ArrayList<Double> getCoeficients () { return coef; }
	protected ArrayList<Double> coef = new ArrayList<Double>();

	/**
	 * simple dot product
	 *  of selected section of arrays
	 * @param left the left side of the multiplication
	 * @param right the right side of the multiplication
	 * @param lo the lo index of the section
	 * @param hi the hi index of the section
	 * @return sum of products of elements
	 */
	public static double dot (List<Double> left, List<Double> right, int lo, int hi)
	{
		double sum = 0;
		for (int n = lo; n <= hi; n++) { sum += left.get (n) * right.get (n); }
		return sum;
	}

	/**
	 * summation of products
	 *   of function with cosine
	 * @param k the multiplier of (n/N)PI
	 * @return the computed sum
	 */
	protected double coefficientOffsetFor (int k)
	{
		List<Double> cosPiKN = multiplesOfCos (cosParameters.get (k), harCount);
		// SIGMA [ 1 <= n <= N-1 ] ( f(cos(n*pi/N)) * cos(n*k*pi/N) )
		return dot (functionEvaluations, cosPiKN, 1, harCount);
	}

	/**
	 * compute coefficient for specified multiplier
	 * @param base the even or odd base depending on term number
	 * @param multiplier harmonic number for this term
	 */
	public void computeCoefficient (double base, int multiplier)
	{
		add (N2 * (base + coefficientOffsetFor (multiplier)));
	}

	/**
	 * track even and odd sequence items.
	 *  even sequence item flag toggles with each call
	 * @param multiplier the current multiplier
	 */
	public void addWithToggle (int multiplier)
	{ computeCoefficient (isEvensequenceItem? even: odd, multiplier); isEvensequenceItem = ! isEvensequenceItem; }
	protected boolean isEvensequenceItem = true;

	/**
	 * add Coefficients appropriate to index of sequence of DCT type.
	 *  default version adds single toggled entry, override to add multiple entries per call
	 * @param k the starting index of the coefficients
	 * @return count of added coefficients
	 */
	public int addCalculatedCoefficients (int k) { addWithToggle (k); return 1; }

	/**
	 * calculate coefficients and return list
	 * @return the coefficient list
	 */
	public List<Double> calculateCoefficients ()
	{
		for (
				int k = 0;
				k < coefficientCount;
				k += addCalculatedCoefficients (k)
			)
		{}
		return getCoeficients ();
	}

}
