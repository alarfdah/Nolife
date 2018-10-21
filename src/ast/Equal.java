package ast;

import visitor.Visitor;

public class Equal extends Comparison {

	@Override
	public String getOperator() {
		return " = ";
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
