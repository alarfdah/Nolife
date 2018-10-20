package ast;

import visitor.Visitor;

public class TypeCharacter extends Type {

	@Override
	public String getTypeName() {
		return "CHARACTER";
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
