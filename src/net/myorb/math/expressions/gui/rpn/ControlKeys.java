
package net.myorb.math.expressions.gui.rpn;

import java.util.HashMap;
import java.util.Map;

public class ControlKeys
{

	public static final String
	ENTER = "KEY$ENTER",
	ESC = "KEY$ESC",
	DEL = "KEY$DEL",
	BS = "KEY$BS",
	PGUP = "KEY$PGUP",
	PGDN = "KEY$PGDN",
	END = "KEY$END",
	HOME = "KEY$HOME",
	ARROW_LT = "KEY$ARROW_LT",
	ARROW_UP = "KEY$ARROW_UP",
	ARROW_RT = "KEY$ARROW_RT",
	ARROW_DN = "KEY$ARROW_DN",
	F1 = "KEY$F1",
	F2 = "KEY$F2",
	F3 = "KEY$F3",
	F4 = "KEY$F4",
	F5 = "KEY$F5",
	F6 = "KEY$F6",
	F7 = "KEY$F7",
	F8 = "KEY$F8",
	F9 = "KEY$F9",
	F10 = "KEY$F10",
	F11 = "KEY$F11",
	F12 = "KEY$F12",
	INS = "KEY$INS";

	public static final Map<Integer, String> CHARACTER_MAP = new HashMap<Integer, String>();

	static
	{
		CHARACTER_MAP.put (8, BS);
		CHARACTER_MAP.put (10, ENTER);
		CHARACTER_MAP.put (27, ESC);
		CHARACTER_MAP.put (127, DEL);
		CHARACTER_MAP.put (33, PGUP);
		CHARACTER_MAP.put (34, PGDN);
		CHARACTER_MAP.put (35, END);
		CHARACTER_MAP.put (36, HOME);
		CHARACTER_MAP.put (37, ARROW_LT);
		CHARACTER_MAP.put (38, ARROW_UP);
		CHARACTER_MAP.put (39, ARROW_RT);
		CHARACTER_MAP.put (40, ARROW_DN);
		CHARACTER_MAP.put (112, F1);
		CHARACTER_MAP.put (113, F2);
		CHARACTER_MAP.put (114, F3);
		CHARACTER_MAP.put (115, F4);
		CHARACTER_MAP.put (116, F5);
		CHARACTER_MAP.put (117, F6);
		CHARACTER_MAP.put (118, F7);
		CHARACTER_MAP.put (119, F8);
		CHARACTER_MAP.put (120, F9);
		CHARACTER_MAP.put (121, F10);
		CHARACTER_MAP.put (122, F11);
		CHARACTER_MAP.put (123, F12);
		CHARACTER_MAP.put (155, INS);
	}

}
