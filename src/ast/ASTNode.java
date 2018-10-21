package ast;

import java.util.LinkedList;
import java.util.List;

import visitor.Visitor;

public abstract class ASTNode {
	
	protected String label;
	protected List<ASTNode> children = new LinkedList<ASTNode>();
	
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
	
	public ASTNode getChild(int i) {
		return children.get(i);
	}
	
	public List<ASTNode> getChildren() {
		return children;
	}
	
	public boolean isEmpty() {
		return children == null;
	}
	
	public abstract Object accept(Visitor visitor);
}
