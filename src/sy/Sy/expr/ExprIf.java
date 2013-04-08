package sy.Sy.expr;

import java.util.Vector;

import sy.Sy.FSContext;
import sy.Sy.LexAnn;
import sy.Sy.err.FSException;
import sy.Sy.obj.FSObject;


public class ExprIf extends FSExpr {

	private FSExpr condition;
	private FSExpr block;
	
	public Vector elseifConditions;
	public Vector elseifBlocks;
	
	public FSExpr elseBlock;
	
	public ExprIf(FSExpr condition, FSExpr block) {
		opType = LexAnn.TT_IF;
		this.condition = condition;
		this.block = block;
		elseifConditions = new Vector(5);
		elseifBlocks = new Vector(5);
	}
	
	public ExprIf(FSExpr condition, FSExpr block, FSExpr elseBlock) {
		opType = LexAnn.TT_IF;
		this.condition = condition;
		this.block = block;
		this.elseBlock = elseBlock;
		elseifConditions = new Vector(5);
		elseifBlocks = new Vector(5);
	}
	
	public FSObject eval(FSContext context) throws FSException {
		if(condition.eval(context) == FSObject.FSTRUE) {
			return this.block.eval(context);
		}
		if(elseifBlocks != null && elseifBlocks.size() > 0) {
			for(int i = 0; i < elseifBlocks.size(); i++) {
				if(((FSExpr)elseifConditions.elementAt(i)).eval(context) == FSObject.FSTRUE) {
					return ((FSExpr)elseifBlocks.elementAt(i)).eval(context);
				}
			}
		}
		if(elseBlock != null) {
			return elseBlock.eval(context);
		}
		return FSObject.FSNULL;
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
