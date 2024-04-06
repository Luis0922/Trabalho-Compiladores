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
                SEMICOLON = 288,
                DEFINED_AS = 289,
                OPEN_PARENTHESES = 290,
                CLOSE_PARENTHESES = 291,
                NOT = 292,
                MINUS = 293,
                EQUAL = 294,
                BIG = 295,
                BIG_EQUAL = 296,
                LEAST = 297,
                LEAST_EQUAL = 298,
                NOT_EQUAL = 299,
                COMMA = 300,
                PLUS = 301,
                OR = 302,
                AND = 303,
                MULTI = 304,
                DIV = 305,
                DOT = 306,
                OPEN_BRACKET = 307,
                CLOSE_BRACKET = 308,
                UNDERLINE = 309,

                // Outros tokens

                NUM = 322,
                ID = 323;
}
