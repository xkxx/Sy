package sy.Sy.expr;

import sy.Sy.FSContext;
import sy.Sy.err.FSException;
import sy.Sy.err.RuntimeError;
import sy.Sy.obj.FSObject;

public abstract class ExprBinaryOp extends FSExpr {

	public FSExpr operands[];

	public ExprBinaryOp(FSExpr[] operands, int op) throws FSException {
		this.operands = operands;
		opType = op;
		if(operands.length > 2) {
			throw new RuntimeError("Too many arguments for StdCalculation: expected 2, got " + operands.length);
		}
	}
	
	public ExprBinaryOp(int op) {
		operands = new FSExpr[2];
		opType = op;
	}
}
