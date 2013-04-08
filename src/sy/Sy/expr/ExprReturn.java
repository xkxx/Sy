package sy.Sy.expr;

import sy.Sy.FSContext;
import sy.Sy.LexAnn;
import sy.Sy.err.FSException;
import sy.Sy.err.RetException;
import sy.Sy.err.RuntimeError;
import sy.Sy.obj.FSObject;

public class ExprReturn extends FSExpr {

	private FSExpr returnExpr;
	
	public ExprReturn(FSExpr returnExpr) {
		opType = LexAnn.TT_RETURN;
		this.returnExpr = returnExpr;
	}
	
	public FSObject eval(FSContext context) throws FSException {
		FSObject returnVal = FSObject.FSNULL;
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
