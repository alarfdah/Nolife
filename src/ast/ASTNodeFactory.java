package ast;

import ast.*;

public class ASTNodeFactory {

	public ASTNode makeASTNode(String nodeType) {
		if (nodeType == "Add")
			return new Add();
		else if (nodeType == "AND")
			return new AND();
		else if (nodeType == "ArrayDecl")
			return new ArrayDecl();
		else if (nodeType == "ArrayDef")
			return new ArrayDef();
		else if (nodeType == "ArrayRef")
			return new ArrayRef();
		else if (nodeType == "Assignment")
			return new Assignment();
		else if (nodeType == "Call")
			return new Call();
		else if (nodeType == "Cases")
			return new Cases();
		else if (nodeType == "CaseStatement")
			return new CaseStatement();
		else if (nodeType == "Clause")
			return new Clause();
		else if (nodeType == "CompoundStatement")
			return new CompoundStatement();
		else if (nodeType == "ConstantCharacter")
			return new ConstantCharacter();
		else if (nodeType == "ConstantFloat")
			return new ConstantFloat();
		else if (nodeType == "ConstantInteger")
			return new ConstantInteger();
		else if (nodeType == "ConstantString")
			return new ConstantString();
		else if (nodeType == "Declare")
			return new Declare();
		else if (nodeType == "Equal")
			return new Equal();
		else if (nodeType == "Function")
			return new Function();
		else if (nodeType == "GreaterThan")
			return new GreaterThan();
		else if (nodeType == "GreaterThanEqual")
			return new GreaterThanEqual();
		else if (nodeType == "IdDecl")
			return new IdDecl();
		else if (nodeType == "IdDef")
			return new IdDef();
		else if (nodeType == "IdRef")
			return new IdRef();
		else if (nodeType == "IfStatement")
			return new IfStatement();
		else if (nodeType == "LessThan")
			return new LessThan();
		else if (nodeType == "LessThanEqual")
			return new LessThanEqual();
		else if (nodeType == "Modulo")
			return new Modulo();
		else if (nodeType == "Multiply")
			return new Multiply();
		else if (nodeType == "NOT")
			return new NOT();
		else if (nodeType == "NotEqual")
			return new NotEqual();
		else if (nodeType == "OR")
			return new OR();
		else if (nodeType == "Parameters")
			return new Parameters();
		else if (nodeType == "Procedure")
			return new Procedure();
		else if (nodeType == "Program")
			return new Program();
		else if (nodeType == "Read")
			return new Read();
		else if (nodeType == "Return")
			return new Return();
		else if (nodeType == "SubroutineDeclarations")
			return new SubroutineDeclarations();
		else if (nodeType == "Subtract")
			return new Subtract();
		else if (nodeType == "TypeCharacter")
			return new TypeCharacter();
		else if (nodeType == "TypeFloat")
			return new TypeFloat();
		else if (nodeType == "TypeInteger")
			return new TypeInteger();
		else if (nodeType == "VariableDeclarations")
			return new VariableDeclarations();
		else if (nodeType == "WhileStatement")
			return new WhileStatement();
		else if (nodeType == "Write")
			return new Write();
		else
			return null;
	}
}
