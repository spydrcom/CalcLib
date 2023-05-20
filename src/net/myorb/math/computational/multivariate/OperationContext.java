
package net.myorb.math.computational.multivariate;

import net.myorb.math.computational.CalculusMarkers;

import net.myorb.math.expressions.symbols.AbstractFunction;

import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.ValueManager.Metadata;
import net.myorb.math.expressions.ValueManager;

/**
 * the context passed processed by the equation processing
 * @author Michael Druckman
 */
public class OperationContext implements ValueManager.VectorOperation
{


	public OperationContext
		(String name, OperationMetadata metadata)
	{ this.setName (name); this.setMetadata (metadata); }
	protected String name; protected OperationMetadata metadata;


	// interface implementations

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ValueManager.GenericValue#setMetadata(net.myorb.math.expressions.ValueManager.Metadata)
	 */
	public void setMetadata (Metadata metadata)
	{
		this.metadata = (OperationMetadata) metadata;
		this.connectProcessors ();
	}
	public void setName (String name) { this.name = name; }

	/**
	 * attach application specific processor objects
	 */
	public void connectProcessors ()
	{
		if ( metadata.getOp ().typeOfOperation () == LAPLACIAN )
		{ metadata.attachEngineFor ( getFunction () ); }
	}
	protected CalculusMarkers.CalculusMarkerTypes
	LAPLACIAN = CalculusMarkers.CalculusMarkerTypes.VECTOR_LAPLACE;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ValueManager.GenericValue#getMetadata()
	 */
	public Metadata getMetadata () { return metadata; }

	/**
	 * @return original OperationMetadata object
	 */
	public OperationMetadata getOpSpecificMetadata () { return metadata; }


	// queries specific to function

	/**
	 * get access to abstract description of function
	 * @param <T> data type used in the function
	 * @return identified target as Function
	 */
	@SuppressWarnings("unchecked")
	public <T> AbstractFunction <T> getFunction ()
	{ return ( AbstractFunction <T> ) metadata.getTarget (); }
	public String getName () { return name; }

	/**
	 * get value of delta for partial derivative evaluation
	 * @param variableIndex the parameter index which identifies the variable
	 * @return delta for partial derivative evaluation
	 */
	public double getDeltaFor (int variableIndex)
	{
		return getFunction ().getPartialDerivativeDelta (variableIndex);
	}


	// coordinates conversions

	public GenericValue toGenericValue
	(FunctionCoordinates.Coordinates coordinates)
	{ return getFunctionCoordinates ().represent (coordinates); }

	public FunctionCoordinates.Coordinates toVector (GenericValue parameter)
	{ return getFunctionCoordinates ().evaluate (parameter); }

	@SuppressWarnings({"rawtypes","unchecked"})
	public FunctionCoordinates getFunctionCoordinates ()
	{ return new FunctionCoordinates (metadata.getOp ().getEnvironment ()); }

	/**
	 * evaluate function at point
	 * @param point the coordinates of a point in domain space
	 * @return the function value at the domain point
	 */
	public GenericValue execute (FunctionCoordinates.Coordinates point)
	{
		return getFunction ().execute (toGenericValue (point));
	}


	// processing for evaluation point

	public GenericValue getEvaluationPoint ()
	{ return toGenericValue (metadata.getEvaluationPoint ()); }

	public void setEvaluationPoint
		(FunctionCoordinates.Coordinates evaluationPoint)
	{ metadata.setEvaluationPoint (evaluationPoint); }

	public void setEvaluationPoint (GenericValue point)
	{ setEvaluationPoint (toVector (point)); }


	// debugging data trace

	/**
	 * provide trace output
	 * @param parameter the data passed into the execution
	 */
	public void dump (GenericValue parameter)
	{
		FunctionCoordinates <?> FC = getFunctionCoordinates ();
		System.out.println ("POINT " + FC.evaluate (parameter));
		System.out.println ("VEC OP " + metadata.getOp ().getName ());
		System.out.println ("TARGET " + metadata.getTarget ().getName ());
		System.out.println ("TYPE " + metadata.getTarget ().getClass ().getCanonicalName ());
		System.out.println ("BODY " + metadata.getTarget ());
	}


}

