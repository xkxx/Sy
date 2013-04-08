package sy.Sy.expr;

import sy.Sy.FSContext;
import sy.Sy.LexAnn;
import sy.Sy.err.FSException;
import sy.Sy.err.RuntimeError;
import sy.Sy.obj.FSObject;

public class ExprLogic extends ExprBinaryOp {
	
	public ExprLogic(FSExpr[] operands, int op) throws FSException {
		super(operands, op);
	}
	
	public ExprLogic(int op) {
		super(op);
	}

	public FSObject eval(FSContext context) throws FSException {
		FSObject lVal = operands[0].eval(context), rVal = operands[1].eval(context);
		
		if (lVal.type == FSObject.T_INT && rVal.type == FSObject.T_INT){
			boolean lb = lVal.getInt()!=0, rb = rVal.getInt()!=0;
			switch(opType) {
            case LexAnn.TT_LAND: return (lb && rb) ? FSObject.FSTRUE : FSObject.FSFALSE;
            case LexAnn.TT_LOR: return (lb || rb) ? FSObject.FSTRUE : FSObject.FSFALSE;
            default: throw new RuntimeError("Unrecognized  logical operator for int&int :" + opType);
			}
		}
		else {
        	throw new RuntimeError("Type Mismatch for mixable operator w/ op " + opType);
        }
	}
	
	public String toString() {
		// Lazy Eval: No need to know until this point.
		switch(opType) {
			case LexAnn.TT_LAND:
				exprName = "(AND";break;
			case LexAnn.TT_LOR:
				exprName = "(OR"; break;
		}
		return exprName + ":\n\t" + operands[0]+ "\n\t" + operands[1] + "\n)";
	}

}
