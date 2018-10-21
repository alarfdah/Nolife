package ast;

import ast.*;

public class ASTNodeFactory {

	public ASTNode makeASTNode(String nodeType) {
		if (nodeType == "Add")
			return new Add();
		else if (nodeType == "Subtract")
			return new Subtract();
		else if (nodeType == "Modulo")
			return new Modulo();
		else if (nodeType == "Multiply")
			return new Multiply();
		else if (nodeType == "IdDef")
			return new IdDef();
		else if (nodeType == "IdDecl")
			return new IdDecl();
		else if (nodeType == "IdRef")
			return new IdRef();
		else if (nodeType == "Declare")
			return new Declare();
		else if (nodeType == "VariableDeclarations")
			return new VariableDeclarations();
		else if (nodeType == "TypeInteger")
			return new TypeInteger();
		else if (nodeType == "TypeFloat")
			return new TypeFloat();
		else if (nodeType == "TypeCharacter")
			return new TypeCharacter();
		else if (nodeType == "Program")
			return new Program();
		else if (nodeType == "Assignment")
			return new Assignment();
		else if (nodeType == "CompoundStatement")
			return new CompoundStatement();
		else if (nodeType == "Read")
			return new Read();
		else if (nodeType == "Write")
			return new Write();
		else if (nodeType == "ConstantInteger")
			return new ConstantInteger();
		else if (nodeType == "ConstantFloat")
			return new ConstantFloat();
		else if (nodeType == "ConstantCharacter")
			return new ConstantCharacter();
		else if (nodeType == "ConstantString")
			return new ConstantString();
		else
			return null;
	}
}
