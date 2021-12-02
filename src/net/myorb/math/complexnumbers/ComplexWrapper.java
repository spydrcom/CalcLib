
package net.myorb.math.complexnumbers;

/**
 * base class for transport for entries of complex domain
 * @author Michael Druckman
 */
public class ComplexWrapper implements ComplexMarker
{
	ComplexMarker wrapped;
	public ComplexWrapper (ComplexMarker toWrap) { this.wrapped = toWrap; }
	public ComplexMarker getWrapped () { return wrapped; }
}
