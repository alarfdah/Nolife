package util;

import java.util.LinkedList;
import java.util.List;

public class SymbolValue {

	private String id;				// id = x
	private int declaredType;		// Type: INTEGER
	private String varType;			// VAR/ARRAY/FUNCTION/PROCEDURE
	private boolean referenced;		// IS REFERENCED?
	private String minBound;		// MIN BOUND
	private String maxBound;		// MAX BOUND
	private List<Integer> parameters = new LinkedList<Integer>();		// PARAMETERS
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public int getDeclaredType() {
		return declaredType;
	}
	public void setDeclaredType(int dataType) {
		this.declaredType = dataType;
	}
	public String getVarType() {
		return varType;
	}
	public void setVarType(String varType) {
		this.varType = varType;
	}
	public boolean isReferenced() {
		return referenced;
	}
	public void setReferenced(boolean referenced) {
		this.referenced = referenced;
	}
	
	public String getMinBound() {
		return minBound;
	}
	public void setMinBound(String minBound) {
		this.minBound = minBound;
	}
	
	public String getMaxBound() {
		return maxBound;
	}
	public void setMaxBound(String maxBound) {
		this.maxBound = maxBound;
	}
	
	public List<Integer> getParameters() {
		return parameters;
	}
	public void addParameter(int parameter) {
		parameters.add(parameter);
	}
	
	
	
}
