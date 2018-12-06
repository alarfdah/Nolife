package visitor;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import ast.*;
import util.TypeTable;

public class MemoryVisitor implements Visitor<Object> {

	// TODO Check reset offset
	
	private int constOffset = 0;
	private int varOffset = 4;
	private int paramOffset = 8;
	
	private HashMap<String, ASTNode> constantMap = new HashMap<>();
	private Stack<HashMap<String, ASTNode>> scopeStack = new Stack<>();
	public static Map<String, ASTNode> sortedConstMap;
	
	public void incrementConstOffset(int by) {
		constOffset += by;
	}
	
	public void resetConstOffset() {
		constOffset = 0;
	}
	
	public void incrementVarOffset(int by) {
		varOffset += by;
	}
	
	public void resetVarOffset() {
		varOffset = 4;
	}
	
	public void incrementParamOffset(int by) {
		paramOffset += by;
	}
	
	public void resetParamOffset() {
		paramOffset = 8;
	}
	
	public int getConstOffset() {
		return constOffset;
	}
	
	public int getVarOffset() {
		return varOffset;
	}
	
	public int getParamOffset() {
		return paramOffset;
	}
	
	public void pushFrame() {
		scopeStack.push(new HashMap<String, ASTNode>());
	}
	
	public void popFrame() {
		scopeStack.pop();
	}
	
	public void printConstantMap() {
		if (constantMap.size() == 0) {
			System.out.println("NO CONSTANTS.");
		}
		for (ASTNode n : constantMap.values()) {
			System.out.println("Value: " + n.getLabel() 
			+ ", Type: " + TypeTable.getTypeName(n.getRealType()) + ", Offset: " + n.getOffset());
		}
	}
	
	public void printVariableMap() {
		if (scopeStack.peek().size() == 0) {
			System.out.println("NO VARIABLES.");
		}
		for (ASTNode n : scopeStack.peek().values()) {
			System.out.println("Variable: " + n.getLabel() 
			+ ", Type: " + TypeTable.getTypeName(n.getRealType()) + ", Offset: " + n.getOffset());
		}
	}
	
	@Override
	public Object visit(Add n) {
		n.getLeftOperand().accept(this);
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(AND n) {
		n.getLeftOperand().accept(this);
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(ArrayDecl n) {
		String minBound = n.getMinBound().getLabel();
		String maxBound = n.getMaxBound().getLabel();
		n.setOffset(getVarOffset());
		n.setArray(true); // Needed for passing arrays without subscripts
		n.setMinimumBound(minBound);
		n.setMaximumBound(maxBound);
		if (scopeStack.size() == 2)
			n.setLocal(true);
		else 
			n.setLocal(false);
		
		scopeStack.peek().put(n.getId(), n);
		incrementVarOffset((Integer.parseInt(maxBound) - Integer.parseInt(minBound)) * 4 + 4);
		return null;
	}

	@Override
	public Object visit(ArrayDef n) {
		n.getSubscriptExpression().accept(this);
		String id = n.getId();
		String minBound = "";
		String maxBound = "";
		int offset = 0;
		boolean local = false;
		boolean param = false;
		boolean array = false;
		if (scopeStack.peek().containsKey(id)) {
			minBound = scopeStack.peek().get(id).getMinimumBound();
			maxBound = scopeStack.peek().get(id).getMaximumBound();
			offset = scopeStack.peek().get(id).getOffset();
			local = scopeStack.peek().get(id).isLocal();
			param = scopeStack.peek().get(id).isParam();
			array = scopeStack.peek().get(id).isArray();
			n.setOffset(offset);
			n.setLocal(local);
			n.setParam(param);
			n.setArray(array);
			n.setMinimumBound(minBound);
			n.setMaximumBound(maxBound);
		} else if (scopeStack.get(0).containsKey(n.getId())) {
			minBound = scopeStack.get(0).get(id).getMinimumBound();
			maxBound = scopeStack.get(0).get(id).getMaximumBound();
			offset = scopeStack.get(0).get(id).getOffset();
			local = scopeStack.get(0).get(id).isLocal();
			param = scopeStack.get(0).get(id).isParam();
			array = scopeStack.get(0).get(id).isArray();
			n.setOffset(offset);
			n.setLocal(local);
			n.setParam(param);
			n.setArray(array);
			n.setMinimumBound(minBound);
			n.setMaximumBound(maxBound);
		} else {
			System.out.println("array '" + id + "' never declared!");
		}
		return null;
	}

	@Override
	public Object visit(ArrayRef n) {
		n.getSubscriptExpression().accept(this);
		String id = n.getId();
		int offset = 0;
		String minBound = "";
		String maxBound = "";
		boolean local = false;
		boolean param = false;
		boolean array = false;
		if (scopeStack.peek().containsKey(id)) {
			minBound = scopeStack.peek().get(id).getMinimumBound();
			maxBound = scopeStack.peek().get(id).getMaximumBound();
			offset = scopeStack.peek().get(id).getOffset();
			local = scopeStack.peek().get(id).isLocal();
			param = scopeStack.peek().get(id).isParam();
			array = scopeStack.peek().get(id).isArray();
			n.setOffset(offset);
			n.setLocal(local);
			n.setParam(param);
			n.setArray(array);
			n.setMinimumBound(minBound);
			n.setMaximumBound(maxBound);
		} else if (scopeStack.get(0).containsKey(n.getId())) {
			minBound = scopeStack.get(0).get(id).getMinimumBound();
			maxBound = scopeStack.get(0).get(id).getMaximumBound();
			offset = scopeStack.get(0).get(id).getOffset();
			local = scopeStack.get(0).get(id).isLocal();
			param = scopeStack.get(0).get(id).isParam();
			array = scopeStack.get(0).get(id).isArray();
			n.setOffset(offset);
			n.setLocal(local);
			n.setParam(param);
			n.setArray(array);
			n.setMinimumBound(minBound);
			n.setMaximumBound(maxBound);
		} else {
			System.out.println("array '" + id + "' never declared!");
		}
		return null;
	}

	@Override
	public Object visit(Assignment n) {
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Object visit(CallFunction n) {
		if (n.getCallArguments() != null) {
			n.getCallArguments().accept(this);				
		}
		return null;
	}

	@Override
	public Object visit(CallProcedure n) {
		if (n.getCallArguments() != null) {
			n.getCallArguments().accept(this);				
		}
		return null;
	}

	@Override
	public Object visit(Cases n) {
		for (ASTNode node : n.getClauseList()) {
			node.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(CaseStatement n) {
		n.getCaseExpression().accept(this);
		if (n.getCases() != null) {
			n.getCases().accept(this);
		}
		return null;
	}

	@Override
	public Object visit(Clause n) {
		n.getStatement().accept(this);
		return null;
	}

	@Override
	public Object visit(CompoundStatement n) {
		for (ASTNode node : n.getStatements()) {
			node.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(ConstantCharacter n) {
		return null;
	}

	@Override
	public Object visit(ConstantFloat n) {
		if (!constantMap.containsKey(n.getLabel())) {
			n.setOffset(getConstOffset());
			constantMap.put(n.getLabel(), n);
			incrementConstOffset(4);			
		} else {
			n.setOffset(constantMap.get(n.getLabel()).getOffset());
		}
		return null;
	}

	@Override
	public Object visit(ConstantInteger n) {
		return null;
	}

	@Override
	public Object visit(ConstantString n) {
		if (!constantMap.containsKey(n.getLabel())) {
			n.setOffset(getConstOffset());
			constantMap.put(n.getLabel(), n);
			incrementConstOffset(n.getLabel().length() - 2 + 1);			
		} else {
			n.setOffset(constantMap.get(n.getLabel()).getOffset());
		}
		return null;
	}

	@Override
	public Object visit(Declare n) {
		for (ASTNode node : n.getDeclarations()) {
			node.accept(this);	
		}
		return null;
	}

	@Override
	public Object visit(Equal n) {
		n.getLeftOperand().accept(this);
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(Function n) {
		String id = n.getId();
		for (ASTNode node : n.getStatements()) {
			// Can have no parameters. So null node.
			if (node != null) {
				if (node instanceof Parameters) {
					node.setLabel(id);
				}
				node.accept(this);
			}
		}
		
		System.out.println("*****FUNCTION(" + n.getId() + ")******");
		printVariableMap();
		System.out.println("----------------------");
		return null;
	}

	@Override
	public Object visit(GreaterThan n) {
		n.getLeftOperand().accept(this);
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(GreaterThanEqual n) {
		n.getLeftOperand().accept(this);
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(IdDecl n) {
		n.setOffset(getVarOffset());
		n.setArray(false);
		if (scopeStack.size() == 2)
			n.setLocal(true);
		else
			n.setLocal(false);
		
		scopeStack.peek().put(n.getId(), n);
		incrementVarOffset(4);
		return null;
	}

	@Override
	public Object visit(IdDef n) {
		String id = n.getId();
		int offset = 0;
		boolean local = false;
		boolean param = false;
		if (scopeStack.peek().containsKey(id)) {
			offset = scopeStack.peek().get(id).getOffset();
			local = scopeStack.peek().get(id).isLocal();
			param = scopeStack.peek().get(id).isParam();
			n.setOffset(offset);
			n.setLocal(local);
			n.setParam(param);
		} else if (scopeStack.get(0).containsKey(n.getId())) {
			offset = scopeStack.get(0).get(id).getOffset();
			local = scopeStack.get(0).get(id).isLocal();
			param = scopeStack.get(0).get(id).isParam();
			n.setOffset(offset);
			n.setLocal(local);
			n.setParam(param);
		} else {
			System.out.println("variable '" + id + "' never declared!");
		}
		return null;
	}

	@Override
	public Object visit(IdRef n) {
		int offset = -1;
		String id = n.getId();
		String maxBound = "";
		String minBound = "";
		boolean array = false;
		boolean param = false;
		boolean local = false;
		if (scopeStack.peek().containsKey(id)) {
			minBound = scopeStack.peek().get(id).getMinimumBound();
			maxBound = scopeStack.peek().get(id).getMaximumBound();
			offset = scopeStack.peek().get(id).getOffset();
			local = scopeStack.peek().get(id).isLocal();
			param = scopeStack.peek().get(id).isParam();
			array = scopeStack.peek().get(id).isArray();
			n.setLocal(local);
			n.setParam(param);
			n.setArray(array);
			n.setOffset(offset);
			n.setMinimumBound(minBound);
			n.setMaximumBound(maxBound);
		} else if (scopeStack.get(0).containsKey(id)) {
			minBound = scopeStack.get(0).get(id).getMinimumBound();
			maxBound = scopeStack.get(0).get(id).getMaximumBound();
			offset = scopeStack.get(0).get(id).getOffset();
			local = scopeStack.get(0).get(id).isLocal();
			param = scopeStack.get(0).get(id).isParam();
			array = scopeStack.get(0).get(id).isArray();
			n.setLocal(local);
			n.setParam(param);
			n.setArray(array);
			n.setOffset(offset);
			n.setMinimumBound(minBound);
			n.setMaximumBound(maxBound);
		}
//		int offset = 0;
//		boolean local = false;
//		boolean param = false;
//		if (scopeStack.peek().containsKey(id)) {
//			offset = scopeStack.peek().get(id).getOffset();
//			local = scopeStack.peek().get(id).isLocal();
//			param = scopeStack.peek().get(id).isParam();
//			n.setOffset(offset);
//			n.setLocal(local);
//			n.setParam(param);
//		} else if (scopeStack.get(0).containsKey(n.getId())) {
//			offset = scopeStack.get(0).get(id).getOffset();
//			local = scopeStack.get(0).get(id).isLocal();
//			param = scopeStack.get(0).get(id).isParam();
//			n.setOffset(offset);
//			n.setLocal(local);
//			n.setParam(param);
//		} else {
//			System.out.println("variable '" + id + "' never declared!");
//		}
		
		return null;
	}

	@Override
	public Object visit(IfStatement n) {
		n.getIfExpression().accept(this);
		n.getThenStatement().accept(this);
		if (n.getElseStatement() != null) {
			n.getElseStatement().accept(this);
		}
		return null;
	}

	@Override
	public Object visit(LessThan n) {
		n.getLeftOperand().accept(this);
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(LessThanEqual n) {
		n.getLeftOperand().accept(this);
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(Modulo n) {
		n.getLeftOperand().accept(this);
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(Multiply n) {
		n.getLeftOperand().accept(this);
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(NOT n) {
		n.getNotChild().accept(this);
		return null;
	}

	@Override
	public Object visit(NotEqual n) {
		n.getLeftOperand().accept(this);
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(OR n) {
		n.getLeftOperand().accept(this);
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(Parameters n) {
		resetParamOffset();
		
		for (ASTNode node : n.getParameters()) {
			if (node instanceof Declaration) { // Params in function signature
				Declaration decl = (Declaration)node;
				for (ASTNode typeNode : decl.getChildren()) { // For each type
					if (typeNode.getChild(0) instanceof IdDecl) {
						IdDecl idDecl = (IdDecl)typeNode.getChild(0);
						idDecl.setOffset(paramOffset);
						idDecl.setLocal(true);
						idDecl.setParam(true);
						idDecl.setArray(true);
						scopeStack.peek().put(idDecl.getId(), idDecl);
					} else if (typeNode.getChild(0) instanceof ArrayDecl) {
						ArrayDecl arrDecl = (ArrayDecl)typeNode.getChild(0);
						arrDecl.setOffset(paramOffset);
						arrDecl.setLocal(true);
						arrDecl.setParam(true);
						arrDecl.setArray(true);
						arrDecl.setMinimumBound(arrDecl.getMinBound().getLabel());
						arrDecl.setMaximumBound(arrDecl.getMaxBound().getLabel());
						scopeStack.peek().put(arrDecl.getId(), arrDecl);
					}
					incrementParamOffset(4);
				}
			} else { // Parameters in call functions
				node.accept(this);
			}
		}
		return null;
	}

	@Override
	public Object visit(Procedure n) {
		for (ASTNode node : n.getStatements()) {
			if (node != null) {
				node.accept(this);
				resetVarOffset();
				resetParamOffset();
			}
		}
		System.out.println("*****PROCEDURE(" + n.getId() + ")******");
		printVariableMap();
		System.out.println("------------------------");
		return null;
	}

	@Override
	public Object visit(Program n) {
		pushFrame();
		for (Statement statement : n.getStatements()) {
			statement.accept(this);
		}
		System.out.println("*****Constant Map*****");
		printConstantMap();
		System.out.println("----------------------");
		
		System.out.println("*****GLOBAL SCOPE*****");
		printVariableMap();
		System.out.println("----------------------");
		popFrame();
		sortedConstMap = sortMap(constantMap);
		
		return null;
	}

	@Override
	public Object visit(Read n) {
		n.getInput().accept(this);
		return null;
	}

	@Override
	public Object visit(Return n) {
		n.getReturn().accept(this);
		return null;
	}

	@Override
	public Object visit(SubroutineDeclarations n) {
		for (ASTNode node : n.getSubroutines()) {
			pushFrame();
			node.accept(this);
			popFrame();
		}
		return null;
	}

	@Override
	public Object visit(Subtract n) {
		n.getLeftOperand().accept(this);
		n.getRightOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(TypeCharacter n) {
		n.getChild().accept(this);
		return null;
	}

	@Override
	public Object visit(TypeFloat n) {
		n.getChild().accept(this);
		return null;
	}

	@Override
	public Object visit(TypeInteger n) {
		n.getChild().accept(this);
		return null;
	}

	@Override
	public Object visit(VariableDeclarations n) {
		for (ASTNode node : n.getDecls()) {
			node.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(WhileStatement n) {
		n.getWhileExpression().accept(this);
		n.getDoStatement().accept(this);
		return null;
	}

	@Override
	public Object visit(Write n) {
		n.getOutput().accept(this);
		return null;	
	}

	private Map<String, ASTNode> sortMap(HashMap<String, ASTNode> map) {
		// 1. Convert Map to List of Map
	    List<Map.Entry<String, ASTNode>> list =
	            new LinkedList<Map.Entry<String, ASTNode>>(map.entrySet());
	
	    // 2. Sort list with Collections.sort(), provide a custom Comparator
	    //    Try switch the o1 o2 position for a different order
	    Collections.sort(list, new Comparator<Map.Entry<String, ASTNode>>() {
	        public int compare(Map.Entry<String, ASTNode> o1,
	                           Map.Entry<String, ASTNode> o2) {
	            return ((Integer)o1.getValue().getOffset()).compareTo((Integer)o2.getValue().getOffset());
	        }
	    });
	
	    // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
	    Map<String, ASTNode> sortedMap = new LinkedHashMap<String, ASTNode>();
	    for (Map.Entry<String, ASTNode> entry : list) {
	        sortedMap.put(entry.getKey(), entry.getValue());
	    }
	    
    return sortedMap;
	}

	
}
