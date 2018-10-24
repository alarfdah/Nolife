package ast;

import visitor.Visitor;

public class ArrayDef extends ASTNode {
	
	public String getId() {
		return getLabel();
	}
	
	public Expression getSubscriptExpression() {
		return (Expression)getChild(0);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
