package ast;

import visitor.Visitor;

public class Clause extends ASTNode {

	public Statement getStatement() {
		return (Statement)getChild(0);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
