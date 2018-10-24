package ast;

import java.util.List;

import visitor.Visitor;

public class Call extends Statement {

	public String getMethodName() {
		return getLabel();
	}
	
	// params
	public List<ASTNode> getCallArguments() {
		if (getChildren() == null) {
			return null;
		}
		return getChildren();
	}
	
	@Override // check if needed
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
