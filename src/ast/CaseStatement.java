package ast;

import java.util.List;

import visitor.Visitor;

public class CaseStatement extends Statement {

	public Expression getCaseExpression() {
		return (Expression)getChild(0);
	}
	
	public Cases getCases() {
		if (getChild(1) == null) {
			return null;
		}
		return (Cases)getChild(1);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
