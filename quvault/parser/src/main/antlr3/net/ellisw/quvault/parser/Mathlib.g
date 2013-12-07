grammar Mathlib;

options {
	output = AST;
	ASTLabelType = CommonTree;
}

tokens {
	SEMI = ';';
	COLON = ':';
	EL = '...';
	EPLUS = '.+';
	EMINUS = '.-';
	EMUL = '.*';
	EMUL_EQ = '.*=';
	EDIV = './';
	ELEFTDIV = '.\\';
	EQ = '=';
	MINUS = '-';
	MINUS_MINUS = '--';
	PLUS = '+';
	PLUS_PLUS = '++';
	MUL_EQ = '*=';
	DIV_EQ = '/=';
	LEFTDIV = '\\';
	LEFTDIV_EQ = '\\=';
	EDIV_EQ = './=';
	ELEFTDIV_EQ = '.\\=';
	OR_EQ = '|=';
	AND_EQ = '&=';
	LSHIFT_EQ = '<<=';
	RSHIFT_EQ = '>>=';
	EXPR_OR_OR = '||';
	EXPR_AND_AND = '&&';
	EXPR_OR = '|';
	EXPR_AND = '&';
	EXPR_LT = '<';
	EXPR_LE = '<=';
	EXPR_EQ = '==';
	EXPR_NE = '~=';
	EXPR_GE = '>=';
	EXPR_GT = '>';
	LSHIFT = '<<';
	RSHIFT = '>>';
	
	BREAK = 'break';
	CASE = 'case';
	CATCH = 'catch';
	CONTINUE = 'continue';
	DO = 'do';
	ELSE = 'else';
	ELSEIF = 'elseif';
	FOR = 'for';
	FCN = 'function';
	GLOBAL = 'global';
	IF = 'if';
	OTHERWISE = 'otherwise';
	STATIC = 'persistent';
	FUNC_RET = 'return';
	SWITCH = 'switch';
	TRY = 'try';
	UNTIL = 'until';
	UNWIND = 'unwind_protect';
	CLEANUP = 'unwind_protect_cleanup';
	WHILE = 'while';
}

@header { package net.ellisw.quvault.parser; }
@lexer::header { package net.ellisw.quvault.parser; }

@members {
	private String sErrors = "";
	
	public String getErrors() { return sErrors; }
	
	@Override
	public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
		sErrors += getErrorHeader(e) + " " + getErrorMessage(e, tokenNames) + "\n";
		super.displayRecognitionError(tokenNames, e);
	}
	
	@Override
	public String getErrorHeader(RecognitionException e)
	{
		String msg = "Line " + e.token.getLine() + ":" + (e.token.getCharPositionInLine() + 1) + ":";
		return msg;
	}
	
	@Override
	public String getErrorMessage(RecognitionException e, String[] tokenNames)
	{
		List stack = getRuleInvocationStack(e, this.getClass().getName());
		String msg = null;
		if (e instanceof NoViableAltException) {
			NoViableAltException nvae = (NoViableAltException)e;
			msg = "invalid character: " + getTokenErrorDisplay(e.token);
		}
		else {
			msg = super.getErrorMessage(e, tokenNames);
		}
		return msg;
	}
}


prog
	:	(SEMI! | NL!)* statementEOS*
	;
	
statementEOS
	:	s1=statement { if ($s1.tree != null) System.out.println($s1.tree.toStringTree()); }
		(SEMI|NL|EOF!)
		(SEMI|NL)*
	;
	catch [RecognitionException ex] {
		System.out.println("input error");
		reportError(ex);
		BitSet bitset = new BitSet();
		bitset.add(SEMI);
		bitset.add(NL);
		consumeUntil(input, bitset);
		input.consume();
	}

statement
	:	expr
	|	IDENT EQ^ NL!* expr
	;

expr	:	exprAdd;

/*
TODO	:	 These are levels of precedence that go before exprAdd
%right '=' ADD_EQ SUB_EQ MUL_EQ DIV_EQ LEFTDIV_EQ POW_EQ EMUL_EQ EDIV_EQ ELEFTDIV_EQ EPOW_EQ OR_EQ AND_EQ LSHIFT_EQ RSHIFT_EQ
%left EXPR_OR_OR
%left EXPR_AND_AND
%left EXPR_OR
%left EXPR_AND
%left EXPR_LT EXPR_LE EXPR_EQ EXPR_NE EXPR_GE EXPR_GT
%left LSHIFT RSHIFT
%left ':'
*/

exprAdd
	:	exprMult (('+'^ | '-'^ | EPLUS^ | EMINUS^) NL!* exprMult)*
	;

exprMult
	:	exprSign (('*'^ | '/'^ | LEFTDIV^ | EMUL^ | EDIV^ | ELEFTDIV^) NL!* exprSign)*
	;

exprSign
	:	('-'^ | '+'!)? exprUnary
	;

exprUnary
	:	('!'^ | '~'^) exprUnary
	|	exprTranspose (PLUS_PLUS^ | MINUS_MINUS^)?
	;

exprTranspose
	:	exprPow (QUOTE^)?
	;

exprPow
	:	atom ((POW^ | EPOW^) NL!* atom)?
	;

atom
	:	NUM
	|	IMAG_NUM
	|	IDENT
	|	'('! expr ')'!
	;

END	:	'end' | 'end_try_catch' | 'end_unwind_protect' | 'endfor' | 'endfunction' | 'endif' | 'endswitch' | 'endwhile';

fragment D:	'0'..'9';
WS	:	(' ' | '\t')+ { $channel = HIDDEN; };
NL	:	'\r'? '\n';
COMMENT	:	('#' | '%') (~'\n')* { $channel = HIDDEN; };
//NOT	:	'~' | '!';
ADD_EQ	:	'+=' | '.+=';
SUB_EQ	:	'-=' | '.-=';
POW	:	'**' | '^';
EPOW	:	'.' POW;
POW_EQ	:	POW EQ;
EPOW_EQ	:	EPOW EQ;
IDENT:	('_'|'$'|'a'..'z'|'A'..'Z')('_'|'$'|'a'..'z'|'A'..'Z'|'0'..'9')*;
fragment EXPON:	(('D'|'d'|'E'|'e') ('+'|'-')? D+);
//fragment NUMBER:	((D+ '.'? D* EXPON?) | ('.' D+ EXPON?) | ('0' ('x'|'X') ('0'..'9'|'a'..'f'|'A'..'F')+));
fragment NUMBER
	:	D+ EXPON?
	|	D+ '.' EXPON
	|	D+ '.' D+ EXPON?
	|	'.' D+ EXPON?
	|	'0' ('x'|'X') ('0'..'9'|'a'..'f'|'A'..'F')+
	;

fragment IM:	('i' | 'I' | 'j' | 'J');
IMAG_NUM:	NUMBER IM;
NUM	:	NUMBER;

QUOTE	:	'\'';
