package ast;

import visitor.Visitor;

public class While extends Statement {

	public String getWhileKeyword() {
		return "WHILE ";
	}
	
	public String getDoKeyword() {
		return " DO ";
	}
	
	public Expression getWhileExpression() {
		return (Expression)getChild(0);
	}
	
	public Statement getDoStatement() {
		return (Statement)getChild(1);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
