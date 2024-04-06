package symbols;
import lexer.*;

public class Type extends Word{
    public int width = 0;

    public Type(String s, int tag, int w){
        super(s, tag);
        width = w;
    }

    public static final Type
    integer = new Type("integer", Tag.INTEGER, 4),
    real = new Type("real", Tag.REAL, 8);

    public static boolean typeNumeric(Type p) {
        if(p == Type.integer || p == Type.real) {
            return true;
        }
        else {
            return false;
        }
    }

    public static Type max(Type p1, Type p2) {
        if(!typeNumeric(p1) || !typeNumeric(p2)) {
            return null;
        } else if (p1 == Type.real || p2 == Type.real) {
            return Type.real;
        }
        else {
            return Type.integer;
        }
    }

}
