package util;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class SymbolTable {

	public static Stack<HashMap<String, Value>> symTable = new Stack<HashMap<String, Value>>();
	
	public static boolean isDeclaredGlobal(String symbol) {
		if (symTable.get(0).containsKey(symbol)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDeclaredLocal(String symbol) {
		if (symTable.peek().containsKey(symbol)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void putId(String symbol) {
		if (symTable.peek().get(symbol) == null) {
			Value value = new Value();
			value.setId(symbol);
			symTable.peek().put(symbol, value);
		} else {
			symTable.peek().get(symbol).setId(symbol);
		}
	}
	
	public static String getId(String symbol) {
		if (symTable.peek().containsKey(symbol)) {
			return symTable.peek().get(symbol).getId();
		} else if (symTable.get(0).containsKey(symbol)) {
			return symTable.get(0).get(symbol).getId();
		} else {
			System.out.println("null get id");
			return null;
		}
	}
	
	public static void putVarType(String symbol, String varType) {
		if (symTable.peek().get(symbol) != null) {
			symTable.peek().get(symbol).setVarType(varType);
		} else if (symTable.get(0).get(symbol) != null) {
			symTable.get(0).get(symbol).setVarType(varType);
		} else {
			System.out.println("null put var type");
		}
	}
	
	public static String getVarType(String symbol) {
		if (symTable.peek().containsKey(symbol)) {
			return symTable.peek().get(symbol).getVarType();
		} else if (symTable.get(0).containsKey(symbol)) {
			return symTable.get(0).get(symbol).getVarType();
		} else {
			System.out.println("null get var type");
			return null;
		}
	}
	
	public static void putDeclaredType(String symbol, int type) {
		if (symTable.peek().get(symbol) != null) {
			symTable.peek().get(symbol).setDeclaredType(type);
		} else if (symTable.get(0).get(symbol) != null) {
			symTable.get(0).get(symbol).setDeclaredType(type);
		} else {
			System.out.println("null put declared type for symbol " + symbol);
		}
	}
	
	public static int getDeclaredType(String symbol) {
		if (symTable.peek().containsKey(symbol)) {
			return symTable.peek().get(symbol).getDeclaredType();
		} else if (symTable.get(0).containsKey(symbol)) {
			return symTable.get(0).get(symbol).getDeclaredType();
		} else {
			System.out.println("null get declared type for symbol " + symbol);
			return -1;
		}
	}
	
	public static void putReferenced(String symbol, boolean isReferenced) {
		if (symTable.peek().get(symbol) != null) {
			symTable.peek().get(symbol).setReferenced(isReferenced);
		} else if (symTable.get(0).get(symbol) != null) {
			symTable.get(0).get(symbol).setReferenced(isReferenced);
		} else {
			System.out.println("null put referenced");
		}
	}
	
	public static boolean getIsReferenced(String symbol) {
		if (symTable.peek().containsKey(symbol)) {
			return symTable.peek().get(symbol).isReferenced();
		} else if (symTable.get(0).containsKey(symbol)) {
			return symTable.get(0).get(symbol).isReferenced();
		} else {
			System.out.println("null get is referenced");
			return false;
		}
	}
	
	public static void putMinBound(String symbol, String minBound) {
		if (symTable.peek().get(symbol) != null) {
			symTable.peek().get(symbol).setMinBound(minBound);			
		} else if (symTable.get(0).get(symbol) != null) {
			symTable.get(0).get(symbol).setMinBound(minBound);
		} else {
			System.out.println("null put min bound");
		}
	}
	
	public static String getMinBound(String symbol) {
		if (symTable.peek().containsKey(symbol)) {
			return symTable.peek().get(symbol).getMinBound();
		} else if (symTable.get(0).containsKey(symbol)) {
			return symTable.get(0).get(symbol).getMinBound();
		} else {
			System.out.println("null get min bound");
			return null;
		}
	}
	
	public static void putMaxBound(String symbol, String maxBound) {
		if (symTable.peek().get(symbol) != null) {
			symTable.peek().get(symbol).setMaxBound(maxBound);
		} else if (symTable.get(0).get(symbol) != null) {
			symTable.get(0).get(symbol).setMaxBound(maxBound);
		} else {
			System.out.println("null put max bound");
		}
	}
	
	public static String getMaxBound(String symbol) {
		if (symTable.peek().containsKey(symbol)) {
			return symTable.peek().get(symbol).getMaxBound();
		} else if (symTable.get(0).containsKey(symbol)) {
			return symTable.get(0).get(symbol).getMaxBound();
		} else {
			System.out.println("null get max bound");
			return null;
		}
	}
	
	// TODO Check this
	public static void putParameter(String symbol, int parameter) {
		if (symTable.peek().get(symbol) != null) {
			symTable.peek().get(symbol).addParameter(parameter);			
		} else if (symTable.get(0).get(symbol) != null) {
			symTable.get(0).get(symbol).addParameter(parameter);
		} else {
			System.out.println("null put parameters");
		}
	}
	
	public static List<Integer> getParameters(String symbol) {
		if (symTable.peek().containsKey(symbol)) {
			return symTable.peek().get(symbol).getParameters();
		} else if (symTable.get(0).containsKey(symbol)) {
			return symTable.get(0).get(symbol).getParameters();
		} else {
			System.out.println("null get parameters");
			return null;
		}
	}
	
}
