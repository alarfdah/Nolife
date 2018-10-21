package visitor;

import ast.*;

public interface Visitor<T> {
	public T visit(Add n);
	public T visit(AND n);
	public T visit(Assignment n);
	public T visit(CallFunction n);
	public T visit(Case n);
	public T visit(CompoundStatement n);
	public T visit(ConstantCharacter n);
	public T visit(ConstantFloat n);
	public T visit(ConstantInteger n);
	public T visit(ConstantString n);
	public T visit(Declare n);
	public T visit(Equal n);
	public T visit(GreaterThan n);
	public T visit(GreaterThanEqual n);
	public T visit(IdDecl n);
	public T visit(IdDef n);
	public T visit(IdRef n);
	public T visit(IF n);
	public T visit(LessThan n);
	public T visit(LessThanEqual n);
	public T visit(Modulo n);
	public T visit(Multiply n);
	public T visit(NOT n);
	public T visit(NotEqual n);
	public T visit(OR n);
	public T visit(Program n);
	public T visit(Read n);
	public T visit(Return n);
	public T visit(Subscript n);
	public T visit(Subtract n);
	public T visit(TypeCharacter n);
	public T visit(TypeFloat n);
	public T visit(TypeInteger n);
	public T visit(VariableDeclarations n);
	public T visit(While n);
	public T visit(Write n);
	public String getSource();
	// public T visit();
}
