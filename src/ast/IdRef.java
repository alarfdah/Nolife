package ast;

import visitor.Visitor;

public class IdRef extends Expression {

	
	public String getId() {
		return getLabel();
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
	
}
