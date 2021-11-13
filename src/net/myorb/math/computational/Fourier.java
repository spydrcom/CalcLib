
package net.myorb.math.computational;

import net.myorb.math.matrices.*;
import net.myorb.math.matrices.optimization.MatrixOperationsOptimized;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.DataSequence;
import net.myorb.math.*;

import java.util.List;

/**
 * implement Fourier transforms using matrix solutions
 * @author Michael Druckman
 */
public class Fourier extends OptimizedMathLibrary<Double>
{


	/**
	 * time series data point collection
	 */
	public static class TimeSeries extends DataSequence<Double>
	{
		static final long serialVersionUID = 12l;
	}


	/**
	 * an ordered list of coefficients defines a Fourier series
	 */
	public static class SeriesCoefficients extends Polynomial.Coefficients<Double>
	{
		/*
		 * f(x) = c0*cos(x) + c1*cos(2x) + c2*cos(3x) + c3*cos(4x) + ...
		 */
		static final long serialVersionUID = 11l;
	}


	/**
	 * a Fourier Series is described with a set of coefficients
	 */
	public interface Transform
	{

		/**
		 * get access to the coefficients objects defining this function
		 * @return the coefficients object
		 */
		SeriesCoefficients getCosCoefficients ();
		SeriesCoefficients getSinCoefficients ();

		/**
		 * get the computation mechanism object
		 * @return access to the Fourier object
		 */
		Fourier getFourier ();

		/**
		 * get the omega multiplier setting the base frequency
		 * @return the value of omega for this series
		 */
		Double getOmega ();
		
	}
	public interface Series extends Function<Double>, Transform {}


	/**
	 * trig lib is the super class
	 */
	public Fourier ()
	{
		super (new DoubleFloatingFieldManager ());
		//manager = new DoubleFloatingFieldManager ();
		mo1 = new MatrixOperationsOptimized<Double> (manager);
		mo2 = new MatrixOperationsAccelerated<Double> (manager);
		mo3 = new MatrixOperations<Double> (manager);
		matrixOperations = mo3;
	}
	//protected DoubleFloatingFieldManager manager;
	MatrixOperations<Double> matrixOperations, mo1, mo2, mo3;
	static final Double PI = 3.14159265358979323;


	public double sin (double x) { return Math.sin(x); }
	public double cos (double x) { return Math.cos(x); }


	/**
	 * given period compute the portion of 2PI per times series step
	 * @param period the number of units making 2PI radians
	 * @return 2*PI/period
	 */
	public Double frequency (Double period)
	{
		return 2.0 * PI / period;
	}


	/**
	 * compute the sum of COS terms for a time value
	 * @param coef the vector of coefficients for the set of terms
	 * @param omega the multiplier of time for this calculation
	 * @param t the time step for the calculation
	 * @return the sum of terms
	 */
	public Double computeCosSeriesValue (SeriesCoefficients coef, Double omega, Double t) 
	{
		return computeCosSeriesValue (coef, omega * t);
	}
	public Double computeCosSeriesValue (SeriesCoefficients coef, Double omegaT)
	{
		Double result = 0d;
		for (int n = 1; n <= coef.size (); n++)
		{ result = result + coef.get (n-1) * cos (n * omegaT); }
		return result;
	}


	/**
	 * compute the sum of SIN terms for a time value
	 * @param coef the vector of coefficients for the set of terms
	 * @param omega the multiplier of time for this calculation
	 * @param t the time step for the calculation
	 * @return the sum of terms
	 */
	public Double computeSinSeriesValue (SeriesCoefficients coef, Double omega, Double t) 
	{
		return computeSinSeriesValue (coef, omega * t);
	}
	public Double computeSinSeriesValue (SeriesCoefficients coef, Double omegaT) 
	{
		Double result = 0d;
		for (int n = 1; n <= coef.size (); n++)
		{ result = result + coef.get (n-1) * sin (n * omegaT); }
		return result;
	}


	/**
	 * set a row of a matrix to the harmonics of omega
	 * @param t the time step number which will be the row of the matrix
	 * @param wt the omega*time value to multiply by the harmonic number
	 * @param count the number of harmonics in this series
	 * @param m the matrix being constructed
	 */
	public void addHarmonics (int t, Double wt, int count, Matrix<Double> m)
	{
		for (int n = 1; n <= count; n++) m.set (t, n, cos (n * wt));
	}


	/**
	 * build the constant matrix for these parameters
	 * @param omega the omega time step value for the series
	 * @param harmonics the number of harmonics in the series
	 * @return a constant matrix for this omega/harmonics pair
	 */
	public Matrix<Double> constructMatrix (Double omega, int harmonics)
	{
		Matrix<Double> transform = new Matrix<Double> (harmonics, harmonics, manager);
		for (int t = 1; t <= harmonics; t++) addHarmonics (t, omega*t, harmonics, transform);
		return transform;
	}
	public Matrix<Double> constructInvMatrix (Double omega, int harmonics)
	{ return matrixOperations.inv (constructMatrix (omega, harmonics)); }

	public Matrix<Double> constructMatrix (Double omega, List<Double> xAxis)
	{
		int harmonics = xAxis.size ();
		Matrix<Double> transform = new Matrix<Double> (harmonics, harmonics, manager);
		for (int n = 1; n <= harmonics; n++) addHarmonics (n, omega * xAxis.get (n-1), harmonics, transform);
		return transform;
	}
	public Matrix<Double> constructInvMatrix (Double omega, List<Double> xAxis)
	{ return matrixOperations.inv (constructMatrix (omega, xAxis)); }


	/**
	 * compute the coefficients vector given a set of series values
	 * @param seriesValues the values of the series to be modeled in this equation
	 * @param transform the transformation matrix that will translate the coefficients
	 * @return a Coefficients object containing the model coefficients
	 */
	public SeriesCoefficients computeCoffecients
		(List<Double> seriesValues, Matrix<Double> transform)
	{
		SeriesCoefficients coefficients = new SeriesCoefficients ();
		Matrix<Double> series = matrixOperations.columnMatrix (seriesValues);
		Matrix<Double> coffecients = matrixOperations.product (transform, series);
		coffecients.getCol (1).addToList (coefficients);
		return coefficients;
	}


	/**
	 * apply Gaussian Elimination
	 *       to a harmonic transform matrix
	 * @param seriesValues the result vector of the equation
	 * @param transform the transform matrix
	 * @return the harmonic coefficients
	 */
	public SeriesCoefficients computeCoffecientsForLargeDataSet
		(List<Double> seriesValues, Matrix<Double> transform)
	{
		SeriesCoefficients coefficients = new SeriesCoefficients ();
		Vector<Double> column = new Vector<Double>(manager); column.load (seriesValues);
		VectorAccess<Double> solution = new SimultaneousEquations<Double> (manager).applyGaussianElimination (transform, column);
		new VectorOperations<Double>(manager).addToList (coefficients, solution);
		return coefficients;
	}


	/**
	 * compute the coefficients vector given a set of series values
	 * @param seriesValues the values of the series to be modeled in this equation
	 * @param omega the base frequency of the series to be described by these
	 * @return a Coefficients object containing the model coefficients
	 */
	public SeriesCoefficients computeCoffecients (TimeSeries seriesValues, Double omega)
	{
		int setSize;
		if ((setSize = seriesValues.size ()) > SET_SIZE_THRESHOLD)
			return computeCoffecientsForLargeDataSet (seriesValues, constructMatrix (omega, setSize));
		return computeCoffecients (seriesValues, constructInvMatrix (omega, setSize));
	}
	public SeriesCoefficients computeCoffecients (DataSequence2D<Double> xy, Double omega)
	{
		if (xy.xAxis.size () > SET_SIZE_THRESHOLD)
			return computeCoffecientsForLargeDataSet (xy.yAxis, constructMatrix (omega, xy.xAxis));
		return computeCoffecients (xy.yAxis, constructInvMatrix (omega, xy.xAxis));
	}
	static final int SET_SIZE_THRESHOLD = 5;


	/**
	 * construct a COS series
	 *  that will model the time series data provided
	 * @param seriesValues the time series data to be modeled
	 * @param omega the omega multiplier which will establish the frequency of the function
	 * @return a series object that models the time series data
	 */
	public Series constructSeries (TimeSeries seriesValues, Double omega)
	{
		return buildCosSeries (omega, computeCoffecients (seriesValues, omega));
	}
	public Series constructSeries (DataSequence2D<Double> xy, Double omega)
	{
		return buildCosSeries (omega, computeCoffecients (xy, omega));
	}


	/**
	 * build a function model based on c0*cos(x)+s0*sin(x)+c1*cos(2x)+s1*sin(2x)+...
	 * @param omega the base frequency of the series to be described by these parameters
	 * @param cosSeriesCoefficients a Coefficients object containing the COS contribution coefficients
	 * @param sinSeriesCoefficients a Coefficients object containing the SIN contribution coefficients
	 * @return the series object describing the function
	 */
	public Series buildMixedPhaseSeries
		(
			Double omega,
			SeriesCoefficients cosSeriesCoefficients,
			SeriesCoefficients sinSeriesCoefficients
		)
	{ return new MixedPhaseFourierSeries (this, omega, cosSeriesCoefficients, sinSeriesCoefficients, manager); }

	public Series buildCosSeries (Double omega, SeriesCoefficients cosSeriesCoefficients)
	{ return new MixedPhaseFourierSeries (this, omega, cosSeriesCoefficients, null, manager); }

	public Series buildSinSeries (Double omega, SeriesCoefficients sinSeriesCoefficients)
	{ return new MixedPhaseFourierSeries (this, omega, null, sinSeriesCoefficients, manager); }


}


/**
 * Fourier series allowing odd and even function value contribution
 * s1 * sin (wx) + s2 * sin (2wx) + s3 * sin (3wx) + ... + c1 * cos (wx) + c2 * cos (2wx) + c3 * cos (3wx) + ...
 */
class MixedPhaseFourierSeries implements Fourier.Series
{

	MixedPhaseFourierSeries
		(
			Fourier fourier, Double omega,
			Fourier.SeriesCoefficients cosSeriesCoefficients,
			Fourier.SeriesCoefficients sinSeriesCoefficients,
			SpaceManager<Double> manager
		)
	{
		this.cosSeriesCoefficients = cosSeriesCoefficients;
		this.sinSeriesCoefficients = sinSeriesCoefficients;
		this.omega = omega; this.fourier = fourier;
		this.manager = manager;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#f(java.lang.Object)
	 */
	public Double eval (Double x)
	{
		Double cosContribution =
			cosSeriesCoefficients==null? 0.0:
				fourier.computeCosSeriesValue (cosSeriesCoefficients, omega, x);
		Double sinContribution =
			sinSeriesCoefficients==null? 0.0:
				fourier.computeSinSeriesValue (sinSeriesCoefficients, omega, x);
		return cosContribution + sinContribution;
	}
	Fourier.SeriesCoefficients cosSeriesCoefficients, sinSeriesCoefficients;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Fourier.Series#getCosCoefficients()
	 */
	public Fourier.SeriesCoefficients getCosCoefficients () { return cosSeriesCoefficients; }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Fourier.Series#getSinCoefficients()
	 */
	public Fourier.SeriesCoefficients getSinCoefficients () { return sinSeriesCoefficients; }

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<Double> getSpaceDescription () { return manager; }
	public SpaceManager<Double> getSpaceManager () { return manager; }
	SpaceManager<Double> manager;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Fourier.Series#getFourier()
	 */
	public Fourier getFourier ()
	{ return fourier; }
	Fourier fourier;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Fourier.Series#getOmega()
	 */
	public Double getOmega ()
	{ return omega; }
	Double omega;

}


