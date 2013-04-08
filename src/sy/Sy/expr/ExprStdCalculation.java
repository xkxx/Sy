package sy.Sy.expr;

import sy.Sy.FSContext;
import sy.Sy.LexAnn;
import sy.Sy.err.FSException;
import sy.Sy.err.RuntimeError;
import sy.Sy.obj.FSObject;
import akme.mobile.util.MathUtil;


public class ExprStdCalculation extends ExprBinaryOp {
	
	public ExprStdCalculation(int op){
		super(op);
	}
	
	public ExprStdCalculation(FSExpr[] operands, int op) throws FSException {
		super(operands, op);
	}
	
	public FSObject eval(FSContext context) throws FSException{
		FSObject lVal = operands[0].eval(context), rVal = operands[1].eval(context);

		if (lVal.type == FSObject.T_INT && rVal.type == FSObject.T_INT){
            long lint = lVal.getInt(), rint = rVal.getInt();
            switch(opType) {
	            case LexAnn.TT_PLUS: return new FSObject(lint + rint);
	            case LexAnn.TT_MINUS: return new FSObject(lint - rint);
	            case LexAnn.TT_MULT: return new FSObject(lint * rint);
	            case LexAnn.TT_DIV: return new FSObject(lVal.getDouble() / rint);
	            case LexAnn.TT_MOD: return new FSObject(lint % rint);
	            case LexAnn.TT_EXP: return new FSObject(MathUtil.pow((double)lint, (double) rint));
	            default: throw new RuntimeError("Unrecognized mixable operator for int&int :" + opType);
            }
        } else if ((lVal.type == FSObject.T_DOUBLE || lVal.type == FSObject.T_INT) &&
                   (rVal.type == FSObject.T_DOUBLE || rVal.type == FSObject.T_INT)){
            double ldouble = lVal.getDouble(), rdouble = rVal.getDouble();
            switch(opType) {
	            case LexAnn.TT_PLUS: return new FSObject(ldouble + rdouble);
	            case LexAnn.TT_MINUS: return new FSObject(ldouble - rdouble);
	            case LexAnn.TT_MULT: return new FSObject(ldouble * rdouble);
	            case LexAnn.TT_DIV: return new FSObject(ldouble / rdouble);
	            case LexAnn.TT_MOD: return new FSObject(ldouble % rdouble);
	            case LexAnn.TT_EXP: return new FSObject(MathUtil.pow(ldouble, rdouble));
	            default: throw new RuntimeError("Unrecognized mixable operator for double&double :" + opType);
            }
        } else if (lVal.type == FSObject.T_STRING && rVal.type == FSObject.T_STRING){
            switch(opType) {
	            case LexAnn.TT_PLUS: return new FSObject(lVal.toString()+rVal.toString());
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
