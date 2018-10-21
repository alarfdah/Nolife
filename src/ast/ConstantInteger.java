package ast;

import visitor.Visitor;

public class ConstantInteger extends Expression {

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
