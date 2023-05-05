
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.math.expressions.charting.multidimensional.VectorFieldPlotDescriptors;

import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.DisplayGraphTypes;

/**
 * 3D plot control for vector field style plots
 * @param <T> data type for plot
 * @author Michael Druckman
 */
public class Plot3DVectorField <T> extends VectorFieldPlotDescriptors <T>
{


	// function evaluation

	/**
	 * call 2D function and return result
	 * @param x the X-axis component of vector
	 * @param y the Y-axis component of vector
	 * @return the function result at the vector
	 */
	public ValueManager.GenericValue evaluate2DCall (double x, double y)
	{
		return equation.evaluateFunctionAt
		(
			// values treated as vector
			new Vector ( new Number[]{ x, y }, mgr )
		);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.Plot3D#evaluate(double, double)
	 */
	public int evaluate (double x, double y)
	{
		// get the 2D vector function result to use computing magnitude
		ValueManager.GenericValue functionResult = evaluate2DCall ( x, y );
		return evaluateMagnitude (functionResult);
	}
	static final String VECTOR_FIELD_ERROR = "No field vector interpretation for function";


	// analysis of function evaluations

	/**
	 * translate function result
	 * - capture angle value and return magnitude
	 * @param x coordinates to domain point (x-axis)
	 * @param y coordinates to domain point (y-axis)
	 * @param toEvaluationIndex the linear index in the buffer
	 * @return the value of the point for the contour plot
	 */
	public int executeContourEvaluation (double x, double y, int toEvaluationIndex)
	{
		ValueManager.GenericValue functionResult = evaluate2DCall (x, y);
		angle [ toEvaluationIndex ] = evaluateAngle (functionResult);
		return evaluateMagnitude (functionResult);
	}


	// computation of vector magnitude

	/**
	 * compute magnitude of vector
	 * @param V the vector result of the function
	 * @return the scalar magnitude
	 */
	@SuppressWarnings("unchecked")
	public int evaluateMagnitude (ValueManager.GenericValue V)
	{
		Double result = 0.0;

		if ( V instanceof ValueManager.MatrixValue )
		{
			// assume the first row is the vector result
			result = magnitude ( (ValueManager.MatrixValue <T>) V );
		}
		else if ( V instanceof ValueManager.DiscreteValue )
		{
			// a single value gives magnitude with no direction
			result = toDouble ( (ValueManager.DiscreteValue <T>) V );
		}
		else if ( V instanceof ValueManager.DimensionedValue )
		{
			// an array returned is treated as multiple component vector
			result = magnitude ( (ValueManager.DimensionedValue <T>) V );
		}
		else throw new RuntimeException (VECTOR_FIELD_ERROR);

		result *= this.getMultiplier ();
		return result.intValue ();
	}

	/**
	 * use row of gradient matrix to compute vector magnitude
	 * @param MV the generic wrapper for a gradient matrix
	 * @return the magnitude of the described vector
	 */
	public double magnitude (ValueManager.MatrixValue <T> MV)
	{
		return new Vector ( MV ).computeMagnitude ( mgr );
	}

	/**
	 * use array of values to compute vector magnitude
	 * @param DV the generic wrapper for an array of values
	 * @return the magnitude of the described vector
	 */
	public double magnitude (ValueManager.DimensionedValue <T> DV)
	{
		return new Vector ( DV ).computeMagnitude ( mgr );
	}


	// computation of vector direction

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.ContourPlotProperties#evaluateAngle(double, double)
	 */
	public double evaluateAngle (double x, double y)
	{
		return evaluateAngle (evaluate2DCall ( x, y ));
	}

	/**
	 * use row of gradient matrix to compute vector direction
	 * @param MV the generic wrapper for a gradient matrix
	 * @return the angle formed by the vector values
	 */
	public double angleFrom (ValueManager.MatrixValue <T> MV)
	{
		Vector V = new Vector ( MV );
		double X = cvt ( V.get (0) ), Y = cvt ( V.get (1) );
		return Math.atan2 ( Y, X ) + PI_OVER_2;
	}
	static final double PI_OVER_2 = Math.PI / 2;

	/**
	 * more efficient version called from plot computer
	 * @param functionResult the generic returned by evaluate
	 * @return the computed angle of the vector
	 */
	@SuppressWarnings("unchecked")
	public double evaluateAngle (ValueManager.GenericValue functionResult)
	{
		if ( functionResult instanceof ValueManager.MatrixValue )
		{ return angleFrom  ( (ValueManager.MatrixValue <T>) functionResult ); }
		else throw new RuntimeException (VECTOR_FIELD_ERROR);
	}


	// buffering of angle captures

	/**
	 * query angle capture at pixel index
	 * @param index the index of the capture of the angle
	 * @return the value of the angle at the index
	 */
	public double getAngleFrom (int index) { return angle [index]; }
	public void setAngle (ValueManager.GenericValue forValue, int atIndex)
	{ angle [ atIndex ] = evaluateAngle (forValue); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.ContourPlotProperties#allocateExtendedBuffer(int)
	 */
	public void allocateExtendedBuffer (int size)
	{ angle = new double [ this.bufferSize = size ]; }
	public int getPixelBufferSize () { return this.bufferSize; }
	protected double [] angle; protected int bufferSize;


	// constructor for descriptor

	/**
	 * @param equation the subroutine describing the function to plot
	 * @param vectorCount the number of direction indicators to include
	 */
	public Plot3DVectorField
		(
			Subroutine <T> equation, Number vectorCount
		)
	{
		this.setEquation
		( equation, identifyFieldPlotComputer (vectorCount) );
		this.setEdgeSize (0); this.setAltEdgeSize (0);
		this.setLowCorner ( new Point () );
		this.equation = equation;
	}
	public Plot3DVectorField () { super (); }
	protected Subroutine <T> equation;


	// plot computer specific description

	/**
	 * build a plot computer for the field display
	 * @param vectorCount the number of direction indicators per axis
	 * @return the plot computer configured for field display
	 */
	public DisplayGraphTypes.PlotComputer identifyFieldPlotComputer
					( Number vectorCount )
	{
		this.vectorCount = vectorCount.intValue ();
		return PlotComputers.getVectorFieldPlotComputer (this);
	}

	/**
	 * @return the requested number of direction indicators per axis
	 */
	public int getVectorCount () { return vectorCount; }
	protected int vectorCount;


	// interface implementations

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		this.setEquation (this);
		this.setPlotNumber (1000);
		this.setTransformIdentity (EQUATION_IDENTITY);
		int ps = DisplayGraph3D.appropriatePointSize (FIELD_PLOT_EDGE_SIZE);
		DisplayGraph3D.plotVectorField (setScale (FIELD_PLOT_EDGE_SIZE, ps), title);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.Plot3D#getPlotEdgeSize()
	 */
	public int getPlotEdgeSize () { return FIELD_PLOT_EDGE_SIZE; }
	static final int FIELD_PLOT_EDGE_SIZE = 200;


	private static final long serialVersionUID = 6489115687060243925L;

}

