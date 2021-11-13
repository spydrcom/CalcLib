
package net.myorb.math.expressions.charting.fractals;

import net.myorb.gui.components.SimplePopupRequest;

/**
 * generalized form for Fractal menu
 * @author Michael Druckman
 */
public abstract class Selection
	extends SimplePopupRequest<Fractal>
	implements Runnable
{

	@Override public int getFieldWidth () { return 20; }
	@Override public String getFrameTitle () { return "Select Fractal"; }
	@Override public int getFrameHeight ()  { return 50; }
	@Override public int getFrameWidth () { return 400; }

	@Override
	public String formatNotificationFor(Fractal selectedItem) {
		return selectedItem.toString() + " chosen";
	}

	@Override
	public void setSelectedItem (Fractal item)
	{ this.item = item; new Thread (this).start (); }

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run () { item.plot (300, 5); }
	Fractal item;

}
