
package net.myorb.math.computational;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ExpressionComponentSpaceManager;

import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.symbols.AbstractFunction;
import net.myorb.math.expressions.symbols.AbstractUnaryOperator;
import net.myorb.math.expressions.SymbolMap.MultivariateOperator;
import net.myorb.math.expressions.SymbolMap.Operation;
import net.myorb.math.expressions.SymbolMap.SymbolType;
import net.myorb.math.expressions.gui.rendering.MathMarkupNodes;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.ValueManager.RawValueList;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.ValueManager.Metadata;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.matrices.Matrix;

import net.myorb.math.Function;

/**
 * implementations of algorithms computing Multivariate calculus operations
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class MultivariateCalculus <T>
{


	/**
	 * abstract base class for operator implementation objects
	 */
	public static abstract class VectorOperator extends AbstractUnaryOperator
			implements CalculusMarkers.CalculusMetadata
	{
		public VectorOperator
		(
			String name, int precedence,
			CalculusMarkers.CalculusMarkerTypes type
		)
		{ super (name, precedence); this.type = type; }

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.CalculusMarkers.CalculusMetadata#typeOfOperation()
		 */
		public CalculusMarkers.CalculusMarkerTypes typeOfOperation () { return type; }
		CalculusMarkers.CalculusMarkerTypes type;
	}


	/**
	 * meta-data collected when the operation was invoked
	 * @param <T> the data type
	 */
	public static class OperationMetadata implements Metadata
	{
		public OperationMetadata
		(Operation op, Operation target)
		{ this.op = op; this.target = target; }
		Operation op, target;
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
		public String getName () { return name; }
		
	}

	public static Operation processVectorOperation (VectorOperator VO, Operation function)
	{
		return new MultivariateOperator ()
		{
			OperationContext context = new OperationContext (function.getName (), new OperationMetadata (VO, function));

			public String markupForDisplay
			(String operator, String parameters, NodeFormatting using)
			{ return null; }

			public String getParameterList() { return null; }

			public String formatPretty () { return null; }

			public GenericValue execute (GenericValue parameter)
			{
				System.out.println (parameter);
				System.out.println ("VEC OP "+context.metadata.op.getName());
				System.out.println ("TARGET "+context.metadata.target.getName());
				System.out.println ("TYPE "+context.metadata.target.getClass().getCanonicalName());
				System.out.println ("BODY "+context.metadata.target);
				return ( (AbstractFunction) context.metadata.target ).execute (parameter);
			}

			public int getPrecedence() { return 99; }

			public String getName() { return "MV_CALCULUS"; }

			public SymbolType getSymbolType () { return SymbolType.PARAMETERIZED; }
			
		};
	}


	public MultivariateCalculus (Environment <T> environment)
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
	 * format MML for vector operation
	 * @param operator the operator symbol
	 * @param operand the text of the operand
	 * @param using mark-up formatting object for display
	 * @return text of the formatted operation
	 */
	public String markupForDisplay
		(String operator, String operand, NodeFormatting using)
	{
		String op = using.formatOperatorReference (NABLA + operator);
		return op  + MathMarkupNodes.space ("2") + operand;
	}
	static final String NABLA = "\u2207";


	/**
	 * compute gradient for function at point
	 * @param context the meta-data holding the context
	 * @return the matrix of partial derivatives
	 */
	public ValueManager.GenericValue grad (OperationContext context)
	{
		return valueManager.newMatrix (partialDerivativeComputations (context));
	}

	Matrix <T> partialDerivativeComputations (OperationContext context)
	{
		Matrix <T> M; int N = context.metadata.parameters;
		partialDerivativeComputations (context, M = new Matrix <T> (N, N, manager), N);
		return M;
	}

	/**
	 * compile matrix of partial derivatives
	 * @param context the meta-data supplied by the operation request
	 * @param results the matrix collecting the partial derivatives
	 * @param N the number of variables in the function
	 */
	void partialDerivativeComputations
		(OperationContext context, Matrix <T> results, int N)
	{
		
	}


	/**
	 * compute divergence for function at point
	 * @param context the meta-data holding the context
	 * @return the scalar value computed as divergence
	 */
	public ValueManager.GenericValue div (OperationContext context)
	{
		T divergence =
			computeDivergence (partialDerivativeComputations (context));
		return valueManager.newDiscreteValue (divergence);
	}

	/**
	 * sum partial derivatives as evaluation of divergence
	 * @param M the matrix of partial derivatives computed
	 * @return the computed divergence
	 */
	T computeDivergence (Matrix <T> M)
	{
		int N = M.getEdgeCount ();
		T divergence = manager.getZero ();

		for (int row = 1; row <= N; row++)
		{
			for (int col = 1; col <= N; col++)
			{
				divergence = 
					manager.add
					(
						divergence, M.get (row, col)
					);
			}
		}

		return divergence;
	}


	/**
	 * compute curl for function at point
	 * @param context the meta-data holding the context
	 * @return the scalar value computed as divergence
	 */
	public ValueManager.GenericValue curl (OperationContext context)
	{
		int N;
		Matrix <T> M = partialDerivativeComputations (context);
		if ( (N = M.getEdgeCount ()) < 2 ) throw new RuntimeException ("Too few dimensions for curl");
		if ( N > 3 ) throw new RuntimeException ("Too many dimensions for curl");
		return valueManager.newDimensionedValue (computeCurl (M));
	}

	/**
	 * apply equations to compute curl
	 * @param M the matrix of partial derivatives computed
	 * @return the computed curl vector
	 */
	RawValueList<T> computeCurl (Matrix <T> M)
	{
		RawValueList <T> vector;
		set ( 2, 2, 1, M, vector = zeroVector () );
		// no contribution from 3rd dimension given 2x2 matrix

		if ( M.getEdgeCount () > 2 )
		{
			// 3rd dimension is present
			set ( 1, 1, 3, M, vector );
			set ( 0, 3, 2, M, vector );
		}

		return vector;
	}

	/**
	 * set one component of the curl vector
	 * @param into index of the orthogonal basis being set
	 * @param V index of the component of the function being processed
	 * @param d index of the orthogonal basis being referenced
	 * @param M the matrix of partial derivatives computed
	 * @param vector the computed curl vector
	 */
	void set (int into, int V, int d, Matrix <T> M, RawValueList <T> vector)
	{
		vector.set ( into, manager.add (M.get (V, d), manager.negate (M.get (d, V))) );		
	}

	/**
	 * @return initialized vector of zero entries
	 */
	RawValueList <T> zeroVector ()
	{
		T Z = manager.getZero ();
		RawValueList <T> vector = new RawValueList <T> ();
		vector.add (Z); vector.add (Z); vector.add (Z);
		return vector;
	}


}

