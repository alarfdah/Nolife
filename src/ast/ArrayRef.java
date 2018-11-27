package ast;

import visitor.Visitor;

public class ArrayRef extends Expression {

	private String minBound;
	private String maxBound;
	
	public void setMinBound(String minBound) {
		this.minBound = minBound;
	}

	public void setMaxBound(String maxBound) {
		this.maxBound = maxBound;
	}
	
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
