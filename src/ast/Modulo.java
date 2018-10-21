package ast;

import visitor.Visitor;

public class Modulo extends BinaryExpression {

	
	@Override
	public String getOperator() {
		return " MOD ";
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
