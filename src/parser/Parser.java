package parser;

import Exceptions.SemanticException;
import lexer.*;
import semantic.Symbol;
import semantic.SymbolTable;

import java.io.IOException;
import java.util.Objects;

public class Parser {

    private final Lexer lex;

    private Token token;

    private final SymbolTable symbolTable = new SymbolTable();

    public void addReservedWord() throws SemanticException {
        symbolTable.addSymbol(new Symbol("if", "RESERVED"), Lexer.line);
        symbolTable.addSymbol(new Symbol("app", "RESERVED"), Lexer.line);
        symbolTable.addSymbol(new Symbol("var", "RESERVED"), Lexer.line);
        symbolTable.addSymbol(new Symbol("init", "RESERVED"), Lexer.line);
        symbolTable.addSymbol(new Symbol("return", "RESERVED"), Lexer.line);
        symbolTable.addSymbol(new Symbol("integer", "RESERVED"), Lexer.line);
        symbolTable.addSymbol(new Symbol("real", "RESERVED"), Lexer.line);
        symbolTable.addSymbol(new Symbol("else", "RESERVED"), Lexer.line);
        symbolTable.addSymbol(new Symbol("then", "RESERVED"), Lexer.line);
        symbolTable.addSymbol(new Symbol("end", "RESERVED"), Lexer.line);
        symbolTable.addSymbol(new Symbol("repeat", "RESERVED"), Lexer.line);
        symbolTable.addSymbol(new Symbol("until", "RESERVED"), Lexer.line);
        symbolTable.addSymbol(new Symbol("read", "RESERVED"), Lexer.line);
        symbolTable.addSymbol(new Symbol("write", "RESERVED"), Lexer.line);
    }

    public Parser(Lexer l) throws IOException {
        lex = l;
        move();
    }

    void move() throws IOException{
        token = lex.scan();
    }

    void error(String s) {
        throw new Error("near line " + Lexer.line + ": " + s);
    }

    void eat(int t) throws IOException{
        if(token.tag == t){
            move();
        } else {
            error("syntax error");
        }
    }

    // Regras da gramática
    public void program() throws IOException {
        addReservedWord();
        if(token.tag == Tag.APP) {
            eat(Tag.APP);
            String varName = ((Word) token).getLexeme();
            symbolTable.addSymbol(new Symbol(varName, "RESERVED"), Lexer.line);
            identifier();
            body();
            symbolTable.print();
        } else {
            error("Syntax error: Expect 'APP', but found " + Tag.getTagName(token.tag));
        }
    }

    void body() throws IOException {
        if (token.tag == Tag.VAR || token.tag == Tag.INIT) {
            if (token.tag == Tag.VAR) {
                eat(Tag.VAR);
                decl_list();
            }
            eat(Tag.INIT);
            stmt_list();
            eat(Tag.RETURN);
            if(token != null) {
                error("Syntax error: Expect end of file, but found " + Tag.getTagName(token.tag));
            }
        } else {
            error("Syntax error: Expect 'VAR' or 'INIT', but found " + Tag.getTagName(token.tag));
        }
    }

    void decl_list() throws IOException {
        switch(token.tag) {
            case Tag.INTEGER:
            case Tag.REAL:
                decl();
                while ((char) token.tag == ';') {
                    eat(';');
                    decl();
                }
                break;
            default:
                error("Syntax error: Expect 'INTEGER' or 'REAL', but found " + Tag.getTagName(token.tag));
        }
    }

    void decl() throws IOException {
        switch(token.tag) {
            case Tag.INTEGER:
            case Tag.REAL:
                String varType = type();
                ident_list(varType);
                break;
            default:
                error("Syntax error: Expect 'INTEGER' or 'REAL', but found " + Tag.getTagName(token.tag));
        }
    }

    void ident_list(String varType) throws IOException {
        if (token.tag == Tag.ID) {
            String varName = ((Word) token).getLexeme();
            symbolTable.addSymbol(new Symbol(varName, varType), Lexer.line);
            identifier();
            while ((char) token.tag == ',') {
                eat(',');
                varName = ((Word) token).getLexeme();
                symbolTable.addSymbol(new Symbol(varName, varType), Lexer.line);
                identifier();
            }
        }
        else {
            error("Syntax error: Expect 'ID', but found " + Tag.getTagName(token.tag));
        }
    }

    String type() throws IOException {
        switch (token.tag){
            case Tag.INTEGER:
                eat(Tag.INTEGER);
                return Tag.getTagName(Tag.INTEGER).toString();
            case Tag.REAL:
                eat(Tag.REAL);
                return Tag.getTagName(Tag.REAL).toString();
            default:
                error("Syntax error: Expect 'INTEGER' or 'REAL', but found " + Tag.getTagName(token.tag));
        }
        return null;
    }

    void stmt_list() throws IOException {
        switch(token.tag) {
            case Tag.ID:
            case Tag.IF:
            case Tag.REPEAT:
            case Tag.READ:
            case Tag.WRITE:
                stmt();
                while ((char) token.tag == ';') {
                    eat(';');
                    stmt();
                }
                break;
            default:
                error("Syntax error: Expect 'ID', 'IF', 'REPEAT', 'READ' or 'WRITE', but found " + Tag.getTagName(token.tag));
        }
    }

    void stmt() throws IOException {
        switch (token.tag) {
            case Tag.ID:
                assign_stmt();
                break;
            case Tag.IF:
                if_stmt();
                break;
            case Tag.REPEAT:
                repeat_stmt();
                break;
            case Tag.READ:
                read_stmt();
                break;
            case Tag.WRITE:
                write_stmt();
                break;
            default:
                error("Syntax error: Expect 'ID', 'IF', 'REPEAT', 'READ' or 'WRITE', but found " + Tag.getTagName(token.tag));
        }
    }

    void assign_stmt() throws IOException {
        if (token.tag == Tag.ID) {
            String varName = ((Word) token).getLexeme();
            identifier();
            Symbol symbol = symbolTable.getSymbol(varName, Lexer.line);
            if (symbol.getType() == null || Objects.equals(symbol.getType(), "RESERVED")) {
                error(String.format("Semantic error: Variable %s not declared or is reserved word", varName));
            }
            eat(Tag.DEFINED_AS);
            String simpleExprType = simple_expr();
            if (simpleExprType != null && !simpleExprType.equals(symbol.getType())) {
                if(!(symbol.getType().equals("REAL") && simpleExprType.equals("INTEGER")))
                    error(String.format("Semantic error: Type mismatch! Expected %s but found %s", symbol.getType(), simpleExprType));
            }
        }
        else {
            error("Syntax error: Expect 'ID', but found " + Tag.getTagName(token.tag));
        }
    }

    void if_stmt() throws IOException {
        if (token.tag == Tag.IF) {
            eat(Tag.IF);
            String conditionType = condition();
            eat(Tag.THEN);
            stmt_list();
            if_stmt_();
            if (conditionType != null && !conditionType.equals("BOOLEAN")) {
                error(String.format("Semantic error: Type mismatch! Expected %s but found %s", "BOOLEAN", conditionType));
            }
        } else {
            error("Syntax error: Expect 'IF', but found " + Tag.getTagName(token.tag));
        }
    }

    void if_stmt_() throws IOException {
        if(token.tag == Tag.ELSE) {
            eat(Tag.ELSE);
            stmt_list();
            eat(Tag.END);
        } else if(token.tag == Tag.END) {
            eat(Tag.END);
        } else {
            error("Syntax error: Expect 'ELSE' or 'END', but found " + Tag.getTagName(token.tag));
        }
    }

    void repeat_stmt() throws IOException {
        if (token.tag == Tag.REPEAT) {
            eat(Tag.REPEAT);
            stmt_list();
            String stmtSuffixType = stmt_suffix();
            if(stmtSuffixType != null && !stmtSuffixType.equals("BOOLEAN")) {
                error(String.format("Semantic error: Type mismatch! Expected BOOLEAN but found %s", stmtSuffixType));
            }
        } else {
            error("Syntax error: Expect 'REPEAT', but found " + Tag.getTagName(token.tag));
        }
    }

    String stmt_suffix() throws IOException {
        if(token.tag == Tag.UNTIL) {
            eat(Tag.UNTIL);
            return condition();
        } else {
            error("Syntax error: Expect 'UNTIL', but found " + Tag.getTagName(token.tag));
            return null;
        }
    }

    void read_stmt() throws IOException {
        if(token.tag == Tag.READ) {
            eat(Tag.READ);
            eat('(');
            identifier();
            eat(')');
        } else {
            error("Syntax error: Expect 'READ', but found " + Tag.getTagName(token.tag));
        }
    }

    void write_stmt() throws IOException {
        if (token.tag == Tag.WRITE) {
            eat(Tag.WRITE);
            eat('(');
            writable();
            eat(')');
        } else {
            error("Syntax error: Expect 'WRITE', but found " + Tag.getTagName(token.tag));
        }
    }

    void writable() throws IOException {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL ||
                (char) token.tag == '(' || (char) token.tag == '!' || (char) token.tag == '-') {
            simple_expr();
        } else if (token.tag == Tag.LITERAL) {
            literal();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', '-', '*', '/', '&&', '+', '||', or 'LITERAL', but found " + Tag.getTagName(token.tag));
        }
    }

    String condition() throws IOException {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL ||
                (char) token.tag == '(' || (char) token.tag == '!' || (char) token.tag == '-')  {
            String expressionType = expression();
            if (expressionType != null && !expressionType.equals("BOOLEAN")) {
                error(String.format("Semantic error: Type mismatch! Expected BOOLEAN but found %s", expressionType));
            }
            return expressionType;
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', '-', '*', '/', or '&&', but found " + Tag.getTagName(token.tag));
            return null;
        }
    }

    String expression() throws IOException {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL ||
                (char) token.tag == '(' || (char) token.tag == '!' || (char) token.tag == '-') {
            String simpleExprType = simple_expr();
            String expressionType = expression_(simpleExprType);
            if(Objects.equals(expressionType, "BOOLEAN")) {
                return expressionType;
            }
            if (expressionType != null && !expressionType.equals(simpleExprType)) {
                error(String.format("Semantic error: Type mismatch! Expected %s but found %s", simpleExprType, expressionType));
                return null;
            }
            return simpleExprType;
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', '-', '*', '/', or '&&', but found " + Tag.getTagName(token.tag));
            return null;
        }
    }

    String expression_(String expressionType) throws IOException {
        if (token.tag == Tag.RETURN || (char) token.tag == ';' || (char) token.tag == ')' || token.tag == Tag.THEN) {
            return null;
        } else if ((char) token.tag == '=' || (char) token.tag == '>' || token.tag == Tag.BIG_EQUAL ||
                (char) token.tag == '<' || token.tag == Tag.LEAST_EQUAL || token.tag == Tag.NOT_EQUAL) {
            relop();
            String simpleExprType = simple_expr();
            if (expressionType != null && !expressionType.equals(simpleExprType)) {
                error(String.format("Semantic error: Type mismatch! Expected %s but found %s", expressionType, simpleExprType));
                return null;
            }
            return "BOOLEAN";
        } else {
            error("Syntax error: Expect '=', '>', '<', '>=', '<=', or '!=', but found " + Tag.getTagName(token.tag));
            return null;
        }
    }

    String simple_expr() throws IOException {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL ||
                (char) token.tag == '(' || (char) token.tag == '!' || (char) token.tag == '-') {
            String termType = term();
            String simpleExpr_Type = simple_expr_();
            if (simpleExpr_Type != null && !simpleExpr_Type.equals(termType)) {
                if((termType.equals("REAL") && simpleExpr_Type.equals("INTEGER")) ||
                        (termType.equals("INTEGER") && simpleExpr_Type.equals("REAL"))){
                    return "REAL";
                }
                error(String.format("Semantic error: Type mismatch! Expected %s but found %s", termType, simpleExpr_Type));
                return null;
            }
            return termType;
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!' or '-' , but found " + Tag.getTagName(token.tag));
            return null;
        }
    }

    String simple_expr_() throws IOException {
        if ((token.tag == Tag.RETURN || (char) token.tag == ';') || (char) token.tag == '=' ||
                (char) token.tag == '>' || token.tag == Tag.BIG_EQUAL || (char) token.tag == '<' ||
                token.tag == Tag.LEAST_EQUAL || token.tag == Tag.NOT_EQUAL || token.tag == Tag.THEN ||
                (char) token.tag == ')' || token.tag == Tag.UNTIL || token.tag == Tag.ELSE || token.tag == Tag.END) {
                return null;
        } else if ((char) token.tag == '+' || (char) token.tag == '-' || token.tag == Tag.OR) {
            addop();
            String termType = term();
            String simpleExpr_Type = simple_expr_();
            if(simpleExpr_Type != null && !simpleExpr_Type.equals(termType)) {
                if((termType.equals("REAL") && simpleExpr_Type.equals("INTEGER")) ||
                        (termType.equals("INTEGER") && simpleExpr_Type.equals("REAL"))){
                    return "REAL";
                }
                error(String.format("Semantic error: Type mismatch! Expected %s but found %s", termType, simpleExpr_Type));
                return null;
            }
            return termType;
        } else {
            error("Syntax error: Expect '+', '-', or '||', but found " + Tag.getTagName(token.tag));
            return null;
        }
    }

    String term() throws IOException {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL ||
                (char) token.tag == '(' || (char) token.tag == '!' || (char) token.tag == '-') {
            String factorAType = factor_a();
            String term_Type = term_();
            if(term_Type != null && !term_Type.equals(factorAType)) {
                if((factorAType.equals("REAL") && term_Type.equals("INTEGER")) ||
                        (factorAType.equals("INTEGER") && term_Type.equals("REAL"))){
                    return "REAL";
                }
                error(String.format("Semantic error: Type mismatch! Expected %s but found %s", factorAType, term_Type));
            }
            return factorAType;
        } else {
            error(String.format("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!' or '-', but found " + Tag.getTagName(token.tag)));
            return null;
        }
    }

    String term_() throws IOException {
        if ((char) token.tag == '+' || (char) token.tag == '-' || token.tag == Tag.OR ||
                token.tag == Tag.RETURN || (char) token.tag == ';' || (char) token.tag == '=' ||
                (char) token.tag == '>' || token.tag == Tag.BIG_EQUAL ||  (char) token.tag == '<' ||
                token.tag == Tag.LEAST_EQUAL || token.tag == Tag.NOT_EQUAL || token.tag == Tag.THEN ||
                (char) token.tag == ')' || token.tag == Tag.UNTIL || token.tag == Tag.ELSE || token.tag == Tag.END) {
                return null;
        } else if ((char) token.tag == '*' || (char) token.tag == '/' || token.tag == Tag.AND) {
            mulop();
            String factorAType = factor_a();
            String term_Type = term_();
            if(term_Type != null && !term_Type.equals(factorAType)) {
                error(String.format("Semantic error: Type mismatch! Expected %s but found %s", factorAType, term_Type));
                return null;
            }
            return factorAType;
        } else {
            error("Syntax error: Expect '*', '/', or '&&', but found " + Tag.getTagName(token.tag));
            return null;
        }
    }

    String factor_a() throws IOException {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL || (char) token.tag == '(') {
            return factor();
        } else if ((char) token.tag == '!') {
            eat('!');
            return factor();
        } else if ((char) token.tag == '-') {
            eat('-');
            return factor();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', or '-', but found " + Tag.getTagName(token.tag));
            return null;
        }
    }

    String factor() throws IOException {
        if (token.tag == Tag.ID) {
            return identifier();
        } else if (token.tag == Tag.NUM || token.tag == Tag.REAL) {
            return constant();
        } else if ((char) token.tag == '(') {
            eat('(');
            String expressionType = expression();
            eat(')');
            return expressionType;
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', or '(', but found " + Tag.getTagName(token.tag));
            return null;
        }
    }

    void relop() throws IOException {
        if ((char) token.tag == '=') {
            eat('=');
        } else if ((char) token.tag == '>') {
            eat('>');
        } else if (token.tag == Tag.BIG_EQUAL) {
            eat(Tag.BIG_EQUAL);
        } else if ((char) token.tag == '<') {
            eat('<');
        } else if (token.tag == Tag.LEAST_EQUAL) {
            eat(Tag.LEAST_EQUAL);
        } else if (token.tag == Tag.NOT_EQUAL) {
            eat(Tag.NOT_EQUAL);
        } else {
            error("Syntax error: Expect '=', '>', or '<', but found " + Tag.getTagName(token.tag));
        }
    }

    void addop() throws IOException {
        if ((char) token.tag == '+') {
            eat('+');
        } else if ((char) token.tag == '-') {
            eat('-');
        } else if (token.tag == Tag.OR) {
            eat(Tag.OR);
        } else {
            error("Syntax error: Expect '+', '-', or '||', but found " + Tag.getTagName(token.tag));
        }
    }

    void mulop() throws IOException {
        if ((char) token.tag == '*') {
            eat('*');
        } else if ((char) token.tag == '/') {
            eat('/');
        } else if (token.tag == Tag.AND) {
            eat(Tag.AND);
        } else {
            error("Syntax error: Expect '*', '/', or '&&', but found " + Tag.getTagName(token.tag));
        }
    }

    String constant() throws IOException {
        return switch (token.tag) {
            case Tag.NUM -> integer_const();
            case Tag.REAL -> float_const();
            default -> {
                error("Syntax error: Expect 'NUM' or 'REAL', but found " + Tag.getTagName(token.tag));
                yield null;
            }
        };
    }

    String integer_const() throws IOException {
            if (token.tag == Tag.NUM) {
            eat(Tag.NUM);
            return Tag.getTagName(Tag.INTEGER).toString();
        } else {
            error("Syntax error: Expect 'NUM', but found " + Tag.getTagName(token.tag));
            return null;
        }
    }

    String float_const() throws IOException {
        if (token.tag == Tag.REAL) {
            eat(Tag.REAL);
            return Tag.getTagName(Tag.REAL).toString();
        } else {
            error("Syntax error: Expect 'REAL', but found " + Tag.getTagName(token.tag));
            return null;
        }
    }

    void literal() throws IOException {
        if (token.tag == Tag.LITERAL) {
            eat(Tag.LITERAL);
        } else {
            error("Syntax error: Expect 'LITERAL', but found " + Tag.getTagName(token.tag));
        }
    }

    String identifier() throws IOException {
        if (token.tag == Tag.ID) {
            String identifierType = symbolTable.getSymbol(((Word) token).getLexeme(), Lexer.line).getType();
            eat(Tag.ID);
            return identifierType;
        } else {
            error("Syntax error: Expect 'ID', but found " + Tag.getTagName(token.tag));
            return null;
        }
    }
}