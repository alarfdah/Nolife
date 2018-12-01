package visitor;

import ast.*;
import util.TypeTable;

public class CodeVisitor implements Visitor<Integer> {

								// 0,		1,		2,		3,		4,		5
	private String[] register = {"%eax", "%ebx", "%ecx", "%edi", "%esi", "%edx"};
	private boolean[] regUsed = { false,  false,  false,  false,  false,  false};
	
	private int findEmptyReg(int n) {
		if (n != -1) {
			regUsed[n] = true;
			return n;
		}
		int i = 1;
		for (i = 1; i < register.length; i++) {
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
	public Integer visit(Add n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		output += "# Add...\n";
		
		if (n.getRealType() == TypeTable.FLOAT) {
			// Push lhs and rhs onto the stack and pass their address
			output += "\tpush " + register[lhs] + "\n";
			output += "\tfld  dword ptr[%esp]\n";
			output += "\tadd  %esp, 4\n";
			output += "\tpush " + register[rhs] + "\n";
			output += "\tfld  dword ptr[%esp]\n";
			output += "\tadd  %esp, 4\n";
			// Add top two values on the float stack
			output += "\tfadd %st(0), %st(1)\n";
			output += "\tsub  %esp, 4\n";
			output += "\tfstp dword ptr[%esp]\n";
			output += "\tpop  " + register[lhs] + "\n";
			// Free the float stack
			output += "\tsub  %esp, 4\n";
			output += "\tfstp dword ptr[%esp]\n";
			output += "\tadd  %esp, 4";
		} else {
			output += "\tadd  " + register[lhs] + ", " + register[rhs] + "\n";			
		}
		System.out.println(output);
		freeReg(rhs);
		return lhs;
	}

	@Override
	public Integer visit(AND n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(ArrayDecl n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(ArrayDef n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(ArrayRef n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Assignment n) {
		String output = "";	
		output += "# Assignment...";
		System.out.println(output);
		
		Integer rhs = (Integer)n.getRhs().accept(this);
		
		if (n.getLhs() instanceof IdDef) {
			IdDef idDef = (IdDef)n.getLhs();
			output = "\tmov  dword ptr [%ebp - " + idDef.getOffset() + "], " + register[rhs]; 
		} else if (n.getLhs() instanceof ArrayDef) {
			ArrayDef arrDef = (ArrayDef)n.getLhs();
		}
		System.out.println(output);
		freeReg(rhs);
		return null;
	}

	@Override
	public Integer visit(CallFunction n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(CallProcedure n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Cases n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(CaseStatement n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Clause n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(CompoundStatement n) {
		for (ASTNode node : n.getStatements()) {
			node.accept(this);
		}
		return null;
	}

	@Override
	public Integer visit(ConstantCharacter n) {
		int regi = findEmptyReg(-1);
		String output = "";
		output += "\tmov  " + register[regi] + ", " + n.getCharacter();
		System.out.println(output);
		return regi;
	}

	@Override
	public Integer visit(ConstantFloat n) {
		int regi = findEmptyReg(-1);
		String output = "";
		output += "\tmov  " + register[regi] + ", [offset flat:_constant + " + n.getOffset() + "]\n";
		output += "\tmov  " + register[regi] + ", dword ptr[" + register[regi] + "]";
		System.out.println(output);
		return regi;
	}

	@Override
	public Integer visit(ConstantInteger n) {
		int regi = findEmptyReg(-1);
		String output = "";
		output += "\tmov  " + register[regi] + ", " + n.getInteger();
		System.out.println(output);
		return regi;
	}

	@Override
	public Integer visit(ConstantString n) {
		int regi = findEmptyReg(-1);
		String output = "";
		output += "\tmov  " + register[regi] + ", [offset flat:_constant + " + n.getOffset() + "]\n";
		output += "\tmov  " + register[regi] + ", dword ptr[" + register[regi] + "]";
		System.out.println(output);
		return regi;
	}

	@Override
	public Integer visit(Declare n) {
		for (ASTNode node : n.getDeclarations()) {
			node.accept(this);	
		}
		return null;
	}

	@Override
	public Integer visit(Equal n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Function n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(GreaterThan n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(GreaterThanEqual n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(IdDecl n) {
		
		return null;
	}

	@Override
	public Integer visit(IdDef n) {
		
		return null;
	}

	@Override
	public Integer visit(IdRef n) {
		int regi = findEmptyReg(-1);
		String output = "";
		output += "#IdRef...\n";
		output += "\tmov  " + register[regi] + ", dword ptr [%ebp - " + n.getOffset() + "]";
		System.out.println(output);
		return regi;
	}

	@Override
	public Integer visit(IfStatement n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(LessThan n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(LessThanEqual n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Modulo n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		int eax = findEmptyReg(0);
		output += "# Modulo...\n";
		output += "\tmov " + register[eax] + ", " + lhs;
		output += "\tcdq\n";
		output += "\tidiv " + register[rhs] + "\n";
		output += "\tmov  " + register[lhs] + ", %edx\n";
		System.out.println(output);
		freeReg(rhs);
		freeReg(eax);
		return lhs;
	}

	@Override
	public Integer visit(Multiply n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		output += "# Multiply...\n";
		if (n.getRealType() == TypeTable.FLOAT) {
			// Push lhs and rhs onto the stack and pass their address
			output += "\tpush " + register[lhs] + "\n";
			output += "\tfld  dword ptr[%esp]\n";
			output += "\tadd  %esp, 4\n";
			output += "\tpush " + register[rhs] + "\n";
			output += "\tfld  dword ptr[%esp]\n";
			output += "\tadd  %esp, 4\n";
			// Multiply top two values on the float stack
			output += "\tfmul %st(0), %st(1)\n";
			output += "\tsub %esp, 4\n";
			output += "\tfstp dword ptr[%esp]\n";
			output += "\tpop " + register[lhs] + "\n";
			// Free the float stack
			output += "\tsub  %esp, 4\n";
			output += "\tfstp dword ptr[%esp]\n";
			output += "\tadd  %esp, 4\n";
		} else {
			output += "\timul  " + register[lhs] + ", " + register[rhs] + "\n";			
		}
		System.out.println(output);
		freeReg(rhs);
		return lhs;
	}

	@Override
	public Integer visit(NOT n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(NotEqual n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(OR n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Parameters n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Procedure n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Program n) {
		String output = "";
		output += "\t.intel_syntax\n";
		output += "\t.section .rodata\n";
		output += ".io_format:\n";
		output += "\t.string \"%f\\12\"\n";
		output += "\t.string \"%d\\12\"\n";
		output += "\t.string \"%c\\12\"\n";
		output += "\t.string \"%s\\12\"\n";
		output += "\t.string \"%f\"\n";
		output += "\t.string \"%d\"\n";
		output += "\t.string \"%c\"\n";
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
	public Integer visit(Read n) {
		String output = "";
		if (n.getInput() instanceof IdDef) {
			IdDef idDef = (IdDef)n.getInput();
			int regi = findEmptyReg(-1);
			output += "\tmov  " + register[regi] + ", %ebp\n";
			output += "\tsub  " + register[regi] + ", " + idDef.getOffset() + "\n";
			output += "\tpush " + register[regi] + "\n";
			if (idDef.getRealType() == TypeTable.FLOAT) {
				output += "\tpush [offset flat:.io_format + 16]\n";
			} else if (idDef.getRealType() == TypeTable.INTEGER) {
				output += "\tpush [offset flat:.io_format + 20]\n";
			} else if (idDef.getRealType() == TypeTable.CHARACTER) {
				output += "\tpush [offset flat:.io_format + 24]\n";				
			}
			output += "\tcall scanf\n";
			output += "\tadd  %esp, 8\n";
		} else if (n.getInput() instanceof ArrayDef) {
			ArrayDef arrDef = (ArrayDef)n.getInput();
			output += "\tmov  \n";
			if (arrDef.getRealType() == TypeTable.FLOAT) {
				output += "\tpush [offset flat:.io_format + 16]\n";
			} else if (arrDef.getRealType() == TypeTable.INTEGER) {
				output += "\tpush [offset flat:.io_format + 20]\n";
			} else if (arrDef.getRealType() == TypeTable.CHARACTER) {
				output += "\tpush [offset flat:.io_format + 24]\n";				
			}
		}
		System.out.println(output);
		return null;
	}

	@Override
	public Integer visit(Return n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(SubroutineDeclarations n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Subtract n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		output += "# Add...\n";
		if (n.getRealType() == TypeTable.FLOAT) {
			// Push lhs and rhs onto the stack and pass their address
			output += "\tpush " + register[lhs] + "\n";
			output += "\tfld  dword ptr[%esp]\n";
			output += "\tadd  %esp, 4\n";
			output += "\tpush " + register[rhs] + "\n";
			output += "\tfld  dword ptr[%esp]\n";
			output += "\tadd  %esp, 4\n";
			// Subtract top two values on the float stack
			output += "\tfsub %st(0), %st(1)\n";
			output += "\tsub %esp, 4\n";
			output += "\tfstp dword ptr[%esp]\n";
			output += "\tpop " + register[lhs] + "\n";
			// Free the float stack
			output += "\tsub  %esp, 4\n";
			output += "\tfstp dword ptr[%esp]\n";
			output += "\tadd  %esp, 4\n";
		} else {
			output += "\tsub  " + register[lhs] + ", " + register[rhs] + "\n";			
		}
		System.out.println(output);
		freeReg(rhs);
		return null;
	}

	@Override
	public Integer visit(TypeCharacter n) {
		n.getChild().accept(this);
		return null;
	}

	@Override
	public Integer visit(TypeFloat n) {
		n.getChild().accept(this);
		return null;
	}

	@Override
	public Integer visit(TypeInteger n) {
		n.getChild().accept(this);
		return null;
	}

	@Override
	public Integer visit(VariableDeclarations n) {
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
	public Integer visit(WhileStatement n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(Write n) {
		String output = "";
		if (n.getOutput() instanceof ConstantFloat) {			
			int regi = findEmptyReg(-1);
			ConstantFloat cf = (ConstantFloat)n.getOutput();
			output += "# Float Constant...\n";
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
			output += "# Integer Constant...\n";
			output += "\tpush " + ci.getInteger() + "\n";
			output += "\tpush [offset flat:.io_format + 4]\n";
			output += "\tcall printf\n";
			output += "\tadd  %esp, 8\n";
		} else if (n.getOutput() instanceof ConstantCharacter) {
			ConstantCharacter cc = (ConstantCharacter)n.getOutput();
			output += "# Character Constant...\n";
			output += "\tpush " + (int)cc.getCharacter().charAt(1) + " #" + cc.getCharacter() + "\n";
			output += "\tpush [offset flat:.io_format + 8]\n";
			output += "\tcall printf\n";
			output += "\tadd  %esp, 8\n";
		} else if (n.getOutput() instanceof ConstantString) {
			ConstantString cs = (ConstantString)n.getOutput();
			output += "# String Constant...\n";
			output += "\tpush [offset flat:_constant + " + MemoryVisitor.sortedConstMap.get(cs.getString()).getOffset() + "]\n";
			output += "\tpush [offset flat:.io_format + 12]\n";
			output += "\tcall printf\n";
			output += "\tadd  %esp, " + (cs.getString().length() - 1 + 4) + "\n";
		} else if (n.getOutput() instanceof IdRef) {
			IdRef idRef = (IdRef)n.getOutput();
			int regi = findEmptyReg(-1);
			if (n.getOutput().getRealType() == TypeTable.FLOAT) {
				output += "# Float Reference...\n";
				output += "\tmov  " + register[regi] + ", dword ptr [%ebp - " + idRef.getOffset() + "] \n";
				output += "\tpush " + register[regi] + "\n";
				output += "\tfld  dword ptr [%esp]\n";
				output += "\tadd  %esp, 4\n";
				output += "\tsub  %esp, 8\n";
				output += "\tfstp qword ptr [%esp]\n";
				output += "\tpush [offset flat:.io_format + 0]\n";
				output += "\tcall printf\n";
				output += "\tadd  %esp, 12\n";
			} else if (n.getOutput().getRealType() == TypeTable.INTEGER) {
				output += "# Integer Reference...\n";
				output += "\tmov  " + register[regi] + ", dword ptr [%ebp - " + idRef.getOffset() + "]\n";
				output += "\tpush [offset flat:.io_format + 4]\n";
				output += "\tcall printf\n";
				output += "\tadd  %esp, 8\n";
			} else if (n.getOutput().getRealType() == TypeTable.CHARACTER) {
				output += "# Character Reference...\n";
				output += "\tmov  ";
			}
			freeReg(regi);
		} else if (n.getOutput() instanceof ArrayRef) {
			if (n.getOutput().getRealType() == TypeTable.FLOAT) {
				
			} else if (n.getOutput().getRealType() == TypeTable.INTEGER) {
				
			} else if (n.getOutput().getRealType() == TypeTable.CHARACTER) {
				
			}
		} else if (n.getOutput() instanceof BinaryExpression) {
			output += "# Expression...\n";
			
			BinaryExpression binaryExpression = (BinaryExpression)n.getOutput();
			Integer regi = (Integer)binaryExpression.accept(this);
			
			if (binaryExpression.getRealType() == TypeTable.FLOAT) {
				output += "# Float Expression Reference...\n";
				output += "\tpush " + register[regi] + "\n";
				output += "\tfld  dword ptr [%esp]\n";
				output += "\tadd  %esp, 4\n";
				output += "\tsub  %esp, 8\n";
				output += "\tfstp qword ptr [%esp]\n";
				output += "\tpush [offset flat:.io_format + 0]\n";
				output += "\tcall printf\n";
				output += "\tadd  %esp, 12\n";
			} else if (binaryExpression.getRealType() == TypeTable.INTEGER) {
				
			} else if (binaryExpression.getRealType() == TypeTable.CHARACTER) {
				
			}
			
		}
		System.out.println(output);
		return null;
	}

}
