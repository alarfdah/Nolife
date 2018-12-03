package visitor;

import ast.*;
import util.TypeTable;

public class CodeVisitor implements Visitor<Object> {

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
	
	private void setLabelNumber(int labelNumber) {
		this.labelNumber = labelNumber;
	}
	
	@Override
	public Object visit(Add n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		output += "# Add...\n";
		
		if (n.getRealType() == TypeTable.FLOAT) {
			// Push lhs and rhs onto the stack and pass their address
			output += "\tpush    " + register[lhs] + "\n";
			output += "\tfld     dword ptr[%esp]\n";
			output += "\tadd     %esp, 4\n";
			output += "\tpush    " + register[rhs] + "\n";
			output += "\tfld     dword ptr[%esp]\n";
			output += "\tadd     %esp, 4\n";
			// Add top two values on the float stack
			output += "\tfadd    %st(0), %st(1)\n";
			output += "\tsub     %esp, 4\n";
			output += "\tfstp    dword ptr[%esp]\n";
			output += "\tpop     " + register[lhs] + "\n";
			// Free the float stack
			output += "\tsub     %esp, 4\n";
			output += "\tfstp    dword ptr[%esp]\n";
			output += "\tadd     %esp, 4";
		} else {
			output += "\tadd   " + register[lhs] + ", " + register[rhs] + "\n";			
		}
		
		if (n.getRealType() != n.getConvertedType()) {
			if (n.getConvertedType() == TypeTable.FLOAT) {
				output += "\n# Add Integer to Float...\n";
				output += "";
			} else if (n.getConvertedType() == TypeTable.INTEGER) {
				output += "\n# Add Float to Integer...\n";
				output += "\tpush   " + register[lhs] + "\n";
				output += "\tfld    dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 4\n";
				output += "\tfisttp dword ptr [%esp]\n";
				output += "\tpop    " + register[lhs];
			}
		}
		
		System.out.println(output);
		freeReg(rhs);
		return lhs;
	}

	@Override
	public Object visit(AND n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		int elseLabel = getLabel();
		output += "# AND...\n";
		output += "\tcmp    " + register[lhs] + ", 0\n";
		output += "\tje     .L" + elseLabel + "\n";
		output += "\tcmp    " + register[rhs] + ", 0\n";
		output += "\tje     .L" + elseLabel;
		System.out.println(output);
		freeReg(lhs);
		freeReg(rhs);
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
		
		Integer rhs = (Integer)n.getRhs().accept(this);
		
		if (n.getLhs() instanceof IdDef) {
			IdDef idDef = (IdDef)n.getLhs();
			output += "\tmov    dword ptr [%ebp - " + idDef.getOffset() + "], " + register[rhs]; 					
		} else if (n.getLhs() instanceof ArrayDef) {
			ArrayDef arrDef = (ArrayDef)n.getLhs();
		}
		System.out.println(output);
		freeReg(rhs);
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
		String output = "";
		int caseStmtEnd = getLabel();
		int clauseLabel = -1;
		
		for (ASTNode node : n.getClauseList()) {
			incrementLabel();
			clauseLabel = getLabel();
			output = ".L_Clause_" + clauseLabel + ":";
			System.out.println(output);
			node.accept(this);
			output = "\tjmp    .L_Case_End_" + caseStmtEnd;
			System.out.println(output);
		}
		return null;
	}

	@Override
	public Object visit(CaseStatement n) {
		String output = "";
		
		Integer regi = (Integer)n.getCaseExpression().accept(this);
		
		String[] clauseLabels;
		int i = 0;
		
		incrementLabel();
		int caseStmtEnd = getLabel();
		
		int clauseStart = -1;
		
		output += "# Case Statement...";
		System.out.println(output);
		output = "";
		
		if (n.getCases() != null) {
			for (ASTNode c : n.getCases().getClauseList()) {
				Clause clause = (Clause)c;
				clauseLabels = clause.getLabel().split(", ");
				incrementLabel();
				clauseStart = getLabel();
				for (i = 0; i < clauseLabels.length; i++) {
					output += "\tcmp    " + register[regi] + ", " + clauseLabels[i] + "\n";
					output += "\tje     .L_Clause_" + clauseStart + "\n";
				}
			}
			output += "\tjmp    .L_Case_End_" + caseStmtEnd + "\n";
		}
		System.out.println(output);
		setLabelNumber(caseStmtEnd);
		n.getCases().accept(this);
		output = ".L_Case_End_" + caseStmtEnd + ":";
		System.out.println(output);
		freeReg(regi);
		return null;
	}

	@Override
	public Object visit(Clause n) {
		n.getStatement().accept(this);
		return n.getLabel();
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
		int regi = findEmptyReg(-1);
		String output = "";
		output += "# Constant Character...\n";
		output += "\tmov    " + register[regi] + ", " + n.getCharacter();
		System.out.println(output);
		return regi;
	}

	@Override
	public Object visit(ConstantFloat n) {
		int regi = findEmptyReg(-1);
		String output = "";
		output += "# Constant Float...\n";
		output += "\tmov    " + register[regi] + ", [offset flat:_constant + " + n.getOffset() + "]\n";
		output += "\tmov    " + register[regi] + ", dword ptr[" + register[regi] + "]";
		if (n.getRealType() != n.getConvertedType()) {
			output += "\n# Float to Integer...\n";
			output += "\tpush    " + register[regi] + "\n";
			output += "\tfld     dword ptr [%esp]";
			output += "\tadd     %esp, 4\n";
			output += "\tsub     %esp, 4\n";
			output += "\tfisttp  dword ptr [%esp]\n";
			output += "\tpop     " + register[regi];
		}
		System.out.println(output);
		return regi;
	}

	@Override
	public Object visit(ConstantInteger n) {
		int regi = findEmptyReg(-1);
		String output = "";
		output += "# Constant Integer...\n";
		output += "\tmov    " + register[regi] + ", " + n.getInteger();
		if (n.getRealType() != n.getConvertedType()) {
			output += "\n# Integer to Float...\n";
			output += "\tpush    " + register[regi] + "\n";
			output += "\tfild    dword ptr [%esp]\n";
			output += "\tadd     %esp, 4\n";
			output += "\tsub     %esp, 4\n";
			output += "\tfstp    dword ptr [%esp]\n";
			output += "\tpop     " + register[regi];
		}
		System.out.println(output);
		return regi;
	}

	@Override
	public Object visit(ConstantString n) {
		int regi = findEmptyReg(-1);
		String output = "";
		output += "\tmov    " + register[regi] + ", [offset flat:_constant + " + n.getOffset() + "]\n";
		output += "\tmov    " + register[regi] + ", dword ptr[" + register[regi] + "]";
		System.out.println(output);
		return regi;
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
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		incrementLabel();
		int labelEqualEqual = getLabel();
		incrementLabel();
		int labelNotEqual = getLabel();
		incrementLabel();
		int labelDone = getLabel();
		
		output += "# Equal Equal...\n";
		output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
		
		output += "\tje    .L" + labelEqualEqual + "\n";
		output += "\tjmp    .L" + labelNotEqual + "\n";
		
		output += ".L" + labelEqualEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 1\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelNotEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 0\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelDone + ":";
		System.out.println(output);
		n.setLabel("je");
		freeReg(rhs);
		return lhs;
	}

	@Override
	public Object visit(Function n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(GreaterThan n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		incrementLabel();
		int labelGreaterThan = getLabel();
		incrementLabel();
		int labelLessThanEqual = getLabel();
		incrementLabel();
		int labelDone = getLabel();
		
		output += "# Greater Than...\n";
		output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
		
		output += "\tjg     .L" + labelGreaterThan + "\n";
		output += "\tjmp    .L" + labelLessThanEqual + "\n";
		
		output += ".L" + labelGreaterThan + ":\n";
		output += "\tmov    " + register[lhs] + ", 1\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelLessThanEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 0\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelDone + ":";
		System.out.println(output);
		freeReg(rhs);
		return lhs;
	}

	@Override
	public Object visit(GreaterThanEqual n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		incrementLabel();
		int labelGreaterThanEqual = getLabel();
		incrementLabel();
		int labelLessThan = getLabel();
		incrementLabel();
		int labelDone = getLabel();
		
		output += "# Greater Than Equal...\n";
		output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
		
		output += "\tjge    .L" + labelGreaterThanEqual + "\n";
		output += "\tjmp    .L" + labelLessThan + "\n";
		
		output += ".L" + labelGreaterThanEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 1\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelLessThan + ":\n";
		output += "\tmov    " + register[lhs] + ", 0\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelDone + ":";
		System.out.println(output);
		freeReg(rhs);
		return lhs;
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
		int regi = findEmptyReg(-1);
		String output = "";
		output += "# IdRef...\n";
		output += "\tmov    " + register[regi] + ", dword ptr [%ebp - " + n.getOffset() + "]";			
		if (n.getRealType() != n.getConvertedType()) {
			if (n.getConvertedType() == TypeTable.FLOAT) {
				output += "\n# Integer to Float...\n";
				output += "\tpush   " + register[regi] + "\n";
				output += "\tfild   dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 4\n";
				output += "\tfstp   dword ptr [%esp]\n";
				output += "\tpop    " + register[regi];
			} else if (n.getConvertedType() == TypeTable.INTEGER) {
				output += "\n# Float to Integer...\n";
				output += "\tpush   " + register[regi] + "\n";
				output += "\tfld    dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 4\n";
				output += "\tfisttp dword ptr [%esp]\n";
				output += "\tpop    " + register[regi];
			}
		}
		System.out.println(output);
		return regi;
	}

	@Override
	public Object visit(IfStatement n) {
		String output = "";
		int labelFalse = -1;
		int labelDone = -1;
		output += "# If Statement...";
		System.out.println(output);
		output = "";
		if (n.getIfExpression() instanceof ConstantInteger
				|| n.getIfExpression() instanceof ConstantCharacter) {
			Integer regi = (Integer)n.getIfExpression().accept(this);
			incrementLabel();
			labelFalse = getLabel();
			incrementLabel();
			labelDone = getLabel();
			
			output += "\tcmp    " + register[regi] + ", 0\n";
			output += "\tje     .L" + labelFalse;
			System.out.println(output);
			
			// If true then
			n.getThenStatement().accept(this);
			// Skip else
			output = "\tjmp    .L" + labelDone + "\n";
			// Skip to else
			output += ".L" + labelFalse + ":";
			System.out.println(output);
			freeReg(regi);
		} else if (n.getIfExpression() instanceof ConstantFloat) {
			Integer regi = (Integer)n.getIfExpression().accept(this);
			
		} else if (n.getIfExpression() instanceof IdRef) {
			Integer regi = (Integer)n.getIfExpression().accept(this);
			incrementLabel();
			labelFalse = getLabel();
			incrementLabel();
			labelDone = getLabel();
			
			output += "\tcmp    " + register[regi] + ", 0\n";
			output += "\tje     .L" + labelFalse;
			System.out.println(output);
			
			// If true then
			n.getThenStatement().accept(this);
			// Skip else
			output = "\tjmp    .L" + labelDone + "\n";
			// Skip to else
			output += ".L" + labelFalse + ":";
			System.out.println(output);
			
			freeReg(regi);
		} else if (n.getIfExpression() instanceof OR) { // OR can bypass the second condition if the first is met
			incrementLabel();
			labelFalse = getLabel();
			n.getIfExpression().accept(this);
			
			output = ".L" + labelFalse + ":";
			System.out.println(output);
			// If true then
			n.getThenStatement().accept(this);
			labelFalse = getLabel();
			incrementLabel();
			labelDone = getLabel();
			
			// Skip else
			output = "\tjmp    .L" + labelDone + "\n";
			// Skip to else
			output += ".L" + labelFalse + ":";
			System.out.println(output);
		} else if (n.getIfExpression() instanceof BinaryExpression) {
			BinaryExpression be = (BinaryExpression)n.getIfExpression();
			Integer regi = (Integer)be.accept(this);
			
			incrementLabel();
			labelFalse = getLabel();
			output += "\tcmp    " + register[regi] + ", 0\n";
			output += "\tje     .L" + labelFalse + "\n";
			System.out.println(output);
			
			n.getThenStatement().accept(this);
			incrementLabel();
			labelDone = getLabel();
			output = "\tjmp    .L" + labelDone + "\n";
			
			output += ".L" + labelFalse + ":";
			System.out.println(output);
			
			freeReg(regi);
		} else if (n.getIfExpression() instanceof NOT) {
			Integer regi = (Integer)n.getIfExpression().accept(this);
			incrementLabel();
			labelFalse = getLabel();
			incrementLabel();
			labelDone = getLabel();
			output += "\tcmp    " + register[regi] + ", 0\n";
			output += "\tje     .L" + labelFalse;
			System.out.println(output);
			
			n.getThenStatement().accept(this);
			output = "\tjmp    .L" + labelDone + "\n";
			output += ".L" + labelFalse + ":";
			System.out.println(output);
		}
		
		if (n.getElseStatement() != null) {
			n.getElseStatement().accept(this);
			
		}
		output = ".L" + labelDone + ":";
		System.out.println(output);
		return null;
	}

	@Override
	public Object visit(LessThan n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		incrementLabel();
		int labelLessThan = getLabel();
		incrementLabel();
		int labelGreaterThanEqual = getLabel();
		incrementLabel();
		int labelDone = getLabel();
		
		output += "# Less Than...\n";
		output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
		
		output += "\tjl     .L" + labelLessThan + "\n";
		output += "\tjmp    .L" + labelGreaterThanEqual + "\n";
		
		output += ".L" + labelLessThan + ":\n";
		output += "\tmov    " + register[lhs] + ", 1\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelGreaterThanEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 0\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelDone + ":";
		System.out.println(output);
		freeReg(rhs);
		return lhs;
	}

	@Override
	public Object visit(LessThanEqual n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		incrementLabel();
		int labelLessThanEqual = getLabel();
		incrementLabel();
		int labelGreaterThan = getLabel();
		incrementLabel();
		int labelDone = getLabel();
		
		output += "# Less Than Equal...\n";
		output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
		
		output += "\tjle    .L" + labelLessThanEqual + "\n";
		output += "\tjmp    .L" + labelGreaterThan + "\n";
		
		output += ".L" + labelLessThanEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 1\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelGreaterThan + ":\n";
		output += "\tmov    " + register[lhs] + ", 0\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelDone + ":";
		System.out.println(output);
		freeReg(rhs);
		return lhs;
	}

	@Override
	public Object visit(Modulo n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		int eax = findEmptyReg(0);
		output += "# Modulo...\n";
		output += "\tmov    " + register[eax] + ", " + lhs;
		output += "\tcdq\n";
		output += "\tidiv   " + register[rhs] + "\n";
		output += "\tmov    " + register[lhs] + ", %edx\n";
		System.out.println(output);
		freeReg(rhs);
		freeReg(eax);
		return lhs;
	}

	@Override
	public Object visit(Multiply n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		output += "# Multiply...\n";
		if (n.getRealType() == TypeTable.FLOAT) {
			// Push lhs and rhs onto the stack and pass their address
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr[%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr[%esp]\n";
			output += "\tadd    %esp, 4\n";
			// Multiply top two values on the float stack
			output += "\tfmul   %st(0), %st(1)\n";
			output += "\tsub    %esp, 4\n";
			output += "\tfstp   dword ptr[%esp]\n";
			output += "\tpop    " + register[lhs] + "\n";
			// Free the float stack
			output += "\tsub    %esp, 4\n";
			output += "\tfstp   dword ptr[%esp]\n";
			output += "\tadd    %esp, 4\n";
		} else {
			output += "\timul  " + register[lhs] + ", " + register[rhs] + "\n";			
		}
		
		if (n.getRealType() != n.getConvertedType()) {
			if (n.getConvertedType() == TypeTable.FLOAT) {
				output += "\n# Multiply Integer to Float...\n";
				output += "";
			} else if (n.getConvertedType() == TypeTable.INTEGER) {
				output += "\n# Multiply Float to Integer...\n";
				output += "\tpush   " + register[lhs] + "\n";
				output += "\tfld    dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 4\n";
				output += "\tfisttp dword ptr [%esp]\n";
				output += "\tpop    " + register[lhs];
			}
		}
		
		System.out.println(output);
		freeReg(rhs);
		return lhs;
	}

	@Override
	public Object visit(NOT n) {
		int regi = -1;
		String output = "";
		output = "# NOT...\n";
		if (n.getNotChild() instanceof ConstantInteger) {
			Integer integer = Integer.parseInt(n.getNotChild().getLabel());			
			regi = findEmptyReg(-1);
			if (integer != 0) {
				integer = 0;
			} else {
				integer = 1;
			}
			output += "\tmov    " + register[regi] + ", " + integer;
		} else if (n.getNotChild() instanceof IdRef) {
			Integer regj = (Integer)n.getNotChild().accept(this);
			incrementLabel();
			int isZero = getLabel();
			incrementLabel();
			int notZero = getLabel();
			output += "\tcmp    " + register[regj] + ", 0\n";
			output += "\tje     .L" + isZero + "\n";
			output += "\tjmp    .L" + notZero + "\n";
			
			output += ".L" + isZero + ":\n";
			output += "\tmov    " + register[regj] + ", 1\n";
			
			output += ".L" + notZero + ":\n";
			output += "\tmov    " + register[regj] + ", 0\n";
			
			regi = regj;
		}
		System.out.println(output);
		return regi;
	}

	@Override
	public Object visit(NotEqual n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		incrementLabel();
		int labelNotEqual = getLabel();
		incrementLabel();
		int labelEqualEqual = getLabel();
		incrementLabel();
		int labelDone = getLabel();
		
		output += "# Equal Equal...\n";
		output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
		
		output += "\tjne    .L" + labelNotEqual + "\n";
		output += "\tjmp    .L" + labelEqualEqual + "\n";
		
		output += ".L" + labelNotEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 1\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelEqualEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 0\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelDone + ":";
		System.out.println(output);
		freeReg(rhs);
		return lhs;
	}

	@Override
	public Object visit(OR n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		incrementLabel();
		int labelNotEqual = getLabel();
		incrementLabel();
		int labelEqualEqual = getLabel();
		incrementLabel();
		int labelDone = getLabel();
		
		output += "# Not Equal...\n";
		output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
		
		output += "\tjne    .L" + labelNotEqual + "\n";
		output += "\tjmp    .L" + labelEqualEqual + "\n";
		
		output += ".L" + labelNotEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 1\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelEqualEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 0\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelDone + ":";
		System.out.println(output);
		n.setLabel("je");
		freeReg(rhs);
		return lhs;
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
		output += ".io_format_in:\n";
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
		output += "\tpush   %ebp\n";
		output += "\tmov    %ebp, %esp";
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
		String output = "";
		output += "# Read...\n";
		IdDef idDef = (IdDef)n.getInput();
		int regi = findEmptyReg(-1);
		if (n.getInput() instanceof IdDef) {
			output += "\tmov    " + register[regi] + ", %ebp\n";
			output += "\tsub    " + register[regi] + ", " + idDef.getOffset() + "\n";
			output += "\tpush   " + register[regi] + "\n";
			if (idDef.getRealType() == TypeTable.FLOAT) {
				output += "\tpush   [offset flat:.io_format_in + 0]\n";
			} else if (idDef.getRealType() == TypeTable.INTEGER) {
				output += "\tpush   [offset flat:.io_format_in + 3]\n";
			} else if (idDef.getRealType() == TypeTable.CHARACTER) {
				output += "\tpush   [offset flat:.io_format_in + 6]\n";				
			}
			output += "\tcall   scanf\n";
			output += "\tadd    %esp, 8\n";
		} else if (n.getInput() instanceof ArrayDef) {
			ArrayDef arrDef = (ArrayDef)n.getInput();
			output += "\tmov  \n";
			if (arrDef.getRealType() == TypeTable.FLOAT) {
				output += "\tpush   [offset flat:.io_format_in + 0]\n";
			} else if (arrDef.getRealType() == TypeTable.INTEGER) {
				output += "\tpush   [offset flat:.io_format_in + 3]\n";
			} else if (arrDef.getRealType() == TypeTable.CHARACTER) {
				output += "\tpush   [offset flat:.io_format_in + 6]\n";				
			}
		}
		System.out.println(output);
		freeReg(regi);
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
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		output += "# Add...\n";
		if (n.getRealType() == TypeTable.FLOAT) {
			// Push lhs and rhs onto the stack and pass their address
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr[%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr[%esp]\n";
			output += "\tadd    %esp, 4\n";
			// Subtract top two values on the float stack
			output += "\tfsub   %st(0), %st(1)\n";
			output += "\tsub    %esp, 4\n";
			output += "\tfstp   dword ptr[%esp]\n";
			output += "\tpop    " + register[lhs] + "\n";
			// Free the float stack
			output += "\tsub    %esp, 4\n";
			output += "\tfstp   dword ptr[%esp]\n";
			output += "\tadd    %esp, 4\n";
		} else {
			output += "\tsub    " + register[lhs] + ", " + register[rhs] + "\n";			
		}
		
		if (n.getRealType() != n.getConvertedType()) {
			if (n.getConvertedType() == TypeTable.FLOAT) {
				output += "\n# Subtract Integer to Float...\n";
				output += "";
			} else if (n.getConvertedType() == TypeTable.INTEGER) {
				output += "\n# Subtract Float to Integer...\n";
				output += "\tpush   " + register[lhs] + "\n";
				output += "\tfld    dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 4\n";
				output += "\tfisttp dword ptr [%esp]\n";
				output += "\tpop    " + register[lhs];
			}
		}
		
		
		System.out.println(output);
		freeReg(rhs);
		return lhs;
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
		output = "\tsub    %esp, " + (subEsp * 4) + "\n";
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
		int regi = findEmptyReg(-1);
		if (n.getOutput() instanceof ConstantFloat) {			
			ConstantFloat cf = (ConstantFloat)n.getOutput();
			output += "# Print Float Constant...\n";
			output += "\tsub    %esp, 8\n";
			output += "\tmov    " + register[regi] + ", [offset flat:_constant + " + MemoryVisitor.sortedConstMap.get(cf.getFloat()).getOffset() + "]\n";
			output += "\tfld    dword ptr [" + register[regi] + "]\n";
			output += "\tfstp   qword ptr [%esp]\n";
			output += "\tpush   [offset flat:.io_format + 0]\n";
			output += "\tcall   printf\n";
			output += "\tadd    %esp, 12";
		} else if (n.getOutput() instanceof ConstantInteger) {
			ConstantInteger ci = (ConstantInteger)n.getOutput();
			output += "# Print Integer Constant...\n";
			output += "\tmov    " + register[regi] + ", " + ci.getInteger() + "\n";
			output += "\tpush   " + register[regi] + "\n";
			output += "\tpush   [offset flat:.io_format + 4]\n";
			output += "\tcall   printf\n";
			output += "\tadd    %esp, 8";
		} else if (n.getOutput() instanceof ConstantCharacter) {
			ConstantCharacter cc = (ConstantCharacter)n.getOutput();
			output += "# Print Character Constant...\n";
			output += "\tmov    " + register[regi] + ", " + (int)cc.getCharacter().charAt(1) + " #" + cc.getCharacter() + "\n";
			output += "\tpush   " + register[regi] + "\n";
			output += "\tpush   [offset flat:.io_format + 8]\n";
			output += "\tcall   printf\n";
			output += "\tadd    %esp, 8";
		} else if (n.getOutput() instanceof ConstantString) {
			ConstantString cs = (ConstantString)n.getOutput();
			output += "# Print String Constant...\n";
			output += "\tpush   [offset flat:_constant + " + MemoryVisitor.sortedConstMap.get(cs.getString()).getOffset() + "]\n";
			output += "\tpush   [offset flat:.io_format + 12]\n";
			output += "\tcall   printf\n";
			output += "\tadd    %esp, 8";
		} else if (n.getOutput() instanceof NOT) {
			NOT not = (NOT)n.getOutput();
			Integer regj = (Integer)not.accept(this);
			output += "# Print NOT Ouptut...\n";
			output += "\tpush   " + register[regj] + "\n";
			output += "\tpush   [offset flat:.io_format + 4]\n";
			output += "\tcall   printf\n";
			output += "\tadd    %esp, 4";
			freeReg(regj);
		} else if (n.getOutput() instanceof IdRef) {
			IdRef idRef = (IdRef)n.getOutput();
			if (n.getOutput().getRealType() == TypeTable.FLOAT) {
				output += "# Print Float Reference...\n";
				output += "\tmov    " + register[regi] + ", dword ptr [%ebp - " + idRef.getOffset() + "] \n";
				output += "\tpush   " + register[regi] + "\n";
				output += "\tfld    dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 8\n";
				output += "\tfstp   qword ptr [%esp]\n";
				output += "\tpush   [offset flat:.io_format + 0]\n";
				output += "\tcall   printf\n";
				output += "\tadd    %esp, 12";
			} else if (n.getOutput().getRealType() == TypeTable.INTEGER) {
				output += "# Print Integer Reference...\n";
				output += "\tmov    " + register[regi] + ", dword ptr [%ebp - " + idRef.getOffset() + "]\n";
				output += "\tpush   " + register[regi] + "\n";
				output += "\tpush   [offset flat:.io_format + 4]\n";
				output += "\tcall   printf\n";
				output += "\tadd    %esp, 8";
			} else if (n.getOutput().getRealType() == TypeTable.CHARACTER) {
				output += "# Print Character Reference...\n";
				output += "\tmov    " + register[regi] + ", dword ptr [%ebp - " + idRef.getOffset() + "]\n";
				output += "\tpush   " + register[regi] + "\n";
				output += "\tpush   [offset flat:.io_format + 8]\n";
				output += "\tcall   printf\n";
				output += "\tadd    %esp, 8";
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
				output += "\tpush   " + register[regj] + "\n";
				output += "\tfld    dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 8\n";
				output += "\tfstp   qword ptr [%esp]\n";
				output += "\tpush   [offset flat:.io_format + 0]\n";
				output += "\tcall   printf\n";
				output += "\tadd    %esp, 12";
			} else if (binaryExpression.getRealType() == TypeTable.INTEGER) {
				output += "# Print Integer Expression Reference...\n";
				output += "\tpush   " + register[regj] + "\n";
				output += "\tpush   [offset flat:.io_format + 4]\n";
				output += "\tcall   printf\n";
				output += "\tadd    %esp, 8";
			} else if (binaryExpression.getRealType() == TypeTable.CHARACTER) {
				output += "# Print Character Expression Reference...\n";
				output += "\tpush   " + register[regj] + "\n";
				output += "\tpush   [offset flat:.io_format + 8]\n";
				output += "\tcall   printf\n";
				output += "\tadd    %esp, 8";
			}
			freeReg(regj);
			
		}
		freeReg(regi);
		System.out.println(output);
		return null;
	}

}
