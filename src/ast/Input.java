package ast;

import visitor.Visitor;

public class Input extends Statement {

	public IdRef getInput() {
		// TODO check the constant inputs like READ('hello')
		return (IdRef)getChild(0);
	}
	
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
