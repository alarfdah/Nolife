package ast;

import visitor.Visitor;

public class Modulo extends BinaryExpression {

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
