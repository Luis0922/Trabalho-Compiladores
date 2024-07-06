package lexer;

import java.util.HashMap;
import java.util.Map;

public class Tag {
    public final static int
                // Palavras reservadas
                APP = 256,
                VAR = 257,
                INIT = 258,
                RETURN = 259,
                INTEGER = 260,
                REAL = 261,
                IF = 262,
                THEN = 263,
                END = 264,
                ELSE = 265,
                REPEAT = 266,
                UNTIL = 267,
                READ = 268,
                WRITE = 269,

                //Operadores e pontuação
                DEFINED_AS = 289,
                BIG_EQUAL = 296,
                LEAST_EQUAL = 298,
                NOT_EQUAL = 299,
                OR = 302,
                AND = 303,

                // Outros tokens

                NUM = 322,
                ID = 323;
    private static final Map<Integer, String> map = new HashMap<Integer, String>();

    static {
        map.put(APP, "APP");
        map.put(VAR, "VAR");
        map.put(INIT, "INIT");
        map.put(RETURN, "RETURN");
        map.put(INTEGER, "INTEGER");
        map.put(REAL, "REAL");
        map.put(IF, "IF");
        map.put(THEN, "THEN");
        map.put(END, "END");
        map.put(ELSE, "ELSE");
        map.put(REPEAT, "REPEAT");
        map.put(UNTIL, "UNTIL");
        map.put(READ, "READ");
        map.put(WRITE, "WRITE");
        map.put(DEFINED_AS, "DEFINED_AS");
        map.put(BIG_EQUAL, "BIG_EQUAL");
        map.put(LEAST_EQUAL, "LEAST_EQUAL");
        map.put(NOT_EQUAL, "NOT_EQUAL");
        map.put(OR, "OR");
        map.put(AND, "AND");
        map.put(NUM, "NUM");
        map.put(ID, "ID");
    }

    public static String getTagName(int value) {
        return map.get(value);
    }
}

