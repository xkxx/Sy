package sy.Sy.test;

import sy.Sy.Sy;
import sy.Sy.expr.ExprBlock;
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
		ExprBlock bin = scriptObject.parse(expression);
		println("AST dump:");
		println(bin.toString());
		SyObject result = bin.eval(scriptObject.getContext());
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
