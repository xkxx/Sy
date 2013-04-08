package sy.Sy.expr;

import sy.Sy.FSContext;
import sy.Sy.LexAnn;
import sy.Sy.err.FSException;
import sy.Sy.obj.FSObject;

public class ExprEquality extends ExprBinaryOp {

	public ExprEquality(FSExpr[] operands, int op) throws FSException {
		super(operands, op);
	}
	
	public ExprEquality(int op){
		super(op);
	}

	public FSObject eval(FSContext context) throws FSException {
		FSObject lVal = operands[0].eval(context), rVal = operands[1].eval(context);
		boolean result = lVal.equals(rVal);
		
		result = (opType == LexAnn.TT_LNEQ) ? !result : result;
		
		return (result) ? FSObject.FSTRUE : FSObject.FSFALSE;
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
