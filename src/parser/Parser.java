package parser;

import lexer.*;

public class Parser {

    private Lexer lex;

    private Token look;

    int used = 0;

    public Parser(Lexer l) throws Exception{
        lex = l;
        move();
    }

    void move() throws Exception{
        if(look != null){
            System.out.println(Tag.getTagName(look.tag));
        }
        look = lex.scan();
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) throws Exception{
        if(look.tag == t){
            move();
        } else {
            error("syntax error");
        }
    }

    // Regras da gramática
    public void program() throws Exception {
        if(look.tag == Tag.APP) {
            match(Tag.APP);
            identifier();
            body();
        } else {
            error("Syntax error: Expect 'APP', but found " + Tag.getTagName(look.tag));
        }
    }

    void body() throws Exception {
        if (look.tag == Tag.VAR || look.tag == Tag.INIT) {
            if (look.tag == Tag.VAR) {
                match(Tag.VAR);
                decl_list();
            }
            match(Tag.INIT);
            stmt_list();
            match(Tag.RETURN);
        } else {
            error("Syntax error: Expect 'VAR' or 'INIT', but found " + Tag.getTagName(look.tag));
        }

    }

    void decl_list() throws Exception {
        switch(look.tag) {
            case Tag.INTEGER:
            case Tag.REAL:
                decl();
                while ((char) look.tag == ';') {
                    match(';');
                    decl();
                }
                break;
            default:
                error("Syntax error: Expect 'INTEGER' or 'REAL', but found " + Tag.getTagName(look.tag));
        }
    }

    void decl() throws Exception {
        switch(look.tag) {
            case Tag.INTEGER:
            case Tag.REAL:
                type();
                ident_list();
                break;
            default:
                error("Syntax error: Expect 'INTEGER' or 'REAL', but found " + Tag.getTagName(look.tag));
        }
    }

    void ident_list() throws Exception {
        if (look.tag == Tag.ID) {
            identifier();
            while ((char) look.tag == ',') {
                match(',');
                identifier();
            }
        }
        else {
            error("Syntax error: Expect 'ID', but found " + Tag.getTagName(look.tag));
        }
    }

    void type() throws Exception {
        switch (look.tag){
            case Tag.INTEGER:
                match(Tag.INTEGER);
                break;
            case Tag.REAL:
                match(Tag.REAL);
                break;
            default:
                error("Syntax error: Expect 'INTEGER' or 'REAL', but found " + Tag.getTagName(look.tag));
        }
    }

    void stmt_list() throws Exception {
        switch(look.tag) {
            case Tag.ID:
            case Tag.IF:
            case Tag.REPEAT:
            case Tag.READ:
            case Tag.WRITE:
                stmt();
                while ((char) look.tag == ';') {
                    match(';');
                    stmt();
                }
                break;
            default:
                error("Syntax error: Expect 'ID', 'IF', 'REPEAT', 'READ' or 'WRITE', but found " + Tag.getTagName(look.tag));
        }
    }

    void stmt() throws Exception {
        switch (look.tag) {
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
                error("Syntax error: Expect 'ID', 'IF', 'REPEAT', 'READ' or 'WRITE', but found " + Tag.getTagName(look.tag));
        }
    }

    void assign_stmt() throws Exception {
        if (look.tag == Tag.ID) {
            identifier();
            match(Tag.DEFINED_AS);
            simple_expr();
        }
        else {
            error("Syntax error: Expect 'ID', but found " + Tag.getTagName(look.tag));
        }
    }

    void if_stmt() throws Exception {
        if (look.tag == Tag.IF) {
            match(Tag.IF);
            condition();
            match(Tag.THEN);
            stmt_list();
            if_stmt_();
        } else {
            error("Syntax error: Expect 'IF', but found " + Tag.getTagName(look.tag));
        }
    }

    void if_stmt_() throws Exception {
        if(look.tag == Tag.ELSE) {
            match(Tag.ELSE);
            stmt_list();
            match(Tag.END);
        } else if(look.tag == Tag.END) {
            match(Tag.END);
        } else {
            error("Syntax error: Expect 'ELSE' or 'END', but found " + Tag.getTagName(look.tag));
        }
    }

    void repeat_stmt() throws Exception {
        if (look.tag == Tag.REPEAT) {
            match(Tag.REPEAT);
            stmt_list();
            stmt_suffix();
        } else {
            error("Syntax error: Expect 'REPEAT', but found " + Tag.getTagName(look.tag));
        }

    }

    void stmt_suffix() throws Exception {
        if(look.tag == Tag.UNTIL) {
            match(Tag.UNTIL);
            condition();
        } else {
            error("Syntax error: Expect 'UNTIL', but found " + Tag.getTagName(look.tag));
        }
    }

    void read_stmt() throws Exception {
        if(look.tag == Tag.READ) {
            match(Tag.READ);
            match('(');
            identifier();
            match(')');
        } else {
            error("Syntax error: Expect 'READ', but found " + Tag.getTagName(look.tag));
        }
    }

    void write_stmt() throws Exception {
        if (look.tag == Tag.WRITE) {
            match(Tag.WRITE);
            match('(');
            writable();
            match(')');
        } else {
            error("Syntax error: Expect 'WRITE', but found " + Tag.getTagName(look.tag));
        }
    }

    void writable() throws Exception {
        if (look.tag == Tag.ID || look.tag == Tag.NUM || look.tag == Tag.REAL ||
                (char) look.tag == '(' || (char) look.tag == '!' || (char) look.tag == '-') {
            simple_expr_();
        } else if (look.tag == Tag.LITERAL) {
            literal();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', '-', '*', '/', '&&', '+', '||', or 'LITERAL', but found " + Tag.getTagName(look.tag));
        }
    }

    void condition() throws Exception {
        if (look.tag == Tag.ID || look.tag == Tag.NUM || look.tag == Tag.REAL ||
                (char) look.tag == '(' || (char) look.tag == '!' || (char) look.tag == '-')  {
            expression();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', '-', '*', '/', or '&&', but found " + Tag.getTagName(look.tag));
        }
    }

    void expression() throws Exception {
        if (look.tag == Tag.ID || look.tag == Tag.NUM || look.tag == Tag.REAL ||
                (char) look.tag == '(' || (char) look.tag == '!' || (char) look.tag == '-') {
            simple_expr();
            expression_();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', '-', '*', '/', or '&&', but found " + Tag.getTagName(look.tag));
        }
    }

    void expression_() throws Exception {
        if (look.tag == Tag.RETURN || (char) look.tag == ';') {

        } else if ((char) look.tag == '=' || (char) look.tag == '>' || look.tag == Tag.BIG_EQUAL ||
                (char) look.tag == '<' || look.tag == Tag.LEAST_EQUAL || look.tag == Tag.NOT_EQUAL) {
            relop();
            simple_expr_();
            expression_();
        } else {
            error("Syntax error: Expect '=', '>', '<', '>=', '<=', or '!=', but found " + Tag.getTagName(look.tag));
        }
    }

    void simple_expr() throws Exception {
        if (look.tag == Tag.ID || look.tag == Tag.NUM || look.tag == Tag.REAL ||
                (char) look.tag == '(' || (char) look.tag == '!' || (char) look.tag == '-') {
            term();
            simple_expr_();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!' or '-' , but found " + Tag.getTagName(look.tag));
        }
    }

    void simple_expr_() throws Exception {
        if ((look.tag == Tag.RETURN || (char) look.tag == ';')) {

        } else if ((char) look.tag == '+' || (char) look.tag == '-' || look.tag == Tag.OR) {
            addop();
            term();
        } else {
            error("Syntax error: Expect '+', '-', or '||', but found " + Tag.getTagName(look.tag));
        }
    }

    void term() throws Exception {
        if (look.tag == Tag.ID || look.tag == Tag.NUM || look.tag == Tag.REAL ||
                (char) look.tag == '(' || (char) look.tag == '!' || (char) look.tag == '-') {
            factor_a();
            term_();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', '-', '*', '/', or '&&', but found " + Tag.getTagName(look.tag));
        }
    }

    void term_() throws Exception {
        if ((char) look.tag == '+' || (char) look.tag == '-' || look.tag == Tag.OR ||
                look.tag == Tag.RETURN || (char) look.tag == ';') {

        } else if ((char) look.tag == '*' || (char) look.tag == '/' || look.tag == Tag.AND) {
            mulop();
            factor_a();
        } else {
            error("Syntax error: Expect '*', '/', or '&&', but found " + Tag.getTagName(look.tag));
        }
    }

    void factor_a() throws Exception {
        if (look.tag == Tag.ID || look.tag == Tag.NUM || look.tag == Tag.REAL || (char) look.tag == '(') {
            factor();
        } else if ((char) look.tag == '!') {
            match('!');
            factor();
        } else if ((char) look.tag == '-') {
            match('-');
            factor();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', or '-', but found " + Tag.getTagName(look.tag));
        }
    }

    void factor() throws Exception {
        if (look.tag == Tag.ID) {
            identifier();
        } else if (look.tag == Tag.NUM || look.tag == Tag.REAL) {
            constant();
        } else if ((char) look.tag == '(') {
            match('(');
            expression();
            match(')');
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', or '(', but found " + Tag.getTagName(look.tag));
        }
    }

    void relop() throws Exception {
        if ((char) look.tag == '=') {
            match('=');
        } else if ((char) look.tag == '>') {
            match('>');
        } else if (look.tag == Tag.BIG_EQUAL) {
            match('>');
            move();
            match('=');
        } else if ((char) look.tag == '<') {
            match('<');
        } else if (look.tag == Tag.LEAST_EQUAL) {
            match('<');
            move();
            match('=');
        } else if (look.tag == Tag.NOT_EQUAL) {
            match(Tag.NOT_EQUAL);
        } else {
            error("Syntax error: Expect '=', '>', or '<', but found " + Tag.getTagName(look.tag));
        }
    }

    void addop() throws Exception {
        if ((char) look.tag == '+') {
            match('+');
        } else if ((char) look.tag == '-') {
            match('-');
        } else if (look.tag == Tag.OR) {
            match(Tag.OR);
        } else {
            error("Syntax error: Expect '+', '-', or '||', but found " + Tag.getTagName(look.tag));
        }
    }

    void mulop() throws Exception {
        if ((char) look.tag == '*') {
            match('*');
        } else if ((char) look.tag == '/') {
            match('/');
        } else if (look.tag == Tag.AND) {
            match(Tag.AND);
        } else {
            error("Syntax error: Expect '*', '/', or '&&', but found " + Tag.getTagName(look.tag));
        }
    }

    void constant() throws Exception {
        switch (look.tag) {
            case Tag.NUM:
                integer_const();
                break;
            case Tag.REAL:
                float_const();
                break;
            default:
                error("Syntax error: Expect 'NUM' or 'REAL', but found " + Tag.getTagName(look.tag));
        }
    }

    void integer_const() throws Exception {
        if (look.tag == Tag.NUM) {
            match(Tag.NUM);
        } else {
            error("Syntax error: Expect 'NUM', but found " + Tag.getTagName(look.tag));
        }
    }

    void float_const() throws Exception {
        if (look.tag == Tag.REAL) {
            match(Tag.REAL);
        } else {
            error("Syntax error: Expect 'REAL', but found " + Tag.getTagName(look.tag));
        }
    }

    void literal() throws Exception {
        if (look.tag == Tag.LITERAL) {
            match(Tag.LITERAL);
        } else {
            error("Syntax error: Expect 'LITERAL', but found " + Tag.getTagName(look.tag));
        }
    }

    void identifier() throws Exception {
        if (look.tag == Tag.ID) {
            match(Tag.ID);
        } else {
            error("Syntax error: Expect 'ID', but found " + Tag.getTagName(look.tag));
        }
    }
}