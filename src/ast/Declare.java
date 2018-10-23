package ast;

import java.util.List;

import visitor.Visitor;

public class Declare extends Declaration {

	public List<ASTNode> getDeclarations() {
		return getChildren();
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
