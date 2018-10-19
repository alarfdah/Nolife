package ast;

import visitor.Visitor;

public abstract class BinaryExpression extends Expression {
	
	
	public ASTNode getLeftOperand() { 
		return (Expression)getChild(0); 
	}
	
	public ASTNode getRightOperand() {
		return (Expression)getChild(1);
	}
	
}
