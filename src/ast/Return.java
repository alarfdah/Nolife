package ast;

import visitor.Visitor;

public class Return extends Statement {

	public String getReturnKeyword() {
		return "RETURN";
	}
	
	public ASTNode getReturn() {
		return getChild(0);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
	
}
