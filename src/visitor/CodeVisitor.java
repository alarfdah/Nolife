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
		int maxBound = Integer.parseInt(n.getMaximumBound());
		int regj = findEmptyReg(-1);
		String output = "";
		output += "# ArrayDef(" + n.getId() + ")...\n";
		
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
			output += "\tsub    " + register[regj] + ", " + (((maxBound - minBound) * 4) + n.getOffset()) + "\n";			
		}
		
		output += "\tadd    " + register[regj] + ", " + register[regi];
		System.out.println(output);
		freeReg(regi);
		return regj;
	}

	@Override
	public Object visit(ArrayRef n) {
		Integer regi = (Integer)n.getSubscriptExpression().accept(this);
		int minBound = Integer.parseInt(n.getMinimumBound());
		int maxBound = Integer.parseInt(n.getMaximumBound());
		int regj = findEmptyReg(-1);
		String output = "";
		
		output += "# ArrayRef(" + n.getId() + ")...\n";
		
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
			output += "\tsub    " + register[regj] + ", " + (((maxBound - minBound) * 4) + n.getOffset()) + "\n";			
		}
		
		output += "\tadd    " + register[regj] + ", " + register[regi] + "\n";
		output += "\tmov    " + register[regj] + ", dword ptr [" + register[regj] + "]\n";
		
		if (n.getRealType() != n.getConvertedType()) {
			if (n.getRealType() == TypeTable.INTEGER && n.getConvertedType() == TypeTable.FLOAT) {
				output += "\n# Integer to Float...\n";
				output += "\tpush   " + register[regj] + "\n";
				output += "\tfild   dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 4\n";
				output += "\tfstp   dword ptr [%esp]\n";
				output += "\tpop    " + register[regj] + "\n";
			} else if (n.getRealType() == TypeTable.FLOAT && n.getConvertedType() == TypeTable.INTEGER) {
				output += "\n# Float to Integer...\n";
				output += "\tpush   " + register[regj] + "\n";
				output += "\tfld    dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 4\n";
				output += "\tfisttp dword ptr [%esp]\n";
				output += "\tpop    " + register[regj] + "\n";
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
		
		output += "\tmov    dword ptr [" + register[lhs] + "], " + register[rhs];			
		System.out.println(output);
		freeReg(lhs);
		freeReg(rhs);
		return null;
	}

	/**
	 *  Please read CallProcedure's explanation as they are pretty similar
	 *  The only difference is that callFunction expects a return value from eax
	 *  which it stores into a separate register after the call has been done.
	 *  Then frees(%eax) 
	 */
	@Override
	public Object visit(CallFunction n) {
		String output = "";
		Integer regi = -1;
		Integer regj = -1;
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
			paramNode = arrayParams.get(i);
			System.out.println("# CONSTANT VALUED PARAMS\n");
			// If the IdRef is an array, do not push as a constant
			if (paramNode instanceof IdRef && paramNode.getRealType() == paramNode.getConvertedType()) {
				// DO NOTHING
			} else if (paramNode instanceof ArrayRef && paramNode.getRealType() == paramNode.getConvertedType()) {
				// DO NOTHING
			} else if (paramNode instanceof IdRef && paramNode.isArray()) {
				// DO NOTHING
			}else { // Push constant values
				regi = (Integer)paramNode.accept(this);
				output = "\tpush   " + register[regi] + "\n";
				System.out.println(output);
				stackPushes++;
				stackPushOffset.add(stackPushes);
				freeReg(regi);				
			}
		
		}
		
		for (i = (arrayParams.size() - 1); i >= 0; i--) {
			paramNode = arrayParams.get(i);
			// Should be the same type. If not then what was returned was a constant
			if (paramNode instanceof IdRef && paramNode.getRealType() == paramNode.getConvertedType()) {
				regi = findEmptyReg(-1);
				
				
				if (paramNode.isLocal()) {
					output = "\tmov    " + register[regi] + ", %ebp\n";
				} else {
					output = "# GLOBAL ACCESS\n";
					output += "\tmov    " + register[regi] + ", offset flat:_main_ebp\n";
					output += "\tmov    " + register[regi] + ", dword ptr [" + register[regi] +"]\n";
				}
				
				if (paramNode.isParam()) { // This is an address
					output += "\tadd    " + register[regi] + ", " + paramNode.getOffset() + "\n";
					output += "\tmov    " + register[regi] + ", dword ptr [" + register[regi] + "]\n";
				} else {
					if (paramNode.isArray()) {
						minBound = Integer.parseInt(paramNode.getMinimumBound());
						maxBound = Integer.parseInt(paramNode.getMaximumBound());
						output += "\tsub    " + register[regi] + ", " + (((maxBound - minBound) * 4) + paramNode.getOffset()) + "\n";
					} else {
						output += "\tsub    " + register[regi] + ", " + paramNode.getOffset() + "\n";			
					}
				}
				output += "\tpush   " + register[regi] + "\n";
				freeReg(regi);
			} else if (paramNode instanceof ArrayRef && paramNode.getRealType() == paramNode.getConvertedType()) {
				regi = (Integer)((ArrayRef)paramNode).getSubscriptExpression().accept(this);
				minBound = Integer.parseInt(paramNode.getMinimumBound());
				maxBound = Integer.parseInt(paramNode.getMaximumBound());
				regj = findEmptyReg(-1);
				
				output = "\tsub    " + register[regi] + ", " + minBound + "\n";
				output += "\timul   " + register[regi] + ", 4\n";
				
				if (paramNode.isLocal()) {
					output += "\tmov    " + register[regj] + ", %ebp\n";
				} else {
					output += "# GLOBAL ACCESS\n";
					output += "\tmov    " + register[regj] + ", offset flat:_main_ebp\n";
					output += "\tmov    " + register[regj] + ", dword ptr [" + register[regj] +"]\n";
				}
				
				if (paramNode.isParam()) {
					output += "\tadd    " + register[regj] + ", " + paramNode.getOffset() + "\n";
					output += "\tmov    " + register[regj] + ", dword ptr [" + register[regj] + "]\n";
				} else {
					output += "\tsub    " + register[regj] + ", " + (((maxBound - minBound) * 4) + paramNode.getOffset()) + "\n";			
				}
				
				output += "\tadd    " + register[regj] + ", " + register[regi] + "\n";
				output += "\tpush   " + register[regj] + "\n";
				freeReg(regj);
				freeReg(regi);
			} else { // Push constant values
				regi = findEmptyReg(-1);
				output = "\tmov    " + register[regi] + ", %esp\n";
				output += "# stackPushes = " + stackPushes + " stackPushoffset = " + stackPushOffset.get(j) + " paramCount = " + paramCount + "\n";
				output += "\tadd    " + register[regi] + ", " 
						+ (((stackPushes - stackPushOffset.get(j++)) + paramCount) * 4) + "\n";
				output += "\tpush   " + register[regi];
				freeReg(regi);			
			}
			paramCount++;
			System.out.println(output);
		}
		
		output = "\tcall   " + n.getId() + "\n";
		output += "# FINAL stackPushes = " + stackPushes + " paramCount = " + paramCount + "\n";
		stackPushes += paramCount;
		if (paramCount != 0) {
			output += "\tadd    %esp, " + (stackPushes * 4) + "\n";		
		}
		output += "\tpop    %esi\n";
		output += "\tpop    %edi\n";
		output += "\tpop    %edx\n";
		output += "\tpop    %ecx\n";
		output += "\tpop    %ebx\n";
		System.out.println(output);
		
		// Since I get back a result in eax
		regi = findEmptyReg(-1);
		output = "\tmov    " + register[regi] + ", %eax\n";
		System.out.println(output);
		freeReg(0);
		return regi;
	}

	/**
	 *  I probably spent half the time on the rest of the this project
	 *  and the other half on the CallProcedure and CallFunction methods.
	 *  
	 *  Explanation:
	 *  	Since all my nodes return registers that contain CONSTANT values
	 *  	but the parameters have to be passed by reference, then I must pass
	 *  	an address to that constant.
	 *  	
	 *  	There are 3 things to take into consideration:
	 *  		1. Parameters should be passed in reverse order.
	 *  		2. Constant values should be pushed onto the stack
	 *  			in reverse order, then, and only then, do we 
	 *  			pass the references to them in reverse order.
	 *  			e.x 
	 *  				CONST 2
	 *  				CONST 1
	 *  				REF(CONST 2)
	 *  				REF(CONST 1)
	 *  		3. Addresses can point to variables on the stack, in which case
	 *  			we cannot accept() IdRef or ArrayRef as they will return 
	 *  			a constant. If we push that constant, we will have 2 copies
	 *  			of the same number, one in the original variable (%ebp-x) and
	 *  			another that has just been pushed beyond the scope of the local
	 *  			variables.
	 *  
	 *  	To handle these issues:
	 *  		1. Store all the parameters in an ArrayList
	 *  		2. Two loops: 
	 *  			1) Push anything that returns a constant.
	 *  				Since IdRef can return constants that we don't need to store,
	 *  				such as the conversion of an integer variable to a float,
	 *  				we need to make sure that the IdRef node DOES have the DIFFERENT
	 *  				realType and convertedType. If the realType and convertedType are
	 *  				the same, then no conversion takes place. In such a case IdRef
	 *  				will be pointing to a variable on the stack. If the types are
	 *  				different, and type conversion takes place, we come out with a new
	 *  				constant that was not previously on the stack. If the latter happens
	 *  				we can push it onto the stack as one of our constants in the first loop.
	 * 					Same thing goes for ArrayRefs.
	 * 				2) Second loop goes through the parameters in reverse order.
	 * 					If it is an IdRef/ArrayRef (AND realType == convertedType), does exactly
	 * 					what the IdRef/ArrayRef node does, except for dereferencing the register.
	 * 					So we now have an address to the actual variable on the stack.
	 * 					If it's neither IdRef nor ArrayRef (or it is, but with different types),
	 * 					then that means we already pushed its value on the stack as a constant.
	 * 					In which case we create a reference to that value off of the constant on the stack.
	 */
	@Override
	public Object visit(CallProcedure n) {
		String output = "";
		Integer regi = -1;
		Integer regj = -1;
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
			paramNode = arrayParams.get(i);
			System.out.println("# CONSTANT VALUED PARAMS\n");
			// If the IdRef is an array, do not push as a constant
			if (paramNode instanceof IdRef && paramNode.getRealType() == paramNode.getConvertedType()) {
				// DO NOTHING
			} else if (paramNode instanceof ArrayRef && paramNode.getRealType() == paramNode.getConvertedType()) {
				// DO NOTHING
			} else if (paramNode instanceof IdRef && paramNode.isArray()) {
				// DO NOTHING
			} else { // Push constant values
				regi = (Integer)paramNode.accept(this);
				output = "\tpush   " + register[regi] + "\n";
				System.out.println(output);
				stackPushes++;
				stackPushOffset.add(stackPushes);
				freeReg(regi);				
			}
		
		}
		
		for (i = (arrayParams.size() - 1); i >= 0; i--) {
			paramNode = arrayParams.get(i);
			// Should be the same type. If not then what was returned was a constant
			if (paramNode instanceof IdRef && paramNode.getRealType() == paramNode.getConvertedType()) {
				regi = findEmptyReg(-1);
				
				
				if (paramNode.isLocal()) {
					output = "\tmov    " + register[regi] + ", %ebp\n";
				} else {
					output = "# GLOBAL ACCESS\n";
					output += "\tmov    " + register[regi] + ", offset flat:_main_ebp\n";
					output += "\tmov    " + register[regi] + ", dword ptr [" + register[regi] +"]\n";
				}
				
				if (paramNode.isParam()) { // This is an address
					output += "\tadd    " + register[regi] + ", " + paramNode.getOffset() + "\n";
					output += "\tmov    " + register[regi] + ", dword ptr [" + register[regi] + "]\n";
				} else {
					if (paramNode.isArray()) {
						minBound = Integer.parseInt(paramNode.getMinimumBound());
						maxBound = Integer.parseInt(paramNode.getMaximumBound());
						output += "\tsub    " + register[regi] + ", " + (((maxBound - minBound) * 4) + paramNode.getOffset()) + "\n";
					} else {
						output += "\tsub    " + register[regi] + ", " + paramNode.getOffset() + "\n";			
					}
				}
				output += "\tpush   " + register[regi] + "\n";
				freeReg(regi);
			} else if (paramNode instanceof ArrayRef && paramNode.getRealType() == paramNode.getConvertedType()) {
				regi = (Integer)((ArrayRef)paramNode).getSubscriptExpression().accept(this);
				minBound = Integer.parseInt(paramNode.getMinimumBound());
				maxBound = Integer.parseInt(paramNode.getMaximumBound());
				regj = findEmptyReg(-1);
				
				output = "\tsub    " + register[regi] + ", " + minBound + "\n";
				output += "\timul   " + register[regi] + ", 4\n";
				
				if (paramNode.isLocal()) {
					output += "\tmov    " + register[regj] + ", %ebp\n";
				} else {
					output += "# GLOBAL ACCESS\n";
					output += "\tmov    " + register[regj] + ", offset flat:_main_ebp\n";
					output += "\tmov    " + register[regj] + ", dword ptr [" + register[regj] +"]\n";
				}
				
				if (paramNode.isParam()) {
					output += "\tadd    " + register[regj] + ", " + paramNode.getOffset() + "\n";
					output += "\tmov    " + register[regj] + ", dword ptr [" + register[regj] + "]\n";
				} else {
					output += "\tsub    " + register[regj] + ", " + (((maxBound - minBound) * 4) + paramNode.getOffset()) + "\n";			
				}
				
				output += "\tadd    " + register[regj] + ", " + register[regi] + "\n";
				output += "\tpush   " + register[regj] + "\n";
				freeReg(regj);
				freeReg(regi);
			} else { // Push constant values
				regi = findEmptyReg(-1);
				output = "\tmov    " + register[regi] + ", %esp\n";
				output += "# stackPushes = " + stackPushes + " stackPushoffset = " + stackPushOffset.get(j) + " paramCount = " + paramCount + "\n";
				output += "\tadd    " + register[regi] + ", " 
						+ (((stackPushes - stackPushOffset.get(j++)) + paramCount) * 4) + "\n";
				output += "\tpush   " + register[regi];
				freeReg(regi);			
			}
			paramCount++;
			System.out.println(output);
		}
		
		output = "\tcall   " + n.getId() + "\n";
		output += "# FINAL stackPushes = " + stackPushes + " paramCount = " + paramCount + "\n";
		stackPushes += paramCount;
		if (paramCount != 0) {
			output += "\tadd    %esp, " + (stackPushes * 4) + "\n";		
		}
		output += "\tpop    %esi\n";
		output += "\tpop    %edi\n";
		output += "\tpop    %edx\n";
		output += "\tpop    %ecx\n";
		output += "\tpop    %ebx\n";
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
		
		if (n.getRealType() == TypeTable.INTEGER || n.getRealType() == TypeTable.CHARACTER) {
			output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
			
			output += "\tje     .L_EQUAL_EQUAL_" + labelEqualEqual + "\n";
			output += "\tjmp    .L_NOT_EQUAL_" + labelNotEqual + "\n";
		} else if (n.getRealType() == TypeTable.FLOAT) {
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tfcomip %st(0), %st(1)\n";
			output += "\tfstp   %st(0)\n"; // Clear the stack
			
			output += "\tje     .L_EQUAL_EQUAL_" + labelEqualEqual + "\n";
			output += "\tjmp    .L_NOT_EQUAL_" + labelNotEqual;
		}
		
		output += ".L_EQUAL_EQUAL_" + labelEqualEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 1\n";
		output += "\tjmp    .L_EQUAL_EQUAL_DONE_" + labelDone + "\n";
		
		output += ".L_NOT_EQUAL_" + labelNotEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 0\n";
		output += "\tjmp    .L_EQUAL_EQUAL_DONE_" + labelDone + "\n";
		
		output += ".L_EQUAL_EQUAL_DONE_" + labelDone + ":";
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
		
		if (n.getRealType() == TypeTable.INTEGER || n.getRealType() == TypeTable.CHARACTER) {
			output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
			
			output += "\tjg     .L_GREATER_THAN_" + labelGreaterThan + "\n";
			output += "\tjmp    .L_LESS_THAN_EQUAL_" + labelLessThanEqual + "\n";
		} else if (n.getRealType() == TypeTable.FLOAT) {
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tfcomip %st(0), %st(1)\n";
			output += "\tfstp   %st(0)\n"; // Clear the stack
			
			output += "\tja     .L_GREATER_THAN_" + labelGreaterThan + "\n";
			output += "\tjmp    .L_LESS_THAN_EQUAL_" + labelLessThanEqual;
		}
		
		output += ".L_GREATER_THAN_" + labelGreaterThan + ":\n";
		output += "\tmov    " + register[lhs] + ", 1\n";
		output += "\tjmp    .L_GREATER_THAN_DONE_" + labelDone + "\n";
		
		output += ".L_LESS_THAN_EQUAL_" + labelLessThanEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 0\n";
		output += "\tjmp    .L_GREATER_THAN_DONE_" + labelDone + "\n";
		
		output += ".L_GREATER_THAN_DONE_" + labelDone + ":";
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
		
		if (n.getRealType() == TypeTable.INTEGER || n.getRealType() == TypeTable.CHARACTER) {
			output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
			
			output += "\tjge    .L_GREATER_THAN_EQUAL_" + labelGreaterThanEqual + "\n";
			output += "\tjmp    .L_LESS_THAN_" + labelLessThan + "\n";
		} else if (n.getRealType() == TypeTable.FLOAT) {
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tfcomip %st(0), %st(1)\n";
			output += "\tfstp   %st(0)\n"; // Clear the stack
			
			output += "\tjae    .L_GREATER_THAN_EQUAL_" + labelGreaterThanEqual + "\n";
			output += "\tjmp    .L_LESS_THAN_" + labelLessThan;
		}
		
		
		output += ".L_GREATER_THAN_EQUAL_" + labelGreaterThanEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 1\n";
		output += "\tjmp    .L_GREATER_THAN_EQUAL_DONE_" + labelDone + "\n";
		
		output += ".L_LESS_THAN_" + labelLessThan + ":\n";
		output += "\tmov    " + register[lhs] + ", 0\n";
		output += "\tjmp    .L_GREATER_THAN_EQUAL_DONE_" + labelDone + "\n";
		
		output += ".L_GREATER_THAN_EQUAL_DONE_" + labelDone + ":";
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
		// For passed arrays
		
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
			if (n.isArray()) {
				Integer minBound = Integer.parseInt(n.getMinimumBound());
				Integer maxBound = Integer.parseInt(n.getMaximumBound());
				output += "\tsub    " + register[regi] + ", " + (((maxBound - minBound) * 4) + n.getOffset()) + "\n";			
			} else {
				output += "\tsub    " + register[regi] + ", " + n.getOffset() + "\n";			
			}
		}
		output += "\tmov    " + register[regi] + ", dword ptr [" + register[regi] + "]\n";
		if (n.getRealType() != n.getConvertedType()) {
			if (n.getRealType() == TypeTable.INTEGER && n.getConvertedType() == TypeTable.FLOAT) {
				output += "\n# Integer to Float...\n";
				output += "\tpush   " + register[regi] + "\n";
				output += "\tfild   dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 4\n";
				output += "\tfstp   dword ptr [%esp]\n";
				output += "\tpop    " + register[regi] + "\n";
				
			} else if (n.getRealType() == TypeTable.FLOAT && n.getConvertedType() == TypeTable.INTEGER) {
				output += "\n# Float to Integer...\n";
				output += "\tpush   " + register[regi] + "\n";
				output += "\tfld    dword ptr [%esp]\n";
				output += "\tadd    %esp, 4\n";
				output += "\tsub    %esp, 4\n";
				output += "\tfisttp dword ptr [%esp]\n";
				output += "\tpop    " + register[regi] + "\n";
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
		
		if (n.getRealType() == TypeTable.INTEGER || n.getRealType() == TypeTable.CHARACTER) {
			output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
			
			output += "\tjl     .L_LESS_THAN_" + labelLessThan + "\n";
			output += "\tjmp    .L_GREATER_THAN_EQUAL_" + labelGreaterThanEqual + "\n";
		} else if (n.getRealType() == TypeTable.FLOAT) {
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tfcomip %st(0), %st(1)\n";
			output += "\tfstp   %st(0)\n"; // Clear the stack
			
			output += "\tjb     .L_LESS_THAN_" + labelLessThan + "\n";
			output += "\tjmp    .L_GREATER_THAN_EQUAL_" + labelGreaterThanEqual;
		}
		
		
		output += ".L_LESS_THAN_" + labelLessThan + ":\n";
		output += "\tmov    " + register[lhs] + ", 1\n";
		output += "\tjmp    .L_LESS_THAN_DONE_" + labelDone + "\n";
		
		output += ".L_GREATER_THAN_EQUAL_" + labelGreaterThanEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 0\n";
		output += "\tjmp    .L_LESS_THAN_DONE_" + labelDone + "\n";
		
		output += ".L_LESS_THAN_DONE_" + labelDone + ":";
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
		
		if (n.getRealType() == TypeTable.INTEGER || n.getRealType() == TypeTable.CHARACTER) {
			output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
			
			output += "\tjle    .L_LESS_THAN_EQUAL_" + labelLessThanEqual + "\n";
			output += "\tjmp    .L_GREATER_THAN_" + labelGreaterThan + "\n";
		} else if (n.getRealType() == TypeTable.FLOAT) {
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tfcomip %st(0), %st(1)\n";
			output += "\tfstp   %st(0)\n"; // Clear the stack
			
			output += "\tjbe    .L_LESS_THAN_EQUAL_" + labelLessThanEqual + "\n";
			output += "\tjmp    .L_GREATER_THAN_" + labelGreaterThan;
		}
		
		output += ".L_LESS_THAN_EQUAL_" + labelLessThanEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 1\n";
		output += "\tjmp    .L_LESS_THAN_EQUAL_DONE_" + labelDone + "\n";
		
		output += ".L_GREATER_THAN_" + labelGreaterThan + ":\n";
		output += "\tmov    " + register[lhs] + ", 0\n";
		output += "\tjmp    .L_LESS_THAN_EQUAL_DONE_" + labelDone + "\n";
		
		output += ".L_LESS_THAN_EQUAL_DONE_" + labelDone + ":";
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
		
		if (n.getRealType() == TypeTable.INTEGER || n.getRealType() == TypeTable.CHARACTER) {
			output += "\tcmp    " + register[lhs] + ", " + register[rhs] + "\n";
			
			output += "\tjne    .L_NOT_EQUAL_" + labelNotEqual + "\n";
			output += "\tjmp    .L_EQUAL_EQUAL_" + labelEqualEqual + "\n";
		} else if (n.getRealType() == TypeTable.FLOAT) {
			output += "\tpush   " + register[lhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tpush   " + register[rhs] + "\n";
			output += "\tfld    dword ptr [%esp]\n";
			output += "\tadd    %esp, 4\n";
			output += "\tfcomip %st(0), %st(1)\n";
			output += "\tfstp   %st(0)\n"; // Clear the stack
			
			output += "\tjne    .L_NOT_EQUAL_" + labelNotEqual + "\n";
			output += "\tjmp    .L_EQUAL_EQUAL_" + labelEqualEqual;
		}
		
		output += ".L_NOT_EQUAL_" + labelNotEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 1\n";
		output += "\tjmp    .L_NOT_EQUAL_DONE_" + labelDone + "\n";
		
		output += ".L_EQUAL_EQUAL_" + labelEqualEqual + ":\n";
		output += "\tmov    " + register[lhs] + ", 0\n";
		output += "\tjmp    .L_NOT_EQUAL_DONE_" + labelDone + "\n";
		
		output += ".L_NOT_EQUAL_DONE_" + labelDone + ":";
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
		output += "\tjmp    .L_LOOP_EXPR_" + labelLoopExpr + "\n";
		output += ".L_STATEMENT_" + labelStatement + ":";
		System.out.println(output);
		
		// Print the statement code
		n.getDoStatement().accept(this);
		
		// Print label for the loop check
		output = ".L_LOOP_EXPR_" + labelLoopExpr + ":";
		System.out.println(output);
		
		// Run the loop check
		Integer regi = (Integer)n.getWhileExpression().accept(this);
		
		// Compare the value given back form the check
		// If not equal, run loop again
		output = "\tcmp    " + register[regi] + ", 0\n";
		output += "\tjne     .L_STATEMENT_" + labelStatement;
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
				output += "\tpush   " + register[regi] + "\n";					
				output += "\tpush   [offset flat:.io_format + 4]\n";
				output += "\tcall   printf\n";
				output += "\tadd    %esp, 8";
			} else if (n.getOutput().getRealType() == TypeTable.CHARACTER) {
				output += "# Print Character Reference...\n";
				output += "\tpush   " + register[regi] + "\n";
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
