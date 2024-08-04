package parser;

import lexer.*;
import symbols.*;

import java.io.IOException;
import java.lang.reflect.Array;

public class Parser {
    private Lexer lexer;
    private Token look;
    Env top = null;
    int used = 0;

    public Parser (Lexer lexer) throws Exception {
        lexer = lexer;
        move();
    }
    void move() throws Exception {
        look = lexer.scan();
    }
    void error(String s) {
        throw new Error("near line " + lexer.line + ": " + s);
    }
    void match(int t) throws Exception {
        if (look.tag == t) {
            move();
        } else {
            error("syntax error");
        }
    }

    public void program() throws Exception {
        Stmt s = block();
        int begin = s.newlabel();
        int after = s.newlabel();
        s.emitlabel(begin);
        s.gen(begin, after);
        s.emitlabel(after);
    }

    Stmt block() throws Exception {
        match('{');
        Env savedEnv = top;
        top = new Env(top);
        decls();
        Stmt s = stmts();
        match('}');
        top = savedEnv;
        return s;
    }

    void decls() throws Exception {
        while (look.tag == Tag.BASIC) {
            Type p = type();
            Token tok = look;
            match(Tag.ID);
            match(';');
            Id id = new Id((Word)tok, p, used);
            top.put(tok, id);
            used = used + p.width;
        }
    }

    Type type() throws Exception {
        Type p = (Type)look;
        match(Tag.BASIC);
        if (look.tag != '[') {
            return p;
        } else {
            return dims(p);
        }
    }

    Type dims(Type p) throws Exception {
        match('[');
        Token tok =look;
        match(Tag.NUM);
        match(']');
        if (look.tag == '[') {
            p = dims(p);
        }
        return new Array(((Num)tok).value, p);
    }
}
