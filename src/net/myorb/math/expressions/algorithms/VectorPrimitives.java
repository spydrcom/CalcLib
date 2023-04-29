
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.evaluationstates.Arrays;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.symbols.IterationConsumerImplementations;
import net.myorb.math.expressions.symbols.IterationConsumer;

import net.myorb.math.expressions.symbols.AbstractBinaryOperator;
import net.myorb.math.expressions.symbols.AbstractParameterizedFunction;
import net.myorb.math.expressions.symbols.AbstractVectorReduction;
import net.myorb.math.expressions.symbols.AbstractVectorConsumer;

import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.*;

import net.myorb.math.computational.Statistics;
import net.myorb.math.matrices.*;

import java.util.ArrayList;
import java.util.List;

/**
 * implementations of algorithms computing vector operations
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class VectorPrimitives<T> extends AlgorithmCore<T>
{


	// originally BuiltInArrayFunctions


	/**
	 * object depends on data structures of the environment
	 * @param environment the environment object holding value management objects
	 */
	public VectorPrimitives (Environment<T> environment)
	{
		super (environment);
		this.vectorOperations = new VectorOperations<T> (spaceManager);
		this.functions = this.abstractions = new BuiltInArrayAbstractions<T> (environment);
		this.conversion = abstractions.getExtendedDataConversions ();
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
	public List<T> vectorNegate (ValueManager.GenericValue v)
	{
		return conversion.toArray (conversion.toVector (v).negate ());
	}

	/**
	 * addition of two vectors
	 * @param left the left side of the operator
	 * @param right the right side of the operator
	 * @return the elements of the sum vector
	 */
	public List<T> vectorAdd (List<T> left, List<T> right)
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
	 * vector cross product of two arrays
	 * @param left left side array reference
	 * @param right right side array reference
	 * @return computed dot product
	 */
	public ValueManager.GenericValue cross
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		Vector<T>
			lParm = conversion.toVector (valueManager.toArray (left)),
			rParm = conversion.toVector (valueManager.toArray (right));
		Vector<T> result = vectorOperations.crossProduct (lParm, rParm);
		return valueManager.newDimensionedValue (result.getElementsList ());
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
		{ return abstractions.getMatrixLibrary ().index (valueManager.toMatrix (left), right); }
		else return valueManager.applyIndex (left, valueManager.toInt (right, spaceManager));
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
	 * Collect values into array array
	 * @param value the value to be concatenated
	 * @return the array with value appended
	 */
	public ValueManager.GenericValue array (ValueManager.GenericValue value)
	{
		return value;
	}


	/**
	 * Append a series of arrays into one long array
	 * @param values the series of arrays to be concatenated
	 * @return the array with all values appended
	 */
	public ValueManager.GenericValue concat (ValueManager.GenericValue values)
	{
		throw new RuntimeException ("CONCAT only available as consumer");
	}


	/**
	 * Append a series of arrays as rows into one matrix
	 * @param values the series of arrays to be concatenated
	 * @return the array with all values appended
	 */
	public ValueManager.GenericValue stack (ValueManager.GenericValue values)
	{
		throw new RuntimeException ("STACK only available as consumer");
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


	/*
	 * implementation of vector functions
	 */


	/**
	 * implement function - LENGTH
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getLengthAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return length (parameters); }
		};
	}


	/**
	 * implement function - INTERVAL
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getIntervalAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return abstractions.arrayInterval (parameters); }
		};
	}


	/**
	 * implement function - APPEND
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getAppendAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return append (parameters); }
		};
	}


	/**
	 * implement function - ARRAY
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getArrayAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return array (parameters); }
		};
	}


	/**
	 * implement function - CONCAT
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractVectorConsumer getConcatAlgorithm (String symbol)
	{
		return new AbstractVectorConsumer (symbol)
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.symbols.AbstractVectorConsumer#getIterationConsumer()
			 */
			public IterationConsumer getIterationConsumer ()
			{
				return IterationConsumerImplementations.getConcatIterationConsumer (spaceManager);
			}

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.SymbolMap.ExecutableUnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
			 */
			public GenericValue execute (GenericValue parameter)
			{
				return concat (parameter);
			}
		};
	}


	/**
	 * implement function - STACK
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractVectorConsumer getStackAlgorithm (String symbol)
	{
		return new AbstractVectorConsumer (symbol)
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.symbols.AbstractVectorConsumer#getIterationConsumer()
			 */
			public IterationConsumer getIterationConsumer ()
			{
				return IterationConsumerImplementations.getStackIterationConsumer (spaceManager);
			}

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.SymbolMap.ExecutableUnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
			 */
			public GenericValue execute (GenericValue parameter)
			{
				return stack (parameter);
			}
		};
	}//TODO:


	/**
	 * implement function - INTEGRALx (indefinite form)
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractVectorReduction getIndefiniteAlgorithm (String symbol)
	{
		return new AbstractVectorReduction (symbol)
		{
			public String markupForDisplay (String operator, Range range, String parameters, NodeFormatting using)
			{ return using.rangeSpecificationNotation (using.integralIndefinite (operator), parameters); }

			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ throw new RuntimeException ("Indefinite integral cannot be computed"); }
		};
	}


	/**
	 * implement function - INTEGRAL
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractVectorConsumer getIntegralAlgorithm (String symbol)
	{
		return new AbstractVectorConsumer (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return sigma (parameters); }
			public IterationConsumer getIterationConsumer () { return IterationConsumerImplementations.getIntegralIterationConsumer (spaceManager); }

			public String markupForDisplay (String operator, Range range, String parameters, NodeFormatting using)
			{ return using.rangeSpecificationNotation (using.integralRange (operator, range), parameters); }
		};
	}


	/**
	 * implement function - INTEGRALC
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractVectorReduction getContourAlgorithm (String symbol)
	{
		return new AbstractVectorReduction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ throw new RuntimeException ("Contour integral computation not implemented"); }

			public String markupForDisplay (String operator, Range range, String parameters, NodeFormatting using)
			{ return using.rangeSpecificationNotation (using.contourRange (operator), parameters); }
		};
	}


	/**
	 * implement function - SUMMATION
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractVectorConsumer getSummationAlgorithm (String symbol)
	{
		return new AbstractVectorConsumer (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return sigma (parameters); }
			public IterationConsumer getIterationConsumer () { return IterationConsumerImplementations.getSummationIterationConsumer (spaceManager); }

			public String markupForDisplay (String operator, Range range, String parameters, NodeFormatting using)
			{ return using.rangeSpecificationNotation (using.indexedRange (operator, range), parameters); }
		};
	}


	/**
	 * implement function - PI
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractVectorConsumer getPiAlgorithm (String symbol)
	{
		return new AbstractVectorConsumer (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return pi (parameters); }
			public IterationConsumer getIterationConsumer () { return IterationConsumerImplementations.getProductIterationConsumer (spaceManager); }

			public String markupForDisplay (String operator, Range range, String parameters, NodeFormatting using)
			{ return using.rangeSpecificationNotation (using.indexedRange (operator, range), parameters); }
		};
	}


	/**
	 * implement function - MAX
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getMaxAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return valueManager.newDiscreteValue (stats.max (conversion.seq (parameters))); }
		};
	}


	/**
	 * implement function - MIN
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getMinAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return valueManager.newDiscreteValue (stats.min (conversion.seq (parameters))); }
		};
	}


	/**
	 * implement function - PEARSON
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getPearsonAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return valueManager.newDiscreteValue (abstractions.pearson (conversion.seq2D (parameters))); }
		};
	}


	/**
	 * implement function - FITLINE
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getFitlineAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return valueManager.newDimensionedValue (abstractions.fitline (conversion.seq2D (parameters))); }
		};
	}


	/**
	 * implement function - FITEXP
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getFitexpAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return valueManager.newDimensionedValue (abstractions.fitexp (conversion.seq2D (parameters))); }
		};
	}


	/**
	 * implement function - FITPOLY
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getFitpolyAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue
			execute (ValueManager.GenericValue parameters)
			{ return abstractions.fitpoly (conversion.seq2D (parameters)); }
		};
	}


	/**
	 * implement function - FITHARMONIC
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getFitharmonicAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return valueManager.newDimensionedValue (abstractions.fitharmonic (parameters)); }
		};
	}


	/**
	 * implement function - LAGRANGE
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getLagrangeAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return abstractions.lagrange (conversion.seq2D (parameters)).coefficientsWithMetadata (null); }
		};
	}


	/**
	 * implement function - CHEBYSHEV
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getChebyshevAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return abstractions.chebyshev (conversion.seq2D (parameters)).coefficientsWithMetadata (null); }
		};
	}


	/**
	 * implement function - DYADIC
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getDyadicAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return valueManager.newMatrix (dyadicProduct (parameters)); }
		};
	}


//	/**
//	 * implement function - CROSS
//	 * @param symbol the symbol associated with this object
//	 * @return operation implementation object
//	 */
//	public AbstractParameterizedFunction getCrossAlgorithm (String symbol)
//	{
//		return new AbstractParameterizedFunction (symbol)
//		{
//			public ValueManager.GenericValue
//			execute (ValueManager.GenericValue parameters)
//			{ return cross (parameters); }
//		};
//	}


	/**
	 * implement function - DOT
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getDotAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue
			execute (ValueManager.GenericValue parameters)
			{ return dot (parameters); }
		};
	}


	/**
	 * implement operator - DOT (.)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getDotAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public String markupForDisplay
				(
					String operator, String firstOperand, String secondOperand,
					boolean fenceFirst, boolean fenceSecond, NodeFormatting using
				)
			{
				return using.formatBinaryOperation
				(
					firstOperand,
					OperatorNomenclature.DOT_PRODUCT_RENDER,
					secondOperand
				);
			}

			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return dot (left, right); }
		};
	}


	/**
	 * implement operator - CROSS (X)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getCrossAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public String markupForDisplay
				(
					String operator, String firstOperand, String secondOperand,
					boolean fenceFirst, boolean fenceSecond, NodeFormatting using
				)
			{
				return using.formatBinaryOperation
				(
					firstOperand,
					OperatorNomenclature.CROSS_PRODUCT_RENDER,
					secondOperand
				);
			}

			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return cross (left, right); }
		};
	}


	/**
	 * implement operator - RANGE (..)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getRangeAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				T ONE = spaceManager.getOne ();
				List<T> values = new ArrayList<T>();
				T hi = valueManager.toDiscrete (right),
					current = valueManager.toDiscrete (left);
				while (!spaceManager.lessThan (hi, current))
				{
					values.add (current);
					current = spaceManager.add (current, ONE);
				}
				return valueManager.newDimensionedValue (values);
			}
		};
	}


	/**
	 * implement operator - [] indexing
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getIndexingAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public String markupForDisplay
				(
					String operator,
					String firstOperand, String secondOperand,
					boolean lfence, boolean rfence,
					NodeFormatting using
				)
			{ return using.formatSubScript (firstOperand, secondOperand); }

			public ValueManager.GenericValue execute
				(
					ValueManager.GenericValue left, ValueManager.GenericValue right
				)
			{ return index (left, right); }
		};
	}


}

