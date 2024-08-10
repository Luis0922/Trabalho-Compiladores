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
        System.out.println(look);
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
        if ((char) look.tag == '[') {
            match('[');
            match(Tag.VAR);
            decl_list();
            match(']');
            match(Tag.INIT);
            stmt_list();
            match(Tag.RETURN);
        } else {
            error("Syntax error: Expect '[', but found " + Tag.getTagName(look.tag));
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
        switch (look.tag){
            case 'a':
                simple_expr();
                break;
            case 'b':
                literal();
                break;
            default:

        }
    }

    void condition() throws Exception {
        expression();
    }

    void expression() throws Exception {

    }

    void simple_expr() throws Exception {

    }

    void term() throws Exception {

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