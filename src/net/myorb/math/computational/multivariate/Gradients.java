
package net.myorb.math.computational.multivariate;

import net.myorb.math.computational.MultivariateCalculus;
import net.myorb.math.computational.MultivariateCalculus.VectorOperator;

import net.myorb.math.computational.multivariate.Gradients.OperationContext;
import net.myorb.math.computational.multivariate.Gradients.OperationMetadata;

import net.myorb.math.expressions.symbols.AbstractFunction;
import net.myorb.math.expressions.SymbolMap.MultivariateOperator;
import net.myorb.math.expressions.SymbolMap.SymbolType;
import net.myorb.math.expressions.SymbolMap.Operation;

import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.ValueManager.Metadata;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.matrices.Matrix;

import net.myorb.math.Function;

/**
 * implementations of algorithms specific to Multivariate derivative calculus
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class Gradients <T>
{


	public Gradients (Environment <T> environment)
	{
		this.valueManager = environment.getValueManager ();
		if (valueManager instanceof ExpressionComponentSpaceManager)
		{ this.compManager = (ExpressionComponentSpaceManager <T>) manager; }
		this.manager = environment.getSpaceManager ();
		this.environment = environment;
	}
	protected ExpressionComponentSpaceManager <T> compManager = null;
	protected ExpressionSpaceManager <T> manager = null;
	protected ValueManager <T> valueManager = null;
	protected Environment <T> environment = null;


	/**
	 * meta-data collected when the operation was invoked
	 * @param <T> the data type
	 */
	public static class OperationMetadata implements Metadata
	{
		public OperationMetadata
		(Operation op, Operation target)
		{ this.op = (MultivariateCalculus.VectorOperator) op; this.target = target; }
		public MultivariateCalculus.VectorOperator getOperation () { return this.op; }
		public Operation getTarget () { return this.target; }
		FunctionCoordinates.Coordinates evaluationPoint;
		MultivariateCalculus.VectorOperator op;
		Operation target = null;
		int parameters = 1;
	}


	public static class TargetMetadata <T> implements Metadata
	{
		Subroutine <T> symbolDescription;
		Function <T> function;
	}


	/**
	 * the context passed from the equation processing
	 * @param <T> the data type
	 */
	public static class OperationContext implements ValueManager.VectorOperation
	{

		public OperationContext
			(String name, OperationMetadata metadata)
		{ this.setName (name); this.setMetadata (metadata); }
		protected String name; protected OperationMetadata metadata;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.ValueManager.GenericValue#setMetadata(net.myorb.math.expressions.ValueManager.Metadata)
		 */
		public void setMetadata
		(Metadata metadata) { this.metadata = (OperationMetadata) metadata; }
		public void setName (String name) { this.name = name; }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.ValueManager.GenericValue#getMetadata()
		 */
		public Metadata getMetadata () { return metadata; }
		
		@SuppressWarnings("unchecked")
		public <T> AbstractFunction <T> getFunction ()
		{ return ( AbstractFunction <T> ) metadata.target; }
		public String getName () { return name; }
		
		public void dump (GenericValue parameter)
		{
			FunctionCoordinates <?> FC = getFunctionCoordinates ();
			System.out.println ("POINT " + FC.evaluate (parameter));
			System.out.println ("VEC OP " + metadata.op.getName ());
			System.out.println ("TARGET " + metadata.target.getName ());
			System.out.println ("TYPE " + metadata.target.getClass ().getCanonicalName ());
			System.out.println ("BODY " + metadata.target);
		}


		// coordinates conversions

		public GenericValue toGenericValue
		(FunctionCoordinates.Coordinates coordinates)
		{ return getFunctionCoordinates ().represent (coordinates); }
		public FunctionCoordinates.Coordinates toVector (GenericValue parameter)
		{ return getFunctionCoordinates ().evaluate (parameter); }

		@SuppressWarnings({"rawtypes","unchecked"})
		FunctionCoordinates getFunctionCoordinates ()
		{ return new FunctionCoordinates (metadata.op.getEnvironment ()); }


		// processing for evaluation point

		public GenericValue getEvaluationPoint ()
		{ return toGenericValue (metadata.evaluationPoint); }

		public void setEvaluationPoint (GenericValue point)
		{ setEvaluationPoint (toVector (point)); }

		public void setEvaluationPoint
		(FunctionCoordinates.Coordinates evaluationPoint)
		{ metadata.evaluationPoint = evaluationPoint; }

	}


	public Matrix <T> partialDerivativeComputations (OperationContext context)
	{
		Matrix <T> M; int N = context.metadata.parameters;
		partialDerivativeComputations (context, M = new Matrix <T> (N, N, manager), N);
		return M;
	}


	public void partialDerivativeComputations
		(OperationContext context, Matrix <T> M, int N)
	{
		
	}

	/**
	 * Vector Operation processor
	 * @param VO the VectorOperator to ultimately be executed
	 * @param function the target function for the Operation to evaluate
	 * @return the Operation object that will drive the algorithm
	 */
	public static Operation processVectorOperation
		(VectorOperator VO, Operation function)
	{
		return new VectorOperationProcessor
		(
			VO, function, new Gradients <> (VO.getEnvironment ())
		);
	}

	/**
	 * perform processing steps
	 * @param context the collected Operation Context
	 * @return the calculated result of the Operation
	 */
	public GenericValue executeFrom (OperationContext context)
	{
		if (REGRESSION) return regressionTest (context);
		VectorOperations <T> op = new VectorOperations <> (environment);
		
		switch (context.metadata.op.typeOfOperation ())
		{
			case VECTOR_DIV:	return  op.div (context);
			case VECTOR_CURL:	return op.curl (context);
			case VECTOR_GRAD:	return op.grad (context);
			default:			break;
		}

		throw new RuntimeException ("Internal error, unreconized operation");
	}
	GenericValue regressionTest (OperationContext context)
	{
		GenericValue P = context.getEvaluationPoint ();
		System.out.println ( "Parameter Point " + P );
		return context.getFunction ().execute (P);
	}
	static boolean REGRESSION = false;

}


/**
 * implementation of Vector Operation processor
 */
class VectorOperationProcessor implements MultivariateOperator
{

	VectorOperationProcessor
		(
			VectorOperator VO, Operation function,
			Gradients <?> gradients
		)
	{
		context = new OperationContext
			(
				function.getName (), new OperationMetadata (VO, function)
			);
		this.gradients = gradients;
	}
	protected OperationContext context; protected Gradients <?> gradients;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ExecutableUnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public GenericValue execute (GenericValue parameter)
	{
		if (TRACE) context.dump (parameter);
		context.setEvaluationPoint (parameter);
		return gradients.executeFrom (context);
	}
	static boolean TRACE = false;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Operation#getPrecedence()
	 */
	public int getPrecedence () { return 99; }	// must be highest possible

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getName()
	 */
	public String getName () { return context.getFunction ().getName (); }
	public SymbolType getSymbolType () { return SymbolType.PARAMETERIZED; }

	// satisfaction of interface, unused in Vector Operation Processing

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ParameterizedFunction#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
	 */
	public String markupForDisplay
	(String operator, String parameters, NodeFormatting using)
	{ return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ParameterizedFunction#getParameterList()
	 */
	public String getParameterList () { return null; }
	public String formatPretty () { return null; }

}

