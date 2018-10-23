package ast;

import visitor.Visitor;

public class Cases extends Statement {

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
