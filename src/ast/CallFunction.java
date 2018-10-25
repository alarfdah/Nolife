package ast;

import visitor.Visitor;

public class CallFunction extends Expression {

	public String getMethodName() {
		return getLabel();
	}
	
	public ASTNode getCallArguments() {
		if (getChild(0) == null) {
			return null;
		}
		return getChild(0);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
