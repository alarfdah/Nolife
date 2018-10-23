package ast;

import java.util.List;

import visitor.Visitor;

public class SubroutineDeclarations extends Statement {

	
	public List<ASTNode> getSubroutines() {
		return getChildren();
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
