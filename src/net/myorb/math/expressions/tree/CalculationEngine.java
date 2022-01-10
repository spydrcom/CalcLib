
package net.myorb.math.expressions.tree;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Quadrature;

import net.myorb.math.expressions.tree.CalculationCache.NodeCache;
import net.myorb.math.expressions.tree.SemanticAnalysis.SemanticError;

import net.myorb.math.expressions.symbols.IterationConsumerImplementations;
import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.symbols.GenericWrapper;

import net.myorb.math.computational.DerivativeApproximation;
import net.myorb.math.computational.CalculusMarkers;

import net.myorb.data.abstractions.SimpleUtilities;
import net.myorb.data.abstractions.Function;

import java.util.HashMap;

/**
 * driver for using expression tree to calculate results
 * @param <T> data type used in expressions
 * @author Michael Druckman
 */
public class CalculationEngine<T>
	implements CalculationCache.NodeCalculator<T>
{


	/**
	 * @param spaceManager manager for data type
	 */
	public CalculationEngine (ExpressionSpaceManager<T> spaceManager)
	{
		this.quadrature = new Quadrature<T>
			(this.valueManager = new ValueManager<T> (), spaceManager);
		this.operatorWrapper = new GenericWrapper<T> (spaceManager);
		this.spaceManager = spaceManager;
	}
	protected ExpressionSpaceManager<T> spaceManager;
	protected GenericWrapper<T> operatorWrapper;
	protected ValueManager<T> valueManager;
	protected Quadrature<T> quadrature;


	/**
	 * convert literal to Generic type
	 * @param element the literal element to convert
	 * @return the generic value representation
	 */
	public GenericValue evaluateLiteral (LexicalAnalysis.Literal<T> element)
	{
		switch (element.getElementType ())
		{
			case CONSTANT:	return valueFor ((LexicalAnalysis.NumericConstant<T>) element);
			case LITERAL:	return valueManager.newDiscreteValue (((LexicalAnalysis.NumericLiteral<T>) element).getValue ());
			case TEXT:		return valueManager.newText (((LexicalAnalysis.TextLiteral) element).getValue ());
			default:		break;
		}
		throw new RuntimeException ("Internal error: unrecognized literal node");
	}
	public GenericValue valueFor (LexicalAnalysis.NumericConstant<T> constant)
	{
		return valueManager.newDiscreteValue (spaceManager.newScalar (constant.value));
	}


	/**
	 * get value of named variable
	 * @param element the element referring to variable
	 * @return the generic value representation
	 */
	public GenericValue evaluateIdentifier (LexicalAnalysis.Identifier<T> element)
	{
		switch (element.getTypeManager ().getType ())
		{
			case Local:
			case Variable: return getValueOfIdentifier (element);
			case Function: throw new RuntimeException ("Internal error: function identifier node");
			default: throw new RuntimeException ("Identifier has no value: " + element);
		}
	}
	public GenericValue getValueOfIdentifier (LexicalAnalysis.Identifier<T> element)
	{
		String name = element.getSymbolProperties ().getName ();
		SymbolMap.VariableLookup symbol = symbolLookupCache.get (name);
		if (symbol == null) symbol = (SymbolMap.VariableLookup) symbols.lookup (name);
		if (symbol == null) throw new RuntimeException ("Symbol not found: " + name);
		GenericValue value = symbol.getValue ();
		symbolLookupCache.put (name, symbol);
		value.setName (name);
		return value;
	}
	protected HashMap<String,SymbolMap.VariableLookup> symbolLookupCache = new HashMap<>();


	/**
	 * invocations are Function or Unary Ops
	 * @param element an invocation description
	 * @return the generic value representation
	 */
	public GenericValue evaluateInvocation (SemanticAnalysis.Invocation<T> element)
	{
		SymbolMap.ExecutableUnaryOperator
			executable = getUnaryOperatorFor (element.identifier);
		GenericValue parametersRepresentation = evaluateElement (element.parameter);
		// note that Function is treated as ExecutableUnaryOperator in symbol table:
		// parameter to function is an array of positional actual parameter objects
		return executable.execute (parametersRepresentation);
	}

	/**
	 * verify element refers to unary operator
	 * @param element an element to be verified as a named symbol
	 * @return the operator from the symbol table that is found to be unary
	 */
	public SymbolMap.ExecutableUnaryOperator getUnaryOperatorFor (Element element)
	{
		SymbolMap.ExecutableUnaryOperator symbol = getOperatorFor (element, SymbolMap.ExecutableUnaryOperator.class);
		if (symbol == null) throw new RuntimeException ("Unary Operator expected, found " + LexicalAnalysis.getNameFor (element));
		return symbol;
	}

	/**
	 * @param element the element
	 *  expected to represent an operator
	 * @param operatorClass the expected class of the operator
	 * @return the symbol reference for the operator, null if not appropriate
	 * @param <C> type to cast element to
	 */
	public <C> C getOperatorFor (Element element, Class<C> operatorClass)
	{
		SymbolMap.Named symbol = LexicalAnalysis.getSymbolFor (element);
		return SimpleUtilities.verifyClass (symbol, operatorClass);
	}

	/**
	 * verify element refers to binary operator
	 * @param element the operator to be used for binary evaluation
	 * @return the operator from the symbol table that is found to be binary
	 */
	public SymbolMap.BinaryOperator getBinaryOperatorFor (Element element)
	{
		SymbolMap.BinaryOperator symbol = getOperatorFor (element, SymbolMap.BinaryOperator.class);
		if (symbol == null) throw new RuntimeException ("Binary Operator expected, found " + LexicalAnalysis.getNameFor (element));
		return symbol;
	}

	/**
	 * binary operator computations
	 * @param element a binary operator description
	 * @return the generic value representation
	 */
	public GenericValue evaluateBinaryOperation (SemanticAnalysis.BinaryOperatorNode<T> element)
	{
		SymbolMap.BinaryOperator executable = getBinaryOperatorFor (element.op);
		GenericValue left = evaluateElement (element.left), right = evaluateElement (element.right);
		return executable.execute (left, right);
	}


	/**
	 * @param element object to be dumped
	 */
	public void dump (LexicalAnalysis.RangeDescriptor<T> element)
	{
		System.out.println ("LO Exp: " + element.loExpr);
		System.out.println ("LO: " + evaluate (element.loExpr));

		System.out.println ("HI Exp: " + element.hiExpr);
		System.out.println ("HI: " + evaluate (element.hiExpr));

		System.out.println ("delta Exp: " + element.delta);
		System.out.println ("delta: " + evaluate (element.delta));
	}


	/**
	 * Range Descriptor computations
	 * @param element a Range Descriptor node
	 * @return the generic value representation
	 */
	public GenericValue evaluateRangeDescriptor (LexicalAnalysis.RangeDescriptor<T> element)
	{
		if (element.iterationConsumer == null) element.iterationConsumer = getNewArrayConsumer ();
		return new RangeEvaluator<T> (element, this, symbols).evaluateRangeExpression ();
	}


	/**
	 * calculus approximations
	 * @param element the CalculusDescriptor to evaluate
	 * @return the calculated result
	 */
	public GenericValue evaluateCalculusDescriptor (SemanticAnalysis.CalculusDescriptor<T> element)
	{
		GenericWrapper.GenericFunction<T> function;
		GenericValue value = evaluateElement (element.parameters);

		if ((function = element.underlyingFunction) == null)
		{
			switch (element.calculusOperation)
			{
				case Derivative:															// f'(x)
					function = element.underlyingFunction =
						resolveDerivativeFunction (element, value.getMetadata ());
					break;

				case TSQuad:																// Tanh-Sinh quad
					function = element.underlyingFunction =
						quadrature.quadApproxFor (resolveQuadratureFunction (element));
					break;

				case DCTQuad:																// Clenshaw-Curtis quad
					function = element.underlyingFunction =
						quadrature.ccqApproxFor (resolveQuadratureFunction (element));
					break;

				case Interval:																// f || (lo,hi) = f(hi) - f(lo)
					function = element.underlyingFunction =
						quadrature.IntervalFor (resolveQuadratureFunction (element));
					break;

				case TrapQuad:																// trapezoid approximation
					function = element.underlyingFunction =
						quadrature.trapApproxFor (resolveQuadratureFunction (element));
					break;

				case TrapAdjust:															// trapezoid adjust
					function = element.underlyingFunction =
						quadrature.trapAdjustFor (resolveQuadratureFunction (element));
					break;

				default: throw new RuntimeException ("Unimplemented calculus functionality");
			}
		}

		return function.eval (value);
	}

	/**
	 * @param element the CalculusDescriptor to resolve
	 * @return the operator representing the target function of the descriptor
	 */
	SymbolMap.ExecutableUnaryOperator resolveQuadratureFunction
		(
			SemanticAnalysis.CalculusDescriptor<T> element
		)
	{
		SymbolMap.Named symbol =
			element.identifier.getSymbolProperties ().getSymbolReference ();
		return ((SymbolMap.ExecutableUnaryOperator) symbol);
	}

	/**
	 * @param element the CalculusDescriptor to resolve
	 * @param parameterMetadata the meta-data found on the parameter evaluation
	 * @return the function object to use as derivative
	 */
	GenericWrapper.GenericFunction<T> resolveDerivativeFunction
		(
			SemanticAnalysis.CalculusDescriptor<T> element,
			ValueManager.Metadata parameterMetadata
		)
	{
		String operatorName =
				element.operator.getSymbolProperties ().getName ();
		if (parameterMetadata != null)
		{
			int operatorOrder = operatorName.length ();
			Function<T> f = getFunctionFor (element.identifier);
			T delta = getApproximationDelta (parameterMetadata);
			return getDerivativeFor (f, operatorOrder, delta);
		}
		else
		{
			String functionName =
				element.identifier.getSymbolProperties ().getName () + operatorName;
			return getFunctionFor (symbols.lookup (functionName), functionName);
		}
	}

	/**
	 * @param identifier a link to a symbol reference
	 * @return a function wrapped for multiple call protocols
	 */
	GenericWrapper.GenericFunction<T> getFunctionFor (LexicalAnalysis.Identifier<T> identifier)
	{
		if (identifier == null) throw new RuntimeException ("Internal error");
		LexicalAnalysis.SymbolProperties properties = identifier.getSymbolProperties ();
		return getFunctionFor (properties.getSymbolReference (), properties.getName ());
	}

	/**
	 * @param symbol a symbol reference
	 * @param name the name of the symbol
	 * @return a function wrapped for multiple call protocols
	 */
	GenericWrapper.GenericFunction<T> getFunctionFor (SymbolMap.Named symbol, String name)
	{
		if (symbol == null)
			throw new RuntimeException ("Symbol not available: " + name);
		return operatorWrapper.functionFor (symbol);
	}

	/**
	 * @param f the underlying function for the derivative
	 * @param operatorOrder 1 for first or 2 for second order derivative
	 * @param delta the RUN value to use for derivative approximation
	 * @return a function wrapped for multiple call protocols
	 */
	GenericWrapper.GenericFunction<T> getDerivativeFor (Function<T> f, int operatorOrder, T delta)
	{
		return DerivativeApproximation.getDerivativesFor (f, delta).forOrder (operatorOrder);
	}

	/**
	 * retrieve the delta value from the meta-data block
	 * @param parameterMetadata the meta-data block holding the derivative parameters
	 * @return the RUN value to use for derivative approximation
	 */
	@SuppressWarnings ("unchecked")
	T getApproximationDelta (ValueManager.Metadata parameterMetadata)
	{
		if (parameterMetadata instanceof CalculusMarkers.DerivativeMetadata)
		{ return ((CalculusMarkers.DerivativeMetadata<T>) parameterMetadata).getDelta (); }
		else throw new RuntimeException ("No calculus metadata");
	}


	/**
	 * @return new array consumer object
	 */
	public IterationConsumer getNewArrayConsumer ()
	{
		return IterationConsumerImplementations.getArrayIterationConsumer (spaceManager);
	}


	/**
	 * @param value the value in question
	 * @return TRUE = value is zero
	 */
	public boolean isZero (GenericValue value)
	{
		return spaceManager.isZero (valueManager.toDiscrete (value));
	}


	/**
	 * evaluate a non-specific expression node
	 * @param element a general raw element of an expression
	 * @return the generic value representation
	 */
	@SuppressWarnings ("unchecked")
	public GenericValue evaluateElement (Element element)
	{
		switch (element.getElementType ())
		{
			case TEXT:					// all literals pass thru core literal evaluation
			case CONSTANT:				// sub-types of literal are recognized at next layer down
			case LITERAL:				return evaluateLiteral ((LexicalAnalysis.Literal<T>) element);
			case IDENTIFIER:			return evaluateIdentifier ((LexicalAnalysis.Identifier<T>) element);
			case INVOCATION:			return evaluateInvocation ((SemanticAnalysis.Invocation<T>) element);
			case AGGREGATE:				return evaluateAggregate ((SemanticAnalysis.AggregateLiteral<T>) element);
			case RANGE_DESCRIPTOR:		return evaluateRangeDescriptor ((LexicalAnalysis.RangeDescriptor<T>) element);
			case CALCULUS_DESCRIPTOR:	return evaluateCalculusDescriptor ((SemanticAnalysis.CalculusDescriptor<T>) element);
			case BINARY_OPERATION:		return evaluateBinaryOperation ((SemanticAnalysis.BinaryOperatorNode<T>) element);
			case SUBEXPRESSION:			return cache.eval ((SubExpression<T>) element);
			default:					break;
		}
		throw new RuntimeException ("Internal error: unrecognized raw element: " + element.getClass ().getName ());
	}


	/**
	 * get value of aggregate node
	 * @param element the aggregate literal
	 * @return the aggregate as a value
	 */
	public GenericValue evaluateAggregate (SemanticAnalysis.AggregateLiteral<T> element)
	{
		ValueManager.RawValueList<T> raw = new ValueManager.RawValueList<T> ();
		for (Element e : element.expression) { raw.add (valueManager.toDiscrete (evaluateElement (e))); }
		return valueManager.newDimensionedValue (raw);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.CalculationCache.NodeCalculator#evaluate(net.myorb.math.expressions.tree.SubExpression)
	 */
	public GenericValue evaluate (SubExpression<T> expression)
	{
		int size = expression.size ();
		if (size == 0) return valueManager.newValueList ();		/* empty parameter list () */
		else if (size > 1) throw new RuntimeException ("Expression not reduced: " + expression);
		else return evaluateElement (expression.get (0));
	}


	/**
	 * use expression tree to compute result
	 * @param expression the expression tree being evaluated
	 * @param symbols symbol map for identifier value updates
	 * @return the computed value for the specified expression
	 * @throws SemanticError for semantic errors found
	 */
	public GenericValue evaluate
		(Expression<T> expression, SymbolMap symbols)
	throws SemanticError
	{
		this.symbols = symbols;
		SemanticAnalysis.attributeIdentifierSymbols (expression, symbols);
		CalculationCache.construct (expression, this);
		return cache.evalFullExpression ();
	}
	public void useCache (NodeCache<T> cache) { this.cache = cache; }
	public void setSymbolMap (SymbolMap symbols) { this.symbols = symbols; }
	protected CalculationCache.NodeCache<T> cache;
	protected SymbolMap symbols;


	/**
	 * prepare calculation engine for specific tree
	 * @param forSubExpression the sub-expression that will be evaluated
	 * @param using access to utility methods
	 * @return a prepared engine
	 * @param <T> data type
	 */
	public static <T> CalculationEngine <T> newCalculationEngine
		(SubExpression <T> forSubExpression, Environment<T> using)
	{
		CalculationEngine <T> engine = new CalculationEngine <T> (using.getSpaceManager ());
		engine.setSymbolMap (using.getSymbolMap ()); CalculationCache.construct (forSubExpression, engine);
		return engine;
	}


}

