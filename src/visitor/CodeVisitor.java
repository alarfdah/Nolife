package visitor;

import ast.*;

public class CodeVisitor implements Visitor {

	@Override
	public Object visit(Add n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(AND n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ArrayDecl n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ArrayDef n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ArrayRef n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Assignment n) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ConstantCharacter n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ConstantFloat n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ConstantInteger n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ConstantString n) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Function n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(GreaterThan n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(GreaterThanEqual n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(IdDecl n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(IdDef n) {
		
		return null;
	}

	@Override
	public Object visit(IdRef n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(IfStatement n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LessThan n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LessThanEqual n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Modulo n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Multiply n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NOT n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NotEqual n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(OR n) {
		// TODO Auto-generated method stub
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
		for (Statement statement : n.getStatements()) {
			statement.accept(this);
		}
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
		// TODO Auto-generated method stub
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
		for (ASTNode node : n.getDecls()) {
			node.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(WhileStatement n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Write n) {
		String output = ".section\n" + 
				".int_format:\n" + 
				"     .string\n" + 
				"...\n" + 
				" .ro_data\n" + 
				"\"%d\\012\\0\"\n" + 
				"     push 1\n" + 
				"     push offset flat:.int_format\n" + 
				"     call printf\n" + 
				"     add %esp,8";
		System.out.println(output);
		return null;
	}

}
