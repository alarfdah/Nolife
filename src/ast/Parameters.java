package ast;

import java.util.List;

import visitor.Visitor;

public class Parameters extends Statement {

	public List<ASTNode> getParameters() {
		return getChildren();
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
