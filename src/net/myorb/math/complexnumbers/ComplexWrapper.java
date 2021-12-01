
package net.myorb.math.complexnumbers;

public class ComplexWrapper implements ComplexMarker
{
	ComplexMarker wrapped;
	public ComplexWrapper (ComplexMarker toWrap) { this.wrapped = toWrap; }
	public ComplexMarker getWrapped () { return wrapped; }
}
