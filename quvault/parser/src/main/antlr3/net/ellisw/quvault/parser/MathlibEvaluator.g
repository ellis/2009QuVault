tree grammar MathlibEvaluator;

options {
	tokenVocab = Mathlib;
	ASTLabelType = CommonTree;
}

@header {
package net.ellisw.quvault.parser;

import java.util.ArrayList;
import java.util.HashMap;
}

@members {
	HashMap<String, MathlibValue> memory = new HashMap<String, MathlibValue>();
	ArrayList<String> resultVarNames = new ArrayList<String>();
	private boolean bPrint;
	private String sErrors = "";
	
	public String getErrors() { return sErrors; }
	
	public MathlibValue getVar(String sVarName) {
		return memory.get(sVarName);
	}
	
	public void setVar(String sVarName, MathlibValue value) {
		memory.put(sVarName, value);
	}
	
	private void printResult() {
		if (bPrint) {
			for (String sVarName : resultVarNames) {
				MathlibValue value = memory.get(sVarName);
				System.out.println(sVarName + " =" + value);
			}
		}
		bPrint = false;
	}
	
	@Override
	public String getErrorHeader(RecognitionException e)
	{
		String msg = "Line " + e.token.getLine() + ":" + (e.token.getCharPositionInLine() + 1) + ":";
		return msg;
	}

	@Override
	public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
		sErrors += getErrorHeader(e) + " " + getErrorMessage(e, tokenNames) + "\n";
		super.displayRecognitionError(tokenNames, e);
	}
}


input
	:	 statement0+  { printResult(); }
	;

statement0
	:	statement { bPrint = true; }
	|	';' { bPrint = false; }
	|	NL { printResult(); }
	;

statement
	:	expr
		{
		resultVarNames.clear();
		resultVarNames.add("ans");
		setVar("ans", $expr.value.getValueForAssignment());
		}
	|	^(EQ IDENT expr) 
		{
		resultVarNames.clear();
		resultVarNames.add($IDENT.text);
		setVar($IDENT.text, $expr.value.getValueForAssignment());
		}
	;

expr returns [MathlibValue value]
	: ^(('+'|'.+') a=expr (b=expr)?) {$value = (b == null) ? a : a.add(b);}
	| ^('-' a=expr (b=expr)?) {$value = (b == null) ? a.negative() : a.sub(b);}
	| ^('.-' a=expr b=expr) {$value = a.sub(b);}
	| ^('*' a=expr b=expr) {$value = a.mult(b);}
	| ^('/' a=expr b=expr) {$value = a.div(b);}
	| ^(('!'|'~') a=expr) {$value = a.not();}
	| ^('^' a=expr b=expr) {$value = a.pow(b);}
	| ^('**' a=expr b=expr) {$value = a.pow(b);}
	| NUM {$value = MathlibValue.parse($NUM.text);}
	| IMAG_NUM {$value = MathlibValue.parse($IMAG_NUM.text);}
	| IDENT
	{
		MathlibValue v = memory.get($IDENT.text);
		if (v != null)
			$value = new MathlibValue(v);
		else if ($IDENT.text.equals("i") || $IDENT.text.equals("I") || $IDENT.text.equals("j") || $IDENT.text.equals("J"))
			$value = MathlibValue.createScalar(0, 1);
		else {
			int nLine = $IDENT.token.getLine();
			int nCol = $IDENT.token.getCharPositionInLine() + 1;
			sErrors += "Line " + nLine + ":" + nCol + ": Undefined variable or function: " + $IDENT.text + "\n";
			$value = MathlibValue.createNull();
		}
	}
	;
