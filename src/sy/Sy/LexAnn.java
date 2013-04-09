package sy.Sy;
import java.util.Hashtable;

import sy.Sy.err.ParseError;


//The lexer - kind of - it started life as a re-implementation of
//StreamTokenizer - hence the peculiarities.


/**
 * <b>Re-Implementation of StreamTokenizer for Sy</b>
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
 *
 * <p>This class is a re-implementation of Sun's StreamTokenizer class
 * as it was causing problems (especially parsing -ve numbers).</p>
 * @author murlen
 * @author Joachim Van der Auwera
 * @version 0.5
 *
 * changes by Joachim Van der Auwera
 * 31.08.2001
 *     - simplified (speeded up) handling of comments (there was also an
 *       inconsistency in the newline handling inside and outside comments).
 *     - small mistake disallowed the letter 'A' in TT_WORD
 *
 * declares a string a, which is initialised to one double quote.
 * 11.8.2002 (murlen) Changed to allow \n \r \" \t in strings
 * 17.02.2003 (jvda) Also allow \\ in strings
 * 23.11.2003 (jvda) major rework of nextT() for speed
 *     - renumbered TT_xxx constants for faster switch statements

 * Modification by xkxx 12/3/2012:
 * - Added keyword global for defining global variables
 * - Added ^ keyword for exp.
 */

public final class LexAnn {

	// maximum line length
    private static final int MAX_LINE_LENGTH=1024;

    // general
    public static final int TT_WORD=9000;
    public static final int TT_INTEGER=9001;
    public static final int TT_DOUBLE=9002;
    public static final int TT_STRING=9003;
    public static final int TT_FUNC=9004;
    public static final int TT_ARRAY=9005;
    public static final int TT_NULL=9006;
    public static final int TT_EOL=9007;
    public static final int TT_EOF=9008; //never set by this class
    public static final int TT_EQ=9009;
    public static final int TT_DEFGLOBAL=9010;
    public static final int TT_TYPEOF=9011;

    // control struct
    public static final int TT_IF=9100;
    public static final int TT_ELSE=9101;
    public static final int TT_THEN=9102; // :
    public static final int TT_ELSIF=9103;
    public static final int TT_DEFFUNC=9104;
    public static final int TT_WHILE=9105;
//    public static final int TT_DEFINT=9019;
//    public static final int TT_DEFSTRING=9020;
//    public static final int TT_DEFDOUBLE=9021;
//    public static final int TT_DEFOBJECT=9022;
    public static final int TT_RETURN=9106;
    public static final int TT_END = 9107;

    // math calc
    public static final int TT_PLUS=9200;
    public static final int TT_MINUS=9201;
    public static final int TT_MULT=9202;
    public static final int TT_DIV=9203;
    public static final int TT_EXP=9204;
    public static final int TT_MOD=9205;

    // logic calc
    public static final int TT_LAND=9300;
    public static final int TT_LOR=9301;
    
    // equality
    public static final int TT_LEQ=9302;
    public static final int TT_LNEQ=9303;
    
    // comparison
    public static final int TT_LGR=9304;
    public static final int TT_LLS=9305;
    public static final int TT_LGRE=9306;
    public static final int TT_LLSE=9307;
    public static final int TT_NOT=9308;

    // table with matches between words and their token values
    private static Hashtable wordToken = new Hashtable() {{
        put("if", new Integer(TT_IF));
        put("then", new Integer(TT_THEN));
        put("else", new Integer(TT_ELSE));
	    put("elseif", new Integer(TT_ELSIF));
        put("while", new Integer(TT_WHILE));
        put("end", new Integer(TT_END));
        put("func", new Integer(TT_DEFFUNC));
        put("return", new Integer(TT_RETURN));
        put("global", new Integer(TT_DEFGLOBAL));
        put("typeof", new Integer(TT_TYPEOF));
        put("null", new Integer(TT_NULL));
    }};
    
    /** contains the current token type */
    public int ttype;

    /** Current token value object */
    public Object value;

    private boolean pBack;
    private char cBuf[],line[];
    private LineLoader lineLoader; 
    private int length;
    private int c=0;
    private static final int EOL=-1;
    private int pos=0;

    /**String representation of token (needs work)*/
    public String toString(){
        return value + ":" + ttype;
    }
    
    
    /**Constructor*/
    public LexAnn() {
        //note hard limit on how long a string can be
        cBuf=new char[MAX_LINE_LENGTH];
    }

    /**Convenience constructor which sets line as well
     * @throws ParseError */
    public LexAnn(String firstLine) throws ParseError {
        this();
        setString(firstLine);
    }
    
    /**Convenience constructor that takes code from CodeLoader
     * @throws ParseError */
    public LexAnn(LineLoader lineLoader) throws ParseError {
        this();
        setLineLoader(lineLoader);
    }
    
    /**
     * Sets the internal line buffer
     * @param str - the string to use
     * @throws ParseError 
     */
    public void setString(String str) throws ParseError {
        checkLine(str);
        line=str.toCharArray();
        pos=0;
        c=0;
        
        ttype = 0;
        value = 0;
    }

    public void setLineLoader(LineLoader lineLoader) throws ParseError {
        this.lineLoader = lineLoader;
    }
    
    /**
     *return the next char in the buffer
     */
    private int getChar() {
        if (pos<line.length) {
            return line[pos++];
        } else {
            return EOL;
        }
    }

    /**
     * return the character at a current line pos (+offset)
     * without affecting internal counters*/
    private int peekChar(int offset) {
        int n;

        n=pos+offset-1;
        if (n>=line.length) {
            return EOL;
        } else {
            return line[n];
        }
    }


    /**Read the next token
     * @return int - ttype
     * @throws ParseError */
    public int nextToken() throws ParseError {
    	if(pBack) {
    		pBack = false;
            return ttype;
    	}
    	if(ttype == 0 || ttype == TT_EOL) {
    		if((lineLoader != null) && (lineLoader.getCurLine() < lineLoader.lineCount())) {
    			setString(lineLoader.nextLine());
    		}
    		else {
    			ttype = LexAnn.TT_EOF;  // the only place TT_EOF gets set
    			return TT_EOL;
    		}
    	}
        return nextT();
    }

    /**Causes next call to nextToken to return same value*/
    public void pushBack() {
        pBack=true;
    }

    /**
     * get the line which is currently being parsed
     * @return
     */
    String getLine() {
        return new String(line);
    }


    //Internal next token function
    private int nextT() {
        int cPos=0;
        boolean getNext;
        value=null;
        ttype=0;

        while (ttype==0) {
            getNext=true;
            switch (c) {
            // start of line of whitespace
            case 0:
            case ' ': case '\t': case '\n': case '\r':
                break;

            // end of line marker
            case EOL:
                ttype=TT_EOL;
                break;

            // comments
            case '#':
                pos=length;     // skip to end of line
                ttype=TT_EOL;
                break;

            // quoted strings
            case '"':
                c=getChar();
                while ((c!=EOL) && (c!='"')) {
                    if (c=='\\'){
                        switch (peekChar(1)) {
                        case 'n' :
                            cBuf[cPos++]='\n';
                            getChar();
                            break;
                        case 't' :
                            cBuf[cPos++]='\t';
                            getChar();
                            break;
                        case 'r' :
                            cBuf[cPos++]='\r';
                            getChar();
                            break;
                        case '\"' :
                            cBuf[cPos++]='"';
                            getChar();
                            break;
                        case '\\' :
                            cBuf[cPos++]='\\';
                            getChar();
                            break;
                        }
                    } else {
                        cBuf[cPos++]=(char)c;
                    }
                    c=getChar();
                }
                value=new String(cBuf,0,cPos);
                ttype=TT_STRING;
                break;

            // Words
            case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
            case 'G': case 'H': case 'I': case 'J': case 'K': case 'L':
            case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R':
            case 'S': case 'T': case 'U': case 'V': case 'W': case 'X':
            case 'Y': case 'Z': case 'a': case 'b': case 'c': case 'd':
            case 'e': case 'f': case 'g': case 'h': case 'i': case 'j':
            case 'k': case 'l': case 'm': case 'n': case 'o': case 'p':
            case 'q': case 'r': case 's': case 't': case 'u': case 'v':
            case 'w': case 'x': case 'y': case 'z':
                while ((c>='A' && c<='Z') || (c>='a' && c<='z')
                        || (c>='0' && c<='9') || c=='_' || c=='.') {
                    cBuf[cPos++]=(char)c;
                    c=getChar();
                }
                getNext=false;
                value=new String(cBuf,0,cPos);
                Integer tt=(Integer)wordToken.get(value);
                if (tt!=null) {
                    ttype=tt.intValue();
                } else {
                    if (c=='(') {
                        ttype=TT_FUNC;
                    } else if (c=='[') {
                        ttype=TT_ARRAY;
                    } else {
                        ttype=TT_WORD;
                    }
                }
                break;

            // Numbers
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                {
                boolean isDouble=false;
                while ((c>='0' && c<='9') || c=='.') {
                    if (c=='.') isDouble=true;
                    cBuf[cPos++]=(char)c;
                    c=getChar();
                }
                getNext=false;
                String str=new String(cBuf,0,cPos);
                if (isDouble) {
                    ttype=TT_DOUBLE;
                    value=Double.valueOf(str);
                } else {
                    ttype=TT_INTEGER;
                    value=new Long(Long.parseLong(str));
                }
                break;
                }

            // others
            case '+':
                ttype=TT_PLUS;
                break;
            case '-':
                ttype=TT_MINUS;
                break;
            case '*':
                ttype=TT_MULT;
                break;
            case '/':
                ttype=TT_DIV;
                break;
            case '^':
                ttype=TT_EXP;
                break;
            case '%':
                ttype=TT_MOD;
                break;
            case ':':
            	ttype=TT_THEN;
            	break;
            case '>':
                if (peekChar(1)=='=') {
                    getChar();
                    ttype=TT_LGRE;
                } else {
                    ttype=TT_LGR;
                }
                break;
            case '<':
                if (peekChar(1)=='=') {
                    getChar();
                    ttype=TT_LLSE;
                } else {
                    ttype=TT_LLS;
                }
                break;
            case '=':
                if (peekChar(1)=='=') {
                    getChar();
                    ttype=TT_LEQ;
                } else {
                    ttype=TT_EQ;
                }
                break;
            case '!':
                if (peekChar(1)=='=') {
                    getChar();
                    ttype=TT_LNEQ;
                } else {
                    ttype=TT_NOT;
                }
                break;
            default:
                if ((c=='|') &&( peekChar(1)=='|')) {
                    getChar();
                    ttype=TT_LOR;
                } else if ((c=='&') &&( peekChar(1)=='&')) {
                    getChar();
                    ttype=TT_LAND;
                } else {
                    ttype=c;
                }
            }
            if (getNext) c=getChar();
        }
        return ttype;
    }
    //Checks line for correctly formed ( ) and "
    //this is a little crude (i.e. the rdp should really pick it up)
    //but it's not all that good about it, hence somewhat kludgy fix
    // TODO: allow "'", '"'
    private void checkLine(String line) throws ParseError{
        boolean inQuotes=false;
        int brCount=0;
        char chars[] = line.toCharArray();
        int n;
        
        if (chars!=null) {
            for (n=0 ; n<chars.length ; n++) {
                switch(chars[n]) {
                    case '#': {
                        if(!inQuotes) {
                            n = chars.length;
                            break;
                        }
                    }
                    case '"':
                    case '\'':
                    {
                        if(inQuotes && chars[n-1]!='\\') {
                            inQuotes = false;
                        }
                        else if(n == 0 || chars[n-1]!='\\') {
                            inQuotes = true;
                        }
                        break;
                    }
                    case '(': {
                        brCount++;
                        break;
                    }
                    case ')': {
                        brCount--;
                        break;
                    }
                }
            }
            if (inQuotes){
                throw new ParseError("Mismatched quotes\n"+ inQuotes + new String (chars));
            }
            if (brCount!=0){
            	throw new ParseError("Mismatched brackets\n"+new String (chars));
            }
        }
        
    }
    
}

