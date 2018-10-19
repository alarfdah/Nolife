package ast;

import visitor.Visitor;

public class Multiply extends BinaryExpression {

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
