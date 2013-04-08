package sy.Sy;

import java.util.Hashtable;

import sy.Sy.obj.FSObject;


public class FSContext {

	public boolean isGlobal;
	private FSContext parent;
	private FScript host; //link to hosting FScript object
	private Hashtable varMap;
	
	public FSContext(FScript host) {
		parent = null;
		this.host = host;
		isGlobal = true;
		varMap = new Hashtable();
	}
	
	public FSContext(FSContext parent) {
		this.parent = parent;
		host = null;
		isGlobal = false;
		varMap = new Hashtable();
	}
	
	public boolean hasVar(String name) {
		return varMap.containsKey(name);
	}
	
	public FSObject getVar(String name) {
        if (varMap.containsKey(name)) {
            return (FSObject) varMap.get(name);
        } else if (parent != null) {
            return parent.getVar(name);
        } else if(host != null){
            return host.getVar(name);
        }
        return FSObject.FSUNDEF; //shouldn't get here
	}
	
	public void setVar(String name, FSObject value) {
		if(parent != null && parent.hasVar(name) == true) {
			parent.setVar(name, value);
		}
		else if(host != null && host.setVar(name, value) == true) {
			return;
		}
		else {
			varMap.put(name, value);
		}
	}
	
	public void setGlobalVar(String name, FSObject value) {
		if(host != null && host.setVar(name, value) == true) {
			return;
		}
		else if(parent != null) {
			parent.setVar(name, value);
		}
		else {
			varMap.put(name, value);
		}
	}
	
	public void clear() {
		varMap.clear();
	}
	
	// TODO: array & matrix
}
