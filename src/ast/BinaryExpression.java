package ast;

import visitor.Visitor;

public abstract class BinaryExpression extends Expression {
	
	
	public abstract String getOperator();
	
	public ASTNode getLeftOperand() { 
		return getChild(0); 
	}
	
	public ASTNode getRightOperand() {
		return getChild(1);
	}
	
}
