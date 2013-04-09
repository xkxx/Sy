package sy.Sy;


import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import sy.Sy.expr.ExprBinaryOp;
import sy.Sy.expr.SyExpr;

public class OpParser {

	// op priority table
	private static final Hashtable opPrio = new Hashtable() {{
		// logic calc
		put(new Integer(LexAnn.TT_LOR),new Integer(1)); // TODO: or short?
		put(new Integer(LexAnn.TT_LAND),new Integer(2));
		
		// logic cmp
		put(new Integer(LexAnn.TT_LEQ),new Integer(3));
		put(new Integer(LexAnn.TT_LNEQ),new Integer(3));
		put(new Integer(LexAnn.TT_LGR),new Integer(3));
		put(new Integer(LexAnn.TT_LGRE),new Integer(3));
		put(new Integer(LexAnn.TT_LLS),new Integer(3));
		put(new Integer(LexAnn.TT_LLSE),new Integer(3));
		
		// math calc
		put(new Integer(LexAnn.TT_PLUS),new Integer(4));
		put(new Integer(LexAnn.TT_MINUS),new Integer(4));
		put(new Integer(LexAnn.TT_MULT),new Integer(5));
		put(new Integer(LexAnn.TT_DIV),new Integer(5));
		put(new Integer(LexAnn.TT_MOD),new Integer(5));
		put(new Integer(LexAnn.TT_EXP),new Integer(6));
	}};
	// end of hashtable
	
	private final int parenWeight = 10;
	
	private Vector tokens;
	private Vector prios;
	
	OpParser () {
		tokens = new Vector(10);
		prios = new Vector(16, 30);
		prios.setSize(16);
	}
	
	//convenience function to get operator priority
	private int getPrio(int op){
		return ((Integer)opPrio.get(new Integer(op))).intValue();
	}
	
	public void add(SyExpr value) {
		tokens.addElement(value);
	}
	
	public void add(SyExpr op, boolean inParen) {
		int prio = getPrio(op.opType) + (inParen ? parenWeight : 0);
		tokens.addElement(op);
		
		if(prio > prios.size()) {
			prios.setSize(prio);
		}
		Vector prioObject = (Vector) prios.elementAt(prio);
		
		if(prioObject == null) {
			prioObject = new Vector(5);
			prios.setElementAt(prioObject, prio);
		}
		
		prioObject.addElement(new Integer(tokens.size()-1));
	}
	
	// TODO: preliminary: parallelism not maximized
	
	private SyExpr opLeftPop(int start) {
		SyExpr value = null;
		for(int i = start-1; i >=0; i--) {
			value = (SyExpr) tokens.elementAt(i);
			if(value != null) {
				tokens.setElementAt(null, i);
				return value;
			}
		}
		return value;
	}
	
	private SyExpr opRightPop(int start) {
		SyExpr value = null;
		for(int i = start+1; i < tokens.size(); i++) {
			value = (SyExpr) tokens.elementAt(i);
			if(value != null) {
				tokens.setElementAt(null, i);
				return value;
			}
		}
		return value;
	}
	public SyExpr parse() {
		System.out.println("op dump: ");
    	System.out.println(tokens);
    	System.out.println(prios);
		SyExpr finalExpr = SyExpr.FSNOP;
		for(int i = prios.size()-1; i > 1; i--) {
			Vector ops = (Vector) prios.elementAt(i);
			if(ops == null) continue;
			
			int lastIndex;
			for(int j = 0; j < ops.size(); j++) {
				int opIndex = ((Integer) ops.elementAt(j)).intValue();
				SyExpr op = (SyExpr) tokens.elementAt(opIndex);
				
				((ExprBinaryOp) op).operands[0] = opLeftPop(opIndex);
				((ExprBinaryOp) op).operands[1] = opRightPop(opIndex);
				
				lastIndex = opIndex;
				finalExpr = op;
			}
		}
		return finalExpr;
	}
}
