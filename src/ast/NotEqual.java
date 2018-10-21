package ast;

import visitor.Visitor;

public class NotEqual extends Comparison {

	@Override
	public String getOperator() {
		return " <> ";
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
