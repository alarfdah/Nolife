package ast;

import visitor.Visitor;

public class NOT extends Expression {

	public String getNotKeyword() {
		return "NOT ";
	}
	
	public Expression getNotChild() {
		return (Expression)getChild(0);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
