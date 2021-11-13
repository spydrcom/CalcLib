
package net.myorb.math.computational.dct;

import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.DataSequence;
import net.myorb.data.abstractions.Function;

import java.util.List;

/**
 * no bias applied for even or odd function
 * @author Michael Druckman
 */
public class UnbiasedCoefficientCalculator extends CoefficientCalculator
{

	public UnbiasedCoefficientCalculator
	(DataSequence2D<Double> fCosPiN) { this (fCosPiN, domainSize (fCosPiN)); }

	public UnbiasedCoefficientCalculator (DataSequence2D<Double> fCosPiN, int N)
	{ super (fCosPiN, N); setCoefficientCount (N); this.N = N; }
	protected int N;

	/**
	 * @return based on half-weighted first and last harmonic values
	 */
	public CoefficientCalculator setStandardTypeIBases ()
	{
		return setBases (functionEvaluations.get (0) / 2, functionEvaluations.get (N) / 2);
	}

	// a#k = 2/pi * INTEGRAL ||(0,pi) f (cos t) cos (kt) dt =
	//       2/N [ f(1)/2 + (-1)^k * f(-1)/2 + SIGMA [ 1 <= n <= N-1 ] ( f(cos(n*pi/N)) * cos(n*k*pi/N) ) ]

	/**
	 * apply harmonic series to function
	 * @param f function must be on interval [-1,1]
	 * @param cosPiN harmonic series cos(k*PI/N)
	 * @return the list of coefficients a#n
	 */
	public static List<Double> computeCoefficients (Function<Double> f, DataSequence<Double> cosPiN)
	{
		DataSequence2D<Double> fCosPiN = DataSequence2D.collectDataFor (f, cosPiN);
		// a#k = 2/N [ f(1)/2 + (-1)^k * f(-1)/2 + SIGMA [ 1 <= n <= N-1 ] ( f(cos(n*pi/N)) * cos(n*k*pi/N) ) ]
		return new UnbiasedCoefficientCalculator (fCosPiN).setStandardTypeIBases ().calculateCoefficients ();
	}

	/**
	 * perform DCT to evaluate function.
	 *  Type I DCT does not force ODD nor EVEN
	 * @param f function must be on interval [-1,1]
	 * @param N the number of samples to be used
	 * @return the list of coefficients a#n
	 */
	public static List<Double> computeCoefficients (Function<Double> f, int N)
	{ return computeCoefficients (f, sequenceOfPiOver (N)); }

}
