package sy.Sy.test;

import sy.Sy.Sy;
import sy.Sy.err.SyException;
import sy.Sy.err.ParseError;
import sy.Sy.err.RuntimeError;
import sy.Sy.obj.SyObject;
import java.util.Scanner;

public class Repl {
	private static void print(String msg) {
		System.out.print(msg);
	}
        private static void println(String msg) {
		System.out.println(msg);
	}
	private static SyObject test(String expression, Sy scriptObject) {
		scriptObject.addLines(expression);
		scriptObject.parse();
		println("AST dump:");
		print(scriptObject.debug());
		SyObject result = scriptObject.run();
		println("Result:");
		println(result.toString());
		return result;
	}
	
	public static void main(String[] args) {
                Scanner console = new Scanner(System.in);
                Sy script = new Sy();
                println("Sy Language REPL");
                while (true) {
                    print("> ");
                    String line = console.nextLine();
                    test(line, script);
                    script.clear();
                }
        }
}
