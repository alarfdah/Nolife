package ast;

import visitor.Visitor;

public class Multiply extends BinaryExpression {

	
	@Override
	public String getOperator() {
		return " * ";
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
