
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.charting.DisplayGraphTypes;
import net.myorb.charting.DisplayGraphTypes.Point;

import net.myorb.data.abstractions.CommonDataStructures;
import net.myorb.data.abstractions.ErrorHandling;

/**
 * 3D plot control for vector field style plots
 * @param <T> data type for plot
 * @author Michael Druckman
 */
public class Plot3DVectorField <T> extends Plot3D <T>
{


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


	ValueManager.GenericValue getCallResult (double x, double y)
	{
		try  { doCall (x, y); }  catch  (Exception e)
		{ throw new ErrorHandling.Terminator (e.getMessage (), e); }
		return equation.topOfStack ();
	}


	void doCall (double x, double y)
	{
		Vector parameterValues = new Vector ();
		add (x, parameterValues); add (y, parameterValues);
		equation.copyParameters (parameterValues);
		equation.run ();
	}


	double angleFrom (ValueManager.MatrixValue <T> MV)
	{
		Vector V = new Vector (MV.getMatrix ().getRow (1).getElementsList ());
		return Math.atan2 (cvt (V.get (1)), cvt (V.get (0)));
	}


	double magnitude (ValueManager.MatrixValue <T> MV)
	{
		return sumSQ ( new Vector (MV.getMatrix ().getRow (1).getElementsList ()) );
	}


	double magnitude (ValueManager.DimensionedValue <T> DV)
	{
		return sumSQ ( new Vector ( DV.getValues () ) );
	}


	double sumSQ (Vector V)
	{
		double result = 0.0, v;
		for (int i = 0; i < V.size (); i++)
		{
			v = mgr.convertToDouble (V.get (i));
			result += v * v;
		}
		return Math.sqrt (result);
	}
	

	void add (double value, Vector parameters)
	{
		parameters.add (this.mgr.convertFromDouble (value));
	}


	public Plot3DVectorField
		(
			Subroutine <T> equation, Double vectorCount
		)
	{
		this.setEquation
		(
			equation, PlotComputers.getVectorFieldPlotComputer
					( this, vectorCount.intValue () )
		);
		this.setEdgeSize (0); this.setAltEdgeSize (0);
		this.setLowCorner ( new Point () );
		this.equation = equation;
	}
	public Plot3DVectorField () { super (); }
	protected Subroutine <T> equation;


	/**
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
		int ps = DisplayGraph3D.appropriatePointSize (ContourPlotEdgeSize);
		DisplayGraph3D.plotVectorField (setScale (ContourPlotEdgeSize, ps), title);
	}
	
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.Plot3D#getPlotEdgeSize()
	 */
	public int getPlotEdgeSize () { return ContourPlotEdgeSize; }
	public static void setContourPlotEdgeSize (int to) { ContourPlotEdgeSize = to; }
	public static int getContourPlotEdgeSize () { return ContourPlotEdgeSize; }
	static int ContourPlotEdgeSize = 200;


	private static final long serialVersionUID = 6489115687060243925L;

}
