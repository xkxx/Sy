package sy.Sy.expr;

import sy.Sy.FSContext;
import sy.Sy.LexAnn;
import sy.Sy.err.FSException;
import sy.Sy.obj.FSObject;

// TODO: array assign, special cases

public class ExprAssign extends FSExpr {

	private FSExpr lhand, rhand;
	private String lvar;
	private boolean isGlobal;
	
	// FIXME: does nothing
	public ExprAssign(FSExpr lhand, FSExpr rhand) {
		opType = LexAnn.TT_EQ;
		this.lhand = lhand;
		this.rhand = rhand;
		isGlobal = false;
	}
	public ExprAssign(String var, FSExpr rhand) {
		opType = LexAnn.TT_EQ;
		this.lvar = var;
		this.rhand = rhand;
		isGlobal = false;
	}
	
	public ExprAssign(String var, FSExpr rhand, boolean isGlobal) {
		opType = LexAnn.TT_EQ;
		this.lvar = var;
		this.rhand = rhand;
		this.isGlobal = isGlobal;
	}
	
	public FSObject eval(FSContext context) throws FSException {
		FSObject rhandVal = rhand.eval(context);
		if(isGlobal) {
			context.setGlobalVar(lvar, rhandVal);
		}
		else {
			context.setVar(lvar, rhandVal);
		}
		return rhandVal;
	}
	
	public String toString() {
		return "(Assign: \n\t" + lvar + "\n\t" + rhand + "\n)";
	}

}
