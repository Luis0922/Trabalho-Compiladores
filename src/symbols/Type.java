package symbols;

import lexer.*;

public class Type extends Word {
    public int width = 0; // width é usado para alocação de memória
    public Type(String s, int tag, int w) {
        super(s, tag);
        width = w;
    }

    public static final Type
            Integer = new Type("integer", Tag.BASIC, 4),
            Real = new Type("real", Tag.BASIC, 8);
}
