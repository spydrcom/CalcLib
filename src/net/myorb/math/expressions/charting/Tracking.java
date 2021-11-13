
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.charting.fractals.Fractal;

// expression evaluation
import net.myorb.math.expressions.evaluationstates.FunctionDefinition;
import net.myorb.math.expressions.evaluationstates.Subroutine;

// charting
import net.myorb.charting.DisplayGraphTypes;

// IOLIB abstractions
import net.myorb.data.abstractions.SimpleUtilities;
import net.myorb.data.abstractions.SimpleXmlHash.Document;
import net.myorb.data.abstractions.SimpleXmlHash.Node;

// IOLIB GUI
import net.myorb.gui.components.SimpleTableAdapter;
import net.myorb.gui.components.SimpleScreenIO;

// JRE
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * compile table of contour plots
 * @author Michael Druckman
 */
public class Tracking extends SimpleScreenIO
{


	public static class PlotMap
		extends HashMap<Integer,ContourPlotProperties>
	{ private static final long serialVersionUID = 7963744658849475806L; }

	public static class DescriptorMap
		extends HashMap<String,DisplayGraphTypes.ContourPlotDescriptor>
	{ private static final long serialVersionUID = -6291443674628932797L; }


	/**
	 * add a plot to the tracking list
	 * @param descriptor a description of what is being shown
	 * @param title a title for the frame of the plot
	 * @param properties the properties required
	 */
	public void add
		(
			DisplayGraphTypes.ContourPlotDescriptor descriptor,
			String title, ContourPlotProperties properties
		)
	{
		Object[] columns = new Object[5];
		columns[0] = plotIdentifier; columns[1] = title; columns[2] = descriptor.getLowCorner ();
		columns[3] = descriptor.getEdgeSize (); columns[4] = descriptor.getPlotParent ();
		table.extendBy (1); table.appendRow (columns); table.refreshTable (); show ();

		properties.setTitle (title);
		properties.setTransformIdentity (descriptor.identifyTransform ());
		properties.setPlotParent (descriptor.getPlotParent ());

		if (descriptor.getPlotNumber () == 0)
		{ descriptor.setPlotNumber (plotIdentifier); }
		properties.setPlotNumber (plotIdentifier);
		plots.put (plotIdentifier++, properties);

		try { ((PlotTable) table).save (); }
		catch (Exception e) { e.printStackTrace (); }

		//System.out.println (properties);
		//dump ();
	}
	public void add
		(
			String title, Map<String,Object> descriptor
		)
	{
		ContourPlotProperties properties = new ContourPlotProperties (-10);
		properties.setTransformIdentity (ContourPlotProperties.POLAR_IDENTITY);
		properties.setPlotNumber (plotIdentifier); properties.putAll (descriptor);
		plots.put (plotIdentifier, properties);
		properties.setTitle (title);

		Object[] columns = new Object[5];
		columns[0] = plotIdentifier++; columns[1] = title; columns[2] = descriptor.get ("POLYDEG");
		columns[3] = descriptor.get ("InnerVariable"); columns[4] = properties.getPlotParent ();
		table.extendBy (1); table.appendRow (columns); table.refreshTable (); show ();
	
		try { ((PlotTable) table).save (); }
		catch (Exception e) { e.printStackTrace(); }

		//System.out.println (properties);
		//dump ();
	}
	private PlotMap plots = new PlotMap ();
	private int plotIdentifier = 1000;
	
	/**
	 * show the accumulated properties
	 */
	void dump ()
	{
		for (Integer i : plots.keySet ())
		{ System.out.println (plots.get (i)); }
		System.out.println ("---");
	}

	/**
	 * @param parentPanel panel that will contain table
	 */
	private void addTableTo (Panel parentPanel)
	{ (table = new PlotTable (plots, equations, this, complexPolar)).addTableToPanel (parentPanel); }
	protected SimpleTableAdapter table;


	/**
	 * add table to panel
	 * @param complexPolar TRUE implies POLAR plots list
	 */
	private void build (boolean complexPolar)
	{
		this.complexPolar = complexPolar;
		addTableTo (mainPanel = startGridPanel (null, 0, 1));
	}
	private boolean complexPolar;
	private Panel mainPanel;


	/**
	 * show panel in frame
	 */
	public void show ()
	{
		if (frame == null)
			frame = show (mainPanel, "Contour Plots", 600, 300);
		else frame.forceToScreen ();
	}
	private Frame frame = null;


	/**
	 * build main panel
	 * @param complexPolar TRUE implies POLAR plots list
	 */
	public Tracking (boolean complexPolar)
	{
		build (complexPolar);
		try { ((PlotTable)table).load (); }
		catch (Exception e) { e.printStackTrace (); }
	}


	/**
	 * @param complexPolar TRUE implies POLAR plots list
	 * @return the tracking object
	 */
	public static Tracking getInstance (boolean complexPolar)
	{
		if (instance == null)
			instance = new Tracking (complexPolar);
		return instance;
	}
	static Tracking instance = null;


	/**
	 * unit test
	 * @param args not used
	 * @throws Exception for errors
	 */
	public static void main (String... args) throws Exception
	{ setTrackingFile (FRACTALS); getInstance (false).show (); }
	static String FRACTALS = "fractalsWorkspace.xml";


	/**
	 * @param trackingFile name of file that mirrors tracking
	 */
	public static void setTrackingFile (String trackingFile) { fileName = trackingFile; }
	public static String getFilePath () { return PLOTS_DIRECTORY + fileName; }
	private static final String PLOTS_DIRECTORY = "plots/";
	public static String fileName = "tracking.xml";


	/**
	 * @return XML Document of tracking file
	 * @throws Exception for any errors
	 */
	public static Document getDocument () throws Exception
	{
		return Document.read (new FileInputStream (getFilePath ()));
	}


	/**
	 * @param def function definition object
	 * @throws Exception for any errors
	 * @param <T> data type
	 */
	public static <T> void loadEquations (FunctionDefinition<T> def) throws Exception
	{
		String profile;
		for (Node node : getDocument ().getNodeList ())
		{
			if ((profile = node.get (ContourPlotProperties.PROFILE)) != null)
			{
				if (equations.containsKey (profile)) continue;
				String body = node.get (ContourPlotProperties.BODY);
				Subroutine<T> symbol = Subroutine.cast (def.processFunctionDefinition (profile, body));
				double multiplier = Double.parseDouble (node.get (ContourPlotProperties.MULTIPLIER));
				//System.out.println (profile + " : " + symbol.toString () + " : " + multiplier);
				Plot3D<T> plot = new Plot3DContour<T> (symbol);
				plot.setMultiplier (multiplier);
				equations.put (profile, plot);
			}
		}
		//System.out.println (equations);
	}
	private static DescriptorMap equations = new DescriptorMap ();


}


/**
 * table of contour plots
 */
class PlotTable extends SimpleTableAdapter
{

	PlotTable
		(
			Tracking.PlotMap plots, Tracking.DescriptorMap equations,
			Tracking tracking, boolean complexPolar
		)
	{
		super (complexPolar? POLAR_COLUMNS : EQN_COLUMNS, INITIAL_TABLE_SIZE);
		this.plots = plots; this.tracking = tracking; this.complexPolar = complexPolar; this.equations = equations;
	}
	public static final String[] EQN_COLUMNS =
			new String[]{"Identity", "Title", "Low Corner", "Edge Size", "Parent"};
	public static final String[] POLAR_COLUMNS = new String[]{"Identity", "Title", "Degree", "Inner", "Parent"};
	public static final int INITIAL_TABLE_SIZE = 2;
	private boolean complexPolar;
	private Tracking tracking;

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.SimpleTableAdapter#doDoubleClick(int)
	 */
	public void doDoubleClick (int forRowNumber)
	{
		ContourPlotProperties
			p = plots.get (Integer.parseInt (get (forRowNumber, 0).toString ()));
		if (complexPolar) { ComplexPlaneTransform.constructPlot (p); }
		else equationOrFractal (p, getTitle (p));
	}
	private static final String
		TITLE = ContourPlotProperties.TITLE,
		IDENTITY = ContourPlotProperties.TRANSFORM_IDENTITY,
		EQUATION = ContourPlotProperties.EQUATION_IDENTITY;
	private Tracking.DescriptorMap equations;
	private Tracking.PlotMap plots;

	/**
	 * @param p the properties of the plot
	 * @return a title for the plot
	 */
	private String getTitle (ContourPlotProperties p)
	{
		String title = "Rebuild";
		if (p.containsKey (TITLE)) title = p.getTitle ();
		else if (p.containsKey (IDENTITY)) title = p.identifyTransform ();
		return title;
	}

	/**
	 * @param p the properties of a plot (equation or fractal)
	 * @param title the title of the plot
	 */
	private void equationOrFractal (ContourPlotProperties p, String title)
	{ DisplayGraph3D.plotContour (isEquation (p) ? describeEquation (p) : describeFractal (p), title); }
	private boolean isEquation (ContourPlotProperties p) { return EQUATION.equals (p.identifyTransform ()); }

	/**
	 * @param p the properties of a equation plot object
	 * @return plot object described
	 */
	public ContourPlotProperties describeEquation (ContourPlotProperties p)
	{
		ContourPlotProperties plot; int n = p.getPlotNumber ();
		while (!p.isEquationSet ())
		{
			if (n < 0) throw new RuntimeException ("Equation not available");
			if ((plot = plots.get (n)).hasProfile ()) { p.setEquation (equations.get (plot.getProfile ())); }
			n = plot.getPlotParent ();
		}
		return p;
	}

	/**
	 * @param properties the properties of a fractal object
	 * @return the fractal with scaling evaluation applied
	 */
	private ContourPlotProperties describeFractal (ContourPlotProperties properties)
	{ return getFractal (properties).setScale (properties.getPointsPerAxis (), properties.getPointsSize ()); }

	/**
	 * @param p the properties of a fractal object
	 * @return fractal object described
	 */
	private Fractal getFractal (ContourPlotProperties p)
	{
		Fractal f = Fractal.reconstituteFractal (p.identifyTransform (), p.getLowCorner (), p.getEdgeSize ());
		if (f != null) return f; else throw new RuntimeException ("Unable to rebuild fractal");
	}

	/**
	 * @throws Exception for errors
	 */
	public void load () throws Exception
	{
		ContourPlotProperties plot;
		for (Node node : Tracking.getDocument ().getNodeList ())
		{
			(plot = new ContourPlotProperties (-20)).putAll (node.withOutName ());
			if (!plot.containsKey (ContourPlotProperties.PLOT_NUMBER)) continue;
			if (complexPolar) tracking.add (plot.getTitle (), plot);
			else tracking.add (plot, plot.getTitle (), plot);
		}
		resizeTo (nextRow + 1);
	}

	/**
	 * @throws Exception for errors
	 */
	public void save () throws Exception
	{
		Document document = new Node ("PLOTS").toDocumentRoot ();

		for (Integer id : SimpleUtilities.orderedKeys (plots))
		{
			new Node ("PLOT").setAll (plots.get (id)).addTo (document);
		}

		document.write (new FileOutputStream (Tracking.getFilePath ()));
	}

}

