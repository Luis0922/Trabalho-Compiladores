package lexer;
public class Literal extends Token{
    public final String value;
    public Literal(String v){
        super(Tag.LITERAL,v);
        value = v;
    }

    public String toString(){
        return "" + value;
    }
}

