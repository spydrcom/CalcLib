
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.data.abstractions.CommonDataStructures;

import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.DisplayGraphTypes;

/**
 * 3D plot control for vector field style plots
 * @param <T> data type for plot
 * @author Michael Druckman
 */
public class Plot3DVectorField <T> extends Plot3D <T>
{


	/**
	 * a local description of the field data
	 */
	@SuppressWarnings("serial") public static class VectorFieldPoints
		extends DisplayGraphTypes.VectorField.Locations {}

	/**
	 * allocate a new VectorField Location set
	 * @return a new structure that will hold a field description
	 */
	public static VectorFieldPoints pointsList ()
	{ return new VectorFieldPoints (); }


	/**
	 * treat list of values as vector
	 */
	@SuppressWarnings("serial")
	class Vector extends CommonDataStructures.ItemList <T>
	{

		/**
		 * construct a parameter list for a function call
		 * @param items the values of the parameter to be included
		 */
		Vector (double [] items)
		{
			for (double item : items)
			{
				this.add ( mgr.convertFromDouble (item) );
			}
		}

		/**
		 * use array of values as vector components
		 * @param DV the generic wrapper for an array of values
		 */
		Vector (ValueManager.DimensionedValue <T> DV) { super ( DV.getValues () ); }

		/**
		 * extract row of gradient matrix and express as vector
		 * @param MV the generic wrapper for a gradient matrix
		 */
		Vector (ValueManager.MatrixValue <T> MV)
		{
			super ( MV.getMatrix ().getRow (1).getElementsList () );
		}

		/**
		 * compute sum of squares of vector components
		 * - standard Pythagorean algorithm for distance computation
		 * @return the magnitude
		 */
		double computeMagnitude ()
		{
			double result = 0.0, v;
			for (int i = 0; i < this.size (); i++)
			{
				v = mgr.convertToDouble (this.get (i));
				result += v * v;
			}
			return Math.sqrt (result);
		}

	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.Plot3D#evaluate(double, double)
	 */
	@SuppressWarnings("unchecked")
	public int evaluate (double x, double y)
	{
		Double result = 0.0;

		// get the 2D vector function result to use computing magnitude
		ValueManager.GenericValue functionResult = evaluate2DCall (x, y);

		if ( functionResult instanceof ValueManager.MatrixValue )
		{
			// assume the first row is the vector result
			result = magnitude ( (ValueManager.MatrixValue <T>) functionResult );
		}
		else if ( functionResult instanceof ValueManager.DiscreteValue )
		{
			// a single value gives magnitude with no direction
			result = toDouble ( (ValueManager.DiscreteValue <T>) functionResult );
		}
		else if ( functionResult instanceof ValueManager.DimensionedValue )
		{
			// an array returned is treated as multiple component vector
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
			functionResult = evaluate2DCall (x, y);
		if ( functionResult instanceof ValueManager.MatrixValue )
		{ return angleFrom  ( (ValueManager.MatrixValue <T>) functionResult ); }
		else throw new RuntimeException (VECTOR_FIELD_ERROR);
	}


	/**
	 * call 2D function and return result
	 * @param x the X-axis component of vector
	 * @param y the Y-axis component of vector
	 * @return the function result at the vector
	 */
	ValueManager.GenericValue evaluate2DCall (double x, double y)
	{
		return equation.evaluateFunctionAt
		(
			// values treated as vector
			new Vector ( new double[]{ x, y } )
		);
	}


	/**
	 * use row of gradient matrix to compute vector direction
	 * @param MV the generic wrapper for a gradient matrix
	 * @return the angle formed by the vector values
	 */
	double angleFrom (ValueManager.MatrixValue <T> MV)
	{
		Vector V = new Vector (MV);
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
		return new Vector (MV).computeMagnitude ();
	}


	/**
	 * use array of values to compute vector magnitude
	 * @param DV the generic wrapper for an array of values
	 * @return the magnitude of the described vector
	 */
	double magnitude (ValueManager.DimensionedValue <T> DV)
	{
		return new Vector ( DV ).computeMagnitude ();
	}


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


	/**
	 * build a plot computer for the field display
	 * @param vectorCount the number of direction indicators per axis
	 * @return the plot computer configured for field display
	 */
	public DisplayGraphTypes.PlotComputer
		identifyFieldPlotComputer (Number vectorCount)
	{
		return PlotComputers.getVectorFieldPlotComputer
			( this, vectorCount.intValue () );
	}


	/**
	 * get the compiled set of vector points in the field
	 * - this is the set of locations identified for the field path plot
	 * @return access to collected description of field
	 */
	public VectorFieldPoints getVectorPoints () { return this.vectorPoints; }
	public void setVectorPoints (VectorFieldPoints vectorPoints) { this.vectorPoints = vectorPoints; }
	protected VectorFieldPoints vectorPoints;


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

