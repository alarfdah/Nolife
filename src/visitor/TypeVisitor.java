package visitor;

import java.util.HashMap;

import ast.*;
import util.*;

public class TypeVisitor implements Visitor<Integer> {

	private HashMap<String, Integer> typeTable = new HashMap<String, Integer>(); // Variable and Type
	
	
	@Override
	public Integer visit(Add n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(AND n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(ArrayDecl n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(ArrayDef n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(ArrayRef n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(Assignment n) {
		
		// Type checking
		Integer lhs = (Integer) n.getLhs().accept(this);
		Integer rhs = (Integer) n.getRhs().accept(this);
		
		Integer assignmentType = TypeTable.relationalOperators[lhs][rhs];
		
		n.setRealType(assignmentType);
		
		return n.getRealType();
	}

	@Override
	public Integer visit(Call n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(Cases n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(CaseStatement n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(Clause n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(CompoundStatement n) {
		n.setRealType(TypeTable.NOTYPE);
		
		for (ASTNode statements : n.getStatements()) {
			statements.accept(this);
		}
		
		return n.getRealType();
	}

	@Override
	public Integer visit(ConstantCharacter n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(ConstantFloat n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(ConstantInteger n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(ConstantString n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(Declare n) {
		n.setRealType(TypeTable.NOTYPE);
		
		for (ASTNode node : n.getDeclarations()) {
			node.accept(this);
		}
		
		return n.getRealType();
	}

	@Override
	public Integer visit(Equal n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(Function n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(GreaterThan n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(GreaterThanEqual n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(IdDecl n) {
		if (!typeTable.containsKey(n.getId())) {
			System.err.println("Variable " + n.getId() + " is not declared!");
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(IdDef n) {
		if (!typeTable.containsKey(n.getId())) {
			System.err.println("Variable " + n.getId() + " is not declared!");
			n.setRealType(TypeTable.ANYTYPE);
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(IdRef n) {
		if (!typeTable.containsKey(n.getId())) {
			System.err.println("Variable " + n.getId() + " is not declared!");
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(IfStatement n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(LessThan n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(LessThanEqual n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(Modulo n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(Multiply n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(NOT n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(NotEqual n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(OR n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(Parameters n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(Procedure n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(Program n) {
		n.setRealType(TypeTable.NOTYPE);
		
		for (Statement statement : n.getStatements()) {
			statement.accept(this);
		}
		
		return n.getRealType();
	}

	@Override
	public Integer visit(Read n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(Return n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(SubroutineDeclarations n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(Subtract n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(TypeCharacter n) {
		n.setRealType(TypeTable.CHARACTER);
		return n.getRealType();
	}

	@Override
	public Integer visit(TypeFloat n) {
		n.setRealType(TypeTable.FLOAT);
		return n.getRealType();
	}

	@Override
	public Integer visit(TypeInteger n) {
		n.setRealType(TypeTable.INTEGER);
		return n.getRealType();
	}

	@Override
	public Integer visit(VariableDeclarations n) {
		n.setRealType(TypeTable.NOTYPE);
		
		for (ASTNode node : n.getDecls()) {
			node.accept(this);
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(WhileStatement n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

	@Override
	public Integer visit(Write n) {
		// TODO Auto-generated method stub
		return n.getRealType();
	}

}
