package ast;

import visitor.Visitor;

public class Write extends Statement {

	
	public String getWriteKeyword() {
		return "WRITE";
	}
	
	public Expression getOutput() {
		return (Expression)getChild(0);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
