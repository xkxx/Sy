Sy: Dynamic Scripting Language for Java ME
==

Sy is a fork of murlen's fscriptME. The entire parser has been rewritten entirely, the interpreter removed, AST compiler added, syntax updated, and overall the code updated to be much more expandable and readable.

### Features

* *First class functions*
* *Compile once, run any time*
* *One-line control structures and function definitions*
* *loads of builtin math functions*
* *Compile-time syntax check*
* *Potential Parellel execution support*

### Implementation and Performance

The interpreter included in the original fscriptME has been removed. A AST compiler has been added in its place. Now new code will have some minor compilation overhead, but frequently used functions will run much faster. Further optimation in the LexAnn might be desirable to achieve better performance. Help will be very appreciated in this aspect.

### Future Roadmap

* *Bug fixing and more optimizations*
* *Landing vector and Matrix support*
* *Support for lazy evaluation and pass-by-name*
* *Complete unit test code*

### Usage

```
Sy scriptObject = new Sy()
scriptObject.addLines(expression);
scriptObject.parse();
SyObject result = scriptObject.run();
```

For detailed documentations, refer to Sy.java.

### Syntax

#### Variable and literals

Variable are defined as they are given values, for example:

```
integer = 10
float = 3.2
boolean = 1 # true is 1, false 0
hello = "hello world"
```

#### Operators

`+, -, *, /, %, =, ==, >, <, >=, <=, &&, ||` work as you expect

`^` is for power. No bitwise operators are supported

#### Block structures

`if` and `while` can either be single-line or multiline, for example:

```
if a == 1: doSomething()

while 1: deadLoop()

while b = 2
  doSomethingElse(b)
end

if today == "saturday" && today == "sunday"
  sleep()
else if today == "friday"
  party()
else
  work()
end
```

#### Functions

Functions in Sy is first class, meaning that you can assign functions to variables. They can be defined in multiple ways:

```
square(x) = x^2

func square(x) = x^2

func square(x)
  x^2
end
```

To assign to global variables in local scope, use `global`, for example:

```
func globalInjection()
  global spy = "I'm a spy"
end
```

#### Comments

`#` is used for comments

#### Builtins

`abs(), ceil(), floor(), sqrt(), sin(), cos(), tan(), toDeg(), toRad(), exp(), ln(), log(), asin(), acos(), atan(), random(), LN2, LN10, LOG2E, LOG10E, SQRT1_2, SQRT2, e, pi` are available and work as you expect

### Unit Test

The unit test code is located at /src/sy/Sy/test