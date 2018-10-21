package ast;

import visitor.Visitor;

public class Input extends Statement {

	public Expression getInput() {
		// TODO check the constant inputs like READ('hello')
		return (Expression)getChild(0);
	}
	
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
