package sy.Sy.err;

public class RuntimeError extends SyException {
    public RuntimeError(String msg) {
		super(msg);
	}

	public String toString() {
        return "Runtime error:  " + msg;
    }
}
