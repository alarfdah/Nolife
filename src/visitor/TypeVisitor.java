package visitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import ast.*;
import util.*;

public class TypeVisitor implements Visitor<Integer> {
	
	
	
	@Override
	public Integer visit(Add n) {
		Integer lOp = (Integer) n.getLeftOperand().accept(this);
		Integer rOp = (Integer) n.getRightOperand().accept(this);
		
		Integer resolvedType = TypeTable.arithmeticOperators[lOp][rOp];
		
		n.setRealType(resolvedType);
		
		if (resolvedType == TypeTable.ANYTYPE) {
			System.err.println("Cannot ADD type " + TypeTable.getTypeName(lOp) 
			+ " to " + TypeTable.getTypeName(rOp) + "!" );
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
		if (!SymbolTable.isDeclaredLocal(id)) {
			
			// Add the id
			SymbolTable.putId(id);
			
			// Add the declared type of array
			SymbolTable.putDeclaredType(id, n.getRealType());
			
			// Add the variable type as ARRAY
			SymbolTable.putVarType(id, "ARRAY");
			
			// Add the minimum bound of the array
			SymbolTable.putMinBound(id, n.getMinBound().getLabel());
			
			// Add the maximum bound of the array
			SymbolTable.putMaxBound(id, n.getMaxBound().getLabel());
			
			n.getMinBound().accept(this);
			n.getMaxBound().accept(this);
			
			// Set the real type of the array
			n.setRealType(n.getRealType());
		} else {
			System.err.println(SymbolTable.getVarType(id) + " with id: " + id + " is already declared!");
			
			// Set it to ANTYPE
			n.setRealType(TypeTable.ANYTYPE);
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(ArrayDef n) {
		String id = n.getId();
		if (!SymbolTable.isDeclaredLocal(id) && !SymbolTable.isDeclaredGlobal(id)) {
			System.err.println("ARRAY " + id + " is not declared!");
			n.setRealType(TypeTable.ANYTYPE);
		} else {
			String expr = "";
			
			// If variable, not array
			if (SymbolTable.getVarType(id).equals("VARIABLE")) {
				System.err.println("Incorrect number of dimensions of variable " + id);
				n.setRealType(TypeTable.ANYTYPE);
			// If variable
			} else {
				// If constant valued expression then check bounds
				if (n.getSubscriptExpression() instanceof ConstantInteger 
						|| n.getSubscriptExpression() instanceof ConstantCharacter) {
					expr = n.getSubscriptExpression().getLabel();
					if (expr.compareTo(SymbolTable.getMinBound(id)) < 0 || expr.compareTo(SymbolTable.getMaxBound(id)) > 0) {
						System.err.println("ArrayIndexOutOfBounds: array " + id + ", index is " + expr);
					}
				}				
				
				n.getSubscriptExpression().accept(this);
				n.setRealType(SymbolTable.getDeclaredType(id));
			}
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(ArrayRef n) {
		String id = n.getId();
		if (!SymbolTable.isDeclaredLocal(id) && !SymbolTable.isDeclaredGlobal(id)) {
			System.err.println("ARRAY " + id + " is not declared!");
			n.setRealType(TypeTable.ANYTYPE);
		} else {
			String expr = "";
			
			// Set the array reference to true
			SymbolTable.putReferenced(id, true);
			
			// If variable, not array
			if (SymbolTable.getVarType(id).equals("VARIABLE")) {
				System.err.println("Incorrect number of dimensions of variable " + id);
				n.setRealType(TypeTable.ANYTYPE);
			// If variable
			} else {
				// If constant valued expression then check bounds
				if (n.getSubscriptExpression() instanceof ConstantInteger 
						|| n.getSubscriptExpression() instanceof ConstantCharacter) {
					expr = n.getSubscriptExpression().getLabel();
					if (expr.compareTo(SymbolTable.getMinBound(id)) < 0 || expr.compareTo(SymbolTable.getMaxBound(id)) > 0) {
						System.err.println("ArrayIndexOutOfBounds: array " + id + ", index is " + expr);
					}
				}				
				
				n.getSubscriptExpression().accept(this);
				n.setRealType(SymbolTable.getDeclaredType(id));
			}
			
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(Assignment n) {
		
		Integer lhs = (Integer) n.getLhs().accept(this);
		Integer rhs = (Integer) n.getRhs().accept(this);
		
		Integer resolvedType = TypeTable.assignmentOperator[lhs][rhs];
		
		if (resolvedType == TypeTable.ANYTYPE) {
			System.err.println("Cannot assign type " + TypeTable.getTypeName(rhs) 
			+ " to type " + TypeTable.getTypeName(lhs) + "!");
		}
		
		return n.getRealType();
	}

	@Override
	public Integer visit(CallFunction n) {
		int i;
		String id = n.getId();
		List<Integer> params = new LinkedList<Integer>();
		List<Integer> paramsOfId;
		
		// if function does not exist
		if (SymbolTable.getVarType(id) == null) {
			n.setRealType(TypeTable.ANYTYPE);
			return n.getRealType();
		}
		
		if (SymbolTable.getVarType(id).equals("PROCEDURE")) {
			System.err.println("Function call invoking a procedure!");
			n.setRealType(TypeTable.ANYTYPE);
		} else {
			n.setRealType(SymbolTable.getDeclaredType(id));
		}
		
		if (n.getCallArguments() != null) {
			n.getCallArguments().accept(this);				
			for (ASTNode node : ((Parameters)n.getCallArguments()).getParameters()) {
				params.add(node.getRealType());
			}
		}
		paramsOfId = SymbolTable.getParameters(id);
		if (params.size() != paramsOfId.size()) {
			System.err.println("Parameters for the FUNCTION " + id + " do not match!");
		} else {
			for (i = 0; i < params.size(); i++) {
				if (params.get(i) != TypeTable.ANYTYPE && params.get(i) != paramsOfId.get(i)) {
					System.err.println("Parameters for the FUNCTION " + id + " do not match!");
				}
			}
		}
		SymbolTable.putReferenced(id, true);
		return n.getRealType();
	}
	
	@Override
	public Integer visit(CallProcedure n) {
		int i;
		String id = n.getId();
		List<Integer> params = new LinkedList<Integer>();
		List<Integer> paramsOfId;
		
		// if procedure does not exist
		if (SymbolTable.getVarType(id) == null) {
			n.setRealType(TypeTable.ANYTYPE);
			return n.getRealType();
		}
		
		if (SymbolTable.getVarType(id).equals("FUNCTION")) {
			System.err.println("Procedure call invoking a function!");
			n.setRealType(TypeTable.ANYTYPE);
		} else {
			n.setRealType(SymbolTable.getDeclaredType(id));
		}
		
		if (n.getCallArguments() != null) {
			n.getCallArguments().accept(this);				
			for (ASTNode node : ((Parameters)n.getCallArguments()).getParameters()) {
				params.add(node.getRealType());
			}
		}
		paramsOfId = SymbolTable.getParameters(id);
		if (params.size() != paramsOfId.size()) {
			System.err.println("Parameters for the PROCEDURE " + id + " do not match!");
		} else {
			for (i = 0; i < params.size(); i++) {
				if (params.get(i) != TypeTable.ANYTYPE && params.get(i) != paramsOfId.get(i)) {
					System.err.println("Parameters for the PROCEDURE " + id + " do not match!");
				}
			}
		}
		SymbolTable.putReferenced(id, true);
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
		if (SymbolTable.isDeclaredLocal(id)) {
			System.err.println(SymbolTable.getVarType(id) + " with id: " + id + " is already declared! (Cannot create function)");
		} else {
			SymbolTable.putId(id);
			SymbolTable.putDeclaredType(id, n.getRealType());
			SymbolTable.putVarType(id, "FUNCTION");
		}
		pushFrame();
		for (Statement statement : n.getStatements()) {
			if (statement != null) {
				statement.accept(this);
				if (statement instanceof Parameters) {
					// For each declare node
					for (ASTNode declareNode : ((Parameters)statement).getParameters()) {
						// For each type node
						for (ASTNode node : ((Declare)declareNode).getDeclarations()) {
							if (node instanceof TypeInteger) {
								SymbolTable.putParameters(id, TypeTable.INTEGER);
							} else if (node instanceof TypeFloat) {
								SymbolTable.putParameters(id, TypeTable.FLOAT);
							} else if (node instanceof TypeCharacter) {
								SymbolTable.putParameters(id, TypeTable.CHARACTER);
							}							
						}
					}
				}
			}
		}
		
		for (Value value : SymbolTable.symTable.peek().values()) {
			if (!SymbolTable.getIsReferenced(value.getId())) {
				System.err.println(value.getVarType() + " " + value.getId() + " was declared but never referenced in FUNCTION " + id + "!");
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
		if (!SymbolTable.isDeclaredLocal(id)) {
			// Add the id
			SymbolTable.putId(id);
			
			// Add the declared type
			SymbolTable.putDeclaredType(id, n.getRealType());
			
			// Add id as a variable
			SymbolTable.putVarType(id, "VARIABLE");
			
			// Set the type of IdDecl
			n.setRealType(n.getRealType());
		} else {
			System.err.println(SymbolTable.getVarType(id) + " with id: " + id + " is already declared locally!");
			
			// Set it to ANYTYPE
			n.setRealType(TypeTable.ANYTYPE);
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(IdDef n) {
		String id = n.getId();
		if (!SymbolTable.isDeclaredLocal(id) && !SymbolTable.isDeclaredGlobal(id)) {
			System.err.println("VARIABLE " + id + " is not declared!");
			// Set it to ANYTYPE
			n.setRealType(TypeTable.ANYTYPE);
		} else {
//			System.out.println(id);
			
			// If array
			if (SymbolTable.getVarType(id).equals("ARRAY")) {
				System.err.println("Incorrect number of dimensions of array " + id);
				n.setRealType(TypeTable.ANYTYPE);
			// If variable
			} else {
				// Set the type of IdDef from declaration
				n.setRealType(SymbolTable.getDeclaredType(id));				
			}
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(IdRef n) {
		String id = n.getId();
		if (!SymbolTable.isDeclaredLocal(id) && !SymbolTable.isDeclaredGlobal(id)) {
			System.err.println("VARIABLE " + id + " is not declared!");
			
			// Set the type to ANYTYPE
			n.setRealType(TypeTable.ANYTYPE);
		} else {
			
			// It is referenced
			SymbolTable.putReferenced(id, true);
			
			// If array
			if (SymbolTable.getVarType(id).equals("ARRAY")) {
				System.err.println("Incorrect number of dimensions of array " + id);
				n.setRealType(TypeTable.ANYTYPE);
			// If variable
			} else {
				// Set the type of IdRef from declaration
				n.setRealType(SymbolTable.getDeclaredType(id));				
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
		
		n.setRealType(resolvedType);
		
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
			+ " with " + TypeTable.getTypeName(rOp) + "!");
			
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
		n.setRealType(resolvedType);
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
		if (SymbolTable.isDeclaredLocal(id)) {
			System.err.println(SymbolTable.getVarType(id) + " with id: " + id + " is already declared! (Cannot create procedure)");
		} else {
			SymbolTable.putId(id);
			SymbolTable.putDeclaredType(id, TypeTable.ANYTYPE);
			SymbolTable.putVarType(id, "PROCEDURE");
			
			n.setRealType(TypeTable.ANYTYPE);
		}
		pushFrame();
		for (Statement statement: n.getStatements()) {
			if (statement != null) {
				statement.accept(this);		
				if (statement instanceof Parameters) {
					// For each declare node
					for (ASTNode declareNode : ((Parameters)statement).getParameters()) {
						// For each type node
						for (ASTNode node : ((Declare)declareNode).getDeclarations()) {
							if (node instanceof TypeInteger) {
								SymbolTable.putParameters(id, TypeTable.INTEGER);
							} else if (node instanceof TypeFloat) {
								SymbolTable.putParameters(id, TypeTable.FLOAT);
							} else if (node instanceof TypeCharacter) {
								SymbolTable.putParameters(id, TypeTable.CHARACTER);
							}							
						}
					}
				}
			}
		}
		for (Value value : SymbolTable.symTable.peek().values()) {
			if (!SymbolTable.getIsReferenced(value.getId())) {
				System.err.println(value.getVarType() + " " + value.getId() + " was declared but never referenced in PROCEDURE " + id + "!");
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
			if (statement instanceof CompoundStatement) {
				for (ASTNode node : ((CompoundStatement)statement).getStatements()) {
					if (node instanceof Return) {
						System.err.println("Main has a RETURN statement!");
					}
				}
			}
		}
		
		
		// Variables/Functions/Procedures that were declared but never referenced
		for (Value value : SymbolTable.symTable.peek().values()) {
			if (SymbolTable.getVarType(value.getId()).equals("VARIABLE") && !SymbolTable.getIsReferenced(value.getId())) {
				System.err.println(value.getVarType() + " " + value.getId() + " was declared but never referenced in MAIN!");
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
		Integer declaredType = (Integer) n.getReturn().accept(this);
		n.setRealType(declaredType);
//		n.setRealType(n.getReturn().getRealType());
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
		SymbolTable.symTable.push(new HashMap<String, Value>());
	}
	
	public void popFrame() {
		SymbolTable.symTable.pop();
	}
	

}
