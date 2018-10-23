package ast;

import visitor.Visitor;

public class TypeInteger extends Type {

	@Override
	public String getTypeKeyword() {
		return "INTEGER";
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
	
}
