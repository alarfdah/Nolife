package ast;

import java.util.LinkedList;
import java.util.List;

import visitor.Visitor;

public class Program extends ASTNode {

	private List<Statement> statements = null;
	
	public String getProgramKeyword() {
		return "PROGRAM ";
	}
	
	public String getProgramName() {
		return getLabel();
	}
	
	public List<Statement> getStatements() {
		if (null == statements) {
			computeStatements();
		}
		return statements;
	}
	
	private void computeStatements() {
		statements = new LinkedList<Statement>();
		getChildren().forEach(child -> statements.add((Statement)child));
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
