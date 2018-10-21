package ast;

import visitor.Visitor;

public class OR extends BinaryExpression {

	@Override
	public String getOperator() {
		return " OR ";
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
