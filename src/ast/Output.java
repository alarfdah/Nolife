package ast;

import visitor.Visitor;

public class Output extends Statement {

	
	public Expression getOutput() {
		return (Expression)getChild(0);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
