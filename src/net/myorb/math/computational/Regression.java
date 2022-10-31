
package net.myorb.math.computational;

import net.myorb.math.expressions.*;
import net.myorb.math.expressions.managers.*;
import net.myorb.math.expressions.evaluationstates.*;
import net.myorb.math.expressions.charting.RegressionCharts;

import net.myorb.math.polynomial.families.chebyshev.ChebyshevPolynomialCalculus;
import net.myorb.math.polynomial.families.ChebyshevPolynomial;

import net.myorb.math.polynomial.OrdinaryPolynomialCalculus;
import net.myorb.math.polynomial.PolynomialSpaceManager;

import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.DataSequence;
import net.myorb.data.abstractions.Function;

import net.myorb.math.*;

import java.io.PrintStream;
import java.util.List;

/**
 * use regression techniques to build a mathematical model for a data set
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class Regression <T> extends Statistics <T>
{


	/**
	 * describe a polynomial model as a result of a regression
	 * @param <T> type of component values on which operations are to be executed
	 */
	public interface Model <T> extends Polynomial.PowerFunction <T>
	{
		/**
		 * full description of the coefficients including meta-data for the transform
		 * @param descriptor the descriptor of the array describing the function domain
		 * @return a dimensioned value wrapped for the value manager
		 */
		ValueManager.DimensionedValue<T> coefficientsWithMetadata (Arrays.Descriptor<T> descriptor);

		/**
		 * get a copy of the adjusted regression data
		 * @return the sequence of data pairs
		 */
		DataSequence2D<T> adjustedDataSet ();

		/**
		 * total Y variation, sum (y(i) - yMean)*2
		 * @return SST
		 */
		T computedSST ();

		/**
		 * total Y regression, sum (yHat(i) - yMean)*2
		 * @return SSR
		 */
		T computedSSR ();

		/**
		 * total Y regression error, sum (y(i) - yHat(i))*2
		 * @return SSE
		 */
		T computedSSE ();

		/**
		 * regression correlation coefficient, 1 - SSE/SST
		 * @return r^2
		 */
		T rSquared ();

		/**
		 * mean square error, SSE / (N - 2)
		 * @return MSE
		 */
		T computedMSE ();

		/**
		 * standard deviation, sqrt (MSE)
		 * @return Standard Deviation
		 */
		T computedStd ();

		/**
		 * coefficient of variation
		 * @return Std-dev / mean
		 */
		T computedCOV ();

		/**
		 * compute the Pearson correlation coefficient
		 * @return the Pearson correlation coefficient for the sample set
		 */
		T pearsonCoefficient ();
	}


	/**
	 * build a library object based on type manager
	 * @param environment central computation control environment
	 */
	public Regression (Environment <T> environment)
	{
		super (environment.getSpaceManager ());
		this.polynomialManager = new OrdinaryPolynomialCalculus <T> (manager);
		this.conversion = environment.getConversionManager ();
		this.environment = environment;
	}
	public Regression (ExpressionSpaceManager <T> manager)
	{
		super (manager);
		this.polynomialManager = new OrdinaryPolynomialCalculus <T> (manager);
		this.conversion = new DataConversions <T> (manager);
	}
	protected Polynomial <T> polynomialManager;
	protected DataConversions <T> conversion;
	protected Environment <T> environment;


	/**
	 * use least squares method to construct a linear model of 2D data points
	 * @param dataSet a sample set of 2 dimensional data points
	 * @return a Model for the data
	 */
	@SuppressWarnings("unchecked")
	public Model<T> leastSquares (DataSequence2D<T> dataSet)
	{
		T z = manager.getZero ();
		DataSequence<T> x = dataSet.xAxis, y = dataSet.yAxis;
		T xMean = mean (x), yMean = mean (y);
		T xySum = z, x2Sum = z;
		
		for (int i = 0; i < x.size(); i++)
		{
			T xDev = subtract (x.get (i), xMean);
			T yDev = subtract (y.get (i), yMean);
			
			xySum = sumOf (xySum, X (xDev, yDev));
			x2Sum = sumOf (x2Sum, squared (xDev));
		}
		
		T b = X (xySum, inverted (x2Sum)), a = subtract (yMean, X (b, xMean));
		return new ModelWrapper<T> (polynomialManager.linearFunctionOfX (b, a), this, dataSet);
	}


	/**
	 * model data set with solution of Y = a*exp(b*X)
	 * @param dataSet a sample set of 2 dimensional data points
	 * @return a Model for the data
	 */
	public Model<T> nonLinear (DataSequence2D<T> dataSet)
	{
		DataSequence2D<T> data =
			new DataSequence2D<T> (dataSet.xAxis, getLnOf (dataSet.yAxis));
		return new NonLinearModelWrapper<T> (leastSquares (data), this, data);
	}


	/**
	 * compute natural log of each data item in a sequence
	 * @param dataSet a sample set of data points to transform
	 * @return a data sequence with log of each source item
	 */
	public DataSequence<T> getLnOf (DataSequence<T> dataSet)
	{
		DataSequence<T> lnData = new DataSequence<T> ();
		transform (dataSet, lnData, getTransform ());
		return lnData;
	}
	Function<T> getTransform ()
	{
		TransformationAbstract<T> t = new TransformationAbstract<T> (manager)
		{ public T f (List<T> x) { return lib.ln (x.get (0)); } };
		t.setLibrary (lib);
		return t;
	}


	/**
	 * build model for power function of specific data set.
	 *  model holds data for display of statistics related to accuracy evaluation.
	 * @param function the power function built from the regression data
	 * @param ofDataSet the data set used to build the model
	 * @return a model object holding function and data
	 */
	public Model<T> newModelFor (Polynomial.PowerFunction<T> function, DataSequence2D<T> ofDataSet)
	{
		return new ModelWrapper<T> (function, null, ofDataSet);
	}


	/**
	 * build model for power function of specific data set
	 * @param coefficients the coefficients of a polynomial power function
	 * @param ofDataSet the data set used to build the model
	 * @return a model object holding function and data
	 */
	public Model<T> newModelFor (Polynomial.Coefficients<T> coefficients, DataSequence2D<T> ofDataSet)
	{ return newModelFor (polynomialManager.getPolynomialFunction (coefficients), ofDataSet); }


	/**
	 * perform polynomial regression on data set
	 *  using Vandermonde solved by matrix inverse (must be small sample set)
	 * @param dataSet a sample set of 2 dimensional data points
	 * @return a Model for the data
	 */
	public Model<T> byPolynomial (DataSequence2D<T> dataSet)
	{
		Polynomial.Coefficients<T> coefficients;
		Vandermonde<T> matrix = new Vandermonde<T> (dataSet.xAxis, manager);
		matrix.inverseSolution (coefficients = new Polynomial.Coefficients<T> (), dataSet.yAxis, manager);
		return newModelFor (coefficients, dataSet);
	}


	/**
	 * apply Gaussian elimination to a Vandermonde matrix.
	 *  this can be applied to larger sample set since determinant need not be computed
	 * @param dataSet the data set to use constructing the Vandermonde matrix
	 * @return the model of the solution
	 */
	public Model<T> byPolynomialUsingGauss (DataSequence2D<T> dataSet)
	{
		Polynomial.Coefficients<T> coefficients;
		Vandermonde<T> matrix = new Vandermonde<T> (dataSet.xAxis, manager);
		matrix.gaussSolution (coefficients = new Polynomial.Coefficients<T> (), dataSet.yAxis, manager);
		return newModelFor (coefficients, dataSet);
	}


	/**
	 * perform Chebyshev Polynomial regression on data set
	 *  using Vandermonde solved by matrix inverse (must be small sample set)
	 * @param dataSet a sample set of 2 dimensional data points
	 * @return a Model for the data
	 */
	public Model<T> byPolynomialUsingChebyshev (DataSequence2D<T> dataSet)
	{
		ChebyshevPolynomial.Coefficients<T> coefficients;
		VandermondeChebyshev<T> matrix = new VandermondeChebyshev<T> (dataSet.xAxis, manager);
		matrix.gaussSolution (coefficients = new ChebyshevPolynomial.Coefficients<T> (), dataSet.yAxis, manager);
		//matrix.inverseSolution (coefficients = new ChebyshevPolynomial.Coefficients<T> (), dataSet.yAxis, manager);
		return useChebyshevModel (coefficients, dataSet);
	}
	public Model<T> useChebyshevModel (ChebyshevPolynomial.Coefficients<T> coefficients, DataSequence2D<T> dataSet)
	{ return newModelFor (new ChebyshevPolynomialCalculus<T> (manager).getPolynomialFunction (coefficients), dataSet); }


	/**
	 * compute interpolation polynomial of data set using Lagrange
	 * @param dataSet a sample set of 2 dimensional data points
	 * @return a Model for the data
	 */
	public Model<T> byLagrange (DataSequence2D<T> dataSet)
	{
		return byLagrange (dataSet, new LagrangeInterpolation<T> (manager));
	}

	public Model<T> byLagrangeCalculus (DataSequence2D<T> dataSet)
	{ return byLagrange (dataSet, new LagrangeInterpolationUsingCalculus<T> (manager)); }
	public Model<T> byLagrange (DataSequence2D<T> dataSet, LagrangeInterpolation<T> interpolationEngine)
	{ return newModelFor (interpolationEngine.forSequence (dataSet), dataSet); }


	/**
	 * build model for general function
	 * @param title a title for the chart and the stats table
	 * @param function the power function being modeled
	 * @param dataSet the data of the regression
	 * @return a model for the regression
	 */
	public Model<T> forGeneralFunction
	(String title, Polynomial.PowerFunction<T> function, DataSequence2D<T> dataSet)
	{
		Regression.Model<T> model = newModelFor (function, dataSet);
		PrintStream out = environment.getOutStream (); out.println (title); out.println (model);
		chartOrdinaryRegression (model, title);
		return model;
	}


	/**
	 * compute the Pearson correlation coefficient
	 * @param dataSet a sample set of 2 dimensional data points
	 * @return the Pearson correlation coefficient
	 */
	@SuppressWarnings("unchecked")
	public T pearsonCoefficient (DataSequence2D<T> dataSet)
	{
		T z = manager.getZero ();
		DataSequence<T> x = dataSet.xAxis, y = dataSet.yAxis;
		int n = x.size (), m = y.size ();
		if (m < n) n = m;
		
		T N = discrete (n);
		T xMean = mean (x), yMean = mean (y);
		T xySum = z, x2Sum = z, y2Sum = z;
		
		for (int i = 0; i < n; i++)
		{
			T xi = x.get (i), yi = y.get (i);
			x2Sum = sumOf (x2Sum, squared (xi));
			y2Sum = sumOf (y2Sum, squared (yi));
			xySum = sumOf (xySum, X (xi, yi));
		}

		try
		{
			T num = subtract (xySum, X (X (xMean, yMean), N));
			T denom = X (sqMinusNmean (x2Sum, xMean, N), sqMinusNmean (y2Sum, yMean, N));
			return X (num, inverted (denom));
		} catch (Exception e)
		{ return manager.getZero (); }
	}
	T sqMinusNmean (T sq, T mean, T n) { return sqrt (subtract (sq, X (n, squared (mean)))); }


	/**
	 * produce chart using ordinary generating function
	 * @param model the model description for the interpolation
	 * @param title a title for the graph
	 */
	public void chartOrdinaryRegression
	(Regression.Model<T> model, String title)
	{
		new RegressionCharts <T> (environment).chartRegression
		(
			model.adjustedDataSet (), conversion.getOrdinaryTransform (model), title
		);
	}


	/**
	 * produce chart using Chebyshev generating function
	 * @param model the model description for the interpolation
	 * @param title a title for the graph
	 */
	public void chartChebyshevRegression
	(Regression.Model<T> model, String title)
	{
		new RegressionCharts <T> (environment).chartRegression
		(
			model.adjustedDataSet (), conversion.getChebyshevTransform (model), title
		);
	}


	/**
	 * a convenience method for producing a regression object
	 *  which is based on double precision floating data points
	 * @return a new regression object
	 */
	public static Regression<Double> newInstance ()
	{
		return new Regression<Double> (new ExpressionFloatingFieldManager ());
	}


}


/**
 * an extended version of the power function wrapper
 *  describing additional regression characteristics pertinent to the building of the model
 * @param <T> type of component values on which operations are to be executed
 */
class ModelWrapper<T> extends Statistics<T> implements Regression.Model<T>
{

	protected Regression<T> regression;
	protected Polynomial.PowerFunction<T> function;
	protected DataSequence2D<T> data;

	public ModelWrapper
	(Polynomial.PowerFunction<T> function, Regression<T> regression, DataSequence2D<T> data)
	{ super (function.getSpaceManager ()); this.function = function; this.regression = regression; this.data = data; }
	public PolynomialSpaceManager<T> getPolynomialSpaceManager () { return function.getPolynomialSpaceManager (); }
	public Polynomial.Coefficients<T> getCoefficients () { return function.getCoefficients (); }
	public SpaceManager<T> getSpaceDescription () { return function.getSpaceManager (); }
	public SpaceManager<T> getSpaceManager () { return function.getSpaceManager (); }
	public Polynomial<T> getPolynomial () { return function.getPolynomial (); }
	public int getDegree () { return function.getDegree (); }
	public T eval (T x) { return function.eval (x); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Regression.Model#coefficientsWithMetadata(net.myorb.math.expressions.evaluationstates.ArrayDescriptor)
	 */
	public ValueManager.DimensionedValue<T> coefficientsWithMetadata (Arrays.Descriptor<T> descriptor)
	{
		ValueManager<T> valueManager = new ValueManager<T> ();
		ValueManager.DimensionedValue<T> coefficients = valueManager.newCoefficientList (function.getCoefficients ());
		if (descriptor == null) coefficients.setMetadata (new TransformConstraints<T> (data, getPolynomial (), getSpaceManager ()));
		else coefficients.setMetadata (descriptor.getDomainConstraints ());
		return coefficients;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Regression.Model#adjustedDataSet()
	 */
	public DataSequence2D<T> adjustedDataSet () { return data; }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Regression.Model#computedSST()
	 */
	public T computedSST ()				// total Y variation, sum (y(i) - yMean)^2
	{
		return sumSquareMeanDeviation (data.yAxis);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Regression.Model#computedSSR()
	 */
	@SuppressWarnings("unchecked")
	public T computedSSR ()				// total Y regression, sum (yHat(i) - yMean)^2
	{
		DataSequence<T> x = data.xAxis, y = data.yAxis;
		T yMean = mean (y), yDev2Sum = manager.getZero ();
		for (int i = 0; i < y.size(); i++)
		{
			T yHat = function.eval (x.get(i)),
				yDev = subtract (yHat, yMean);
			yDev2Sum = sumOf (yDev2Sum, squared (yDev));
		}
		return yDev2Sum;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Regression.Model#computedSSE()
	 */
	@SuppressWarnings("unchecked")
	public T computedSSE ()				// total Y regression error, sum (y(i) - yHat(i))^2
	{
		T yDev2Sum = manager.getZero ();
		DataSequence<T> x = data.xAxis, y = data.yAxis;
		for (int i = 0; i < y.size (); i++)
		{
			T yHat = function.eval (x.get (i)),
				yDev = subtract (y.get (i), yHat);
			yDev2Sum = sumOf (yDev2Sum, squared (yDev));
		}
		return yDev2Sum;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Regression.Model#rSquared()
	 */
	public T rSquared ()				// regression correlation coefficient, 1 - SSE/SST
	{
		return subtract (discrete (1), X (computedSSE (), inverted (computedSST ())));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Regression.Model#computedMSE()
	 */
	public T computedMSE ()				// mean square error, SSE / (N - 2)
	{
		return X (computedSSE (), inverted (discrete (data.xAxis.size () - 2)));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Regression.Model#computedStd()
	 */
	public T computedStd ()				// standard deviation, sqrt (MSE)
	{
		try { return sqrt (computedMSE ()); }
		catch (Exception e) { return manager.getZero (); }
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Regression.Model#computedCOV()
	 */
	public T computedCOV ()				// coefficient of variation, std-dev / mean
	{
		return X (computedStd (), inverted (mean (data.yAxis)));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Regression.Model#pearsonCoefficient()
	 */
	public T pearsonCoefficient ()
	{
		if (regression == null) return manager.getZero ();
		return regression.pearsonCoefficient (data);
	}

	/**
	 * @return Pearson Display (when appropriate)
	 */
	public String pearsonDisplay ()
	{
		if (regression == null) return "";
		return "\nPC  = " + pearsonCoefficient ();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return new StringBuffer ().
		append ("\nr^2 = " + rSquared ()).
		append ("\nSTD = " + computedStd ()).
		append ("\nCOV = " + computedCOV ()).
		append ("\nMSE = " + computedMSE ()).
		append ("\nSSE = " + computedSSE ()).
		append ("\nSSR = " + computedSSR ()).
		append ("\nSST = " + computedSST ()).
		append (pearsonDisplay ()).
		toString();
	}
}


/**
 * additional layer to the polynomial model providing a model for Y = a * e^ bX
 * @param <T> type of component values on which operations are to be executed
 */
class NonLinearModelWrapper<T> extends ModelWrapper<T>
{

	public NonLinearModelWrapper
	(Regression.Model<T> line, Regression<T> regression, DataSequence2D<T> data)
	{
		super (line, regression, data);
	}

	/** 
	 * 
	 * override gives Y = f(X) = exp (c0 + c1*X) = e^ c0 * e^ (c1*X)
	 * 
	 * in the model Y = a * e^ bX
	 *   c0 = ln(a), c1 = b
	 *   
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ModelWrapper#f(java.lang.Object)
	 */
	public T eval (T x) { return lib.exp (function.eval (x)); } 

}


