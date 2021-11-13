
package net.myorb.math.expressions;

import net.myorb.math.*;

public interface SymbolTableManagerI<T>
{

	void importFromPowerLibrary (SymbolMap into);
	void importFromPowerLibrary (final PowerLibrary<T> powerLibrary, SymbolMap into);
	void importFromSpeedLibrary (final HighSpeedMathLibrary speedLibrary, SymbolMap into);
	void importFromTrigLibrary (final TrigIdentities<T> trigLibrary, SymbolMap into);
	void importFromSpaceManager (SymbolMap into);

}
