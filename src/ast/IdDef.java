package ast;

import visitor.Visitor;

public class IdDef extends ASTNode {

	public String getId() {
		return getLabel();
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
	

}
