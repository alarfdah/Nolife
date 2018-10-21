package ast;

import visitor.Visitor;

public class ConstantString extends Expression {

	public String getString() {
		return getLabel();
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	
}
