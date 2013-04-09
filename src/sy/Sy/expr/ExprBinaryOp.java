package sy.Sy.expr;

import sy.Sy.SyContext;
import sy.Sy.err.SyException;
import sy.Sy.err.RuntimeError;
import sy.Sy.obj.SyObject;

public abstract class ExprBinaryOp extends SyExpr {

	public SyExpr operands[];

	public ExprBinaryOp(SyExpr[] operands, int op) throws SyException {
		this.operands = operands;
		opType = op;
		if(operands.length > 2) {
			throw new RuntimeError("Too many arguments for StdCalculation: expected 2, got " + operands.length);
		}
	}
	
	public ExprBinaryOp(int op) {
		operands = new SyExpr[2];
		opType = op;
	}
}
