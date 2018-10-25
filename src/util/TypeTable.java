package util;

public final class TypeTable {
	
	public final static int INTEGER = 0;
	public final static int FLOAT = 1;
	public final static int CHARACTER = 2;
	public final static int ANYTYPE = 3;
	
	
	public final static int[][] arithmeticOperators = {
			{INTEGER, FLOAT, ANYTYPE}, 		// E.g. INTEGER + X
			{FLOAT, FLOAT, ANYTYPE},		// E.g. FLOAT + X
			{ANYTYPE, ANYTYPE, ANYTYPE}		// E.g. CHARACTER + X
	};
	
	public final static int[][] logicalOperators = {
			{CHARACTER, ANYTYPE, ANYTYPE}, 	// E.g. CHARACTER OR X	
			{ANYTYPE, INTEGER, INTEGER},	// E.g. INTEGER OR X
			{ANYTYPE, INTEGER, FLOAT}		// E.g. FLOAT OR X
	};
	
	public final static int[][] relationalOperators = {
			{INTEGER, ANYTYPE, ANYTYPE},	// E.g. CHARACTER < X
			{ANYTYPE, INTEGER, INTEGER},	// E.g. INTEGER < X
			{ANYTYPE, INTEGER, INTEGER}		// E.g. FLOAT < X
	};
	
	public final static int[][] modOperator = {
			{INTEGER, ANYTYPE, ANYTYPE}, 		// E.g. INTEGER MOD X
			{ANYTYPE, ANYTYPE, ANYTYPE},		// E.g. FLOAT MOD X
			{ANYTYPE, ANYTYPE, ANYTYPE}			// E.g. CHARACTER MOD X
	};
	
	
	public final static int[] notOperator = { INTEGER, ANYTYPE, ANYTYPE }; // E.g. NOT X
	
	public static String getTypeName(int type) {
		String strType = "";
		switch (type) {
		case 0:
			strType = "INTEGER";
			break;
		case 1:
			strType = "FLOAT";
			break;
		case 2:
			strType = "CHARACTER";
			break;
		case 3:
			strType = "ANYTYPE";
			break;
			default:
				strType = "error";
		}
		return strType;
	}

}
