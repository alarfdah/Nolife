package util;

public class MemoryValue {
	
	private String value;
	private String minBound;
	private String maxBound;
	private int offset;
	private int type;
	
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}
