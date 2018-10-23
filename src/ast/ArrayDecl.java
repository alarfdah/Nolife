package ast;

import visitor.Visitor;

public class ArrayDecl extends Declaration {

	public IdDecl getVariableName() {
		return (IdDecl)getChild(0);
	}
	
	public Expression getMinBound() {
		return (Expression)getChild(1);
	}
	
	public Expression getMaxBound() {
		return (Expression)getChild(2);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
