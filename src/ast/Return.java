package ast;

import visitor.Visitor;

public class Return extends Statement {

	public IdRef getReturn() {
		return (IdRef)getChild(0);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
	
}
