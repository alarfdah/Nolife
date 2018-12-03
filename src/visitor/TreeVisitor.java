package visitor;

import ast.*;

public class TreeVisitor implements Visitor<Object> {

	private String prefix = "";
	private String src = "";
	
	@Override
	public Object visit(Add n) {
		src += "ADD";
		src += "\n";
		addSpace();
		n.getLeftOperand().accept(this);
		removeSpace();
		addSpace();
		n.getRightOperand().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(AND n) {
		src += "AND";
		src += "\n";
		addSpace();
		n.getLeftOperand().accept(this);
		removeSpace();
		addSpace();
		n.getRightOperand().accept(this);
		removeSpace();
		return null;
	}
	
	@Override
	public Object visit(ArrayDecl n) {
		src += "ARRAY DECL label(" + n.getId() + ")";
		src += "\n";
		addSpace();
		n.getMinBound().accept(this);
		removeSpace();
		addSpace();
		n.getMaxBound().accept(this);
		removeSpace();
		return null;
	}
	
	@Override
	public Object visit(ArrayDef n) {
		src += "ARRAY DEF label(" + n.getId() + ")";
		src += "\n";
		addSpace();
		n.getSubscriptExpression().accept(this);
		removeSpace();
		return null;
	}
	
	@Override
	public Object visit(ArrayRef n) {
		src += "ARRAY REF label(" + n.getId() + ")";
		src += "\n";
		addSpace();
		n.getSubscriptExpression().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(Assignment n) {
		src += "ASSIGNMENT";
		src += "\n";
		addSpace();
		n.getLhs().accept(this);
		removeSpace();
		addSpace();
		n.getRhs().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(CallFunction n) {
		src += "CALL FUNCTION label(" + n.getId() + ")";
		src += "\n";
		if (n.getCallArguments() != null) {
			addSpace();
			n.getCallArguments().accept(this);				
			removeSpace();
		}
		return null;
	}
	
	@Override
	public Object visit(CallProcedure n) {
		src += "CALL PROCEDURE label(" + n.getId() + ")";
		src += "\n";
		if (n.getCallArguments() != null) {
			addSpace();
			n.getCallArguments().accept(this);				
			removeSpace();
		}
		return null;
	}

	@Override
	public Object visit(Cases n) {
		src += "CASES";
		src += "\n";
		for (ASTNode node : n.getClauseList()) {
			addSpace();
			node.accept(this);
			removeSpace();
		}
		return null;
	}
	
	@Override
	public Object visit(CaseStatement n) {
		src += "CASE STATEMENT";
		src += "\n";
		addSpace();
		n.getCaseExpression().accept(this);
		removeSpace();
		if (n.getCases() != null) {
			addSpace();
			n.getCases().accept(this);
			removeSpace();			
		}
		return null;
	}
	
	@Override
	public Object visit(Clause n) {
		src += "CLAUSE label(" + n.getLabel() + ")";
		src += "\n";
		addSpace();
		n.getStatement().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(CompoundStatement n) {
		src += "COMPOUND STATEMENT";
		src += "\n";
		for (ASTNode node : n.getStatements()) {
			addSpace();
			node.accept(this);
			removeSpace();
		}
		return null;
	}

	@Override
	public Object visit(ConstantCharacter n) {
		src += "CONST CHARACTER label(" + n.getCharacter() + ")";
		src += "\n";
		return null;
	}

	@Override
	public Object visit(ConstantFloat n) {
		src += "CONST FLOAT label(" + n.getFloat() + ")";
		src += "\n";
		return null;
	}

	@Override
	public Object visit(ConstantInteger n) {
		src += "CONST INTEGER label(" + n.getInteger() + ")";
		src += "\n";
		return null;
	}

	@Override
	public Object visit(ConstantString n) {
		src += "CONST STRING label(" + n.getString() + ")";
		src += "\n";
		return null;
	}

	@Override
	public Object visit(Declare n) {
		src += "DECLARATION";
		src += "\n";
		for (ASTNode node : n.getChildren()) {
			addSpace();
			node.accept(this);
			removeSpace();
			
		}
		return null;
	}

	@Override
	public Object visit(Equal n) {
		src += "EQUAL";
		src += "\n";
		addSpace();
		n.getLeftOperand().accept(this);
		removeSpace();
		addSpace();
		n.getRightOperand().accept(this);
		removeSpace();
		return null;
	}
	
	@Override
	public Object visit(Function n) {
		src += n.getFunctionKeyword();
		src += "\n";
		for (ASTNode node : n.getStatements()) {
			// Can have no parameters. So null node.
			if (node != null) {
				addSpace();
				node.accept(this);
				removeSpace();				
			}
		}
		return null;
	}

	@Override
	public Object visit(GreaterThan n) {
		src += "GREATER THAN";
		src += "\n";
		addSpace();
		n.getLeftOperand().accept(this);
		removeSpace();
		addSpace();
		n.getRightOperand().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(GreaterThanEqual n) {
		src += "GREATER THAN EQUAL";
		src += "\n";
		addSpace();
		n.getLeftOperand().accept(this);
		removeSpace();
		addSpace();
		n.getRightOperand().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(IdDecl n) {
		src += "ID DECL label(" + n.getId() + ")";
		src += "\n";
		return null;
	}

	@Override
	public Object visit(IdDef n) {
		src += "ID DEF label(" + n.getId() + ")";
		src += "\n";
		return null;
	}

	@Override
	public Object visit(IdRef n) {
		src += "ID REF label(" + n.getId() + ")";
		src += "\n";
		return null;
	}

	@Override
	public Object visit(IfStatement n) {
		src += n.getIfKeyword();
		src += "\n";
		addSpace();
		n.getIfExpression().accept(this);
		removeSpace();
		addSpace();
		src += n.getThenKeyword();
		src += "\n";
		addSpace();
		n.getThenStatement().accept(this);
		removeSpace();
		removeSpace();
		if (n.getElseStatement() != null) {
			addSpace();
			src += n.getElseKeyword();
			src += "\n";
			addSpace();
			n.getElseStatement().accept(this);
			removeSpace();
			removeSpace();
		}
		return null;
	}

	@Override
	public Object visit(LessThan n) {
		src += "LESS THAN";
		src += "\n";
		addSpace();
		n.getLeftOperand().accept(this);
		removeSpace();
		addSpace();
		n.getRightOperand().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(LessThanEqual n) {
		src += "LESS THAN EQUAL";
		src += "\n";
		addSpace();
		n.getLeftOperand().accept(this);
		removeSpace();
		addSpace();
		n.getRightOperand().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(Modulo n) {
		src += "MODULO";
		src += "\n";
		addSpace();
		n.getLeftOperand().accept(this);
		removeSpace();
		addSpace();
		n.getRightOperand().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(Multiply n) {
		src += "MULTIPLY";
		src += "\n";
		addSpace();
		n.getLeftOperand().accept(this);
		removeSpace();
		addSpace();
		n.getRightOperand().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(NOT n) {
		src += "NOT";
		src += "\n";
		addSpace();
		n.getNotChild().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(NotEqual n) {
		src += "NOT EQUAL";
		src += "\n";
		addSpace();
		n.getLeftOperand().accept(this);
		removeSpace();
		addSpace();
		n.getRightOperand().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(OR n) {
		src += "OR";
		src += "\n";
		addSpace();
		n.getLeftOperand().accept(this);
		removeSpace();
		addSpace();
		n.getRightOperand().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(Parameters n) {
		src += "PARAMETERS";
		src += "\n";
		for (ASTNode node : n.getParameters()) {
			addSpace();
			node.accept(this);
			removeSpace();
		}
		return null;
	}

	@Override
	public Object visit(Procedure n) {
		src += n.getProcedureKeyword();
		src += "\n";
		for (ASTNode node : n.getStatements()) {
			if (node != null) {
				addSpace();
				node.accept(this);
				removeSpace();				
			}
		}
		return null;
	}
	
	@Override
	public Object visit(Program n) {
		src += n.getProgramKeyword() + " label(" + n.getProgramName() + ")";
		src += "\n";
		for (Statement statement : n.getStatements()) {
			addSpace();
			statement.accept(this);
			removeSpace();
		}
		return null;
	}

	@Override
	public Object visit(Read n) {
		src += n.getReadKeyword();
		src += "\n";
		addSpace();
		n.getInput().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(Return n) {
		src += n.getReturnKeyword();
		src += "\n";
		addSpace();
		n.getReturn().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(SubroutineDeclarations n) {
		src += "SUBROUTINE DECLS";
		src += "\n";
		for (ASTNode node : n.getSubroutines()) {
			addSpace();
			node.accept(this);
			removeSpace();
		}
		return null;
	}

	@Override
	public Object visit(Subtract n) {
		src += "SUBTRACT";
		src += "\n";
		addSpace();
		n.getLeftOperand().accept(this);
		removeSpace();
		addSpace();
		n.getRightOperand().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(TypeCharacter n) {
		src += n.getTypeKeyword();
		src += "\n";
		addSpace();
		n.getChild().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(TypeFloat n) {
		src += n.getTypeKeyword();
		src += "\n";
		addSpace();
		n.getChild().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(TypeInteger n) {
		src += n.getTypeKeyword();
		src += "\n";
		addSpace();
		n.getChild().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(VariableDeclarations n) {
		src += n.getVarKeyword() + "DECLARATIONS";
		src += "\n";
		for (ASTNode node : n.getDecls()) {
			addSpace();
			node.accept(this);
			removeSpace();
		}
		return null;
	}

	@Override
	public Object visit(WhileStatement n) {
		src += n.getWhileKeyword();
		src += "\n";
		addSpace();
		n.getWhileExpression().accept(this);
		removeSpace();
		addSpace();
		n.getDoStatement().accept(this);
		removeSpace();
		return null;
	}

	@Override
	public Object visit(Write n) {
		src += n.getWriteKeyword();
		src += "\n";
		addSpace();
		n.getOutput().accept(this);
		removeSpace();
		return null;
	}

	public String getSource() {
		return src;
	}
	
	public void addSpace() {
		prefix += "\t";
		src += prefix;
	}
	
	public void removeSpace() {
		prefix = prefix.substring(0, prefix.length() - 1);
	}

}
