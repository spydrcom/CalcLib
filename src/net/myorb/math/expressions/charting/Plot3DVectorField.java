
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.evaluationstates.Subroutine;
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

		try  { doCall (x, y); }  catch  (Exception e)
		{ throw new ErrorHandling.Terminator (e.getMessage (), e); }

		ValueManager.GenericValue tos = equation.topOfStack ();

		if ( tos instanceof ValueManager.MatrixValue )
		{
			result = magnitude ( (ValueManager.MatrixValue <T>) tos );
		}
		else if ( tos instanceof ValueManager.DiscreteValue )
		{
			result = toDouble ( (ValueManager.DiscreteValue <T>) tos );
		}
		else if ( tos instanceof ValueManager.DimensionedValue )
		{
			result = magnitude ( (ValueManager.DimensionedValue <T>) tos );
		}
		else throw new RuntimeException (VECTOR_FIELD_ERROR);

		result *= this.getMultiplier ();
		return result.intValue ();
	}
	static final String VECTOR_FIELD_ERROR = "No field vector interpretation for function";


	void doCall (double x, double y)
	{
		Vector parameterValues = new Vector ();
		add (x, parameterValues); add (y, parameterValues);
		equation.copyParameters (parameterValues);
		equation.run ();
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
		return result;
	}
	

	void add (double value, Vector parameters)
	{ parameters.add (this.mgr.convertFromDouble (value)); }
	double toDouble (ValueManager.DiscreteValue <T> DV) { return this.cvt ( DV.getValue () ); }
	double cvt (T value) { return this.mgr.convertToDouble (value); }


	public Plot3DVectorField
		(
			Subroutine <T> equation, Double vectorCount
		)
	{
		this.setEquation (equation);
		this.setEdgeSize (0); this.setAltEdgeSize (0);
		this.vectorCount = vectorCount.intValue ();
		this.setLowCorner (new Point ());
		this.equation = equation;
	}
	public Plot3DVectorField () { super (); }
	protected Subroutine <T> equation;
	protected int vectorCount;


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		this.setEquation (this);
		this.setPlotNumber (1000);
		this.setTransformIdentity (EQUATION_IDENTITY);
		int ps = DisplayGraph3D.appropriatePointSize (ContourPlotEdgeSize);
		DisplayGraph3D.plotContour (setScale (ContourPlotEdgeSize, ps), title);
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
