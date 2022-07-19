
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.charting.DisplayGraphTypes;

import net.myorb.math.MultiDimensional;

import net.myorb.gui.components.SimpleScreenIO;

/**
 * boiler plate for 3D plot control
 * @author Michael Druckman
 */
public class Plot3D<T> extends ContourPlotProperties
	implements PlotComputers.TransformProcessing,
		DisplayGraphTypes.ContourPlotDescriptor,
		Runnable
{


	public Plot3D () { super (-4); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.Transform3D#evaluate(double, double)
	 */
	public int evaluate (double x, double y)
	{
		@SuppressWarnings("unchecked") double result = multiplier *
			spaceManager.convertToDouble (equation.f (spaceManager.convertFromDouble (x), spaceManager.convertFromDouble (y)));
		return (int) (result);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.ContourPlotProperties#evaluateReal(double, double)
	 */
	public double evaluateReal (double x, double y)
	{
		@SuppressWarnings("unchecked") double result = multiplier *
			spaceManager.convertToDouble (equation.f (spaceManager.convertFromDouble (x), spaceManager.convertFromDouble (y)));
		return result;
	}


	/**
	 * @param equation any implementer of Multi-Dimensional
	 */
	public void setEquation (MultiDimensional.Function<T> equation)
	{
		this.equation = equation;
		this.spaceManager = (ExpressionSpaceManager<T>) equation.getSpaceDescription ();
		this.setPlotComputer (PlotComputers.getBruteForcePlotComputer (this));
		this.setEquation (this);
	}
	private ExpressionSpaceManager<T> spaceManager;
	private MultiDimensional.Function<T> equation;


	/**
	 * @param equation a vectored transform
	 */
	public void setEquation (MultiDimensionalVectored<T> equation)
	{
		this.setPointsPerAxis (equation.pointsPerAxis);
		this.setPlotComputer (PlotComputers.getVectorPlotComputer (this));
		this.vectorTransformProcessing = PlotComputers.getVectoredTransformProcessing
				(this, equation.getEnvironment ());
		this.vectoredEquation = equation;
		this.setEquation (this);
	}
	public MultiDimensionalVectored<T> getMultiDimensionalVectored () { return vectoredEquation; }
	private MultiDimensionalVectored<T> vectoredEquation;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotComputers.TransformProcessing#executeTransform()
	 */
	public PlotComputers.TransformResultsCollection executeTransform ()
	{
		return vectorTransformProcessing.executeTransform ();
	}
	protected PlotComputers.TransformProcessing vectorTransformProcessing;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.ContourPlotProperties#setMultiplier(double)
	 */
	public void setMultiplier (double multiplier) 
	{ super.setMultiplier (multiplier); this.multiplier = multiplier; }
	private double multiplier;


	/**
	 * @return units along edge in plot
	 */
	public int getPlotEdgeSize () { throw new RuntimeException ("Plot edge size not configured"); }


	/**
	 * display plot with frame titled as specified
	 * @param title the title to show on the frame
	 */
	public void show (String title)
	{
		this.title = title;
		this.getActivityDescriptor ().setTitle (title);
		SimpleScreenIO.startBackgroundTask (this);
	}
	protected String title;


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run () {}


	private static final long serialVersionUID = 5613614604502495618L;
}

