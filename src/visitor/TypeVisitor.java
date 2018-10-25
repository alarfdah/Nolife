package visitor;

import java.util.HashMap;
import java.util.Stack;

import ast.*;
import util.*;

public class TypeVisitor implements Visitor<Integer> {

	Stack<HashMap<String, Integer>> symTable = new Stack<HashMap<String, Integer>>();
	HashMap<String, String> subroutineTable = new HashMap<String, String>();
	
	@Override
	public Integer visit(Add n) {
		Integer lOp = (Integer) n.getLeftOperand().accept(this);
		Integer rOp = (Integer) n.getRightOperand().accept(this);
		
		Integer resolvedType = TypeTable.arithmeticOperators[lOp][rOp];
		
		n.setRealType(resolvedType);
		
		if (resolvedType == TypeTable.ANYTYPE) {
			System.err.println("Cannot ADD type " + TypeTable.getTypeName(lOp) 
			+ " to " + TypeTable.getTypeName(rOp) + "!");
		}
		
		return n.getRealType();
	}

	@Override
	public Integer visit(AND n) {
		Integer lOp = (Integer) n.getLeftOperand().accept(this);
		Integer rOp = (Integer) n.getRightOperand().accept(this);
		
		Integer resolvedType = TypeTable.logicalOperators[lOp][rOp];
		
		n.setRealType(resolvedType);
		
		if (resolvedType == TypeTable.ANYTYPE) {
			System.err.println("Cannot AND type " + TypeTable.getTypeName(lOp) 
			+ " with " + TypeTable.getTypeName(rOp) + "!");
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(ArrayDecl n) {
		String id = n.getId();
		if (!isDeclaredLocal(id)) {
			symTable.peek().put(id, n.getRealType());
			n.getMinBound().accept(this);
			n.getMaxBound().accept(this);
		} else {
			System.err.println("Array " + id + " is already declared!");
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(ArrayDef n) {
		String id = n.getId();
		if (!isDeclaredLocal(id) && !isDeclaredGlobal(id)) {
			System.err.println("Array " + id + " is not declared!");
		} else {
			n.getSubscriptExpression().accept(this);			
			// TODO out of array bounds
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(ArrayRef n) {
		String id = n.getId();
		if (!isDeclaredLocal(id) && !isDeclaredGlobal(id)) {
			System.err.println("Array " + id + " is not declared!");
		} else {
			n.getSubscriptExpression().accept(this);
			// TODO out of array bounds
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(Assignment n) {
		
		Integer lhs = (Integer) n.getLhs().accept(this);
		Integer rhs = (Integer) n.getRhs().accept(this);
		
		return n.getRealType();
	}

	@Override
	public Integer visit(CallFunction n) {
		if (subroutineTable.get(n.getMethodName()).equals("PROCEDURE")) {
			System.err.println("Function call invoking a procedure!");
		}
		if (n.getCallArguments() != null) {
			n.getCallArguments().accept(this);				
		}
		return n.getRealType();
	}
	
	@Override
	public Integer visit(CallProcedure n) {
		if (subroutineTable.get(n.getMethodName()).equals("FUNCTION")) {
			System.err.println("Procedure call invoking a function!");
		}
		if (n.getCallArguments() != null) {
			n.getCallArguments().accept(this);				
		}
		
		return n.getRealType();
	}

	@Override
	public Integer visit(Cases n) {
		for (ASTNode node : n.getClauseList()) {
			node.accept(this);
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(CaseStatement n) {
		n.getCaseExpression().accept(this);
		if (n.getCases() != null) {
			n.getCases().accept(this);			
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(Clause n) {
		n.getStatement().accept(this);
		return n.getRealType();
	}

	@Override
	public Integer visit(CompoundStatement n) {
		n.setRealType(TypeTable.ANYTYPE);
		
		for (ASTNode statements : n.getStatements()) {
			statements.accept(this);
		}
		
		return n.getRealType();
	}

	@Override
	public Integer visit(ConstantCharacter n) {
		n.setRealType(TypeTable.CHARACTER);
		return n.getRealType();
	}

	@Override
	public Integer visit(ConstantFloat n) {
		n.setRealType(TypeTable.FLOAT);
		return n.getRealType();
	}

	@Override
	public Integer visit(ConstantInteger n) {
		n.setRealType(TypeTable.INTEGER);
		return n.getRealType();
	}

	@Override
	public Integer visit(ConstantString n) {
		n.setRealType(TypeTable.CHARACTER);
		return n.getRealType();
	}

	@Override
	public Integer visit(Declare n) {
		n.setRealType(TypeTable.ANYTYPE);
		
		for (ASTNode node : n.getDeclarations()) {
			node.accept(this);
		}
		
		return n.getRealType();
	}

	@Override
	public Integer visit(Equal n) {
		Integer lOp = (Integer) n.getLeftOperand().accept(this);
		Integer rOp = (Integer) n.getRightOperand().accept(this);
		
		Integer resolvedType = TypeTable.relationalOperators[lOp][rOp];
		
		n.setRealType(resolvedType);
		
		if (resolvedType == TypeTable.ANYTYPE) {
			System.err.println("Cannot use '=' on type " + TypeTable.getTypeName(lOp) 
			+ " and type " + TypeTable.getTypeName(rOp) + "!");
			
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(Function n) {
		if (isDeclaredLocal(n.getId())) {
			System.err.println("Variable " + n.getId() + " is already declared! (Cannot create function)");
		} else {
			symTable.peek().put(n.getId(), n.getRealType());			
			subroutineTable.put(n.getId(), "FUNCTION");
		}
		pushFrame();
		for (Statement statement : n.getStatements()) {
			if (statement != null) {
				statement.accept(this);
			}
		}
		popFrame();
		return n.getRealType();
	}

	@Override
	public Integer visit(GreaterThan n) {
		Integer lOp = (Integer) n.getLeftOperand().accept(this);
		Integer rOp = (Integer) n.getRightOperand().accept(this);
		
		Integer resolvedType = TypeTable.relationalOperators[lOp][rOp];
		
		n.setRealType(resolvedType);
		
		if (resolvedType == TypeTable.ANYTYPE) {
			System.err.println("Cannot use '>' on type " + TypeTable.getTypeName(lOp) 
			+ " and type " + TypeTable.getTypeName(rOp) + "!");
			
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(GreaterThanEqual n) {Integer lOp = (Integer) n.getLeftOperand().accept(this);
	Integer rOp = (Integer) n.getRightOperand().accept(this);
	
	Integer resolvedType = TypeTable.relationalOperators[lOp][rOp];
	
	n.setRealType(resolvedType);
	
	if (resolvedType == TypeTable.ANYTYPE) {
		System.err.println("Cannot use '>=' on type " + TypeTable.getTypeName(lOp) 
		+ " and type " + TypeTable.getTypeName(rOp) + "!");
		
	}
		return n.getRealType();
	}

	@Override
	public Integer visit(IdDecl n) {
		String id = n.getId();
		if (!isDeclaredLocal(id)) {
			symTable.peek().put(id, n.getRealType());
		} else {
			System.err.println("Variable " + id + " is already declared locally!");
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(IdDef n) {
		String id = n.getId();
		if (!isDeclaredLocal(id) && !isDeclaredGlobal(id)) {
			System.err.println("Variable " + id + " is not declared!");
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(IdRef n) {
		String id = n.getId();
		if (!isDeclaredLocal(id) && !isDeclaredGlobal(id)) {
			System.err.println("Variable " + id + " is not declared!");
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(IfStatement n) {
		n.getIfExpression().accept(this);
		n.getThenStatement().accept(this);
		if (n.getElseStatement() != null) {
			n.getElseStatement().accept(this);			
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(LessThan n) {
		Integer lOp = (Integer) n.getLeftOperand().accept(this);
		Integer rOp = (Integer) n.getRightOperand().accept(this);
		
		Integer resolvedType = TypeTable.relationalOperators[lOp][rOp];
		
		n.setRealType(resolvedType);
		
		if (resolvedType == TypeTable.ANYTYPE) {
			System.err.println("Cannot use '<' on type " + TypeTable.getTypeName(lOp) 
			+ " and type " + TypeTable.getTypeName(rOp) + "!");
			
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(LessThanEqual n) {
		Integer lOp = (Integer) n.getLeftOperand().accept(this);
		Integer rOp = (Integer) n.getRightOperand().accept(this);
		
		Integer resolvedType = TypeTable.relationalOperators[lOp][rOp];
		
		n.setRealType(resolvedType);
		
		if (resolvedType == TypeTable.ANYTYPE) {
			System.err.println("Cannot use '<=' on type " + TypeTable.getTypeName(lOp) 
			+ " and type " + TypeTable.getTypeName(rOp) + "!");
			
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(Modulo n) {
		Integer lOp = (Integer) n.getLeftOperand().accept(this);
		Integer rOp = (Integer) n.getRightOperand().accept(this);
		
		Integer resolvedType = TypeTable.modOperator[lOp][rOp];
		
		if (resolvedType == TypeTable.ANYTYPE) {
			System.err.println("Cannot MOD on type " + TypeTable.getTypeName(lOp) 
			+ " and type " + TypeTable.getTypeName(rOp) + "!");
			
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(Multiply n) {
		Integer lOp = (Integer) n.getLeftOperand().accept(this);
		Integer rOp = (Integer) n.getRightOperand().accept(this);
		
		Integer resolvedType = TypeTable.arithmeticOperators[lOp][rOp];
		
		n.setRealType(resolvedType);
		
		if (resolvedType == TypeTable.ANYTYPE) {
			System.err.println("Cannot MULTIPLY type " + TypeTable.getTypeName(lOp) 
			+ " to " + TypeTable.getTypeName(rOp) + "!");
			
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(NOT n) {
		Integer declaredType = (Integer) n.getNotChild().accept(this);
		
		Integer resolvedType = TypeTable.notOperator[declaredType];
		
		if (resolvedType == TypeTable.ANYTYPE) {
			System.err.println("Cannot use NOT on type " + TypeTable.getTypeName(declaredType) + "!");
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(NotEqual n) {
		Integer lOp = (Integer) n.getLeftOperand().accept(this);
		Integer rOp = (Integer) n.getRightOperand().accept(this);
		
		Integer resolvedType = TypeTable.relationalOperators[lOp][rOp];
		
		n.setRealType(resolvedType);
		
		if (resolvedType == TypeTable.ANYTYPE) {
			System.err.println("Cannot use '<>' on type " + TypeTable.getTypeName(lOp) 
			+ " and type " + TypeTable.getTypeName(rOp) + "!");
			
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(OR n) {
		Integer lOp = (Integer) n.getLeftOperand().accept(this);
		Integer rOp = (Integer) n.getRightOperand().accept(this);
		
		Integer resolvedType = TypeTable.logicalOperators[lOp][rOp];
		
		n.setRealType(resolvedType);
		
		if (resolvedType == TypeTable.ANYTYPE) {
			System.err.println("Cannot OR type " + TypeTable.getTypeName(lOp) 
			+ " with " + TypeTable.getTypeName(rOp) + "!");
			
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(Parameters n) {
		for (ASTNode node : n.getParameters()) {
			node.accept(this);
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(Procedure n) {
		if (isDeclaredLocal(n.getId())) {
			System.err.println("Variable " + n.getId() + " is already declared! (Cannot create procedure)");
		} else {
			symTable.peek().put(n.getId(), n.getRealType());
			subroutineTable.put(n.getId(), "PROCEDURE");
		}
		pushFrame();
		for (ASTNode node : n.getStatements()) {
			node.accept(this);
		}
		popFrame();
		return n.getRealType();
	}

	@Override
	public Integer visit(Program n) {
		n.setRealType(TypeTable.ANYTYPE);
		
		pushFrame();
		
		for (Statement statement : n.getStatements()) {
			statement.accept(this);
		}
		
		popFrame();
		return n.getRealType();
	}

	@Override
	public Integer visit(Read n) {
		n.getInput().accept(this);
		return n.getRealType();
	}

	@Override
	public Integer visit(Return n) {
		n.getReturn().accept(this);
		return n.getRealType();
	}

	@Override
	public Integer visit(SubroutineDeclarations n) {
		for (ASTNode node : n.getSubroutines()) {
			node.accept(this);
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(Subtract n) {
		Integer lOp = (Integer) n.getLeftOperand().accept(this);
		Integer rOp = (Integer) n.getRightOperand().accept(this);
		
		Integer resolvedType = TypeTable.arithmeticOperators[lOp][rOp];
		
		n.setRealType(resolvedType);
		
		if (resolvedType == TypeTable.ANYTYPE) {
			System.err.println("Cannot SUBTRACT type " + TypeTable.getTypeName(rOp) 
			+ " from " + TypeTable.getTypeName(lOp) + "!");
			
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(TypeCharacter n) {
		n.setRealType(TypeTable.CHARACTER);
		
		n.getChild().setRealType(TypeTable.CHARACTER);
		n.getChild().accept(this);
		return n.getRealType();
	}

	@Override
	public Integer visit(TypeFloat n) {
		n.setRealType(TypeTable.FLOAT);
		
		n.getChild().setRealType(TypeTable.FLOAT);
		n.getChild().accept(this);
		return n.getRealType();
	}

	@Override
	public Integer visit(TypeInteger n) {
		n.setRealType(TypeTable.INTEGER);
		
		n.getChild().setRealType(TypeTable.INTEGER);
		n.getChild().accept(this);
		return n.getRealType();
	}

	@Override
	public Integer visit(VariableDeclarations n) {
		n.setRealType(TypeTable.ANYTYPE);
		
		for (ASTNode node : n.getDecls()) {
			node.accept(this);
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(WhileStatement n) {
		n.getWhileExpression().accept(this);
		n.getDoStatement().accept(this);
		return n.getRealType();
	}

	@Override
	public Integer visit(Write n) {
		n.getOutput().accept(this);
		return n.getRealType();
	}

	public void pushFrame() {
		symTable.push(new HashMap<String, Integer>());
	}
	
	public void popFrame() {
		symTable.pop();
	}
	
	public boolean isDeclaredGlobal(String symbol) {
		if (symTable.get(0).containsKey(symbol)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isDeclaredLocal(String symbol) {
		if (symTable.peek().containsKey(symbol)) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getDeclaredType(String symbol) {
		if (symTable.peek().containsKey(symbol)) {
			return symTable.peek().get(symbol);
		} else if (symTable.get(0).containsKey(symbol)) {
			return symTable.get(0).get(symbol);
		} else {
			return -1;
		}
	}
}
