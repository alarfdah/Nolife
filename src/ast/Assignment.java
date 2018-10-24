package ast;

import visitor.Visitor;

public class Assignment extends Statement {

	
	public ASTNode getLhs() {
		return getChild(0);
	}
	
	public ASTNode getRhs() {
		return getChild(1);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
