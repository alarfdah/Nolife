package util;

import java.util.HashMap;
import java.util.Stack;

public class MemoryTable {
	
	private static int constOffset = 0;
	private static int varOffset = 0;
	private static int paramOffset = 0;
	
	private static HashMap<String, MemoryValue> constantMap = new HashMap<>();
	private static Stack<HashMap<String, MemoryValue>> scopeStack = new Stack<>();
	
	public static void incrementConstOffset(int by) {
		constOffset += by;
	}
	
	public static void resetConstOffset() {
		constOffset = 0;
	}
	
	public static void incrementVarOffset(int by) {
		varOffset += by;
	}
	
	public static void resetVarOffset() {
		varOffset = 0;
	}
	
	public static void incrementParamOffset(int by) {
		paramOffset += by;
	}
	
	public static void resetParamOffset() {
		paramOffset = 0;
	}
	
	public static int getConstOffset() {
		return constOffset;
	}
	
	public static int getVarOffset() {
		return varOffset;
	}
	
	public static int getParamOffset() {
		return paramOffset;
	}
	
	public static void putConstantCharacter(String str) {
		if (!constantMap.containsKey(str)) {
			// Store the value
			MemoryValue value = new MemoryValue();
			value.setValue(str);
			value.setOffset(constOffset);
			value.setType(TypeTable.CHARACTER);
			constantMap.put(str, value);
			
			// Handle offset
			incrementConstOffset(4);
		}
	}
	
	public static void putConstantString(String constant) {
		if (!constantMap.containsKey(constant)) {
			// Store the value
			MemoryValue value = new MemoryValue();
			value.setValue(constant);
			value.setOffset(constOffset);
			value.setType(TypeTable.STRING);
			constantMap.put(constant, value);
			
			// Handle offset (strlength - quotes + \0)
			incrementConstOffset(constant.length() - 2 + 1);
		}
	}
	
	public static void putConstantInteger(String constant) {
		if (!constantMap.containsKey(constant)) {
			// Store the offset
			MemoryValue value = new MemoryValue();
			value.setValue(constant);
			value.setOffset(constOffset);
			value.setType(TypeTable.INTEGER);
			constantMap.put(constant, value);
			
			// Handle offset
			incrementConstOffset(4);
		}
	}
	
	public static void putConstantFloat(String constant) {
		if (!constantMap.containsKey(constant)) {
			// Store the offset
			MemoryValue value = new MemoryValue();
			value.setValue(constant);
			value.setOffset(constOffset);
			value.setType(TypeTable.FLOAT);
			constantMap.put(constant, value);
			
			// Handle offset
			incrementConstOffset(4);
		}
	}
	
	public static String getConstant(String constant) {
		if (constantMap.containsKey(constant)) {
			return constantMap.get(constant).getValue();
		} else {
			System.out.println("null get constant!");
			return null;
		}
	}
	
	public static void putDeclaredVariable(String varName, int type) {
		if (scopeStack.peek().get(varName) == null) {
			// Store the offset
			MemoryValue value = new MemoryValue();
			value.setOffset(varOffset);
			value.setType(type);
			
			// Store in hashMap
			scopeStack.peek().put(varName, value);
			
			// Increment the variable offset
			incrementVarOffset(4);
		} else {
			System.out.println("variable already exists");
		}
	}
	
	public static void putDeclaredArray(String scope, String arrName, String minBound, String maxBound, int type) {
		if (scopeStack.peek().get(arrName) == null) {
			// Store the offset
			MemoryValue value = new MemoryValue();
			value.setOffset(varOffset);
			value.setType(type);
			value.setMinBound(minBound);
			value.setMaxBound(maxBound);
			
			// Store in hashMap
			scopeStack.peek().put(arrName, value);
			
			// Increment the variable offset
			incrementVarOffset(Integer.parseInt(maxBound) - Integer.parseInt(minBound) * 4);
		} else {
			System.out.println("variable already exists");
		}
	}
	
	public static void printConstantMap() {
		for(MemoryValue m : constantMap.values()) {
			System.out.println("Value: " + m.getValue() 
			+ ", Type: " + TypeTable.getTypeName(m.getType()) + ", Offset: " + m.getOffset());
		}
	}
	
	public static void pushFrame() {
		scopeStack.push(new HashMap<String, MemoryValue>());
	}
	
	public static void popFrame() {
		scopeStack.pop();
	}
	
	
}
