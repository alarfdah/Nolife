package ast;

import visitor.Visitor;

public class CallProcedure extends Statement {

	public String getId() {
		return getLabel();
	}
	
	public ASTNode getCallArguments() {
		if (getChild(0) == null) {
			return null;
		}
		return getChild(0);
	}
	
	@Override // check if needed
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
