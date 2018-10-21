package ast;

import visitor.Visitor;

public class CompoundStatement extends Statement {

	public String getBegin() {
		return "\nBEGIN\n";
	}
	
	public String getEnd() {
		return "\nEND\n";
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
