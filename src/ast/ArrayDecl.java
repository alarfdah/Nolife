package ast;

import visitor.Visitor;

public class ArrayDecl extends Declaration {

	private String minimumBound;
	private String maximumBound;
	
	public void setMinimumBound(String minBound) {
		this.minimumBound = minBound;
	}

	public void setMaximumBound(String maxBound) {
		this.maximumBound = maxBound;
	}
	
	public String getMinimumBound() {
		return minimumBound;
	}
	
	public String getMaximumBound() {
		return maximumBound;
	}

	public String getId() {
		return getLabel();
	}
	
	public Expression getMinBound() {
		return (Expression)getChild(0);
	}
	
	public Expression getMaxBound() {
		return (Expression)getChild(1);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
