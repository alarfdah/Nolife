package ast;

import visitor.Visitor;

public class Declare extends Declaration {

	public Type getDeclaredType() {
		return (Type)getChild(0);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
