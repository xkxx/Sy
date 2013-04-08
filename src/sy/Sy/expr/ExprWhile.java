package sy.Sy.expr;

import sy.Sy.FSContext;
import sy.Sy.LexAnn;
import sy.Sy.err.FSException;
import sy.Sy.obj.FSObject;

public class ExprWhile extends FSExpr {

	private FSExpr exitCondition;
	private FSExpr loopBlock;
	
	public ExprWhile(FSExpr exitCondition, FSExpr loopBlock) {
		opType = LexAnn.TT_WHILE;
		this.exitCondition = exitCondition;
		this.loopBlock = loopBlock;
	}
	
	public FSObject eval(FSContext context) throws FSException {
		try {
		FSObject condition = exitCondition.eval(context);
		FSObject returnVal = FSObject.FSNULL;
		
		while (condition == FSObject.FSTRUE) {
			returnVal = loopBlock.eval(context);
			condition = exitCondition.eval(context);
		}
		return returnVal;
		
		}
		catch(FSException err) {
			throw err;
		}
	}
	
	public String toString() {
		return "(While:\n\t" + exitCondition + "\n\t" + loopBlock + "\n)";
	}

}
