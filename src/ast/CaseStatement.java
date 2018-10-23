package ast;

import java.util.List;

import visitor.Visitor;

public class CaseStatement extends Statement {

	public Expression getCaseExpression() {
		return (Expression)getChild(0);
	}
	
	public Cases getCases() {
		return (Cases)getChild(1);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
