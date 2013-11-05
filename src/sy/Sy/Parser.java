package sy.Sy;

import akme.mobile.util.MathUtil;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import java.lang.*;

import sy.Sy.err.SyException;
import sy.Sy.err.ParseError;
import sy.Sy.err.RetException;
import sy.Sy.expr.*;
import sy.Sy.obj.SyFunction;
import sy.Sy.obj.SyObject;


/**
 * <b>Parser - Does the parsing - i.e it's the brains of the code.</b>
 * <p>
 * <I>Copyright (C) 2002 murlen.</I></p>
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.</p>
 * <p>
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.</p>
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc.,59 Temple Place, Suite 330, Boston MA 0211-1307 USA
 * </p>
 * @author murlen
 * @version 0.5
 *
 ** modifications by Joachim Van der Auwera
 * 14.08.2001 added support for indexed variables
 * 20.08.2001 -clean handling of setVar with null value
 *   - cleaner handling of if with null condition
 *   - make sure running empty script does nothing
 *   - extra info when throwing an exception (with surrounding lines)
 *   - changed operator prioritues for && and ||
 *   - fixed bug in parseIf with handling of nesting of if clauses with else
 *   - check for missing endif or endwhile (caused infinit loops)
 *   - check for a null to prevernt excepion in parseExpr
 * 28.08.2001
 *   - call to host.getVar() replaced by host.getVarEntry() (and added
 *     proper exception handling, in that case)
 * 31.08.2001
 *   - test on if condition being of correct type re-introduced
 * 10.09.2001
 *   - added < <= > >= on strings
 *   
 * 12.03.2012
 *   - add keyword global for assigning global variables
 *   - will add new variables if assigning values to undefined variables
 *   - added ^ for exp calculations
 *   - TODO: add func def like f(x) = 2*sin(x)
 */
public class Parser {
    
    private LineLoader code; //the code
    private LexAnn tok; //tokenizer
    
    private SyObject retVal;
    private SyContext global;
    private Sy host; //link to hosting Sy object
    private ExprBlock root;
    
    private String error[];
    /** Public constructor
     * @param h a reference to the Sy object
     */
    Parser(Sy h) {
    	global = new SyContext(h);
        root = new ExprBlock();
        tok=new LexAnn();
        host = h;
    }
    
    /**
     * Sets the LineLoader class to be used for input
     * @param in - the class
     * @throws ParseError 
     */
    void setCode(LineLoader in) {
        code=in;
        tok.setLineLoader(in);
    }
    
    /**
     *Incrementally parsing lines
     */
    void parse() {
        
        while (tok.ttype!=LexAnn.TT_EOF) {
        	getNextToken();
        	SyExpr expr = parseLine();
        	if(expr != SyExpr.FSNOP) {
        		root.addExpr(expr);
        	}
            
        }
        
    }
    
    /**
     * Incrementally parsing lines
     * @param line - the line to be parsed
     */
    void parse(String line) {
        tok.setString(line);
        parse();
    }
    
    SyObject run() {
    	return retVal =  root.eval(global);
    }
    
    /**
     * Resets the runtime state.
     */
    void reset() {
    	global.clear();
    }
    
    /**
     * Resets the parser state.
     */
    void clear() {
    	global.clear();
    	root = null;
    }
    
    /*statement - top level thing
     * 
     * TODO: make everything expressions
    */
    private SyExpr parseLine() {
        switch(tok.ttype) {
        //control struct and func def
            case LexAnn.TT_IF: {
            	return parseIf();
            }
            case LexAnn.TT_WHILE: {
            	return parseWhile();
            }
            case LexAnn.TT_DEFFUNC: {
            	return parseFunc(true);
            }
            case LexAnn.TT_DEFGLOBAL: {
            	return parseGlobalVarDef();
            }
            case LexAnn.TT_RETURN: {
            	getNextToken();
                return new ExprReturn(parseLine());
            }
            // FIXME: never consider func() = occation
            case LexAnn.TT_FUNC: {
                return parseFunc(false);
            }
//            case LexAnn.TT_ARRAY: {
//                parseArrayAssign(); // TODO
//                break;
//            }
            case LexAnn.TT_WORD: {
            	String var = (String) tok.value;
            	getNextToken();
            	if(tok.ttype == LexAnn.TT_EQ) {
            		return parseAssign(var);
            	}
            	else if(tok.ttype >= LexAnn.TT_PLUS && tok.ttype <= LexAnn.TT_NOT) {
            		return parseOp(new ExprVal(var));
            	}
            	else {
            		return new ExprVal(var);
            	}
            }
            case LexAnn.TT_INTEGER:
            case LexAnn.TT_DOUBLE:
            case LexAnn.TT_STRING: {
            	ExprVal firstVal = new ExprVal(new SyObject(tok.value, tok.ttype));
            	getNextToken();
            	if(tok.ttype >= LexAnn.TT_PLUS && tok.ttype <= LexAnn.TT_NOT) {
            		return parseOp(firstVal);
            	}
            	else {
            		return firstVal;
            	}
            }
            case LexAnn.TT_PLUS:
            case LexAnn.TT_MINUS: {
            	return parseOp(null);
            }
            case '(': {
            	return parseOp(null);
            }
            case LexAnn.TT_EOL: {
                return SyExpr.FSNOP;
            }
            case LexAnn.TT_EOF:{
            	return SyExpr.FSNOP;
            }
            default: {
                throw new ParseError("Expected identifier");
            }
        }
    }
    
    //Asignment parser
    /* NOTE: if var doesn't exist, it will be added.
     * 
     * */
    private SyExpr parseAssign(String firstVal) {
        getNextToken();
        return new ExprAssign(firstVal, parseLine());
    }
    
    private SyExpr parseFunc(boolean isFuncDef) {
    	if(isFuncDef == true) {
    		getNextToken(); // skip 'func'
    	}
	    String funcName = (String)tok.value;
	    
	    // should be a '('
	    getNextToken();
	    if(tok.ttype != '(') {
	    	throw new ParseError("expecting ( at function call");
	    }
	    getNextToken();

	    Vector funcParams = new Vector(4);
	    while(tok.ttype != ')') {
	    	funcParams.addElement(parseLine());
	    	if(tok.ttype == ',') {
	    		getNextToken();
	    	}
	    	else if (tok.ttype == ')') {
	    		break;
	    	}
	    	else {
	    		throw new ParseError("expecting , between function arguments");
	    	}
	    }
	    
	    getNextToken();
	    
	    if(isFuncDef == true) {
	    	return parseFuncDef(funcName, funcParams);
	    }
	    else {
		    // expecting op, = or nothing
		    if(tok.ttype == LexAnn.TT_EQ) {
		    	return parseFuncDef(funcName, funcParams);
		    }
		    else {
		    	ExprFuncCall funcCall = new ExprFuncCall(new ExprVal(funcName), funcParams);
		    	
		    	if(tok.ttype >= LexAnn.TT_PLUS && tok.ttype <= LexAnn.TT_NOT) {
		    		return parseOp(funcCall);
		    	}
		    	else {
		    		return funcCall;
		    	}
		    }
	    }
    }

	//handles function definitions
    private SyExpr parseFuncDef(String funcName, Vector funcParams) {
        
    	boolean multiline = (tok.ttype == LexAnn.TT_EQ)? false : true;
    	getNextToken();
        
    	SyExpr body;
    	
    	if(multiline) {
    		body = new ExprBlock();
    		while(tok.ttype != LexAnn.TT_END) {
    			((ExprBlock) body).addExpr(parseLine());
    			getNextToken();
    		}
    	}
    	else {
    		if(tok.ttype == LexAnn.TT_EOL) {
    			throw new ParseError("single-line function def must have body at the same line");
    		}
    		body = parseLine(); // TODO: variable context;
    	}
        
    	// this is a conservative approach
    	// we evaluate each funcParam and check if it is a TT_NAME
    	// we could allow arbitrary param names -- but it would be messy
    	
    	for(int i = 0; i < funcParams.size(); i++) {
    		String paramName = ((ExprVal) funcParams.elementAt(i)).getVarName();
    		if(paramName == null) {
    			throw new ParseError("function parameter is not a valid identifier");
    		}
    		funcParams.setElementAt(paramName, i);
    	}
    	SyFunction func = new SyFunction(body, funcParams, global);
    	global.setVar(funcName, func);
    	
        return new ExprVal(func);
    }
    
    //Expression parser
    private SyExpr parseOp(SyExpr firstVar) {
    	OpParser opParser = new OpParser();
    	
    	if(firstVar != null) {
    		opParser.add(firstVar);
    	}
        boolean exit = false;
        boolean inParen = false;
    	while(exit != true) {
    		if(tok.ttype >= LexAnn.TT_INTEGER && tok.ttype <= LexAnn.TT_NULL) {
    			opParser.add(new ExprVal(new SyObject (tok.value, tok.ttype)));
    		}
    		else if(tok.ttype == LexAnn.TT_WORD) {
    			opParser.add(new ExprVal((String)tok.value));
    		}
    		else if(tok.ttype == LexAnn.TT_FUNC) {
    			opParser.add(parseFunc(false));
    		}
    		else if(tok.ttype == LexAnn.TT_LAND || tok.ttype == LexAnn.TT_LOR) {
    			opParser.add(new ExprLogic(tok.ttype), inParen);
    		}
    		else if(tok.ttype == LexAnn.TT_LEQ || tok.ttype == LexAnn.TT_LNEQ) {
    			opParser.add(new ExprEquality(tok.ttype), inParen);
    		}
    		else if(tok.ttype >= LexAnn.TT_PLUS && tok.ttype <= LexAnn.TT_MOD) {
    			opParser.add(new ExprStdCalculation(tok.ttype), inParen);
    		}
    		else if (tok.ttype >= LexAnn.TT_LGR && tok.ttype <= LexAnn.TT_NOT) {
    			opParser.add(new ExprStdComparison(tok.ttype), inParen);
    		}
    		else if(tok.ttype == '(') {
    			inParen = true;
    		}
    		else if(tok.ttype == ')') {
    			if(!inParen) {
        			exit = true;
        			tok.pushBack(); // FIXME: potential fail point
    			}
    			else {
    				inParen = false;
    			}
    		}
    		else {
    			exit = true;
    			tok.pushBack(); // FIXME: potential fail point
    		}
    		getNextToken();
    	}
    	return opParser.parse();
    }
    
    private ExprIf parseIf() {
    	getNextToken();
    	SyExpr condition = parseLine();
    	// FIXME: we assume that after condition ttype=:or\n
    	boolean multiline = (tok.ttype == LexAnn.TT_THEN)? false : true;
    	getNextToken();
    	
    	ExprIf ret;
		
    	if(multiline) {
    		ExprBlock block = new ExprBlock();
    		while(tok.ttype == LexAnn.TT_END ||
    			  tok.ttype == LexAnn.TT_ELSIF ||
    			  tok.ttype == LexAnn.TT_ELSE) {
    			block.addExpr(parseLine());
    			getNextToken();
    		}
    		ret = new ExprIf(condition, block);
    		while (tok.ttype == LexAnn.TT_ELSIF) {
    			ret.elseifConditions.addElement(parseLine());
    			getNextToken();
    			block = new ExprBlock();
    			while(tok.ttype == LexAnn.TT_END ||
    	    		  tok.ttype == LexAnn.TT_ELSIF ||
    	    		  tok.ttype == LexAnn.TT_ELSE) {
    	    		block.addExpr(parseLine());
    	    		getNextToken();
    	    	}
    			ret.elseifBlocks.addElement(block);
    		}
    		if(tok.ttype == LexAnn.TT_ELSE) {
    			block = new ExprBlock();
    			while(tok.ttype == LexAnn.TT_END) {
      	    		block.addExpr(parseLine());
      	    		getNextToken();
      	    	}
    			ret.elseBlock = block;
    		}
    	}
    	else {
    		if(tok.ttype == LexAnn.TT_EOL) {
    			throw new ParseError("single-line IF must have body at the same line");
    		}
    		ret = new ExprIf(condition, parseLine());
    		getNextToken();
    		while (tok.ttype == LexAnn.TT_ELSIF) {
    			ret.elseifConditions.addElement(parseLine());
    			getNextToken();
    			if(tok.ttype != LexAnn.TT_THEN) {
    				throw new ParseError("single-line ELSE IF condition must end with :");
    			}
    			ret.elseifBlocks.addElement(parseLine());
    			if(tok.ttype == LexAnn.TT_EOL) {
    				throw new ParseError("expecting expression after ELSE IF condition");
    			}
    			getNextToken();
    		}
    		if(tok.ttype == LexAnn.TT_ELSE) {
    			ret.elseBlock = parseLine();
    		}
    	}
    	return ret;
    }
    
    private SyExpr parseWhile() {
        //parses the while statement
    	getNextToken();
        SyExpr condition = parseLine();
    	boolean multiline = (tok.ttype == LexAnn.TT_THEN)? false : true;
    	getNextToken();
    	if(multiline) {
    		ExprBlock block = new ExprBlock();
    		while(tok.ttype == LexAnn.TT_END) {
    			block.addExpr(parseLine());
    			getNextToken();
    		}
    		return new ExprWhile(condition, block);
    	}
    	else {
    		if(tok.ttype == LexAnn.TT_EOL) {
    			throw new ParseError("single-line IF must have body at the same line");
    		}
    		return new ExprWhile(condition, parseLine());
    	}
    }
    
    //parse global variable definitions
    // FIXME: add documentation on that setting local variable will change its outer scope.
    // While global keyword is for adding new global variables.
    private ExprBlock parseGlobalVarDef() {
        String name;
        ExprBlock block = new ExprBlock();
        do {
            getNextToken(); // var name
            if (tok.ttype!=LexAnn.TT_WORD) {
                throw new ParseError("Expected variable name identifier in global variable definition");
            }
            
            name = (String) tok.value;
            
            getNextToken(); // equal sign
            if (tok.ttype!=LexAnn.TT_EQ){
            	throw new ParseError("Expected value assignment following global variable definition");
            }
            getNextToken(); // value
            block.addExpr(new ExprAssign(name, parseLine(), true));
            getNextToken(); // , or EOL
            if (tok.ttype!=',' && tok.ttype!=LexAnn.TT_EOL) {
            	throw new ParseError("Expected ',' or EOL in global variable definition");
            }
        } while (tok.ttype!=LexAnn.TT_EOL);
        return block;
    }
//    
//    //format an error message and throw SyException
//    private void parseError(String s) throws SyException {
//        if(tok == null) {
//        	throw new SyException(s);
//        }
//        // set up our error block
//        error=new String[6];
//        error[0]=s;
//        error[1]=(new Integer(code.getCurLine())).toString();
//        error[2]=code.getLine();
//        error[3]=tok.toString();
//        error[4]=vars.toString();
//        if (gVars!=null) error[5]=(gVars==null)?"":gVars.toString();
//
//        // build the display string
//        s="\n\t"+s+"\n"+getContext();
//
//        throw new SyException(s);
//    }
    
    /**
     * get the current context
     * @return SyContext global context
     */
    
    public SyContext getContext() {
    	return global;
    }
    
    public SyExpr getAST() {
    	return root;
    }
    
//    public String getContext() {
//        int l=code.getCurLine();
//        String s="\t\t at line:" + l + " ";
//        if (l>-1) {
//            s+="\n\t\t\t  "+code.getLine(l-2);
//            s+="\n\t\t\t  "+code.getLine(l-1);
//            s+="\n\t\t\t> "+code.getLine(l)+" <";
//            s+="\n\t\t\t  "+code.getLine(l+1);
//            s+="\n\t\t\t  "+code.getLine(l+2);
//            s=s+ "\n\t\t current token:" + tok.toString();;
//            s=s+ "\n\t\t Variable dump:" + global;
//        } else s+="\n\t\t\t> "+tok.getLine()+" <";
//
//        return s;
//    }
    
    //return the error block
    public String[] getError() {
        return error;
    }
    
    // Convenient method
    private void getNextToken()  {
        
        tok.nextToken();
//    	System.out.println("next token: " + tok.ttype + "  " + tok.value);
    }
    
    private void resetTokens()  {
        tok.setString(code.getLine());
        tok.nextToken();
    }
    
    
    //Gets the 'return' value from the parser
    SyObject getReturnValue(){
	    return retVal;
    }
    
    //Can be called from external functions to force an exit
    void exit(SyObject o) {
	    retVal=o;
	    throw new RetException(o);
    }
}