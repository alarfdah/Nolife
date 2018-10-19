package ast;

import visitor.Visitor;

public class Add extends BinaryExpression {

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
