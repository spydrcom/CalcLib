
package net.myorb.math.computational.multivariate;

import net.myorb.math.expressions.SymbolMap.Operation;
import net.myorb.math.expressions.SymbolMap.SymbolType;

import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.computational.multivariate.FunctionCoordinates.Coordinates;
import net.myorb.math.computational.MultivariateCalculus.VectorOperator;

import net.myorb.math.expressions.SymbolMap.MultivariateOperator;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.matrices.VectorAccess;
import net.myorb.math.matrices.Matrix;

/**
 * implementations of algorithms specific to Multivariate derivative calculus
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class Gradients <T> extends DataManagers <T>
{


	public Gradients
		(VectorOperator vectorOperator)
	{ this (vectorOperator.getEnvironment ()); }

	public Gradients
		(Environment <T> environment)
	{ super (environment); this.connectManagers (); }


	/**
	 * get management objects from environment
	 */
	public void connectManagers ()
	{ this.functionCoordinates = new FunctionCoordinates <T> (environment); }
	protected FunctionCoordinates <T> functionCoordinates = null;


	/**
	 * compute derivatives for each unit vector function
	 * @param context the meta-data context collected for the function
	 * @return the matrix of partial derivatives
	 */
	public Matrix <T> partialDerivativeComputations (OperationContext context)
	{
		Matrix <T> M;

		int functions, derivatives = context.metadata.getParameters ();
		Coordinates evalPoint = context.metadata.getEvaluationPoint ();
		Coordinates baseVec = eval ( evalPoint, context );

		if ( ( functions = baseVec.size () ) == 1 )
		{ M = new Matrix <> (functions, derivatives, manager); }
		else if ( functions == derivatives ) { M = Matrix.square (functions, manager); }
		else { throw new RuntimeException ("Function profile cannot be used"); }

		partialDerivativeComputations
		(
			context, M, derivatives,
			evalPoint, baseVec
		);

		return M;
	}


	/**
	 * compute derivatives for each unit vector function
	 * @param context the meta-data context collected for the function
	 * @param M the matrix that will hold the evaluated derivatives
	 * @param N the number of variables in the function
	 * @param evalPoint the point of the evaluation
	 * @param baseVec the function result at point
	 */
	public void partialDerivativeComputations
		(
			OperationContext context, Matrix <T> M, int N,
			Coordinates evalPoint, Coordinates baseVec
		)
	{
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


	/**
	 * prepare to calculate rise over run
	 * @param evalAt the evaluation point including the appropriate delta offset
	 * @param baseVec the evaluation of the full function at the evaluation point
	 * @param delta the run value to use for the derivative calculations
	 * @param column the index that identifies the relative variable
	 */
	public void partialDerivativeComputation
		(
			Coordinates evalAt, Coordinates baseVec,
			double delta, VectorAccess <T> column
		)
	{
		Coordinates
			rise = evalAt.minus (baseVec);
		for (int n = 0; n < column.size (); n++)
		{ column.set (n+1, riseOverRun (rise, delta, n)); }
	}


	/**
	 * the definitive algorithm for derivatives
	 * @param rise the offset values minus the base
	 * @param run the delta value to use as a run in the calculation
	 * @param n the index that identifies the relative variable
	 * @return the computed derivative value
	 */
	T riseOverRun (Coordinates rise, double run, int n)
	{ return manager.convertFromDouble (rise.get (n) / run); }


	/**
	 * evaluate the function at a vector of Coordinates
	 * @param parameter a vector of Coordinates to use as a parameter
	 * @param context the meta-data for the function being evaluated
	 * @return Coordinates of the vector returned by the function
	 */
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
			VO, function, new Gradients <> (VO)
		);
	}


	/**
	 * perform processing steps
	 * @param context the collected Operation Context meta-data
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


	/**
	 * display parameter value
	 * @param context the collected meta-data
	 * @return the function evaluation of the parameter
	 */
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

