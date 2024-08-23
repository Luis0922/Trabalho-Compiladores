package parser;

import lexer.*;

import java.io.IOException;

public class Parser {

    private Lexer lex;

    private Token token;

    int used = 0;

    public Parser(Lexer l) throws IOException {
        lex = l;
        move();
    }

    void move() throws IOException{
        if(token != null){
            System.out.println(Tag.getTagName(token.tag));
        }
        token = lex.scan();
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
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
        if(token.tag == Tag.APP) {
            eat(Tag.APP);
            identifier();
            body();
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
                type();
                ident_list();
                break;
            default:
                error("Syntax error: Expect 'INTEGER' or 'REAL', but found " + Tag.getTagName(token.tag));
        }
    }

    void ident_list() throws IOException {
        if (token.tag == Tag.ID) {
            identifier();
            while ((char) token.tag == ',') {
                eat(',');
                identifier();
            }
        }
        else {
            error("Syntax error: Expect 'ID', but found " + Tag.getTagName(token.tag));
        }
    }

    void type() throws IOException {
        switch (token.tag){
            case Tag.INTEGER:
                eat(Tag.INTEGER);
                break;
            case Tag.REAL:
                eat(Tag.REAL);
                break;
            default:
                error("Syntax error: Expect 'INTEGER' or 'REAL', but found " + Tag.getTagName(token.tag));
        }
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
            identifier();
            eat(Tag.DEFINED_AS);
            simple_expr();
        }
        else {
            error("Syntax error: Expect 'ID', but found " + Tag.getTagName(token.tag));
        }
    }

    void if_stmt() throws IOException {
        if (token.tag == Tag.IF) {
            eat(Tag.IF);
            condition();
            eat(Tag.THEN);
            stmt_list();
            if_stmt_();
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
            stmt_suffix();
        } else {
            error("Syntax error: Expect 'REPEAT', but found " + Tag.getTagName(token.tag));
        }

    }

    void stmt_suffix() throws IOException {
        if(token.tag == Tag.UNTIL) {
            eat(Tag.UNTIL);
            condition();
        } else {
            error("Syntax error: Expect 'UNTIL', but found " + Tag.getTagName(token.tag));
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

    void condition() throws IOException {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL ||
                (char) token.tag == '(' || (char) token.tag == '!' || (char) token.tag == '-')  {
            expression();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', '-', '*', '/', or '&&', but found " + Tag.getTagName(token.tag));
        }
    }

    void expression() throws IOException {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL ||
                (char) token.tag == '(' || (char) token.tag == '!' || (char) token.tag == '-') {
            simple_expr();
            expression_();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', '-', '*', '/', or '&&', but found " + Tag.getTagName(token.tag));
        }
    }

    void expression_() throws IOException {
        if (token.tag == Tag.RETURN || (char) token.tag == ';' || (char) token.tag == ')' || token.tag == Tag.THEN) {

        } else if ((char) token.tag == '=' || (char) token.tag == '>' || token.tag == Tag.BIG_EQUAL ||
                (char) token.tag == '<' || token.tag == Tag.LEAST_EQUAL || token.tag == Tag.NOT_EQUAL) {
            relop();
            simple_expr();
        } else {
            error("Syntax error: Expect '=', '>', '<', '>=', '<=', or '!=', but found " + Tag.getTagName(token.tag));
        }
    }

    void simple_expr() throws IOException {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL ||
                (char) token.tag == '(' || (char) token.tag == '!' || (char) token.tag == '-') {
            term();
            simple_expr_();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!' or '-' , but found " + Tag.getTagName(token.tag));
        }
    }

    void simple_expr_() throws IOException {
        if ((token.tag == Tag.RETURN || (char) token.tag == ';') || (char) token.tag == '=' ||
                (char) token.tag == '>' || token.tag == Tag.BIG_EQUAL || (char) token.tag == '<' ||
                token.tag == Tag.LEAST_EQUAL || token.tag == Tag.NOT_EQUAL || token.tag == Tag.THEN ||
                (char) token.tag == ')' || token.tag == Tag.UNTIL) {

        } else if ((char) token.tag == '+' || (char) token.tag == '-' || token.tag == Tag.OR) {
            addop();
            term();
            simple_expr_();
        } else {
            error("Syntax error: Expect '+', '-', or '||', but found " + Tag.getTagName(token.tag));
        }
    }

    void term() throws IOException {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL ||
                (char) token.tag == '(' || (char) token.tag == '!' || (char) token.tag == '-') {
            factor_a();
            term_();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!' or '-', but found " + Tag.getTagName(token.tag));
        }
    }

    void term_() throws IOException {
        if ((char) token.tag == '+' || (char) token.tag == '-' || token.tag == Tag.OR ||
                token.tag == Tag.RETURN || (char) token.tag == ';' || (char) token.tag == '=' ||
                (char) token.tag == '>' || token.tag == Tag.BIG_EQUAL ||  (char) token.tag == '<' ||
                token.tag == Tag.LEAST_EQUAL || token.tag == Tag.NOT_EQUAL || token.tag == Tag.THEN ||
                (char) token.tag == ')' || token.tag == Tag.UNTIL) {

        } else if ((char) token.tag == '*' || (char) token.tag == '/' || token.tag == Tag.AND) {
            mulop();
            factor_a();
            term_();
        } else {
            error("Syntax error: Expect '*', '/', or '&&', but found " + Tag.getTagName(token.tag));
        }
    }

    void factor_a() throws IOException {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL || (char) token.tag == '(') {
            factor();
        } else if ((char) token.tag == '!') {
            eat('!');
            factor();
        } else if ((char) token.tag == '-') {
            eat('-');
            factor();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', or '-', but found " + Tag.getTagName(token.tag));
        }
    }

    void factor() throws IOException {
        if (token.tag == Tag.ID) {
            identifier();
        } else if (token.tag == Tag.NUM || token.tag == Tag.REAL) {
            constant();
        } else if ((char) token.tag == '(') {
            eat('(');
            expression();
            eat(')');
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', or '(', but found " + Tag.getTagName(token.tag));
        }
    }

    void relop() throws IOException {
        if ((char) token.tag == '=') {
            eat('=');
        } else if ((char) token.tag == '>') {
            eat('>');
        } else if (token.tag == Tag.BIG_EQUAL) {
            eat('>');
            move();
            eat('=');
        } else if ((char) token.tag == '<') {
            eat('<');
        } else if (token.tag == Tag.LEAST_EQUAL) {
            eat('<');
            move();
            eat('=');
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

    void constant() throws IOException {
        switch (token.tag) {
            case Tag.NUM:
                integer_const();
                break;
            case Tag.REAL:
                float_const();
                break;
            default:
                error("Syntax error: Expect 'NUM' or 'REAL', but found " + Tag.getTagName(token.tag));
        }
    }

    void integer_const() throws IOException {
        if (token.tag == Tag.NUM) {
            eat(Tag.NUM);
        } else {
            error("Syntax error: Expect 'NUM', but found " + Tag.getTagName(token.tag));
        }
    }

    void float_const() throws IOException {
        if (token.tag == Tag.REAL) {
            eat(Tag.REAL);
        } else {
            error("Syntax error: Expect 'REAL', but found " + Tag.getTagName(token.tag));
        }
    }

    void literal() throws IOException {
        if (token.tag == Tag.LITERAL) {
            eat(Tag.LITERAL);
        } else {
            error("Syntax error: Expect 'LITERAL', but found " + Tag.getTagName(token.tag));
        }
    }

    void identifier() throws IOException {
        if (token.tag == Tag.ID) {
            eat(Tag.ID);
        } else {
            error("Syntax error: Expect 'ID', but found " + Tag.getTagName(token.tag));
        }
    }
}