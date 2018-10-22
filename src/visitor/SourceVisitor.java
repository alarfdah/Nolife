package visitor;

import ast.*;

public class SourceVisitor<T> implements Visitor<T> {

	
	private String src = "";
	
	@Override
	public T visit(Add n) {
		n.getLeftOperand().accept(this);
		src += n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public T visit(AND n) {
		n.getLeftOperand().accept(this);
		src += n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}
	
	@Override
	public T visit(Assignment n) {
		n.getLhs().accept(this);
		src += " := ";
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public T visit(CallFunction n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T visit(Case n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T visit(CompoundStatement n) {
		src += n.getBegin();
		for (ASTNode node : n.getChildren()) {
			src += "\t";
			node.accept(this);
			src += ";";
			src += "\n";
		}
		if (src != null && src.length() > 0 && src.charAt(src.length() - 2) == ';') {
	        src = src.substring(0, src.length() - 2);
	    }
		src += n.getEnd();
		return null;
	}
	
	@Override
	public T visit(ConstantCharacter n) {
		src += n.getCharacter();
		return null;
	}
	
	@Override
	public T visit(ConstantFloat n) {
		src += n.getFloat();
		return null;
	}
	
	@Override
	public T visit(ConstantInteger n) {
		src += n.getInteger();
		return null;
	}

	@Override
	public T visit(ConstantString n) {
		src += n.getString();
		return null;
	}
	
	@Override
	public T visit(Declare n) {
		for (ASTNode node : n.getChildren().subList(0, n.getChildren().size() - 1)) {
			node.accept(this);
			src += ", ";
		}
		
		// Removes the last ", "
		if (src != null && src.length() > 0 && src.charAt(src.length() - 2) == ',') {
	        src = src.substring(0, src.length() - 2);
	    }
		
		src += ": ";
		n.getDeclaredType().accept(this);
		src += ";";
		return null;
	}
	
	@Override
	public T visit(Equal n) {
		n.getLeftOperand().accept(this);
		src += n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}
	
	@Override
	public T visit(GreaterThan n) {
		n.getLeftOperand().accept(this);
		src += n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}
	
	@Override
	public T visit(GreaterThanEqual n) {
		n.getLeftOperand().accept(this);
		src += n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}
	
	@Override
	public T visit(IdDecl n) {
		src += n.getId();
		return null;
	}

	@Override
	public T visit(IdDef n) {
		src += n.getId();
		return null;
	}

	@Override
	public T visit(IdRef n) {
		src += n.getId();
		return null;
	}

	@Override
	public T visit(IF n) {
		src += n.getIfKeyword();
		src += "(";
		n.getIfExpression().accept(this);
		src += ")\n";
		src += "\t";
		src += n.getThenKeyword();
		src += "\n";
		src += "\t\t";
		n.getThenStatement().accept(this);
		src += "\n";
		src += "\t";
		try {
			src += n.getElseKeyword();
			src += "\n";
			src += "\t\t";
			n.getElseStatement().accept(this);
		} catch (Exception e) {
			src = src.substring(0, src.length() - 10);
		}
		
		return null;
	}
	
	@Override
	public T visit(LessThan n) {
		n.getLeftOperand().accept(this);
		src += n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public T visit(LessThanEqual n) {
		n.getLeftOperand().accept(this);
		src += n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}
	
	@Override
	public T visit(Modulo n) {
		n.getLeftOperand().accept(this);
		src += n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public T visit(Multiply n) {
		n.getLeftOperand().accept(this);
		src += n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public T visit(NOT n) {
		src += n.getNotKeyword();
		src += "(";
		n.getNotChild().accept(this);
		src += ")";
		return null;
	}
	
	@Override
	public T visit(NotEqual n) {
		n.getLeftOperand().accept(this);
		src += n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}
	
	@Override
	public T visit(OR n) {
		n.getLeftOperand().accept(this);
		src += n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}
	
	@Override
	public T visit(Program n) {
		src += n.getProgramType();
		src += n.getProgramName();
		for (ASTNode node : n.getStatements()) {
			node.accept(this);
		}
		return null;
	}

	@Override
	public T visit(Read n) {
		src += n.getReadKeyword();
		src += "(";
		n.getInput().accept(this);
		src += ")";
		return null;
	}
	
	@Override
	public T visit(Return n) {
		src += n.getReturnKeyword();
		src += "(";
		n.getReturn().accept(this);
		src += ")";
		return null;
	}

	@Override
	public T visit(Subscript n) {
		n.getId().accept(this);
		src += "[";
		n.getSubscriptExpression().accept(this);
		src += "]";
		return null;
	}

	@Override
	public T visit(Subtract n) {
		n.getLeftOperand().accept(this);
		src += n.getOperator();
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public T visit(TypeCharacter n) {
		src += n.getTypeName();
//		n.getChild().accept(this);
		return null;
	}

	@Override
	public T visit(TypeFloat n) {
		src += n.getTypeName();
//		n.getChild().accept(this);
		return null;
	}

	@Override
	public T visit(TypeInteger n) {
		src += n.getTypeName();
//		n.getChild().accept(this);
		return null;
	}

	@Override
	public T visit(VariableDeclarations n) {
		src += n.getVarKeyword();
		for (ASTNode node : n.getDecls()) {
			src += "\t";
			node.accept(this);
			src += "\n";
		}
		return null;
	}
	
	@Override
	public T visit(While n) {
		src += n.getWhileKeyword();
		src += "(";
		n.getWhileExpression().accept(this);
		src += ")";
		src += n.getDoKeyword();
		src += "\n";
		src += "\t";
		n.getDoStatement().accept(this);
		
		return null;
	}
	
	@Override
	public T visit(Write n) {
		src += n.getWriteKeyword();
		src += "(";
		n.getOutput().accept(this);
		src += ")";
		return null;
	}

	@Override
	public String getSource() {
		return src;
	}

	
}
