package visitor;

import ast.*;

public class CodeVisitor implements Visitor {

	
	private String[] register = {"%eax", "%ebx", "%ecx", "%edi", "%esi", "%edx"};
	private boolean[] regUsed = { false,  false,  false,  false,  false,  false};
	
	private int findEmptyReg(int n) {
		if (n != -1) {
			regUsed[n] = true;
			return n;
		}
		int i = 0;
		for (i = 0; i < register.length; i++) {
			if (!regUsed[i]) {
				regUsed[i] = true;
				return i;
			}
		}
		return -1;
	}
	
	private void freeReg(int n) {
		regUsed[n] = false;
	}
	
	@Override
	public Object visit(Add n) {
		n.getLeftOperand();
		n.getRightOperand();
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
		String output = "";
		output += "# Assignment...\n";
		if (n.getLhs() instanceof IdDef) {
			IdDef idDef = (IdDef)n.getLhs();
			output += "mov  [%ebp - " + idDef.getOffset() + "], " + n.getRhs().getLabel() + "\n";
		} else if (n.getLhs() instanceof ArrayDef) {
			ArrayDef arrDef = (ArrayDef)n.getLhs();
		}
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
		String output = "";
		output += "\t.intel_syntax\n";
		output += "\t.section .rodata\n";
		output += ".io_format:\n";
		output += "\t.string \"%f\\12\"\n";
		output += "\t.string \"%d\\12\"\n";
		output += "\t.string \"%c\\12\"\n";
		output += "\t.string \"%s\\12\"\n";
		output += "\t.text\n";
		output += "\t.globl main;\n";
		output += "\t.type main, @function\n";
		output += "_constant:\n";
		for (String label : MemoryVisitor.sortedConstMap.keySet()) {
			ASTNode node = MemoryVisitor.sortedConstMap.get(label);
			if (node instanceof ConstantFloat) {
				output += "\t.float " + node.getLabel() + "\n";
			} else if (node instanceof ConstantString) {
				output += "\t.string \"" + node.getLabel().substring(1, node.getLabel().length() - 1) + "\"\n";
			}
		}
		output += "main:\n";
		output += "\tpush %ebp\n";
		output += "\tmov  %ebp, %esp";
		System.out.println(output);
		
		for (Statement statement : n.getStatements()) {
			statement.accept(this);
		}
		output = "\tleave\n";
		output += "\tret\n";
		System.out.println(output);
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
		String output = "";
		int subEsp = 0;
		
		for (ASTNode node : n.getDecls()) {
			for (ASTNode node2 : node.getChildren()) {
				if (node2.getChild(0) instanceof ArrayDecl) {
					ArrayDecl arrDecl = (ArrayDecl)node2.getChild(0);
					int max = Integer.parseInt(arrDecl.getMaximumBound());
					int min = Integer.parseInt(arrDecl.getMinimumBound());
					subEsp += max - min + 1;
				} else {
					subEsp++;					
				}
			}
			node.accept(this);
		}
		output = "\tsub  %esp, " + (subEsp * 4) + "\n";
		System.out.println(output);
		return null;
	}

	@Override
	public Object visit(WhileStatement n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Write n) {
		String output = "";
		if (n.getOutput() instanceof ConstantFloat) {			
			int regi = findEmptyReg(-1);
			ConstantFloat cf = (ConstantFloat)n.getOutput();
			output += "\t# Float constant...\n";
			output += "\tsub  %esp, 8\n";
			output += "\tmov  " + register[regi] + ", [offset flat:_constant + " + MemoryVisitor.sortedConstMap.get(cf.getFloat()).getOffset() + "]\n";
			output += "\tfld  dword ptr [" + register[regi] + "]\n";
			output += "\tfstp qword ptr [%esp]\n";
			output += "\tpush [offset flat:.io_format + 0]\n";
			output += "\tcall printf\n";
			output += "\tadd  %esp, 12\n";
			freeReg(regi);
		} else if (n.getOutput() instanceof ConstantInteger) {
			ConstantInteger ci = (ConstantInteger)n.getOutput();
			output += "\t# Integer constant...\n";
			output += "\tpush " + ci.getInteger() + "\n";
			output += "\tpush [offset flat:.io_format + 4]\n";
			output += "\tcall printf\n";
			output += "\tadd  %esp, 8\n";
		} else if (n.getOutput() instanceof ConstantCharacter) {
			ConstantCharacter cc = (ConstantCharacter)n.getOutput();
			output += "\t# Character constant...\n";
			output += "\tpush " + (int)cc.getCharacter().charAt(1) + " #" + cc.getCharacter() + "\n";
			output += "\tpush [offset flat:.io_format + 8]\n";
			output += "\tcall printf\n";
			output += "\tadd  %esp, 8\n";
		} else if (n.getOutput() instanceof ConstantString) {
			ConstantString cs = (ConstantString)n.getOutput();
			output += "\t# String constant...\n";
			output += "\tpush [offset flat:_constant + " + MemoryVisitor.sortedConstMap.get(cs.getString()).getOffset() + "]\n";
			output += "\tpush [offset flat:.io_format + 12]\n";
			output += "\tcall printf\n";
			output += "\tadd  %esp, " + (cs.getString().length() - 1 + 4) + "\n";
		}
		System.out.println(output);
		return null;
	}

}
