package sy.Sy.expr;

import java.util.Vector;

import sy.Sy.SyContext;
import sy.Sy.LexAnn;
import sy.Sy.err.SyException;
import sy.Sy.obj.SyObject;


public class ExprIf extends SyExpr {

	private SyExpr condition;
	private SyExpr block;
	
	public Vector elseifConditions;
	public Vector elseifBlocks;
	
	public SyExpr elseBlock;
	
	public ExprIf(SyExpr condition, SyExpr block) {
		opType = LexAnn.TT_IF;
		this.condition = condition;
		this.block = block;
		elseifConditions = new Vector(5);
		elseifBlocks = new Vector(5);
	}
	
	public ExprIf(SyExpr condition, SyExpr block, SyExpr elseBlock) {
		opType = LexAnn.TT_IF;
		this.condition = condition;
		this.block = block;
		this.elseBlock = elseBlock;
		elseifConditions = new Vector(5);
		elseifBlocks = new Vector(5);
	}
	
	public SyObject eval(SyContext context) throws SyException {
		if(condition.eval(context) == SyObject.FSTRUE) {
			return this.block.eval(context);
		}
		if(elseifBlocks != null && elseifBlocks.size() > 0) {
			for(int i = 0; i < elseifBlocks.size(); i++) {
				if(((SyExpr)elseifConditions.elementAt(i)).eval(context) == SyObject.FSTRUE) {
					return ((SyExpr)elseifBlocks.elementAt(i)).eval(context);
				}
			}
		}
		if(elseBlock != null) {
			return elseBlock.eval(context);
		}
		return SyObject.FSNULL;
	}
	public String toString() {
		StringBuffer result = new StringBuffer("(If: \n\t[condition \n\t");
		result.append(condition);
		result.append("]\n\t[Block \n\t");
		result.append(block);
		result.append("]\n\t");
		if(elseifBlocks != null && elseifBlocks.size() > 0) {
			for(int i = 0; i < elseifBlocks.size(); i++) {
				result.append("[Elseif: \n\t");
				result.append(elseifConditions.elementAt(i));
				result.append("\n\t");
				result.append(elseifBlocks.elementAt(i));
				result.append("\n]");
			}
		}
		if(elseBlock != null) {
			result.append("[Else: \n\t");
			result.append(elseBlock);
			result.append("\n]");
		}
		return result.toString();
	}

}
