package lexer;

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
}
