package ast;

import visitor.Visitor;

public class IdDecl extends Declaration {

	public String getId() {
		return getLabel();
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
	
	
}
