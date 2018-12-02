package visitor;

import ast.*;
import util.TypeTable;

public class CodeVisitor implements Visitor<Integer> {

								// 0,		1,		2,		3,		4,		5
	private String[] register = {"%eax", "%ebx", "%ecx", "%edi", "%esi", "%edx"};
	private boolean[] regUsed = { false,  false,  false,  false,  false,  false};
	private int labelNumber = 0;
	
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
	
	private int getLabel() {
		return labelNumber;
	}
	
	private void incrementLabel() {
		labelNumber++;
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
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		int elseLabel = getLabel();
		output += "# AND...\n";
		output += "\tcmp  " + register[lhs] + ", 0\n";
		output += "\tje  .L" + elseLabel + "\n";
		output += "\tcmp  " + register[rhs] + ", 0\n";
		output += "\tje  .L" + elseLabel;
		System.out.println(output);
		freeReg(lhs);
		freeReg(rhs);
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
		output += "# Assignment...\n";
		
		Integer rhs = (Integer)n.getRhs().accept(this);
		
		if (n.getLhs() instanceof IdDef) {
			IdDef idDef = (IdDef)n.getLhs();
			output += "\tmov  dword ptr [%ebp - " + idDef.getOffset() + "], " + register[rhs]; 
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
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		int label = getLabel();
		output += "# Equal Equal...\n";
		output += "\tcmp  " + register[lhs] + ", " + register[rhs] + "\n";
		output += "\tjne  .L" + label + "\n";
		System.out.println(output);
		freeReg(lhs);
		freeReg(rhs);
		return null;
	}

	@Override
	public Integer visit(Function n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visit(GreaterThan n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		int label = getLabel();
		output += "# Greater Than...\n";
		output += "\tcmp  " + register[lhs] + ", " + register[rhs] + "\n";
		output += "\tjle  .L" + label + "\n";
		System.out.println(output);
		freeReg(lhs);
		freeReg(rhs);
		return null;
	}

	@Override
	public Integer visit(GreaterThanEqual n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		int label = getLabel();
		output += "# Greater Than Equal...\n";
		output += "\tcmp  " + register[lhs] + ", " + register[rhs] + "\n";
		output += "\tjl  .L" + label + "\n";
		System.out.println(output);
		freeReg(lhs);
		freeReg(rhs);
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
		output += "# IdRef...\n";
		output += "\tmov  " + register[regi] + ", dword ptr [%ebp - " + n.getOffset() + "]";
		System.out.println(output);
		return regi;
	}

	@Override
	public Integer visit(IfStatement n) {
		String output = "";
		int elseLabel = -1;
		int ifLabel = -1;
		output += "# If Statement...";
		System.out.println(output);
		output = "";
		if (n.getIfExpression() instanceof ConstantInteger) {
			Integer regi = (Integer)n.getIfExpression().accept(this);
			incrementLabel();
			elseLabel = getLabel();
			incrementLabel();
			ifLabel = getLabel();
			
			output += "\tcmp  " + register[regi] + ", 0\n";
			output += "\tje   .L" + elseLabel;
			System.out.println(output);
			
			// If true then
			n.getThenStatement().accept(this);
			// Skip else
			output = "\tjmp  .L" + ifLabel + "\n";
			// Skip to else
			output += ".L" + elseLabel + ":";
			System.out.println(output);
						
			freeReg(regi);
		} else if (n.getIfExpression() instanceof IdRef) {
			Integer regi = (Integer)n.getIfExpression().accept(this);
			incrementLabel();
			elseLabel = getLabel();
			incrementLabel();
			ifLabel = getLabel();
			
			output += "\tcmp  " + register[regi] + ", 0\n";
			output += "\tje   .L" + elseLabel;
			System.out.println(output);
			
			// If true then
			n.getThenStatement().accept(this);
			// Skip else
			output = "\tjmp  .L" + ifLabel + "\n";
			// Skip to else
			output += ".L" + elseLabel + ":";
			System.out.println(output);
			
			
			freeReg(regi);
		} else if (n.getIfExpression() instanceof OR) { // OR can bypass the second condition if the first is met
			incrementLabel();
			elseLabel = getLabel();
			n.getIfExpression().accept(this);
			
			output = ".L" + elseLabel + ":";
			System.out.println(output);
			// If true then
			n.getThenStatement().accept(this);
			elseLabel = getLabel();
			incrementLabel();
			ifLabel = getLabel();
			
			// Skip else
			output = "\tjmp  .L" + ifLabel + "\n";
			// Skip to else
			output += ".L" + elseLabel + ":";
			System.out.println(output);
		} else if (n.getIfExpression() instanceof BinaryExpression) {
			incrementLabel();
			elseLabel = getLabel();
			n.getIfExpression().accept(this);
			
			// If true then
			n.getThenStatement().accept(this);
			incrementLabel();
			ifLabel = getLabel();
			
			// Skip else
			output += "\tjmp  .L" + ifLabel + "\n";
			// Skip to else
			output += ".L" + elseLabel + ":";
			System.out.println(output);
		} else if (n.getIfExpression() instanceof NOT) {
			Integer regi = (Integer)n.getIfExpression().accept(this);
			incrementLabel();
			elseLabel = getLabel();
			incrementLabel();
			ifLabel = getLabel();
			output += "\tcmp  " + register[regi] + ", 0\n";
			output += "\tje   .L" + elseLabel;
			System.out.println(output);
			
			n.getThenStatement().accept(this);
			output = "\tjmp  .L" + ifLabel + "\n";
			output += ".L" + elseLabel + ":";
			System.out.println(output);
		}
		
		if (n.getElseStatement() != null) {
			n.getElseStatement().accept(this);
			
		}
		output = ".L" + ifLabel + ":";
		System.out.println(output);
		return null;
	}

	@Override
	public Integer visit(LessThan n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		int label = getLabel();
		output += "# Less Than...\n";
		output += "\tcmp  " + register[lhs] + ", " + register[rhs] + "\n";
		output += "\tjge  .L" + label + "\n";
		System.out.println(output);
		freeReg(lhs);
		freeReg(rhs);
		return null;
	}

	@Override
	public Integer visit(LessThanEqual n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		int label = getLabel();
		output += "# Less Than Equal...\n";
		output += "\tcmp  " + register[lhs] + ", " + register[rhs] + "\n";
		output += "\tjg  .L" + label + "\n";
		System.out.println(output);
		freeReg(lhs);
		freeReg(rhs);
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
			output += "\tsub  %esp, 4\n";
			output += "\tfstp dword ptr[%esp]\n";
			output += "\tpop  " + register[lhs] + "\n";
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
		try {
			Integer integer = Integer.parseInt(n.getNotChild().getLabel());			
			String output = "";
			int regi = findEmptyReg(-1);
			if (integer != 0) {
				integer = 0;
			} else {
				integer = 1;
			}
			output = "# NOT...\n";
			output += "\tmov  " + register[regi] + ", " + integer;
			System.out.println(output);
			return regi;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	@Override
	public Integer visit(NotEqual n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		int label = getLabel();
		output += "# Not Equal...\n";
		output += "\tcmp  " + register[lhs] + ", " + register[rhs] + "\n";
		output += "\tje  .L" + label + "\n";
		System.out.println(output);
		freeReg(lhs);
		freeReg(rhs);
		return null;
	}

	@Override
	public Integer visit(OR n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		int ifLabel = getLabel();
		output += "# OR...\n";
		output += "\tcmp  " + register[lhs] + ", 0\n";
		output += "\tjne  .L" + ifLabel + "\n";
		incrementLabel();
		int elseLabel = getLabel();
		output += "\tcmp  " + register[rhs] + ", 0\n";
		output += "\tje   .L" + elseLabel;
		System.out.println(output);
		freeReg(lhs);
		freeReg(rhs);
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
			output += "\tsub  %esp, 4\n";
			output += "\tfstp dword ptr[%esp]\n";
			output += "\tpop  " + register[lhs] + "\n";
			// Free the float stack
			output += "\tsub  %esp, 4\n";
			output += "\tfstp dword ptr[%esp]\n";
			output += "\tadd  %esp, 4\n";
		} else {
			output += "\tsub  " + register[lhs] + ", " + register[rhs] + "\n";			
		}
		System.out.println(output);
		freeReg(rhs);
		return lhs;
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
		int regi = findEmptyReg(-1);
		if (n.getOutput() instanceof ConstantFloat) {			
			ConstantFloat cf = (ConstantFloat)n.getOutput();
			output += "# Float Constant...\n";
			output += "\tsub  %esp, 8\n";
			output += "\tmov  " + register[regi] + ", [offset flat:_constant + " + MemoryVisitor.sortedConstMap.get(cf.getFloat()).getOffset() + "]\n";
			output += "\tfld  dword ptr [" + register[regi] + "]\n";
			output += "\tfstp qword ptr [%esp]\n";
			output += "\tpush [offset flat:.io_format + 0]\n";
			output += "\tcall printf\n";
			output += "\tadd  %esp, 12";
		} else if (n.getOutput() instanceof ConstantInteger) {
			ConstantInteger ci = (ConstantInteger)n.getOutput();
			output += "# Integer Constant...\n";
			output += "\tmov  " + register[regi] + ", " + ci.getInteger() + "\n";
			output += "\tpush " + register[regi] + "\n";
			output += "\tpush [offset flat:.io_format + 4]\n";
			output += "\tcall printf\n";
			output += "\tadd  %esp, 8";
		} else if (n.getOutput() instanceof ConstantCharacter) {
			ConstantCharacter cc = (ConstantCharacter)n.getOutput();
			output += "# Character Constant...\n";
			output += "\tmov  " + register[regi] + ", " + (int)cc.getCharacter().charAt(1) + " #" + cc.getCharacter() + "\n";
			output += "\tpush " + register[regi] + "\n";
			output += "\tpush [offset flat:.io_format + 8]\n";
			output += "\tcall printf\n";
			output += "\tadd  %esp, 8";
		} else if (n.getOutput() instanceof ConstantString) {
			ConstantString cs = (ConstantString)n.getOutput();
			output += "# String Constant...\n";
			output += "\tpush [offset flat:_constant + " + MemoryVisitor.sortedConstMap.get(cs.getString()).getOffset() + "]\n";
			output += "\tpush [offset flat:.io_format + 12]\n";
			output += "\tcall printf\n";
			output += "\tadd  %esp, " + (cs.getString().length() - 1 + 4);
		} else if (n.getOutput() instanceof NOT) {
			NOT not = (NOT)n.getOutput();
			Integer regj = (Integer)not.accept(this);
			output += "# NOT Ouptut...\n";
			output += "\tpush " + register[regj] + "\n";
			output += "\tpush [offset flat:.io_format + 4]\n";
			output += "\tcall printf\n";
			output += "\tadd %esp, 4";
			freeReg(regj);
		} else if (n.getOutput() instanceof IdRef) {
			IdRef idRef = (IdRef)n.getOutput();
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
				output += "\tadd  %esp, 12";
			} else if (n.getOutput().getRealType() == TypeTable.INTEGER) {
				output += "# Integer Reference...\n";
				output += "\tmov  " + register[regi] + ", dword ptr [%ebp - " + idRef.getOffset() + "]\n";
				output += "\tpush " + register[regi] + "\n";
				output += "\tpush [offset flat:.io_format + 4]\n";
				output += "\tcall printf\n";
				output += "\tadd  %esp, 8";
			} else if (n.getOutput().getRealType() == TypeTable.CHARACTER) {
				output += "# Character Reference...\n";
				output += "\tmov  " + register[regi] + ", dword ptr [%ebp - " + idRef.getOffset() + "]\n";
				output += "\tpush " + register[regi] + "\n";
				output += "\tpush [offset flat:.io_format + 8]\n";
				output += "\tcall printf\n";
				output += "\tadd  %esp, 8";
			}
			// TODO -------------
		} else if (n.getOutput() instanceof ArrayRef) {
			if (n.getOutput().getRealType() == TypeTable.FLOAT) {
				
			} else if (n.getOutput().getRealType() == TypeTable.INTEGER) {
				
			} else if (n.getOutput().getRealType() == TypeTable.CHARACTER) {
				
			}
		} else if (n.getOutput() instanceof BinaryExpression) {
			output += "# Expression...\n";
			
			BinaryExpression binaryExpression = (BinaryExpression)n.getOutput();
			Integer regj = (Integer)binaryExpression.accept(this);
			
			if (binaryExpression.getRealType() == TypeTable.FLOAT) {
				output += "# Float Expression Reference...\n";
				output += "\tpush " + register[regj] + "\n";
				output += "\tfld  dword ptr [%esp]\n";
				output += "\tadd  %esp, 4\n";
				output += "\tsub  %esp, 8\n";
				output += "\tfstp qword ptr [%esp]\n";
				output += "\tpush [offset flat:.io_format + 0]\n";
				output += "\tcall printf\n";
				output += "\tadd  %esp, 12";
			} else if (binaryExpression.getRealType() == TypeTable.INTEGER) {
				output += "# Integer Expression Reference...\n";
				output += "\tpush " + register[regj] + "\n";
				output += "\tpush [offset flat:.io_format + 4]\n";
				output += "\tcall printf\n";
				output += "\tadd  %esp, 8";
			} else if (binaryExpression.getRealType() == TypeTable.CHARACTER) {
				output += "# Character Expression Reference...\n";
				output += "\tpush " + register[regj] + "\n";
				output += "\tpush [offset flat:.io_format + 8]\n";
				output += "\tcall printf\n";
				output += "\tadd  %esp, 8";
			}
			freeReg(regj);
			
		}
		freeReg(regi);
		System.out.println(output);
		return null;
	}

}
