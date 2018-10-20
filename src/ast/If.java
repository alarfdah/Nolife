package ast;

import visitor.Visitor;

public class If extends Statement {

	public Expression getIfExpression() {
		return (Expression)getChild(0);
	}
	
	/**
	 * Can be a list
	 * @return Statement
	 */
	public Statement getThenStatement() {
		// TODO check if has to be a list
		return (Statement)getChild(1);
	}
	
	/**
	 * Can be optional
	 * @return Statement
	 */
	public Statement getElseStatement() {
		return (Statement)getChild(2);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
