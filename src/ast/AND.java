package ast;

import visitor.Visitor;

public class AND extends BinaryExpression {

	@Override
	public String getOperator() {
		return " AND ";
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
