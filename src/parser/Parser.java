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
    void program() throws Exception {
        match(Tag.APP);
        identifier();
        body();
    }

    void body() throws Exception {
        if (look.tag == '[') {
            match('[');
            match(Tag.VAR);
            decl_list();
            match(']');
            match(Tag.INIT);
            stmt_list();
            match(Tag.RETURN);
        } else {
            error("Syntax error: Expect '[', but found " + look);
        }

    }

    void decl_list() throws Exception {
        switch(look.tag) {
            case Tag.INTEGER:
            case Tag.REAL:
                decl();
                while (look.tag == ';') {
                    match(';');
                    decl();
                }
                break;
            default:
                error("Syntax error: Expect 'INTEGER' or 'REAL', but found " + look);
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
                error("Syntax error: Expect 'INTEGER' or 'REAL', but found " + look);
        }
    }

    void ident_list() throws Exception {
        if (look.tag == Tag.ID) {
            identifier();
            while (look.tag == ',') {
                match(',');
                identifier();
            }
        }
        error("Syntax error: Expect 'ID', but found " + look);
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
                error("Syntax error: Expect 'INTEGER' or 'REAL', but found " + look);
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
                while (look.tag == ';') {
                    match(';');
                    stmt();
                }
                break;
            default:
                error("Syntax error: Expect 'ID', 'IF', 'REPEAT', 'READ' or 'WRITE', but found " + look);
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
                error("Syntax error: Expect 'ID', 'IF', 'REPEAT', 'READ' or 'WRITE', but found " + look);
        }
    }

    void assign_stmt() throws Exception {
        if (look.tag == Tag.ID) {
            identifier();
            match(Tag.DEFINED_AS);
            simple_expr();
        }
        error("Syntax error: Expect 'ID', but found " + look);
    }

    void if_stmt() throws Exception {

    }

    void repeat_stmt() throws Exception {
        if (look.tag == Tag.REPEAT) {
            match(Tag.REPEAT);
            stmt_list();
            stmt_suffix();
        } else {
            error("Syntax error: Expect 'REPEAT', but found " + look);
        }

    }

    void stmt_suffix() throws Exception {
        if(look.tag == Tag.UNTIL) {
            match(Tag.UNTIL);
            condition();
        } else {
            error("Syntax error: Expect 'UNTIL', but found " + look);
        }
    }

    void read_stmt() throws Exception {
        if(look.tag == Tag.READ) {
            match(Tag.READ);
            match('(');
            identifier();
            match(')');
        } else {
            error("Syntax error: Expect 'READ', but found " + look);
        }
    }

    void write_stmt() throws Exception {
        if (look.tag == Tag.WRITE) {
            match(Tag.WRITE);
            match('(');
            writable();
            match(')');
        } else {
            error("Syntax error: Expect 'WRITE', but found " + look);
        }
    }

    void writable() throws Exception {
        switch (look.tag){
            case :
                simple_expr();
                break;
            case :
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
        switch (look.tag) {
            case Tag.ID:
            case Tag.NUM:
            case Tag.REAL:
            case '(':
                factor();
                break;
            case '!':
                match('!');
                factor();
                break;
            case '-':
                match('-');
                factor();
                break;
            default:
                error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', or '-', but found " + look);
        }
    }

    void factor() throws Exception {
        switch (look.tag) {
            case Tag.ID:
                identifier();
                break;
            case Tag.NUM:
            case Tag.REAL:
                constant();
                break;
            case '(':
                match('(');
                expression();
                match(')');
                break;
            default:
                error("Syntax error: Expect 'ID', 'NUM', 'REAL', or '(', but found " + look);
        }
    }

    void relop() throws Exception {
        switch (look.tag){
            case '=':
                match('=');
                break;
            case '>':
                match('>');
                break;
            case Tag.BIG_EQUAL:
                // ou match('>'); match('=');
                match(Tag.BIG_EQUAL);
                break;
            case '<':
                match('<');
                break;
            case Tag.LEAST_EQUAL:
                match(Tag.LEAST_EQUAL);
                break;
            case Tag.NOT_EQUAL:
                match(Tag.NOT_EQUAL);
            default:
                error("Syntax error: Expect '=', '>', '>=', '<', '<=', or '<>', but found " + look);
        }
    }

    void addop() throws Exception {
        switch (look.tag) {
            case '+':
                match('+');
                break;
            case '-':
                match('-');
                break;
            case Tag.OR:
                match(Tag.OR);
                break;
            default:
                error("Syntax error: Expect '+', '-', or '||', but found " + look);
        }
    }

    void mulop() throws Exception {
        switch (look.tag) {
            case '*':
                match('*');
                break;
            case '/':
                match('/');
                break;
            case Tag.AND:
                match(Tag.AND);
                break;
            default:
                error("Syntax error: Expect '*', '/', or '&&', but found " + look);
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
                error("Syntax error: Expect 'NUM' or 'REAL', but found " + look);
        }
    }

    void integer_const() throws Exception {
        if (look.tag == Tag.NUM) {
            match(Tag.NUM);
        } else {
            error("Syntax error: Expect 'NUM', but found " + look);
        }
    }

    void float_const() throws Exception {
        if (look.tag == Tag.REAL) {
            match(Tag.REAL);
        } else {
            error("Syntax error: Expect 'REAL', but found " + look);
        }
    }

    void literal() throws Exception {
        if (look.tag == Tag.LITERAL) {
            match(Tag.LITERAL);
        } else {
            error("Syntax error: Expect 'LITERAL', but found " + look);
        }
    }

    void identifier() throws Exception {
        if (look.tag == Tag.ID) {
            match(Tag.ID);
        } else {
            error("Syntax error: Expect 'ID', but found " + look);
        }
    }
}