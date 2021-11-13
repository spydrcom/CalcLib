
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.evaluationstates.ExpressionMacro;
import net.myorb.math.expressions.ExpressionSpaceManager;

// chart lib
import net.myorb.charting.PlotLegend;

// JRE
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * a mouse listener that will trigger a legend display update
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class MouseSampleTrigger<T> extends MouseMotionHandler implements MouseListener
{


	/**
	 * the interface used by the display layer
	 */
	public interface SampleDisplay extends PlotLegend.SampleDisplay {}


	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked (MouseEvent e) {}
	public void mouseEntered (MouseEvent e) {}
	public void mousePressed (MouseEvent e) {}
	public void mouseExited  (MouseEvent e) {}


	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased (MouseEvent e)
	{
		int sampleCount;
		if (samples == null) return;
		double xValue = translateXAxis (e.getX ());
		List<T> valueList = macro.evaluate (xValue);
		String[] yValues = new String[sampleCount = valueList.size ()];
		for (int i = 0; i < sampleCount; i++)
		{
			yValues[i] = manager.toDecimalString (valueList.get (i));
		}
		samples.display (Double.toString (xValue), yValues);
	}


	/**
	 * connect the GUI that will provide the display
	 * @param samples a sample display
	 */
	public void setDisplay (PlotLegend.SampleDisplay samples) { this.samples = samples; }
	public PlotLegend.SampleDisplay getDisplay () { return samples; }
	protected PlotLegend.SampleDisplay samples = null;


	/**
	 * connect the macro that will compute the values
	 * @param macro the subroutine object
	 */
	public void setMacro (ExpressionMacro<T> macro)
	{
		this.manager = macro.getExpressionSpaceManager ();
		this.macro = macro;
	}
	protected ExpressionSpaceManager<T> manager = null;
	protected ExpressionMacro<T> macro = null;


	public MouseSampleTrigger (double scalingX, int axisWidth)
	{ super (scalingX, axisWidth, null); }
	public MouseSampleTrigger () {}


	private static final long serialVersionUID = 6432349172603041910L;
}

