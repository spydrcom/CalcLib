
package net.myorb.math.expressions;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.OrdinaryPolynomialCalculus;

import net.myorb.math.polynomial.families.ChebyshevPolynomial;
import net.myorb.math.polynomial.families.HyperGeometricPolynomial;
import net.myorb.math.polynomial.families.chebyshev.ChebyshevPolynomialCalculus;

import net.myorb.math.computational.Combinatorics;
import net.myorb.math.computational.PolynomialRoots;
import net.myorb.math.computational.GaussQuadrature;
import net.myorb.math.computational.Regression;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.matrices.MatrixOperations;
import net.myorb.math.matrices.Matrix;
import net.myorb.math.*;

import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.DataSequence;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * processing of polynomial built-in functions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class BuiltInPolynomialFunctions<T>
{


	public BuiltInPolynomialFunctions
	(Environment<T> environment, PowerLibrary<T> powerLibrary)
	{
		this.spaceManager = (this.environment = environment).getSpaceManager ();
		this.polynomialRoots = new PolynomialRoots<T> (spaceManager, powerLibrary);
		this.polynomialSpaceManager = new PolynomialSpaceManager<T> (spaceManager);
		this.poly = new OrdinaryPolynomialCalculus<T> (spaceManager);
		this.conversion = environment.getConversionManager ();
		this.valueManager = environment.getValueManager ();
		this.powerLibrary = powerLibrary;
	}
	protected ValueManager<T> valueManager;
	protected OrdinaryPolynomialCalculus<T> poly;
	protected ExpressionSpaceManager<T> spaceManager;
	protected PolynomialSpaceManager<T> polynomialSpaceManager;
	protected PolynomialRoots<T> polynomialRoots;
	protected DataConversions<T> conversion;
	protected PowerLibrary<T> powerLibrary;
	protected Environment<T> environment;


	/**
	 * convert array value to polynomial coefficient list
	 * @param polynomialCoefficients the array of coefficient values
	 * @return the list of coefficients
	 */
	public Polynomial.Coefficients<T> getCoefficients
		(ValueManager.GenericValue polynomialCoefficients)
	{
		Polynomial.Coefficients<T> coefficients = new Polynomial.Coefficients<T> ();
		coefficients.addAll (valueManager.toArray (polynomialCoefficients));
		return coefficients;
	}


	/**
	 * wrap coefficients as function return array value
	 * @param coefficients the array of coefficient values
	 * @return a dimensioned value containing the coefficients
	 */
	public ValueManager.GenericValue coefficientsArray (Polynomial.Coefficients<T> coefficients)
	{
		return valueManager.newDimensionedValue (coefficients);
	}


	/**
	 * compute hyper-geometric polynomial coefficients
	 * @param configValues numerator and denominator values
	 * @return polynomial coefficients
	 */
	public ValueManager.GenericValue hypergeometric (ValueManager.GenericValue configValues)
	{
		List<ValueManager.GenericValue> values = ((ValueManager.ValueList) configValues).getValues ();
		Polynomial.Coefficients<T> num = getCoefficients (values.get (0)), denom = getCoefficients (values.get (1));
		HyperGeometricPolynomial<T> hgp = new HyperGeometricPolynomial<T> (environment.getSpaceManager ());
		if (values.size () > 2) { hgp.setTermCount (valueManager.toDiscrete (values.get (2))); }
		return coefficientsArray (hgp.generatePolynomialCoefficientsFor (num, denom));
	}


	/**
	 * compute the derivative of a polynomial
	 * @param polynomialCoefficients the array of coefficient values
	 * @return the list of derivative coefficients
	 */
	public ValueManager.GenericValue derivative
		(ValueManager.GenericValue polynomialCoefficients)
	{
		Polynomial.Coefficients<T> coefficients = getCoefficients (polynomialCoefficients);
		Polynomial.Coefficients<T> derivativeCoefficients = poly.computeDerivativeCoefficients (coefficients);
		return coefficientsArray (derivativeCoefficients);
	}


	/**
	 * compute the derivative of a Chebyshev T polynomial
	 * @param polynomialCoefficients the array of coefficient values
	 * @return the list of derivative coefficients
	 */
	public ValueManager.GenericValue derivativeOfChebyshevT
		(ValueManager.GenericValue polynomialCoefficients)
	{
		Polynomial.Coefficients<T> coefficients = getCoefficients (polynomialCoefficients);
		ChebyshevPolynomialCalculus<T> cpoly = new ChebyshevPolynomialCalculus<T>(environment.getSpaceManager ());
		Polynomial.Coefficients<T> derivativeCoefficients = cpoly.getFirstKindDerivative (coefficients);
		return coefficientsArray (derivativeCoefficients);
	}


	/**
	 * compute the integral of a polynomial
	 * @param polynomialCoefficients the array of coefficient values
	 * @return the list of integral coefficients
	 */
	public ValueManager.GenericValue integral (ValueManager.GenericValue polynomialCoefficients)
	{
		Polynomial.Coefficients<T> coefficients = getCoefficients (polynomialCoefficients);
		Polynomial.Coefficients<T> IntegralCoefficients = poly.computeIntegralCoefficients (coefficients);
		return coefficientsArray (IntegralCoefficients);
	}


	/**
	 * compute the roots of a polynomial
	 * @param polynomialCoefficients the array of coefficient values that define a polynomial
	 * @return the list of computed roots
	 */
	public ValueManager.GenericValue roots (ValueManager.GenericValue polynomialCoefficients)
	{
		Polynomial.Coefficients<T> coefficients = getCoefficients (polynomialCoefficients);
		List<T> computedRoots = polynomialRoots.evaluateEquation (coefficients);
		return valueManager.newDimensionedValue (computedRoots);
	}


	/**
	 * multiply two polynomials
	 * @param polynomialParameters descriptions of two polynomials defined by the coefficients
	 * @return the coefficients of the product polynomial
	 */
	public ValueManager.GenericValue conv (ValueManager.GenericValue polynomialParameters)
	{
		ValueManager.ValueList parameterList =
			(ValueManager.ValueList)polynomialParameters;
		List<ValueManager.GenericValue> values = parameterList.getValues ();
		Polynomial.Coefficients<T> left = getCoefficients (values.get (0)), right = getCoefficients (values.get (1));
		Polynomial.PowerFunction<T> f1 = poly.getPolynomialFunction (left), f2 = poly.getPolynomialFunction (right);
		Polynomial.PowerFunction<T> result = polynomialSpaceManager.multiply (f1, f2);
		return valueManager.newDimensionedValue (result.getCoefficients ());
	}


	/**
	 * divide two polynomials
	 * @param polynomialParameters descriptions of two polynomials defined by the coefficients
	 * @return coefficients of the quotient polynomial
	 */
	public ValueManager.GenericValue deconv (ValueManager.GenericValue polynomialParameters)
	{
		ValueManager.ValueList parameterList =
			(ValueManager.ValueList)polynomialParameters;
		List<ValueManager.GenericValue> values = parameterList.getValues ();
		ValueManager.GenericValue remainderOut = values.size() > 2? values.get (2): null;
		Polynomial.PowerFunction<T> rem = poly.getPolynomialFunction (poly.newCoefficients ()); 
		Polynomial.Coefficients<T> left = getCoefficients (values.get (0)), right = getCoefficients (values.get (1));
		Polynomial.PowerFunction<T> f1 = poly.getPolynomialFunction (left), f2 = poly.getPolynomialFunction (right);
		return processResults (polynomialSpaceManager.divide (f1, f2, rem), rem, remainderOut);
	}
	ValueManager.GenericValue processResults
	(Polynomial.PowerFunction<T> result, Polynomial.PowerFunction<T> rem, ValueManager.GenericValue remainderOut)
	{
		String name;
		if (remainderOut != null && (name = remainderOut.getName ()) != null)
		{ environment.setSymbol (name, valueManager.newDimensionedValue (rem.getCoefficients ())); }
		return valueManager.newDimensionedValue (result.getCoefficients ());
	}


	/**
	 * creat power function from a set of polynomial coefficients
	 * @param polynomialCoefficients the coefficients as a dimensioned value
	 * @return the identified polynomial as a power function
	 */
	public Polynomial.PowerFunction<T> getTransform
	(ValueManager.GenericValue polynomialCoefficients)
	{
		Polynomial.Coefficients<T> coefficients =
			getCoefficients (polynomialCoefficients);
		return poly.getPolynomialFunction (coefficients);
	}

	public Polynomial.PowerFunction<T> getDerivativeTransform
	(ValueManager.GenericValue polynomialCoefficients, int order)
	{
		for (int o = order; o > 0; o--)
			polynomialCoefficients = derivative (polynomialCoefficients);
		return poly.getPolynomialFunction (getCoefficients (polynomialCoefficients));
	}


	/**
	 * apply transform to list of values
	 * @param values the list of values to use as parameters
	 * @param transform the transform to be applied
	 * @return the list of results of the transform
	 */
	public List<T> eval
	(List<T> values, Polynomial.PowerFunction<T> transform)
	{
		List<T> results = new ArrayList<T> ();
		for (T v : values) results.add (transform.eval (v));
		return results;
	}


	/**
	 * special case for matrix as a polynomial parameter
	 * @param x a matrix to be used as the function variable
	 * @param polynomialCoefficients coefficients of the polynomial
	 * @return the function result as a Matrix
	 */
	public ValueManager.GenericValue eval
	(Matrix <T> x, ValueManager.GenericValue polynomialCoefficients)
	{
		return valueManager.newMatrix
		(
			new MatrixOperations <T> (spaceManager)
				.sumOfSeries
				(
					getCoefficients (polynomialCoefficients),
					x
				)
		);
	}


	/**
	 * apply transform to list of values
	 * @param values the list of values to use as parameters
	 * @param polynomialCoefficients coefficients of the polynomial
	 * @return the list of results of the transform
	 */
	public List<T> clenshawEval
	(List<T> values, ValueManager.GenericValue polynomialCoefficients)
	{
		List<T> results = new ArrayList<T> ();
		ChebyshevPolynomial<T> clenshaw = new ChebyshevPolynomial<T> (spaceManager);
		Polynomial.Coefficients<T> a = getCoefficients (polynomialCoefficients);
		for (T v : values) results.add (clenshaw.evaluatePolynomial (a, v));
		return results;
	}

	public ValueManager.GenericValue clenshawDerivative
	(ValueManager.GenericValue polynomialCoefficients, int order)
	{
		for (int o = order; o > 0; o--)
			polynomialCoefficients = derivativeOfChebyshevT (polynomialCoefficients);
		return polynomialCoefficients;
	}


	/**
	 * wrap a list of results
	 *  in appropriate value wrapper
	 * @param results the list of results
	 * @return the generic value, either discrete or dimensioned
	 */
	public ValueManager.GenericValue format (List<T> results)
	{
		if (results.size () == 1)										// check for single element
			return valueManager.newDiscreteValue (results.get (0));		// single element is treated as discrete
		else return valueManager.newDimensionedValue (results);			// other than single, use dimensioned, including empty
	}


	/**
	 * Evaluate a polynomial defined by an array of coefficients
	 * @param polynomialCoefficients an array of coefficients that define the polynomial
	 * @param x the value(s) of X to be evaluated in the polynomial
	 * @return the computed result
	 */
	public ValueManager.GenericValue eval
	(ValueManager.GenericValue polynomialCoefficients, ValueManager.GenericValue x)
	{
		if (valueManager.isMatrix (x))		// special case for matrix
		{ return eval (valueManager.toMatrix (x), polynomialCoefficients); }
		return format (eval (valueManager.toArray (x), getTransform (polynomialCoefficients)));
	}

	public ValueManager.GenericValue evalPrime
	(ValueManager.GenericValue polynomialCoefficients, ValueManager.GenericValue x, int order)
	{ return format (eval (valueManager.toArray (x), getDerivativeTransform (polynomialCoefficients, order))); }


	/**
	 * evaluate an Euler polynomial
	 * @param order the order of polynomial to build
	 * @param x the value of the polynomial parameter
	 * @return the computed value
	 */
	public ValueManager.GenericValue eulerEval
	(ValueManager.GenericValue order, ValueManager.GenericValue x)
	{
		double [] euler = Combinatorics.eulerCoefficients
			(spaceManager.convertToInteger (valueManager.toDiscrete (order)));
		Polynomial.Coefficients <T> coefficients = new Polynomial.Coefficients <T> ();
		for (double c : euler) { coefficients.add (spaceManager.convertFromDouble (c)); }
		return format (eval (valueManager.toArray (x), poly.getPolynomialFunction (coefficients)));
	}


	/**
	 * use Clenshaw special case algorithm to evaluate Chebyshev polynomial
	 * @param polynomialCoefficients the array of coefficients
	 * @param x an array (maybe singleton) of parameters
	 * @return an array of results
	 */
	public ValueManager.GenericValue clenshawEval
	(ValueManager.GenericValue polynomialCoefficients, ValueManager.GenericValue x)
	{ return format (clenshawEval (valueManager.toArray (x), polynomialCoefficients)); }

	public ValueManager.GenericValue clenshawDerivativeEval
	(ValueManager.GenericValue polynomialCoefficients, ValueManager.GenericValue x, int order)
	{ return format (clenshawEval (valueManager.toArray (x), clenshawDerivative (polynomialCoefficients, order))); }


	/**
	 * Clenshaw quadrature provides numerical integration
	 *  of functions approximated by Chebyshev polynomial interpolation
	 * @param parameters a value list holding the coefficient array and an interval
	 * @return the computed integral
	 */
	public ValueManager.GenericValue clenshawQuadrature (ValueManager.GenericValue parameters)
	{
		ValueManager.ValueList parameterList = (ValueManager.ValueList)parameters;
		List<ValueManager.GenericValue> quadratureParameters = parameterList.getValues ();

		if (quadratureParameters.size() == 2)
		{
			return clenshawQuadrature
			(
				getCoefficients (quadratureParameters.get (0)),
				valueManager.toDiscrete (quadratureParameters.get (1))
			);
		}
		else if (quadratureParameters.size() == 3)
		{
			return clenshawQuadrature
			(
				getCoefficients (quadratureParameters.get (0)),
				valueManager.toDiscrete (quadratureParameters.get (1)),
				valueManager.toDiscrete (quadratureParameters.get (2))
			);
		}
		else
		{
			throw new RuntimeException ("Integral is for interval or single value");
		}
	}
	public ValueManager.GenericValue clenshawQuadrature (Polynomial.Coefficients<T> coefficients, T atX)
	{
		ValueManager.GenericValue p = valueManager.newDiscreteValue
			(new ChebyshevPolynomialCalculus<T> (spaceManager).evaluatePolynomialIntegral (coefficients, atX));
		return p;
	}
	public ValueManager.GenericValue clenshawQuadrature
	(Polynomial.Coefficients<T> coefficients, T intervalLo, T intervalHi)
	{
		ValueManager.GenericValue p = valueManager.newDiscreteValue (new ChebyshevPolynomialCalculus<T> (spaceManager)
				.evaluatePolynomialIntegral (coefficients, intervalLo, intervalHi));
		return p;
	}


	/**
	 * Gauss quadrature provides numerical integration
	 *  of functions approximated by Lagrange polynomial interpolation
	 * @param parameters a value list holding the coefficient array
	 * @return the computed integral
	 */
	public ValueManager.GenericValue gaussQuadrature (ValueManager.GenericValue parameters)
	{
		ValueManager.ValueList
			parameterList = (ValueManager.ValueList)parameters;
		List<ValueManager.GenericValue> coordinates = parameterList.getValues ();
		DataSequence<T> xValues = DataSequence.fromList (valueManager.toArray (coordinates.get (0)));
		DataSequence<T> yValues = DataSequence.fromList (valueManager.toArray (coordinates.get (1)));
		DataSequence2D<T> dataSet = new DataSequence2D<T> (xValues, yValues);
		GaussQuadrature<T> quad = new GaussQuadrature<T>(environment);
		return coefficientsArray (quad.computeIntegral (dataSet));
	}


	/**
	 * Evaluate an exponential ( a * exp (b*x) ) defined by an array containing (a, b)
	 * @param polynomialCoefficients an array of coefficients that define the polynomial ( a + b*x )
	 * @param x the value(s) of X to be evaluated in the polynomial
	 * @return the computed result
	 */
	public ValueManager.GenericValue expEval
	(ValueManager.GenericValue polynomialCoefficients, ValueManager.GenericValue x)
	{ return format (exp (eval (valueManager.toArray (x), getTransform (polynomialCoefficients)))); }


	/**
	 * compute exp(x) of each element of a list
	 * @param values the list of values to use as parameters
	 * @return the list of results
	 */
	public List<T> exp (List<T> values)
	{
		for (int i = 0; i < values.size(); i++)
		{ values.set (i, powerLibrary.exp (values.get (i))); }
		return values;
	}


	/**
	 * produce report on regression
	 * @param model the regression model data set
	 * @param leftSide the text to display as left side of equation
	 * @param title a title for the display
	 */
	public void processRegression (Regression.Model<T> model, String leftSide, String title)
	{
		PolynomialSpaceManager<T> psm =
			model.getPolynomial ().getPolynomialSpaceManager ();
		PrintStream out = environment.getOutStream ();

		out.println (); out.println (title);
		out.print ("\n\t "); out.print (leftSide); out.print (" = ");
		out.print (psm.toString (model)); out.println ();
		out.println (model);
	}


}

