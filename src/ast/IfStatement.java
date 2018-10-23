package ast;

import visitor.Visitor;

public class IfStatement extends Statement {

	public String getIfKeyword() {
		return "IF ";
	}
	
	public String getThenKeyword() {
		return "THEN ";
	}
	
	public String getElseKeyword() {
		return "ELSE ";
	}
	
	
	public Comparison getIfExpression() {
		return (Comparison)getChild(0);
	}
	
	/**
	 * Can be a list
	 * @return Statement
	 */
	public Statement getThenStatement() {
		return (Statement)getChild(1);
	}
	
	
	/**
	 * Can be optional
	 * @return Statement
	 */
	public Statement getElseStatement() {
		if (getChild(2) == null) {
			return null;
		}
		return (Statement)getChild(2);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
