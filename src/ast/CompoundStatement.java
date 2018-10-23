package ast;

import java.util.List;

import visitor.Visitor;

public class CompoundStatement extends Statement {

	public String getBegin() {
		return "BEGIN\n";
	}
	
	public String getEnd() {
		return "END\n";
	}
	
	public List<ASTNode> getStatements() {
		return getChildren();
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

}
