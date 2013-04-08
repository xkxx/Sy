package sy.Sy.expr;

import java.util.Vector;

import sy.Sy.FSContext;
import sy.Sy.LexAnn;
import sy.Sy.err.FSException;
import sy.Sy.err.RuntimeError;
import sy.Sy.obj.FSFunction;
import sy.Sy.obj.FSObject;


public class ExprFuncCall extends FSExpr {

	private FSExpr funcExpr;
	private Vector arguments;
	
	public ExprFuncCall (FSExpr funcExpr, Vector arguments) {
		this.funcExpr = funcExpr;
		this.arguments = arguments;
	}
	public FSObject eval(FSContext context) throws FSException {
		FSObject func = funcExpr.eval(context);
		FSObject params[] = new FSObject[arguments.size()];
		if(func.type != FSObject.T_FUNC) {
			throw new RuntimeError("Object "+ func + " is not a function");
		}
		for(int i = 0; i < arguments.size(); i++) {
			params[i] = ((FSExpr)arguments.elementAt(i)).eval(context);
		}
		return ((FSFunction) func).call(params);
	}
	public String toString() {
		return "(FuncCall: \n\t" + funcExpr + "\n\t" + arguments + "\n)";
	}

}
