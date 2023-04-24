
package net.myorb.math.computational.multivariate;

import net.myorb.math.expressions.SymbolMap.Operation;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.ValueManager.Metadata;
import net.myorb.math.expressions.symbols.AbstractFunction;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.matrices.Matrix;

import net.myorb.math.Function;
import net.myorb.math.computational.MultivariateCalculus;

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
		String name; OperationMetadata metadata;

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
			@SuppressWarnings({ "unchecked", "rawtypes" })
			FunctionCoordinates <?> FC = new FunctionCoordinates
					(metadata.op.getEnvironment ());
			System.out.println ("POINT "+FC.evaluate(parameter));
			System.out.println ("VEC OP "+metadata.op.getName());
			System.out.println ("TARGET "+metadata.target.getName());
			System.out.println ("TYPE "+metadata.target.getClass().getCanonicalName());
			System.out.println ("BODY "+metadata.target);
		}

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


}

