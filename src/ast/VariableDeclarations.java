package ast;

import java.util.List;

import visitor.Visitor;

public class VariableDeclarations extends Statement {

	public String getVarKeyword() {
		return getLabel();
	}
	
	public List<ASTNode> getDecls() {
		return getChildren();
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
