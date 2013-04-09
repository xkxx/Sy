package sy.Sy;

import java.util.Hashtable;
import java.util.Random;

import sy.Sy.err.RuntimeError;
import sy.Sy.obj.SyFunction;
import sy.Sy.obj.SyObject;
import akme.mobile.util.MathUtil;

// built-in globals
public class SyBuiltin {
	
	public static final Hashtable vars = new Hashtable() {{
		// type constants
		put("int",		new SyObject(SyObject.T_INT));
		put("double",	new SyObject(SyObject.T_DOUBLE));
		put("string",	new SyObject(SyObject.T_STRING));
		put("function",	new SyObject(SyObject.T_FUNC));
		put("array",	new SyObject(SyObject.T_ARRAY));
		put("undef",	new SyObject(SyObject.T_UNDEF));
		
		// constants
		put("LN2",		new SyObject(6931471805599453D));
		put("LN10",		new SyObject(2.302585092994046D));
		put("LOG2E",	new SyObject(1.4426950408889634D));
		put("LOG10E",	new SyObject(0.4342944819032518D));
		put("SQRT1_2",	new SyObject(2.718281828459045D));
		put("SQRT2",	new SyObject(1.4142135623730951D));
		put("e",  		new SyObject(Math.E));
		put("pi",		new SyObject(Math.PI));
		
		// basic functions
		put("abs",		new BuiltinFuncBase("abs") {
			protected double call(double params[]) {
				return Math.abs(params[0]);
			}
		});
		put("ceil",		new BuiltinFuncBase("ceil") {
			protected double call(double params[]) {
				return Math.ceil(params[0]);
			}
		});
		put("floor",	new BuiltinFuncBase("floor") {
			protected double call(double params[]) {
				return Math.floor(params[0]);
			}
		});
		put("sqrt",		new BuiltinFuncBase("sqrt") {
			protected double call(double params[]) {
				return Math.sqrt(params[0]);
			}
		});
		put("sin",		new BuiltinFuncBase("sin") {
			protected double call(double params[]) {
				return Math.sin(params[0]);
			}
		});
		put("cos",		new BuiltinFuncBase("cos") {
			protected double call(double params[]) {
				return Math.cos(params[0]);
			}
		});
		put("tan",		new BuiltinFuncBase("tan") {
			protected double call(double params[]) {
				return Math.tan(params[0]);
			}
		});
		put("toDeg",	new BuiltinFuncBase("toDeg") {
			protected double call(double params[]) {
				return Math.toDegrees(params[0]);
			}
		});
		put("toRad",	new BuiltinFuncBase("toRad") {
			protected double call(double params[]) {
				return Math.toRadians(params[0]);
			}
		});
		
		// extended functions
		put("exp",		new BuiltinFuncBase("exp") {
			protected double call(double params[]) {
				return MathUtil.exp(params[0]);
			}
		});
		put("ln",		new BuiltinFuncBase("ln") {
			protected double call(double params[]) {
				return MathUtil.exp(params[0]);
			}
		});
		put("log",		new BuiltinFuncBase("log") {
			protected double call(double params[]) {
				return MathUtil.log10(params[0]);
			}
		});
		put("asin",		new BuiltinFuncBase("asin") {
			protected double call(double params[]) {
				return MathUtil.asin(params[0]);
			}
		});
		put("acos",		new BuiltinFuncBase("acos") {
			protected double call(double params[]) {
				return MathUtil.acos(params[0]);
			}
		});
		put("atan",		new BuiltinFuncBase("atan") {
			protected double call(double params[]) {
				return MathUtil.atan(params[0]);
			}
		});
		put("atan2",		new BuiltinFuncBase("atan2") {
			protected double call(double params[]) {
				return MathUtil.atan2(params[0], params[1]);
			}
		});
		put("random",		new BuiltinFuncBase("random") {
			protected double call(double params[]) {
				return (new Random()).nextDouble();
			}
		});
		
	}};
	// end of hashtable
}

abstract class BuiltinFuncBase extends SyFunction {
	// each builtin function must implement these attributes
		protected final String funcName;
		protected abstract double call(double params[]);
		
		public String toString() {
			return "(Function: " + funcName + " [builtin] )";
		}
		public BuiltinFuncBase(String funcName) {
			super(null, null, null);
			this.funcName = funcName;
			context = null;
		}
		
		public SyObject call(SyObject params[]) throws RuntimeError {
			double[] vars = new double[params.length];
			for (int i = 0; i < params.length; i++) {
				if(params[i].type == SyObject.T_DOUBLE || params[i].type == SyObject.T_INT) {
					vars[i] = params[i].getDouble();
				}
				else {
					throw new RuntimeError("builtin math functions only accept int or double");
				}
			}
			return new SyObject(call(vars));
		}
}
