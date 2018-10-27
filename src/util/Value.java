package util;

public class Value {

	private String id;
	private int declaredType;
	private String varType;
	private boolean referenced;
	private String minBound;
	private String maxBound;
	private String parameters;
	
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
	
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	
}
