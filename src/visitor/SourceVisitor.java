package visitor;

import ast.*;

public class SourceVisitor implements Visitor<Object> {

	private String prefix = "";
	private String src = "";
	
	@Override
	public Object visit(Add n) {
		n.getLeftOperand().accept(this);
		//src n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(AND n) {
		n.getLeftOperand().accept(this);
		//src n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}
	
	@Override
	public Object visit(ArrayDecl n) {
		// TODO
		return null;
	}
	
	@Override
	public Object visit(ArrayDef n) {
		// TODO
		return null;
	}
	
	@Override
	public Object visit(ArrayRef n) {
		// TODO
		return null;
	}
	
	@Override
	public Object visit(Assignment n) {
		n.getLhs().accept(this);
		//src " := ";
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Object visit(Call n) {
		//src n.getMethodName();
		for (ASTNode node : n.getCallArguments()) {
			node.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(Cases n) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object visit(CaseStatement n) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object visit(Clause n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(CompoundStatement n) {
		//src prefix;
		//src n.getBegin();
		for (ASTNode node : n.getStatements()) {
			addSpace();
			//src prefix;
			node.accept(this);
			removeSpace();
			//src ";";
			//src "\n";
		}
		if (src != null && src.length() > 0 && src.charAt(src.length() - 2) == ';') {
	        src = src.substring(0, src.length() - 2);
	    }
		//src "\n";
		//src prefix;
		//src n.getEnd();
		return null;
	}
	
	@Override
	public Object visit(ConstantCharacter n) {
		//src n.getCharacter();
		return null;
	}
	
	@Override
	public Object visit(ConstantFloat n) {
		//src n.getFloat();
		return null;
	}
	
	@Override
	public Object visit(ConstantInteger n) {
		//src n.getInteger();
		return null;
	}

	@Override
	public Object visit(ConstantString n) {
		//src n.getString();
		return null;
	}
	
	@Override
	public Object visit(Declare n) {
		for (ASTNode node : n.getChildren().subList(0, n.getChildren().size() - 1)) {
			node.accept(this);
			//src ", ";
		}
		
		// Removes the last ", "
		if (src != null && src.length() > 0 && src.charAt(src.length() - 2) == ',') {
	        src = src.substring(0, src.length() - 2);
	    }
		
		//src ": ";
		//src ";";
		return null;
	}
	
	@Override
	public Object visit(Equal n) {
		n.getLeftOperand().accept(this);
		//src n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}
	
	@Override
	public Object visit(Function n) {
		
		return null;
	}
	
	@Override
	public Object visit(GreaterThan n) {
		n.getLeftOperand().accept(this);
		//src n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}
	
	@Override
	public Object visit(GreaterThanEqual n) {
		n.getLeftOperand().accept(this);
		//src n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}
	
	@Override
	public Object visit(IdDecl n) {
		//src n.getId();
		return null;
	}

	@Override
	public Object visit(IdDef n) {
		//src n.getId();
		return null;
	}

	@Override
	public Object visit(IdRef n) {
		//src n.getId();
		return null;
	}

	@Override
	public Object visit(IfStatement n) {
		//src n.getIfKeyword();
		//src "(";
		n.getIfExpression().accept(this);
		//src ")\n";
		//src prefix;
		//src n.getThenKeyword();
		//src "\n";
		addSpace();
		//src prefix;
		n.getThenStatement().accept(this);
		removeSpace();
		if (n.getElseStatement() != null) {
			//src "\n";
			//src prefix;
			//src n.getElseKeyword();
			//src "\n";
			addSpace();
			//src prefix;
			n.getElseStatement().accept(this);			
			removeSpace();
		}
		return null;
	}
	
	@Override
	public Object visit(LessThan n) {
		n.getLeftOperand().accept(this);
		//src n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(LessThanEqual n) {
		n.getLeftOperand().accept(this);
		//src n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}
	
	@Override
	public Object visit(Modulo n) {
		n.getLeftOperand().accept(this);
		//src n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(Multiply n) {
		n.getLeftOperand().accept(this);
		//src n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(NOT n) {
		//src n.getNotKeyword();
		//src "(";
		n.getNotChild().accept(this);
		//src ")";
		return null;
	}
	
	@Override
	public Object visit(NotEqual n) {
		n.getLeftOperand().accept(this);
		//src n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}
	
	@Override
	public Object visit(OR n) {
		n.getLeftOperand().accept(this);
		//src n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}
	
	@Override
	public Object visit(Parameters n) {
		return null;
	}
	
	@Override
	public Object visit(Procedure n) {
		
		return null;
	}
	
	@Override
	public Object visit(Program n) {
		//src n.getProgramKeyword();
		//src n.getProgramName();
		for (ASTNode node : n.getStatements()) {
			node.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(Read n) {
		//src n.getReadKeyword();
		//src "(";
		n.getInput().accept(this);
		//src ")";
		return null;
	}
	
	@Override
	public Object visit(Return n) {
		//src n.getReturnKeyword();
		//src "(";
		n.getReturn().accept(this);
		//src ")";
		return null;
	}

	@Override
	public Object visit(SubroutineDeclarations n) {
	
		return null;
	}

	@Override
	public Object visit(Subtract n) {
		n.getLeftOperand().accept(this);
		//src n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(TypeCharacter n) {
		//src n.getTypeKeyword();
//		n.getChild().accept(this);
		return null;
	}

	@Override
	public Object visit(TypeFloat n) {
		//src n.getTypeKeyword();
//		n.getChild().accept(this);
		return null;
	}

	@Override
	public Object visit(TypeInteger n) {
		//src n.getTypeKeyword();
//		n.getChild().accept(this);
		return null;
	}

	@Override
	public Object visit(VariableDeclarations n) {
		//src n.getVarKeyword();
		for (ASTNode node : n.getDecls()) {
			addSpace();
			//src prefix;
			node.accept(this);
			removeSpace();
			//src "\n";
		}
		return null;
	}
	
	@Override
	public Object visit(WhileStatement n) {
		//src n.getWhileKeyword();
		//src "(";
		n.getWhileExpression().accept(this);
		//src ")";
		//src n.getDoKeyword();
		//src "\n";
		addSpace();
		n.getDoStatement().accept(this);
		removeSpace();
		return null;
	}
	
	@Override
	public Object visit(Write n) {
		//src n.getWriteKeyword();
		//src "(";
		n.getOutput().accept(this);
		//src ")";
		return null;
	}

	public String getSource() {
		return src;
	}
	
	private void addSpace() {
		prefix += "\t";
	}
	
	private void removeSpace() {
		prefix = prefix.substring(0, prefix.length() - 1);
	}
	
}
