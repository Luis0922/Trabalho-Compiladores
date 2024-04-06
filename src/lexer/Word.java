package lexer;

public class Word extends Token {
    private String lexeme = "";
    public static final Word and = new Word ("&&", Tag.AND);
    public static final Word semi_colon = new Word (";", Tag.SEMICOLON);
    public static final Word defined_as = new Word ("::=", Tag.DEFINED_AS);
    public static final Word open_parentheses = new Word ("(", Tag.OPEN_PARENTHESES);
    public static final Word close_parentheses = new Word (")", Tag.CLOSE_PARENTHESES);
    public static final Word not = new Word ("!", Tag.NOT);
    public static final Word minus = new Word ("-", Tag.MINUS);
    public static final Word equal = new Word ("=", Tag.EQUAL);
    public static final Word big = new Word (">", Tag.BIG);
    public static final Word big_equal = new Word (">=", Tag.BIG_EQUAL);
    public static final Word least = new Word ("<", Tag.LEAST);
    public static final Word least_equal = new Word ("<=", Tag.LEAST_EQUAL);
    public static final Word not_equal = new Word ("!=", Tag.NOT_EQUAL);
    public static final Word comma = new Word (",", Tag.COMMA);
    public static final Word plus = new Word ("+", Tag.PLUS);
    public static final Word or = new Word ("||", Tag.OR);
    public static final Word multi = new Word ("*", Tag.MULTI);
    public static final Word div = new Word ("/", Tag.DIV);
    public static final Word dot = new Word (".", Tag.DOT);
    public static final Word open_bracket = new Word ("{", Tag.OPEN_BRACKET);
    public static final Word close_bracket = new Word ("}", Tag.CLOSE_BRACKET);
    public static final Word underline = new Word ("_", Tag.UNDERLINE);

    public Word (String s, int tag){
        super (tag);
        lexeme = s;
    }
    public String toString(){
        return "" + lexeme;
    }


}
