package sy.Sy.expr;

import java.util.Vector;

import sy.Sy.SyContext;
import sy.Sy.LexAnn;
import sy.Sy.err.SyException;
import sy.Sy.err.RuntimeError;
import sy.Sy.obj.SyFunction;
import sy.Sy.obj.SyObject;


public class ExprFuncCall extends SyExpr {

	private SyExpr funcExpr;
	private Vector arguments;
	
	public ExprFuncCall (SyExpr funcExpr, Vector arguments) {
		this.funcExpr = funcExpr;
		this.arguments = arguments;
	}
	public SyObject eval(SyContext context) throws SyException {
		SyObject func = funcExpr.eval(context);
		SyObject params[] = new SyObject[arguments.size()];
		if(func.type != SyObject.T_FUNC) {
			throw new RuntimeError("Object "+ func + " is not a function");
		}
		for(int i = 0; i < arguments.size(); i++) {
			params[i] = ((SyExpr)arguments.elementAt(i)).eval(context);
		}
		return ((SyFunction) func).call(params);
	}
	public String toString() {
		return "(FuncCall: \n\t" + funcExpr + "\n\t" + arguments + "\n)";
	}

}
