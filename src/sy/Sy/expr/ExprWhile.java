package sy.Sy.expr;

import sy.Sy.SyContext;
import sy.Sy.LexAnn;
import sy.Sy.err.SyException;
import sy.Sy.obj.SyObject;

public class ExprWhile extends SyExpr {

	private SyExpr exitCondition;
	private SyExpr loopBlock;
	
	public ExprWhile(SyExpr exitCondition, SyExpr loopBlock) {
		opType = LexAnn.TT_WHILE;
		this.exitCondition = exitCondition;
		this.loopBlock = loopBlock;
	}
	
	public SyObject eval(SyContext context) throws SyException {
		try {
		SyObject condition = exitCondition.eval(context);
		SyObject returnVal = SyObject.FSNULL;
		
		while (condition == SyObject.FSTRUE) {
			returnVal = loopBlock.eval(context);
			condition = exitCondition.eval(context);
		}
		return returnVal;
		
		}
		catch(SyException err) {
			throw err;
		}
	}
	
	public String toString() {
		return "(While:\n\t" + exitCondition + "\n\t" + loopBlock + "\n)";
	}

}
