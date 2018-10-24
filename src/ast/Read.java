package ast;

import visitor.Visitor;

public class Read extends Statement {

	public String getReadKeyword() {
		return "READ";
	}
	
	public ASTNode getInput() {
		return getChild(0);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
