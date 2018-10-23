package ast;

import java.util.List;

import visitor.Visitor;

public class CallFunction extends Expression {

	public IdRef getFunctionVariable() {
		return (IdRef)getChild(0);
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
