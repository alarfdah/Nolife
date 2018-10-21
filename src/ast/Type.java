package ast;

public abstract class Type extends ASTNode {

	public abstract String getTypeName();
	
	public ASTNode getChild() {
		return getChild(0);
	}
}
