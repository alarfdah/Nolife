package ast;

import java.util.LinkedList;
import java.util.List;

import visitor.Visitor;

public class Procedure extends Subroutine {

	private List<Statement> statements = null;
	
	public String getProcedureKeyword() {
		return "PROCEDURE ";
	}
	
	public String getId() {
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
