package sy.Sy.obj;

import java.util.Hashtable;
import java.util.Vector;

import sy.Sy.SyContext;
import sy.Sy.err.SyException;
import sy.Sy.err.RetException;
import sy.Sy.expr.SyExpr;


// data object - don't care about encapsulation
public class SyFunction extends SyObject {

    public SyExpr body;
    public Vector paramNames; //list of parameter names
    protected SyContext context;
    
    public Hashtable localVars;
    
    public SyFunction(SyContext parent) {
    	super(null, T_FUNC);
        paramNames=new Vector(4);
        context = new SyContext(parent);
    }
    
    public SyFunction(SyExpr body, Vector paramNames, SyContext parent) {
    	super(null, T_FUNC);
        this.body = body;
        this.paramNames = paramNames;
        context = new SyContext(parent);
    }
    
    public SyObject call(SyObject params[]) throws SyException {
    	for(int i = 0; i < params.length; i++) {
    		String name = (String) paramNames.elementAt(i);
    		SyObject value = (SyObject) params[i];
    		context.setVar(name, value);
    	}
    	try {
			return body.eval(context);
		}
		catch (RetException retException) {
			return retException.retValue;
		}
		catch (SyException generalError) {
			throw generalError;
		}
    }
    
    public String toString() {
        return "(Function: " + paramNames + ")";
    }
}
