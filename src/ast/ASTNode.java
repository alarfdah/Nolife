package ast;

import java.util.LinkedList;
import java.util.List;

import visitor.Visitor;

public abstract class ASTNode {
	
	protected String label;
	protected List<ASTNode> children = new LinkedList<ASTNode>();
	protected int realType;
	protected int convertedType;
	protected int offset;
	protected boolean local;
	protected boolean param = false;
	protected boolean array = false;
	protected String minimumBound;
	protected String maximumBound;
	
	public void setMinimumBound(String minBound) {
		this.minimumBound = minBound;
	}

	public void setMaximumBound(String maxBound) {
		this.maximumBound = maxBound;
	}
	
	public String getMinimumBound() {
		return minimumBound;
	}
	
	public String getMaximumBound() {
		return maximumBound;
	}
	
	public void setLabel(String l) {
		label = l;
	}
	
	public String getLabel() {
		return label;
	}
	
	public ASTNode addChild(ASTNode c) {
		children.add(c);
		return this;
	}
	
	public ASTNode addChild(int i, ASTNode c) {
		children.add(i, c);
		return this;
	}
	
	public void removeChild(ASTNode c) {
		children.remove(c);
	}
	
	public void removeChild(int i) {
		children.remove(i);
	}
	
	public ASTNode getChild(int i) {
		return children.get(i);
	}
	
	public List<ASTNode> getChildren() {
		return children;
	}
	
	public int getRealType() {
		return realType;
	}

	public void setRealType(int realType) {
		this.realType = realType;
	}

	public int getConvertedType() {
		return convertedType;
	}
	
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setConvertedType(int convertedType) {
		this.convertedType = convertedType;
	}
	
	public boolean isLocal() {
		return local;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}
	
	public boolean isParam() {
		return param;
	}

	public void setParam(boolean param) {
		this.param = param;
	}

	public boolean isArray() {
		return array;
	}

	public void setArray(boolean array) {
		this.array = array;
	}
	
	public abstract Object accept(Visitor visitor);
}
