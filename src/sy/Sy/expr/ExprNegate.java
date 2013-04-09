package sy.Sy.expr;

import sy.Sy.LexAnn;
import sy.Sy.SyContext;
import sy.Sy.err.RuntimeError;
import sy.Sy.err.SyException;
import sy.Sy.obj.SyObject;

public class ExprNegate extends SyExpr {

	private SyExpr var;
	
	public ExprNegate(SyExpr var) {
		opType = LexAnn.TT_MINUS;
		this.var = var;
	}
	
	public SyObject eval(SyContext context) throws SyException {
		SyObject val = var.eval(context);
		if (val.type == SyObject.T_INT) {
			return new SyObject(-val.getInt());
		}
		else if(val.type == SyObject.T_DOUBLE) {
			return new SyObject(-val.getDouble());
		}
		else {
			throw new RuntimeError("argument illegal for unary -:" + var);
		}
	}
	
	public String toString() {
		return "(Negate:\n\t" + var + "\n\t" + "\n)";
	}
}
