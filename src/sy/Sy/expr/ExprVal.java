package sy.Sy.expr;

import sy.Sy.SyContext;
import sy.Sy.obj.*;

public class ExprVal extends SyExpr {

	protected SyObject val;
	protected String valKey;
	
	public ExprVal(SyObject val) {
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
	public SyObject getVal() {
		return val;
	}
	
	// get constant value
	public String getVarName() {
		return valKey;
	}
	
	public SyObject eval(SyContext context) {
		if(val != null) {
			return val;
		}
		else if (valKey != null) {
			return context.getVar(valKey);
		}
		return SyObject.FSNULL; // shouldn't get here
	}

}
