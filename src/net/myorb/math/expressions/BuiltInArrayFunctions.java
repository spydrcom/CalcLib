
package net.myorb.math.expressions;

import net.myorb.math.expressions.symbols.IterationConsumerImplementations;
import net.myorb.math.expressions.symbols.IterationConsumer;

import net.myorb.math.expressions.ValueManager.GenericValue;

import net.myorb.math.expressions.evaluationstates.*;
import net.myorb.math.expressions.symbols.*;
import net.myorb.math.computational.*;
import net.myorb.math.matrices.*;
import net.myorb.math.*;

import java.util.ArrayList;
import java.util.List;

/**
 * implementation of built-in array functions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class BuiltInArrayFunctions<T> extends BuiltInFunctions<T>
{


	/**
	 * type manager used to evaluate computations
	 * @param environment access to the evaluation environment
	 */
	public BuiltInArrayFunctions (Environment<T> environment)
	{
		super (environment);
		this.vectorOperations = new VectorOperations<T> (spaceManager);
		this.abstractions = new BuiltInArrayAbstractions<T> (environment);
		this.conversion = new ExtendedDataConversions<T> (environment);
		this.functions = new ArrayFunction<T> (environment);
		this.stats = new Statistics<T>(spaceManager);
	}
	protected BuiltInArrayAbstractions<T> abstractions = null;
	protected ExtendedDataConversions<T> conversion = null;
	protected VectorOperations<T> vectorOperations = null;
	protected ArrayFunction<T> functions = null;
	protected Statistics<T> stats = null;


	/**
	 * look for dimensioned data
	 * @param left left side of operator
	 * @param right right side of operator
	 * @return TRUE = dimensioned parameter is present
	 */
	public boolean dimensionedParameterPresent
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		return valueManager.isDimensioned (left) || valueManager.isDimensioned (right);
	}


	/*
	 * implementation of arithmetic operators applied to arrays
	 */


	/**
	 * negate the elements of a vector
	 * @param v the array to treat as a vector
	 * @return the elements of the negated vector
	 */
	public ValueManager.RawValueList<T> vectorNegate (ValueManager.GenericValue v)
	{
		return conversion.toArray (conversion.toVector (v).negate ());
	}

	/**
	 * addition of two vectors
	 * @param left the left side of the operator
	 * @param right the right side of the operator
	 * @return the elements of the sum vector
	 */
	public ValueManager.RawValueList<T> vectorAdd (List<T> left, List<T> right)
	{
		return conversion.toArray (conversion.toVector (left).plus (conversion.toVector (right)));
	}

	/**
	 * multiply vector by scalar
	 * @param scalar the discrete value of the scalar
	 * @param v the generic array value to treat as a vector
	 * @return the scaled vector converted to an array value
	 */
	@SuppressWarnings("unchecked")
	public ValueManager.GenericValue vectorScale (T scalar, ValueManager.GenericValue v)
	{
		ValueManager.Metadata metadata;
		List<T> array = conversion.toArray (conversion.toVector (v).times (scalar));
		ValueManager.GenericValue result = valueManager.newDimensionedValue (array);
		if ((metadata = v.getMetadata ()) != null)
		{
			if (metadata instanceof Arrays.Descriptor)
			{
				String op = spaceManager.toDecimalString (scalar) + " *";
				Arrays.Descriptor<T> dsc = (Arrays.Descriptor<T>) metadata;
				metadata = functions.vectorOpMetadata (op, array, dsc);
			}
			result.setMetadata (metadata);
		}
		return result;
	}

	/**
	 * vector cross product
	 *  operation implementation
	 * @param left the left side of the operator
	 * @param right the right side of the operator
	 * @return cross product result
	 */
	public ValueManager.GenericValue crossProduct
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		List<T> array = conversion.toArray
		(
			vectorOperations.crossProduct
			(
					conversion.toVector (left),
					conversion.toVector (right)
			)
		);
		return valueManager.newDimensionedValue (array);
	}

	/**
	 * multiplication operations on arrays/vectors.
	 *  identifies as vector scale or cross product
	 * @param left the left side of the operator
	 * @param right the right side of the operator
	 * @return the product array as a value
	 */
	public ValueManager.GenericValue vectorMultiply
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		ValueManager.GenericValue d = left, a = left;

		if (valueManager.isDiscrete (left)) a = right;
		else if (valueManager.isDiscrete (right)) d = right;
		else return crossProduct (left, right);

		T discrete = valueManager.toDiscrete (d);
		return vectorScale (discrete, a);
	}


	/*
	 * vector dot product and cross product operators
	 */


	/**
	 * vector dot product of two arrays
	 * @param parameters stack constructed parameter object
	 * @return the scalar dot product
	 */
	public ValueManager.GenericValue dot (ValueManager.GenericValue parameters)
	{
		ValueManager.ValueList parameterList = (ValueManager.ValueList)parameters;
		List<ValueManager.GenericValue> values = parameterList.getValues ();
		return dot (values.get(0), values.get(1));
	}


	/**
	 * vector dot product of two arrays
	 * @param left left side array reference
	 * @param right right side array reference
	 * @return computed dot product
	 */
	public ValueManager.GenericValue dot
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		Vector<T> lParm = conversion.toVector (valueManager.toArray (left));
		Vector<T> rParm = conversion.toVector (valueManager.toArray (right));
		T result = vectorOperations.dotProduct (lParm, rParm);
		return valueManager.newDiscreteValue (result);
	}


	/**
	 * dyadic product of vectors
	 * @param parameters stack constructed parameter object
	 * @return computed matrix
	 */
	public Matrix<T> dyadicProduct (ValueManager.GenericValue parameters)
	{
		ValueManager.ValueList
			parameterList = (ValueManager.ValueList)parameters;
		List<ValueManager.GenericValue> values = parameterList.getValues ();
		Vector<T> l = conversion.toVector (valueManager.toArray (values.get (0)));
		Vector<T> r = conversion.toVector (valueManager.toArray (values.get (1)));
		return vectorOperations.dyadicProduct (l, r);
	}


	/*
	 * general primitive processing for arrays
	 */


	/**
	 * array element indexing
	 * @param left the reference to the array
	 * @param right the index value identifying element
	 * @return the value of the specified element
	 */
	public ValueManager.GenericValue index (ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		if (valueManager.isMatrix (left))
			return abstractions.getMatrixLibrary ().index (valueManager.toMatrix (left), right);
		List<T> array = valueManager.toArray (left); int index = valueManager.toInt (right, spaceManager);
		return valueManager.newDiscreteValue (array.get (index));
	}


	/**
	 * Append a series of arrays into one long array
	 * @param values the series of arrays to be concatenated
	 * @return the array with all values appended
	 */
	public ValueManager.GenericValue append (ValueManager.GenericValue values)
	{
		List<T> result = new ArrayList<T> ();
		ValueManager.ValueList parameterList = (ValueManager.ValueList)values;
		List<ValueManager.GenericValue> arrays = parameterList.getValues ();
		for (ValueManager.GenericValue array : arrays)
		{
			result.addAll (valueManager.toArray (array));
		}
		return valueManager.newDimensionedValue (result);
	}


	/**
	 * standard mathematical SIGMA function.
	 *  the sum of all elements of the array are computed
	 * @param values the values that comprise the array
	 * @return the scalar sum of elements
	 */
	public ValueManager.GenericValue sigma (ValueManager.GenericValue values)
	{
		T sum = spaceManager.getZero ();
		List<T> array = valueManager.toArray (values);
		for (T item : array) { sum = spaceManager.add (sum, item); }
		return valueManager.newDiscreteValue (sum);
	}


	/**
	 * standard mathematical PI function.
	 *  the product of all elements of the array are computed
	 * @param values the values that comprise the array
	 * @return the scalar product of elements
	 */
	public ValueManager.GenericValue pi (ValueManager.GenericValue values)
	{
		T product = spaceManager.getOne ();
		List<T> array = valueManager.toArray (values);
		for (T item : array) { product = spaceManager.multiply (product, item); }
		return valueManager.newDiscreteValue (product);
	}


	/**
	 * get the length of an array
	 * @param values the parameter passed to the function
	 * @return length of the array
	 */
	public ValueManager.GenericValue length (ValueManager.GenericValue values)
	{
		List<T> array = valueManager.toArray (values);
		T l = spaceManager.newScalar (array.size ());
		return valueManager.newDiscreteValue (l);
	}


	/**
	 * wrapper for iteration consumer objects used by expression tree range descriptors
	 */
	public static abstract class AbstractParameterizedConsumer
			extends AbstractParameterizedFunction
			implements IterationConsumer
	{

		protected AbstractParameterizedConsumer (String name)
		{ super (name); iterationConsumer = getIterationConsumer (); }
		public void accept (GenericValue value) { iterationConsumer.accept (value); }
		public void setIterationValue (GenericValue value) { iterationConsumer.setIterationValue (value); }
		public GenericValue getCalculatedResult () { return iterationConsumer.getCalculatedResult (); }
		public void setCurrentValue (GenericValue currentValue) {}
		public abstract IterationConsumer getIterationConsumer ();
		public void init () { iterationConsumer.init (); }
		protected IterationConsumer iterationConsumer;

	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.BuiltInFunctions#importFromSpaceManager(net.myorb.math.expressions.SymbolMap)
	 */
	public void importFromSpaceManager (SymbolMap into)
	{
		into.add
		(
			new AbstractParameterizedFunction (LENGTH_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return length (parameters); } },
			"Length of an array treated as a unary function"
		);
		into.add
		(
			new AbstractParameterizedFunction (INTERVAL_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return abstractions.arrayInterval (parameters); } },
			"Select sub-list of elements for interval lo-hi"
		);
		into.add
		(
			new AbstractParameterizedFunction (APPEND_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return append (parameters); } },
			"Append a series of arrays into one long array"
		);
		into.add
		(
			new AbstractParameterizedConsumer (SIGMA_OPERATOR)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return sigma (parameters); }
				public IterationConsumer getIterationConsumer () { return IterationConsumerImplementations.getSummationIterationConsumer (spaceManager); }
			},
			"Sum of items of an array, traditional capital SIGMA notation"
		);
		into.add
		(
			new AbstractParameterizedConsumer (INTEGRAL_OPERATOR)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return sigma (parameters); }
				public IterationConsumer getIterationConsumer () { return IterationConsumerImplementations.getSummationIterationConsumer (spaceManager); }
			},
			"Sum of items of an array constructed based on delta terms, using traditional INTEGRAL notation"
		);
		into.add
		(
			new AbstractParameterizedConsumer (SUMMATION_OPERATOR)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return sigma (parameters); }
				public IterationConsumer getIterationConsumer () { return IterationConsumerImplementations.getSummationIterationConsumer (spaceManager); }
			},
			"Sum of items of an array, summation functionality using SIGMA notation"
		);
		into.add
		(
			new AbstractParameterizedConsumer (PI_OPERATOR)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return pi (parameters); }
				public IterationConsumer getIterationConsumer () { return IterationConsumerImplementations.getProductIterationConsumer (spaceManager); }
			},
			"Product of items of an array, traditional capital PI notation"
		);
		into.add
		(
			new AbstractParameterizedFunction (MAX_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
				{ return valueManager.newDiscreteValue (stats.max (conversion.seq (parameters))); }
			}, "Maximum value found in array"
		);
		into.add
		(
			new AbstractParameterizedFunction (MIN_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
				{ return valueManager.newDiscreteValue (stats.min (conversion.seq (parameters))); }
			}, "Minimum value found in array"
		);
		into.add
		(
			new AbstractParameterizedFunction (PEARSON_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
				{ return valueManager.newDiscreteValue (abstractions.pearson (conversion.seq2D (parameters))); }
			}, "Compute Pearson regression coefficient for X/Y data set pair"
		);
		into.add
		(
			new AbstractParameterizedFunction (FITLINE_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
				{ return valueManager.newDimensionedValue (abstractions.fitline (conversion.seq2D (parameters))); }
			}, "Apply least squares regression to find line of best fit"
		);
		into.add
		(
			new AbstractParameterizedFunction (FITEXP_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
				{ return valueManager.newDimensionedValue (abstractions.fitexp (conversion.seq2D (parameters))); }
			}, "Apply non-linear (logarithmic) regression to find curve of best fit"
		);
		into.add
		(
			new AbstractParameterizedFunction (FITPOLY_FUNCTION)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameters)
				{ return abstractions.fitpoly (conversion.seq2D (parameters)); }
			}, "Apply Vandermonde matrix to solve for polynomial coefficients to find curve of best fit"
		);
		into.add
		(
			new AbstractParameterizedFunction (LAGRANGE_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
				{ return abstractions.lagrange (conversion.seq2D (parameters)).coefficientsWithMetadata (null); }
			}, "Apply Lagrange series to derive interpolation polynomial as curve of best fit"
		);
		into.add
		(
			new AbstractParameterizedFunction (CHEBYSHEV_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
				{ return abstractions.chebyshev (conversion.seq2D (parameters)).coefficientsWithMetadata (null); }
			}, "Apply Vandermonde matrix to solve for Chebyshev interpolation polynomial as curve of best fit"
		);
		into.add
		(
			new AbstractParameterizedFunction (FITHARMONIC_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
				{ return valueManager.newDimensionedValue (abstractions.fitharmonic (parameters)); }
			}, "Apply harmonic series regression to find curve of best fit"
		);
		into.add
		(
			new AbstractParameterizedFunction (DYADIC_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
				{ return valueManager.newMatrix (dyadicProduct (parameters)); }
			}, "Compute dyadic product of 2 arrays"
		);
		into.add
		(
			new AbstractParameterizedFunction (DOT_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return dot (parameters); } },
			"Dot product of two arrays, lengths of arrays must match"
		);
		into.add
		(
			new AbstractBinaryOperator (DOT_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
					(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{ return dot (left, right); }
			},
			"Dot product of two arrays, lengths of arrays must match"
		);
		into.add
		(
			new AbstractBinaryOperator (INDEXING_OPERATOR, SymbolMap.INDEX_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
					(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{ return index (left, right); }
			},
			"Array indexing operation implemented as a binary operator"
		);

		// identify other imported libraries
		super.importFromSpaceManager (into);
		this.importPrimeFunctions (into);
		this.importStatFunctions (into);
	}


	/**
	 * prime number functions that operate on arrays
	 * @param into the symbol map object collecting imported symbols
	 */
	public void importPrimeFunctions (SymbolMap into)
	{
		into.add
		(
			new AbstractParameterizedFunction (PRIMES_FUNCTION)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue parameters) { return abstractions.primes (parameters); }
			}, "Get an array of all primes less than parameter"
		);
		into.add
		(
			new AbstractParameterizedFunction (FACTORS_FUNCTION)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue parameters) { return abstractions.factors (parameters); }
			}, "Get an array of all prime factors of the parameter"
		);
		into.add
		(
			new AbstractParameterizedFunction (GCF_FUNCTION)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue parameters) { return abstractions.gcf (parameters); }
			}, "Greatest common factor of two integers"
		);
		into.add
		(
			new AbstractParameterizedFunction (LCM_FUNCTION)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue parameters) { return abstractions.lcm (parameters); }
			}, "Least common multiple of two integers"
		);
	}


	/**
	 * statistical functions that operate on arrays
	 * @param into the symbol map object collecting imported symbols
	 */
	public void importStatFunctions (SymbolMap into)
	{
		into.add
		(
			new AbstractParameterizedFunction (MEAN_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
				{ return valueManager.newDiscreteValue (stats.mean (conversion.seq (parameters))); }
			}, "Mean of an array of values"
		);
		into.add
		(
			new AbstractParameterizedFunction (MEDIAN_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
				{ return valueManager.newDiscreteValue (stats.median (conversion.seq (parameters))); }
			}, "Median of an array of values"
		);
		into.add
		(
			new AbstractParameterizedFunction (MODE_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
				{ return valueManager.newDiscreteValue (stats.mode (conversion.seq (parameters))); }
			}, "Mode of an array of values"
		);
		into.add
		(
			new AbstractParameterizedFunction (STDEV_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
				{ return valueManager.newDiscreteValue (stats.stdDev (conversion.seq (parameters))); }
			}, "Standard deviation of an array of values"
		);
		into.add
		(
			new AbstractParameterizedFunction (VARIANCE_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
				{ return valueManager.newDiscreteValue (stats.variance (conversion.seq (parameters))); }
			}, "Computed variance of an array of values"
		);
		into.add
		(
			new AbstractParameterizedFunction (COV_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
				{ return valueManager.newDiscreteValue (stats.cov (conversion.seq (parameters))); }
			}, "Computed covariance of an array of values"
		);
	}


	/**
	 * import symbols found in power library into map
	 * @param powerLibrary the power library object to use for power functions
	 * @param into the symbol map object collecting imported symbols
	 */
	public void importFromPowerLibrary (PowerLibrary<T> powerLibrary, SymbolMap into)
	{
		abstractions.setPowerLibrary (powerLibrary);
		importFromPolynomialLibrary (abstractions.getPolynomialLibrary (), into);
		importMatrixFunctions (abstractions.getMatrixLibrary (), into);
	}


	/**
	 * import symbols found in polynomial library into map
	 * @param poly the polynomial library object to use for Polynomial functions
	 * @param into the symbol map object collecting imported symbols
	 */
	public void importFromPolynomialLibrary (final BuiltInPolynomialFunctions<T> poly, SymbolMap into)
	{
		into.add
		(
			new AbstractBinaryOperator (ARRAY_EVAL_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
					(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{ return abstractions.arrayFunctionEval (left, right); }
			},
			"Evaluate function defined by array at X"
		);
		into.add
		(
			new AbstractBinaryOperator (POLY_EVAL_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
					(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{ return poly.eval (left, right); }
			},
			"Evaluate a polynomial defined by an array of coefficients ( c0 + c1*x + c2*x^2 + ... )"
		);
		into.add
		(
			new AbstractBinaryOperator (CLENSHAW_EVAL_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
					(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{ return poly.clenshawEval (left, right); }
			},
			"Evaluate a Chebyshev polynomial using Clenshaw's special case" +
			" defined by an array of coefficients ( c0 + c1*T[1](x) + c2*T[2](x) + ... )"
		);
		into.add
		(
			new AbstractBinaryOperator (CLENSHAW_PRIME_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
					(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{ return poly.clenshawDerivativeEval (left, right, 1); }
			},
			"Evaluate a Chebyshev polynomial derivative using Clenshaw's special case"
		);
		into.add
		(
			new AbstractBinaryOperator (CLENSHAW_DPRIME_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
					(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{ return poly.clenshawDerivativeEval (left, right, 2); }
			},
			"Evaluate a Chebyshev polynomial second derivative using Clenshaw's special case"
		);
		into.add
		(
			new AbstractBinaryOperator (EXP_EVAL_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
					(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{ return poly.expEval (left, right); }
			},
			"Evaluate an exponential ( a * exp (b * x) ) defined by an array containing (a, b)"
		);
		into.add
		(
			new AbstractBinaryOperator (HAR_EVAL_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
					(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{ return valueManager.newDiscreteValue (abstractions.evalharmonic (left, right)); }
			},
			"Evaluate a harmonic series defined by an array of coefficients ( c0 + c1*cos(x) + c2*cos(2x) + ... )"
		);
		into.add
		(
			new AbstractParameterizedFunction (POLYHG_FUNCTION)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameters)
				{ return poly.hypergeometric (parameters); }
			}, "Compute coefficients for hyper geometric polynomial"
		);
		into.add
		(
			new AbstractParameterizedFunction (POLYINT_FUNCTION)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameters)
				{ return poly.integral (parameters); }
			}, "Compute integral of polynomial"
		);
		into.add
		(
			new AbstractParameterizedFunction (POLYDER_FUNCTION)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameters)
				{ return poly.derivative (parameters); }
			}, "Compute derivative of polynomial"
		);
		into.add
		(
			new AbstractParameterizedFunction (CHEBDER_FUNCTION)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameters)
				{ return poly.derivativeOfChebyshevT (parameters); }
			}, "Compute derivative of Chebyshev T polynomial"
		);
		into.add
		(
			new AbstractParameterizedFunction (ARRAYINT_FUNCTION)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameters)
				{ return abstractions.arrayIntegral (parameters); }
			}, "Compute integral of function described by array"
		);
		into.add
		(
			new AbstractParameterizedFunction (ARRAYDER_FUNCTION)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameters)
				{ return abstractions.arrayDerivative (parameters); }
			}, "Compute derivative of function described by array"
		);
		into.add
		(
			new AbstractParameterizedFunction (INTERPOLATE_FUNCTION)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameters)
				{ return abstractions.arrayInterpolation (parameters); }
			}, "Generate Lagrange interpolation polynomial for function described by array"
		);
		into.add
		(
			new AbstractParameterizedFunction (CHEBINTERP_FUNCTION)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameters)
				{ return abstractions.chebyshevInterpolation (parameters); }
			}, "Generate Chebyshev interpolation polynomial for function described by array"
		);
		into.add
		(
			new AbstractParameterizedFunction (CLENQUAD_FUNCTION)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameters)
				{ return poly.clenshawQuadrature (parameters); }
			}, "Compute integral of function described by Chebyshev polynomial"
		);
		into.add
		(
			new AbstractParameterizedFunction (CONV_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return poly.conv (parameters); }
			}, "Compute product of polynomials"
		);
		into.add
		(
			new AbstractParameterizedFunction (DECONV_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return poly.deconv (parameters); }
			}, "Compute quotient of polynomials"
		);
		into.add
		(
			new AbstractParameterizedFunction (ROOTS_KEYWORD)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return poly.roots (parameters); }
			}, "Compute roots of polynomial"
		);
	}


	/**
	 * matrix functions
	 * @param mat object implementing matrix functionality
	 * @param into the symbol map object collecting imported symbols
	 */
	public void importMatrixFunctions (final BuiltInMatrixFunctions<T> mat, SymbolMap into)
	{
		into.add
		(
			new AbstractBinaryOperator (DIAG_INDEX_OPERATOR, SymbolMap.INDEX_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
					(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{ return mat.diagIndex (left, right); }
			},
			"Matrix indexing operation selecting diag vector"
		);
		into.add
		(
			new AbstractBinaryOperator (ROW_INDEX_OPERATOR, SymbolMap.INDEX_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
					(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{ return mat.rowIndex (left, right); }
			},
			"Matrix indexing operation selecting row vector"
		);
		into.add
		(
			new AbstractBinaryOperator (COL_INDEX_OPERATOR, SymbolMap.INDEX_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
					(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{ return mat.colIndex (left, right); }
			},
			"Matrix indexing operation selecting column vector"
		);
		into.add
		(
			new AbstractBinaryOperator (TENSOR_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
					(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{ return mat.tensor (left, right); }
			},
			"Compute tensor product of two matrices"
		);
		into.add
		(
			new AbstractParameterizedFunction (MATRIX_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.newMatrix (parameters); } },
			"Construct matrix from array with dimensions"
		);
		into.add
		(
			new AbstractParameterizedFunction (ROW_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.row (parameters); } },
			"Read row vector from matrix"
		);
		into.add
		(
			new AbstractParameterizedFunction (COLUMN_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.column (parameters); } },
			"Read column vector from matrix"
		);
		into.add
		(
			new AbstractParameterizedFunction (MATMUL_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.matmul (parameters); } },
			"Compute product of two matrices"
		);
		into.add
		(
			new AbstractParameterizedFunction (MATADD_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.matadd (parameters); } },
			"Compute sum of two matrices"
		);
		into.add
		(
			new AbstractParameterizedFunction (DET_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.det (parameters); } },
			"Compute determinant of matrix"
		);
		into.add
		(
			new AbstractParameterizedFunction (TRACE_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.trace (parameters); } },
			"Compute trace of matrix"
		);
		into.add
		(
			new AbstractParameterizedFunction (INV_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.inv (parameters); } },
			"Compute inverse of matrix"
		);
		into.add
		(
			new AbstractParameterizedFunction (IDENTITY_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.identity (parameters); } },
			"Compute identity matrix with specified size"
		);
		into.add
		(
			new AbstractParameterizedFunction (MINOR_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.minor (parameters); } },
			"Compute minor of matrix"
		);
		into.add
		(
			new AbstractParameterizedFunction (ADJ_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.adj (parameters); } },
			"Compute adjugate of matrix"
		);
		into.add
		(
			new AbstractParameterizedFunction (COFACTOR_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.cofactor (parameters); } },
			"Compute cofactor matrix from source"
		);
		into.add
		(
			new AbstractParameterizedFunction (COMATRIX_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.comatrix (parameters); } },
			"Compute comatrix matrix from source"
		);
		into.add
		(
			new AbstractParameterizedFunction (TRIU_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.triu (parameters); } },
			"Compute upper triangular matrix from source"
		);
		into.add
		(
			new AbstractParameterizedFunction (TRIL_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.tril (parameters); } },
			"Compute lower triangular matrix from source"
		);
		into.add
		(
			new AbstractParameterizedFunction (TRANSPOSE_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.transpose (parameters); } },
			"Compute transpose of matrix"
		);
		into.add
		(
			new AbstractParameterizedFunction (CHARACTERISTIC_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.characteristic (parameters); } },
			"Compute characteristic polynomial for matrix"
		);
		into.add
		(
			new AbstractParameterizedFunction (EIG_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.eig (parameters); } },
			"Compute Von Mises dominant eigen-pair"
		);
		into.add
		(
			new AbstractParameterizedFunction (GAUSSIAN_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.gaussian (parameters); }
			}, "Solve linear equations with Gaussian elimination"
		);
		into.add
		(
			new AbstractParameterizedFunction (SOLVE_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.solve (parameters); }
			}, "Solve linear equations with column substitution"
		);
		into.add
		(
			new AbstractParameterizedFunction (AUGMENTED_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return mat.augmented (parameters); }
			}, "Construct augmented matrix from source matrix and additional column"
		);
		into.add
		(
			new AbstractParameterizedFunction (VANCHE_FUNCTION)
			{
				public ValueManager.GenericValue execute
						(ValueManager.GenericValue parameters)
				{ return mat.vanche (conversion.seq (parameters)); }
			}, "Construct Vandermonde matrix for a Chebyshev interpolation"
		);
		into.add
		(
			new AbstractParameterizedFunction (VC31_FUNCTION)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameters) { return mat.vc31 (parameters); }
			}, "Solve LUx=b using VC31LU to produce Chebyshev spline for function values"
		);
		into.add
		(
			new AbstractParameterizedFunction (LUXB_FUNCTION)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameters) { return mat.luXb (parameters); }
			}, "Solve LUx=b general case from assignment array=LUXB(L,U,b)"
		);
		into.add
		(
			new AbstractParameterizedFunction (PIVOT_FUNCTION)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameters) { return mat.pivot (parameters); }
			}, "Reorder a vector to a specified pattern"
		);
		into.add
		(
			new AbstractParameterizedFunction (GENKNOT_FUNCTION)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameters) { return mat.genknot (parameters); }
			}, "Construct a zero knot for odd or even functions"
		);
		into.add
		(
			new AbstractParameterizedFunction (EVALSPLINE_FUNCTION)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameters) { return mat.vc31SplineEval (parameters); }
			}, "Evaluate a VC31 spline function at specified parameter"
		);
	}


}

