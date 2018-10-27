package ast;

import java.util.List;

import visitor.Visitor;

public class Parameters extends Statement {

	public Declare getParameters() {
		if (getChild(0) == null) {
			return null;
		}
		return (Declare)getChild(0);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
