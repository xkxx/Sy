package sy.Sy;

import java.util.Vector;

import sy.Sy.err.SyException;
import sy.Sy.err.ParseError;
import sy.Sy.expr.ExprFuncCall;
import sy.Sy.expr.ExprVal;
import sy.Sy.expr.SyExpr;
import sy.Sy.obj.SyObject;


/**
 * <b>Femto Script - an incredibly simplistic scripting language</b>
   <p>
  <I>Copyright (C) 2002 murlen.</I></p>
    <p>
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or (at your option) any later version.</p>
    <p>
    This software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.</p>
    <p>
    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc.,59 Temple Place, Suite 330, Boston MA 0211-1307 USA
    </p>
 * @author murlen
 * @author Joachim Van der Auwera - extension concept
 * @version 0.5
 */
public class Sy {
    
    
    private Parser parser;
    private LineLoader code;
    private SyContext global;
    
    /** Constructor 
     * @throws ParseError */
    public Sy() {
        
        parser=new Parser(this);
        code=new LineLoader();
        parser.setCode(code);
        global = parser.getContext();
    }
    
    public void addLines(String s){
        code.addLines(s);
    }
    
    public void parse() {
    	parser.parse();
    }
    
    /**
     * parse the inputed line; only used in debug
     * for interactive evaluation, use evaluateExprssion
     */
    public void parse(String line) {
    	parser.parse(line);
    }
    
    /**
     * Run the parser over currently loaded code
     *@return any return value of the script's execution (will be one of 
     *Sy's supported type objects, Integer,String)
     */
    public SyObject run() {
		return parser.run();
    }

    /**
     * Run the parser over given expression
     *@return any return value of the script's execution (will be one of 
     *Sy's supported type objects, Integer,String)
     * @throws SyException 
     */ 
    public SyObject evaluateExpression(String expr) {
        parser.parse(expr);
        return parser.run();
    }
    
    /**
     * Resets the internal code store
     */
    public void reset(){
        code.reset();
        parser.reset();
    }
    
    /**
     * Resets the internal code store
     */
    public void clear(){
        code.reset();
    }
    
    public String debug() {
    	return parser.getAST().toString();
    }
    /**
    * Forces an exit from the currently running script 
    * equivalent of 'exit' keyword in FScriptME itself.
    * intended to be called from within functions implemented in Sy 
    * subclasses
    *@param o, object that will be returned from the entry point (runCode or cont)
    **/
    public void exit(SyObject o) {
	    parser.exit(o);
    }
    
    /** TODO
     * Continues execution from current point - only really
     * useful in a document processing application where you may
     * wish to add code, execute, add some more code..etc..
     *@return any return value of the script's execution (will be one of 
     *Sy's supported type objects, Integer,String)
     */
//    public Object cont() {
//        if (code.getCurLine()==0){
//            return run();
//        }
//        else {
//            return parser.parse(code.getCurLine()+1,code.lineCount()-1);
//        }
//    }
    
    /**
     * Returns more details on any error states, indicated by
     * FSExceptions.
     * @return String, see below <br>
     * s[0]=the error text <BR>
     * s[1]=the line number <BR>
     * s[2]=the line text <BR>
     * s[3]=the current token <BR>
     * s[4]=a variable dump (current scope) <BR>
     * s[5]=a global variable dump (only if currnent scope is not global <BR>
     */
    public String[] getError() {
        return parser.getError();
    }
    
    /**
     * Override this method to allow external access to variables
     * in your code.
     * @param name, the name of the variable the parser is requesting
     * e.g
     * add this...
     * <br>
     * if (name.equals("one") { return new Integer(1) }
     * <br>
     * to allow the code
     * <br>
     * a=one
     * <br>
     * to work in Sy
     * @return Object
     */
    protected SyObject getVar(String name) {
    	return SyBuiltin.vars.containsKey(name) ? (SyObject) SyBuiltin.vars.get(name) : SyObject.FSUNDEF;
    }
    
    /**
     * Override this method to allow external access to variables
     * in your code.
     *<p>As getVar(String name) but allows an index variable to be
     *passed so code such as :
     * name=list[2]
     * is possible
     * @param name, the name of the variable the parser is requesting
     * @return Object - currently expected to be String, Integer
     */
    protected Object getVar(String name,Object index) {
    	return SyObject.FSUNDEF;
    }
    
    /**
     * Logical inverse of getVar
     * @param name the variable name
     * @param value the value to set it to
     */
    protected boolean setVar(String name,Object value){
        return false;
    }
    
    /**
     * Logical inverse of getVar (with index)
     * @param name the variable name
     * @param index the index into the 'array'
     * @param value the value to set it to
     */
    protected boolean setVar(String name,Object index,Object value) {
    	return false;
    }
       
    /**
     * Override this call to implement custom functions
     * See the BasicIO class for an example
     *
     * @param name the function name
     * @param params an ArrayList of parameter values
     * @return an Object, currently expected to be Integer or String
     */
    protected Object callFunction(String name, Vector params) {
        throw new SyException("Unrecognized External: " + name);
    }
    
    /**
     *Sets a variable in script space = the value passed in - the variable
     *must be have the correct type - note that if the varialble is not defined in the 
     *script, calls will be made to subclass setVar methods - therefore this method 
     *should be used with caution from within an overriden setVar.
     *@param name the name of the variable
     *@param value the value to set variable to (String,Integer)*/
    public final void setScriptVar(String name, SyObject value) {
        global.setVar(name,value);
    }
    
   /**
     *Gets a variable in script space note that if the varialble is not defined in the 
     *script, calls will be made to subclass getVar methods - therefore this method 
     *should be used with caution from within an overriden getVar.
     *@param name the name of the variable
     *@return the value of the variable (String,Integer)*/
    public final SyObject getScriptVar(String name) {
        return global.getVar(name);
    }
    
    /**Calls a function in the script - note that if the function is not defined calls
     *will be made to the subclass callFunction methods - therefore this method should
     *be used with caution from within an overriden callFunction.
     *@param name the name of the function
     *@param params the parameters to pass (must be correct type and number)
     *@return the return value of the function (String,Integer)*/
    
    public final Object callScriptFunction(String name,Vector params) {
    	return new ExprFuncCall(new ExprVal(name), params).eval(global);
    }
    
}
