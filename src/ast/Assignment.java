package ast;

import visitor.Visitor;

public class Assignment extends Statement {

	
	public IdDef getLhs() {
		return (IdDef)getChild(0);
	}
	
	public Expression getRhs() {
		return (Expression)getChild(1);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
