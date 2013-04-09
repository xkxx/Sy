package sy.Sy.expr;

import sy.Sy.SyContext;
import sy.Sy.LexAnn;
import sy.Sy.err.SyException;
import sy.Sy.obj.SyObject;

public abstract class SyExpr {
	
	public static final SyExpr FSNOP = new SyExpr(LexAnn.TT_EOL) {
		public SyObject eval(SyContext context) {
			return SyObject.FSNULL;
		}
		public String toString () {
			return "(No Operation)";
		}
	};
	
	public int opType;
	
	protected static String exprName;
	
	public abstract String toString();
	public abstract SyObject eval(SyContext context) throws SyException;
	public SyExpr (int opType) {
		this.opType = opType;
	}
	public SyExpr () {
	}
}
