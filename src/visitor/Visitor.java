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
	public T visit(Declaration n);
	public T visit(Constant n);
}
