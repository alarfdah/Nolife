package ast;

import visitor.Visitor;

public class Output extends Statement {

	
	public IdRef getOutput() {
		// TODO check the constant outputs like WRITE('hello')
		return (IdRef)getChild(0);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
