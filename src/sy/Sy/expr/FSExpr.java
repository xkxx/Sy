package sy.Sy.expr;

import sy.Sy.FSContext;
import sy.Sy.LexAnn;
import sy.Sy.err.FSException;
import sy.Sy.obj.FSObject;

public abstract class FSExpr {
	
	public static final FSExpr FSNOP = new FSExpr(LexAnn.TT_EOL) {
		public FSObject eval(FSContext context) {
			return FSObject.FSNULL;
		}
		public String toString () {
			return "(No Operation)";
		}
	};
	
	public int opType;
	
	protected static String exprName;
	
	public abstract String toString();
	public abstract FSObject eval(FSContext context) throws FSException;
	public FSExpr (int opType) {
		this.opType = opType;
	}
	public FSExpr () {
	}
}
