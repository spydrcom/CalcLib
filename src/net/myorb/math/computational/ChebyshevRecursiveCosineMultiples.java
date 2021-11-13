
package net.myorb.math.computational;

import net.myorb.data.abstractions.DataSequence;

import java.util.ArrayList;
import java.util.List;

/**
 * utilize Chebyshev recursive angle cosine multiple
 * @author Michael Druckman
 */
public class ChebyshevRecursiveCosineMultiples
{

	/**
	 * full table of multiples of cos(n*PI/N)
	 * @param N the number of segments of PI to tabulate
	 * @return list of cos(n*PI/N) where [0 LE n LE N]
	 */
	public static List<Double> multiplesOfPiOver (int N)
	{
		return multiplesOfCosOf (Math.PI/N, N);
	}

	/**
	 * full table of multiples of cos(n*PI/N)
	 * @param N the number of segments of PI to tabulate
	 * @return sequence of cos(n*PI/N) where [0 LE n LE N]
	 */
	public static DataSequence<Double> sequenceOfPiOver (int N)
	{
		return DataSequence.fromList (multiplesOfCosOf (Math.PI/N, N));
	}

	/**
	 * compute cosine of value and tabulate  cos(n * value)
	 * @param value the original value (n = 1) starting cos (1 * value)
	 * @param N number of multiples to calculate
	 * @return list of multiples
	 */
	public static List<Double> multiplesOfCosOf (double value, int N)
	{
		return multiplesOfCos (Math.cos (value), N);
	}

	/**
	 * compute cosine of multiples of angle cos(n * angle).
	 *  results: list[n] = cos(n * angle) for n=0,1,..,N
	 * @param cos the starting value of cos(1 * angle)
	 * @param N number of multiples to calculate
	 * @return list of multiples
	 */
	public static List<Double> multiplesOfCos (double cos, int N)
	{
		double cosnm1, cosnm2, cosn, twoCos = 2 * cos;
		ArrayList<Double> multiples = new ArrayList<Double>();

		multiples.add (cosnm2 = 1.0);							// cos(0) = 1
		multiples.add (cosnm1 = cos);							// cos value = 1x

//		multiples.add (cosnm2 = twoCos * cos - 1);				// cos 2x = 2cos^2 - 1
//		multiples.add (cosnm1 = twoCos * cosnm2 - cos);			// cos 3x = 4cos^3 - 3cos

		for (int n=2; n<=N; n++)
		{
			multiples.add (cosn = twoCos * cosnm1 - cosnm2);	// cos nt = 2 cos t cos((n-1)t) - cos((n-2)t)
			cosnm2 = cosnm1; cosnm1 = cosn;						// Chebyshev recursive cosine multiple
		}

		return multiples;
	}

}
