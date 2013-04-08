package sy.Sy.expr;

import java.util.Enumeration;
import java.util.Vector;

import sy.Sy.FSContext;
import sy.Sy.err.FSException;
import sy.Sy.obj.FSObject;


public class ExprBlock extends FSExpr {

	private Vector exprSequence;
	
	public ExprBlock () {
		exprSequence = new Vector(10);
	}
	
	public void addExpr(FSExpr expr) {
		exprSequence.addElement(expr);
		}
	
	public FSObject eval(FSContext context) throws FSException {
		FSObject exprValue = FSObject.FSNULL;
		for (Enumeration expr = exprSequence.elements(); expr.hasMoreElements();) {
			exprValue = ((FSExpr)expr.nextElement()).eval(context);
		}
		return exprValue;
	}
	public String toString() {
		StringBuffer result = new StringBuffer("(Block:");
		for (Enumeration expr = exprSequence.elements(); expr.hasMoreElements();) {
			result.append("\n\t");
			result.append(((FSExpr)expr.nextElement()));
	     }
		result.append("\n)");
		return result.toString();
	}

}
