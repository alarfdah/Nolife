package ast;

import visitor.Visitor;

public class ConstantCharacter extends Expression {

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
