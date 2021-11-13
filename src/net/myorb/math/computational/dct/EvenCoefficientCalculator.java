
package net.myorb.math.computational.dct;

import net.myorb.math.computational.TransformExtensions;

import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.DataSequence;
import net.myorb.data.abstractions.Function;

import java.util.List;

/**
 * force even function characteristics
 * @author Michael Druckman
 */
public class EvenCoefficientCalculator extends CoefficientCalculator
{

	public EvenCoefficientCalculator
	(DataSequence2D<Double> fCosPiN) { this (fCosPiN, domainSize (fCosPiN)); }

	public EvenCoefficientCalculator (DataSequence2D<Double> fCosPiN, int N)
	{ super (fCosPiN, N / 2); setCoefficientCount (N); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.dct.CoefficientCalculator#addCalculatedCoefficients(int)
	 */
	public int addCalculatedCoefficients (int k) { addWithToggle (k); add (0.0); return 2; }

	// a#(2*k) =  { for even number (N) of values }
	//       2/N * [ ( f(1) + f(-1) ) /2 + (-1)^k * f(0) + SIGMA [ 1 <= n <= N/2 - 1 ] ( { f(cos(n*pi/N))+ f(-cos(n*pi/N)) } * cos(2*n*k*pi/N) ) ]

	/**
	 * apply harmonic series to function
	 * @param f function on interval [-1,1]
	 * @param cosPiN harmonic series cos(k*PI/N)
	 * @return the list of coefficients a#2k
	 */
	public static List<Double> computeCoefficients (Function<Double> f, DataSequence<Double> cosPiN)
	{
		Function<Double> fSmp = TransformExtensions.getSmpFunction (f);
		DataSequence2D<Double> fCosPiN = DataSequence2D.collectDataFor (fSmp, cosPiN.halfSequence ()); fCosPiN.xAxis = cosPiN;
		// a#2k = 2/N * [ ( f(1) + f(-1) ) /2 + (-1)^k * f(0) + SIGMA [ 1 <= n <= N/2 - 1 ] ( { f(cos(n*pi/N))+ f(-cos(n*pi/N)) } * cos(2*n*k*pi/N) ) ]
		return new EvenCoefficientCalculator (fCosPiN).setBases (fSmp.eval (1.0) / 2, f.eval (0.0)).calculateCoefficients ();
	}

	/**
	 * a[2k] coefficients
	 *  used as shortcut for CCQ.
	 *  Type II DCT provides EVEN function
	 * @param f function on interval [-1,1]
	 * @param N the number of samples to be used
	 * @return the list of coefficients a#2k
	 */
	public static List<Double> computeCoefficients (Function<Double> f, int N)
	{ return computeCoefficients (f, sequenceOfPiOver (N)); }

}
