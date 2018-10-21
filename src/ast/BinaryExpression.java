package ast;

import visitor.Visitor;

public abstract class BinaryExpression extends Expression {
	
	
	public abstract String getOperator();
	
	public Expression getLeftOperand() { 
		return (Expression)getChild(0); 
	}
	
	public Expression getRightOperand() {
		return (Expression)getChild(1);
	}
	
}
