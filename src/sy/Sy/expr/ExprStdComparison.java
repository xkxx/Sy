package sy.Sy.expr;

import sy.Sy.SyContext;
import sy.Sy.LexAnn;
import sy.Sy.err.SyException;
import sy.Sy.err.RuntimeError;
import sy.Sy.obj.SyObject;

public class ExprStdComparison extends ExprBinaryOp {
	
	public ExprStdComparison(SyExpr[] operands, int op) throws SyException {
		super(operands, op);
	}
	
	public ExprStdComparison(int op) {
		super(op);
	}

	public SyObject eval(SyContext context) throws SyException {
		SyObject lVal = operands[0].eval(context), rVal = operands[1].eval(context);

		if((lVal.type == SyObject.T_INT || lVal.type == SyObject.T_DOUBLE) &&
		   (rVal.type == SyObject.T_INT || rVal.type == SyObject.T_DOUBLE)) {
			
			double ldouble = lVal.getDouble(), rdouble = rVal.getDouble();
			switch(opType) {
	            case LexAnn.TT_LLS: return (ldouble < rdouble) ? SyObject.FSTRUE : SyObject.FSFALSE;
	            case LexAnn.TT_LLSE: return (ldouble <= rdouble) ? SyObject.FSTRUE : SyObject.FSFALSE;
	            case LexAnn.TT_LGR: return (ldouble > rdouble) ? SyObject.FSTRUE : SyObject.FSFALSE;
	            case LexAnn.TT_DIV: return (ldouble >= rdouble) ? SyObject.FSTRUE : SyObject.FSFALSE;
	            default: throw new RuntimeError("Unrecognized mixable comparison for numbers: " + opType);
			}
		} else if(lVal.type == SyObject.T_STRING || rVal.type == SyObject.T_STRING) {
			int condition = lVal.toString().compareTo(rVal.toString());
			switch(opType) {
	            case LexAnn.TT_LLS: return (condition < 0) ? SyObject.FSTRUE : SyObject.FSFALSE;
	            case LexAnn.TT_LLSE: return (condition <= 0) ? SyObject.FSTRUE : SyObject.FSFALSE;
	            case LexAnn.TT_LGR: return (condition > 0) ? SyObject.FSTRUE : SyObject.FSFALSE;
	            case LexAnn.TT_DIV: return (condition >= 0) ? SyObject.FSTRUE : SyObject.FSFALSE;
	            default: throw new RuntimeError("Unrecognized mixable comparison for string&string: " + opType);
			}
        } else {
        	throw new RuntimeError("Type Mismatch for mixable comparison w/ op " + opType);
        }
	}
	
	public String toString() {
		// Lazy Eval: No need to know until this point.
		switch(opType) {
			case LexAnn.TT_LLS:
				exprName = "(Less";break;
			case LexAnn.TT_LLSE:
				exprName = "(LessEqual"; break;
			case LexAnn.TT_LGR:
				exprName = "(Larger"; break;
			case LexAnn.TT_LGRE: 
				exprName = "(LargerEqual"; break;
		}
		return exprName + ":\n\t" + operands[0] + "\n\t" + operands[1] + "\n)";
	}

}
