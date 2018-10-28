package util;

public final class TypeTable {
	
	public final static int CHARACTER = 0;
	public final static int INTEGER = 1;
	public final static int FLOAT = 2;
	public final static int ANYTYPE = 3;
	
	
	public final static int[][] arithmeticOperators = {
			{ANYTYPE,	ANYTYPE, ANYTYPE, ANYTYPE},		// E.g. CHARACTER 	+ X
			{ANYTYPE,	INTEGER, FLOAT,   INTEGER}, 	// E.g. INTEGER 	+ X
			{ANYTYPE,	FLOAT,   FLOAT,   FLOAT  },		// E.g. FLOAT 		+ X
			{ANYTYPE,   INTEGER, FLOAT,   ANYTYPE}		// E.g. ANYTYPE 	+ X
	};
	
	public final static int[][] logicalOperators = {
			{CHARACTER, ANYTYPE, ANYTYPE, CHARACTER}, 	// E.g. CHARACTER 	OR X	
			{ANYTYPE,   INTEGER, INTEGER, INTEGER  },	// E.g. INTEGER 	OR X
			{ANYTYPE,   INTEGER, FLOAT,   FLOAT    },	// E.g. FLOAT 		OR X
			{CHARACTER, INTEGER, FLOAT,   ANYTYPE  }	// E.g. ANYTYPE 	OR X
	};
	
	public final static int[][] relationalOperators = {
			{INTEGER, ANYTYPE, ANYTYPE, INTEGER},		// E.g. CHARACTER 	< X
			{ANYTYPE, INTEGER, INTEGER, INTEGER},		// E.g. INTEGER 	< X
			{ANYTYPE, INTEGER, INTEGER, INTEGER},		// E.g. FLOAT 		< X
			{INTEGER, INTEGER, INTEGER, ANYTYPE}		// E..g ANYTYPE 	< X
	};
	
	public final static int[][] modOperator = {
			{ANYTYPE, ANYTYPE, ANYTYPE, ANYTYPE},		// E.g. CHARACTER 	MOD X
			{ANYTYPE, INTEGER, ANYTYPE, INTEGER}, 		// E.g. INTEGER 	MOD X
			{ANYTYPE, ANYTYPE, ANYTYPE, ANYTYPE},		// E.g. FLOAT 		MOD X
			{ANYTYPE, INTEGER, ANYTYPE, ANYTYPE}		// E.g. ANYTYPE 	MOD X
	};
	
	public final static int[][] assignmentOperator = {
			{CHARACTER, ANYTYPE, ANYTYPE, CHARACTER},	// E.g. CHARACTER 	:= X
			{ANYTYPE,	INTEGER, INTEGER, INTEGER  },	// E.g. INTEGER 	:= X
			{ANYTYPE,	FLOAT,   FLOAT,   FLOAT	   },	// E.g. FLOAT 		:= X
			{CHARACTER,	INTEGER, FLOAT,   ANYTYPE  }	// E.g. ANYTYPE 	:= X
	};
	
	public final static int[] notOperator = { ANYTYPE, INTEGER, ANYTYPE, ANYTYPE }; // E.g. NOT X
	
	public static String getTypeName(int type) {
		String strType = "";
		switch (type) {
		case 0:
			strType = "CHARACTER";
			break;
		case 1:
			strType = "INTEGER";
			break;
		case 2:
			strType = "FLOAT";
			break;
			default:
				strType = "ANYTYPE";
		}
		return strType;
	}

}
