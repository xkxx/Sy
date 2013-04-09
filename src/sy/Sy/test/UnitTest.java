package sy.Sy.test;
import static org.junit.Assert.assertTrue;

import org.junit.*;

import sy.Sy.Sy;
import sy.Sy.err.SyException;
import sy.Sy.err.ParseError;
import sy.Sy.err.RuntimeError;
import sy.Sy.obj.SyObject;

public class UnitTest {
	private Sy scriptObject;
	private static void print(String msg) {
		System.out.println(msg);
	}
	private SyObject test(String expression) throws SyException {
		scriptObject.addLines(expression);
		scriptObject.parse();
		print("AST dump:");
		print(scriptObject.debug());
		SyObject result = scriptObject.run();
		print("Result:");
		print(result.toString());
		return result;
	}
	
	@BeforeClass
    public static void setUpClass() throws Exception {
    }
 
    // Code executed before each test    
    @Before
    public void setUp() throws Exception {
		scriptObject = new Sy();
    }
 
    @Test
    public void testNumberExpr() throws RuntimeError, SyException {
        String expression = "3243";
        assertTrue(test(expression).getInt() == 3243);
    }
    
    @Test
    public void testNumberExpr2() throws RuntimeError, SyException {
        String expression = "-3243";
        assertTrue(test(expression).getInt() == -3243);
    }
    
    @Test
    public void testNumberEquality() throws RuntimeError, SyException {
        String expression = "3243 == 3243";
        assertTrue(test(expression).getInt() == 1);
    }
    
    @Test
    public void testNumberInEquality() throws RuntimeError, SyException {
        String expression = "3243 != 323";
        assertTrue(test(expression).getInt() == 1);
    }
 
    @Test
    public void testStringExpr() throws RuntimeError, SyException {
        String expression = "\"hello world\"";
        assertTrue(test(expression).getString().equals("hello world"));
    }
 
    @Test
    public void testDoubleExpr() throws RuntimeError, SyException {
        String expression = "0.314159";
        assertTrue(test(expression).getDouble() == 0.314159);
    }
    
    @Test
    public void testDoubleExpr2() throws RuntimeError, SyException {
        String expression = "3.";
        assertTrue(test(expression).getDouble() == 3.);
    }


    @Test
    public void testVarAssign() throws RuntimeError, SyException {
        String expression = "abc = 3.14159 \n abc == 3.14159";
        assertTrue(test(expression).getInt() == 1);
    }

    @Test
    public void testBuiltinFunc() throws RuntimeError, SyException {
        String expression = "sin(pi/2)";
        assertTrue(test(expression).getDouble() == 1.0);
    }
    
    @Test
    public void testSingleLineFunc() throws RuntimeError, SyException {
        String expression = "f(x) = x^2 \n f(4)";
        assertTrue(test(expression).getDouble() == 16.0);
    }
    
    @Test
    public void testSingleLineFunc2Var() throws RuntimeError, SyException {
        String expression = "f(x, y) = x+y \n f(4,2)";
        assertTrue(test(expression).getDouble() == 6.0);
    }
    
    @Test
    public void testFxplicitSingleLineFunc() throws RuntimeError, SyException {
        String expression = "func f(x) = x^2 \n f(4)";
        assertTrue(test(expression).getDouble() == 16.0);
    }
    
    @Test
    public void testFxplicitSingleLineFunc2() throws RuntimeError, SyException {
        String expression = "func f(x) = x^2 \n f(x,y) = x+y \n f(4,4)";
        assertTrue(test(expression).getDouble() == 8.0);
    }
    
    
    @Test
    public void testMultiLineFunc() throws RuntimeError, SyException {
        String expression = "func f(x) \n x^2 \n end \n f(4)";
        assertTrue(test(expression).getDouble() == 16.0);
    }
    
    @Test
    public void testSingleLineIf() throws RuntimeError, SyException {
        String expression = "if 3/2 == 1.5: 3.14159";
        assertTrue(test(expression).getDouble() == 3.14159);
    }
    
    // Code executed after each test   
    @After
    public void tearDown() throws Exception {
    }
 
    @AfterClass
    public static void tearDownClass() throws Exception {
        // Code executed after the last test method 
    }
}
