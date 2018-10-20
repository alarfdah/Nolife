package ast;

import java.util.List;

import visitor.Visitor;

public class CallFunction extends Expression {

	public String getFunctionName() {
		return getChild(0).getLabel();
	}
	
	// params
	public List<ASTNode> getCallArguments() {
		return getChildren().subList(1, getChildren().size());
	}
	
	@Override // check if needed
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
