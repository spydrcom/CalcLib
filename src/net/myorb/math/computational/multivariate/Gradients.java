
package net.myorb.math.computational.multivariate;

import net.myorb.math.expressions.SymbolMap.Operation;
import net.myorb.math.expressions.SymbolMap.SymbolType;

import net.myorb.math.expressions.ValueManager.GenericValue;

import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.computational.multivariate.FunctionCoordinates.Coordinates;
import net.myorb.math.computational.MultivariateCalculus.VectorOperator;
import net.myorb.math.expressions.SymbolMap.MultivariateOperator;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.matrices.VectorAccess;
import net.myorb.math.matrices.Matrix;

/**
 * implementations of algorithms specific to Multivariate derivative calculus
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class Gradients <T>
{


	public Gradients (Environment <T> environment)
	{
		functionCoordinates = new FunctionCoordinates <T> (environment);
		this.manager = environment.getSpaceManager ();
		this.valueManager = environment.getValueManager ();
		if (manager instanceof ExpressionComponentSpaceManager)
		{ this.compManager = (ExpressionComponentSpaceManager <T>) manager; }
		this.environment = environment;
	}
	FunctionCoordinates <T> functionCoordinates;
	protected ExpressionComponentSpaceManager <T> compManager = null;
	protected ExpressionSpaceManager <T> manager = null;
	protected ValueManager <T> valueManager = null;
	protected Environment <T> environment = null;


	public Matrix <T> partialDerivativeComputations (OperationContext context)
	{
		Matrix <T> M; int N = context.metadata.getParameters ();
		partialDerivativeComputations (context, M = new Matrix <T> (N, N, manager), N);
		return M;
	}


	public void partialDerivativeComputations
		(OperationContext context, Matrix <T> M, int N)
	{
		Coordinates evalPoint = context.metadata.getEvaluationPoint ();
		Coordinates baseVec = eval ( evalPoint, context );

		for (int n = 0; n < N; n++)
		{
			double delta = context.metadata.getDeltaFor (n);

			Coordinates	evalAt = eval
			(
				evalPoint.plus (delta, n), context
			);

			partialDerivativeComputation
			(
				evalAt, baseVec, delta,
				M.getColAccess (n+1)
			);
		}
	}
	public void partialDerivativeComputation
		(
			Coordinates evalAt, Coordinates baseVec,
			double delta, VectorAccess <T> column
		)
	{
		Coordinates dif = evalAt.minus (baseVec);

		for (int n = 0; n < column.size (); n++)
		{
			double tan = dif.get (n) / delta;
			column.set (n+1, manager.convertFromDouble (tan));
		}
	}
	public Coordinates eval (Coordinates parameter, OperationContext context)
	{ return functionCoordinates.evaluate (context.execute (parameter)); }


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
		
		switch (context.metadata.getOp ().typeOfOperation ())
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

