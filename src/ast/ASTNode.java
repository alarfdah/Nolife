package ast;

import java.util.LinkedList;
import java.util.List;

import visitor.Visitor;

public abstract class ASTNode {
	
	protected String label;
	protected List<ASTNode> children = new LinkedList<ASTNode>();
	protected int realType;
	protected int convertedType;
	
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

	public void setConvertedType(int convertedType) {
		this.convertedType = convertedType;
	}
	
	
	public abstract Object accept(Visitor visitor);
}
