package ast;

import visitor.Visitor;

public class TypeFloat extends Type {

	@Override
	public String getTypeName() {
		return "FLOAT";
	}
	
	
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
