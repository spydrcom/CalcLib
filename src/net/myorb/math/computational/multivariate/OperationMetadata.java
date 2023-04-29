
package net.myorb.math.computational.multivariate;

import net.myorb.math.computational.MultivariateCalculus;

import net.myorb.math.expressions.ValueManager.Metadata;
import net.myorb.math.expressions.SymbolMap.Operation;

/**
 * meta-data collected when the operation was invoked
 * @author Michael Druckman
 */
public class OperationMetadata implements Metadata
{


	public OperationMetadata (Operation op, Operation target)
	{
		this.setOp ( (MultivariateCalculus.VectorOperator) op );
		this.setTarget (target);
	}


	public void setOp (MultivariateCalculus.VectorOperator op) { this.op = op; }
	public MultivariateCalculus.VectorOperator getOperation () { return this.op; }
	public MultivariateCalculus.VectorOperator getOp () { return op; }
	private MultivariateCalculus.VectorOperator op;


	public void setTarget
		(Operation target) { this.target = target; }
	public Operation getTarget () { return this.target; }
	private Operation target = null;


	public void setEvaluationPoint
	(FunctionCoordinates.Coordinates evaluationPoint)
	{ this.evaluationPoint = evaluationPoint; this.setParameters (evaluationPoint.size ()); }
	public FunctionCoordinates.Coordinates getEvaluationPoint () { return evaluationPoint; }
	private FunctionCoordinates.Coordinates evaluationPoint;


	public int getParameters () { return parameters; }
	public void setParameters (int parameters) { this.parameters = parameters; }
	private int parameters = 1;


}

