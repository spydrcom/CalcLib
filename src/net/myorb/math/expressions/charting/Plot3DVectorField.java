
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.DisplayGraphTypes;

import net.myorb.data.abstractions.CommonDataStructures;
import net.myorb.data.abstractions.ErrorHandling;

/**
 * 3D plot control for vector field style plots
 * @param <T> data type for plot
 * @author Michael Druckman
 */
public class Plot3DVectorField <T> extends Plot3D <T>
{


	/**
	 * treat list of values as vector
	 */
	@SuppressWarnings("serial")
	class Vector extends CommonDataStructures.ItemList <T>
	{ Vector () {} Vector (java.util.List <T> items) { super (items); } }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.Plot3D#evaluate(double, double)
	 */
	@SuppressWarnings("unchecked")
	public int evaluate (double x, double y)
	{
		Double result = 0.0;

		ValueManager.GenericValue functionResult = getCallResult (x, y);

		if ( functionResult instanceof ValueManager.MatrixValue )
		{
			result = magnitude ( (ValueManager.MatrixValue <T>) functionResult );
		}
		else if ( functionResult instanceof ValueManager.DiscreteValue )
		{
			result = toDouble ( (ValueManager.DiscreteValue <T>) functionResult );
		}
		else if ( functionResult instanceof ValueManager.DimensionedValue )
		{
			result = magnitude ( (ValueManager.DimensionedValue <T>) functionResult );
		}
		else throw new RuntimeException (VECTOR_FIELD_ERROR);

		result *= this.getMultiplier ();
		return result.intValue ();
	}
	static final String VECTOR_FIELD_ERROR = "No field vector interpretation for function";


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.ContourPlotProperties#evaluateAngle(double, double)
	 */
	@SuppressWarnings("unchecked")
	public double evaluateAngle (double x, double y)
	{
		ValueManager.GenericValue
			functionResult = getCallResult (x, y);
		if ( functionResult instanceof ValueManager.MatrixValue )
		{ return angleFrom  ( (ValueManager.MatrixValue <T>) functionResult ); }
		else throw new RuntimeException (VECTOR_FIELD_ERROR);
	}


	/**
	 * call 2D function
	 * @param x the X parameter
	 * @param y the Y parameter
	 * @return the function result
	 */
	ValueManager.GenericValue getCallResult (double x, double y)
	{
		try  { doCall (x, y); }  catch  (Exception e)
		{ throw new ErrorHandling.Terminator (e.getMessage (), e); }
		return equation.topOfStack ();
	}


	/**
	 * execute parameter profile and run
	 * @param x the X parameter
	 * @param y the Y parameter
	 */
	void doCall (double x, double y)
	{
		Vector parameterValues = new Vector ();
		add (x, parameterValues); add (y, parameterValues);
		equation.copyParameters (parameterValues);
		equation.run ();
	}


	/**
	 * extract row of gradient matrix and express as vector
	 * @param MV the generic wrapper for a gradient matrix
	 * @return the vector extracted for first matrix row
	 */
	Vector getRowVector (ValueManager.MatrixValue <T> MV)
	{
		return new Vector ( MV.getMatrix ().getRow (1).getElementsList () );
	}


	/**
	 * use row of gradient matrix to compute vector direction
	 * @param MV the generic wrapper for a gradient matrix
	 * @return the angle formed by the vector values
	 */
	double angleFrom (ValueManager.MatrixValue <T> MV)
	{
		Vector V = getRowVector (MV);
		double X = cvt ( V.get (0) ), Y = cvt ( V.get (1) );
		return Math.atan2 ( Y, X );
	}


	/**
	 * use row of gradient matrix to compute vector magnitude
	 * @param MV the generic wrapper for a gradient matrix
	 * @return the magnitude of the described vector
	 */
	double magnitude (ValueManager.MatrixValue <T> MV)
	{
		return sumSQ ( getRowVector (MV) );
	}


	/**
	 * use array of values to compute vector magnitude
	 * @param DV the generic wrapper for an array of values
	 * @return the magnitude of the described vector
	 */
	double magnitude (ValueManager.DimensionedValue <T> DV)
	{
		return sumSQ ( new Vector ( DV.getValues () ) );
	}


	/**
	 * compute sum of squares of vector components
	 * - standard Pythagorean algorithm for distance computation
	 * @param V the vector to evaluate
	 * @return the magnitude
	 */
	double sumSQ ( Vector V )
	{
		double result = 0.0, v;
		for (int i = 0; i < V.size (); i++)
		{
			v = mgr.convertToDouble (V.get (i));
			result += v * v;
		}
		return Math.sqrt (result);
	}
	

	/**
	 * construct a parameter list for a function call
	 * @param value the value of the parameter to be included
	 * @param parameters the vector of parameters being compiled
	 */
	void add (double value, Vector parameters)
	{
		parameters.add (this.mgr.convertFromDouble (value));
	}


	/**
	 * @param equation the subroutine describing the function to plot
	 * @param vectorCount the number of direction indicators to include
	 */
	public Plot3DVectorField
		(
			Subroutine <T> equation, Double vectorCount
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


	/**
	 * build a plot computer for the field display
	 * @param vectorCount the number of direction indicators per axis
	 * @return the plot computer configured for field display
	 */
	public DisplayGraphTypes.PlotComputer
		identifyFieldPlotComputer (Double vectorCount)
	{
		return PlotComputers.getVectorFieldPlotComputer
			( this, vectorCount.intValue () );
	}


	/**
	 * get the compiled set of vector points in the field
	 * - this is the set of locations identified for the field path plot
	 * @return access to collected description of field
	 */
	public DisplayGraphTypes.VectorField.Locations
		getVectorPoints () { return this.vectorPoints; }
	protected DisplayGraphTypes.VectorField.Locations vectorPoints;
	public void setVectorPoints (DisplayGraphTypes.VectorField.Locations vectorPoints)
	{ this.vectorPoints = vectorPoints; }


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

