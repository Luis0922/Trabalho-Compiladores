import Exceptions.IncompleteValueException;
import lexer.Lexer;
import lexer.Tag;
import lexer.Token;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        var lexer = new Lexer("./src/teste.txt");
        Token token;
        while ((token = lexer.scan()) != null) {
            if(token.tag < 256) {
                System.out.println("< " + (char) token.tag + " >");
            }
            else {
                if(Tag.getTagName(token.tag) != null) {
                    System.out.println("< " + Tag.getTagName(token.tag) + " , " + token + " >");
                }
            }
        }
    }
}