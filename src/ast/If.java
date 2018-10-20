package ast;

import visitor.Visitor;

public class If extends Statement {

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
