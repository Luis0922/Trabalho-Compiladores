package lexer;

public class Token {
    public final int tag; //constante que representa o token
    public final String lexeme; // sequência de caracteres que compõe o token

    public Token (int t, String lex){
        tag = t;
        lexeme = lex;
    }
    public String toString(){
        return "" + tag;
    }
}

