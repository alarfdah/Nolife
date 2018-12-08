package visitor;

import java.util.ArrayList;

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
		
		// Dereference
		if (n.getLeftOperand() instanceof IdRef || n.getLeftOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[lhs] + ", dword ptr [" + register[lhs] + "]\n";
		}
		if (n.getRightOperand() instanceof IdRef || n.getRightOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[rhs] + ", dword ptr [" + register[rhs] + "]\n";
		}
		if (n.getRealType() == TypeTable.FLOAT) {
			// Push lhs and rhs onto the stack and pass their address
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr[%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr[%esp]\n";
			output += "\tadd    %esp, 4\n";
			// Add top two values on the float stack
			output += "\tfadd   %st(0), %st(1)\n";
			output += "\tsub    %esp, 4\n";
			output += "\tfstp   dword ptr[%esp]\n";
			output += "\tpop    " + register[lhs] + "\n";
			// Free the float stack
			output += "\tsub    %esp, 4\n";
			output += "\tfstp   dword ptr[%esp]\n";
			output += "\tadd    %esp, 4";
		} else {
			output += "\tadd    " + register[lhs] + ", " + register[rhs] + "\n";			
		}
		
		if (n.getRealType() != n.getConvertedType()) {
			if (n.getRealType() == TypeTable.INTEGER && n.getConvertedType() == TypeTable.FLOAT) {
				output += "\n# Add Integer to Float...\n";
				output += "\tpush    " + register[lhs] + "\n";
				output += "\tfild    dword ptr [%esp]\n";
				output += "\tadd     %esp, 4\n";
				output += "\tsub     %esp, 4\n";
				output += "\tfstp    dword ptr [%esp]\n";
				output += "\tpop     " + register[lhs] + "\n";
			} else if (n.getRealType() == TypeTable.FLOAT && n.getConvertedType() == TypeTable.INTEGER) {
				output += "\n# Add Float to Integer...\n";
				output += "\tpush   " + register[lhs] + "\n";
				output += "\tfld    dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 4\n";
				output += "\tfisttp dword ptr [%esp]\n";
				output += "\tpop    " + register[lhs] + "\n";
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
		incrementLabel();
		int labelLeftOp = getLabel();
		incrementLabel();
		int labelRightOp = getLabel();
		incrementLabel();
		int labelDone = getLabel();
		
		output += "# AND...\n";
		// Dereference
		if (n.getLeftOperand() instanceof IdRef || n.getLeftOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[lhs] + ", dword ptr [" + register[lhs] + "]\n";
		}
		if (n.getRightOperand() instanceof IdRef || n.getRightOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[rhs] + ", dword ptr [" + register[rhs] + "]\n";
		}
		if (n.getRealType() == TypeTable.INTEGER || n.getRealType() == TypeTable.CHARACTER) {
			output += "\tcmp    " + register[lhs] + ", 0\n";
			output += "\tje     .L" + labelLeftOp + "\n";

			output += "\tcmp    " + register[rhs] + ", 0\n";
			output += "\tje     .L" + labelRightOp + "\n";
		} else if (n.getRealType() == TypeTable.FLOAT) {
			// Push the left Op
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			// Push a zero
			output += "\tpush   0\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			// Compare
			output += "\tfcomip %st(0), %st(1)\n";
			output += "\tfstp   %st(0)\n"; // Clear the stack
			
			output += "\tje     .L" + labelLeftOp + "\n";
			
			
			// Push the right Op
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			// Push a zero
			output += "\tpush   0\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			// Compare
			output += "\tfcomip %st(0), %st(1)\n";
			output += "\tfstp   %st(0)\n"; // Clear the stack
						
			output += "\tje     .L" + labelRightOp + "\n";
		}
		output += "\tmov    " + register[lhs] + ", 1\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelLeftOp + ":\n";
		output += "\tmov    " + register[lhs] + ", 0\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelRightOp + ":\n";
		output += "\tmov    " + register[lhs] + ", 0\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelDone + ":";
		System.out.println(output);
		freeReg(rhs);
		return lhs;
	}

	@Override
	public Object visit(ArrayDecl n) {
		return null;
	}

	@Override
	public Object visit(ArrayDef n) {
		Integer regi = (Integer)n.getSubscriptExpression().accept(this);
		int minBound = Integer.parseInt(n.getMinimumBound());
		int regj = findEmptyReg(-1);
		String output = "";
		output += "# Array Definition(" + n.getId() + ")...\n";
		if (n.getSubscriptExpression() instanceof IdRef) {
			output += "\tmov    " + register[regi] + ", dword ptr [" + register[regi] + "]\n";
		}
		output += "\tsub    " + register[regi] + ", " + minBound + "\n";
		output += "\timul   " + register[regi] + ", 4\n";
		
		if (n.isLocal()) {
			output += "\tmov    " + register[regj] + ", %ebp\n";
		} else {
			output += "# GLOBAL ACCESS\n";
			output += "\tmov    " + register[regj] + ", offset flat:_main_ebp\n";
			output += "\tmov    " + register[regj] + ", dword ptr [" + register[regj] +"]\n";
		}
		
		if (n.isParam()) {
			output += "\tadd    " + register[regj] + ", " + n.getOffset() + "\n";
			output += "\tmov    " + register[regj] + ", dword ptr [" + register[regj] + "]\n";
		} else {
			output += "\tsub    " + register[regj] + ", " + n.getOffset() + "\n";			
		}
		
		output += "\tsub    " + register[regj] + ", " + register[regi];
		System.out.println(output);
		freeReg(regi);
		return regj;
	}

	@Override
	public Object visit(ArrayRef n) {
		Integer regi = (Integer)n.getSubscriptExpression().accept(this);
		int minBound = Integer.parseInt(n.getMinimumBound());
		int regj = findEmptyReg(-1);
		String output = "";
		
		output += "# Array Reference(" + n.getId() + ")...\n";
		if (n.getSubscriptExpression() instanceof IdRef) {
			output += "\tmov    " + register[regi] + ", dword ptr [" + register[regi] + "]\n";
		}
		output += "\tsub    " + register[regi] + ", " + minBound + "\n";
		output += "\timul   " + register[regi] + ", 4\n";
		
		if (n.isLocal()) {
			output += "\tmov    " + register[regj] + ", %ebp\n";
		} else {
			output += "# GLOBAL ACCESS\n";
			output += "\tmov    " + register[regj] + ", offset flat:_main_ebp\n";
			output += "\tmov    " + register[regj] + ", dword ptr [" + register[regj] +"]\n";
		}
		
		if (n.isParam()) {
			output += "\tadd    " + register[regj] + ", " + (n.getOffset()) + "\n";
			output += "\tmov    " + register[regj] + ", dword ptr [" + register[regj] + "]\n";
		} else {
			output += "\tsub    " + register[regj] + ", " + n.getOffset() + "\n";			
		}
		
		output += "\tsub    " + register[regj] + ", " + register[regi] + "\n";
		
		if (n.getRealType() != n.getConvertedType()) {
			if (n.getRealType() == TypeTable.INTEGER && n.getConvertedType() == TypeTable.FLOAT) {
				output += "\n# Integer to Float...\n";
				output += "\tpush   dword ptr [" + register[regj] + "]\n";
				output += "\tfild   dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 4\n";
				output += "\tfstp   dword ptr [%esp]\n";
				output += "\tmov    " + register[regj] + ", %esp\n";
				output += "\tadd    %esp, 4";
			} else if (n.getRealType() == TypeTable.FLOAT && n.getConvertedType() == TypeTable.INTEGER) {
				output += "\n# Float to Integer...\n";
				output += "\tpush   dword ptr [" + register[regj] + "]\n";
				output += "\tfld    dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 4\n";
				output += "\tfisttp dword ptr [%esp]\n";
				output += "\tmov    " + register[regj] + ", %esp\n";
				output += "\tadd    %esp, 4";
			}
		}
		
		System.out.println(output);
		freeReg(regi);
		return regj;
	}

	@Override
	public Object visit(Assignment n) {
		Integer lhs = (Integer)n.getLhs().accept(this);
		Integer rhs = (Integer)n.getRhs().accept(this);
		String output = "";
		output += "# Assignment...\n";
		if (n.getRhs() instanceof IdRef || n.getRhs() instanceof ArrayRef) {
			output += "\tmov    " + register[rhs] + " , dword ptr [" + register[rhs] + "]\n";
		} 
		output += "\tmov    dword ptr [" + register[lhs] + "], " + register[rhs];			
		System.out.println(output);
		freeReg(lhs);
		freeReg(rhs);
		return null;
	}

	@Override
	public Object visit(CallFunction n) {
		String output = "";
		Integer regi = -1;
		ASTNode paramNode;
		int paramCount = 0;
		int stackPushes = 0;
		int minBound = -1;
		int maxBound = -1;
		int i = 0, j = 0;
		
		ArrayList<ASTNode> arrayParams = new ArrayList<ASTNode>();		// Parameters of call
		ArrayList<Integer> stackPushOffset = new ArrayList<Integer>();	// Constant's offset
		
		output += "# Call Function...";
		System.out.println(output);
		output = "";
		
		// Add all parameters to arraylist
		if (null != n.getCallArguments()) {
			Parameters params = (Parameters)n.getCallArguments();
			for (ASTNode node : params.getParameters()) {
				arrayParams.add(node);				
			}
		}
		
		// Save parameters
		output = "\tpush   %ebx\n";
		output += "\tpush   %ecx\n";
		output += "\tpush   %edx\n";
		output += "\tpush   %edi\n";
		output += "\tpush   %esi\n";
		System.out.println(output);
		
		for (i = (arrayParams.size() - 1); i >= 0; i--) {
			System.out.println("# CONSTANT VALUED PARAMS\n");
			if (arrayParams.get(i) instanceof BinaryExpression 
					|| arrayParams.get(i) instanceof ConstantInteger
					|| arrayParams.get(i) instanceof ConstantCharacter
					|| arrayParams.get(i) instanceof ConstantFloat
					|| arrayParams.get(i) instanceof CallFunction) {
				regi = (Integer)arrayParams.get(i).accept(this);
				output = "\tpush   " + register[regi] + "\n";
				System.out.println(output);
				stackPushes++;
				stackPushOffset.add(stackPushes);
				freeReg(regi);
			} else {
				if (!(arrayParams.get(i) instanceof IdRef) && !(arrayParams.get(i) instanceof ArrayRef) ) {
					System.err.println(" Handle :" + arrayParams.get(i).getClass().getName());
					
				}
			}
		}
		
		for (i = (arrayParams.size() - 1); i >= 0; i--) {
			paramNode = arrayParams.get(i);
			if (paramNode instanceof IdRef || paramNode instanceof ArrayRef) {
				regi = (Integer)paramNode.accept(this);
				output = "\tpush   " + register[regi] + "\n";
				System.out.println(output);
				freeReg(regi);
			} else if (paramNode instanceof BinaryExpression
					|| arrayParams.get(i) instanceof ConstantInteger
					|| arrayParams.get(i) instanceof ConstantCharacter
					|| arrayParams.get(i) instanceof ConstantFloat
					|| arrayParams.get(i) instanceof CallFunction) {
				regi = findEmptyReg(-1);
				output = "\tmov    " + register[regi] + ", %esp\n";
				output += "# offset = " + stackPushOffset.get(j) + "\n";
				output += "\tadd    " + register[regi] + ", " 
				+ (((stackPushes - stackPushOffset.get(j++)) + paramCount) * 4) + "\n";
				output += "\tpush   " + register[regi];
				System.out.println(output);
				freeReg(regi);
			}
			// Calculate the amount needed to be added back for array params
			if (paramNode instanceof IdRef && paramNode.isArray()) {
				maxBound = Integer.parseInt(paramNode.getMaximumBound());
				minBound = Integer.parseInt(paramNode.getMinimumBound());
				paramCount += maxBound - minBound + 1;
			} else {
				paramCount++;
			}
		}
		
		
		output = "\tcall   " + n.getId() + "\n";
		
		
		stackPushes += paramCount;
		if (paramCount != 0) {
			output += "\tadd    %esp, " + (stackPushes * 4) + "\n";			
		}
		output += "\tpop   %esi\n";
		output += "\tpop   %edi\n";
		output += "\tpop   %edx\n";
		output += "\tpop   %ecx\n";
		output += "\tpop   %ebx\n";
		System.out.println(output);
		
		// Since I get back a result in eax
		regi = findEmptyReg(-1);
		output = "\tmov    " + register[regi] + ", %eax\n";
		System.out.println(output);
		freeReg(0);
		return regi;
	}

	@Override
	public Object visit(CallProcedure n) {
		String output = "";
		Integer regi = -1;
		ASTNode paramNode;
		int paramCount = 0;
		int stackPushes = 0;
		int minBound = -1;
		int maxBound = -1;
		int i = 0, j = 0;
		
		ArrayList<ASTNode> arrayParams = new ArrayList<ASTNode>();		// Parameters of call
		ArrayList<Integer> stackPushOffset = new ArrayList<Integer>();	// Constant's offset
		
		output += "# Call Procedure...";
		System.out.println(output);
		output = "";
		
		// Add all parameters to arraylist
		if (null != n.getCallArguments()) {
			Parameters params = (Parameters)n.getCallArguments();
			for (ASTNode node : params.getParameters()) {
				arrayParams.add(node);				
			}
		}
		
		// Save registers
		output = "\tpush   %ebx\n";
		output += "\tpush   %ecx\n";
		output += "\tpush   %edx\n";
		output += "\tpush   %edi\n";
		output += "\tpush   %esi\n";
		System.out.println(output);
		
		for (i = (arrayParams.size() - 1); i >= 0; i--) {
			System.out.println("# CONSTANT VALUED PARAMS\n");
			if (arrayParams.get(i) instanceof BinaryExpression 
					|| arrayParams.get(i) instanceof ConstantInteger
					|| arrayParams.get(i) instanceof ConstantCharacter
					|| arrayParams.get(i) instanceof ConstantFloat
					|| arrayParams.get(i) instanceof CallFunction) {
				regi = (Integer)arrayParams.get(i).accept(this);
				output = "\tpush   " + register[regi] + "\n";
				System.out.println(output);
				stackPushes++;
				stackPushOffset.add(stackPushes);
				freeReg(regi);
			} else {
				if (!(arrayParams.get(i) instanceof IdRef) && !(arrayParams.get(i) instanceof ArrayRef) ) {
					System.err.println(" Handle :" + arrayParams.get(i).getClass().getName());
					
				}
			}
		}
		
		for (i = (arrayParams.size() - 1); i >= 0; i--) {
			paramNode = arrayParams.get(i);
			if (paramNode instanceof IdRef || paramNode instanceof ArrayRef) {
				regi = (Integer)paramNode.accept(this);
				output = "\tpush   " + register[regi] + "\n";
				System.out.println(output);
				freeReg(regi);
			} else if (paramNode instanceof BinaryExpression
					|| arrayParams.get(i) instanceof ConstantInteger
					|| arrayParams.get(i) instanceof ConstantCharacter
					|| arrayParams.get(i) instanceof ConstantFloat
					|| arrayParams.get(i) instanceof CallFunction) {
				regi = findEmptyReg(-1);
				output = "\tmov    " + register[regi] + ", %esp\n";
				output += "# offset = " + stackPushOffset.get(j) + "\n";
				output += "\tadd    " + register[regi] + ", " 
				+ (((stackPushes - stackPushOffset.get(j++)) + paramCount) * 4) + "\n";
				output += "\tpush   " + register[regi];
				System.out.println(output);
				freeReg(regi);
			}
			// Calculate the amount needed to be added back for array params
			if (paramNode instanceof IdRef && paramNode.isArray()) {
				maxBound = Integer.parseInt(paramNode.getMaximumBound());
				minBound = Integer.parseInt(paramNode.getMinimumBound());
				paramCount += maxBound - minBound + 1;
			} else {
				paramCount++;
			}
		}
		
		output = "\tcall   " + n.getId() + "\n";
		
		stackPushes += paramCount;
		if (paramCount != 0) {
			output += "\tadd    %esp, " + (stackPushes * 4) + "\n";		
		}
		output += "\tpop   %esi\n";
		output += "\tpop   %edi\n";
		output += "\tpop   %edx\n";
		output += "\tpop   %ecx\n";
		output += "\tpop   %ebx\n";
		System.out.println(output);
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
		// Dereference
		if (n.getCaseExpression() instanceof IdRef || n.getCaseExpression() instanceof ArrayRef) {
			output += "\tmov    " + register[regi] + ", dword ptr [" + register[regi] + "]\n";
		}
		
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
		output += "\tmov    " + register[regi] + ", " + (int)n.getCharacter().charAt(1);
		System.out.println(output);
		return regi;
	}

	@Override
	public Object visit(ConstantFloat n) {
		int regi = findEmptyReg(-1);
		String output = "";
		output += "# Constant Float...\n";
		output += "\tmov    " + register[regi] + ", [offset flat:_constant + " + n.getOffset() + "]\n";
		output += "\tmov    " + register[regi] + ", dword ptr [" + register[regi] + "]";
		if (n.getRealType() != n.getConvertedType()) {
			output += "\n# Float to Integer...\n";
			output += "\tpush    " + register[regi] + "\n";
			output += "\tfld     dword ptr [%esp]\n";
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
		output += "# Constant String...\n";
		output += "\tmov    " + register[regi] + ", [offset flat:_constant + " + n.getOffset() + "] #" + n.getString();
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
		// Dereference
		if (n.getLeftOperand() instanceof IdRef || n.getLeftOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[lhs] + ", dword ptr [" + register[lhs] + "]\n";
		}
		if (n.getRightOperand() instanceof IdRef || n.getRightOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[rhs] + ", dword ptr [" + register[rhs] + "]\n";
		}
		if (n.getRealType() == TypeTable.INTEGER || n.getRealType() == TypeTable.CHARACTER) {
			output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
			
			output += "\tje     .L" + labelEqualEqual + "\n";
			output += "\tjmp    .L" + labelNotEqual + "\n";
		} else if (n.getRealType() == TypeTable.FLOAT) {
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tfcomip %st(0), %st(1)\n";
			output += "\tfstp   %st(0)\n"; // Clear the stack
			
			output += "\tje     .L" + labelEqualEqual + "\n";
			output += "\tjmp    .L" + labelNotEqual;
		}
		
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
		String output = "";
		Integer subEsp = -1;
		
		for (Statement statement : n.getStatements()) {
			if (statement instanceof VariableDeclarations) {
				subEsp = (Integer)statement.accept(this);
			} else if (statement instanceof CompoundStatement) {
				output = n.getId() + ":\n";
				output += "\tpush   %ebp\n";
				output += "\tmov    %ebp, %esp\n";
				if (subEsp != -1) {
					output += "\tsub    %esp, " + subEsp * 4;					
				}
				System.out.println(output);
				statement.accept(this);
			} else {
				if (null != statement) {
					statement.accept(this);					
				}
			}
		}
		
		output = "\tleave\n";
		output += "\tret\n";
		System.out.println(output);
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
		// Dereference
		if (n.getLeftOperand() instanceof IdRef || n.getLeftOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[lhs] + ", dword ptr [" + register[lhs] + "]\n";
		}
		if (n.getRightOperand() instanceof IdRef || n.getRightOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[rhs] + ", dword ptr [" + register[rhs] + "]\n";
		}
		if (n.getRealType() == TypeTable.INTEGER || n.getRealType() == TypeTable.CHARACTER) {
			output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
			
			output += "\tjg     .L" + labelGreaterThan + "\n";
			output += "\tjmp    .L" + labelLessThanEqual + "\n";
		} else if (n.getRealType() == TypeTable.FLOAT) {
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tfcomip %st(0), %st(1)\n";
			output += "\tfstp   %st(0)\n"; // Clear the stack
			
			output += "\tja     .L" + labelGreaterThan + "\n";
			output += "\tjmp    .L" + labelLessThanEqual;
		}
		
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
		// Dereference
		if (n.getLeftOperand() instanceof IdRef || n.getLeftOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[lhs] + ", dword ptr [" + register[lhs] + "]\n";
		}
		if (n.getRightOperand() instanceof IdRef || n.getRightOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[rhs] + ", dword ptr [" + register[rhs] + "]\n";
		}
		if (n.getRealType() == TypeTable.INTEGER || n.getRealType() == TypeTable.CHARACTER) {
			output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
			
			output += "\tjge    .L" + labelGreaterThanEqual + "\n";
			output += "\tjmp    .L" + labelLessThan + "\n";
		} else if (n.getRealType() == TypeTable.FLOAT) {
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tfcomip %st(0), %st(1)\n";
			output += "\tfstp   %st(0)\n"; // Clear the stack
			
			output += "\tjae    .L" + labelGreaterThanEqual + "\n";
			output += "\tjmp    .L" + labelLessThan;
		}
		
		
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
		String output = "";
		int regi = findEmptyReg(-1);
		output += "# IdDef(" + n.getId() + ")...\n";
		
		if (n.isLocal()) {
			output += "\tmov    " + register[regi] + ", %ebp\n";
		} else {
			output += "# GLOBAL ACCESS\n";
			output += "\tmov    " + register[regi] + ", offset flat:_main_ebp\n";
			output += "\tmov    " + register[regi] + ", dword ptr [" + register[regi] +"]\n";
		}
		
		if (n.isParam()) {
			output += "\tadd    " + register[regi] + ", " + n.getOffset() + "\n";
			output += "\tmov    " + register[regi] + ", dword ptr [" + register[regi] + "]\n";
		} else {
			output += "\tsub    " + register[regi] + ", " + n.getOffset();			
		}
		System.out.println(output);
		return regi;
	}

	@Override
	public Object visit(IdRef n) {
		int regi = findEmptyReg(-1);
		String output = "";
		output += "# IdRef(" + n.getId() + ")...\n";
		
		if (n.isLocal()) {
			output += "\tmov    " + register[regi] + ", %ebp\n";
		} else {
			output += "# GLOBAL ACCESS\n";
			output += "\tmov    " + register[regi] + ", offset flat:_main_ebp\n";
			output += "\tmov    " + register[regi] + ", dword ptr [" + register[regi] +"]\n";
		}
		
		if (n.isParam()) { // This is an address
			output += "\tadd    " + register[regi] + ", " + n.getOffset() + "\n";
			output += "\tmov    " + register[regi] + ", dword ptr [" + register[regi] + "]\n";
		} else {
			output += "\tsub    " + register[regi] + ", " + n.getOffset();			
		}
		
		if (n.getRealType() != n.getConvertedType()) {
			if (n.getRealType() == TypeTable.INTEGER && n.getConvertedType() == TypeTable.FLOAT) {
				output += "\n# Integer to Float...\n";
				output += "\tpush   dword ptr [" + register[regi] + "]\n";
				output += "\tfild   dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 4\n";
				output += "\tfstp   dword ptr [%esp]\n";
				output += "\tmov    " + register[regi] + ", %esp\n";
				output += "\tadd    %esp, 4";
			} else if (n.getRealType() == TypeTable.FLOAT && n.getConvertedType() == TypeTable.INTEGER) {
				output += "\n# Float to Integer...\n";
				output += "\tpush   dword ptr [" + register[regi] + "]\n";
				output += "\tfld    dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 4\n";
				output += "\tfisttp dword ptr [%esp]\n";
				output += "\tmov    " + register[regi] + ", %esp\n";
				output += "\tadd    %esp, 4";
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
		Integer regi = (Integer)n.getIfExpression().accept(this);
		
		output += "# If Statement...";
		System.out.println(output);
		output = "";
		if (n.getIfExpression() instanceof ConstantInteger
				|| n.getIfExpression() instanceof ConstantCharacter) {
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
		} else if (n.getIfExpression() instanceof IdRef) {
			incrementLabel();
			labelFalse = getLabel();
			incrementLabel();
			labelDone = getLabel();
			
			output += "\tcmp    dword ptr [" + register[regi] + "], 0\n";
			output += "\tje     .L" + labelFalse;
			System.out.println(output);
			
			// If true then
			n.getThenStatement().accept(this);
			// Skip else
			output = "\tjmp    .L" + labelDone + "\n";
			// Skip to else
			output += ".L" + labelFalse + ":";
			System.out.println(output);
			
		} else if (n.getIfExpression() instanceof BinaryExpression) {
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
			
		} else if (n.getIfExpression() instanceof NOT) {
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
		freeReg(regi);
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
		// Dereference
		if (n.getLeftOperand() instanceof IdRef || n.getLeftOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[lhs] + ", dword ptr [" + register[lhs] + "]\n";
		}
		if (n.getRightOperand() instanceof IdRef || n.getRightOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[rhs] + ", dword ptr [" + register[rhs] + "]\n";
		}
		if (n.getRealType() == TypeTable.INTEGER || n.getRealType() == TypeTable.CHARACTER) {
			output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
			
			output += "\tjl     .L" + labelLessThan + "\n";
			output += "\tjmp    .L" + labelGreaterThanEqual + "\n";
		} else if (n.getRealType() == TypeTable.FLOAT) {
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tfcomip %st(0), %st(1)\n";
			output += "\tfstp   %st(0)\n"; // Clear the stack
			
			output += "\tjb     .L" + labelLessThan + "\n";
			output += "\tjmp    .L" + labelGreaterThanEqual;
		}
		
		
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
		// Dereference
		if (n.getLeftOperand() instanceof IdRef || n.getLeftOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[lhs] + ", dword ptr [" + register[lhs] + "]\n";
		}
		if (n.getRightOperand() instanceof IdRef || n.getRightOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[rhs] + ", dword ptr [" + register[rhs] + "]\n";
		}
		if (n.getRealType() == TypeTable.INTEGER || n.getRealType() == TypeTable.CHARACTER) {
			output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
			
			output += "\tjle     .L" + labelLessThanEqual + "\n";
			output += "\tjmp    .L" + labelGreaterThan + "\n";
		} else if (n.getRealType() == TypeTable.FLOAT) {
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tfcomip %st(0), %st(1)\n";
			output += "\tfstp   %st(0)\n"; // Clear the stack
			
			output += "\tjbe     .L" + labelLessThanEqual + "\n";
			output += "\tjmp    .L" + labelGreaterThan;
		}
		
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
		int edx = findEmptyReg(5);
		// Dereference
		if (n.getLeftOperand() instanceof IdRef || n.getLeftOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[lhs] + ", dword ptr [" + register[lhs] + "]\n";
		}
		if (n.getRightOperand() instanceof IdRef || n.getRightOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[rhs] + ", dword ptr [" + register[rhs] + "]\n";
		}
		output += "# Modulo...\n";
		output += "\tmov    " + register[eax] + ", " + register[lhs] + "\n";
		output += "\tcdq\n";
		output += "\tidiv   " + register[rhs] + "\n";
		output += "\tmov    " + register[lhs] + ", " + register[edx] + "\n";
		System.out.println(output);
		freeReg(rhs);
		freeReg(eax);
		freeReg(edx);
		return lhs;
	}

	@Override
	public Object visit(Multiply n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		output += "# Multiply...\n";
		// Dereference
		if (n.getLeftOperand() instanceof IdRef || n.getLeftOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[lhs] + ", dword ptr [" + register[lhs] + "]\n";
		}
		if (n.getRightOperand() instanceof IdRef || n.getRightOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[rhs] + ", dword ptr [" + register[rhs] + "]\n";
		}
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
			if (n.getRealType() == TypeTable.INTEGER && n.getConvertedType() == TypeTable.FLOAT) {
				output += "\n# Multiply Integer to Float...\n";
				output += "\tpush    " + register[lhs] + "\n";
				output += "\tfild    dword ptr [%esp]\n";
				output += "\tadd     %esp, 4\n";
				output += "\tsub     %esp, 4\n";
				output += "\tfstp    dword ptr [%esp]\n";
				output += "\tpop     " + register[lhs];
			} else if (n.getRealType() == TypeTable.FLOAT && n.getConvertedType() == TypeTable.INTEGER) {
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
		String output = "";
		output = "# NOT...\n";
		Integer regi = (Integer)n.getNotChild().accept(this);
		// Dereference
		if (n.getNotChild() instanceof IdRef || n.getNotChild() instanceof ArrayRef) {
			output += "\tmov    " + register[regi] + ", dword ptr [" + register[regi] + "]\n";
		}
		incrementLabel();
		int isZero = getLabel();
		incrementLabel();
		int notZero = getLabel();
		incrementLabel();
		int done = getLabel();
		
		output += "\tcmp    " + register[regi] + ", 0\n";
		output += "\tje     .L" + isZero + "\n";
		output += "\tjmp    .L" + notZero + "\n";
		
		output += ".L" + isZero + ":\n";
		output += "\tmov    " + register[regi] + ", 1\n";
		output += "\tjmp    .L" + done + "\n";
		
		output += ".L" + notZero + ":\n";
		output += "\tmov    " + register[regi] + ", 0\n";
		output += "\tjmp    .L" + done + "\n";
		
		output += ".L" + done + ":";
		
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
		// Dereference
		if (n.getLeftOperand() instanceof IdRef || n.getLeftOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[lhs] + ", dword ptr [" + register[lhs] + "]\n";
		}
		if (n.getRightOperand() instanceof IdRef || n.getRightOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[rhs] + ", dword ptr [" + register[rhs] + "]\n";
		}
		if (n.getRealType() == TypeTable.INTEGER || n.getRealType() == TypeTable.CHARACTER) {
			output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
			
			output += "\tjne     .L" + labelNotEqual + "\n";
			output += "\tjmp    .L" + labelEqualEqual + "\n";
		} else if (n.getRealType() == TypeTable.FLOAT) {
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tfcomip %st(0), %st(1)\n";
			output += "\tfstp   %st(0)\n"; // Clear the stack
			
			output += "\tjne     .L" + labelNotEqual + "\n";
			output += "\tjmp    .L" + labelEqualEqual;
		}
		
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
		int labelLeftOp = getLabel();
		incrementLabel();
		int labelRightOp = getLabel();
		incrementLabel();
		int labelDone = getLabel();
		
		output += "# OR...\n";
		// Dereference
		if (n.getLeftOperand() instanceof IdRef || n.getLeftOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[lhs] + ", dword ptr [" + register[lhs] + "]\n";
		}
		if (n.getRightOperand() instanceof IdRef || n.getRightOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[rhs] + ", dword ptr [" + register[rhs] + "]\n";
		}
		if (n.getRealType() == TypeTable.INTEGER || n.getRealType() == TypeTable.CHARACTER) {
			output += "\tcmp    " + register[lhs] + ", 0\n";
			output += "\tjne    .L" + labelLeftOp + "\n";

			output += "\tcmp    " + register[rhs] + ", 0\n";
			output += "\tje     .L" + labelRightOp + "\n";
		} else if (n.getRealType() == TypeTable.FLOAT) {
			// Push the left Op
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			// Push a zero
			output += "\tpush   0\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			// Compare
			output += "\tfcomip %st(0), %st(1)\n";
			output += "\tfstp   %st(0)\n"; // Clear the stack
			
			output += "\tjne     .L" + labelLeftOp + "\n";
			
			
			// Push the right Op
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			// Push a zero
			output += "\tpush   0\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			// Compare
			output += "\tfcomip %st(0), %st(1)\n";
			output += "\tfstp   %st(0)\n"; // Clear the stack
						
			output += "\tje     .L" + labelRightOp + "\n";
		}
		
		output += "\tmov    " + register[lhs] + ", 1\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelLeftOp + ":\n";
		output += "\tmov    " + register[lhs] + ", 1\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelRightOp + ":\n";
		output += "\tmov    " + register[lhs] + ", 0\n";
		output += "\tjmp    .L" + labelDone + "\n";
		
		output += ".L" + labelDone + ":";
		System.out.println(output);
		freeReg(rhs);
		return lhs;
	}

	@Override
	public Object visit(Parameters n) {
		return null;
	}

	@Override
	public Object visit(Procedure n) {
		String output = "";
		Integer subEsp = -1;
		
		for (Statement statement : n.getStatements()) {
			if (statement instanceof VariableDeclarations) {
				subEsp = (Integer)statement.accept(this);
			} else if (statement instanceof CompoundStatement) {
				output = n.getId() + ":\n";
				output += "\tpush   %ebp\n";
				output += "\tmov    %ebp, %esp\n";
				if (subEsp != -1) {
					output += "\tsub    %esp, " + subEsp * 4;					
				}
				System.out.println(output);
				statement.accept(this);
			} else {
				if (null != statement) {
					statement.accept(this);					
				}
			}
		}
		
		output = "\tleave\n";
		output += "\tret\n";
		System.out.println(output);
		return null;
	}

	@Override
	public Object visit(Program n) {
		String output = "";
		Integer subEsp = -1;
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
		System.out.println(output);
		
		for (Statement statement : n.getStatements()) {
			if (statement instanceof VariableDeclarations) {
				subEsp = (Integer)statement.accept(this);
			} else if (statement instanceof CompoundStatement) {
				output = "main:\n";
				output += "\tpush   %ebp\n";
				output += "\tmov    %ebp, %esp\n";
				if (subEsp != -1) {
					output += "\tsub    %esp, " + subEsp * 4 + "\n";
					output += "\tmov    %ebx, offset flat:_main_ebp\n";
					output += "\tmov    dword ptr [%ebx], %ebp\n";
				}
				System.out.println(output);
				statement.accept(this);
			} else {
				statement.accept(this);
			}
		}
		output = "\tleave\n";
		output += "\tret\n";
		output += ".comm _main_ebp, 4, 4\n";
		System.out.println(output);
		return null;
	}

	@Override
	public Object visit(Read n) {
		String output = "";
		output += "# Read...\n";
		Integer regi  = (Integer)n.getInput().accept(this);
		output += "\tmov    " + register[regi] + ", %ebp\n";
		output += "\tsub    " + register[regi] + ", " + n.getInput().getOffset() + "\n";
		output += "\tpush   " + register[regi] + "\n";
		if (n.getInput().getRealType() == TypeTable.FLOAT) {
			output += "\tpush   [offset flat:.io_format_in + 0]\n";
		} else if (n.getInput().getRealType() == TypeTable.INTEGER) {
			output += "\tpush   [offset flat:.io_format_in + 3]\n";
		} else if (n.getInput().getRealType() == TypeTable.CHARACTER) {
			output += "\tpush   [offset flat:.io_format_in + 6]\n";				
		}
		output += "\tcall   scanf\n";
		output += "\tadd    %esp, 8\n";
		System.out.println(output);
		freeReg(regi);
		return null;
	}

	@Override
	public Object visit(Return n) {
		Integer regi = (Integer)n.getReturn().accept(this);
		String output = "";
		int eax = findEmptyReg(0);
		output += "# Return...\n";
		if (n.getReturn() instanceof IdRef || n.getReturn() instanceof ArrayRef) {
			output += "\tmov    " + register[regi] + ", dword ptr [" + register[regi] + "]\n";	
		}
		output += "\tmov    " + register[eax] + ", " + register[regi] + "\n";
		output += "\tleave\n";
		output += "\tret\n";
		System.out.println(output);
		freeReg(regi);
		return eax;
	}

	@Override
	public Object visit(SubroutineDeclarations n) {
		for (ASTNode node : n.getSubroutines()) {
			node.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(Subtract n) {
		Integer lhs = (Integer)n.getLeftOperand().accept(this);
		Integer rhs = (Integer)n.getRightOperand().accept(this);
		String output = "";
		output += "# Subtract...\n";
		// Dereference
		if (n.getLeftOperand() instanceof IdRef || n.getLeftOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[lhs] + ", dword ptr [" + register[lhs] + "]\n";
		}
		if (n.getRightOperand() instanceof IdRef || n.getRightOperand() instanceof ArrayRef) {
			output += "\tmov    " + register[rhs] + ", dword ptr [" + register[rhs] + "]\n";
		}
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
			if (n.getRealType() == TypeTable.INTEGER && n.getConvertedType() == TypeTable.FLOAT) {
				output += "\n# Subtract Integer to Float...\n";
				output += "\tpush   " + register[lhs] + "\n";
				output += "\tfild   dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 4\n";
				output += "\tfstp   dword ptr [%esp]\n";
				output += "\tpop    " + register[lhs];
			} else if (n.getRealType() == TypeTable.FLOAT && n.getConvertedType() == TypeTable.INTEGER) {
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
		return subEsp;
	}

	@Override
	public Object visit(WhileStatement n) {
		String output = "";
		// Get the labels
		incrementLabel();
		int labelLoopExpr = getLabel();
		incrementLabel();
		int labelStatement = getLabel();
		
		// Print the start label
		output += "# While Statement...\n";
		output += "\tjmp    .L" + labelLoopExpr + "\n";
		output += ".L" + labelStatement + ":";
		System.out.println(output);
		
		// Print the statement code
		n.getDoStatement().accept(this);
		
		// Print label for the loop check
		output = ".L" + labelLoopExpr + ":";
		System.out.println(output);
		
		// Run the loop check
		Integer regi = (Integer)n.getWhileExpression().accept(this);
		// Dereference
		if (n.getWhileExpression() instanceof IdRef || n.getWhileExpression() instanceof ArrayRef) {
			output += "\tmov    " + register[regi] + ", dword ptr [" + register[regi] + "]\n";
		}
		// Compare the value given back form the check
		// If not equal, run loop again
		output = "\tcmp    " + register[regi] + ", 0\n";
		output += "\tjne     .L" + labelStatement;
		System.out.println(output);
		
		freeReg(regi);
		return null;
	}

	@Override
	public Object visit(Write n) {

		// TODO may need to save registers
		String output = "";
		int regi = (Integer)n.getOutput().accept(this);
		if (n.getOutput() instanceof ConstantFloat) {
			output += "# Print Float Constant...\n";
			output += "\tsub    %esp, 8\n";
			output += "\tpush   " + register[regi] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tsub    %esp, 4\n";
			output += "\tfstp   qword ptr [%esp]\n";
			output += "\tpush   [offset flat:.io_format + 0]\n";
			output += "\tcall   printf\n";
			output += "\tadd    %esp, 12";
		} else if (n.getOutput() instanceof ConstantInteger) {
			output += "# Print Integer Constant...\n";
			output += "\tpush   " + register[regi] + "\n";
			output += "\tpush   [offset flat:.io_format + 4]\n";
			output += "\tcall   printf\n";
			output += "\tadd    %esp, 8";
		} else if (n.getOutput() instanceof ConstantCharacter) {
			output += "# Print Character Constant...\n";
			output += "\tpush   " + register[regi] + "\n";
			output += "\tpush   [offset flat:.io_format + 8]\n";
			output += "\tcall   printf\n";
			output += "\tadd    %esp, 8";
		} else if (n.getOutput() instanceof ConstantString) {
			output += "# Print String Constant...\n";
			output += "\tpush   " + register[regi] + "\n";
			output += "\tpush   [offset flat:.io_format + 12]\n";
			output += "\tcall   printf\n";
			output += "\tadd    %esp, 8";
		} else if (n.getOutput() instanceof NOT) {
			output += "# Print NOT Ouptut...\n";
			output += "\tpush   " + register[regi] + "\n";
			output += "\tpush   [offset flat:.io_format + 4]\n";
			output += "\tcall   printf\n";
			output += "\tadd    %esp, 4";
		} else if (n.getOutput() instanceof IdRef || n.getOutput() instanceof ArrayRef) {
			if (n.getOutput().getRealType() == TypeTable.FLOAT) {
				output += "# Print Float Reference...\n";
				output += "\tpush   dword ptr [" + register[regi] + "]\n";					
				output += "\tfld    dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 8\n";
				output += "\tfstp   qword ptr [%esp]\n";
				output += "\tpush   [offset flat:.io_format + 0]\n";
				output += "\tcall   printf\n";
				output += "\tadd    %esp, 12";
			} else if (n.getOutput().getRealType() == TypeTable.INTEGER) {
				output += "# Print Integer Reference...\n";
				output += "\tpush   dword ptr [" + register[regi] + "]\n";					
				output += "\tpush   [offset flat:.io_format + 4]\n";
				output += "\tcall   printf\n";
				output += "\tadd    %esp, 8";
			} else if (n.getOutput().getRealType() == TypeTable.CHARACTER) {
				output += "# Print Character Reference...\n";
				output += "\tpush   dword ptr [" + register[regi] + "]\n";
				output += "\tpush   [offset flat:.io_format + 8]\n";
				output += "\tcall   printf\n";
				output += "\tadd    %esp, 8";
			}
		} else if (n.getOutput() instanceof BinaryExpression) {
			output += "# Expression...\n";
			if (n.getOutput().getRealType() == TypeTable.FLOAT) {
				output += "# Float Expression Reference...\n";
				output += "\tpush   " + register[regi] + "\n";
				output += "\tfld    dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 8\n";
				output += "\tfstp   qword ptr [%esp]\n";
				output += "\tpush   [offset flat:.io_format + 0]\n";
				output += "\tcall   printf\n";
				output += "\tadd    %esp, 12";
			} else if (n.getOutput().getRealType() == TypeTable.INTEGER) {
				output += "# Print Integer Expression Reference...\n";
				output += "\tpush   " + register[regi] + "\n";
				output += "\tpush   [offset flat:.io_format + 4]\n";
				output += "\tcall   printf\n";
				output += "\tadd    %esp, 8";
			} else if (n.getOutput().getRealType() == TypeTable.CHARACTER) {
				output += "# Print Character Expression Reference...\n";
				output += "\tpush   " + register[regi] + "\n";
				output += "\tpush   [offset flat:.io_format + 8]\n";
				output += "\tcall   printf\n";
				output += "\tadd    %esp, 8";
			}
			
		} else if (n.getOutput() instanceof CallFunction) {
			output += "# CallFunction...\n";
			
			if (n.getOutput().getRealType() == TypeTable.FLOAT) {
				output += "# Float Expression Reference...\n";
				output += "\tpush   " + register[regi] + "\n";
				output += "\tfld    dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 8\n";
				output += "\tfstp   qword ptr [%esp]\n";
				output += "\tpush   [offset flat:.io_format + 0]\n";
				output += "\tcall   printf\n";
				output += "\tadd    %esp, 12";
			} else if (n.getOutput().getRealType() == TypeTable.INTEGER) {
				output += "# Print Integer Expression Reference...\n";
				output += "\tpush   " + register[regi] + "\n";
				output += "\tpush   [offset flat:.io_format + 4]\n";
				output += "\tcall   printf\n";
				output += "\tadd    %esp, 8";
			} else if (n.getOutput().getRealType() == TypeTable.CHARACTER) {
				output += "# Print Character Expression Reference...\n";
				output += "\tpush   " + register[regi] + "\n";
				output += "\tpush   [offset flat:.io_format + 8]\n";
				output += "\tcall   printf\n";
				output += "\tadd    %esp, 8";
			}
		}
		freeReg(regi);
		System.out.println(output);
		return null;
	}

}
