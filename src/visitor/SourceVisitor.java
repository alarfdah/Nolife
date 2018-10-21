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
	public T visit(Assignment n) {
		n.getLhs().accept(this);
		src += " := ";
		n.getRhs().accept(this);
		src += "\n";
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
			node.accept(this);
		}
		src += n.getEnd();
		return null;
	}
	
	@Override
	public T visit(ConstantCharacter n) {
		src += n.getLabel();
		return null;
	}
	
	@Override
	public T visit(ConstantFloat n) {
		src += n.getLabel();
		return null;
	}
	
	@Override
	public T visit(ConstantInteger n) {
		src += n.getLabel();
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
	public T visit(If n) {
		return null;
	}

	@Override
	public T visit(Declare n) {
		for (ASTNode node : n.getChildren().subList(0, n.getChildren().size() - 1)) {
			node.accept(this);
			src += ", ";
		}
		if (src != null && src.length() > 0 && src.charAt(src.length() - 1) == ' ') {
	        src = src.substring(0, src.length() - 1);
	    }
		if (src != null && src.length() > 0 && src.charAt(src.length() - 1) == ',') {
	        src = src.substring(0, src.length() - 1);
	    }
		src += ": ";
		n.getDeclaredType().accept(this);
		src += ";";
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
		// TODO Auto-generated method stub
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
		src += n.getVarKeyword() + " ";
		for (ASTNode node : n.getDecls()) {
			node.accept(this);
			src += "\n";
		}
		return null;
	}
	
	@Override
	public T visit(While n) {
		// TODO Auto-generated method stub
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
