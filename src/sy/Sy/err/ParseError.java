package sy.Sy.err;

public class ParseError extends FSException {
	public ParseError (String msg) {
		super(msg);
	}
    public String toString() {
        return "Parse Error:  " + msg;
    }
}
