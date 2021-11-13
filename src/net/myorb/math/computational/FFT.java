
package net.myorb.math.computational;

import java.util.List;

/**
 * fast fourier transform implementation
 * as documented at https://en.wikipedia.org/wiki/Fast_Fourier_transform
 */
public class FFT extends Fourier
{


	// taken from C++ source posted at 
	//   https://en.wikipedia.org/wiki/Fast_Fourier_transform
	// translated to Java and edited by Michael Druckman


	static final double TwoPi = 2 * PI;


	/**
	 * perform FFT analysis
	 * @param AVal series of values to be analyzed
	 * @param FTvl the resulting analysis series
	 */
	public static void analysis (TimeSeries AVal, List<Double> FTvl) 
	{
		int Nvl = AVal.size(), n = Nvl * 2;
		double Tmvl[] = loadTempArray (AVal, n);
		int i = 1, j = 1;

		while (i <n)
		{
			if (j> i)
			{
				double Tmpr = Tmvl[i]; Tmvl[i] = Tmvl[j]; Tmvl[j] = Tmpr;
				Tmpr = Tmvl[i+1]; Tmvl[i+1] = Tmvl[j+1]; Tmvl[j+1] = Tmpr;
			}

			i = i + 2; 
			int m = Nvl;
			while ((m>= 2) && (j> m))
			{
				j = j - m; m = m>> 2;
			}

			j = j + m;
		}

		new FFT ().iterateOver (Tmvl, FTvl, n);
	}


	/**
	 * allocate the working space.
	 *  compute the space size, allocate from heap, 
	 *  and copy original time series data into the workspace
	 * @param AVal the original time series data
	 * @param n the size of the working space
	 * @return the workspace array
	 */
	public static double[] loadTempArray (TimeSeries AVal, int n)
	{
		int i, j;
		double Tmvl[] = new double[n+1];

		for (i = 0; i <AVal.size(); i++)
		{
			j = i * 2; Tmvl[j] = 0; Tmvl[j+1] = AVal.get(i);
		}

		return Tmvl;
	}


	/**
	 * iterator that works on transform temp space
	 * @param Tmvl the temp work space array building the transform
	 * @param FTvl the output list the transform data is to be written to
	 * @param n the size of the temp space which is twice the input data size
	 */
	public void iterateOver (double Tmvl[], List<Double> FTvl, int n)
	{
		int Mmax = 2;

		while (n> Mmax)
		{
			double Theta = -TwoPi / Mmax, Wpi = sin (Theta);
			double Wtmp = sin (Theta / 2), Wpr = Wtmp * Wtmp * 2;

			int m = 1, Istp = Mmax * 2; 
			double Wr = 1, Wi = 0;

			while (m <Mmax)
			{
				int i = m; m = m + 2;
				double Tmpr = Wr, Tmpi = Wi;
				Wr = Wr - Tmpr * Wpr - Tmpi * Wpi;
				Wi = Wi + Tmpr * Wpi - Tmpi * Wpr;

				while (i <n)
				{
					int j = i + Mmax;
					Tmpr = Wr * Tmvl[j] - Wi * Tmvl[j+1];
					Tmpi = Wi * Tmvl[j] + Wr * Tmvl[j+1];

					Tmvl[j] = Tmvl[i] - Tmpr; Tmvl[j+1] = Tmvl[i+1] - Tmpi;
					Tmvl[i] = Tmvl[i] + Tmpr; Tmvl[i+1] = Tmvl[i+1] + Tmpi;
					i = i + Istp;
				}
			}

			Mmax = Istp;
		}

		formatResults (Tmvl, FTvl);
	}


	/**
	 * compute output and write to output list
	 * @param Tmvl the workspace used for computing transform
	 * @param FTvl the list to write output into
	 */
	public void formatResults (double Tmvl[], List<Double> FTvl)
	{
		int i, j;
		int Nft = FTvl.size ();

		for (i = 1; i <Nft; i++)
		{
			j = i * 2; FTvl.set (i, sqrt (pow (Tmvl[j], 2) + pow (Tmvl[j+1], 2)));
		}
	}


	
	
	/**
	 * original source with minor edits
	 * @param AVal source time series data
	 * @param FTvl fourier transform output array
	 * @param Nvl number of values in source data
	 * @param Nft number of entries in transform
	 */
	public void runAnalysis (double[] AVal, double[] FTvl, int Nvl, int Nft)
	{
		int jmax = 0;
		int i, j, n, m, Mmax, Istp;
		double Tmpr, Tmpi, Wtmp, Theta;
		double Wpr, Wpi, Wr, Wi;
		double[] Tmvl;

		n = Nvl * 2; Tmvl = new double[n+1];

		for (i = 0; i <Nvl; i++) {
			j = i * 2; Tmvl[j] = 0; Tmvl[j+1] = AVal[i];
		}

		i = 1; j = 1;
		while (i <n) {
			if (j> i) {
				Tmpr = Tmvl[i]; Tmvl[i] = Tmvl[j]; Tmvl[j] = Tmpr;
				Tmpr = Tmvl[i+1]; Tmvl[i+1] = Tmvl[j+1]; Tmvl[j+1] = Tmpr;
			}
			i = i + 2; m = Nvl;
			while ((m>= 2) && (j> m)) {
				j = j - m; m = m>> 2;
			}
			j = j + m;
		}

		Mmax = 2;
		while (n> Mmax) {
			Theta = -TwoPi / Mmax; Wpi = sin(Theta);
			Wtmp = sin(Theta / 2); Wpr = Wtmp * Wtmp * 2;
			Istp = Mmax * 2; Wr = 1; Wi = 0; m = 1;

			while (m <Mmax) {
				i = m; m = m + 2; Tmpr = Wr; Tmpi = Wi;
				Wr = Wr - Tmpr * Wpr - Tmpi * Wpi;
				Wi = Wi + Tmpr * Wpi - Tmpi * Wpr;

				while (i <n) {
					j = i + Mmax; 
					if (j > jmax) jmax = j;
					Tmpr = Wr * Tmvl[j] - Wi * Tmvl[j+1];
					Tmpi = Wi * Tmvl[j] + Wr * Tmvl[j+1];

					Tmvl[j] = Tmvl[i] - Tmpr; Tmvl[j+1] = Tmvl[i+1] - Tmpi;
					Tmvl[i] = Tmvl[i] + Tmpr; Tmvl[i+1] = Tmvl[i+1] + Tmpi;
					i = i + Istp;
				}
			}

			Mmax = Istp;
		}

		for (i = 1; i <Nft; i++) {
			j = i * 2; FTvl[i] = sqrt (pow (Tmvl[j], 2) + pow (Tmvl[j+1], 2));
		}
	}



}

