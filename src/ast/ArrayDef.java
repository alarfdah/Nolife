package ast;

import visitor.Visitor;

public class ArrayDef extends ASTNode {
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
