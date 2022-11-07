
package net.myorb.math.expressions;

import net.myorb.math.computational.Fourier;
import net.myorb.math.computational.Regression;
import net.myorb.math.expressions.charting.RegressionCharts;
import net.myorb.math.expressions.evaluationstates.Arrays;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.DataSequence;
import net.myorb.math.*;

import java.util.ArrayList;
import java.util.List;

/**
 * implementation of built-in array abstractions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class BuiltInArrayAbstractions<T> extends ArrayFunction<T>
{


	/**
	 * get access to control object from central object store
	 * @param environment the central object store object
	 */
	public BuiltInArrayAbstractions (Environment<T> environment)
	{
		super (environment);
		setPowerLibrary (environment.getLibrary ());
		this.regression = new Regression<T> (environment);
		this.fourier = new Fourier ();
	}
	protected Regression<T> regression;
	protected Fourier fourier;


	/**
	 * identify power library for domain type
	 * @param powerLibrary a library of power functions
	 */
	public void setPowerLibrary (PowerLibrary<T> powerLibrary)
	{
		this.poly = this.matLib = new BuiltInMatrixFunctions<T>
		(environment, this.powerLibrary = powerLibrary);
	}
	protected PowerLibrary<T> powerLibrary;


	/**
	 * get access to polynomial operations
	 * @return the polynomial library object
	 */
	public BuiltInPolynomialFunctions<T> getPolynomialLibrary ()
	{
		return poly;
	}
	protected BuiltInPolynomialFunctions<T> poly;


	/**
	 * get access to matrix operations
	 * @return the matrix library object
	 */
	public BuiltInMatrixFunctions<T> getMatrixLibrary ()
	{
		return matLib;
	}
	protected BuiltInMatrixFunctions<T> matLib;


	/**
	 * Chebyshev polynomial regression
	 * @param data the data subjected to regression
	 * @return the model of the best fit equation
	 */
	public Regression.Model<T> chebyshev (DataSequence2D<T> data)
	{
		Regression.Model<T> model =
			regression.byPolynomialUsingChebyshev (data);
		poly.processRegression (model, "Y", "Chebyshev Interpolation");
		regression.chartChebyshevRegression (model, "Chebyshev Interpolation");
		return model;
	}


	/**
	 * Lagrange polynomial regression
	 * @param data the data subjected to regression
	 * @return the model of the best fit equation
	 */
	public Regression.Model<T> lagrange (DataSequence2D<T> data)
	{
		Regression.Model<T> model = regression.byLagrange (data);
		poly.processRegression (model, "Y", "Lagrange Interpolation");
		regression.chartOrdinaryRegression (model, "Lagrange Interpolation");
		return model;
	}


	/**
	 * Lagrange polynomial regression (calculus implementation)
	 * @param data the data subjected to regression
	 * @return the model of the best fit equation
	 */
	public Regression.Model<T> lagrangeCalculus (DataSequence2D<T> data)
	{
		Regression.Model<T> model = regression.byLagrangeCalculus (data);
		poly.processRegression (model, "Y", "Lagrange (Calculus) Interpolation");
		regression.chartOrdinaryRegression (model, "Lagrange (Calculus) Interpolation");
		return model;
	}


	/**
	 * build polynomial interpolation of function described by array
	 * @param parameters array and metadata holding function description
	 * @return array of polynomial coefficients describing interpolation of function
	 */
	public ValueManager.GenericValue
		arrayInterpolation (ValueManager.GenericValue parameters)
	{ return lagrange (getSeq (parameters)).coefficientsWithMetadata (null); }
	public ValueManager.GenericValue chebyshevInterpolation (ValueManager.GenericValue parameters)
	{ return chebyshev (getSeq (parameters)).coefficientsWithMetadata (null); }
	DataSequence2D<T> getSeq (ValueManager.GenericValue parameters)
	{
		Arrays.Descriptor<T>
			arrayDescriptor = getArrayMetadataFor (parameters);
		DataSequence<T> y = conversion.seq (valueManager.toArray (parameters));
		DataSequence<T> x = conversion.seq (arrayDescriptor.enumerateDomain (y.size ()));
		return new DataSequence2D<T> (x, y);
	}


	/*
	 * regression calculation
	 */


	/**
	 * least squares regression
	 * @param data the data subjected to regression
	 * @return the coefficients of the best fit equation
	 */
	public List<T> fitline (DataSequence2D<T> data)
	{
		Regression.Model<T> model =
			regression.leastSquares (data);
		poly.processRegression (model, "Y", "Linear regression");
		regression.chartOrdinaryRegression (model, "Linear regression");
		return model.getCoefficients ();
	}


	/**
	 * non-linear regression
	 * @param data the data subjected to regression
	 * @return the coefficients of the best fit equation
	 */
	public List<T> fitexp (DataSequence2D<T> data)
	{
		regression.setLibrary (powerLibrary);
		Regression.Model<T> model = regression.nonLinear (data);
		poly.processRegression (model, "Ln Y", "Non-Linear regression");
		regression.chartOrdinaryRegression (model, "Non-Linear regression");
		return model.getCoefficients ();
	}


	/**
	 * Vandermonde polynomial regression
	 * @param data the data subjected to regression
	 * @return the coefficients of the best fit equation
	 */
	public ValueManager.GenericValue fitpoly (DataSequence2D<T> data)
	{
		regression.setLibrary (powerLibrary);
		Regression.Model<T> model = byPolynomial (data);
		poly.processRegression (model, "Y", "Vandermonde Interpolation");
		regression.chartOrdinaryRegression (model, "Vandermonde Interpolation");
		return model.coefficientsWithMetadata (null);
	}
	Regression.Model<T> byPolynomial (DataSequence2D<T> data)
	{
		int size = data.xAxis.size ();
		if (size > 5) return regression.byPolynomialUsingGauss (data);
		else return regression.byPolynomial (data);
	}


	/**
	 * harmonic regression ( X/Y and Time Series )
	 * @param series the data subjected to regression
	 * @param omega the period of the root harmonic of the series
	 * @return the coefficients (for harmonic series) of the best fit equation
	 */
	public List<T> harmonicTimeSeries (Fourier.TimeSeries series, Double omega)
	{
		List<T> result = new ArrayList<T>();
		Fourier.Series regressionSeries =  fourier.constructSeries (series, omega);
		conversion.convertToGeneric (regressionSeries.getCosCoefficients (), result);
		RegressionCharts.harmonicPlot (series, regressionSeries);
		return result;
	}
	public List<T> harmonicTimeSeries (ValueManager.GenericValue sequence, ValueManager.GenericValue omegaParameter)
	{
		Fourier.TimeSeries series = conversion.newTimeSeries (valueManager.toArray (sequence));
		Double omega = spaceManager.convertToDouble (valueManager.toDiscrete (omegaParameter));
		return harmonicTimeSeries (series, omega);
	}
	public List<T> harmonicXyRegression (DataSequence2D<T> xy, ValueManager.GenericValue omegaParameter)
	{
		List<T> result = new ArrayList<T>(); DataSequence2D<Double> xyFloat;
		Double omega = spaceManager.convertToDouble (valueManager.toDiscrete (omegaParameter));
		Fourier.Series regressionSeries =  fourier.constructSeries (xyFloat = conversion.seq2D (xy), omega);
		conversion.convertToGeneric (regressionSeries.getCosCoefficients (), result);
		RegressionCharts.harmonicPlot (xyFloat, regressionSeries);
		return result;
	}
	public List<T> fitharmonic (ValueManager.GenericValue parameters)
	{
		List<ValueManager.GenericValue>
		parameterList = ((ValueManager.ValueList)parameters).getValues ();
		if (parameterList.size () == 3) { return harmonicXyRegression (conversion.seq2D (parameters), parameterList.get (2)); }
		else if (parameterList.size () == 2) return harmonicTimeSeries (parameterList.get (0), parameterList.get (1));
		throw new RuntimeException ("FITHARMONIC requires 2 parameters for time series or 3 parameters for regression");
	}


	/**
	 * evaluate harmonic series
	 * @param coefficients the coefficients of the series
	 * @param t the omega*t value for the evaluation point
	 * @return the value of the series at t
	 */
	public Double evalharmonic (Fourier.SeriesCoefficients coefficients, Double t)
	{
		return fourier.computeCosSeriesValue (coefficients, t);
	}
	public T evalharmonic (ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		Fourier.SeriesCoefficients coefficients =
			conversion.newFourierCoefficients (valueManager.toArray (left));
		Double t = spaceManager.convertToDouble (valueManager.toDiscrete (right));
		return spaceManager.convertFromDouble (evalharmonic (coefficients, t));
	}


	/**
	 * compute Pearson regression coefficient
	 * @param data the data subjected to regression
	 * @return the computed Pearson regression coefficient
	 */
	public T pearson (DataSequence2D<T> data)
	{
		return regression.pearsonCoefficient (data);
	}


}


