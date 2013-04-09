package sy.Sy.err;

public class ParseError extends SyException {
	public ParseError (String msg) {
		super(msg);
	}
    public String toString() {
        return "Parse Error:  " + msg;
    }
}
