package ast;

import visitor.Visitor;

public class Constant extends Expression {

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
