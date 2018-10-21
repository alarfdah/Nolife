package ast;

import visitor.Visitor;

public class ConstantInteger extends Expression {

	public String getInteger() {
		return getLabel();
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
