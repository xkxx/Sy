package sy.Sy.expr;

import java.util.Enumeration;
import java.util.Vector;

import sy.Sy.SyContext;
import sy.Sy.err.SyException;
import sy.Sy.obj.SyObject;


public class ExprBlock extends SyExpr {

	private Vector exprSequence;
	
	public ExprBlock () {
		exprSequence = new Vector(10);
	}
	
	public void addExpr(SyExpr expr) {
		exprSequence.addElement(expr);
		}
	
	public SyObject eval(SyContext context) throws SyException {
		SyObject exprValue = SyObject.FSNULL;
		for (Enumeration expr = exprSequence.elements(); expr.hasMoreElements();) {
			exprValue = ((SyExpr)expr.nextElement()).eval(context);
		}
		return exprValue;
	}
	public String toString() {
		StringBuffer result = new StringBuffer("(Block:");
		for (Enumeration expr = exprSequence.elements(); expr.hasMoreElements();) {
			result.append("\n\t");
			result.append(((SyExpr)expr.nextElement()));
	     }
		result.append("\n)");
		return result.toString();
	}

}
