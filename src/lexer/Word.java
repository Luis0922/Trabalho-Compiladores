package lexer;

public class Word extends Token {
    private String lexeme = "";
    public static final Word and = new Word ("&&", Tag.AND);
    public static final Word defined_as = new Word (":=", Tag.DEFINED_AS);
    public static final Word big_equal = new Word (">=", Tag.BIG_EQUAL);
    public static final Word least_equal = new Word ("<=", Tag.LEAST_EQUAL);
    public static final Word not_equal = new Word ("!=", Tag.NOT_EQUAL);
    public static final Word or = new Word ("||", Tag.OR);

    public Word (String s, int tag){
        super (tag,s);
        lexeme = s;
    }
    public String toString(){
        return "" + lexeme;
    }

    public String getLexeme(){
        return lexeme;
    }

}
