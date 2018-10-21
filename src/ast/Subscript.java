package ast;

import visitor.Visitor;

public class Subscript extends Expression {
	
	
	public IdRef getId() {
		return (IdRef)getChild(0);
	}
	
	// May not be an expression(?)
	public Expression getSubscriptExpression() {
		return (Expression)getChild(1);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
