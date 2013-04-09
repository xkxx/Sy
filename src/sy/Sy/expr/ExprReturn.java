package sy.Sy.expr;

import sy.Sy.SyContext;
import sy.Sy.LexAnn;
import sy.Sy.err.SyException;
import sy.Sy.err.RetException;
import sy.Sy.err.RuntimeError;
import sy.Sy.obj.SyObject;

public class ExprReturn extends SyExpr {

	private SyExpr returnExpr;
	
	public ExprReturn(SyExpr returnExpr) {
		opType = LexAnn.TT_RETURN;
		this.returnExpr = returnExpr;
	}
	
	public SyObject eval(SyContext context) throws SyException {
		SyObject returnVal = SyObject.FSNULL;
		try {
			returnVal = returnExpr.eval(context);
		}
		catch(RuntimeError err) {
			throw err;
		}
		throw new RetException(returnVal);
	}
	public String toString() {
		return "(Return:\n\t" + returnExpr + "\n)";
	}

}
