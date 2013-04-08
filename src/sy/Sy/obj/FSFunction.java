package sy.Sy.obj;

import java.util.Hashtable;
import java.util.Vector;

import sy.Sy.FSContext;
import sy.Sy.err.FSException;
import sy.Sy.err.RetException;
import sy.Sy.expr.FSExpr;


// data object - don't care about encapsulation
public class FSFunction extends FSObject {

    public FSExpr body;
    public Vector paramNames; //list of parameter names
    protected FSContext context;
    
    public Hashtable localVars;
    
    public FSFunction(FSContext parent) {
    	super(null, T_FUNC);
        paramNames=new Vector(4);
        context = new FSContext(parent);
    }
    
    public FSFunction(FSExpr body, Vector paramNames, FSContext parent) {
    	super(null, T_FUNC);
        this.body = body;
        this.paramNames = paramNames;
        context = new FSContext(parent);
    }
    
    public FSObject call(FSObject params[]) throws FSException {
    	for(int i = 0; i < params.length; i++) {
    		String name = (String) paramNames.elementAt(i);
    		FSObject value = (FSObject) params[i];
    		context.setVar(name, value);
    	}
    	try {
			return body.eval(context);
		}
		catch (RetException retException) {
			return retException.retValue;
		}
		catch (FSException generalError) {
			throw generalError;
		}
    }
    
    public String toString() {
        return "(Function: " + paramNames + ")";
    }
}
