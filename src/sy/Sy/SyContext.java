package sy.Sy;

import java.util.Hashtable;

import sy.Sy.obj.SyObject;


public class SyContext {

	public boolean isGlobal;
	private SyContext parent;
	private Sy host; //link to hosting Sy object
	private Hashtable varMap;
	
	public SyContext(Sy host) {
		parent = null;
		this.host = host;
		isGlobal = true;
		varMap = new Hashtable();
	}
	
	public SyContext(SyContext parent) {
		this.parent = parent;
		host = null;
		isGlobal = false;
		varMap = new Hashtable();
	}
	
	public boolean hasVar(String name) {
		return varMap.containsKey(name);
	}
	
	public SyObject getVar(String name) {
        if (varMap.containsKey(name)) {
            return (SyObject) varMap.get(name);
        } else if (parent != null) {
            return parent.getVar(name);
        } else if(host != null){
            return host.getVar(name);
        }
        return SyObject.FSUNDEF; //shouldn't get here
	}
	
	public void setVar(String name, SyObject value) {
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
	
	public void setGlobalVar(String name, SyObject value) {
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
