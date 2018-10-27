package visitor;

import java.util.HashMap;
import java.util.Stack;

import ast.*;
import util.*;

public class TypeVisitor implements Visitor<Integer> {

	Stack<HashMap<String, Value>> symTable = new Stack<HashMap<String, Value>>();
	
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
			
			// Add the id
			putId(id);
			
			// Add the declared type of array
			putDeclaredType(id, n.getRealType());
			
			// Add the variable type as ARRAY
			putVarType(id, "ARRAY");
			
			// Add the minimum bound of the array
			putMinBound(id, n.getMinBound().getLabel());
			
			// Add the maximum bound of the array
			putMaxBound(id, n.getMaxBound().getLabel());
			
			n.getMinBound().accept(this);
			n.getMaxBound().accept(this);
			
			// Set the real type of the array
			n.setRealType(n.getRealType());
		} else {
			System.err.println("Array " + id + " is already declared!");
			
			// Set it to ANTYPE
			n.setRealType(TypeTable.ANYTYPE);
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(ArrayDef n) {
		String id = n.getId();
		if (!isDeclaredLocal(id) && !isDeclaredGlobal(id)) {
			System.err.println("Array " + id + " is not declared!");
			n.setRealType(TypeTable.ANYTYPE);
		} else {
			String expr = "";
			
			// If constant valued expression then check bounds
			if (n.getSubscriptExpression() instanceof ConstantInteger 
					|| n.getSubscriptExpression() instanceof ConstantCharacter) {
				expr = n.getSubscriptExpression().getLabel();
				if (expr.compareTo(getMinBound(id)) < 0 || expr.compareTo(getMaxBound(id)) > 0) {
					System.err.println("ArrayIndexOutOfBounds: array " + id + ", index is " + expr);
				}
			}
			
			n.getSubscriptExpression().accept(this);			
			n.setRealType(getDeclaredType(id));
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(ArrayRef n) {
		String id = n.getId();
		if (!isDeclaredLocal(id) && !isDeclaredGlobal(id)) {
			System.err.println("Array " + id + " is not declared!");
			n.setRealType(TypeTable.ANYTYPE);
		} else {
			String expr = "";
			
			// Set the array reference to true
			putReferenced(id, true);
			
			// If constant valued expression then check bounds
			if (n.getSubscriptExpression() instanceof ConstantInteger 
					|| n.getSubscriptExpression() instanceof ConstantCharacter) {
				expr = n.getSubscriptExpression().getLabel();
				if (expr.compareTo(getMinBound(id)) < 0 || expr.compareTo(getMaxBound(id)) > 0) {
					System.err.println("ArrayIndexOutOfBounds: array " + id + ", index is " + expr);
				}
			}
			
			n.getSubscriptExpression().accept(this);
			n.setRealType(getDeclaredType(id));
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
		n.setRealType(TypeTable.ANYTYPE);
		String id = n.getId();
		if (getVarType(id).equals("PROCEDURE")) {
			System.err.println("Function call invoking a procedure!");
		}
		
		if (n.getCallArguments() != null) {
			n.getCallArguments().accept(this);				
			String params = "";
			Type typeNode;
			for (ASTNode node : ((Parameters)n.getCallArguments()).getParameters()) {
				params += TypeTable.getTypeName(getDeclaredType(node.getLabel()));
				params += ",";
			}
			if (!params.equals(getParameters(id))) {
				System.err.println("Parameters for the FUNCTION " + id + " do not match!");
			}
		}
		putReferenced(id, true);
		return n.getRealType();
	}
	
	@Override
	public Integer visit(CallProcedure n) {
		n.setRealType(TypeTable.ANYTYPE);
		String id = n.getId();
		if (getVarType(id).equals("FUNCTION")) {
			System.err.println("Procedure call invoking a function!");
		}
		if (n.getCallArguments() != null) {
			n.getCallArguments().accept(this);				
			String params = "";
			Type typeNode;
			for (ASTNode node : ((Parameters)n.getCallArguments()).getParameters()) {
				params += TypeTable.getTypeName(getDeclaredType(node.getLabel()));
				params += ",";
			}
			if (!params.equals(getParameters(id))) {
				System.err.println("Parameters for the PROCEDURE " + id + " do not match!");
			}
		}
		putReferenced(id, true);
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
		String id = n.getId();
		if (isDeclaredLocal(id)) {
			System.err.println("Variable " + id + " is already declared! (Cannot create function)");
		} else {
			putId(id);
			putDeclaredType(id, n.getRealType());
			putVarType(id, "FUNCTION");
			
			// Set the type of function
			n.setRealType(n.getRealType());
		}
		pushFrame();
		for (Statement statement : n.getStatements()) {
			if (statement != null) {
				statement.accept(this);
				if (statement instanceof Parameters) {
					String params = "";
					Type typeNode;
					// For each declare node
					for (ASTNode declareNode : ((Parameters)statement).getParameters()) {
						// For each type node
						for (ASTNode node : ((Declare)declareNode).getDeclarations()) {
							if (node instanceof TypeInteger) {
								typeNode = (TypeInteger)node;
								params += typeNode.getTypeKeyword();
								params += ",";
							} else if (node instanceof TypeFloat) {
								typeNode = (TypeFloat)node;
								params += typeNode.getTypeKeyword();
								params += ",";
							} else if (node instanceof TypeCharacter) {
								typeNode = (TypeCharacter)node;
								params += typeNode.getTypeKeyword();
								params += ",";
							}							
						}
					}
					putParameters(id, params);
				}
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
			// Add the id
			putId(id);
			
			// Add the declared type
			putDeclaredType(id, n.getRealType());
			
			// Add id as a variable
			putVarType(id, "VARIABLE");
			
			// Set the type of IdDecl
			n.setRealType(n.getRealType());
		} else {
			System.err.println("Variable " + id + " is already declared locally!");
			
			// Set it to ANYTYPE
			n.setRealType(TypeTable.ANYTYPE);
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(IdDef n) {
		String id = n.getId();
		if (!isDeclaredLocal(id) && !isDeclaredGlobal(id)) {
			System.err.println("Variable " + id + " is not declared!");
			// Set it to ANYTYPE
			n.setRealType(TypeTable.ANYTYPE);
		} else {
			
			// If array
			if (getVarType(id).equals("ARRAY")) {
				System.err.println("Incorrect number of dimensions of array " + id);
				n.setRealType(TypeTable.ANYTYPE);
			// If variable
			} else {
				// Set the type of IdDef from declaration
				n.setRealType(getDeclaredType(id));				
			}
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(IdRef n) {
		String id = n.getId();
		if (!isDeclaredLocal(id) && !isDeclaredGlobal(id)) {
			System.err.println("Variable " + id + " is not declared!");
			
			// Set the type to ANYTYPE
			n.setRealType(TypeTable.ANYTYPE);
		} else {
			
			// It is referenced
			putReferenced(id, true);
			
			// If array
			if (getVarType(id).equals("ARRAY")) {
				System.err.println("Incorrect number of dimensions of array " + id);
				n.setRealType(TypeTable.ANYTYPE);
			// If variable
			} else {
				// Set the type of IdRef from declaration
				n.setRealType(getDeclaredType(id));				
			}
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
		String id = n.getId();
		if (isDeclaredLocal(id)) {
			System.err.println("Variable " + id + " is already declared! (Cannot create procedure)");
		} else {
			putId(id);
			putDeclaredType(id, TypeTable.ANYTYPE);
			putVarType(id, "PROCEDURE");
			
			n.setRealType(TypeTable.ANYTYPE);
		}
		pushFrame();
		for (Statement statement: n.getStatements()) {
			if (statement != null) {
				statement.accept(this);		
				if (statement instanceof Parameters) {
					String params = "";
					Type typeNode;
					// For each declare node
					for (ASTNode declareNode : ((Parameters)statement).getParameters()) {
						// For each type node
						for (ASTNode node : ((Declare)declareNode).getDeclarations()) {
							if (node instanceof TypeInteger) {
								typeNode = (TypeInteger)node;
								params += typeNode.getTypeKeyword();
								params += ",";
							} else if (node instanceof TypeFloat) {
								typeNode = (TypeFloat)node;
								params += typeNode.getTypeKeyword();
								params += ",";
							} else if (node instanceof TypeCharacter) {
								typeNode = (TypeCharacter)node;
								params += typeNode.getTypeKeyword();
								params += ",";
							}							
						}
					}
					putParameters(id, params);
				}
			}
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
		for (Value value : symTable.peek().values()) {
			if (!getIsReferenced(value.getId())) {
				System.err.println(value.getVarType() + " " + value.getId() + " was declared but never referenced!");
			}
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
		symTable.push(new HashMap<String, Value>());
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
	
	public void putId(String symbol) {
		if (symTable.peek().get(symbol) == null) {
			Value value = new Value();
			value.setId(symbol);
			symTable.peek().put(symbol, value);
		} else {
			symTable.peek().get(symbol).setId(symbol);
		}
	}
	
	public String getId(String symbol) {
		if (symTable.peek().containsKey(symbol)) {
			return symTable.peek().get(symbol).getId();
		} else if (symTable.get(0).containsKey(symbol)) {
			return symTable.get(0).get(symbol).getId();
		} else {
			return "nullString";
		}
	}
	
	public void putVarType(String symbol, String varType) {
		if (symTable.peek().get(symbol) == null) {
			Value value = new Value();
			value.setVarType(varType);
			symTable.peek().put(symbol, value);
		} else {
			symTable.peek().get(symbol).setVarType(varType);
		}
	}
	
	public String getVarType(String symbol) {
		if (symTable.peek().containsKey(symbol)) {
			return symTable.peek().get(symbol).getVarType();
		} else if (symTable.get(0).containsKey(symbol)) {
			return symTable.get(0).get(symbol).getVarType();
		} else {
			return "nullString";
		}
	}
	
	public void putDeclaredType(String symbol, int type) {
		if (symTable.peek().get(symbol) == null) {
			Value value = new Value();
			value.setDeclaredType(type);
			symTable.peek().put(symbol, value);
		} else {
			symTable.peek().get(symbol).setDeclaredType(type);
		}
	}
	
	public int getDeclaredType(String symbol) {
		if (symTable.peek().containsKey(symbol)) {
			return symTable.peek().get(symbol).getDeclaredType();
		} else if (symTable.get(0).containsKey(symbol)) {
			return symTable.get(0).get(symbol).getDeclaredType();
		} else {
			return -1;
		}
	}
	
	public void putReferenced(String symbol, boolean isReferenced) {
		if (symTable.peek().get(symbol) == null) {
			Value value = new Value();
			value.setReferenced(isReferenced);
			symTable.peek().put(symbol, value);
		} else {
			symTable.peek().get(symbol).setReferenced(isReferenced);
		}
	}
	
	public boolean getIsReferenced(String symbol) {
		if (symTable.peek().containsKey(symbol)) {
			return symTable.peek().get(symbol).isReferenced();
		} else if (symTable.get(0).containsKey(symbol)) {
			return symTable.get(0).get(symbol).isReferenced();
		} else {
			return false;
		}
	}
	
	public void putMinBound(String symbol, String minBound) {
		if (symTable.peek().get(symbol) == null) {
			Value value = new Value();
			value.setMinBound(minBound);
			symTable.peek().put(symbol, value);
		} else {
			symTable.peek().get(symbol).setMinBound(minBound);
		}
	}
	
	public String getMinBound(String symbol) {
		if (symTable.peek().containsKey(symbol)) {
			return symTable.peek().get(symbol).getMinBound();
		} else if (symTable.get(0).containsKey(symbol)) {
			return symTable.get(0).get(symbol).getMinBound();
		} else {
			return "nullMinBound";
		}
	}
	
	public void putMaxBound(String symbol, String maxBound) {
		if (symTable.peek().get(symbol) == null) {
			Value value = new Value();
			value.setMaxBound(maxBound);
			symTable.peek().put(symbol, value);
		} else {
			symTable.peek().get(symbol).setMaxBound(maxBound);
		}
	}
	
	public String getMaxBound(String symbol) {
		if (symTable.peek().containsKey(symbol)) {
			return symTable.peek().get(symbol).getMaxBound();
		} else if (symTable.get(0).containsKey(symbol)) {
			return symTable.get(0).get(symbol).getMaxBound();
		} else {
			return "nullMaxBound";
		}
	}
	
	// TODO Check this
	public void putParameters(String symbol, String parameters) {
		if (symTable.get(0).get(symbol) == null) {
			Value value = new Value();
			value.setParameters(parameters);
			symTable.get(0).put(symbol, value);
		} else {
			symTable.get(0).get(symbol).setParameters(parameters);
		}
	}
	
	public String getParameters(String symbol) {
		if (symTable.get(0).containsKey(symbol)) {
			return symTable.get(0).get(symbol).getParameters();
		} else {
			return "nullParameter";
		}
	}
}
