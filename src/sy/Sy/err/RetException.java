package sy.Sy.err;

import sy.Sy.obj.SyObject;

public class RetException extends SyException {
	public SyObject retValue;
	public RetException(SyObject retValue) {
		this.retValue = retValue;
	}
}
