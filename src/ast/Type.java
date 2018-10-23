package ast;

public abstract class Type extends ASTNode {

	public abstract String getTypeKeyword();
	
	public ASTNode getChild() {
		return getChild(0);
	}
}
