package parser;

import lexer.*;

public class Parser {

    private Lexer lex;

    private Token token;

    int used = 0;

    public Parser(Lexer l) throws Exception{
        lex = l;
        move();
    }

    void move() throws Exception{
        if(token != null){
            System.out.println(Tag.getTagName(token.tag));
        }
        token = lex.scan();
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) throws Exception{
        if(token.tag == t){
            move();
        } else {
            error("syntax error");
        }
    }

    // Regras da gramática
    public void program() throws Exception {
        if(token.tag == Tag.APP) {
            match(Tag.APP);
            identifier();
            body();
        } else {
            error("Syntax error: Expect 'APP', but found " + Tag.getTagName(token.tag));
        }
    }

    void body() throws Exception {
        if (token.tag == Tag.VAR || token.tag == Tag.INIT) {
            if (token.tag == Tag.VAR) {
                match(Tag.VAR);
                decl_list();
            }
            match(Tag.INIT);
            stmt_list();
            match(Tag.RETURN);
        } else {
            error("Syntax error: Expect 'VAR' or 'INIT', but found " + Tag.getTagName(token.tag));
        }

    }

    void decl_list() throws Exception {
        switch(token.tag) {
            case Tag.INTEGER:
            case Tag.REAL:
                decl();
                while ((char) token.tag == ';') {
                    match(';');
                    decl();
                }
                break;
            default:
                error("Syntax error: Expect 'INTEGER' or 'REAL', but found " + Tag.getTagName(token.tag));
        }
    }

    void decl() throws Exception {
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

    void ident_list() throws Exception {
        if (token.tag == Tag.ID) {
            identifier();
            while ((char) token.tag == ',') {
                match(',');
                identifier();
            }
        }
        else {
            error("Syntax error: Expect 'ID', but found " + Tag.getTagName(token.tag));
        }
    }

    void type() throws Exception {
        switch (token.tag){
            case Tag.INTEGER:
                match(Tag.INTEGER);
                break;
            case Tag.REAL:
                match(Tag.REAL);
                break;
            default:
                error("Syntax error: Expect 'INTEGER' or 'REAL', but found " + Tag.getTagName(token.tag));
        }
    }

    void stmt_list() throws Exception {
        switch(token.tag) {
            case Tag.ID:
            case Tag.IF:
            case Tag.REPEAT:
            case Tag.READ:
            case Tag.WRITE:
                stmt();
                while ((char) token.tag == ';') {
                    match(';');
                    stmt();
                }
                break;
            default:
                error("Syntax error: Expect 'ID', 'IF', 'REPEAT', 'READ' or 'WRITE', but found " + Tag.getTagName(token.tag));
        }
    }

    void stmt() throws Exception {
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

    void assign_stmt() throws Exception {
        if (token.tag == Tag.ID) {
            identifier();
            match(Tag.DEFINED_AS);
            simple_expr();
        }
        else {
            error("Syntax error: Expect 'ID', but found " + Tag.getTagName(token.tag));
        }
    }

    void if_stmt() throws Exception {
        if (token.tag == Tag.IF) {
            match(Tag.IF);
            condition();
            match(Tag.THEN);
            stmt_list();
            if_stmt_();
        } else {
            error("Syntax error: Expect 'IF', but found " + Tag.getTagName(token.tag));
        }
    }

    void if_stmt_() throws Exception {
        if(token.tag == Tag.ELSE) {
            match(Tag.ELSE);
            stmt_list();
            match(Tag.END);
        } else if(token.tag == Tag.END) {
            match(Tag.END);
        } else {
            error("Syntax error: Expect 'ELSE' or 'END', but found " + Tag.getTagName(token.tag));
        }
    }

    void repeat_stmt() throws Exception {
        if (token.tag == Tag.REPEAT) {
            match(Tag.REPEAT);
            stmt_list();
            stmt_suffix();
        } else {
            error("Syntax error: Expect 'REPEAT', but found " + Tag.getTagName(token.tag));
        }

    }

    void stmt_suffix() throws Exception {
        if(token.tag == Tag.UNTIL) {
            match(Tag.UNTIL);
            condition();
        } else {
            error("Syntax error: Expect 'UNTIL', but found " + Tag.getTagName(token.tag));
        }
    }

    void read_stmt() throws Exception {
        if(token.tag == Tag.READ) {
            match(Tag.READ);
            match('(');
            identifier();
            match(')');
        } else {
            error("Syntax error: Expect 'READ', but found " + Tag.getTagName(token.tag));
        }
    }

    void write_stmt() throws Exception {
        if (token.tag == Tag.WRITE) {
            match(Tag.WRITE);
            match('(');
            writable();
            match(')');
        } else {
            error("Syntax error: Expect 'WRITE', but found " + Tag.getTagName(token.tag));
        }
    }

    void writable() throws Exception {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL ||
                (char) token.tag == '(' || (char) token.tag == '!' || (char) token.tag == '-') {
            simple_expr();
        } else if (token.tag == Tag.LITERAL) {
            literal();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', '-', '*', '/', '&&', '+', '||', or 'LITERAL', but found " + Tag.getTagName(token.tag));
        }
    }

    void condition() throws Exception {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL ||
                (char) token.tag == '(' || (char) token.tag == '!' || (char) token.tag == '-')  {
            expression();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', '-', '*', '/', or '&&', but found " + Tag.getTagName(token.tag));
        }
    }

    void expression() throws Exception {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL ||
                (char) token.tag == '(' || (char) token.tag == '!' || (char) token.tag == '-') {
            simple_expr();
            expression_();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', '-', '*', '/', or '&&', but found " + Tag.getTagName(token.tag));
        }
    }

    void expression_() throws Exception {
        if (token.tag == Tag.RETURN || (char) token.tag == ';' || (char) token.tag == ')') {

        } else if ((char) token.tag == '=' || (char) token.tag == '>' || token.tag == Tag.BIG_EQUAL ||
                (char) token.tag == '<' || token.tag == Tag.LEAST_EQUAL || token.tag == Tag.NOT_EQUAL) {
            relop();
            simple_expr();
        } else {
            error("Syntax error: Expect '=', '>', '<', '>=', '<=', or '!=', but found " + Tag.getTagName(token.tag));
        }
    }

    void simple_expr() throws Exception {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL ||
                (char) token.tag == '(' || (char) token.tag == '!' || (char) token.tag == '-') {
            term();
            simple_expr_();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!' or '-' , but found " + Tag.getTagName(token.tag));
        }
    }

    void simple_expr_() throws Exception {
        if ((token.tag == Tag.RETURN || (char) token.tag == ';') || (char) token.tag == '=' ||
                (char) token.tag == '>' || token.tag == Tag.BIG_EQUAL || (char) token.tag == '<' ||
                token.tag == Tag.LEAST_EQUAL || token.tag == Tag.NOT_EQUAL || token.tag == Tag.THEN ||
                (char) token.tag == ')') {

        } else if ((char) token.tag == '+' || (char) token.tag == '-' || token.tag == Tag.OR) {
            addop();
            term();
            simple_expr_();
        } else {
            error("Syntax error: Expect '+', '-', or '||', but found " + Tag.getTagName(token.tag));
        }
    }

    void term() throws Exception {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL ||
                (char) token.tag == '(' || (char) token.tag == '!' || (char) token.tag == '-') {
            factor_a();
            term_();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', '-', '*', '/', or '&&', but found " + Tag.getTagName(token.tag));
        }
    }

    void term_() throws Exception {
        if ((char) token.tag == '+' || (char) token.tag == '-' || token.tag == Tag.OR ||
                token.tag == Tag.RETURN || (char) token.tag == ';' || (char) token.tag == '=' ||
                (char) token.tag == '>' || token.tag == Tag.BIG_EQUAL ||  (char) token.tag == '<' ||
                token.tag == Tag.LEAST_EQUAL || token.tag == Tag.NOT_EQUAL || token.tag == Tag.THEN ||
                (char) token.tag == ')') {

        } else if ((char) token.tag == '*' || (char) token.tag == '/' || token.tag == Tag.AND) {
            mulop();
            factor_a();
        } else {
            error("Syntax error: Expect '*', '/', or '&&', but found " + Tag.getTagName(token.tag));
        }
    }

    void factor_a() throws Exception {
        if (token.tag == Tag.ID || token.tag == Tag.NUM || token.tag == Tag.REAL || (char) token.tag == '(') {
            factor();
        } else if ((char) token.tag == '!') {
            match('!');
            factor();
        } else if ((char) token.tag == '-') {
            match('-');
            factor();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', or '-', but found " + Tag.getTagName(token.tag));
        }
    }

    void factor() throws Exception {
        if (token.tag == Tag.ID) {
            identifier();
        } else if (token.tag == Tag.NUM || token.tag == Tag.REAL) {
            constant();
        } else if ((char) token.tag == '(') {
            match('(');
            expression();
            match(')');
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', or '(', but found " + Tag.getTagName(token.tag));
        }
    }

    void relop() throws Exception {
        if ((char) token.tag == '=') {
            match('=');
        } else if ((char) token.tag == '>') {
            match('>');
        } else if (token.tag == Tag.BIG_EQUAL) {
            match('>');
            move();
            match('=');
        } else if ((char) token.tag == '<') {
            match('<');
        } else if (token.tag == Tag.LEAST_EQUAL) {
            match('<');
            move();
            match('=');
        } else if (token.tag == Tag.NOT_EQUAL) {
            match(Tag.NOT_EQUAL);
        } else {
            error("Syntax error: Expect '=', '>', or '<', but found " + Tag.getTagName(token.tag));
        }
    }

    void addop() throws Exception {
        if ((char) token.tag == '+') {
            match('+');
        } else if ((char) token.tag == '-') {
            match('-');
        } else if (token.tag == Tag.OR) {
            match(Tag.OR);
        } else {
            error("Syntax error: Expect '+', '-', or '||', but found " + Tag.getTagName(token.tag));
        }
    }

    void mulop() throws Exception {
        if ((char) token.tag == '*') {
            match('*');
        } else if ((char) token.tag == '/') {
            match('/');
        } else if (token.tag == Tag.AND) {
            match(Tag.AND);
        } else {
            error("Syntax error: Expect '*', '/', or '&&', but found " + Tag.getTagName(token.tag));
        }
    }

    void constant() throws Exception {
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

    void integer_const() throws Exception {
        if (token.tag == Tag.NUM) {
            match(Tag.NUM);
        } else {
            error("Syntax error: Expect 'NUM', but found " + Tag.getTagName(token.tag));
        }
    }

    void float_const() throws Exception {
        if (token.tag == Tag.REAL) {
            match(Tag.REAL);
        } else {
            error("Syntax error: Expect 'REAL', but found " + Tag.getTagName(token.tag));
        }
    }

    void literal() throws Exception {
        if (token.tag == Tag.LITERAL) {
            match(Tag.LITERAL);
        } else {
            error("Syntax error: Expect 'LITERAL', but found " + Tag.getTagName(token.tag));
        }
    }

    void identifier() throws Exception {
        if (token.tag == Tag.ID) {
            match(Tag.ID);
        } else {
            error("Syntax error: Expect 'ID', but found " + Tag.getTagName(token.tag));
        }
    }
}