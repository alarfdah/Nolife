package visitor;

import ast.*;

public interface Visitor<T> {
	public T visit(Add n);
	public T visit(Subtract n);
	public T visit(Divide n);
	public T visit(Multiply n);
	public T visit(IdDef n);
	public T visit(IdRef n);
	public T visit(IdDecl n);
	public T visit(Modulo n);
	public T visit(Constant n);
	public T visit(CallFunction n);
	public T visit(Subscript n);
	public T visit(TypeCharacter n);
	public T visit(TypeInteger n);
	public T visit(TypeFloat n);
	public T visit(If n);
	public T visit(While n);
	public T visit(Case n);
	public T visit(Input n);
	public T visit(Output n);
	public T visit(Return n);
	public T visit(Assignment n);
	public T visit(Program n);
	// public T visit();
}
