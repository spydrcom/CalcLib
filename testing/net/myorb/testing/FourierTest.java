
package net.myorb.testing;

import net.myorb.math.complexnumbers.ComplexFieldManager;
import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.computational.*;
import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.MatrixOperations;

import java.util.Date;

/**
 * 
 * run Fourier transform test
 * 
 * @author Michael Druckman
 *
 */
public class FourierTest extends Fourier
{


	/**
	 * execute the test
	 */
	public void runTest ()
	{
		Date start = new Date ();
		TimeSeries seriesData = new TimeSeries ();
		Double omega = frequency (30d);

		addToList
			(
				seriesData, 112d, 114d, 113d, 110d, 106d, 108d, 111d // , 115d // , 117d // , 116d
			);
		Series fourierSeries = constructSeries (seriesData, omega);

		System.out.println ();
		System.out.println ("series =========");
		show (seriesData);

		System.out.println ();
		System.out.println ("coffecients =========");
		show (fourierSeries.getCosCoefficients ());

		System.out.println ();
		System.out.println ("computations =========");
		for (int t = 1; t <= seriesData.size () + 5; t++)
		{
			Double value = fourierSeries.eval ((double)t);
			System.out.println (value);
		}

		Date finish = new Date ();
		long millis = finish.getTime() - start.getTime();

		System.out.println ("---");
		System.out.println (millis + "ms");
		System.out.println (seriesData.size () + " terms");
		System.out.println ("---");
	}


	void buildSeriesTest ()
	{
		addToList (seriesCoefficients, 1d, 2d, 3d, 4d);
		series = buildCosSeries (omega, seriesCoefficients);
		for (int i = 0; i < 512; i++) seriesValues.add (series.eval ((double)i));
	}
	SeriesCoefficients seriesCoefficients = new SeriesCoefficients ();
	TimeSeries seriesValues = new TimeSeries ();
	Double omega = frequency (512.0);
	Series series;


	void runSeriesTest ()
	{
		System.out.println ("--- coef");
		show (seriesCoefficients); System.out.println ("---");
		System.out.println ("seriesValues.length = " + seriesValues.size());
	}


	void fftRawTest (int displaySize)
	{
		double FTvl[] = new double[displaySize];
		double AVal[] = new double[seriesValues.size()];
		
		for (int i=0; i<AVal.length; i++) AVal[i] = seriesValues.get (i);
		new FFT ().runAnalysis (AVal, FTvl, AVal.length, FTvl.length);

		for (int i = 0; i < FTvl.length; i++)
		{
			System.out.println (FTvl[i]);
		}
	}

	void runFftRawTest ()
	{
		System.out.println ("===");
		System.out.println ("start raw test");
		fftRawTest (50); System.out.println ("end raw test");
		System.out.println ("===");
	}

	
	void runFftTest ()
	{
		TimeSeries analysis = new TimeSeries ();
		fillAppendingWith (analysis, 0d, 20);
		FFT.analysis (seriesValues, analysis);
		System.out.println ("--- analysis");
		show (analysis); System.out.println ("---");
	}


	void runAllTests ()
	{
		runTest ();
		buildSeriesTest ();
		runSeriesTest ();
		runFftRawTest ();
		runFftTest ();
	}


	void runDumpTest ()
	{
		buildSeriesTest ();
		dump (seriesValues);
		System.out.println ("--------------------------");
		fftRawTest (512);
	}


	void runDftTest ()
	{
		Matrix<ComplexValue<Double>> vm = new DFT ().getVandermondeMatrix (4);
		ComplexFieldManager<Double> cfm = new ComplexFieldManager<Double> (manager);
		MatrixOperations<ComplexValue<Double>> matrixOperations  = new MatrixOperations<ComplexValue<Double>> (cfm);
		matrixOperations.show (vm);
	}


	/**
	 * execution starting point
	 * @param args not used
	 */
	public static void main (String... args)
	{
		FourierTest ft = new FourierTest ();
		ft.runAllTests ();
		//ft.runDumpTest ();
		//ft.runDftTest ();
	}


}
