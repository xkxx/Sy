package sy.Sy.expr;

import sy.Sy.SyContext;
import sy.Sy.LexAnn;
import sy.Sy.err.SyException;
import sy.Sy.err.RuntimeError;
import sy.Sy.obj.SyObject;
import akme.mobile.util.MathUtil;


public class ExprStdCalculation extends ExprBinaryOp {
	
	public ExprStdCalculation(int op){
		super(op);
	}
	
	public ExprStdCalculation(SyExpr[] operands, int op) {
		super(operands, op);
	}
	
	public SyObject eval(SyContext context) {
		SyObject lVal = operands[0].eval(context), rVal = operands[1].eval(context);

		if (lVal.type == SyObject.T_INT && rVal.type == SyObject.T_INT){
            long lint = lVal.getInt(), rint = rVal.getInt();
            switch(opType) {
	            case LexAnn.TT_PLUS: return new SyObject(lint + rint);
	            case LexAnn.TT_MINUS: return new SyObject(lint - rint);
	            case LexAnn.TT_MULT: return new SyObject(lint * rint);
	            case LexAnn.TT_DIV: return new SyObject(lVal.getDouble() / rint);
	            case LexAnn.TT_MOD: return new SyObject(lint % rint);
	            case LexAnn.TT_EXP: return new SyObject(MathUtil.pow((double)lint, (double) rint));
	            default: throw new RuntimeError("Unrecognized mixable operator for int&int :" + opType);
            }
        } else if ((lVal.type == SyObject.T_DOUBLE || lVal.type == SyObject.T_INT) &&
                   (rVal.type == SyObject.T_DOUBLE || rVal.type == SyObject.T_INT)){
            double ldouble = lVal.getDouble(), rdouble = rVal.getDouble();
            switch(opType) {
	            case LexAnn.TT_PLUS: return new SyObject(ldouble + rdouble);
	            case LexAnn.TT_MINUS: return new SyObject(ldouble - rdouble);
	            case LexAnn.TT_MULT: return new SyObject(ldouble * rdouble);
	            case LexAnn.TT_DIV: return new SyObject(ldouble / rdouble);
	            case LexAnn.TT_MOD: return new SyObject(ldouble % rdouble);
	            case LexAnn.TT_EXP: return new SyObject(MathUtil.pow(ldouble, rdouble));
	            default: throw new RuntimeError("Unrecognized mixable operator for double&double :" + opType);
            }
        } else if (lVal.type == SyObject.T_STRING && rVal.type == SyObject.T_STRING){
            switch(opType) {
	            case LexAnn.TT_PLUS: return new SyObject(lVal.toString()+rVal.toString());
	            default: throw new RuntimeError("Unrecognized mixable operator for string&string :" + opType);
        }
        } else {
        	throw new RuntimeError("Type Mismatch for mixable operator w/ op " + opType);
        }
	}
	public String toString() {
		// Lazy Eval: No need to know until this point.
		switch(opType) {
			case LexAnn.TT_PLUS:
				exprName = "(Addition";break;
			case LexAnn.TT_MINUS:
				exprName = "(Substraction"; break;
			case LexAnn.TT_MULT:
				exprName = "(Multiplication"; break;
			case LexAnn.TT_DIV: 
				exprName = "(Division"; break;
			case LexAnn.TT_MOD: 
				exprName = "(Mod"; break;
			case LexAnn.TT_EXP: 
				exprName = "(Power function"; break;
		}
		return exprName + ":\n\t" + operands[0] + "\n\t" + operands[1] + "\n)";
	}

}
