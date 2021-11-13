
package net.myorb.math.expressions.gui.rpn;

import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.AbstractButton;

import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.Dimension;

import java.util.HashMap;
import java.util.Map;

/**
 * selector panel for trigonometry functions
 * @author Michael Druckman
 */
public class TrigPanel extends ButtonManager
{

	/**
	 * allow clearable functionality
	 */
	interface Clearable
	{
		void clear ();
	}

	/**
	 * allow refreshable functionality
	 */
	interface Refreshable
	{
		/**
		 * refresh relative to selected states
		 * @param inv inverse functions are selected
		 * @param hyp hyperbolic functions are selected 
		 * @param co CO functions are selected
		 */
		void refresh (boolean inv, boolean hyp, boolean co);
	}

	static final Map<String,String> CONVERSIONS = new HashMap<String,String> ();
	static final String[] CIRCULAR = new String[]{"SIN", "COS", "TAN", "COT", "SEC", "CSC"};

	static
	{
		for (String function : CIRCULAR)
		{
			add2Conversions ("DEG", function, "DTR", "RTD"); // degree to radian | radian to degree
			add2Conversions ("GRD", function, "GTR", "RTG"); // gradian to radian | radian to gradian
		}
	}
	public static void add2Conversions (String angle, String function, String pre, String post)
	{
		String functionAngle = function + "|" + angle + "|";
		CONVERSIONS.put ("A" + functionAngle + "POST", post);	// ARC function require conversion after call (radians returned by function)
		CONVERSIONS.put (functionAngle + "PRE", pre);			// basic functions require conversion prior to call (function takes radians)
	}

	/**
	 * emit pre-conversion where appropriate
	 * @param functionAngle the key identifying function and angle expression
	 * @param request the request processor object
	 */
	public static void preConvert (String functionAngle, OperationRequest request)
	{
		String conversion = CONVERSIONS.get (functionAngle + "PRE");
		if (conversion != null) request.perform (conversion);
	}

	/**
	 * emit post-conversion where appropriate
	 * @param functionAngle the key identifying function and angle expression
	 * @param request the request processor object
	 */
	public static void postConvert (String functionAngle, OperationRequest request)
	{
		String conversion = CONVERSIONS.get (functionAngle + "POST");
		if (conversion != null) request.perform (conversion);
	}

	/**
	 * construct the request panel
	 * @param map the map of GUI elements
	 * @return the swing panel
	 */
	public static JPanel getPanel (Map<String,Object> map)
	{
		JPanel panel = new JPanel ();
		panel.setLayout (new GridLayout(3,3));

		AbstractButton rad, deg, grd;
		ButtonGroup group = new ButtonGroup ();
		group.add (rad = newButton (new RadButton (map), "Angles in Radians"));
		group.add (deg = newButton (new DegButton (map), "Angles in Degrees"));
		group.add (grd = newButton (new GrdButton (map), "Angles in Gradians"));
		rad.setSelected (true); map.put ("Angles$Expressed", "RAD");

		AbstractButton
		inv = newButton (new InvButton (map), "Inverse of selected function"),
		hyp = newButton (new HypButton (map), "Hyperbolic version of selected function"),
		co = newButton (new CoButton (map), "CO-Function of selected function");

		AbstractButton
		sin = newButton (new SinButton (map), "SIN/COS depending on CO button"),
		tan = newButton (new TanButton (map), "TAN/COT (Tangent/Cotangent) depending on CO button"),
		sec = newButton (new SecButton (map), "SEC/CSC (Secant/Cosecant) depending on CO button");

		panel.add (rad); panel.add (inv); panel.add (sin);
		panel.add (deg); panel.add (hyp); panel.add (tan);
		panel.add (grd); panel.add (co);  panel.add (sec);

		panel.setPreferredSize (new Dimension (300, 100));
		return panel;
	}

	/**
	 * clear all clearable objects
	 * @param map the map of GUI elements
	 */
	public static void clear (Map<String,Object> map)
	{
		((Clearable)map.get ("INV$ACT")).clear ();
		((Clearable)map.get ("HYP$ACT")).clear ();
		((Clearable)map.get ("CO$ACT")).clear ();
		refresh (map);
	}

	/**
	 * refresh all refreshable objects
	 * @param map the map of GUI elements
	 */
	public static void refresh (Map<String,Object> map)
	{
		boolean
		inv = map.containsKey ("InverseSelected"),
		hyp = map.containsKey ("HyperbolicSelected"),
		co = map.containsKey ("CO-Function");
		
		((Refreshable)map.get ("SIN$ACT")).refresh (inv, hyp, co);
		((Refreshable)map.get ("TAN$ACT")).refresh (inv, hyp, co);
		((Refreshable)map.get ("SEC$ACT")).refresh (inv, hyp, co);
	}

}

/**
 * describe a toggle button that is clearable
 */
class ToggledClearable extends Toggled implements TrigPanel.Clearable
{

	ToggledClearable (String name, String selection, Map<String,Object> map)
	{
		super (name, map); this.selection = selection;
	}
	String selection;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rpn.MappedButtonComponent#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0)
	{
		if (button.isSelected ())
		{ map.put (selection, "TRUE"); }
		else  { map.remove (selection); }
		ButtonManager.coreReset (map);
		TrigPanel.refresh (map);
	}
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rpn.TrigPanel.Clearable#clear()
	 */
	public void clear ()
	{
		button.setSelected (false);
		map.remove (selection);
	}
	
}

/**
 * INV button.
 * toggle button that select INVERSE versions of functions
 */
class InvButton extends ToggledClearable
{
	InvButton (Map<String,Object> map)
	{ super ("INV", "InverseSelected", map); }
}

/**
 * HYP button.
 * toggle button that select HYPERBOLIC versions of functions
 */
class HypButton extends ToggledClearable
{
	HypButton (Map<String,Object> map)
	{ super ("HYP", "HyperbolicSelected", map); }
}

/**
 * CO button.
 * toggle button that select CO versions of functions (SIN => COS, TAN => COT, ...)
 */
class CoButton extends ToggledClearable
{
	CoButton (Map<String,Object> map)
	{ super ("CO", "CO-Function", map); }
}

/**
 * emit sequences of operations implementing request
 */
class Enact extends Button implements TrigPanel.Refreshable
{

	Enact (String baseName, String coName, Map<String,Object> map)
	{
		super (map, baseName);
		this.request = (OperationRequest) map.get ("REQUEST");
		this.baseName = baseName;
		this.coName = coName;
	}
	OperationRequest request;
	String baseName, coName;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rpn.MappedButtonComponent#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0)
	{
		if (request != null)
		{
			String selected = button.getText ();
			String angle = map.get ("Angles$Expressed").toString ();
			String functionAngle = selected + "|" + angle + "|";
			TrigPanel.preConvert (functionAngle, request);
			request.perform (selected.toLowerCase ());
			TrigPanel.postConvert (functionAngle, request);
		}
		ButtonManager.coreReset (map);
		TrigPanel.clear (map);
	}
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rpn.TrigPanel.Refreshable#refresh(boolean, boolean, boolean)
	 */
	public void refresh (boolean inv, boolean hyp, boolean co)
	{
		String name = co? coName: baseName;

		if (hyp)
		{
			name = name + "H";
			if (inv) name = "AR" + name;
		} else if (inv) name = "A" + name;
		button.setText (name);
	}

}

/**
 * SIN button.
 * button that executes SIN/COS function calls
 */
class SinButton extends Enact
{
	SinButton (Map<String,Object> map)
	{
		super ("SIN", "COS", map);
	}
}

/**
 * TAN button.
 * button that executes TAN/COT function calls
 */
class TanButton extends Enact
{
	TanButton (Map<String,Object> map)
	{
		super ("TAN", "COT", map);
	}
}

/**
 * SEC button.
 * button that executes SEC/CSC function calls
 */
class SecButton extends Enact
{
	SecButton (Map<String,Object> map)
	{
		super ("SEC", "CSC", map);
	}
}

/**
 * base class for control of angle expression
 */
class Angle extends ToggledClearable
{
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rpn.ToggledClearable#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0)
	{
		map.put ("Angles$Expressed", name);
		ButtonManager.coreReset (map);
		TrigPanel.refresh (map);
	}
	Angle (Map<String,Object> map, String name)
	{ super (name, name, map); }
}

/**
 * RAD button.
 * toggle button that selects angle expression in radians
 */
class RadButton extends Angle
{
	RadButton (Map<String,Object> map)
	{ super (map, "RAD"); }
}

/**
 * DEG button.
 * toggle button that selects angle expression in degrees
 */
class DegButton extends Angle
{
	DegButton (Map<String,Object> map)
	{ super (map, "DEG"); }
}

/**
 * GRD button.
 * toggle button that selects angle expression in gradians
 */
class GrdButton extends Angle
{
	GrdButton (Map<String,Object> map)
	{ super (map, "GRD"); }
}
