package sy.Sy.expr;

import sy.Sy.SyContext;
import sy.Sy.LexAnn;
import sy.Sy.err.SyException;
import sy.Sy.err.RuntimeError;
import sy.Sy.obj.SyObject;

public class ExprLogic extends ExprBinaryOp {
	
	public ExprLogic(SyExpr[] operands, int op) throws SyException {
		super(operands, op);
	}
	
	public ExprLogic(int op) {
		super(op);
	}

	public SyObject eval(SyContext context) throws SyException {
		SyObject lVal = operands[0].eval(context), rVal = operands[1].eval(context);
		
		if (lVal.type == SyObject.T_INT && rVal.type == SyObject.T_INT){
			boolean lb = lVal.getInt()!=0, rb = rVal.getInt()!=0;
			switch(opType) {
            case LexAnn.TT_LAND: return (lb && rb) ? SyObject.FSTRUE : SyObject.FSFALSE;
            case LexAnn.TT_LOR: return (lb || rb) ? SyObject.FSTRUE : SyObject.FSFALSE;
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
