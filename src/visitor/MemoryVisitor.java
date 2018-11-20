package visitor;

import java.util.HashMap;
import java.util.Stack;

import ast.*;
import util.MemoryTable;
import util.MemoryValue;
import util.TypeTable;

public class MemoryVisitor implements Visitor {

	private int constOffset = 0;
	private int varOffset = 0;
	private int paramOffset = 0;
	
	private HashMap<String, ASTNode> constantMap = new HashMap<>();
	private Stack<HashMap<String, ASTNode>> scopeStack = new Stack<>();
	
	
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
		varOffset = 0;
	}
	
	public void incrementParamOffset(int by) {
		paramOffset += by;
	}
	
	public void resetParamOffset() {
		paramOffset = 0;
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
		System.out.println("Constant Map");
		for(ASTNode n : constantMap.values()) {
			System.out.println("Value: " + n.getLabel() 
			+ ", Type: " + TypeTable.getTypeName(n.getRealType()) + ", Offset: " + n.getOffset());
		}	
	}
	
	public void printVariableMap() {
		System.out.println("Variable Map");
		System.out.println("Current scope");
		for(ASTNode n : scopeStack.peek().values()) {
			System.out.println("Variable: " + n.getLabel() 
			+ ", Type: " + TypeTable.getTypeName(n.getRealType()) + ", Offset: " + n.getOffset());
		}
		System.out.println("Global scope");
		for(ASTNode n : scopeStack.get(0).values()) {
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
		String minBound = n.getMinBound().getChild(0).getLabel();
		String maxBound = n.getMaxBound().getChild(0).getLabel();
		
		return null;
	}

	@Override
	public Object visit(ArrayDef n) {
		n.getSubscriptExpression().accept(this);
		return null;
	}

	@Override
	public Object visit(ArrayRef n) {
		n.getSubscriptExpression().accept(this);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(CallProcedure n) {
		// TODO Auto-generated method stub
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
		for (ASTNode node : n.getStatements()) {
			node.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(ConstantCharacter n) {
		n.setOffset(getConstOffset());
		constantMap.put(n.getLabel(), n);
		incrementConstOffset(4);
		return null;
	}

	@Override
	public Object visit(ConstantFloat n) {
		n.setOffset(getConstOffset());
		constantMap.put(n.getLabel(), n);
		incrementConstOffset(4);
		return null;
	}

	@Override
	public Object visit(ConstantInteger n) {
		n.setOffset(getConstOffset());
		constantMap.put(n.getLabel(), n);
		incrementConstOffset(4);
		return null;
	}

	@Override
	public Object visit(ConstantString n) {
		n.setOffset(getConstOffset());
		constantMap.put(n.getLabel(), n);
		incrementConstOffset(n.getLabel().length() - 2 + 1);
		return null;
	}

	@Override
	public Object visit(Declare n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Equal n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Function n) {
		
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
		incrementVarOffset(4);
		return null;
	}

	@Override
	public Object visit(IdDef n) {
		int offset = 0;
		if (scopeStack.peek().containsKey(n.getLabel())) {
			offset = scopeStack.peek().get(n.getLabel()).getOffset();
			n.setOffset(offset);
		} else if (scopeStack.get(0).containsKey(n.getLabel())) {
			offset = scopeStack.get(0).get(n.getLabel()).getOffset();
			n.setOffset(offset);
		} else {
			System.out.println("variable never declared!");
		}
		return null;
	}

	@Override
	public Object visit(IdRef n) {
		int offset = 0;
		if (scopeStack.peek().containsKey(n.getLabel())) {
			offset = scopeStack.peek().get(n.getLabel()).getOffset();
			n.setOffset(offset);
		} else if (scopeStack.get(0).containsKey(n.getLabel())) {
			offset = scopeStack.get(0).get(n.getLabel()).getOffset();
			n.setOffset(offset);
		} else {
			System.out.println("variable never declared!");
		}
		return null;
	}

	@Override
	public Object visit(IfStatement n) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Procedure n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Program n) {
		pushFrame();
		for (Statement statement : n.getStatements()) {
			statement.accept(this);
		}
		printConstantMap();
		printVariableMap();
		popFrame();
		return null;
	}

	@Override
	public Object visit(Read n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Return n) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		
		return null;
	}

	@Override
	public Object visit(WhileStatement n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Write n) {
		n.getOutput().accept(this);
		return null;
		
		
	}

}
