package ast;

import visitor.Visitor;

public class ArrayDecl extends Declaration {

	public String getId() {
		return getLabel();
	}
	
	public Expression getMinBound() {
		return (Expression)getChild(0);
	}
	
	public Expression getMaxBound() {
		return (Expression)getChild(1);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
