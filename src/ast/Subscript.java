package ast;

import visitor.Visitor;

public class Subscript extends Expression {
	
	
	public String getId() {
		return getChild(0).getLabel();
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
