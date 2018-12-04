package ast;

import visitor.Visitor;

public class ArrayRef extends Expression {


	public String getId() {
		return getLabel();
	}
	
	public Expression getSubscriptExpression() {
		return (Expression)getChild(0);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
