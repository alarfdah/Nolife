package visitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ast.*;
import util.*;

public class TypeVisitor implements Visitor<Integer> {
	
	private boolean checkArraySubscript = true;
	
	@Override
	public Integer visit(Add n) {
		Integer lOp = (Integer) n.getLeftOperand().accept(this);
		Integer rOp = (Integer) n.getRightOperand().accept(this);
		
		Integer resolvedType = TypeTable.arithmeticOperators[lOp][rOp];
		n.setRealType(resolvedType);
		
		// Set converted type of children
		n.getLeftOperand().setConvertedType(resolvedType);
		n.getRightOperand().setConvertedType(resolvedType);
		

		if (resolvedType == TypeTable.ANYTYPE && lOp != TypeTable.ANYTYPE && rOp != TypeTable.ANYTYPE) {
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
		
		// Set converted type of children
		n.getLeftOperand().setConvertedType(resolvedType);
		n.getRightOperand().setConvertedType(resolvedType);
				
		
		if (resolvedType == TypeTable.ANYTYPE && lOp != TypeTable.ANYTYPE && rOp != TypeTable.ANYTYPE) {
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
				if (n.getSubscriptExpression() instanceof ConstantInteger) {
					expr = n.getSubscriptExpression().getLabel();
					
					// If ArrayDecl subscript is a CHARACTER and the ArrayDef subscript is an INTEGER
					if (SymbolTable.getMinBound(id).startsWith("'") && Character.isLetter(SymbolTable.getMinBound(id).charAt(1))) {
						System.err.println("Index type mismatch:\n\tArray " + id + " expects subscript type CHARACTER and index is of type INTEGER!");
						
					// If ArrayDecl and ArrayDef subscripts are of the same type, but outside bounds
					} else if (Integer.valueOf(expr).compareTo(Integer.valueOf(SymbolTable.getMinBound(id))) < 0 || Integer.valueOf(expr).compareTo(Integer.valueOf(SymbolTable.getMaxBound(id))) > 0) {
						System.err.println("ArrayIndexOutOfBounds: array " + id + ", index is " + expr);
					}
				
				} else if (n.getSubscriptExpression() instanceof ConstantCharacter) {
					expr = n.getSubscriptExpression().getLabel();
					
					// If ArrayDecl subscript is a INTEGER and the ArrayDef subscript is a CHARACTER
					if (SymbolTable.isStringInt(SymbolTable.getMinBound(id))) {
						System.err.println("Index type mismatch:\n\tArray " + id + " expects subscript type INTEGER and index is of type CHARACTER!");
						
					// If ArrayDecl and ArrayDef subscripts are of the same type, but outside bounds	
					} else if (expr.compareTo(SymbolTable.getMinBound(id)) < 0 || expr.compareTo(SymbolTable.getMaxBound(id)) > 0) {
						System.err.println("ArrayIndexOutOfBounds: array " + id + ", index is " + expr);
					}
				}				
				
				// Call accept on subscript expression
				n.getSubscriptExpression().accept(this);
				
				// Set type of ArrayDef from type of ArrayDecl
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
				if (n.getSubscriptExpression() instanceof ConstantInteger) {
					expr = n.getSubscriptExpression().getLabel();
					
					// If ArrayDecl subscript is a CHARACTER and the ArrayDef subscript is an INTEGER 
					if (SymbolTable.getMinBound(id).startsWith("'") && Character.isLetter(SymbolTable.getMinBound(id).charAt(1))) {
						System.err.println("Index type mismatch:\n\tArray " + id + " expects subscript type CHARACTER and index is of type INTEGER!");

					// If ArrayDecl and ArrayDef subscripts are of the same type, but outside bounds
					} else if (Integer.valueOf(expr).compareTo(Integer.valueOf(SymbolTable.getMinBound(id))) < 0 || Integer.valueOf(expr).compareTo(Integer.valueOf(SymbolTable.getMaxBound(id))) > 0) {
						System.err.println("ArrayIndexOutOfBounds: array " + id + ", index is " + expr);
					}
					
				} else if (n.getSubscriptExpression() instanceof ConstantCharacter) {
					expr = n.getSubscriptExpression().getLabel();
					
					// If ArrayDecl subscript is a INTEGER and the ArrayDef subscript is a CHARACTER
					if (SymbolTable.isStringInt(SymbolTable.getMinBound(id))) {
						System.err.println("Index type mismatch:\n\tArray " + id + " expects subscript type INTEGER and index is of type CHARACTER!");
						
					// If ArrayDecl and ArrayDef subscripts are of the same type, but outside bounds	
					} else if (expr.compareTo(SymbolTable.getMinBound(id)) < 0 || expr.compareTo(SymbolTable.getMaxBound(id)) > 0) {
						System.err.println("ArrayIndexOutOfBounds: array " + id + ", index is " + expr);
					}
				}
				
				// Call accept on subscript expression
				n.getSubscriptExpression().accept(this);

				// Set type of ArrayDef from type of ArrayDecl
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
		
		// Set converted type of children
		n.getLhs().setConvertedType(resolvedType);
		n.getRhs().setConvertedType(resolvedType);
		
		if (resolvedType == TypeTable.ANYTYPE && lhs != TypeTable.ANYTYPE && rhs != TypeTable.ANYTYPE) {
			System.err.println("Cannot assign type " + TypeTable.getTypeName(rhs) 
			+ " to type " + TypeTable.getTypeName(lhs) + "!");
		}
		
		return n.getRealType();
	}

	@Override
	public Integer visit(CallFunction n) {
		int i;
		String id = n.getId();
		List<Integer> paramsOfId;
		List<Integer> paramsOfCall = new LinkedList<Integer>();
		
		// if procedure does not exist
		if (SymbolTable.getVarType(id) == null) {
			n.setRealType(TypeTable.ANYTYPE);
			System.err.println("FUNCTION " + id + " was never declared!");
			return n.getRealType();
		}
		
		// If calling a function
		if (SymbolTable.getVarType(id).equals("PROCEDURE")) {
			System.err.println("Function call invoking procedure " + id + "!");
			n.setRealType(TypeTable.ANYTYPE);
		} else {
			n.setRealType(SymbolTable.getDeclaredType(id));
		}
		
		// Get parameters
		if (n.getCallArguments() != null) {
			checkArraySubscript = false;
			n.getCallArguments().accept(this);				
			checkArraySubscript = true;
			for (ASTNode node : ((Parameters)n.getCallArguments()).getParameters()) {
				paramsOfCall.add(node.getRealType());
			}
		}
		
		// Get procedure params
		paramsOfId = SymbolTable.getParameters(id);
		
		// Compare size of call params and procedure params
		if (paramsOfCall.size() != paramsOfId.size()) {
			System.err.println("Parameters for the FUNCTION " + id + " do not match:");
			System.err.println("\t Parameter size: " + paramsOfCall.size() + " but function accepts parameters of size: " + paramsOfId.size() + "!");

		} else {
			boolean printed = false;
			
			// Check if each param matches
			for (i = 0; i < paramsOfCall.size(); i++) {
				if (paramsOfCall.get(i) != TypeTable.ANYTYPE 
						&& (paramsOfCall.get(i) != TypeTable.INTEGER && paramsOfId.get(i) != TypeTable.FLOAT)
						&& (paramsOfCall.get(i) != TypeTable.FLOAT && paramsOfId.get(i) != TypeTable.INTEGER)
						&& paramsOfCall.get(i) != paramsOfId.get(i)) {
					if (!printed) {
						System.err.println("Parameters for the FUNCTION " + id + " do not match:");
						printed = true;
					}
					System.err.println("\t Parameter " + (i + 1) + " of type " + TypeTable.getTypeName(paramsOfCall.get(i)) + " but function accepts type " + TypeTable.getTypeName(paramsOfId.get(i)) + "!");
				}
			}
		}
		
		// Procedure is now referenced
		SymbolTable.putReferenced(id, true);
		return n.getRealType();
	}
	
	@Override
	public Integer visit(CallProcedure n) {
		int i;
		String id = n.getId();
		List<Integer> paramsOfId;
		List<Integer> paramsOfCall = new LinkedList<Integer>();
		
		// if procedure does not exist
		if (SymbolTable.getVarType(id) == null) {
			n.setRealType(TypeTable.ANYTYPE);
			System.err.println("PROCEDURE " + id + " was never declared!");
			return n.getRealType();
		}
		
		// If calling a function
		if (SymbolTable.getVarType(id).equals("FUNCTION")) {
			System.err.println("Procedure call invoking function " + id + "!");
			n.setRealType(TypeTable.ANYTYPE);
		} else {
			n.setRealType(SymbolTable.getDeclaredType(id));
		}
		
		// Get parameters
		if (n.getCallArguments() != null) {
			checkArraySubscript = false;
			n.getCallArguments().accept(this);				
			checkArraySubscript = true;
			for (ASTNode node : ((Parameters)n.getCallArguments()).getParameters()) {
				paramsOfCall.add(node.getRealType());
			}
		}
		
		// Get procedure params
		paramsOfId = SymbolTable.getParameters(id);
		
		// Compare size of call params and procedure params
		if (paramsOfCall.size() != paramsOfId.size()) {
			System.err.println("Parameters for the PROCEDURE " + id + " do not match:");
			System.err.println("\t Parameter size: " + paramsOfCall.size() + " but procedure accepts parameters of size: " + paramsOfId.size() + "!");
		} else {
			boolean printed = false;
			
			// Check if each param matches
			for (i = 0; i < paramsOfCall.size(); i++) {
				if (paramsOfCall.get(i) != TypeTable.ANYTYPE 
						&& (paramsOfCall.get(i) != TypeTable.INTEGER && paramsOfId.get(i) != TypeTable.FLOAT)
						&& (paramsOfCall.get(i) != TypeTable.FLOAT && paramsOfId.get(i) != TypeTable.INTEGER)
						&& paramsOfCall.get(i) != paramsOfId.get(i)) {
					if (!printed) {
						System.err.println("Parameters for the PROCEDURE " + id + " do not match:");
						printed = true;
					}
					System.err.println("\t Parameter " + (i + 1) + " of type " + TypeTable.getTypeName(paramsOfCall.get(i)) + " but procedure accepts type " + TypeTable.getTypeName(paramsOfId.get(i)) + "!");
				}
			}
		}
		
		// Procedure is now referenced
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
		n.setConvertedType(TypeTable.CHARACTER);
		return n.getRealType();
	}

	@Override
	public Integer visit(ConstantFloat n) {
		n.setRealType(TypeTable.FLOAT);
		n.setConvertedType(TypeTable.FLOAT);
		return n.getRealType();
	}

	@Override
	public Integer visit(ConstantInteger n) {
		n.setRealType(TypeTable.INTEGER);
		n.setConvertedType(TypeTable.INTEGER);
		return n.getRealType();
	}

	@Override
	public Integer visit(ConstantString n) {
		n.setRealType(TypeTable.STRING);
		n.setConvertedType(TypeTable.STRING);
		return n.getRealType();
	}

	/**
	 * Calls all accept on the IdDecls (identifiers)
	 */
	@Override
	public Integer visit(Declare n) {
		// Call accept on IdDecl
		for (ASTNode node : n.getDeclarations()) {
			node.accept(this);
		}
		
		return n.getRealType();
	}

	/**
	 * Get left and right operands' types
	 */
	@Override
	public Integer visit(Equal n) {
		Integer lOp = (Integer) n.getLeftOperand().accept(this);
		Integer rOp = (Integer) n.getRightOperand().accept(this);
		
		Integer resolvedType = TypeTable.relationalOperators[lOp][rOp];
		
		n.setRealType(resolvedType);
		n.setConvertedType(TypeTable.INTEGER);
		
		// Set converted type of children
		n.getLeftOperand().setConvertedType(resolvedType);
		n.getRightOperand().setConvertedType(resolvedType);
		
		if (resolvedType == TypeTable.ANYTYPE && lOp != TypeTable.ANYTYPE && rOp != TypeTable.ANYTYPE) {
			System.err.println("Cannot use '=' on type " + TypeTable.getTypeName(lOp) 
			+ " and type " + TypeTable.getTypeName(rOp) + "!");
			
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(Function n) {
		String id = n.getId();
		n.setRealType(SymbolTable.getDeclaredType(id));
		for (Statement statement : n.getStatements()) {
			if (statement != null) {
				statement.accept(this);
			}
		}
		
		for (SymbolValue value : SymbolTable.symTable.peek().values()) {
			if (!SymbolTable.getIsReferenced(value.getId())) {
				System.err.println(value.getVarType() + " " + value.getId() + " was declared but never referenced in FUNCTION " + id + "!");
			}
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(GreaterThan n) {
		Integer lOp = (Integer) n.getLeftOperand().accept(this);
		Integer rOp = (Integer) n.getRightOperand().accept(this);
		
		Integer resolvedType = TypeTable.relationalOperators[lOp][rOp];
		
		n.setRealType(resolvedType);
		n.setConvertedType(TypeTable.INTEGER);
		
		// Set converted type of children
		n.getLeftOperand().setConvertedType(resolvedType);
		n.getRightOperand().setConvertedType(resolvedType);
		
		
		if (resolvedType == TypeTable.ANYTYPE && lOp != TypeTable.ANYTYPE && rOp != TypeTable.ANYTYPE) {
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
	n.setConvertedType(TypeTable.INTEGER);
	
	// Set converted type of children
	n.getLeftOperand().setConvertedType(resolvedType);
	n.getRightOperand().setConvertedType(resolvedType);
	
	
	if (resolvedType == TypeTable.ANYTYPE && lOp != TypeTable.ANYTYPE && rOp != TypeTable.ANYTYPE) {
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
			if (checkArraySubscript && SymbolTable.getVarType(id).equals("ARRAY")) {
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
			if (checkArraySubscript && SymbolTable.getVarType(id).equals("ARRAY")) {
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
		
		// Set converted type of children
		n.getLeftOperand().setConvertedType(resolvedType);
		n.getRightOperand().setConvertedType(resolvedType);
		
		
		if (resolvedType == TypeTable.ANYTYPE && lOp != TypeTable.ANYTYPE && rOp != TypeTable.ANYTYPE) {
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
		n.setConvertedType(TypeTable.INTEGER);
		
		// Set converted type of children
		n.getLeftOperand().setConvertedType(resolvedType);
		n.getRightOperand().setConvertedType(resolvedType);
		
		
		if (resolvedType == TypeTable.ANYTYPE && lOp != TypeTable.ANYTYPE && rOp != TypeTable.ANYTYPE) {
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
		n.setConvertedType(TypeTable.INTEGER);
		
		// Set converted type of children
		n.getLeftOperand().setConvertedType(resolvedType);
		n.getRightOperand().setConvertedType(resolvedType);
		
		
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
		
		// Set converted type of children
		n.getLeftOperand().setConvertedType(resolvedType);
		n.getRightOperand().setConvertedType(resolvedType);
		
		
		if (resolvedType == TypeTable.ANYTYPE && lOp != TypeTable.ANYTYPE && rOp != TypeTable.ANYTYPE) {
			System.err.println("Cannot MULTIPLY type " + TypeTable.getTypeName(lOp) 
			+ " with " + TypeTable.getTypeName(rOp) + "!");
			
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(NOT n) {
		Integer declaredType = (Integer) n.getNotChild().accept(this);
		Integer resolvedType = TypeTable.notOperator[declaredType];
		
		n.getNotChild().setConvertedType(resolvedType);
		
		if (resolvedType == TypeTable.ANYTYPE && declaredType != TypeTable.ANYTYPE) {
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
		n.setConvertedType(TypeTable.INTEGER);
		
		// Set converted type of children
		n.getLeftOperand().setConvertedType(resolvedType);
		n.getRightOperand().setConvertedType(resolvedType);
		
		
		if (resolvedType == TypeTable.ANYTYPE && lOp != TypeTable.ANYTYPE && rOp != TypeTable.ANYTYPE) {
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
		
		// Set converted type of children
		n.getLeftOperand().setConvertedType(resolvedType);
		n.getRightOperand().setConvertedType(resolvedType);
		
		
		if (resolvedType == TypeTable.ANYTYPE && lOp != TypeTable.ANYTYPE && rOp != TypeTable.ANYTYPE) {
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
		n.setRealType(SymbolTable.getDeclaredType(id));
		for (Statement statement: n.getStatements()) {
			if (statement != null) {
				statement.accept(this);
			}
		}
		for (SymbolValue value : SymbolTable.symTable.peek().values()) {
			if (!SymbolTable.getIsReferenced(value.getId())) {
				System.err.println(value.getVarType() + " " + value.getId() + " was declared but never referenced in PROCEDURE " + id + "!");
			}
		}
		return n.getRealType();
	}

	/**
	 * Creates a new stack frame
	 * Calls all statements
	 * Checks for a return statement in main
	 * Creates an warning error for declared values that are not being referenced
	 * Pops the stack frame
	 */
	@Override
	public Integer visit(Program n) {
		n.setRealType(TypeTable.ANYTYPE);
		// Create a new stack frame
		pushFrame();
		
		// Go through all statements
		for (Statement statement : n.getStatements()) {
			statement.accept(this);
			
			// Check for return statement
			if (statement instanceof CompoundStatement) {
				for (ASTNode node : ((CompoundStatement)statement).getStatements()) {
					if (node instanceof Return) {
						System.err.println("Main has a RETURN statement!");
					}
				}
			}
		}
		
		// Variables, Functions, Procedures that were declared but never referenced
		for (SymbolValue value : SymbolTable.symTable.peek().values()) {
			if (!SymbolTable.getIsReferenced(value.getId())) {
				System.err.println(value.getVarType() + " " + value.getId() + " was declared but never referenced in MAIN!");
			}
		}
		
		// Pop the stack frame
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
		checkArraySubscript = false;
		Integer declaredType = (Integer) n.getReturn().accept(this);
		checkArraySubscript = true;
		n.setRealType(declaredType);
		return n.getRealType();
	}

	@Override
	public Integer visit(SubroutineDeclarations n) {
		String id = "";
		for (ASTNode node : n.getSubroutines()) {
			int declaredType = 3;
			String varType = "";
			List<Statement> statements = null;
			
			// If a type node that contains a function
			if (node instanceof Type) {
				
				// Get the declared type
				if (node instanceof TypeInteger) {
					declaredType = TypeTable.INTEGER;
				} else if (node instanceof TypeFloat) {
					declaredType = TypeTable.FLOAT;
				} else if (node instanceof TypeCharacter) {
					declaredType = TypeTable.CHARACTER;
				}
				
				varType = "FUNCTION";
				id = ((Type)node).getChild().getLabel();
				statements = ((Function)((Type)node).getChild()).getStatements();
				
			// If a procedure node
			} else if (node instanceof Procedure) {
				declaredType = TypeTable.ANYTYPE;
				varType = "PROCEDURE";
				id = node.getLabel();
				statements = ((Procedure)node).getStatements();
			}
			
			// Add all subroutines to the table
			if (SymbolTable.isDeclaredGlobal(id)) {
				System.err.println(SymbolTable.getVarType(id) + " with id: " + id + " is already declared globally!");
			} else {
				SymbolTable.putId(id);
				SymbolTable.putDeclaredType(id, declaredType);
				SymbolTable.putVarType(id, varType);	
			}
			
			// Add all the subroutines' parameters
			for (Statement statement : statements) {
				if (statement != null && statement instanceof Parameters) {
					
					// For each declare node
					for (ASTNode declareNode : ((Parameters)statement).getParameters()) {
						
						// For each type node
						for (ASTNode typeNode : ((Declare)declareNode).getDeclarations()) {
							
							if (typeNode instanceof TypeInteger) {
								SymbolTable.putParameter(id, TypeTable.INTEGER);
							} else if (typeNode instanceof TypeFloat) {
								SymbolTable.putParameter(id, TypeTable.FLOAT);
							} else if (typeNode instanceof TypeCharacter) {
								SymbolTable.putParameter(id, TypeTable.CHARACTER);
							}							
						}
					}
				}
			}
		 
		}
		
		for (ASTNode node : n.getSubroutines()) {
			pushFrame();
			node.accept(this);
			popFrame();
		}
		return n.getRealType();
	}

	@Override
	public Integer visit(Subtract n) {
		Integer lOp = (Integer) n.getLeftOperand().accept(this);
		Integer rOp = (Integer) n.getRightOperand().accept(this);
		
		Integer resolvedType = TypeTable.arithmeticOperators[lOp][rOp];
		
		n.setRealType(resolvedType);
		
		// Set converted type of children
		n.getLeftOperand().setConvertedType(resolvedType);
		n.getRightOperand().setConvertedType(resolvedType);
		
		
		if (resolvedType == TypeTable.ANYTYPE && lOp != TypeTable.ANYTYPE && rOp != TypeTable.ANYTYPE) {
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
		// Call declare nodes
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
		SymbolTable.symTable.push(new HashMap<String, SymbolValue>());
	}
	
	public void popFrame() {
		SymbolTable.symTable.pop();
	}
	

}
