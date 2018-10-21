package ast;

import visitor.Visitor;

public class Case extends Statement {

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
