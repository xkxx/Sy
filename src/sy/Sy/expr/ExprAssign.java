package sy.Sy.expr;

import sy.Sy.SyContext;
import sy.Sy.LexAnn;
import sy.Sy.err.SyException;
import sy.Sy.obj.SyObject;

// TODO: array assign, special cases

public class ExprAssign extends SyExpr {

	private SyExpr lhand, rhand;
	private String lvar;
	private boolean isGlobal;
	
	// FIXME: does nothing
	public ExprAssign(SyExpr lhand, SyExpr rhand) {
		opType = LexAnn.TT_EQ;
		this.lhand = lhand;
		this.rhand = rhand;
		isGlobal = false;
	}
	public ExprAssign(String var, SyExpr rhand) {
		opType = LexAnn.TT_EQ;
		this.lvar = var;
		this.rhand = rhand;
		isGlobal = false;
	}
	
	public ExprAssign(String var, SyExpr rhand, boolean isGlobal) {
		opType = LexAnn.TT_EQ;
		this.lvar = var;
		this.rhand = rhand;
		this.isGlobal = isGlobal;
	}
	
	public SyObject eval(SyContext context) {
		SyObject rhandVal = rhand.eval(context);
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
