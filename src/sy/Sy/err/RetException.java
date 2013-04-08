package sy.Sy.err;

import sy.Sy.obj.FSObject;

public class RetException extends FSException {
	public FSObject retValue;
	public RetException(FSObject retValue) {
		this.retValue = retValue;
	}
}
