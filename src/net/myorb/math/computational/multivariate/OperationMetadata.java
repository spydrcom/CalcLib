
package net.myorb.math.computational.multivariate;

import net.myorb.math.computational.MultivariateCalculus;
import net.myorb.math.computational.DerivativeApproximationMultiDim;

import net.myorb.math.expressions.symbols.AbstractFunction;
import net.myorb.math.expressions.SymbolMap.Operation;
import net.myorb.math.expressions.ValueManager;

/**
 * meta-data collected when the operation was invoked
 * @author Michael Druckman
 */
public class OperationMetadata implements ValueManager.Metadata
{


	public OperationMetadata (Operation op, Operation target)
	{
		this.setOp ( (MultivariateCalculus.VectorOperator) op );
		this.setTarget (target);
	}


	/**
	 * capture meta-data for execution of vector operations
	 * - calculus markers are assigned to the operation extension
	 * @param op the operation description of the vector operation being executed
	 */
	public void setOp (MultivariateCalculus.VectorOperator op) { this.op = op; }
	public MultivariateCalculus.VectorOperator getOp () { return op; }
	private MultivariateCalculus.VectorOperator op;


	/**
	 * attach a Derivative Approximation processor
	 * @param function the function to be analyzed
	 * @param <T> data type for operations
	 */
	public <T> void attachEngineFor (AbstractFunction <T> function)
	{  engine  =  new DerivativeApproximationMultiDim <>  (function);  }
	@SuppressWarnings("unchecked") public <T> DerivativeApproximationMultiDim <T>
		getEngine () { return (DerivativeApproximationMultiDim <T>) engine; }
	protected DerivativeApproximationMultiDim <?> engine;


	/**
	 * @param target the operation describing the function being evaluated
	 */
	public void setTarget (Operation target) { this.target = target; }
	public Operation getTarget () { return this.target; }
	private Operation target = null;


	/**
	 * capture the point at which the evaluation was done
	 * @param evaluationPoint the coordinates of the evaluation point
	 */
	public void setEvaluationPoint (FunctionCoordinates.Coordinates evaluationPoint)
	{ this.evaluationPoint = evaluationPoint; this.setParameters (evaluationPoint.size ()); }
	public FunctionCoordinates.Coordinates getEvaluationPoint () { return evaluationPoint; }
	private FunctionCoordinates.Coordinates evaluationPoint;


	/**
	 * @return the count of parameters in the invocation
	 */
	public int getParameters () { return parameters; }
	public void setParameters (int parameters) { this.parameters = parameters; }
	private int parameters = 1;


}

