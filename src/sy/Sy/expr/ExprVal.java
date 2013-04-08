package sy.Sy.expr;

import sy.Sy.FSContext;
import sy.Sy.obj.*;

public class ExprVal extends FSExpr {

	protected FSObject val;
	protected String valKey;
	
	public ExprVal(FSObject val) {
		this.val = val;
	}
	public ExprVal(String key) {
		this.valKey = key;
	}

	public String toString() {
		if(val != null) {
			return "(Val:\t" + val + ")";
		}
		else {
			return "(Val:\t" + valKey + ")";
		}
	}

	// get constant value
	public FSObject getVal() {
		return val;
	}
	
	// get constant value
	public String getVarName() {
		return valKey;
	}
	
	public FSObject eval(FSContext context) {
		if(val != null) {
			return val;
		}
		else if (valKey != null) {
			return context.getVar(valKey);
		}
		return FSObject.FSNULL; // shouldn't get here
	}

}
