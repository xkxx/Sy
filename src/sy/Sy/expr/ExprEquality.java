package sy.Sy.expr;

import sy.Sy.SyContext;
import sy.Sy.LexAnn;
import sy.Sy.err.SyException;
import sy.Sy.obj.SyObject;

public class ExprEquality extends ExprBinaryOp {

	public ExprEquality(SyExpr[] operands, int op) throws SyException {
		super(operands, op);
	}
	
	public ExprEquality(int op){
		super(op);
	}

	public SyObject eval(SyContext context) throws SyException {
		SyObject lVal = operands[0].eval(context), rVal = operands[1].eval(context);
		boolean result = lVal.equals(rVal);
		
		result = (opType == LexAnn.TT_LNEQ) ? !result : result;
		
		return (result) ? SyObject.FSTRUE : SyObject.FSFALSE;
	}
	
	public String toString() {
		// Lazy Eval: No need to know until this point.
		switch(opType) {
			case LexAnn.TT_LEQ:
				exprName = "(Equality";break;
			case LexAnn.TT_LNEQ:
				exprName = "(Non-Equality"; break;
		}
		return exprName + ":\n\t" + operands[0] + "\n\t" +operands[1] + "\n)";
	}

}
