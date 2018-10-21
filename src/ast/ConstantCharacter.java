package ast;

import visitor.Visitor;

public class ConstantCharacter extends Expression {

	public String getCharacter() {
		return getLabel();
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
